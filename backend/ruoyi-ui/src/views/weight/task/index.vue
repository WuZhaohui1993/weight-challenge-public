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
      <el-form-item label="任务日期" prop="taskDate">
        <el-date-picker clearable
          v-model="queryParams.taskDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择任务日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="任务名称" prop="taskName">
        <el-input
          v-model="queryParams.taskName"
          placeholder="请输入任务名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="来源ID" prop="sourceId">
        <el-input
          v-model="queryParams.sourceId"
          placeholder="请输入来源ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标值" prop="targetValue">
        <el-input
          v-model="queryParams.targetValue"
          placeholder="请输入目标值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前值" prop="currentValue">
        <el-input
          v-model="queryParams.currentValue"
          placeholder="请输入当前值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否完成" prop="isCompleted">
        <el-input
          v-model="queryParams.isCompleted"
          placeholder="请输入是否完成"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="完成时间" prop="completedAt">
        <el-date-picker clearable
          v-model="queryParams.completedAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择完成时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="积分奖励" prop="pointsReward">
        <el-input
          v-model="queryParams.pointsReward"
          placeholder="请输入积分奖励"
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
          v-hasPermi="['ruoyi-weight:task:add']"
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
          v-hasPermi="['ruoyi-weight:task:edit']"
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
          v-hasPermi="['ruoyi-weight:task:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:task:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="用户" align="center" width="140">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="任务日期" align="center" prop="taskDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.taskDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="任务类型" align="center" prop="taskType" />
      <el-table-column label="任务名称" align="center" prop="taskName" />
      <el-table-column label="来源ID" align="center" prop="sourceId" />
      <el-table-column label="目标值" align="center" prop="targetValue" />
      <el-table-column label="当前值" align="center" prop="currentValue" />
      <el-table-column label="是否完成" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="toFlagTagType(scope.row.isCompleted)" size="small">
            {{ toFlagText(scope.row.isCompleted, '已完成', '未完成') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="完成时间" align="center" prop="completedAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.completedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="积分奖励" align="center" prop="pointsReward" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:task:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:task:remove']"
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

    <!-- 添加或修改每日任务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <entity-id-field v-model="form.userId" label="用户ID" prop="userId" entity-type="user" placeholder="请输入用户ID" />
        <el-form-item label="任务日期" prop="taskDate">
          <el-date-picker clearable
            v-model="form.taskDate"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择任务日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="来源ID" prop="sourceId">
          <el-input v-model="form.sourceId" placeholder="请输入来源ID" />
        </el-form-item>
        <el-form-item label="目标值" prop="targetValue">
          <el-input v-model="form.targetValue" placeholder="请输入目标值" />
        </el-form-item>
        <el-form-item label="当前值" prop="currentValue">
          <el-input v-model="form.currentValue" placeholder="请输入当前值" />
        </el-form-item>
        <el-form-item label="是否完成" prop="isCompleted">
          <el-select v-model="form.isCompleted" placeholder="请选择" style="width: 100%">
            <el-option label="已完成" value="1" />
            <el-option label="未完成" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="完成时间" prop="completedAt">
          <el-date-picker clearable
            v-model="form.completedAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择完成时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="积分奖励" prop="pointsReward">
          <el-input v-model="form.pointsReward" placeholder="请输入积分奖励" />
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
import { listTask, getTask, delTask, addTask, updateTask } from "@/api/weight/task"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Task",
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['userId'],
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
      // 每日任务表格数据
      taskList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        taskDate: null,
        taskType: null,
        taskName: null,
        sourceId: null,
        targetValue: null,
        currentValue: null,
        isCompleted: null,
        completedAt: null,
        pointsReward: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        taskDate: [
          { required: true, message: "任务日期不能为空", trigger: "blur" }
        ],
        taskType: [
          { required: true, message: "任务类型不能为空", trigger: "change" }
        ],
        taskName: [
          { required: true, message: "任务名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询每日任务列表 */
    getList() {
      this.loading = true
      listTask(this.queryParams).then(response => {
        this.taskList = response.rows
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
        taskDate: null,
        taskType: null,
        taskName: null,
        sourceId: null,
        targetValue: null,
        currentValue: null,
        isCompleted: null,
        completedAt: null,
        pointsReward: null,
        createTime: null,
        updateTime: null
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
      this.title = "添加每日任务"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getTask(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改每日任务"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTask(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addTask(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除每日任务编号为"' + ids + '"的数据项？').then(function() {
        return delTask(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/task/export', {
        ...this.queryParams
      }, `task_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
