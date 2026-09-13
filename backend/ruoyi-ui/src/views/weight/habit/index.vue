<template>
  <div class="app-container">
    <!-- 精简的搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable style="width: 150px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="习惯名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="请输入习惯名称" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="isActive">
        <el-select v-model="queryParams.isActive" placeholder="请选择" clearable style="width: 100px">
          <el-option label="启用" value="1" />
          <el-option label="停用" value="0" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['ruoyi-weight:habit:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:habit:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 优化的习惯列表 -->
    <el-table v-loading="loading" :data="habitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="图标" align="center" width="60">
        <template slot-scope="scope">
          <span class="habit-icon">{{ scope.row.icon || '✅' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="习惯名称" align="center" prop="name" min-width="120" />
      <el-table-column label="用户" align="center" width="100">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="时段" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="periodTagType(scope.row.period)" size="small">{{ periodText(scope.row.period) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="频率" align="center" width="80">
        <template slot-scope="scope">
          <span>{{ frequencyText(scope.row.frequency) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="连续打卡" align="center" width="100">
        <template slot-scope="scope">
          <span class="streak-current">🔥 {{ scope.row.currentStreak || 0 }}天</span>
        </template>
      </el-table-column>
      <el-table-column label="最长连续" align="center" width="90">
        <template slot-scope="scope">
          <span>🏆 {{ scope.row.longestStreak || 0 }}天</span>
        </template>
      </el-table-column>
      <el-table-column label="总打卡" align="center" width="80">
        <template slot-scope="scope">
          <el-tag type="success" size="small">{{ scope.row.totalCheckins || 0 }}次</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="70">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.isActive" active-value="1" inactive-value="0" @change="handleStatusChange(scope.row)" disabled />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-time" @click="showCheckins(scope.row)">记录</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['ruoyi-weight:habit:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:habit:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 打卡记录抽屉 -->
    <el-drawer :title="checkinDrawerTitle" :visible.sync="checkinDrawerVisible" size="45%" direction="rtl">
      <div class="checkin-drawer">
        <!-- 习惯信息卡片 -->
        <el-card v-if="currentHabit" class="habit-info-card">
          <div class="habit-header">
            <span class="habit-icon-large">{{ currentHabit.icon || '✅' }}</span>
            <div class="habit-info">
              <h3>{{ currentHabit.name }}</h3>
              <p>
                <span class="muted-text">所属用户：</span>
                <user-link :user-id="currentHabit.userId" />
              </p>
              <el-tag type="success" size="small">🔥 连续 {{ currentHabit.currentStreak || 0 }} 天</el-tag>
              <el-tag type="warning" size="small">🏆 最长 {{ currentHabit.longestStreak || 0 }} 天</el-tag>
            </div>
          </div>
        </el-card>

        <!-- 打卡记录列表 -->
        <el-card class="checkin-list-card">
          <div slot="header">⏰ 打卡记录</div>
          <el-table :data="checkinList" v-loading="checkinLoading" size="small">
            <el-table-column label="打卡日期" align="center" prop="checkinDate" min-width="100" />
            <el-table-column label="打卡时间" align="center" prop="checkinTime" width="100" />
            <el-table-column label="状态" align="center" width="80">
              <template slot-scope="scope">
                <el-tag type="success" size="small">✓ 已完成</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
          </el-table>
          <pagination v-show="checkinTotal>0" :total="checkinTotal" :page.sync="checkinQueryParams.pageNum" :limit.sync="checkinQueryParams.pageSize" @pagination="getCheckinList" />
        </el-card>
      </div>
    </el-drawer>

    <!-- 添加或修改习惯对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <entity-id-field v-model="form.userId" label="用户ID" prop="userId" entity-type="user" placeholder="请输入用户ID" />
        <el-form-item label="习惯名称" prop="name">
          <el-input v-model="form.name" placeholder="例如：每日运动30分钟" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <emoji-picker v-model="form.icon" placeholder="选择习惯图标" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="时段" prop="period">
              <el-select v-model="form.period" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.weight_habit_period"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="频率" prop="frequency">
              <el-select v-model="form.frequency" placeholder="请选择" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.weight_habit_freq"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="提醒时间" prop="reminderTime">
          <el-time-picker v-model="form.reminderTime" value-format="HH:mm" placeholder="选择提醒时间" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="习惯描述或备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHabit, getHabit, delHabit, addHabit, updateHabit } from "@/api/weight/habit"
import { listCheckin } from "@/api/weight/checkin"
import EmojiPicker from "@/components/EmojiPicker"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Habit",
  dicts: ['weight_habit_period', 'weight_habit_freq'],
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['userId'],
  components: { EmojiPicker },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      habitList: [],
      title: "",
      open: false,
      // 打卡记录抽屉
      checkinDrawerVisible: false,
      checkinDrawerTitle: "",
      currentHabit: null,
      checkinList: [],
      checkinTotal: 0,
      checkinLoading: false,
      checkinQueryParams: { pageNum: 1, pageSize: 10, habitId: null },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        name: null,
        isActive: null
      },
      form: {},
      rules: {
        userId: [{ required: true, message: "用户ID不能为空", trigger: "blur" }],
        name: [{ required: true, message: "习惯名称不能为空", trigger: "blur" }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listHabit(this.queryParams).then(response => {
        this.habitList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    periodText(period) {
      const map = { 'morning': '早晨', 'noon': '中午', 'evening': '晚上', 'all_day': '全天' }
      return map[period] || period || '全天'
    },
    periodTagType(period) {
      const map = { 'morning': 'warning', 'noon': '', 'evening': 'info', 'all_day': 'success' }
      return map[period] || ''
    },
    frequencyText(frequency) {
      const map = { 'daily': '每天', 'weekly': '每周', 'weekday': '工作日' }
      return map[frequency] || frequency || '每天'
    },
    showCheckins(row) {
      this.currentHabit = row
      this.checkinDrawerTitle = `${row.name} - 打卡记录`
      this.checkinQueryParams.habitId = row.id
      this.checkinQueryParams.pageNum = 1
      this.checkinDrawerVisible = true
      this.getCheckinList()
    },
    getCheckinList() {
      this.checkinLoading = true
      listCheckin(this.checkinQueryParams).then(response => {
        this.checkinList = response.rows || []
        this.checkinTotal = response.total || 0
        this.checkinLoading = false
      }).catch(() => { this.checkinLoading = false })
    },
    handleStatusChange(row) {
      updateHabit(row).then(() => {
        this.$modal.msgSuccess("状态已更新")
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        userId: null,
        name: null,
        icon: null,
        period: 'all_day',
        frequency: 'daily',
        reminderTime: null,
        isActive: '1',
        remark: null
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
      this.title = "新增习惯"
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getHabit(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改习惯"
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateHabit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addHabit(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除该习惯？').then(function () {
        return delHabit(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => { })
    }
  }
}
</script>

<style scoped>
.habit-icon {
  font-size: 24px;
}

.habit-icon-large {
  font-size: 48px;
  margin-right: 15px;
}

.streak-current {
  color: #E6A23C;
  font-weight: bold;
}

.checkin-drawer {
  padding: 0 20px;
}

.habit-info-card {
  margin-bottom: 15px;
}

.habit-header {
  display: flex;
  align-items: flex-start;
}

.habit-info h3 {
  margin: 0 0 8px 0;
}

.habit-info p {
  color: #909399;
  margin: 0 0 8px 0;
}

.muted-text {
  color: #909399;
}

.checkin-list-card {
  margin-bottom: 15px;
}
</style>
