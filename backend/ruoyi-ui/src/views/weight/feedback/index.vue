<template>
  <div class="app-container feedback-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="用户ID"
          clearable
          style="width: 120px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="category">
        <el-select v-model="queryParams.category" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="优先级" prop="priority">
        <el-select v-model="queryParams.priority" placeholder="全部" clearable style="width: 110px">
          <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="关键词" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="反馈内容"
          clearable
          style="width: 180px"
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
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['ruoyi-weight:feedback:edit']"
        >处理</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['ruoyi-weight:feedback:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:feedback:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="feedbackList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" prop="id" width="70" align="center" />
      <el-table-column label="用户" min-width="120" align="center">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="类型" width="110" align="center">
        <template slot-scope="scope">
          <el-tag size="small">{{ toCategoryText(scope.row.category) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="反馈内容" prop="content" min-width="240" show-overflow-tooltip />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">
            {{ toStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="优先级" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="priorityTagType(scope.row.priority)" size="small">
            {{ toPriorityText(scope.row.priority) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="来源页面" prop="sourcePage" min-width="160" show-overflow-tooltip />
      <el-table-column label="提交时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleUpdate(scope.row)">查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            class="danger-btn"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:feedback:remove']"
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

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" label-width="90px" class="feedback-detail-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="提交用户">
              <user-link v-if="form.userId" :user-id="form.userId" />
              <span v-else>-</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提交时间">
              <span>{{ form.createTime || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="类型">
              <el-select v-model="form.category" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="优先级">
              <el-select v-model="form.priority" style="width: 100%">
                <el-option v-for="item in priorityOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="反馈内容">
          <el-input v-model="form.content" type="textarea" :rows="5" readonly />
        </el-form-item>

        <el-form-item label="截图" v-if="parsedImages.length">
          <div class="feedback-images">
            <el-image
              v-for="(image, index) in parsedImages"
              :key="image + index"
              :src="resolveImageUrl(image)"
              :preview-src-list="parsedImages.map(resolveImageUrl)"
              fit="cover"
              class="feedback-image"
            />
          </div>
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="联系方式">
              <span>{{ form.contact || '-' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源页面">
              <span>{{ form.sourcePage || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="环境信息">
          <el-input v-model="form.environmentJson" type="textarea" :rows="4" readonly />
        </el-form-item>

        <el-form-item label="处理回复">
          <el-input
            v-model="form.replyContent"
            type="textarea"
            maxlength="500"
            show-word-limit
            :rows="4"
            placeholder="填写后会通过系统通知同步给用户"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" v-hasPermi="['ruoyi-weight:feedback:edit']">保 存</el-button>
        <el-button @click="cancel">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listFeedback, getFeedback, delFeedback, updateFeedback } from '@/api/weight/feedback'
import weightAdminPageMixin from '@/mixins/weightAdminPage'
import { resolveAssetUrl } from '@/utils/asset'

export default {
  name: 'Feedback',
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['userId', 'status', 'category'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      feedbackList: [],
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        category: null,
        content: null,
        status: null,
        priority: null
      },
      form: {},
      categoryOptions: [
        { label: 'Bug 问题', value: 'bug' },
        { label: '功能建议', value: 'suggestion' },
        { label: '体验问题', value: 'experience' },
        { label: '账号数据', value: 'account' },
        { label: '其他', value: 'other' }
      ],
      statusOptions: [
        { label: '待处理', value: 'pending' },
        { label: '处理中', value: 'processing' },
        { label: '已解决', value: 'resolved' },
        { label: '已关闭', value: 'closed' }
      ],
      priorityOptions: [
        { label: '低', value: 'low' },
        { label: '普通', value: 'normal' },
        { label: '高', value: 'high' },
        { label: '紧急', value: 'urgent' }
      ]
    }
  },
  computed: {
    parsedImages() {
      return this.parseImages(this.form.images)
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFeedback(this.queryParams).then(response => {
        this.feedbackList = response.rows
        this.total = response.total
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
        userId: null,
        category: 'suggestion',
        content: null,
        images: null,
        contact: null,
        sourcePage: null,
        environmentJson: null,
        status: 'pending',
        priority: 'normal',
        replyContent: null
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
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFeedback(id).then(response => {
        this.form = {
          ...response.data,
          category: response.data.category || 'other',
          status: response.data.status || 'pending',
          priority: response.data.priority || 'normal'
        }
        this.open = true
        this.title = '处理用户反馈'
      })
    },
    submitForm() {
      updateFeedback(this.form).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.open = false
        this.getList()
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除用户反馈编号为 "' + ids + '" 的数据项？').then(function() {
        return delFeedback(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('ruoyi-weight/feedback/export', {
        ...this.queryParams
      }, `feedback_${new Date().getTime()}.xlsx`)
    },
    parseImages(images) {
      if (!images) return []
      if (Array.isArray(images)) return images
      try {
        const parsed = JSON.parse(images)
        return Array.isArray(parsed) ? parsed : [images]
      } catch (error) {
        return [images]
      }
    },
    resolveImageUrl(image) {
      return resolveAssetUrl(image)
    },
    toCategoryText(value) {
      const item = this.categoryOptions.find(option => option.value === value)
      return item ? item.label : value || '-'
    },
    toStatusText(value) {
      const item = this.statusOptions.find(option => option.value === value)
      return item ? item.label : value || '-'
    },
    toPriorityText(value) {
      const item = this.priorityOptions.find(option => option.value === value)
      return item ? item.label : value || '-'
    },
    statusTagType(status) {
      return {
        pending: 'warning',
        processing: '',
        resolved: 'success',
        closed: 'info'
      }[status] || 'info'
    },
    priorityTagType(priority) {
      return {
        low: 'info',
        normal: '',
        high: 'warning',
        urgent: 'danger'
      }[priority] || 'info'
    }
  }
}
</script>

<style scoped>
.feedback-images {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.feedback-image {
  width: 96px;
  height: 96px;
  border-radius: 6px;
  border: 1px solid #ebeef5;
}

.feedback-detail-form ::v-deep .el-form-item__content {
  min-height: 32px;
}
</style>
