<template>
  <div class="app-container notification-page">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="接收用户" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="用户ID"
          clearable
          style="width: 120px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="触发用户" prop="fromUserId">
        <el-input
          v-model="queryParams.fromUserId"
          placeholder="用户ID"
          clearable
          style="width: 120px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="item in notificationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="目标ID" prop="targetId">
        <el-input
          v-model="queryParams.targetId"
          placeholder="目标ID"
          clearable
          style="width: 120px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="通知内容"
          clearable
          style="width: 180px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否已读" prop="isRead">
        <el-select v-model="queryParams.isRead" placeholder="全部" clearable style="width: 110px">
          <el-option label="已读" value="1" />
          <el-option label="未读" value="0" />
        </el-select>
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
          icon="el-icon-message"
          size="mini"
          @click="handleSend"
          v-hasPermi="['ruoyi-weight:notification:add']"
        >发送通知</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['ruoyi-weight:notification:add']"
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
          v-hasPermi="['ruoyi-weight:notification:edit']"
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
          v-hasPermi="['ruoyi-weight:notification:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:notification:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="notificationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="接收用户" align="center" width="140">
        <template slot-scope="scope">
          <user-link :user-id="scope.row.userId" />
        </template>
      </el-table-column>
      <el-table-column label="通知类型" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.type === 'system' ? 'warning' : 'info'" size="small">
            {{ toNotificationTypeText(scope.row.type) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="触发用户" align="center" width="140">
        <template slot-scope="scope">
          <user-link v-if="scope.row.fromUserId" :user-id="scope.row.fromUserId" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="目标类型" align="center" prop="targetType" />
      <el-table-column label="目标ID" align="center" prop="targetId" />
      <el-table-column label="通知内容" prop="content" min-width="240" show-overflow-tooltip />
      <el-table-column label="预览内容" prop="preview" min-width="180" show-overflow-tooltip />
      <el-table-column label="是否已读" align="center" width="90">
        <template slot-scope="scope">
          <el-tag :type="toFlagTagType(scope.row.isRead)" size="small">
            {{ toFlagText(scope.row.isRead, '已读', '未读') }}
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
            v-hasPermi="['ruoyi-weight:notification:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:notification:remove']"
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

    <el-dialog title="发送系统通知" :visible.sync="sendOpen" width="680px" append-to-body>
      <el-form ref="sendForm" :model="sendForm" :rules="sendRules" label-width="96px" class="notification-send-form">
        <el-form-item label="接收范围" prop="recipientMode">
          <el-radio-group v-model="sendForm.recipientMode" @change="clearSendRecipientValidate">
            <el-radio-button label="all">全部用户</el-radio-button>
            <el-radio-button label="selected">指定用户</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="sendForm.recipientMode === 'selected'" label="接收用户" prop="userIdsText">
          <el-input
            v-model="sendForm.userIdsText"
            type="textarea"
            :rows="3"
            placeholder="输入用户ID，支持逗号、空格或换行分隔"
          />
          <div v-if="selectedRecipientPreview.length" class="recipient-preview">
            <el-tag v-for="userId in selectedRecipientPreview" :key="userId" size="mini" type="info">用户 #{{ userId }}</el-tag>
            <span v-if="selectedRecipientCount > selectedRecipientPreview.length" class="recipient-more">
              另有 {{ selectedRecipientCount - selectedRecipientPreview.length }} 个
            </span>
          </div>
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input
            v-model="sendForm.content"
            type="textarea"
            :rows="5"
            maxlength="500"
            show-word-limit
            placeholder="请输入要展示给用户的通知消息"
          />
        </el-form-item>
        <el-form-item label="预览内容" prop="preview">
          <el-input
            v-model="sendForm.preview"
            maxlength="120"
            show-word-limit
            placeholder="不填则自动截取通知内容"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSendForm">发 送</el-button>
        <el-button @click="sendOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加或修改消息通知对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="560px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="88px">
        <entity-id-field v-model="form.userId" label="接收用户" prop="userId" entity-type="user" placeholder="请输入接收通知的用户ID" />
        <entity-id-field v-model="form.fromUserId" label="触发用户" prop="fromUserId" entity-type="user" placeholder="请输入触发通知的用户ID" />
        <el-form-item label="通知类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择通知类型" style="width: 100%">
            <el-option v-for="item in notificationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标类型" prop="targetType">
          <el-input v-model="form.targetType" placeholder="如 feedback / circle / feed，可留空" />
        </el-form-item>
        <el-form-item label="目标ID" prop="targetId">
          <el-input v-model="form.targetId" placeholder="请输入目标ID" />
        </el-form-item>
        <el-form-item label="通知内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="5" maxlength="500" show-word-limit placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="预览内容" prop="preview">
          <el-input v-model="form.preview" maxlength="120" show-word-limit placeholder="请输入预览内容" />
        </el-form-item>
        <el-form-item label="是否已读" prop="isRead">
          <el-select v-model="form.isRead" placeholder="请选择" style="width: 100%">
            <el-option label="已读" value="1" />
            <el-option label="未读" value="0" />
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
import { listNotification, getNotification, delNotification, addNotification, broadcastNotification, updateNotification } from "@/api/weight/notification"
import weightAdminPageMixin from "@/mixins/weightAdminPage"

export default {
  name: "Notification",
  mixins: [weightAdminPageMixin],
  routeQueryFilterKeys: ['userId', 'fromUserId', 'isRead', 'type', 'targetType'],
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
      // 消息通知表格数据
      notificationList: [],
      sendOpen: false,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        type: null,
        fromUserId: null,
        targetType: null,
        targetId: null,
        content: null,
        preview: null,
        isRead: null,
      },
      // 表单参数
      form: {},
      sendForm: {
        recipientMode: 'selected',
        userIdsText: '',
        content: '',
        preview: ''
      },
      notificationTypeOptions: [
        { label: '系统', value: 'system' },
        { label: '点赞', value: 'like' },
        { label: '评论', value: 'comment' },
        { label: '回复', value: 'reply' },
        { label: '关注', value: 'follow' },
        { label: '圈子加入', value: 'circle_join' },
        { label: '圈子提醒', value: 'circle_nudge' },
        { label: '成就', value: 'achievement' }
      ],
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "接收通知的用户ID不能为空", trigger: "blur" }
        ],
        type: [
          { required: true, message: "通知类型不能为空", trigger: "change" }
        ],
        content: [
          { required: true, message: "通知内容不能为空", trigger: "blur" }
        ],
      },
      sendRules: {
        recipientMode: [
          { required: true, message: "请选择接收范围", trigger: "change" }
        ],
        userIdsText: [
          { validator: (rule, value, callback) => this.validateSendUserIds(value, callback), trigger: "blur" }
        ],
        content: [
          { required: true, message: "通知内容不能为空", trigger: "blur" },
          { max: 500, message: "通知内容不能超过500字", trigger: "blur" }
        ],
        preview: [
          { max: 120, message: "预览内容不能超过120字", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  computed: {
    selectedRecipientPreview() {
      return this.parseRecipientIds(this.sendForm.userIdsText).ids.slice(0, 12)
    },
    selectedRecipientCount() {
      return this.parseRecipientIds(this.sendForm.userIdsText).ids.length
    }
  },
  methods: {
    /** 查询消息通知列表 */
    getList() {
      this.loading = true
      listNotification(this.queryParams).then(response => {
        this.notificationList = response.rows
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
        type: 'system',
        fromUserId: null,
        targetType: null,
        targetId: null,
        content: null,
        preview: null,
        isRead: '0',
        createTime: null
      }
      this.resetForm("form")
    },
    resetSendForm() {
      this.sendForm = {
        recipientMode: 'selected',
        userIdsText: '',
        content: '',
        preview: ''
      }
      this.$nextTick(() => {
        this.resetForm("sendForm")
      })
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
      this.title = "添加消息通知"
    },
    /** 发送系统通知 */
    handleSend() {
      this.resetSendForm()
      this.sendOpen = true
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getNotification(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改消息通知"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateNotification(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addNotification(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 提交系统通知发送 */
    submitSendForm() {
      this.$refs["sendForm"].validate(valid => {
        if (!valid) return

        const parsed = this.parseRecipientIds(this.sendForm.userIdsText)
        if (this.sendForm.recipientMode === 'selected') {
          if (parsed.invalid.length) {
            this.$modal.msgWarning("存在无效用户ID：" + parsed.invalid.join("、"))
            return
          }
          if (!parsed.ids.length) {
            this.$modal.msgWarning("请至少填写一个接收用户ID")
            return
          }
        }

        const payload = {
          recipientMode: this.sendForm.recipientMode,
          userIds: this.sendForm.recipientMode === 'selected' ? parsed.ids : [],
          content: String(this.sendForm.content || '').trim(),
          preview: String(this.sendForm.preview || '').trim() || null
        }
        const confirmText = this.sendForm.recipientMode === 'all'
          ? '确认给全部用户发送这条系统通知？'
          : `确认给 ${parsed.ids.length} 个指定用户发送这条系统通知？`
        this.$modal.confirm(confirmText).then(() => {
          return broadcastNotification(payload)
        }).then(response => {
          this.$modal.msgSuccess(response.msg || "发送成功")
          this.sendOpen = false
          this.getList()
        }).catch(() => {})
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除消息通知编号为"' + ids + '"的数据项？').then(function() {
        return delNotification(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/notification/export', {
        ...this.queryParams
      }, `notification_${new Date().getTime()}.xlsx`)
    },
    validateSendUserIds(value, callback) {
      if (this.sendForm.recipientMode !== 'selected') {
        callback()
        return
      }
      const parsed = this.parseRecipientIds(value)
      if (parsed.invalid.length) {
        callback(new Error("存在无效用户ID：" + parsed.invalid.join("、")))
        return
      }
      if (!parsed.ids.length) {
        callback(new Error("请至少填写一个接收用户ID"))
        return
      }
      callback()
    },
    parseRecipientIds(value) {
      const raw = String(value || '').trim()
      if (!raw) {
        return { ids: [], invalid: [] }
      }
      const tokens = raw.split(/[,\s，、;；]+/).filter(Boolean)
      const invalid = []
      const ids = []
      tokens.forEach(token => {
        if (!/^\d+$/.test(token)) {
          invalid.push(token)
          return
        }
        const id = Number(token)
        if (!Number.isSafeInteger(id) || id <= 0) {
          invalid.push(token)
          return
        }
        if (!ids.includes(id)) {
          ids.push(id)
        }
      })
      return { ids, invalid }
    },
    clearSendRecipientValidate() {
      this.$nextTick(() => {
        if (this.$refs.sendForm) {
          this.$refs.sendForm.clearValidate('userIdsText')
        }
      })
    },
    toNotificationTypeText(value) {
      const item = this.notificationTypeOptions.find(option => option.value === value)
      return item ? item.label : value || '-'
    }
  }
}
</script>

<style scoped>
.notification-send-form ::v-deep .el-radio-button__inner {
  min-width: 92px;
}

.recipient-preview {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.recipient-more {
  color: #909399;
  font-size: 12px;
}
</style>
