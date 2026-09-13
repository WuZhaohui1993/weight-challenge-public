<template>
  <div class="app-container">
    <!-- 精简的搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="圈子名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入圈子名称" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['ruoyi-weight:circle:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['ruoyi-weight:circle:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:circle:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 优化的圈子列表 -->
    <el-table v-loading="loading" :data="circleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="图标" align="center" width="60">
        <template slot-scope="scope">
          <span class="circle-icon">{{ scope.row.icon || '🎯' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="圈子名称" align="center" prop="name" min-width="120" />
      <el-table-column label="创建者" align="center" width="100">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.creatorId" />
        </template>
      </el-table-column>
      <el-table-column label="成员数" align="center" width="80">
        <template slot-scope="scope">
          <el-tag type="primary" size="small" @click="showMembers(scope.row)" style="cursor: pointer">
            👥 {{ scope.row.memberCount || 0 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="押金" align="center" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.depositRequired > 0" class="deposit-amount">
            ¥{{ scope.row.depositRequired }}
          </span>
          <el-tag v-else type="info" size="small">免押金</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="持续天数" align="center" prop="durationDays" width="90">
        <template slot-scope="scope">
          {{ scope.row.durationDays || '-' }} 天
        </template>
      </el-table-column>
      <el-table-column label="时间范围" align="center" min-width="180">
        <template slot-scope="scope">
          <span v-if="scope.row.startDate">
            {{ scope.row.startDate.substring(0, 10) }} ~ {{ scope.row.endDate ? scope.row.endDate.substring(0, 10) : '待定' }}
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" width="70">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === '0' ? 'success' : 'warning'" size="small">
            {{ scope.row.type === '0' ? '公开' : '私密' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="70">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="惩罚规则" align="center" width="90">
        <template slot-scope="scope">
          {{ penaltyRuleText(scope.row.penaltyRule) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="showProfile(scope.row)">360°</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['ruoyi-weight:circle:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:circle:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加或修改圈子对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-tabs v-model="formTab">
          <!-- Tab 1: 基础信息 -->
          <el-tab-pane label="基础信息" name="basic">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="圈子名称" prop="name">
                  <el-input v-model="form.name" placeholder="请输入圈子名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="图标" prop="icon">
                  <emoji-picker v-model="form.icon" placeholder="选择圈子图标" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="类型" prop="type">
                  <el-radio-group v-model="form.type">
                    <el-radio label="0">公开</el-radio>
                    <el-radio label="1">私密</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status">
                    <el-radio label="0">正常</el-radio>
                    <el-radio label="1">停用</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="分类" prop="categoryId">
                  <el-select v-model="form.categoryId" placeholder="请选择分类" clearable style="width: 100%">
                    <el-option
                      v-for="cat in categoryList"
                      :key="cat.id"
                      :label="cat.name"
                      :value="cat.id"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="创建者" prop="creatorId">
                  <user-link v-if="form.creatorId" :user-id="form.creatorId" />
                  <span v-else class="text-muted">系统创建</span>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="简介" prop="description">
              <el-input v-model="form.description" type="textarea" placeholder="请输入圈子简介" :rows="3" />
            </el-form-item>
            <el-form-item label="封面图" prop="coverUrl">
              <el-input v-model="form.coverUrl" placeholder="请输入封面图URL（可选）">
                <template slot="prepend">URL</template>
              </el-input>
            </el-form-item>
          </el-tab-pane>

          <!-- Tab 2: 规则设置 -->
          <el-tab-pane label="规则设置" name="rules">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="押金金额" prop="depositRequired">
                  <el-input-number v-model="form.depositRequired" :min="0" :precision="2" :step="10" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="持续天数" prop="durationDays">
                  <el-input-number v-model="form.durationDays" :min="0" :max="365" style="width: 100%" />
                  <div class="form-hint">0 表示永久</div>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="开始日期" prop="startDate">
                  <el-date-picker clearable v-model="form.startDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="结束日期" prop="endDate">
                  <el-date-picker clearable v-model="form.endDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width: 100%" />
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item label="惩罚规则" prop="penaltyRule">
              <el-radio-group v-model="form.penaltyRule">
                <el-radio-button label="0">无惩罚</el-radio-button>
                <el-radio-button label="1">提醒</el-radio-button>
                <el-radio-button label="2">扣押金</el-radio-button>
                <el-radio-button label="3">踢出</el-radio-button>
              </el-radio-group>
            </el-form-item>
          </el-tab-pane>

          <!-- Tab 3: 统计信息（只读） -->
          <el-tab-pane label="统计信息" name="stats" v-if="form.id">
            <el-descriptions :column="2" border size="small">
              <el-descriptions-item label="圈子ID">{{ form.id }}</el-descriptions-item>
              <el-descriptions-item label="成员数">{{ form.memberCount || 0 }} 人</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ form.createTime || '-' }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ form.updateTime || '-' }}</el-descriptions-item>
            </el-descriptions>
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
import { listCircle, getCircle, delCircle, addCircle, updateCircle } from "@/api/weight/circle"
import { listCategory } from "@/api/weight/category"
import EmojiPicker from "@/components/EmojiPicker"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "WeightCircle",
  dicts: ['weight_penalty_rule', 'weight_circle_type'],
  components: { EmojiPicker },
  mixins: [weightAdminPageMixin],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      circleList: [],
      title: "",
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        status: null
      },
      form: {},
      formTab: 'basic',
      categoryList: [],
      rules: {
        name: [{ required: true, message: "圈子名称不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList().then(() => {
      this.syncRouteAction()
    })
    this.loadCategories()
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
      return listCircle(this.queryParams).then(response => {
        this.circleList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    loadCategories() {
      listCategory({ pageSize: 100 }).then(response => {
        this.categoryList = response.rows || []
      }).catch(() => {})
    },
    statusText(status) {
      const map = { '0': '正常', '1': '停用' }
      return map[status] || '未知'
    },
    statusTagType(status) {
      const map = { '0': 'success', '1': 'danger' }
      return map[status] || 'info'
    },
    penaltyRuleText(rule) {
      const map = { '0': '无', '1': '提醒', '2': '扣押金', '3': '踢出' }
      return map[rule] || '-'
    },
    showProfile(row) {
      this.openCircleProfile(row.id)
    },
    syncRouteAction() {
      const { circleId, action } = this.$route.query || {}
      if (!circleId || !action) return
      if (action === 'detail') {
        this.openCircleProfile(circleId)
      }
      if (action === 'edit') {
        this.handleUpdate({ id: circleId })
      }
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        name: null,
        icon: null,
        type: '0',
        status: '0',
        description: null,
        depositRequired: 0,
        durationDays: 21,
        startDate: null,
        endDate: null,
        penaltyRule: null
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增圈子"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCircle(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改圈子"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCircle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCircle(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除该圈子？').then(function () {
        return delCircle(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => { })
    }
  }
}
</script>

<style scoped>
.circle-icon {
  font-size: 24px;
}

.circle-icon-large {
  font-size: 48px;
  margin-right: 15px;
}

.deposit-amount {
  color: #E6A23C;
  font-weight: bold;
}

.member-drawer {
  padding: 0 20px;
}

.circle-info-card {
  margin-bottom: 15px;
}

.circle-header {
  display: flex;
  align-items: flex-start;
}

.circle-info h3 {
  margin: 0 0 8px 0;
}

.circle-info p {
  color: #909399;
  margin: 0 0 8px 0;
}

.member-list-card {
  margin-bottom: 15px;
}
</style>
