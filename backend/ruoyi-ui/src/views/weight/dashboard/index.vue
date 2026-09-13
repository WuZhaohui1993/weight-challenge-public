<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card users">
          <div class="stat-icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.totalUsers || 0 }}</div>
            <div class="stat-label">用户总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card circles">
          <div class="stat-icon">
            <i class="el-icon-connection"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.totalCircles || 0 }}</div>
            <div class="stat-label">圈子总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card checkins">
          <div class="stat-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.todayCheckins || 0 }}</div>
            <div class="stat-label">今日打卡</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card records">
          <div class="stat-icon">
            <i class="el-icon-document"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.totalRecords || 0 }}</div>
            <div class="stat-label">五类记录</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card feedback">
          <div class="stat-icon">
            <i class="el-icon-service"></i>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ overview.pendingFeedback || 0 }}</div>
            <div class="stat-label">待处理反馈</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-section">
      <el-col :span="6">
        <el-card shadow="hover" class="operation-card" @click.native="goToRoute(weightAdminRoutes.circleFeed)">
          <div class="operation-value">{{ contentStats.todayFeeds || overview.todayFeeds || 0 }}</div>
          <div class="operation-label">今日新增动态</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="operation-card" @click.native="goToRoute(weightAdminRoutes.comment)">
          <div class="operation-value">{{ contentStats.todayComments || 0 }}</div>
          <div class="operation-label">今日新增评论</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="operation-card warning" @click.native="goToRoute(weightAdminRoutes.circleFeed, { status: '1' })">
          <div class="operation-value">{{ overview.pendingContent || 0 }}</div>
          <div class="operation-label">待审核内容</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="operation-card danger" @click.native="goToRoute(weightAdminRoutes.notification, { isRead: '0' })">
          <div class="operation-value">{{ contentStats.unreadNotifications || overview.unreadNotifications || 0 }}</div>
          <div class="operation-label">未读通知</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-section">
      <el-col :span="16">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>📈 最近7天打卡趋势</span>
          </div>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>🔥 热门圈子 Top5</span>
          </div>
          <div class="top-circles-list">
            <div v-for="(circle, index) in topCircles" :key="circle.id" class="circle-item clickable" @click="openCircle(circle)">
              <span class="rank" :class="'rank-' + (index + 1)">{{ index + 1 }}</span>
              <span class="icon">{{ circle.icon || '⚖️' }}</span>
              <span class="name">{{ circle.name }}</span>
              <span class="count">{{ circle.memberCount || 0 }}人</span>
            </div>
            <el-empty v-if="topCircles.length === 0" description="暂无数据"></el-empty>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="20" class="quick-actions">
      <el-col :span="24">
        <el-card shadow="hover">
          <div slot="header" class="card-header">
            <span>⚡ 快捷操作</span>
          </div>
          <div class="action-buttons">
            <el-button type="primary" icon="el-icon-user" @click="goToRoute(weightAdminRoutes.user)">用户管理</el-button>
            <el-button type="success" icon="el-icon-document" @click="goToRoute(weightAdminRoutes.records)">记录管理</el-button>
            <el-button type="warning" icon="el-icon-chat-dot-round" @click="goToRoute(weightAdminRoutes.circleFeed)">动态审核</el-button>
            <el-button type="danger" icon="el-icon-service" @click="goToRoute(weightAdminRoutes.feedback, { status: 'pending' })">用户反馈</el-button>
            <el-button type="info" icon="el-icon-lock" @click="goToRoute(weightAdminRoutes.certificate)">证书管理</el-button>
            <el-button type="info" icon="el-icon-connection" @click="goToRoute(weightAdminRoutes.circle)">圈子管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getOverview, getCheckinTrend, getTopCircles, getContentStats } from "@/api/weight/dashboard"
import * as echarts from 'echarts'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'

export default {
  name: "WeightDashboard",
  data() {
    return {
      overview: {},
      contentStats: {},
      topCircles: [],
      trendData: [],
      chart: null,
      weightAdminRoutes: WEIGHT_ADMIN_ROUTES
    }
  },
  mounted() {
    this.loadData()
  },
  beforeDestroy() {
    if (this.chart) {
      this.chart.dispose()
    }
  },
  methods: {
    async loadData() {
      // 加载概览数据
      getOverview().then(res => {
        this.overview = res.data || {}
      })
      
      // 加载热门圈子
      getTopCircles().then(res => {
        this.topCircles = res.data || []
      })

      getContentStats().then(res => {
        this.contentStats = res.data || {}
      })
      
      // 加载趋势数据并渲染图表
      getCheckinTrend().then(res => {
        this.trendData = res.data || []
        this.$nextTick(() => {
          this.initChart()
        })
      })
    },
    openCircle(circle) {
      if (!circle || !circle.id) return
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.circle,
        query: {
          circleId: String(circle.id),
          action: 'detail'
        }
      })
    },
    goToRoute(path, query) {
      if (!path) return
      const target = query ? { path, query } : path
      this.$router.push(target).catch(() => {})
    },
    initChart() {
      if (!this.$refs.trendChart) return
      
      this.chart = echarts.init(this.$refs.trendChart)
      
      const dates = this.trendData.map(item => item.date)
      const counts = this.trendData.map(item => item.count)
      
      const option = {
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLine: { lineStyle: { color: '#ddd' } },
          axisLabel: { color: '#666' }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisLabel: { color: '#666' },
          splitLine: { lineStyle: { color: '#eee' } }
        },
        series: [{
          name: '打卡人数',
          type: 'line',
          data: counts,
          smooth: true,
          itemStyle: { color: '#409EFF' },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          }
        }]
      }
      
      this.chart.setOption(option)
      
      // 响应式
      window.addEventListener('resize', () => {
        this.chart && this.chart.resize()
      })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
}

.stat-card.users { border-top: 3px solid #409EFF; }
.stat-card.circles { border-top: 3px solid #67C23A; }
.stat-card.checkins { border-top: 3px solid #E6A23C; }
.stat-card.records { border-top: 3px solid #909399; }
.stat-card.feedback { border-top: 3px solid #F56C6C; }

.stat-icon {
  font-size: 40px;
  margin-right: 20px;
  opacity: 0.8;
}

.stat-card.users .stat-icon { color: #409EFF; }
.stat-card.circles .stat-icon { color: #67C23A; }
.stat-card.checkins .stat-icon { color: #E6A23C; }
.stat-card.records .stat-icon { color: #909399; }
.stat-card.feedback .stat-icon { color: #F56C6C; }

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-section {
  margin-bottom: 20px;
}

.content-section {
  margin-bottom: 20px;
}

.operation-card {
  cursor: pointer;
  border-top: 3px solid #409EFF;
}

.operation-card.warning {
  border-top-color: #E6A23C;
}

.operation-card.danger {
  border-top-color: #F56C6C;
}

.operation-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.operation-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.chart-container {
  height: 300px;
}

.card-header {
  font-weight: bold;
}

.top-circles-list {
  min-height: 250px;
}

.circle-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.circle-item.clickable {
  cursor: pointer;
}

.circle-item:last-child {
  border-bottom: none;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #ddd;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  margin-right: 10px;
}

.rank-1 { background: #FFD700; }
.rank-2 { background: #C0C0C0; }
.rank-3 { background: #CD7F32; }

.icon {
  font-size: 20px;
  margin-right: 10px;
}

.name {
  flex: 1;
  color: #303133;
}

.count {
  color: #909399;
  font-size: 12px;
}

.action-buttons {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.action-buttons .el-button {
  flex: 1;
  min-width: 120px;
}
</style>
