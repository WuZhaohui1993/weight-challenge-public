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
      <el-form-item label="发布者用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入发布者用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关联的原始记录ID" prop="sourceRecordId">
        <el-input
          v-model="queryParams.sourceRecordId"
          placeholder="请输入关联的原始记录ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="点赞数" prop="likesCount">
        <el-input
          v-model="queryParams.likesCount"
          placeholder="请输入点赞数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评论数" prop="commentsCount">
        <el-input
          v-model="queryParams.commentsCount"
          placeholder="请输入评论数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否精选" prop="isFeatured">
        <el-input
          v-model="queryParams.isFeatured"
          placeholder="请输入是否精选"
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
          v-hasPermi="['ruoyi-weight:feed:add']"
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
          v-hasPermi="['ruoyi-weight:feed:edit']"
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
          v-hasPermi="['ruoyi-weight:feed:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:feed:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="feedList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="圈子ID" align="center" prop="circleId" />
      <el-table-column label="发布者用户ID" align="center" prop="userId" />
      <el-table-column label="动态类型" align="center" prop="feedType" />
      <el-table-column label="动态文本内容" align="center" prop="content" />
      <el-table-column label="图片URL数组(JSON)" align="center" prop="images" />
      <el-table-column label="关联的原始记录ID" align="center" prop="sourceRecordId" />
      <el-table-column label="关联记录类型" align="center" prop="sourceRecordType" />
      <el-table-column label="点赞数" align="center" prop="likesCount" />
      <el-table-column label="评论数" align="center" prop="commentsCount" />
      <el-table-column label="是否精选" align="center" prop="isFeatured" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:feed:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:feed:remove']"
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

    <!-- 添加或修改圈子动态对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="圈子ID" prop="circleId">
          <el-input v-model="form.circleId" placeholder="请输入圈子ID" />
        </el-form-item>
        <el-form-item label="发布者用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入发布者用户ID" />
        </el-form-item>
        <el-form-item label="动态文本内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="图片URL数组(JSON)" prop="images">
          <el-input v-model="form.images" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="关联的原始记录ID" prop="sourceRecordId">
          <el-input v-model="form.sourceRecordId" placeholder="请输入关联的原始记录ID" />
        </el-form-item>
        <el-form-item label="点赞数" prop="likesCount">
          <el-input v-model="form.likesCount" placeholder="请输入点赞数" />
        </el-form-item>
        <el-form-item label="评论数" prop="commentsCount">
          <el-input v-model="form.commentsCount" placeholder="请输入评论数" />
        </el-form-item>
        <el-form-item label="是否精选" prop="isFeatured">
          <el-input v-model="form.isFeatured" placeholder="请输入是否精选" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
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
import { listFeed, getFeed, delFeed, addFeed, updateFeed } from "@/api/ruoyi-weight/feed"

export default {
  name: "Feed",
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
      // 圈子动态表格数据
      feedList: [],
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
        feedType: null,
        content: null,
        images: null,
        sourceRecordId: null,
        sourceRecordType: null,
        likesCount: null,
        commentsCount: null,
        isFeatured: null,
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
          { required: true, message: "发布者用户ID不能为空", trigger: "blur" }
        ],
        feedType: [
          { required: true, message: "动态类型不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询圈子动态列表 */
    getList() {
      this.loading = true
      listFeed(this.queryParams).then(response => {
        this.feedList = response.rows
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
        feedType: null,
        content: null,
        images: null,
        sourceRecordId: null,
        sourceRecordType: null,
        likesCount: null,
        commentsCount: null,
        isFeatured: null,
        status: null,
        delFlag: null,
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
      this.title = "添加圈子动态"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFeed(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改圈子动态"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFeed(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addFeed(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除圈子动态编号为"' + ids + '"的数据项？').then(function() {
        return delFeed(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/feed/export', {
        ...this.queryParams
      }, `feed_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
