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
        <el-select v-model="queryParams.period" placeholder="请选择周期" clearable style="width: 160px">
          <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="指标编码" prop="metricCode">
        <el-select v-model="queryParams.metricCode" placeholder="请选择指标" clearable style="width: 220px">
          <el-option v-for="item in metricOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="校验方式" prop="verificationType">
        <el-select v-model="queryParams.verificationType" placeholder="请选择校验方式" clearable style="width: 160px">
          <el-option v-for="item in verificationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
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
          type="info"
          plain
          icon="el-icon-collection-tag"
          size="mini"
          @click="handleAddTemplate"
          v-hasPermi="['ruoyi-weight:goal:add']"
        >新增模板</el-button>
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
      <el-table-column label="圈子" align="center" width="140">
        <template slot-scope="scope">
          <el-tag v-if="Number(scope.row.circleId) === 0" size="small" type="warning">标准模板</el-tag>
          <circle-link v-else :circle-id="scope.row.circleId" />
        </template>
      </el-table-column>
      <el-table-column label="目标类型" align="center" prop="goalType" />
      <el-table-column label="目标名称" align="center" prop="goalName" />
      <el-table-column label="目标描述" align="center" prop="description" />
      <el-table-column label="指标编码" align="center" prop="metricCode" min-width="160" />
      <el-table-column label="目标值" align="center" prop="targetValue" />
      <el-table-column label="目标单位" align="center" prop="targetUnit" />
      <el-table-column label="周期" align="center" prop="period" />
      <el-table-column label="验证方式" align="center" prop="verificationType" />
      <el-table-column label="未完成惩罚描述" align="center" prop="penaltyForFailure" />
      <el-table-column label="是否必选" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="toFlagTagType(scope.row.isRequired)" size="small">
            {{ toFlagText(scope.row.isRequired, '必选', '可选') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'" size="small">
            {{ toStatusText(scope.row.status, { '0': '正常', '1': '停用' }) }}
          </el-tag>
        </template>
      </el-table-column>
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
        <entity-id-field v-model="form.circleId" label="圈子ID" prop="circleId" entity-type="circle" placeholder="请输入圈子ID，0 表示标准模板" />
        <div class="form-tip">`circleId = 0` 表示标准目标模板，创建圈子时可直接选择。</div>
        <el-form-item label="目标类型" prop="goalType">
          <el-select v-model="form.goalType" placeholder="请选择目标类型" style="width: 100%">
            <el-option v-for="item in goalTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="指标编码" prop="metricCode">
          <el-select v-model="form.metricCode" placeholder="请选择指标编码" style="width: 100%">
            <el-option v-for="item in metricOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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
          <el-select v-model="form.period" placeholder="请选择周期" style="width: 100%">
            <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="校验方式" prop="verificationType">
          <el-select v-model="form.verificationType" placeholder="请选择校验方式" style="width: 100%">
            <el-option v-for="item in verificationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="未完成惩罚描述" prop="penaltyForFailure">
          <el-input v-model="form.penaltyForFailure" placeholder="请输入未完成惩罚描述" />
        </el-form-item>
        <el-form-item label="是否必选" prop="isRequired">
          <el-select v-model="form.isRequired" placeholder="请选择" style="width: 100%">
            <el-option label="必选" value="1" />
            <el-option label="可选" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input v-model="form.sortOrder" placeholder="请输入排序" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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
import { listGoal, getGoal, delGoal, addGoal, updateGoal } from "@/api/weight/goal"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Goal",
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['circleId'],
  data() {
    return {
      goalTypeOptions: [
        { label: '减重', value: 'weight_loss' },
        { label: '打卡', value: 'checkin' },
        { label: '运动', value: 'exercise' },
        { label: '饮食', value: 'diet' },
        { label: '习惯', value: 'habit' }
      ],
      metricOptions: [
        { label: '体重记录次数', value: 'weight_record_count' },
        { label: '减重公斤数', value: 'weight_loss_kg' },
        { label: '运动分钟数', value: 'exercise_minutes' },
        { label: '运动次数', value: 'exercise_count' },
        { label: '饮食记录次数', value: 'food_record_count' },
        { label: '习惯打卡次数', value: 'habit_checkin_count' },
        { label: '圈内打卡次数', value: 'circle_checkin_count' },
        { label: '手动圈子打卡', value: 'manual_circle_checkin' }
      ],
      periodOptions: [
        { label: '每日', value: '0' },
        { label: '每周', value: '1' },
        { label: '每月', value: '2' },
        { label: '总计', value: '3' }
      ],
      verificationTypeOptions: [
        { label: '自动统计', value: '0' },
        { label: '手动完成', value: '1' }
      ],
      statusOptions: [
        { label: '正常', value: '0' },
        { label: '停用', value: '1' }
      ],
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
        metricCode: null,
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
        metricCode: [
          { required: true, message: "指标编码不能为空", trigger: "change" }
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
        metricCode: null,
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
      this.form.status = '0'
      this.form.verificationType = '0'
      this.form.period = '0'
      this.open = true
      this.title = "添加圈子目标"
    },
    handleAddTemplate() {
      this.handleAdd()
      this.form.circleId = 0
      this.title = "添加标准目标模板"
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

<style scoped>
.form-tip {
  margin: -6px 0 14px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}
</style>
