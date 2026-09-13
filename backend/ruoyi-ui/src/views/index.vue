<template>
  <div class="app-container home">
    <section class="home-hero">
      <div>
        <p class="home-kicker">Xingwan Tongxing Admin</p>
        <h2>星绾同行运营后台</h2>
        <p class="home-desc">
          面向圈子挑战、健康记录、用户激励和内容治理的管理工作台。优先处理真实运营数据，减少无关框架信息干扰。
        </p>
        <div class="hero-actions">
          <el-button type="primary" icon="el-icon-data-analysis" @click="goRoute(weightAdminRoutes.dashboard)">查看运营概览</el-button>
          <el-button icon="el-icon-connection" @click="goRoute(weightAdminRoutes.circle)">管理圈子</el-button>
        </div>
      </div>
      <div class="hero-panel">
        <span class="panel-label">今日重点</span>
        <strong>圈子活跃、任务完成、异常内容</strong>
        <p>从数据概览进入，再按用户、圈子、记录和通知逐项处理。</p>
      </div>
    </section>

    <el-row :gutter="20" class="operate-grid">
      <el-col v-for="item in operateItems" :key="item.title" :xs="24" :sm="12" :lg="6">
        <button type="button" class="operate-card" @click="goRoute(item.path)">
          <i :class="item.icon"></i>
          <span>{{ item.title }}</span>
          <em>{{ item.desc }}</em>
        </button>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :xs="24" :lg="14">
        <el-card class="work-card" shadow="never">
          <div slot="header" class="card-header">
            <span>运营模块</span>
          </div>
          <div class="module-list">
            <div v-for="module in modules" :key="module.title" class="module-item">
              <div>
                <strong>{{ module.title }}</strong>
                <p>{{ module.desc }}</p>
              </div>
              <el-button size="mini" type="text" @click="goRoute(module.path)">进入</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card class="work-card" shadow="never">
          <div slot="header" class="card-header">
            <span>合并前检查</span>
          </div>
          <ul class="check-list">
            <li>确认管理员菜单只保留当前版本需要开放的业务入口。</li>
            <li>检查小程序端创建圈子、打卡、记录上传和通知链路。</li>
            <li>发布前核对接口域名、上传路径、隐私协议和提审截图。</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'

export default {
  name: "Index",
  data() {
    return {
      weightAdminRoutes: WEIGHT_ADMIN_ROUTES,
      operateItems: [
        { title: '用户管理', desc: '资料、体重目标、同步偏好', icon: 'el-icon-user', path: WEIGHT_ADMIN_ROUTES.user },
        { title: '圈子管理', desc: '挑战配置、成员状态、排行', icon: 'el-icon-connection', path: WEIGHT_ADMIN_ROUTES.circle },
        { title: '记录管理', desc: '体重、饮食、运动、饮水', icon: 'el-icon-document', path: WEIGHT_ADMIN_ROUTES.records },
        { title: '消息通知', desc: '系统提醒、互动消息、任务提示', icon: 'el-icon-message', path: WEIGHT_ADMIN_ROUTES.notification },
        { title: '证书管理', desc: '域名证书状态、检测和续签', icon: 'el-icon-lock', path: WEIGHT_ADMIN_ROUTES.certificate }
      ],
      modules: [
        { title: '运营仪表盘', desc: '查看核心数据、打卡趋势和热门圈子。', path: WEIGHT_ADMIN_ROUTES.dashboard },
        { title: '目标与任务', desc: '维护圈子目标、习惯模板和每日任务。', path: WEIGHT_ADMIN_ROUTES.category },
        { title: '内容与互动', desc: '处理圈子动态、评论、点赞和关注关系。', path: WEIGHT_ADMIN_ROUTES.circleFeed },
        { title: '押金与成就', desc: '核对押金记录，维护成就徽章和激励配置。', path: WEIGHT_ADMIN_ROUTES.deposit }
      ]
    }
  },
  methods: {
    goRoute(path) {
      this.$router.push(path).catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  min-height: calc(100vh - 84px);
  background: #f5f7fa;
  color: #172033;
}

.home-hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 340px;
  gap: 32px;
  padding: 34px 36px;
  border-radius: 10px;
  background:
    linear-gradient(135deg, rgba(16, 42, 54, 0.96), rgba(27, 127, 102, 0.88)),
    #102a36;
  color: #ffffff;
}

.home-kicker {
  margin: 0 0 10px;
  color: #8ce1b7;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.home h2 {
  margin: 0;
  font-size: 34px;
  line-height: 1.2;
}

.home-desc {
  max-width: 690px;
  margin: 16px 0 0;
  color: rgba(255, 255, 255, 0.74);
  font-size: 15px;
  line-height: 1.8;
}

.hero-actions {
  margin-top: 28px;
}

.hero-panel {
  align-self: stretch;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 28px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.08);
}

.panel-label {
  color: #8ce1b7;
  font-size: 13px;
  margin-bottom: 14px;
}

.hero-panel strong {
  font-size: 22px;
  line-height: 1.4;
}

.hero-panel p {
  margin: 14px 0 0;
  color: rgba(255, 255, 255, 0.68);
  line-height: 1.7;
}

.operate-grid {
  margin-top: 20px;
}

.operate-card {
  width: 100%;
  min-height: 142px;
  padding: 22px;
  border: 1px solid #e6ebf1;
  border-radius: 8px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  transition: border-color .2s ease, transform .2s ease, box-shadow .2s ease;
}

.operate-card:hover {
  border-color: #2bb37d;
  transform: translateY(-2px);
  box-shadow: 0 14px 34px rgba(25, 46, 69, 0.08);
}

.operate-card i {
  display: block;
  color: #1b7f66;
  font-size: 26px;
  margin-bottom: 18px;
}

.operate-card span,
.operate-card em {
  display: block;
}

.operate-card span {
  color: #172033;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 8px;
}

.operate-card em {
  color: #6e7d90;
  font-style: normal;
  font-size: 13px;
  line-height: 1.6;
}

.work-card {
  margin-top: 20px;
  border-color: #e6ebf1;
  border-radius: 8px;
}

.card-header {
  font-weight: 700;
  color: #172033;
}

.module-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 18px 0;
  border-bottom: 1px solid #edf1f5;
}

.module-item:last-child {
  border-bottom: 0;
}

.module-item strong {
  color: #172033;
  font-size: 15px;
}

.module-item p {
  margin: 7px 0 0;
  color: #738196;
  font-size: 13px;
}

.check-list {
  margin: 0;
  padding-left: 18px;
  color: #5f6f84;
  line-height: 2;
}

@media (max-width: 960px) {
  .home-hero {
    grid-template-columns: 1fr;
    padding: 28px 24px;
  }
}
</style>
