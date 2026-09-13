<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="习惯名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入习惯名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="图标emoji" prop="icon">
        <el-input
          v-model="queryParams.icon"
          placeholder="请输入图标emoji"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="时段" prop="period">
        <el-input
          v-model="queryParams.period"
          placeholder="请输入时段"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="频率" prop="frequency">
        <el-input
          v-model="queryParams.frequency"
          placeholder="请输入频率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提醒时间" prop="reminderTime">
        <el-date-picker clearable
          v-model="queryParams.reminderTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提醒时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="是否启用" prop="isActive">
        <el-input
          v-model="queryParams.isActive"
          placeholder="请输入是否启用"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前连续天数" prop="currentStreak">
        <el-input
          v-model="queryParams.currentStreak"
          placeholder="请输入当前连续天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最长连续天数" prop="longestStreak">
        <el-input
          v-model="queryParams.longestStreak"
          placeholder="请输入最长连续天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总打卡次数" prop="totalCheckins">
        <el-input
          v-model="queryParams.totalCheckins"
          placeholder="请输入总打卡次数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ruoyi-weight:habit:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ruoyi-weight:habit:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ruoyi-weight:habit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:habit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="habitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="习惯名称" align="center" prop="name" />
      <el-table-column label="图标emoji" align="center" prop="icon" />
      <el-table-column label="时段" align="center" prop="period" />
      <el-table-column label="频率" align="center" prop="frequency" />
      <el-table-column label="提醒时间" align="center" prop="reminderTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.reminderTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" align="center" prop="isActive" />
      <el-table-column label="当前连续天数" align="center" prop="currentStreak" />
      <el-table-column label="最长连续天数" align="center" prop="longestStreak" />
      <el-table-column label="总打卡次数" align="center" prop="totalCheckins" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:habit:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:habit:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改习惯定义对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="习惯名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入习惯名称" />
        </el-form-item>
        <el-form-item label="图标emoji" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标emoji" />
        </el-form-item>
        <el-form-item label="时段" prop="period">
          <el-input v-model="form.period" placeholder="请输入时段" />
        </el-form-item>
        <el-form-item label="频率" prop="frequency">
          <el-input v-model="form.frequency" placeholder="请输入频率" />
        </el-form-item>
        <el-form-item label="提醒时间" prop="reminderTime">
          <el-date-picker clearable
            v-model="form.reminderTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择提醒时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否启用" prop="isActive">
          <el-input v-model="form.isActive" placeholder="请输入是否启用" />
        </el-form-item>
        <el-form-item label="当前连续天数" prop="currentStreak">
          <el-input v-model="form.currentStreak" placeholder="请输入当前连续天数" />
        </el-form-item>
        <el-form-item label="最长连续天数" prop="longestStreak">
          <el-input v-model="form.longestStreak" placeholder="请输入最长连续天数" />
        </el-form-item>
        <el-form-item label="总打卡次数" prop="totalCheckins">
          <el-input v-model="form.totalCheckins" placeholder="请输入总打卡次数" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listHabit, getHabit, delHabit, addHabit, updateHabit } from "@/api/ruoyi-weight/habit"

export default {
  name: "Habit",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 习惯定义表格数据
      habitList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        name: null,
        icon: null,
        period: null,
        frequency: null,
        reminderTime: null,
        isActive: null,
        currentStreak: null,
        longestStreak: null,
        totalCheckins: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "习惯名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询习惯定义列表 */
    getList() {
      this.loading = true
      listHabit(this.queryParams).then(response => {
        this.habitList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        userId: null,
        name: null,
        icon: null,
        period: null,
        frequency: null,
        reminderTime: null,
        isActive: null,
        currentStreak: null,
        longestStreak: null,
        totalCheckins: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加习惯定义"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getHabit(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改习惯定义"
      })
    },
    /** 提交按钮 */
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
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除习惯定义编号为"' + ids + '"的数据项？').then(function() {
        return delHabit(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/habit/export', {
        ...this.queryParams
      }, `habit_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
