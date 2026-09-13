<template>
  <div class="app-container records-page">
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="68px"
    >
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          style="width: 140px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入用户昵称"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="日期范围">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-toolbar">
      <div class="toolbar-context">
        <template v-if="routeUserId">
          <el-tag type="info" effect="plain" size="small">当前按用户筛选</el-tag>
          <user-link :user-id="routeUserId" />
          <el-button type="text" size="mini" @click="clearRouteUserFilter">查看全部</el-button>
        </template>
        <span v-else class="toolbar-hint">默认自动加载最近记录，可按用户或日期进一步筛选。</span>
      </div>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="fetchActiveTab" />
    </div>

    <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
      <el-tab-pane label="体重记录" name="weight">
        <el-table v-loading="loading" :data="weightList" stripe>
          <el-table-column label="用户" align="center" width="130">
            <template slot-scope="scope">
              <user-link :user-id="scope.row.userId" />
            </template>
          </el-table-column>
          <el-table-column label="体重(kg)" align="center" prop="weight" width="100">
            <template slot-scope="scope">
              <span class="weight-value">{{ scope.row.weight }}</span>
            </template>
          </el-table-column>
          <el-table-column label="BMI" align="center" prop="bmi" width="80" />
          <el-table-column label="体脂率(%)" align="center" prop="bodyFatRate" width="100" />
          <el-table-column label="图片" align="center" width="140">
            <template slot-scope="scope">
              <record-images :images="getImageUrls(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="同步圈子" align="center" width="110">
            <template slot-scope="scope">
              <sync-summary :ids="parseSyncCircles(scope.row.syncToCircles)" />
            </template>
          </el-table-column>
          <el-table-column label="动态" align="center" width="90">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openSourceFeed('weight_record', scope.row.id)">查动态</el-button>
            </template>
          </el-table-column>
          <el-table-column label="记录日期" align="center" prop="recordedAt" width="150" />
          <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>
        <pagination
          v-show="weightTotal > 0"
          :total="weightTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getWeightList"
        />
      </el-tab-pane>

      <el-tab-pane label="饮食记录" name="food">
        <el-table v-loading="loading" :data="foodList" stripe>
          <el-table-column label="用户" align="center" width="130">
            <template slot-scope="scope">
              <user-link :user-id="scope.row.userId" />
            </template>
          </el-table-column>
          <el-table-column label="餐类" align="center" prop="mealType" width="80">
            <template slot-scope="scope">
              {{ mealTypeDict[scope.row.mealType] || scope.row.mealType || '-' }}
            </template>
          </el-table-column>
          <el-table-column label="食物名称" align="center" prop="foodName" min-width="150" show-overflow-tooltip />
          <el-table-column label="卡路里(kcal)" align="center" prop="calories" width="120">
            <template slot-scope="scope">
              <el-tag :type="scope.row.calories > 500 ? 'danger' : 'success'" size="small">
                {{ scope.row.calories || 0 }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="营养" align="center" width="150">
            <template slot-scope="scope">
              <span class="nutrition-text">
                蛋白 {{ scope.row.protein || 0 }}g / 脂肪 {{ scope.row.fat || 0 }}g
              </span>
            </template>
          </el-table-column>
          <el-table-column label="图片" align="center" width="140">
            <template slot-scope="scope">
              <record-images :images="getImageUrls(scope.row, ['imageUrl'])" />
            </template>
          </el-table-column>
          <el-table-column label="同步圈子" align="center" width="110">
            <template slot-scope="scope">
              <sync-summary :ids="parseSyncCircles(scope.row.syncToCircles)" />
            </template>
          </el-table-column>
          <el-table-column label="动态" align="center" width="90">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openSourceFeed('food_record', scope.row.id)">查动态</el-button>
            </template>
          </el-table-column>
          <el-table-column label="记录日期" align="center" prop="recordedAt" width="150" />
          <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>
        <pagination
          v-show="foodTotal > 0"
          :total="foodTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getFoodList"
        />
      </el-tab-pane>

      <el-tab-pane label="运动记录" name="exercise">
        <el-table v-loading="loading" :data="exerciseList" stripe>
          <el-table-column label="用户" align="center" width="130">
            <template slot-scope="scope">
              <user-link :user-id="scope.row.userId" />
            </template>
          </el-table-column>
          <el-table-column label="运动类型" align="center" prop="exerciseType" min-width="130" show-overflow-tooltip />
          <el-table-column label="时长" align="center" prop="durationMinutes" width="100">
            <template slot-scope="scope">{{ scope.row.durationMinutes || 0 }} 分钟</template>
          </el-table-column>
          <el-table-column label="消耗" align="center" prop="caloriesBurned" width="120">
            <template slot-scope="scope">
              <el-tag type="warning" size="small">-{{ scope.row.caloriesBurned || 0 }} kcal</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="距离(km)" align="center" prop="distance" width="100" />
          <el-table-column label="图片" align="center" width="140">
            <template slot-scope="scope">
              <record-images :images="getImageUrls(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="同步圈子" align="center" width="110">
            <template slot-scope="scope">
              <sync-summary :ids="parseSyncCircles(scope.row.syncToCircles)" />
            </template>
          </el-table-column>
          <el-table-column label="动态" align="center" width="90">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openSourceFeed('exercise_record', scope.row.id)">查动态</el-button>
            </template>
          </el-table-column>
          <el-table-column label="记录日期" align="center" prop="recordedAt" width="150" />
          <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>
        <pagination
          v-show="exerciseTotal > 0"
          :total="exerciseTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getExerciseList"
        />
      </el-tab-pane>

      <el-tab-pane label="饮水记录" name="water">
        <el-table v-loading="loading" :data="waterList" stripe>
          <el-table-column label="用户" align="center" width="130">
            <template slot-scope="scope">
              <user-link :user-id="scope.row.userId" />
            </template>
          </el-table-column>
          <el-table-column label="饮水量(ml)" align="center" prop="ml" width="120">
            <template slot-scope="scope">
              <span class="water-amount">{{ scope.row.ml || 0 }} ml</span>
            </template>
          </el-table-column>
          <el-table-column label="杯数" align="center" prop="cups" width="80" />
          <el-table-column label="图片" align="center" width="140">
            <template slot-scope="scope">
              <record-images :images="getImageUrls(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="动态" align="center" width="90">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openSourceFeed('water_record', scope.row.id)">查动态</el-button>
            </template>
          </el-table-column>
          <el-table-column label="记录日期" align="center" prop="recordedAt" width="150" />
          <el-table-column label="备注" align="center" prop="remark" min-width="160" show-overflow-tooltip />
        </el-table>
        <pagination
          v-show="waterTotal > 0"
          :total="waterTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getWaterList"
        />
      </el-tab-pane>

      <el-tab-pane label="习惯打卡" name="checkin">
        <el-table v-loading="loading" :data="checkinList" stripe>
          <el-table-column label="用户" align="center" width="130">
            <template slot-scope="scope">
              <user-link :user-id="scope.row.userId" />
            </template>
          </el-table-column>
          <el-table-column label="习惯ID" align="center" prop="habitId" width="100" />
          <el-table-column label="打卡图片" align="center" width="140">
            <template slot-scope="scope">
              <record-images :images="getImageUrls(scope.row)" />
            </template>
          </el-table-column>
          <el-table-column label="动态" align="center" width="90">
            <template slot-scope="scope">
              <el-button type="text" size="mini" @click="openSourceFeed('habit_checkin', scope.row.id)">查动态</el-button>
            </template>
          </el-table-column>
          <el-table-column label="打卡时间" align="center" prop="checkedAt" width="150" />
          <el-table-column label="备注" align="center" prop="note" min-width="180" show-overflow-tooltip />
        </el-table>
        <pagination
          v-show="checkinTotal > 0"
          :total="checkinTotal"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getCheckinList"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { listWeightRecords, listFoodRecords, listExerciseRecords, listWaterRecords } from '@/api/weight/records'
import { listCheckin } from '@/api/weight/checkin'
import { listUser } from '@/api/weight/user'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'
import { resolveImageList } from '@/utils/weightImages'
import weightAdminPageMixin from '@/mixins/weightAdminPage'

const DEFAULT_TAB = 'weight'
const VALID_TABS = ['weight', 'food', 'exercise', 'water', 'checkin']
const createDefaultQueryParams = () => ({
  pageNum: 1,
  pageSize: 10,
  userId: null,
  nickname: null
})

const RecordImages = {
  functional: true,
  props: {
    images: {
      type: Array,
      default: () => []
    }
  },
  render(h, context) {
    const images = context.props.images || []
    if (!images.length) {
      return h('span', '-')
    }
    return h('div', { class: 'record-images' }, images.slice(0, 3).map((image, index) => h('el-image', {
      key: image + index,
      class: 'record-image',
      props: {
        src: image,
        fit: 'cover',
        previewSrcList: images
      }
    })))
  }
}

const SyncSummary = {
  functional: true,
  props: {
    ids: {
      type: Array,
      default: () => []
    }
  },
  render(h, context) {
    const ids = context.props.ids || []
    if (!ids.length) {
      return h('span', '-')
    }
    return h('el-tag', { props: { size: 'small', type: 'success' } }, `已关联 ${ids.length}`)
  }
}

export default {
  name: 'Records',
  components: { RecordImages, SyncSummary },
  mixins: [weightAdminPageMixin],
  data() {
    return {
      loading: false,
      showSearch: true,
      activeTab: DEFAULT_TAB,
      routeTab: DEFAULT_TAB,
      routeUserId: null,
      dateRange: [],
      queryParams: createDefaultQueryParams(),
      weightList: [],
      weightTotal: 0,
      foodList: [],
      foodTotal: 0,
      exerciseList: [],
      exerciseTotal: 0,
      waterList: [],
      waterTotal: 0,
      checkinList: [],
      checkinTotal: 0,
      mealTypeDict: {
        '0': '早餐',
        '1': '午餐',
        '2': '晚餐',
        '3': '加餐'
      }
    }
  },
  watch: {
    '$route.query.userId'() {
      this.syncRouteContext()
    },
    '$route.query.tab'() {
      this.syncRouteContext()
    }
  },
  created() {
    this.syncRouteContext({ reload: false })
    this.fetchActiveTab()
  },
  methods: {
    getDefaultQueryParams() {
      return createDefaultQueryParams()
    },
    normalizeValue(value) {
      if (value === undefined || value === null || value === '') {
        return null
      }
      return String(value)
    },
    normalizeTab(tab) {
      return VALID_TABS.includes(tab) ? tab : DEFAULT_TAB
    },
    syncRouteContext({ reload = true } = {}) {
      const nextRouteUserId = this.normalizeValue(this.$route.query.userId)
      const nextRouteTab = this.normalizeTab(this.$route.query.tab)
      const userChanged = nextRouteUserId !== this.routeUserId
      const tabChanged = nextRouteTab !== this.routeTab

      this.routeUserId = nextRouteUserId
      this.routeTab = nextRouteTab

      if (userChanged) {
        this.queryParams.userId = nextRouteUserId
        this.queryParams.nickname = null
        this.queryParams.pageNum = 1
      }

      if (tabChanged) {
        this.activeTab = nextRouteTab
        this.queryParams.pageNum = 1
      }

      if (reload && (userChanged || tabChanged)) {
        this.fetchActiveTab()
      }
    },
    async resolveUserIdFilter() {
      const normalizedUserId = this.normalizeValue(this.queryParams.userId)
      if (normalizedUserId) {
        return normalizedUserId
      }

      const nickname = (this.queryParams.nickname || '').trim()
      if (!nickname) {
        return null
      }

      try {
        const userRes = await listUser({ nickname, pageSize: 20 })
        const matchedUsers = userRes.rows || []
        if (matchedUsers.length === 0) {
          this.$message.warning('未找到匹配的用户，请检查昵称或改用用户ID筛选')
          return undefined
        }
        if (matchedUsers.length > 1) {
          this.$message.warning('匹配到多个用户，请改用用户ID或更精确的昵称')
          return undefined
        }
        return String(matchedUsers[0].userId)
      } catch (error) {
        console.error('查询用户失败:', error)
        this.$message.error('查询用户失败，请稍后重试')
        return undefined
      }
    },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.queryParams.pageNum = 1
      this.fetchActiveTab()
    },
    async handleQuery() {
      this.queryParams.pageNum = 1
      const resolvedUserId = await this.resolveUserIdFilter()
      if (resolvedUserId === undefined) {
        return
      }
      this.queryParams.userId = resolvedUserId
      this.fetchActiveTab()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = this.getDefaultQueryParams()
      this.activeTab = DEFAULT_TAB
      this.routeTab = DEFAULT_TAB
      this.routeUserId = null

      const nextQuery = { ...this.$route.query }
      delete nextQuery.userId
      delete nextQuery.tab
      this.$router.replace({ path: this.$route.path, query: nextQuery }).catch(() => {})

      this.fetchActiveTab()
    },
    clearRouteUserFilter() {
      this.queryParams.userId = null
      this.queryParams.nickname = null
      this.queryParams.pageNum = 1
      this.routeUserId = null

      const nextQuery = { ...this.$route.query }
      delete nextQuery.userId
      this.$router.replace({ path: this.$route.path, query: nextQuery }).catch(() => {})

      this.fetchActiveTab()
    },
    fetchActiveTab() {
      switch (this.activeTab) {
        case 'food':
          this.getFoodList()
          break
        case 'exercise':
          this.getExerciseList()
          break
        case 'water':
          this.getWaterList()
          break
        case 'checkin':
          this.getCheckinList()
          break
        case 'weight':
        default:
          this.getWeightList()
          break
      }
    },
    getWeightList() {
      this.loading = true
      listWeightRecords(this.addRecordDateRange(this.queryParams)).then(res => {
        this.weightList = res.rows || []
        this.weightTotal = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getFoodList() {
      this.loading = true
      listFoodRecords(this.addRecordDateRange(this.queryParams)).then(res => {
        this.foodList = res.rows || []
        this.foodTotal = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getExerciseList() {
      this.loading = true
      listExerciseRecords(this.addRecordDateRange(this.queryParams)).then(res => {
        this.exerciseList = res.rows || []
        this.exerciseTotal = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getWaterList() {
      this.loading = true
      listWaterRecords(this.addRecordDateRange(this.queryParams)).then(res => {
        this.waterList = res.rows || []
        this.waterTotal = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getCheckinList() {
      this.loading = true
      listCheckin(this.addCheckinDateRange(this.queryParams)).then(res => {
        this.checkinList = res.rows || []
        this.checkinTotal = res.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    addRecordDateRange(params) {
      const query = { ...params }
      if (this.dateRange && this.dateRange.length === 2) {
        query['params[beginRecordedAt]'] = `${this.dateRange[0]} 00:00:00`
        query['params[endRecordedAt]'] = `${this.dateRange[1]} 23:59:59`
      }
      return query
    },
    addCheckinDateRange(params) {
      const query = { ...params }
      if (this.dateRange && this.dateRange.length === 2) {
        query['params[beginCheckedAt]'] = `${this.dateRange[0]} 00:00:00`
        query['params[endCheckedAt]'] = `${this.dateRange[1]} 23:59:59`
      }
      return query
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
    },
    parseSyncCircles(value) {
      if (!value) return []
      if (Array.isArray(value)) return value.filter(item => item !== null && item !== undefined)
      const text = String(value).trim()
      if (!text) return []
      try {
        const parsed = JSON.parse(text)
        return Array.isArray(parsed) ? parsed.filter(item => item !== null && item !== undefined) : []
      } catch (error) {
        return text.split(',').map(item => item.trim()).filter(Boolean)
      }
    },
    openSourceFeed(sourceRecordType, sourceRecordId) {
      if (!sourceRecordType || !sourceRecordId) return
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.circleFeed,
        query: {
          sourceRecordType,
          sourceRecordId: String(sourceRecordId)
        }
      })
    }
  }
}
</script>

<style scoped>
.table-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-context {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 28px;
}

.toolbar-hint {
  color: #909399;
  font-size: 13px;
}

.weight-value {
  font-weight: bold;
  color: #409EFF;
}

.water-amount {
  color: #67C23A;
  font-weight: bold;
}

.nutrition-text {
  color: #606266;
  font-size: 12px;
}

.record-images {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.record-image {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}
</style>
