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
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-input
          v-model="queryParams.role"
          placeholder="请输入角色"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="加入时间" prop="joinedAt">
        <el-date-picker clearable
          v-model="queryParams.joinedAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择加入时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="是否置顶" prop="isPinned">
        <el-input
          v-model="queryParams.isPinned"
          placeholder="请输入是否置顶"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="连续打卡天数" prop="streakDays">
        <el-input
          v-model="queryParams.streakDays"
          placeholder="请输入连续打卡天数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="累计打卡次数" prop="totalCheckins">
        <el-input
          v-model="queryParams.totalCheckins"
          placeholder="请输入累计打卡次数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="体重变化量" prop="weightChange">
        <el-input
          v-model="queryParams.weightChange"
          placeholder="请输入体重变化量"
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
          v-hasPermi="['ruoyi-weight:member:add']"
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
          v-hasPermi="['ruoyi-weight:member:edit']"
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
          v-hasPermi="['ruoyi-weight:member:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:member:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="memberList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" width="60" />
      <el-table-column label="圈子" align="center" width="120">
        <template slot-scope="scope">
          <circle-link :circle-id="scope.row.circleId" />
        </template>
      </el-table-column>
      <el-table-column label="用户" align="center" width="120">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="角色" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.role === '0' ? 'warning' : 'info'" size="small">
            {{ toStatusText(scope.row.role, { '0': '管理员', '1': '成员' }) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="加入时间" align="center" prop="joinedAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.joinedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否置顶" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="toFlagTagType(scope.row.isPinned, 'warning', 'info')" size="small">
            {{ toFlagText(scope.row.isPinned, '已置顶', '普通') }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="连续打卡天数" align="center" prop="streakDays" />
      <el-table-column label="押金状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.depositStatus === 'paid' ? 'success' : scope.row.depositStatus === 'refunded' ? 'info' : 'warning'" size="small">
            {{ toStatusText(scope.row.depositStatus, { paid: '已支付', pending: '待退款', refunded: '已退回', deducted: '已扣除' }) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="累计打卡次数" align="center" prop="totalCheckins" />
      <el-table-column label="体重变化量" align="center" prop="weightChange" />
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'" size="small">
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
            v-hasPermi="['ruoyi-weight:member:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:member:remove']"
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

    <!-- 添加或修改圈子成员对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <entity-id-field v-model="form.circleId" label="圈子ID" prop="circleId" entity-type="circle" placeholder="请输入圈子ID" />
        <entity-id-field v-model="form.userId" label="用户ID" prop="userId" entity-type="user" placeholder="请输入用户ID" />
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择" style="width: 100%">
            <el-option label="管理员" value="0" />
            <el-option label="成员" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="加入时间" prop="joinedAt">
          <el-date-picker clearable
            v-model="form.joinedAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择加入时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否置顶" prop="isPinned">
          <el-select v-model="form.isPinned" placeholder="请选择" style="width: 100%">
            <el-option label="置顶" value="1" />
            <el-option label="普通" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="连续打卡天数" prop="streakDays">
          <el-input v-model="form.streakDays" placeholder="请输入连续打卡天数" />
        </el-form-item>
        <el-form-item label="累计打卡次数" prop="totalCheckins">
          <el-input v-model="form.totalCheckins" placeholder="请输入累计打卡次数" />
        </el-form-item>
        <el-form-item label="体重变化量" prop="weightChange">
          <el-input v-model="form.weightChange" placeholder="请输入体重变化量" />
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
import { listMember, getMember, delMember, addMember, updateMember } from "@/api/weight/member"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Member",
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['circleId', 'userId'],
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
      // 圈子成员表格数据
      memberList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        circleId: null,
        userId: null,
        role: null,
        joinedAt: null,
        isPinned: null,
        streakDays: null,
        depositStatus: null,
        totalCheckins: null,
        weightChange: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        circleId: [
          { required: true, message: "圈子ID不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询圈子成员列表 */
    getList() {
      this.loading = true
      listMember(this.queryParams).then(response => {
        this.memberList = response.rows
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
        userId: null,
        role: null,
        joinedAt: null,
        isPinned: null,
        streakDays: null,
        depositStatus: null,
        totalCheckins: null,
        weightChange: null,
        status: null,
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
      this.title = "添加圈子成员"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getMember(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改圈子成员"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMember(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addMember(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除圈子成员编号为"' + ids + '"的数据项？').then(function() {
        return delMember(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/member/export', {
        ...this.queryParams
      }, `member_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
