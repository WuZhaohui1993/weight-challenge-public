<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="圈子ID" prop="circleId">
        <el-input
          v-model="queryParams.circleId"
          placeholder="请输入圈子ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目标名称" prop="goalName">
        <el-input
          v-model="queryParams.goalName"
          placeholder="请输入目标名称"
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
      <el-form-item label="目标单位" prop="targetUnit">
        <el-input
          v-model="queryParams.targetUnit"
          placeholder="请输入目标单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="周期" prop="period">
        <el-input
          v-model="queryParams.period"
          placeholder="请输入周期"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="未完成惩罚描述" prop="penaltyForFailure">
        <el-input
          v-model="queryParams.penaltyForFailure"
          placeholder="请输入未完成惩罚描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否必选" prop="isRequired">
        <el-input
          v-model="queryParams.isRequired"
          placeholder="请输入是否必选"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="排序" prop="sortOrder">
        <el-input
          v-model="queryParams.sortOrder"
          placeholder="请输入排序"
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
          v-hasPermi="['ruoyi-weight:goal:add']"
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
          v-hasPermi="['ruoyi-weight:goal:edit']"
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
          v-hasPermi="['ruoyi-weight:goal:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:goal:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="goalList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="圈子ID" align="center" prop="circleId" />
      <el-table-column label="目标类型" align="center" prop="goalType" />
      <el-table-column label="目标名称" align="center" prop="goalName" />
      <el-table-column label="目标描述" align="center" prop="description" />
      <el-table-column label="目标值" align="center" prop="targetValue" />
      <el-table-column label="目标单位" align="center" prop="targetUnit" />
      <el-table-column label="周期" align="center" prop="period" />
      <el-table-column label="验证方式" align="center" prop="verificationType" />
      <el-table-column label="未完成惩罚描述" align="center" prop="penaltyForFailure" />
      <el-table-column label="是否必选" align="center" prop="isRequired" />
      <el-table-column label="排序" align="center" prop="sortOrder" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:goal:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:goal:remove']"
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

    <!-- 添加或修改圈子目标对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="圈子ID" prop="circleId">
          <el-input v-model="form.circleId" placeholder="请输入圈子ID" />
        </el-form-item>
        <el-form-item label="目标名称" prop="goalName">
          <el-input v-model="form.goalName" placeholder="请输入目标名称" />
        </el-form-item>
        <el-form-item label="目标描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="目标值" prop="targetValue">
          <el-input v-model="form.targetValue" placeholder="请输入目标值" />
        </el-form-item>
        <el-form-item label="目标单位" prop="targetUnit">
          <el-input v-model="form.targetUnit" placeholder="请输入目标单位" />
        </el-form-item>
        <el-form-item label="周期" prop="period">
          <el-input v-model="form.period" placeholder="请输入周期" />
        </el-form-item>
        <el-form-item label="未完成惩罚描述" prop="penaltyForFailure">
          <el-input v-model="form.penaltyForFailure" placeholder="请输入未完成惩罚描述" />
        </el-form-item>
        <el-form-item label="是否必选" prop="isRequired">
          <el-input v-model="form.isRequired" placeholder="请输入是否必选" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input v-model="form.sortOrder" placeholder="请输入排序" />
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
import { listGoal, getGoal, delGoal, addGoal, updateGoal } from "@/api/ruoyi-weight/goal"

export default {
  name: "Goal",
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
      // 圈子目标表格数据
      goalList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        circleId: null,
        goalType: null,
        goalName: null,
        description: null,
        targetValue: null,
        targetUnit: null,
        period: null,
        verificationType: null,
        penaltyForFailure: null,
        isRequired: null,
        sortOrder: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        circleId: [
          { required: true, message: "圈子ID不能为空", trigger: "blur" }
        ],
        goalType: [
          { required: true, message: "目标类型不能为空", trigger: "change" }
        ],
        goalName: [
          { required: true, message: "目标名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询圈子目标列表 */
    getList() {
      this.loading = true
      listGoal(this.queryParams).then(response => {
        this.goalList = response.rows
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
        circleId: null,
        goalType: null,
        goalName: null,
        description: null,
        targetValue: null,
        targetUnit: null,
        period: null,
        verificationType: null,
        penaltyForFailure: null,
        isRequired: null,
        sortOrder: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
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
      this.title = "添加圈子目标"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getGoal(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改圈子目标"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateGoal(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addGoal(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除圈子目标编号为"' + ids + '"的数据项？').then(function() {
        return delGoal(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/goal/export', {
        ...this.queryParams
      }, `goal_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
