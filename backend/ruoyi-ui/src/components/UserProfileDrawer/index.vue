<template>
  <detail-drawer
    :visible="visible"
    :title="title"
    :size="size"
    :avatar="user ? (user.avatar || defaultAvatar) : ''"
    :header-title="user ? (user.nickname || '未设置昵称') : ''"
    :header-subtitle="user ? `ID: ${user.userId}` : ''"
    :header-tags="headerTags"
    :show-header="true"
    @update:visible="$emit('update:visible', $event)"
  >
    <template #sections>
      <!-- 基础信息 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">📋 基础信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="手机号">{{ getUserProp('phone') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生日">{{ getUserProp('birthday') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册来源">
            <el-tag :type="sourceTagType(getUserProp('source'))" size="small">{{ sourceText(getUserProp('source')) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ getUserProp('createTime') || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 健康数据 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">📊 健康数据</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="身高">{{ getUserProp('height') || '-' }} cm</el-descriptions-item>
          <el-descriptions-item label="性别">{{ genderText(getUserProp('gender')) }}</el-descriptions-item>
          <el-descriptions-item label="当前体重">{{ getUserProp('currentWeight') || '-' }} kg</el-descriptions-item>
          <el-descriptions-item label="目标体重">{{ getUserProp('targetWeight') || '-' }} kg</el-descriptions-item>
          <el-descriptions-item label="BMI">
            <el-tag :type="bmiTagType(getUserProp('bmi'))" size="small">{{ getUserProp('bmi') || '-' }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="连续打卡">🔥 {{ getUserProp('streakDays') || 0 }} 天</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 目标设置 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">🎯 目标设置</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="建议摄入上限">{{ getUserProp('dailyCalorieTarget') || '-' }} kcal</el-descriptions-item>
          <el-descriptions-item label="每日饮水">{{ getUserProp('dailyWaterTarget') || '-' }} 杯</el-descriptions-item>
          <el-descriptions-item label="成就积分">⭐ {{ getUserProp('achievementPoints') || 0 }}</el-descriptions-item>
          <el-descriptions-item label="圈子功能">{{ getUserProp('circleFeatureEnabled') ? '已启用' : '未启用' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 偏好设置 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">⚙️ 偏好设置</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="同步模式">
            <el-tag :type="getUserProp('syncMode') === '0' ? 'success' : 'info'" size="small">
              {{ getUserProp('syncMode') === '0' ? '实时同步' : '手动同步' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="主圈子">
            <circle-link v-if="getUserProp('mainCircleId')" :circle-id="getUserProp('mainCircleId')" />
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 统计概览 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">📈 数据统计</div>
        <el-row :gutter="16">
          <el-col :span="4">
            <div class="stat-item" @click="viewRecords('weight')">
              <div class="stat-value">{{ stats.weightCount }}</div>
              <div class="stat-label">体重记录</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-item" @click="viewRecords('food')">
              <div class="stat-value">{{ stats.foodCount }}</div>
              <div class="stat-label">饮食记录</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-item" @click="viewRecords('exercise')">
              <div class="stat-value">{{ stats.exerciseCount }}</div>
              <div class="stat-label">运动记录</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-item" @click="viewRecords('water')">
              <div class="stat-value">{{ stats.waterCount }}</div>
              <div class="stat-label">饮水记录</div>
            </div>
          </el-col>
          <el-col :span="4">
            <div class="stat-item" @click="viewRecords('checkin')">
              <div class="stat-value">{{ stats.checkinCount }}</div>
              <div class="stat-label">习惯打卡</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 参与圈子 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">
          <span>👥 参与圈子 ({{ circles.length }})</span>
        </div>
        <div v-if="circlesLoading" class="loading-text">加载中...</div>
        <div v-else-if="circles.length === 0" class="empty-text">暂未参与任何圈子</div>
        <div v-else class="circle-list">
          <div v-for="circle in circles" :key="circle.id" class="circle-item">
            <circle-link :circle-id="circle.circleId" />
            <el-tag :type="circleRoleTagType(circle.role)" size="mini">
              {{ circleRoleText(circle.role) }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <!-- 习惯列表 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">
          <span>✅ 习惯管理 ({{ habits.length }})</span>
        </div>
        <div v-if="habitsLoading" class="loading-text">加载中...</div>
        <div v-else-if="habits.length === 0" class="empty-text">暂未创建任何习惯</div>
        <div v-else class="habit-list">
          <div v-for="habit in habits" :key="habit.id" class="habit-item">
            <span class="habit-icon">{{ habit.icon || '📌' }}</span>
            <span class="habit-name">{{ habit.name }}</span>
            <el-tag :type="habit.status === '1' ? 'success' : 'info'" size="mini">
              {{ habit.status === '1' ? '进行中' : '已暂停' }}
            </el-tag>
          </div>
        </div>
      </el-card>

      <!-- 关注关系 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">
          <span>🔗 社交关系</span>
          <span class="follow-summary">
            <el-tag size="mini">关注 {{ followings.length }}</el-tag>
            <el-tag size="mini" type="info">粉丝 {{ followers.length }}</el-tag>
          </span>
        </div>
        <el-tabs v-model="followTab" type="card" size="mini">
          <!-- TA 关注的人 -->
          <el-tab-pane :label="`👤 TA关注的 (${followings.length})`" name="following">
            <div v-if="followingsLoading" class="loading-text">加载中...</div>
            <div v-else-if="followings.length === 0" class="empty-text">暂未关注任何人</div>
            <div v-else class="follow-grid">
              <div v-for="f in followings" :key="f.id" class="follow-card">
                <user-link :user-id="f.followingId" :show-avatar="true" :avatar-size="28" />
                <el-tag v-if="isMutualFollow(f.followingId)" type="success" size="mini" class="mutual-tag">
                  <i class="el-icon-refresh"></i> 互关
                </el-tag>
              </div>
            </div>
          </el-tab-pane>
          <!-- 关注 TA 的人 -->
          <el-tab-pane :label="`👥 TA的粉丝 (${followers.length})`" name="follower">
            <div v-if="followersLoading" class="loading-text">加载中...</div>
            <div v-else-if="followers.length === 0" class="empty-text">暂无粉丝</div>
            <div v-else class="follow-grid">
              <div v-for="f in followers" :key="f.id" class="follow-card">
                <user-link :user-id="f.followerId" :show-avatar="true" :avatar-size="28" />
                <el-tag v-if="isMutualFollow(f.followerId)" type="success" size="mini" class="mutual-tag">
                  <i class="el-icon-refresh"></i> 互关
                </el-tag>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-card>

      <!-- 历史记录 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">📜 历史记录</div>
        <el-tabs v-model="recordTab" type="card" size="mini" @tab-click="loadRecordTab">
          <!-- 体重记录 -->
          <el-tab-pane label="⚖️ 体重" name="weight">
            <div v-if="recordsLoading" class="loading-text">加载中...</div>
            <div v-else-if="weightRecords.length === 0" class="empty-text">暂无体重记录</div>
            <el-table v-else :data="weightRecords" size="mini" max-height="200" style="width: 100%">
              <el-table-column label="日期" min-width="80">
                <template slot-scope="scope">{{ formatDate(scope.row.recordedAt) }}</template>
              </el-table-column>
              <el-table-column label="体重" min-width="70">
                <template slot-scope="scope">
                  <span class="weight-value">{{ scope.row.weight }} kg</span>
                </template>
              </el-table-column>
              <el-table-column label="BMI" prop="bmi" min-width="50" />
              <el-table-column label="体脂率" min-width="60">
                <template slot-scope="scope">{{ scope.row.bodyFatRate ? scope.row.bodyFatRate + '%' : '-' }}</template>
              </el-table-column>
              <el-table-column label="图片" min-width="70">
                <template slot-scope="scope"><profile-images :images="getImageUrls(scope.row)" /></template>
              </el-table-column>
              <el-table-column label="备注" prop="remark" show-overflow-tooltip />
            </el-table>
          </el-tab-pane>
          <!-- 饮食记录 -->
          <el-tab-pane label="🍽️ 饮食" name="food">
            <div v-if="recordsLoading" class="loading-text">加载中...</div>
            <div v-else-if="foodRecords.length === 0" class="empty-text">暂无饮食记录</div>
            <el-table v-else :data="foodRecords" size="mini" max-height="200" style="width: 100%">
              <el-table-column label="日期" min-width="80">
                <template slot-scope="scope">{{ formatDate(scope.row.recordedAt) }}</template>
              </el-table-column>
              <el-table-column label="食物" prop="foodName" min-width="100" show-overflow-tooltip />
              <el-table-column label="卡路里" min-width="70">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.calories > 500 ? 'danger' : 'success'" size="mini">{{ scope.row.calories }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="蛋白质" min-width="60">
                <template slot-scope="scope">{{ scope.row.protein ? scope.row.protein + 'g' : '-' }}</template>
              </el-table-column>
              <el-table-column label="餐类" min-width="60">
                <template slot-scope="scope">{{ mealTypeDict[scope.row.mealType] || scope.row.mealType }}</template>
              </el-table-column>
              <el-table-column label="图片" min-width="70">
                <template slot-scope="scope"><profile-images :images="getImageUrls(scope.row, ['imageUrl'])" /></template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <!-- 运动记录 -->
          <el-tab-pane label="🏃 运动" name="exercise">
            <div v-if="recordsLoading" class="loading-text">加载中...</div>
            <div v-else-if="exerciseRecords.length === 0" class="empty-text">暂无运动记录</div>
            <el-table v-else :data="exerciseRecords" size="mini" max-height="200" style="width: 100%">
              <el-table-column label="日期" min-width="80">
                <template slot-scope="scope">{{ formatDate(scope.row.recordedAt) }}</template>
              </el-table-column>
              <el-table-column label="类型" prop="exerciseType" min-width="80" />
              <el-table-column label="时长" min-width="70">
                <template slot-scope="scope">{{ scope.row.durationMinutes }}分</template>
              </el-table-column>
              <el-table-column label="消耗" min-width="70">
                <template slot-scope="scope">
                  <el-tag type="warning" size="mini">-{{ scope.row.caloriesBurned }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="距离" min-width="60">
                <template slot-scope="scope">{{ scope.row.distance ? scope.row.distance + 'km' : '-' }}</template>
              </el-table-column>
              <el-table-column label="图片" min-width="70">
                <template slot-scope="scope"><profile-images :images="getImageUrls(scope.row)" /></template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
          <!-- 饮水记录 -->
          <el-tab-pane label="💧 饮水" name="water">
            <div v-if="recordsLoading" class="loading-text">加载中...</div>
            <div v-else-if="waterRecords.length === 0" class="empty-text">暂无饮水记录</div>
            <el-table v-else :data="waterRecords" size="mini" max-height="200" style="width: 100%">
              <el-table-column label="日期" min-width="80">
                <template slot-scope="scope">{{ formatDate(scope.row.recordedAt) }}</template>
              </el-table-column>
              <el-table-column label="饮水量" min-width="80">
                <template slot-scope="scope">{{ scope.row.ml }} ml</template>
              </el-table-column>
              <el-table-column label="杯数" prop="cups" min-width="50" />
              <el-table-column label="图片" min-width="70">
                <template slot-scope="scope"><profile-images :images="getImageUrls(scope.row)" /></template>
              </el-table-column>
              <el-table-column label="备注" prop="remark" show-overflow-tooltip />
            </el-table>
          </el-tab-pane>
          <!-- 习惯打卡 -->
          <el-tab-pane label="✅ 习惯" name="checkin">
            <div v-if="recordsLoading" class="loading-text">加载中...</div>
            <div v-else-if="checkinRecords.length === 0" class="empty-text">暂无习惯打卡</div>
            <el-table v-else :data="checkinRecords" size="mini" max-height="200" style="width: 100%">
              <el-table-column label="日期" min-width="80">
                <template slot-scope="scope">{{ formatDate(scope.row.checkedAt) }}</template>
              </el-table-column>
              <el-table-column label="习惯ID" prop="habitId" min-width="70" />
              <el-table-column label="图片" min-width="70">
                <template slot-scope="scope"><profile-images :images="getImageUrls(scope.row)" /></template>
              </el-table-column>
              <el-table-column label="备注" prop="note" show-overflow-tooltip />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </template>

    <!-- 快捷操作 -->
    <template #actions>
      <el-button size="small" type="primary" @click="viewAllRecords">查看全部记录</el-button>
      <el-button size="small" @click="viewUserFeeds">用户动态</el-button>
      <el-button size="small" @click="viewUserFeedback">用户反馈</el-button>
      <el-button size="small" @click="viewUserNotifications">用户通知</el-button>
      <el-button size="small" @click="$emit('edit', user)">编辑信息</el-button>
    </template>
  </detail-drawer>
</template>

<script>
import { listWeightRecords, listFoodRecords, listExerciseRecords, listWaterRecords } from '@/api/weight/records'
import { listCheckin } from '@/api/weight/checkin'
import { listMember } from '@/api/weight/member'
import { listHabit } from '@/api/weight/habit'
import { listFollow } from '@/api/weight/follow'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'
import { resolveImageList } from '@/utils/weightImages'

const ProfileImages = {
  functional: true,
  props: {
    images: {
      type: Array,
      default: () => []
    }
  },
  render(h, context) {
    const images = context.props.images || []
    if (!images.length) return h('span', '-')
    return h('div', { class: 'profile-images' }, images.slice(0, 2).map((image, index) => h('el-image', {
      key: image + index,
      class: 'profile-image',
      props: {
        src: image,
        fit: 'cover',
        previewSrcList: images
      }
    })))
  }
}

export default {
  name: 'UserProfileDrawer',
  components: { ProfileImages },
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    user: {
      type: Object,
      default: null
    },
    title: {
      type: String,
      default: '用户 360° 画像'
    },
    size: {
      type: String,
      default: '50%'
    }
  },
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      stats: {
        weightCount: 0,
        foodCount: 0,
        exerciseCount: 0,
        waterCount: 0,
        checkinCount: 0
      },
      circles: [],
      circlesLoading: false,
      habits: [],
      habitsLoading: false,
      // 关注关系
      followTab: 'following',
      followings: [],
      followingsLoading: false,
      followers: [],
      followersLoading: false,
      // 历史记录
      recordTab: 'weight',
      recordsLoading: false,
      weightRecords: [],
      foodRecords: [],
      exerciseRecords: [],
      waterRecords: [],
      checkinRecords: [],
      mealTypeDict: {
        '0': '早餐',
        '1': '午餐',
        '2': '晚餐',
        '3': '加餐'
      }
    }
  },
  computed: {
    headerTags() {
      if (!this.user) return []
      const tags = []
      // 性别标签
      const genderMap = { '0': '未知', '1': '男', '2': '女' }
      const genderTypeMap = { '0': 'info', '1': '', '2': 'danger' }
      tags.push({ 
        text: genderMap[this.user.gender] || '未知', 
        type: genderTypeMap[this.user.gender] || 'info' 
      })
      // 来源标签
      const sourceMap = { '0': '微信', '1': 'iOS', '2': 'Android', '3': 'H5', '4': '后台' }
      if (this.user.source !== undefined) {
        tags.push({ text: sourceMap[this.user.source] || '未知', type: 'warning' })
      }
      return tags
    }
  },
  watch: {
    visible(val) {
      if (val && this.user) {
        this.loadUserData()
      }
    },
    user(val) {
      if (val && this.visible) {
        this.loadUserData()
      }
    }
  },
  methods: {
    getUserProp(prop) {
      return this.user ? this.user[prop] : null
    },
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      return `${d.getMonth() + 1}/${d.getDate()}`
    },
    async loadUserData() {
      const userId = this.user ? this.user.userId : null
      if (!userId) return

      // 并行加载所有数据
      this.loadStats(userId)
      this.loadCircles(userId)
      this.loadHabits(userId)
      this.loadFollowings(userId)
      this.loadFollowers(userId)
      // 加载默认的体重记录
      this.recordTab = 'weight'
      this.loadRecordsByType('weight')
    },
    async loadStats(userId) {
      try {
      const [weightRes, foodRes, exerciseRes, waterRes, checkinRes] = await Promise.all([
          listWeightRecords({ userId, pageSize: 1 }),
          listFoodRecords({ userId, pageSize: 1 }),
          listExerciseRecords({ userId, pageSize: 1 }),
          listWaterRecords({ userId, pageSize: 1 }),
          listCheckin({ userId, pageSize: 1 })
        ])
        this.stats = {
          weightCount: weightRes.total || 0,
          foodCount: foodRes.total || 0,
          exerciseCount: exerciseRes.total || 0,
          waterCount: waterRes.total || 0,
          checkinCount: checkinRes.total || 0
        }
      } catch (e) {
        console.error('Failed to load stats:', e)
      }
    },
    async loadCircles(userId) {
      this.circlesLoading = true
      try {
        const res = await listMember({ userId, pageSize: 100 })
        this.circles = res.rows || []
      } catch (e) {
        console.error('Failed to load circles:', e)
      } finally {
        this.circlesLoading = false
      }
    },
    async loadHabits(userId) {
      this.habitsLoading = true
      try {
        const res = await listHabit({ userId, pageSize: 100 })
        this.habits = res.rows || []
      } catch (e) {
        console.error('Failed to load habits:', e)
      } finally {
        this.habitsLoading = false
      }
    },
    async loadFollowings(userId) {
      this.followingsLoading = true
      try {
        const res = await listFollow({ followerId: userId, pageSize: 100 })
        this.followings = res.rows || []
      } catch (e) {
        console.error('Failed to load followings:', e)
      } finally {
        this.followingsLoading = false
      }
    },
    async loadFollowers(userId) {
      this.followersLoading = true
      try {
        const res = await listFollow({ followingId: userId, pageSize: 100 })
        this.followers = res.rows || []
      } catch (e) {
        console.error('Failed to load followers:', e)
      } finally {
        this.followersLoading = false
      }
    },
    // 判断是否互关
    isMutualFollow(targetUserId) {
      const userId = this.user ? this.user.userId : null
      if (!userId) return false
      // 目标用户在“TA关注的”列表中，同时也在“TA的粉丝”列表中
      const isFollowing = this.followings.some(f => f.followingId === targetUserId)
      const isFollower = this.followers.some(f => f.followerId === targetUserId)
      return isFollowing && isFollower
    },
    bmiTagType(bmi) {
      if (!bmi) return 'info'
      if (bmi < 18.5) return 'warning'
      if (bmi < 24) return 'success'
      if (bmi < 28) return 'warning'
      return 'danger'
    },
    genderText(gender) {
      const map = { '0': '未知', '1': '男', '2': '女' }
      return map[gender] || '未知'
    },
    sourceText(source) {
      const map = { '0': '微信小程序', '1': 'iOS', '2': 'Android', '3': 'H5', '4': '后台' }
      return map[source] || '未知'
    },
    sourceTagType(source) {
      const map = { '0': 'success', '1': '', '2': '', '3': 'warning', '4': 'info' }
      return map[source] || 'info'
    },
    circleRoleText(role) {
      const map = { admin: '管理员', '0': '管理员', member: '成员', '1': '成员' }
      return map[role] || '成员'
    },
    circleRoleTagType(role) {
      return ['admin', '0'].includes(String(role)) ? 'danger' : 'info'
    },
    viewRecords(type) {
      const typeMap = { 'weight': 'weight', 'food': 'food', 'exercise': 'exercise', 'water': 'water', 'checkin': 'checkin' }
      this.$router.push({ 
        path: WEIGHT_ADMIN_ROUTES.records,
        query: { userId: this.user.userId, tab: typeMap[type] || 'weight' } 
      })
    },
    viewAllRecords() {
      this.$router.push({ path: WEIGHT_ADMIN_ROUTES.records, query: { userId: this.user.userId } })
    },
    viewUserFeeds() {
      this.$router.push({ path: WEIGHT_ADMIN_ROUTES.circleFeed, query: { userId: this.user.userId } })
    },
    viewUserFeedback() {
      this.$router.push({ path: WEIGHT_ADMIN_ROUTES.feedback, query: { userId: this.user.userId } })
    },
    viewUserNotifications() {
      this.$router.push({ path: WEIGHT_ADMIN_ROUTES.notification, query: { userId: this.user.userId } })
    },
    // 历史记录 Tab 切换
    loadRecordTab(tab) {
      const tabName = tab.name || tab
      this.loadRecordsByType(tabName)
    },
    // 根据类型加载记录
    async loadRecordsByType(type) {
      const userId = this.user ? this.user.userId : null
      if (!userId) return

      this.recordsLoading = true
      try {
        const params = { userId, pageNum: 1, pageSize: 5 }
        switch (type) {
          case 'weight':
            this.weightRecords = (await listWeightRecords(params)).rows || []
            break
          case 'food':
            this.foodRecords = (await listFoodRecords(params)).rows || []
            break
          case 'exercise':
            this.exerciseRecords = (await listExerciseRecords(params)).rows || []
            break
          case 'water':
            this.waterRecords = (await listWaterRecords(params)).rows || []
            break
          case 'checkin':
            this.checkinRecords = (await listCheckin(params)).rows || []
            break
        }
      } catch (e) {
        console.error('Failed to load records:', e)
      } finally {
        this.recordsLoading = false
      }
    },
    getImageUrls(record, fallbackFields = []) {
      if (!record) return []
      const urls = []
      const rawValues = [record.images].concat(fallbackFields.map(field => record[field]))
      rawValues.forEach(raw => {
        resolveImageList(raw).forEach(url => {
          if (url && !urls.includes(url)) {
            urls.push(url)
          }
        })
      })
      return urls
    }
  }
}
</script>

<style scoped>
.section-card {
  margin-bottom: 16px;
}

.section-header {
  font-weight: 500;
  font-size: 14px;
}

.stat-item {
  text-align: center;
  padding: 12px 8px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.stat-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.circle-list, .habit-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.circle-item, .habit-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 6px;
}

.habit-icon {
  font-size: 16px;
}

.habit-name {
  font-size: 13px;
  color: #303133;
}

.loading-text, .empty-text {
  text-align: center;
  color: #909399;
  padding: 16px;
}

.follow-summary {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.follow-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 200px;
  overflow-y: auto;
}

.follow-card {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.2s;
}

.follow-card:hover {
  background: linear-gradient(135deg, #e8f4ff 0%, #d9ecff 100%);
  border-color: #409eff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.mutual-tag {
  margin-left: auto;
  font-size: 11px;
}

.mutual-tag i {
  margin-right: 2px;
}

.profile-images {
  display: inline-flex;
  gap: 4px;
}

.profile-image {
  width: 28px;
  height: 28px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
</style>
