<template>
  <div class="app-container">
    <!-- 精简的搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入昵称"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="queryParams.gender" placeholder="请选择" clearable style="width: 120px">
          <el-option label="未知" value="0" />
          <el-option label="男" value="1" />
          <el-option label="女" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['ruoyi-weight:user:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:user:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['ruoyi-weight:user:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 优化的用户列表 -->
    <el-table
      v-loading="loading"
      :data="userList"
      :default-sort="defaultSort"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="用户ID" align="center" prop="userId" width="80" />
      <el-table-column label="头像" align="center" width="70">
        <template slot-scope="scope">
          <el-avatar :size="40" :src="resolveAvatar(scope.row.avatar)" />
        </template>
      </el-table-column>
      <el-table-column label="昵称" align="center" prop="nickname" min-width="100" />
      <el-table-column label="性别" align="center" width="70">
        <template slot-scope="scope">
          <el-tag :type="genderTagType(scope.row.gender)" size="small">
            {{ genderText(scope.row.gender) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="sourceTagType(scope.row.source)" size="small">
            {{ sourceText(scope.row.source) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="当前体重" align="center" width="100">
        <template slot-scope="scope">
          <span class="weight-value">{{ scope.row.currentWeight || '-' }} kg</span>
        </template>
      </el-table-column>
      <el-table-column label="目标体重" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.targetWeight || '-' }} kg</span>
        </template>
      </el-table-column>
      <el-table-column label="BMI" align="center" prop="bmi" width="70">
        <template slot-scope="scope">
          <el-tag :type="bmiTagType(scope.row.bmi)" size="small">
            {{ scope.row.bmi || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" align="center" prop="createTime" width="160" sortable="custom">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="连续打卡" align="center" width="90">
        <template slot-scope="scope">
          <span class="streak-days">🔥 {{ scope.row.streakDays || 0 }}天</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">360°</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['ruoyi-weight:user:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:user:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改用户扩展信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="750px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="activeTab">
          <!-- Tab 1: 基础信息 -->
          <el-tab-pane label="基础信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="24" class="avatar-section">
                <el-form-item label="头像">
                  <div class="avatar-uploader">
                    <el-avatar :size="80" :src="resolveAvatar(form.avatar)" />
                    <el-upload
                      class="avatar-upload-btn"
                      :action="uploadUrl"
                      :headers="uploadHeaders"
                      :show-file-list="false"
                      :on-success="handleAvatarSuccess"
                      :before-upload="beforeAvatarUpload"
                    >
                      <el-button size="mini" type="primary">更换头像</el-button>
                    </el-upload>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="form.nickname" placeholder="请输入昵称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
                    <el-option label="未知" value="0" />
                    <el-option label="男" value="1" />
                    <el-option label="女" value="2" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="form.phone" placeholder="请输入手机号" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="生日" prop="birthday">
                  <el-date-picker clearable v-model="form.birthday" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- Tab 2: 健康数据 -->
          <el-tab-pane label="健康数据" name="health">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="身高(cm)" prop="height">
                  <el-input-number v-model="form.height" :min="100" :max="250" :precision="1" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="目标体重(kg)" prop="targetWeight">
                  <el-input-number v-model="form.targetWeight" :precision="1" :step="0.5" :min="30" :max="200" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="当前体重(kg)">
                  <el-input v-model="form.currentWeight" disabled>
                    <template slot="append">kg</template>
                  </el-input>
                  <div class="form-tip">由最新体重记录自动更新</div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="BMI指数">
                  <el-tag :type="bmiTagType(form.bmi)" size="medium">{{ form.bmi || '-' }}</el-tag>
                  <span class="bmi-label">{{ bmiLabel(form.bmi) }}</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="建议摄入上限" prop="dailyCalorieTarget">
                  <el-input-number v-model="form.dailyCalorieTarget" :step="100" :min="0" :max="5000" disabled style="width: 100%" />
                  <div class="form-tip">由用户基础资料和目标自动计算</div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="饮水目标(杯)" prop="dailyWaterTarget">
                  <el-input-number v-model="form.dailyWaterTarget" :min="1" :max="20" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- Tab 3: 偏好设置 -->
          <el-tab-pane label="偏好设置" name="preferences">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="圈子功能">
                  <el-switch
                    v-model="form.circleFeatureEnabled"
                    :active-value="1"
                    :inactive-value="0"
                    active-text="启用"
                    inactive-text="禁用"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="同步模式" prop="syncMode">
                  <el-radio-group v-model="form.syncMode">
                    <el-radio label="0">实时同步</el-radio>
                    <el-radio label="1">手动同步</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="24">
                <el-form-item label="主圈子" prop="mainCircleId">
                  <el-select v-model="form.mainCircleId" placeholder="选择首页展示的主圈子" clearable style="width: 100%">
                    <el-option
                      v-for="circle in userCircles"
                      :key="circle.circleId"
                      :label="circle.circleName || `圈子 ${circle.circleId}`"
                      :value="circle.circleId"
                    />
                  </el-select>
                  <div class="form-tip">设置在首页展示的主圈子</div>
                </el-form-item>
              </el-col>
            </el-row>
          </el-tab-pane>

          <!-- Tab 4: 账号信息 -->
          <el-tab-pane label="账号信息" name="account">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="用户ID">{{ form.userId }}</el-descriptions-item>
              <el-descriptions-item label="注册来源">
                <el-tag :type="sourceTagType(form.source)" size="small">{{ sourceText(form.source) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="微信OpenID">
                <span class="mono-text">{{ form.openid || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="微信UnionID">
                <span class="mono-text">{{ form.unionid || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="连续打卡">
                <span class="streak-days">🔥 {{ form.streakDays || 0 }} 天</span>
              </el-descriptions-item>
              <el-descriptions-item label="成就积分">
                <span class="achievement-points">⭐ {{ form.achievementPoints || 0 }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ form.createTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ form.updateTime || '-' }}</el-descriptions-item>
            </el-descriptions>
            <el-form-item label="备注" prop="remark" style="margin-top: 20px">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-tab-pane>
        </el-tabs>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, delUser, addUser, updateUser } from "@/api/weight/user"
import { listMember } from "@/api/weight/member"
import { getCircle } from "@/api/weight/circle"
import { getToken } from '@/utils/auth'
import { normalizeStoredAsset, normalizeUploadedAsset, resolveAssetUrl } from '@/utils/asset'
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "WeightUser",
  mixins: [weightAdminPageMixin],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      userList: [],
      title: "",
      open: false,
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      defaultSort: { prop: 'createTime', order: 'descending' },
      // 编辑表单相关
      activeTab: 'basic',
      uploadUrl: process.env.VUE_APP_BASE_API + '/common/upload',
      uploadHeaders: { Authorization: 'Bearer ' + getToken() },
      userCircles: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickname: null,
        gender: null,
        orderByColumn: 'createTime',
        isAsc: 'descending'
      },
      form: {},
      rules: {}
    }
  },
  created() {
    this.applyRouteQueryFilters(['userId'])
    this.getList().then(() => {
      this.syncRouteAction()
    })
  },
  watch: {
    '$route.query': {
      deep: true,
      handler() {
        this.syncRouteAction()
      }
    }
  },
  methods: {
    getList() {
      this.loading = true
      return listUser(this.queryParams).then(response => {
        this.userList = response.rows || []
        this.total = response.total
        this.loading = false
      })
    },
    genderText(gender) {
      const map = { '0': '未知', '1': '男', '2': '女' }
      return map[gender] || '未知'
    },
    resolveAvatar(avatar) {
      return avatar ? resolveAssetUrl(avatar) : this.defaultAvatar
    },
    genderTagType(gender) {
      const map = { '1': '', '2': 'danger' }
      return map[gender] || 'info'
    },
    bmiTagType(bmi) {
      if (!bmi) return 'info'
      if (bmi < 18.5) return 'warning'
      if (bmi < 24) return 'success'
      if (bmi < 28) return 'warning'
      return 'danger'
    },
    sourceText(source) {
      const map = { '0': '微信小程序', '1': 'iOS', '2': 'Android', '3': 'H5', '4': '后台' }
      return map[source] || '未知'
    },
    sourceTagType(source) {
      const map = { '0': 'success', '1': '', '2': '', '3': 'warning', '4': 'info' }
      return map[source] || 'info'
    },
    handleDetail(row) {
      this.openUserProfile(row.userId)
    },
    syncRouteAction() {
      const { userId, action } = this.$route.query || {}
      if (!userId || !action) return
      if (action === 'detail') {
        this.openUserProfile(userId)
      }
      if (action === 'edit') {
        this.handleUpdate({ userId })
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        userId: null,
        nickname: null,
        avatar: null,
        gender: null,
        phone: null,
        height: null,
        birthday: null,
        targetWeight: null,
        currentWeight: null,
        bmi: null,
        dailyCalorieTarget: 0,
        dailyWaterTarget: 8,
        circleFeatureEnabled: 1,
        syncMode: '0',
        mainCircleId: null,
        source: null,
        openid: null,
        unionid: null,
        streakDays: 0,
        achievementPoints: 0,
        remark: null
      }
      this.activeTab = 'basic'
      this.userCircles = []
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.queryParams.orderByColumn = this.defaultSort.prop
      this.queryParams.isAsc = this.defaultSort.order
      this.handleQuery()
    },
    handleSortChange(column) {
      this.queryParams.orderByColumn = column.prop || this.defaultSort.prop
      this.queryParams.isAsc = column.order || this.defaultSort.order
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.userId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleUpdate(row) {
      this.reset()
      const userId = row.userId || this.ids
      getUser(userId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户信息"
        // 加载用户已参与的圈子
        this.loadUserCircles(userId)
      })
    },
    // 加载用户参与的圈子列表（并获取圈子名称）
    async loadUserCircles(userId) {
      try {
        const res = await listMember({ userId, pageSize: 100 })
        const members = res.rows || []
        // 并行获取每个圈子的详细信息
        const circlesWithName = await Promise.all(
          members.map(async (m) => {
            try {
              const circleRes = await getCircle(m.circleId)
              return {
                ...m,
                circleName: circleRes.data?.name || `圈子 ${m.circleId}`
              }
            } catch {
              return { ...m, circleName: `圈子 ${m.circleId}` }
            }
          })
        )
        this.userCircles = circlesWithName
      } catch (e) {
        console.error('Failed to load user circles:', e)
        this.userCircles = []
      }
    },
    // 头像上传成功
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.form.avatar = normalizeUploadedAsset(res)
        this.$modal.msgSuccess('头像上传成功')
      } else {
        this.$modal.msgError(res.msg || '头像上传失败')
      }
    },
    // 头像上传前校验
    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) {
        this.$modal.msgError('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$modal.msgError('图片大小不能超过 2MB!')
        return false
      }
      return true
    },
    // BMI 文字标签
    bmiLabel(bmi) {
      if (!bmi) return ''
      if (bmi < 18.5) return '偏瘦'
      if (bmi < 24) return '正常'
      if (bmi < 28) return '偏胖'
      return '肥胖'
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const payload = {
            ...this.form,
            avatar: normalizeStoredAsset(this.form.avatar)
          }

          if (this.form.userId != null) {
            updateUser(payload).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addUser(payload).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const userIds = row.userId || this.ids
      this.$modal.confirm('是否确认删除该用户？').then(function() {
        return delUser(userIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('ruoyi-weight/user/export', { ...this.queryParams }, `user_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.weight-value {
  font-weight: bold;
  color: #409EFF;
}

.streak-days {
  color: #E6A23C;
  font-weight: bold;
}

.user-detail {
  padding: 0 20px;
}

.detail-card {
  margin-bottom: 15px;
}

.user-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-info h3 {
  margin: 0 0 5px 0;
}

.user-info p {
  margin: 0 0 5px 0;
  color: #909399;
  font-size: 12px;
}

/* 头像上传区域 */
.avatar-uploader {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-upload-btn {
  display: inline-block;
}

/* 表单提示 */
.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* BMI 标签 */
.bmi-label {
  margin-left: 8px;
  font-size: 13px;
  color: #606266;
}

/* 等距文本 */
.mono-text {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 12px;
  color: #606266;
  word-break: break-all;
}

/* 成就积分 */
.achievement-points {
  color: #409EFF;
  font-weight: bold;
}
</style>
