<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关注者用户ID" prop="followerId">
        <el-input
          v-model="queryParams.followerId"
          placeholder="请输入关注者用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="被关注者用户ID" prop="followingId">
        <el-input
          v-model="queryParams.followingId"
          placeholder="请输入被关注者用户ID"
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
          v-hasPermi="['ruoyi-weight:follow:add']"
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
          v-hasPermi="['ruoyi-weight:follow:edit']"
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
          v-hasPermi="['ruoyi-weight:follow:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:follow:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="followList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="关注者" align="center" width="140">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.followerId" />
        </template>
      </el-table-column>
      <el-table-column label="被关注者" align="center" width="140">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.followingId" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:follow:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:follow:remove']"
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

    <!-- 添加或修改用户关注关系对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <entity-id-field v-model="form.followerId" label="关注者" prop="followerId" entity-type="user" placeholder="请输入关注者用户ID" />
        <entity-id-field v-model="form.followingId" label="被关注者" prop="followingId" entity-type="user" placeholder="请输入被关注者用户ID" />
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFollow, getFollow, delFollow, addFollow, updateFollow } from "@/api/weight/follow"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Follow",
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['followerId', 'followingId'],
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
      // 用户关注关系表格数据
      followList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        followerId: null,
        followingId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        followerId: [
          { required: true, message: "关注者用户ID不能为空", trigger: "blur" }
        ],
        followingId: [
          { required: true, message: "被关注者用户ID不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.applyUserFallbackFilter()
    this.getList()
  },
  watch: {
    '$route.query.userId'() {
      if (this.applyUserFallbackFilter()) {
        this.queryParams.pageNum = 1
        this.getList()
      }
    }
  },
  methods: {
    applyUserFallbackFilter() {
      const routeQuery = this.$route.query || {}
      if (routeQuery.followerId || routeQuery.followingId) {
        if (this.queryParams.followerId === null && this.queryParams.followingId === null) {
          return false
        }
        return false
      }

      const fallbackUserId = routeQuery.userId || null
      if ((this.queryParams.followerId || null) === fallbackUserId) {
        return false
      }

      this.queryParams.followerId = fallbackUserId
      this.queryParams.followingId = null
      return true
    },
    /** 查询用户关注关系列表 */
    getList() {
      this.loading = true
      listFollow(this.queryParams).then(response => {
        this.followList = response.rows
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
        followerId: null,
        followingId: null,
        createTime: null
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
      this.title = "添加用户关注关系"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFollow(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改用户关注关系"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFollow(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addFollow(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除用户关注关系编号为"' + ids + '"的数据项？').then(function() {
        return delFollow(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/follow/export', {
        ...this.queryParams
      }, `follow_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
