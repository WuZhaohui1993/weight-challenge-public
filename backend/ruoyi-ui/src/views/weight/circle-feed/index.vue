<template>
  <div class="app-container feed-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="动态ID" prop="id">
        <el-input v-model="queryParams.id" placeholder="动态ID" clearable style="width: 110px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="用户" prop="userId">
        <el-input v-model="queryParams.userId" placeholder="用户ID" clearable style="width: 120px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="圈子" prop="originCircleId">
        <el-input v-model="queryParams.originCircleId" placeholder="圈子ID" clearable style="width: 120px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="feedType">
        <el-select v-model="queryParams.feedType" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in feedTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="可见" prop="visibilityScope">
        <el-select v-model="queryParams.visibilityScope" placeholder="全部" clearable style="width: 120px">
          <el-option label="公开" value="public" />
          <el-option label="圈内" value="circle" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="精选" prop="isFeatured">
        <el-select v-model="queryParams.isFeatured" placeholder="全部" clearable style="width: 120px">
          <el-option label="精选" :value="1" />
          <el-option label="普通" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="来源" prop="sourceRecordId">
        <el-input v-model="queryParams.sourceRecordId" placeholder="来源记录ID" clearable style="width: 130px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['ruoyi-weight:feed:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['ruoyi-weight:feed:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:feed:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['ruoyi-weight:feed:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      ref="feedTable"
      class="feed-table"
      v-loading="loading"
      :data="feedList"
      row-key="id"
      :row-style="feedTableRowStyle"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="70" />
      <el-table-column label="发布者" align="center" width="130">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="圈子" align="center" width="130">
        <template slot-scope="scope">
          <circle-link v-if="scope.row.originCircleId || scope.row.circleId" :circle-id="scope.row.originCircleId || scope.row.circleId" />
          <span v-else>公开广场</span>
        </template>
      </el-table-column>
      <el-table-column label="类型" align="center" width="90">
        <template slot-scope="scope">
          <el-tag size="small">{{ toFeedTypeText(scope.row.feedType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="可见范围" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="scope.row.visibilityScope === 'public' ? 'success' : 'info'" size="small">
            {{ scope.row.visibilityScope === 'public' ? '公开' : '圈内' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内容" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ stripHtml(scope.row.content) || '[图片动态]' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图片" align="center" width="150">
        <template slot-scope="scope">
          <content-images :images="getImageUrls(scope.row.images)" @load="syncFeedTableLayout" />
        </template>
      </el-table-column>
      <el-table-column label="来源记录" align="center" width="150">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.sourceRecordType && scope.row.sourceRecordId"
            type="text"
            size="mini"
            @click="openSourceRecord(scope.row)"
          >
            {{ toSourceTypeText(scope.row.sourceRecordType) }} #{{ scope.row.sourceRecordId }}
          </el-button>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="互动" align="center" width="110">
        <template slot-scope="scope">
          <span>{{ scope.row.likesCount || 0 }} 赞 / {{ scope.row.commentsCount || 0 }} 评</span>
        </template>
      </el-table-column>
      <el-table-column label="精选" align="center" width="80">
        <template slot-scope="scope">
          <el-tag :type="Number(scope.row.isFeatured) === 1 ? 'warning' : 'info'" size="small">
            {{ Number(scope.row.isFeatured) === 1 ? '精选' : '普通' }}
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
      <el-table-column label="操作" align="center" width="210" fixed="right" class-name="feed-action-column">
        <template slot-scope="scope">
          <div class="feed-table-actions">
            <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['ruoyi-weight:feed:edit']">查看</el-button>
            <el-button size="mini" type="text" @click="toggleFeatured(scope.row)" v-hasPermi="['ruoyi-weight:feed:edit']">
              {{ Number(scope.row.isFeatured) === 1 ? '取消精选' : '精选' }}
            </el-button>
            <el-dropdown size="mini" @command="status => updateFeedStatus(scope.row, status)" v-hasPermi="['ruoyi-weight:feed:edit']">
              <el-button type="text" size="mini">状态<i class="el-icon-arrow-down el-icon--right"></i></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="0">恢复正常</el-dropdown-item>
                <el-dropdown-item command="1">标为待审</el-dropdown-item>
                <el-dropdown-item command="2">屏蔽</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button size="mini" type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:feed:remove']">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="760px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <entity-id-field v-model="form.userId" label="发布者" prop="userId" entity-type="user" placeholder="请输入发布者用户ID" />
          </el-col>
          <el-col :span="12">
            <entity-id-field v-model="form.originCircleId" label="来源圈子" prop="originCircleId" entity-type="circle" placeholder="公开动态可留空" />
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="动态类型" prop="feedType">
              <el-select v-model="form.feedType" style="width: 100%">
                <el-option v-for="item in feedTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="可见范围" prop="visibilityScope">
              <el-select v-model="form.visibilityScope" style="width: 100%">
                <el-option label="公开" value="public" />
                <el-option label="圈内" value="circle" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态" prop="status">
              <el-select v-model="form.status" style="width: 100%">
                <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="动态内容">
          <editor v-model="form.content" :min-height="192" />
        </el-form-item>
        <el-form-item label="图片(JSON)" prop="images">
          <el-input v-model="form.images" type="textarea" :rows="3" placeholder="图片 URL JSON 数组" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="来源类型" prop="sourceRecordType">
              <el-input v-model="form.sourceRecordType" placeholder="如 weight_record" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="来源ID" prop="sourceRecordId">
              <el-input v-model="form.sourceRecordId" placeholder="来源记录ID" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否精选" prop="isFeatured">
              <el-switch v-model="form.isFeatured" :active-value="1" :inactive-value="0" />
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
import { listFeed, getFeed, delFeed, addFeed, updateFeed } from '@/api/weight/feed'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'
import { resolveImageList } from '@/utils/weightImages'
import weightAdminPageMixin from '@/mixins/weightAdminPage'

const ContentImages = {
  functional: true,
  props: {
    images: {
      type: Array,
      default: () => []
    }
  },
  render(h, context) {
    const images = context.props.images || []
    const notifyLayout = context.listeners.load
    if (!images.length) return h('span', '-')
    return h('div', { class: 'content-images' }, images.slice(0, 3).map((image, index) => h('el-image', {
      key: image + index,
      class: 'content-image',
      on: {
        load: () => {
          if (typeof notifyLayout === 'function') notifyLayout()
        },
        error: () => {
          if (typeof notifyLayout === 'function') notifyLayout()
        }
      },
      props: {
        src: image,
        fit: 'cover',
        previewSrcList: images
      }
    })))
  }
}

export default {
  name: 'Feed',
  components: { ContentImages },
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['id', 'circleId', 'originCircleId', 'userId', 'feedType', 'visibilityScope', 'status', 'isFeatured', 'sourceRecordId', 'sourceRecordType'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      feedList: [],
      feedTableRowStyle: {
        height: '66px'
      },
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        id: null,
        circleId: null,
        originCircleId: null,
        userId: null,
        feedType: null,
        visibilityScope: null,
        sourceRecordId: null,
        sourceRecordType: null,
        isFeatured: null,
        status: null
      },
      form: {},
      feedTypeOptions: [
        { label: '体重', value: 'weight' },
        { label: '饮食', value: 'food' },
        { label: '运动', value: 'exercise' },
        { label: '饮水', value: 'water' },
        { label: '习惯', value: 'habit' },
        { label: '文本', value: 'text' }
      ],
      statusOptions: [
        { label: '正常', value: '0' },
        { label: '待审核', value: '1' },
        { label: '已屏蔽', value: '2' }
      ],
      rules: {
        userId: [{ required: true, message: '发布者用户ID不能为空', trigger: 'blur' }],
        feedType: [{ required: true, message: '动态类型不能为空', trigger: 'change' }],
        visibilityScope: [{ required: true, message: '可见范围不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFeed(this.queryParams).then(response => {
        this.feedList = response.rows || []
        this.total = response.total || 0
        this.loading = false
        this.syncFeedTableLayout()
      }).catch(() => {
        this.loading = false
      })
    },
    syncFeedTableLayout() {
      this.$nextTick(() => {
        if (!this.$refs.feedTable) return
        this.$refs.feedTable.doLayout()
        window.setTimeout(() => {
          if (this.$refs.feedTable) {
            this.$refs.feedTable.doLayout()
          }
        }, 80)
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        id: null,
        circleId: null,
        originCircleId: null,
        userId: null,
        feedType: 'text',
        visibilityScope: 'public',
        content: null,
        images: null,
        sourceRecordId: null,
        sourceRecordType: null,
        likesCount: 0,
        commentsCount: 0,
        isFeatured: 0,
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
      this.title = '添加动态'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getFeed(id).then(response => {
        this.form = {
          ...response.data,
          originCircleId: response.data.originCircleId || response.data.circleId,
          visibilityScope: response.data.visibilityScope || 'circle',
          feedType: response.data.feedType || 'text',
          status: response.data.status || '0',
          isFeatured: Number(response.data.isFeatured || 0)
        }
        this.open = true
        this.title = '查看/处理动态'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const payload = {
          ...this.form,
          circleId: this.form.originCircleId || this.form.circleId || null
        }
        if (payload.id != null) {
          updateFeed(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        } else {
          addFeed(payload).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    updateFeedStatus(row, status) {
      const payload = { id: row.id, status }
      if (status !== '0' && Number(row.isFeatured) === 1) {
        payload.isFeatured = 0
      }
      updateFeed(payload).then(() => {
        this.$modal.msgSuccess(payload.isFeatured === 0 ? '状态已更新，已取消精选' : '状态已更新')
        this.getList()
      })
    },
    toggleFeatured(row) {
      const isFeatured = Number(row.isFeatured) === 1 ? 0 : 1
      if (isFeatured === 1 && String(row.status) !== '0') {
        this.$modal.msgWarning('只有正常动态可以设为精选，请先恢复正常')
        return
      }
      updateFeed({ id: row.id, isFeatured }).then(() => {
        this.$modal.msgSuccess(isFeatured === 1 ? '已设为精选' : '已取消精选')
        this.getList()
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除圈子动态编号为"' + ids + '"的数据项？').then(function() {
        return delFeed(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('ruoyi-weight/circle-feed/export', {
        ...this.queryParams
      }, `feed_${new Date().getTime()}.xlsx`)
    },
    getImageUrls(images) {
      return resolveImageList(images)
    },
    stripHtml(content) {
      return String(content || '').replace(/<[^>]+>/g, '').trim()
    },
    toFeedTypeText(value) {
      const item = this.feedTypeOptions.find(option => option.value === value)
      return item ? item.label : value || '-'
    },
    toSourceTypeText(value) {
      return {
        weight_record: '体重',
        food_record: '饮食',
        exercise_record: '运动',
        water_record: '饮水',
        habit_checkin: '习惯'
      }[value] || value || '-'
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
    openSourceRecord(row) {
      const tabMap = {
        weight_record: 'weight',
        food_record: 'food',
        exercise_record: 'exercise',
        water_record: 'water',
        habit_checkin: 'checkin'
      }
      const tab = tabMap[row.sourceRecordType] || 'weight'
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.records,
        query: {
          tab,
          userId: row.userId ? String(row.userId) : undefined
        }
      })
    }
  }
}
</script>

<style scoped>
.feed-table ::v-deep .el-table__body tr {
  height: 66px;
}

.feed-table ::v-deep .el-table__body td {
  padding: 10px 0;
}

.feed-table ::v-deep .feed-action-column .cell {
  line-height: 1;
}

.feed-table-actions {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  white-space: nowrap;
}

.feed-table-actions ::v-deep .el-button--mini {
  padding-top: 0;
  padding-bottom: 0;
}

.feed-table-actions ::v-deep .el-dropdown {
  line-height: 1;
}

.content-images {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 44px;
}

.content-image {
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.danger-btn {
  color: #f56c6c !important;
}
</style>
