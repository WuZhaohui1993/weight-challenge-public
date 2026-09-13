<template>
  <span>
  <detail-drawer
    :visible="visible"
    :title="title"
    :size="size"
    :icon="circle ? (circle.icon || '🎯') : '🎯'"
    :header-title="circle ? circle.name : ''"
    :header-subtitle="circle ? `ID: ${circle.id}` : ''"
    :header-tags="headerTags"
    :show-header="true"
    @update:visible="$emit('update:visible', $event)"
  >
    <template #sections>
      <!-- 基础信息 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">
          📋 基础信息
          <el-button style="float: right; padding: 3px 0" type="text" icon="el-icon-edit" @click="handleEdit">编辑</el-button>
        </div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="类型">
            <el-tag :type="circle && circle.type === '0' ? 'success' : 'warning'" size="small">
              {{ circle && circle.type === '0' ? '公开' : '私密' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="circle && circle.status === '0' ? 'success' : 'danger'" size="small">
              {{ circle && circle.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="押金要求">¥{{ circle ? (circle.depositRequired || 0) : 0 }}</el-descriptions-item>
          <el-descriptions-item label="持续天数">{{ circle ? (circle.durationDays || '-') : '-' }} 天</el-descriptions-item>
          <el-descriptions-item label="开始日期">{{ circle ? formatDate(circle.startDate) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="结束日期">{{ circle ? formatDate(circle.endDate) : '-' }}</el-descriptions-item>
          <el-descriptions-item label="惩罚规则">
            <el-tag :type="penaltyTagType(circle && circle.penaltyRule)" size="small">
              {{ penaltyText(circle && circle.penaltyRule) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ circle ? (circle.createTime || '-') : '-' }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="circle && circle.description" class="description-text">
          <strong>简介：</strong>{{ circle.description }}
        </div>
      </el-card>

      <!-- 统计概览 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">📊 统计概览</div>
        <el-row :gutter="16">
          <el-col :span="6">
            <div class="stat-item clickable" @click="activeTab = 'members'">
              <div class="stat-value primary">{{ circle ? (circle.memberCount || 0) : 0 }}</div>
              <div class="stat-label">成员数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item clickable" @click="activeTab = 'deposits'">
              <div class="stat-value warning">¥{{ depositStats.total }}</div>
              <div class="stat-label">总押金</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item clickable" @click="activeTab = 'goals'">
              <div class="stat-value success">{{ goals.length }}</div>
              <div class="stat-label">目标数</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item clickable" @click="activeTab = 'feeds'">
              <div class="stat-value info">{{ feedTotal }}</div>
              <div class="stat-label">动态数</div>
            </div>
          </el-col>
        </el-row>
      </el-card>

      <!-- 标签页数据 -->
      <el-card class="section-card">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 目标配置 -->
          <el-tab-pane label="🎯 目标配置" name="goals">
            <div class="tab-toolbar">
              <el-button size="mini" type="primary" icon="el-icon-plus" @click="showAddGoalDialog">添加目标</el-button>
              <el-button size="mini" icon="el-icon-refresh" @click="loadGoals">刷新</el-button>
            </div>
            <div v-if="goalsLoading" class="loading-text">加载中...</div>
            <div v-else-if="goals.length === 0" class="empty-text">暂无目标配置，点击“添加目标”创建</div>
            <el-table v-else :data="goals" size="mini" max-height="200">
              <el-table-column label="目标名称" prop="goalName" min-width="100" />
              <el-table-column label="类型" min-width="70">
                <template slot-scope="scope">{{ goalTypeDict[scope.row.goalType] || scope.row.goalType }}</template>
              </el-table-column>
              <el-table-column label="目标值" min-width="80">
                <template slot-scope="scope">{{ scope.row.targetValue }} {{ scope.row.targetUnit }}</template>
              </el-table-column>
              <el-table-column label="周期" min-width="50">
                <template slot-scope="scope">{{ periodDict[scope.row.period] || '-' }}</template>
              </el-table-column>
              <el-table-column label="必选" min-width="50">
                <template slot-scope="scope">
                  <el-switch v-model="scope.row.isRequired" :active-value="1" :inactive-value="0" @change="updateGoalRequired(scope.row)" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" icon="el-icon-edit" @click="editGoal(scope.row)" />
                  <el-button size="mini" type="text" icon="el-icon-delete" class="danger-text" @click="removeGoal(scope.row)" />
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 成员列表 -->
          <el-tab-pane label="👥 成员列表" name="members">
            <div class="tab-toolbar">
              <el-button size="mini" type="primary" icon="el-icon-plus" @click="showAddMemberDialog">添加成员</el-button>
              <el-button size="mini" icon="el-icon-refresh" @click="loadMembers">刷新</el-button>
            </div>
            <div v-if="membersLoading" class="loading-text">加载中...</div>
            <div v-else-if="members.length === 0" class="empty-text">暂无成员</div>
            <el-table v-else :data="members" size="mini" max-height="250">
              <el-table-column label="用户" min-width="80">
                <template slot-scope="scope">
                  <user-link :user-id="scope.row.userId" />
                </template>
              </el-table-column>
              <el-table-column label="角色" min-width="100">
                <template slot-scope="scope">
                  <el-select v-model="scope.row.role" size="mini" @change="updateMemberRole(scope.row)">
                    <el-option label="管理员" value="0" />
                    <el-option label="成员" value="1" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="状态" min-width="70">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.status"
                    active-value="0"
                    inactive-value="1"
                    @change="updateMemberStatus(scope.row)"
                  />
                </template>
              </el-table-column>
              <el-table-column label="加入时间" min-width="90">
                <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="50">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" class="danger-text" @click="removeMember(scope.row)">移除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 押金记录 -->
          <el-tab-pane label="💰 押金记录" name="deposits">
            <div v-if="depositsLoading" class="loading-text">加载中...</div>
            <div v-else-if="deposits.length === 0" class="empty-text">暂无押金记录</div>
            <el-table v-else :data="deposits" size="mini" max-height="200">
              <el-table-column label="用户" min-width="100">
                <template slot-scope="scope">
                  <user-link :user-id="scope.row.userId" />
                </template>
              </el-table-column>
              <el-table-column label="金额" min-width="80">
                <template slot-scope="scope">
                  <span class="deposit-amount">¥{{ scope.row.amount }}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" min-width="70">
                <template slot-scope="scope">
                  <el-tag :type="depositStatusType(scope.row.status)" size="mini">{{ depositStatusText(scope.row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="支付时间" min-width="100">
                <template slot-scope="scope">{{ formatDate(scope.row.paidAt) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 最近动态 -->
          <el-tab-pane label="📝 最近动态" name="feeds">
            <div v-if="feedsLoading" class="loading-text">加载中...</div>
            <div v-else-if="feeds.length === 0" class="empty-text">暂无动态</div>
            <el-table v-else :data="feeds" size="mini" max-height="200">
              <el-table-column label="用户" min-width="100">
                <template slot-scope="scope">
                  <user-link :user-id="scope.row.userId" />
                </template>
              </el-table-column>
              <el-table-column label="类型" min-width="70">
                <template slot-scope="scope">{{ feedTypeDict[scope.row.feedType] || scope.row.feedType }}</template>
              </el-table-column>
              <el-table-column label="内容" prop="content" min-width="150" show-overflow-tooltip />
              <el-table-column label="互动" min-width="80">
                <template slot-scope="scope">👍{{ scope.row.likesCount || 0 }} 💬{{ scope.row.commentsCount || 0 }}</template>
              </el-table-column>
              <el-table-column label="时间" min-width="100">
                <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
              </el-table-column>
            </el-table>
          </el-tab-pane>

          <!-- 排行榜 -->
          <el-tab-pane label="🏆 排行榜" name="rankings">
            <div v-if="rankingsLoading" class="loading-text">加载中...</div>
            <div v-else-if="rankings.length === 0" class="empty-text">暂无排行数据</div>
            <el-table v-else :data="rankings" size="mini" max-height="200">
              <el-table-column label="排名" min-width="60">
                <template slot-scope="scope">
                  <span :class="'rank-' + scope.row.rank">{{ scope.row.rank }}</span>
                </template>
              </el-table-column>
              <el-table-column label="用户" min-width="100">
                <template slot-scope="scope">
                  <user-link :user-id="scope.row.userId" />
                </template>
              </el-table-column>
              <el-table-column label="类型" min-width="80">
                <template slot-scope="scope">{{ rankTypeDict[scope.row.rankType] || scope.row.rankType }}</template>
              </el-table-column>
              <el-table-column label="数值" prop="rankValue" min-width="80" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-card>

      <!-- 快捷操作 -->
      <el-card class="section-card">
        <div slot="header" class="section-header">⚡ 快捷操作</div>
        <div class="actions-wrapper">
          <el-button size="small" type="primary" icon="el-icon-edit" @click="handleEdit">编辑圈子</el-button>
          <el-button size="small" icon="el-icon-plus" @click="showAddMemberDialog">添加成员</el-button>
          <el-button size="small" icon="el-icon-s-data" @click="goToRanking">查看排行</el-button>
        </div>
      </el-card>
    </template>
  </detail-drawer>

  <!-- 添加成员对话框 -->
  <el-dialog title="添加成员" :visible.sync="addMemberDialogVisible" width="400px" append-to-body>
    <el-form :model="addMemberForm" label-width="80px">
      <el-form-item label="用户ID">
        <el-input v-model="addMemberForm.userId" placeholder="请输入用户ID" type="number" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="addMemberForm.role" style="width: 100%">
          <el-option label="管理员" value="0" />
          <el-option label="成员" value="1" />
        </el-select>
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="addMemberDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="confirmAddMember">确 定</el-button>
    </div>
  </el-dialog>

  <!-- 添加/编辑目标对话框 -->
  <el-dialog :title="goalDialogTitle" :visible.sync="goalDialogVisible" width="500px" append-to-body>
    <el-form :model="goalForm" :rules="goalRules" ref="goalFormRef" label-width="80px">
      <el-form-item label="目标名称" prop="goalName">
        <el-input v-model="goalForm.goalName" placeholder="例如：每日跑步6公里" />
      </el-form-item>
      <el-form-item label="目标类型" prop="goalType">
        <el-select v-model="goalForm.goalType" placeholder="选择类型" style="width: 100%">
          <el-option label="减重" value="weight_loss" />
          <el-option label="打卡" value="checkin" />
          <el-option label="运动" value="exercise" />
          <el-option label="饮食" value="diet" />
          <el-option label="习惯" value="habit" />
          <el-option label="自定义" value="custom" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="目标值" prop="targetValue">
            <el-input-number v-model="goalForm.targetValue" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单位" prop="targetUnit">
            <el-input v-model="goalForm.targetUnit" placeholder="kg/次/分钟" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="周期" prop="period">
        <el-select v-model="goalForm.period" style="width: 100%">
          <el-option label="每日" value="0" />
          <el-option label="每周" value="1" />
          <el-option label="每月" value="2" />
          <el-option label="总计" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="必选目标">
        <el-switch v-model="goalForm.isRequired" :active-value="1" :inactive-value="0" />
      </el-form-item>
    </el-form>
    <div slot="footer">
      <el-button @click="goalDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="submitGoalForm">确 定</el-button>
    </div>
  </el-dialog>
  </span>
</template>

<script>
import { getCircle } from "@/api/weight/circle"
import { listMember, addMember, updateMember, delMember } from "@/api/weight/member"
import { listDeposit } from "@/api/weight/deposit"
import { listGoal, addGoal, updateGoal, delGoal } from "@/api/weight/goal"
import { listFeed } from "@/api/weight/feed"
import { listRanking } from "@/api/weight/ranking"
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'
import DetailDrawer from "@/components/DetailDrawer"

export default {
  name: "CircleProfileDrawer",
  components: { DetailDrawer },
  props: {
    visible: { type: Boolean, default: false },
    circleId: { type: [Number, String], default: null },
    size: { type: String, default: '50%' }
  },
  data() {
    return {
      circle: null,
      activeTab: 'goals',
      members: [],
      membersLoading: false,
      deposits: [],
      depositsLoading: false,
      depositStats: { total: 0 },
      goals: [],
      goalsLoading: false,
      feeds: [],
      feedsLoading: false,
      feedTotal: 0,
      rankings: [],
      rankingsLoading: false,
      // 添加成员对话框
      addMemberDialogVisible: false,
      addMemberForm: { userId: '', role: '1' },
      // 目标管理对话框
      goalDialogVisible: false,
      goalDialogTitle: '添加目标',
      goalForm: { id: null, goalName: '', goalType: 'checkin', targetValue: 1, targetUnit: '次', period: '0', isRequired: 0 },
      goalRules: {
        goalName: [{ required: true, message: '请输入目标名称', trigger: 'blur' }],
        goalType: [{ required: true, message: '请选择目标类型', trigger: 'change' }],
        targetValue: [{ required: true, message: '请输入目标值', trigger: 'blur' }]
      },
      // 字典
      goalTypeDict: { 'weight_loss': '减重', 'checkin': '打卡', 'exercise': '运动', 'diet': '饮食', 'habit': '习惯', 'custom': '自定义' },
      periodDict: { '0': '每日', '1': '每周', '2': '每月', '3': '总计' },
      feedTypeDict: { 'weight': '体重', 'food': '饮食', 'exercise': '运动', 'habit': '习惯', 'text': '文字' },
      rankTypeDict: { 'weight_loss': '减重榜', 'checkin': '打卡榜', 'exercise': '运动榜' }
    }
  },
  computed: {
    title() {
      return this.circle ? `${this.circle.icon || '🎯'} ${this.circle.name} - 360°全景` : '圈子详情'
    },
    headerTags() {
      if (!this.circle) return []
      return [
        { text: this.circle.type === '0' ? '公开' : '私密', type: this.circle.type === '0' ? 'success' : 'warning' },
        { text: this.circle.status === '0' ? '正常' : '停用', type: this.circle.status === '0' ? 'success' : 'danger' }
      ]
    }
  },
  watch: {
    visible(val) {
      if (val && this.circleId) {
        this.loadCircleData()
      }
    },
    circleId(val) {
      if (val && this.visible) {
        this.loadCircleData()
      }
    }
  },
  methods: {
    formatDate(date) {
      if (!date) return '-'
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    penaltyText(rule) {
      const map = { '0': '无', '1': '提醒', '2': '扣押金', '3': '踢出' }
      return map[rule] || '-'
    },
    penaltyTagType(rule) {
      const map = { '0': 'info', '1': 'warning', '2': 'danger', '3': 'danger' }
      return map[rule] || 'info'
    },
    depositStatusText(status) {
      const map = { '0': '待支付', '1': '已支付', '2': '已退还', '3': '已扣除' }
      return map[status] || '未知'
    },
    depositStatusType(status) {
      const map = { '0': 'warning', '1': 'success', '2': 'info', '3': 'danger' }
      return map[status] || ''
    },
    async loadCircleData() {
      if (!this.circleId) return
      try {
        const res = await getCircle(this.circleId)
        this.circle = res.data
      } catch (e) {
        console.error('加载圈子信息失败:', e)
      }
      this.loadMembers()
      this.loadDeposits()
      this.loadGoals()
      this.loadFeeds()
      this.loadRankings()
    },
    async loadMembers() {
      this.membersLoading = true
      try {
        const res = await listMember({ circleId: this.circleId, pageSize: 10 })
        this.members = res.rows || []
      } catch (e) { console.error('加载成员失败:', e) }
      this.membersLoading = false
    },
    async loadDeposits() {
      this.depositsLoading = true
      try {
        const res = await listDeposit({ circleId: this.circleId, pageSize: 10 })
        this.deposits = res.rows || []
        this.depositStats.total = this.deposits.filter(d => d.status === '1').reduce((sum, d) => sum + parseFloat(d.amount || 0), 0).toFixed(2)
      } catch (e) { console.error('加载押金记录失败:', e) }
      this.depositsLoading = false
    },
    async loadGoals() {
      this.goalsLoading = true
      try {
        const res = await listGoal({ circleId: this.circleId })
        this.goals = res.rows || []
      } catch (e) { console.error('加载目标失败:', e) }
      this.goalsLoading = false
    },
    async loadFeeds() {
      this.feedsLoading = true
      try {
        const res = await listFeed({ circleId: this.circleId, pageSize: 5 })
        this.feeds = res.rows || []
        this.feedTotal = res.total || 0
      } catch (e) { console.error('加载动态失败:', e) }
      this.feedsLoading = false
    },
    async loadRankings() {
      this.rankingsLoading = true
      try {
        const res = await listRanking({ circleId: this.circleId, pageSize: 10 })
        this.rankings = res.rows || []
      } catch (e) { console.error('加载排行榜失败:', e) }
      this.rankingsLoading = false
    },
    handleEdit() {
      this.$emit('edit', this.circle)
    },
    // 成员管理方法
    showAddMemberDialog() {
      this.addMemberForm = { userId: '', role: '1' }
      this.addMemberDialogVisible = true
    },
    async confirmAddMember() {
      if (!this.addMemberForm.userId) {
        this.$message.warning('请输入用户ID')
        return
      }
      try {
        await addMember({
          circleId: this.circleId,
          userId: this.addMemberForm.userId,
          role: this.addMemberForm.role,
          status: '1'
        })
        this.$message.success('添加成员成功')
        this.addMemberDialogVisible = false
        this.loadMembers()
      } catch (e) {
        this.$message.error('添加成员失败')
      }
    },
    async updateMemberRole(member) {
      try {
        await updateMember({ id: member.id, role: member.role })
        this.$message.success('角色更新成功')
      } catch (e) {
        this.$message.error('更新失败')
        this.loadMembers()
      }
    },
    async updateMemberStatus(member) {
      try {
        await updateMember({ id: member.id, status: member.status })
        this.$message.success('状态更新成功')
      } catch (e) {
        this.$message.error('更新失败')
        this.loadMembers()
      }
    },
    removeMember(member) {
      this.$confirm(`确定移除该成员吗？`, '提示', { type: 'warning' }).then(async () => {
        try {
          await delMember(member.id)
          this.$message.success('移除成功')
          this.loadMembers()
        } catch (e) {
          this.$message.error('移除失败')
        }
      }).catch(() => {})
    },
    goToRanking() {
      this.$router.push({ path: WEIGHT_ADMIN_ROUTES.ranking, query: { circleId: this.circleId } })
      this.$emit('update:visible', false)
    },
    // 目标管理方法
    showAddGoalDialog() {
      this.goalDialogTitle = '添加目标'
      this.goalForm = { id: null, goalName: '', goalType: 'checkin', targetValue: 1, targetUnit: '次', period: '0', isRequired: 0 }
      this.goalDialogVisible = true
    },
    editGoal(goal) {
      this.goalDialogTitle = '编辑目标'
      this.goalForm = { ...goal }
      this.goalDialogVisible = true
    },
    async submitGoalForm() {
      this.$refs.goalFormRef.validate(async valid => {
        if (!valid) return
        try {
          const data = { ...this.goalForm, circleId: this.circleId }
          if (this.goalForm.id) {
            await updateGoal(data)
            this.$message.success('目标更新成功')
          } else {
            await addGoal(data)
            this.$message.success('目标添加成功')
          }
          this.goalDialogVisible = false
          this.loadGoals()
        } catch (e) {
          this.$message.error('操作失败')
        }
      })
    },
    async updateGoalRequired(goal) {
      try {
        await updateGoal({ id: goal.id, isRequired: goal.isRequired })
        this.$message.success('更新成功')
      } catch (e) {
        this.$message.error('更新失败')
        this.loadGoals()
      }
    },
    removeGoal(goal) {
      this.$confirm(`确定删除目标「${goal.goalName}」吗？`, '提示', { type: 'warning' }).then(async () => {
        try {
          await delGoal(goal.id)
          this.$message.success('删除成功')
          this.loadGoals()
        } catch (e) {
          this.$message.error('删除失败')
        }
      }).catch(() => {})
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

.description-text {
  margin-top: 12px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 13px;
  color: #606266;
}

.stat-item {
  text-align: center;
  padding: 12px 0;
}

.stat-item.clickable {
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}

.stat-item.clickable:hover {
  background: #f5f7fa;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.stat-value.primary { color: #409eff; }
.stat-value.warning { color: #e6a23c; }
.stat-value.success { color: #67c23a; }
.stat-value.info { color: #909399; }

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.loading-text, .empty-text {
  text-align: center;
  padding: 20px;
  color: #909399;
}

.deposit-amount {
  color: #e6a23c;
  font-weight: bold;
}

.actions-wrapper {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.rank-1 { color: #ffd700; font-weight: bold; }
.rank-2 { color: #c0c0c0; font-weight: bold; }
.rank-3 { color: #cd7f32; font-weight: bold; }

.tab-toolbar {
  margin-bottom: 10px;
  display: flex;
  gap: 8px;
}

.danger-text {
  color: #f56c6c !important;
}
</style>
