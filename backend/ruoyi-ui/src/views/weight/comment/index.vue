<template>
  <div class="app-container comment-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="用户ID" clearable style="width: 120px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="动态" prop="feedId">
        <el-input v-model="queryParams.feedId" placeholder="动态ID" clearable style="width: 120px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['ruoyi-weight:comment:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['ruoyi-weight:comment:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:comment:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['ruoyi-weight:comment:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="动态" align="center" width="90">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openFeed(scope.row.feedId)">#{{ scope.row.feedId }}</el-button>
        </template>
      </el-table-column>
      <el-table-column label="评论者" align="center" width="130">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="评论内容" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ stripHtml(scope.row.content) || '[图片评论]' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center" width="150">
        <template slot-scope="scope">
          <comment-images :images="getImageUrls(scope.row.images)" />
        </template>
      </el-table-column>
      <el-table-column label="回复对象" align="center" width="130">
        <template slot-scope="scope">
          <user-link v-if="scope.row.replyToUserId" :user-id="scope.row.replyToUserId" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="点赞" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.likesCount > 5 ? 'danger' : scope.row.likesCount > 0 ? 'warning' : 'info'" size="small">
            {{ scope.row.likesCount || 0 }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">
            {{ toStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间" align="center" prop="createTime" width="155" />
      <el-table-column label="操作" align="center" width="190" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleUpdate(scope.row)">查看</el-button>
          <el-dropdown size="mini" @command="status => updateCommentStatus(scope.row, status)" v-hasPermi="['ruoyi-weight:comment:edit']">
            <el-button type="text" size="mini">状态<i class="el-icon-arrow-down el-icon--right"></i></el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="0">恢复正常</el-dropdown-item>
              <el-dropdown-item command="1">标为待审</el-dropdown-item>
              <el-dropdown-item command="2">屏蔽</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <el-button size="mini" type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:comment:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="动态ID" prop="feedId">
              <el-input v-model="form.feedId" placeholder="请输入动态ID" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <entity-id-field v-model="form.userId" label="评论者" prop="userId" entity-type="user" placeholder="请输入评论者用户ID" />
          </el-col>
        </el-row>
        <el-form-item label="评论内容">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="图片(JSON)" prop="images">
          <el-input v-model="form.images" type="textarea" :rows="3" placeholder="评论图片 URL JSON 数组" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="回复评论ID" prop="replyToCommentId">
              <el-input v-model="form.replyToCommentId" placeholder="回复的评论ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <entity-id-field v-model="form.replyToUserId" label="回复用户" prop="replyToUserId" entity-type="user" placeholder="回复的用户ID" />
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listComment, getComment, delComment, addComment, updateComment } from '@/api/weight/comment'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'
import { resolveImageList } from '@/utils/weightImages'
import weightAdminPageMixin from '@/mixins/weightAdminPage'

const CommentImages = {
  functional: true,
  props: {
    images: {
      type: Array,
      default: () => []
    }
  },
  render(h, context) {
    const images = context.props.images || []
    if (!images.length) return h('span', '-')
    return h('div', { class: 'comment-images' }, images.slice(0, 3).map((image, index) => h('el-image', {
      key: image + index,
      class: 'comment-image',
      props: {
        src: image,
        fit: 'cover',
        previewSrcList: images
      }
    })))
  }
}

export default {
  name: 'Comment',
  components: { CommentImages },
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['userId', 'feedId', 'status'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      commentList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        feedId: null,
        userId: null,
        status: null
      },
      statusOptions: [
        { label: '正常', value: '0' },
        { label: '待审核', value: '1' },
        { label: '已屏蔽', value: '2' }
      ],
      form: {},
      rules: {
        feedId: [{ required: true, message: '动态ID不能为空', trigger: 'blur' }],
        userId: [{ required: true, message: '评论者用户ID不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '评论内容不能为空', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listComment(this.queryParams).then(response => {
        this.commentList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        feedId: null,
        userId: null,
        content: null,
        images: null,
        replyToCommentId: null,
        replyToUserId: null,
        likesCount: 0,
        status: '0',
        delFlag: '0'
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
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
      this.title = '添加评论'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getComment(id).then(response => {
        this.form = {
          ...response.data,
          status: response.data.status || '0'
        }
        this.open = true
        this.title = '查看/处理评论'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.id != null) {
          updateComment(this.form).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addComment(this.form).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    updateCommentStatus(row, status) {
      updateComment({ id: row.id, status }).then(() => {
        this.$modal.msgSuccess('状态已更新')
        this.getList()
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除评论编号为"' + ids + '"的数据项？').then(function() {
        return delComment(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('ruoyi-weight/comment/export', {
        ...this.queryParams
      }, `comment_${new Date().getTime()}.xlsx`)
    },
    getImageUrls(images) {
      return resolveImageList(images)
    },
    stripHtml(content) {
      return String(content || '').replace(/<[^>]+>/g, '').trim()
    },
    toStatusText(value) {
      const item = this.statusOptions.find(option => option.value === String(value))
      return item ? item.label : value || '-'
    },
    statusTagType(status) {
      return {
        '0': 'success',
        '1': 'warning',
        '2': 'danger'
      }[String(status)] || 'info'
    },
    openFeed(feedId) {
      if (!feedId) return
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.circleFeed,
        query: {
          id: String(feedId)
        }
      })
    }
  }
}
</script>

<style scoped>
.comment-images {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.comment-image {
  width: 42px;
  height: 42px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.danger-btn {
  color: #f56c6c !important;
}
</style>
