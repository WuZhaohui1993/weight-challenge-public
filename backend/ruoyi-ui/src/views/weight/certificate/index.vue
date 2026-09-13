<template>
  <div class="app-container certificate-page">
    <el-row :gutter="12" class="summary-row">
      <el-col :span="4">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ summary.total || 0 }}</div>
          <div class="summary-label">证书总数</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="summary-card valid">
          <div class="summary-value">{{ summary.valid || 0 }}</div>
          <div class="summary-label">正常</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="summary-card warning">
          <div class="summary-value">{{ summary.expiring || 0 }}</div>
          <div class="summary-label">即将到期</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="summary-card danger">
          <div class="summary-value">{{ (summary.expired || 0) + (summary.failed || 0) }}</div>
          <div class="summary-label">异常</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ summary.autoRenew || 0 }}</div>
          <div class="summary-label">自动续签</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="never" class="summary-card">
          <div class="summary-value">{{ summary.minDaysRemaining === null || summary.minDaysRemaining === undefined ? '-' : summary.minDaysRemaining }}</div>
          <div class="summary-label">最近剩余天数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-alert
      class="provider-alert"
      type="info"
      :closable="false"
      show-icon
      title="国内运行环境建议优先配置可控的证书服务商和验证方式：云厂商证书、DNS-01 或自定义命令均可托管；certbot webroot 适合当前已验证的 example.com 这类公网 HTTP-01 场景。"
    />

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input v-model="queryParams.name" placeholder="证书名称" clearable style="width: 150px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="域名" prop="primaryDomain">
        <el-input v-model="queryParams.primaryDomain" placeholder="主域名" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="服务商" prop="provider">
        <el-select v-model="queryParams.provider" placeholder="全部" clearable style="width: 130px">
          <el-option v-for="item in providerOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用" prop="enabled">
        <el-select v-model="queryParams.enabled" placeholder="全部" clearable style="width: 100px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['ruoyi-weight:certificate:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['ruoyi-weight:certificate:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-refresh" size="mini" :disabled="single" @click="handleCheck()" v-hasPermi="['ruoyi-weight:certificate:check']">检测</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-s-promotion" size="mini" :disabled="single" @click="handleRenew()" v-hasPermi="['ruoyi-weight:certificate:renew']">申请/续签</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['ruoyi-weight:certificate:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['ruoyi-weight:certificate:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="refreshData"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="certificateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="证书" min-width="220">
        <template slot-scope="scope">
          <div class="cert-name">{{ scope.row.name }}</div>
          <div class="cert-domain">{{ scope.row.primaryDomain }}</div>
        </template>
      </el-table-column>
      <el-table-column label="服务商" width="120" align="center">
        <template slot-scope="scope">{{ toProviderText(scope.row.provider) }}</template>
      </el-table-column>
      <el-table-column label="签发方式" width="145" align="center">
        <template slot-scope="scope">
          <div>{{ toAcmeModeText(scope.row.acmeMode) }}</div>
          <div class="muted">{{ toChallengeText(scope.row.challengeType) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">{{ toStatusText(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="剩余" width="90" align="center">
        <template slot-scope="scope">
          <span :class="{ dangerText: scope.row.daysRemaining !== null && scope.row.daysRemaining <= 7, warningText: scope.row.daysRemaining > 7 && scope.row.daysRemaining <= scope.row.renewBeforeDays }">
            {{ scope.row.daysRemaining === null || scope.row.daysRemaining === undefined ? '-' : scope.row.daysRemaining + '天' }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="到期时间" prop="notAfter" width="160" align="center" />
      <el-table-column label="自动续签" width="95" align="center">
        <template slot-scope="scope">
          <el-tag :type="Number(scope.row.autoRenew) === 1 ? 'success' : 'info'" size="small">
            {{ Number(scope.row.autoRenew) === 1 ? '开启' : '关闭' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="最近检测" prop="lastCheckedAt" width="160" align="center" />
      <el-table-column label="最近错误" prop="lastError" min-width="220" show-overflow-tooltip />
      <el-table-column label="操作" width="220" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleUpdate(scope.row)">查看</el-button>
          <el-button size="mini" type="text" icon="el-icon-refresh" @click="handleCheck(scope.row)" v-hasPermi="['ruoyi-weight:certificate:check']">检测</el-button>
          <el-button size="mini" type="text" icon="el-icon-s-promotion" @click="handleRenew(scope.row)" v-hasPermi="['ruoyi-weight:certificate:renew']">续签</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(scope.row)" v-hasPermi="['ruoyi-weight:certificate:remove']">删除</el-button>
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

    <el-dialog :title="title" :visible.sync="open" width="860px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="118px" class="cert-form">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="证书名称" prop="name">
              <el-input v-model="form.name" placeholder="如 管理端证书 / NAS 证书" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主域名" prop="primaryDomain">
              <el-input v-model="form.primaryDomain" placeholder="如 example.com" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="域名列表" prop="domains">
          <el-input v-model="form.domains" placeholder="多个域名用逗号分隔，如 example.com,www.example.com" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="证书服务商" prop="provider">
              <el-select v-model="form.provider" style="width: 100%" @change="applyProviderPreset">
                <el-option v-for="item in providerOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="签发方式" prop="acmeMode">
              <el-select v-model="form.acmeMode" style="width: 100%">
                <el-option v-for="item in acmeModeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="验证方式" prop="challengeType">
              <el-select v-model="form.challengeType" style="width: 100%">
                <el-option v-for="item in challengeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="ACME目录">
          <el-input v-model="form.caDirectoryUrl" placeholder="可按服务商自动填充，也可填写国内可达的 ACME 目录地址" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="DNS服务商">
              <el-select v-model="form.dnsProvider" clearable filterable allow-create default-first-option style="width: 100%" placeholder="DNS-01 时填写，如 aliyun / tencent / dnspod">
                <el-option label="阿里云 DNS" value="aliyun" />
                <el-option label="腾讯云 DNSPod" value="tencent" />
                <el-option label="Cloudflare" value="cloudflare" />
                <el-option label="手工 DNS" value="manual" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请邮箱">
              <el-input v-model="form.email" placeholder="ACME 注册邮箱，可留空" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="webroot目录" v-if="form.acmeMode === 'certbot_webroot'">
          <el-input v-model="form.webrootPath" placeholder="/var/www/weight-challenge-admin" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="证书文件">
              <el-input v-model="form.fullchainPath" placeholder="/etc/letsencrypt/live/domain/fullchain.pem" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="私钥文件">
              <el-input v-model="form.keyPath" placeholder="/etc/letsencrypt/live/domain/privkey.pem" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="自定义签发命令" v-if="form.acmeMode === 'custom_command'">
          <el-input v-model="form.issueCommand" type="textarea" :rows="3" placeholder="可使用 {primaryDomain}、{domains}、{webrootPath}、{email}、{caDirectoryUrl} 占位符" />
        </el-form-item>

        <el-form-item label="自定义续签命令" v-if="form.acmeMode === 'custom_command'">
          <el-input v-model="form.renewCommand" type="textarea" :rows="3" placeholder="留空时使用自定义签发命令" />
        </el-form-item>

        <el-form-item label="部署命令">
          <el-input v-model="form.deployCommand" type="textarea" :rows="2" placeholder="续签后执行，如 systemctl reload nginx" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="提前续签天数">
              <el-input-number v-model="form.renewBeforeDays" :min="1" :max="60" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="自动续签">
              <el-switch v-model="form.autoRenew" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="启用状态">
              <el-switch v-model="form.enabled" active-value="0" inactive-value="1" active-text="启用" inactive-text="停用" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16" v-if="form.id">
          <el-col :span="8">
            <el-form-item label="当前状态">
              <el-tag :type="statusTagType(form.status)">{{ toStatusText(form.status) }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="剩余天数">
              <span>{{ form.daysRemaining === null || form.daysRemaining === undefined ? '-' : form.daysRemaining + '天' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="到期时间">
              <span>{{ form.notAfter || '-' }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="最近日志" v-if="form.lastLog">
          <el-input v-model="form.lastLog" type="textarea" :rows="6" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button v-if="form.id" icon="el-icon-refresh" @click="handleCheck(form)" v-hasPermi="['ruoyi-weight:certificate:check']">检测</el-button>
        <el-button v-if="form.id" icon="el-icon-s-promotion" @click="handleRenew(form)" v-hasPermi="['ruoyi-weight:certificate:renew']">申请/续签</el-button>
        <el-button type="primary" @click="submitForm">保 存</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCertificate,
  getCertificateSummary,
  getCertificate,
  delCertificate,
  addCertificate,
  updateCertificate,
  checkCertificate,
  renewCertificate
} from '@/api/weight/certificate'

export default {
  name: 'WeightCertificate',
  data() {
    return {
      loading: true,
      actionLoading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      certificateList: [],
      summary: {},
      title: '',
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        primaryDomain: null,
        provider: null,
        status: null,
        enabled: null
      },
      form: {},
      providerOptions: [
        { label: "Let's Encrypt", value: 'letsencrypt', directory: 'https://acme-v02.api.letsencrypt.org/directory' },
        { label: 'ZeroSSL', value: 'zerossl', directory: 'https://acme.zerossl.com/v2/DV90' },
        { label: '阿里云证书', value: 'aliyun' },
        { label: '腾讯云证书', value: 'tencent' },
        { label: '自定义服务商', value: 'custom' }
      ],
      acmeModeOptions: [
        { label: 'certbot webroot', value: 'certbot_webroot' },
        { label: '自定义命令', value: 'custom_command' },
        { label: '手工托管', value: 'manual' }
      ],
      challengeOptions: [
        { label: 'HTTP-01', value: 'http-01' },
        { label: 'DNS-01', value: 'dns-01' },
        { label: '手工验证', value: 'manual' }
      ],
      statusOptions: [
        { label: '正常', value: 'valid' },
        { label: '即将到期', value: 'expiring' },
        { label: '已过期', value: 'expired' },
        { label: '失败', value: 'failed' },
        { label: '未知', value: 'unknown' }
      ],
      rules: {
        name: [{ required: true, message: '证书名称不能为空', trigger: 'blur' }],
        primaryDomain: [{ required: true, message: '主域名不能为空', trigger: 'blur' }],
        provider: [{ required: true, message: '证书服务商不能为空', trigger: 'change' }],
        acmeMode: [{ required: true, message: '签发方式不能为空', trigger: 'change' }],
        challengeType: [{ required: true, message: '验证方式不能为空', trigger: 'change' }]
      }
    }
  },
  created() {
    this.refreshData()
  },
  methods: {
    refreshData() {
      this.getSummary()
      this.getList()
    },
    getSummary() {
      getCertificateSummary().then(response => {
        this.summary = response.data || {}
      })
    },
    getList() {
      this.loading = true
      listCertificate(this.queryParams).then(response => {
        this.certificateList = response.rows
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
        name: null,
        primaryDomain: null,
        domains: null,
        provider: 'letsencrypt',
        caDirectoryUrl: 'https://acme-v02.api.letsencrypt.org/directory',
        acmeMode: 'certbot_webroot',
        challengeType: 'http-01',
        dnsProvider: null,
        webrootPath: '/var/www/weight-challenge-admin',
        email: null,
        fullchainPath: null,
        keyPath: null,
        certPath: null,
        renewBeforeDays: 20,
        autoRenew: 1,
        deployCommand: 'systemctl reload nginx',
        issueCommand: null,
        renewCommand: null,
        status: 'unknown',
        enabled: '0',
        remark: null
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
      this.title = '新增HTTPS证书'
    },
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getCertificate(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = 'HTTPS证书详情'
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const request = this.form.id ? updateCertificate(this.form) : addCertificate(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? '修改成功' : '新增成功')
          this.open = false
          this.refreshData()
        })
      })
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除HTTPS证书编号为"' + ids + '"的数据项？').then(function() {
        return delCertificate(ids)
      }).then(() => {
        this.refreshData()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleCheck(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) return
      this.actionLoading = true
      this.$modal.loading('正在检测证书状态，请稍候...')
      checkCertificate(id).then(response => {
        this.$modal.closeLoading()
        this.$modal.msgSuccess('检测完成')
        this.afterActionRefresh(response.data)
      }).catch(() => {
        this.$modal.closeLoading()
      }).finally(() => {
        this.actionLoading = false
      })
    },
    handleRenew(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) return
      this.$modal.confirm('将按当前配置执行证书申请/续签命令，是否继续？').then(() => {
        this.$modal.loading('正在申请或续签证书，请稍候...')
        return renewCertificate(id, true)
      }).then(response => {
        this.$modal.closeLoading()
        this.$modal.msgSuccess('续签流程已执行')
        this.afterActionRefresh(response.data)
      }).catch(() => {
        this.$modal.closeLoading()
      })
    },
    afterActionRefresh(data) {
      if (this.open && data && data.id === this.form.id) {
        this.form = data
      }
      this.refreshData()
    },
    handleExport() {
      this.download('ruoyi-weight/certificate/export', {
        ...this.queryParams
      }, `certificate_${new Date().getTime()}.xlsx`)
    },
    applyProviderPreset(value) {
      const provider = this.providerOptions.find(item => item.value === value)
      if (provider && provider.directory && !this.form.caDirectoryUrl) {
        this.form.caDirectoryUrl = provider.directory
      } else if (provider && provider.directory && ['letsencrypt', 'zerossl'].includes(value)) {
        this.form.caDirectoryUrl = provider.directory
      }
      if (['aliyun', 'tencent', 'custom'].includes(value) && this.form.acmeMode === 'certbot_webroot') {
        this.form.acmeMode = 'custom_command'
        this.form.challengeType = 'dns-01'
      }
    },
    statusTagType(status) {
      if (status === 'valid') return 'success'
      if (status === 'expiring') return 'warning'
      if (status === 'expired' || status === 'failed') return 'danger'
      return 'info'
    },
    toStatusText(value) {
      const item = this.statusOptions.find(option => option.value === value)
      return item ? item.label : '未知'
    },
    toProviderText(value) {
      const item = this.providerOptions.find(option => option.value === value)
      return item ? item.label : (value || '未配置')
    },
    toAcmeModeText(value) {
      const item = this.acmeModeOptions.find(option => option.value === value)
      return item ? item.label : (value || '-')
    },
    toChallengeText(value) {
      const item = this.challengeOptions.find(option => option.value === value)
      return item ? item.label : (value || '-')
    }
  }
}
</script>

<style scoped>
.certificate-page {
  background: #f6f8fb;
}

.summary-row {
  margin-bottom: 12px;
}

.summary-card {
  border-radius: 6px;
  border: 1px solid #e5e9f2;
}

.summary-value {
  font-size: 24px;
  line-height: 32px;
  font-weight: 700;
  color: #303133;
}

.summary-label {
  margin-top: 4px;
  font-size: 13px;
  color: #606266;
}

.summary-card.valid .summary-value {
  color: #67c23a;
}

.summary-card.warning .summary-value,
.warningText {
  color: #e6a23c;
}

.summary-card.danger .summary-value,
.dangerText {
  color: #f56c6c;
}

.provider-alert {
  margin-bottom: 12px;
}

.cert-name {
  font-weight: 600;
  color: #303133;
}

.cert-domain,
.muted {
  margin-top: 3px;
  font-size: 12px;
  color: #909399;
}

.danger-btn {
  color: #f56c6c;
}

.cert-form ::v-deep .el-textarea__inner {
  font-family: Menlo, Monaco, Consolas, "Courier New", monospace;
}
</style>
