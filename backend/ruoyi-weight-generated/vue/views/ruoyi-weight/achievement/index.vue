<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="徽章名称" prop="badgeName">
        <el-input
          v-model="queryParams.badgeName"
          placeholder="请输入徽章名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="徽章图标" prop="badgeIcon">
        <el-input
          v-model="queryParams.badgeIcon"
          placeholder="请输入徽章图标"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="徽章描述" prop="badgeDescription">
        <el-input
          v-model="queryParams.badgeDescription"
          placeholder="请输入徽章描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="解锁时间" prop="unlockedAt">
        <el-date-picker clearable
          v-model="queryParams.unlockedAt"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择解锁时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="进度(0-100)" prop="progress">
        <el-input
          v-model="queryParams.progress"
          placeholder="请输入进度(0-100)"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否已解锁" prop="isUnlocked">
        <el-input
          v-model="queryParams.isUnlocked"
          placeholder="请输入是否已解锁"
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
          v-hasPermi="['ruoyi-weight:achievement:add']"
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
          v-hasPermi="['ruoyi-weight:achievement:edit']"
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
          v-hasPermi="['ruoyi-weight:achievement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['ruoyi-weight:achievement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="achievementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="主键" align="center" prop="id" />
      <el-table-column label="用户ID" align="center" prop="userId" />
      <el-table-column label="徽章类型" align="center" prop="badgeType" />
      <el-table-column label="徽章名称" align="center" prop="badgeName" />
      <el-table-column label="徽章图标" align="center" prop="badgeIcon" />
      <el-table-column label="徽章描述" align="center" prop="badgeDescription" />
      <el-table-column label="解锁时间" align="center" prop="unlockedAt" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.unlockedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="进度(0-100)" align="center" prop="progress" />
      <el-table-column label="是否已解锁" align="center" prop="isUnlocked" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['ruoyi-weight:achievement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['ruoyi-weight:achievement:remove']"
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

    <!-- 添加或修改成就徽章对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="徽章名称" prop="badgeName">
          <el-input v-model="form.badgeName" placeholder="请输入徽章名称" />
        </el-form-item>
        <el-form-item label="徽章图标" prop="badgeIcon">
          <el-input v-model="form.badgeIcon" placeholder="请输入徽章图标" />
        </el-form-item>
        <el-form-item label="徽章描述" prop="badgeDescription">
          <el-input v-model="form.badgeDescription" placeholder="请输入徽章描述" />
        </el-form-item>
        <el-form-item label="解锁时间" prop="unlockedAt">
          <el-date-picker clearable
            v-model="form.unlockedAt"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择解锁时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="进度(0-100)" prop="progress">
          <el-input v-model="form.progress" placeholder="请输入进度(0-100)" />
        </el-form-item>
        <el-form-item label="是否已解锁" prop="isUnlocked">
          <el-input v-model="form.isUnlocked" placeholder="请输入是否已解锁" />
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
import { listAchievement, getAchievement, delAchievement, addAchievement, updateAchievement } from "@/api/ruoyi-weight/achievement"

export default {
  name: "Achievement",
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
      // 成就徽章表格数据
      achievementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        badgeType: null,
        badgeName: null,
        badgeIcon: null,
        badgeDescription: null,
        unlockedAt: null,
        progress: null,
        isUnlocked: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        badgeType: [
          { required: true, message: "徽章类型不能为空", trigger: "change" }
        ],
        badgeName: [
          { required: true, message: "徽章名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询成就徽章列表 */
    getList() {
      this.loading = true
      listAchievement(this.queryParams).then(response => {
        this.achievementList = response.rows
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
        badgeType: null,
        badgeName: null,
        badgeIcon: null,
        badgeDescription: null,
        unlockedAt: null,
        progress: null,
        isUnlocked: null,
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
      this.title = "添加成就徽章"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getAchievement(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改成就徽章"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateAchievement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addAchievement(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除成就徽章编号为"' + ids + '"的数据项？').then(function() {
        return delAchievement(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('ruoyi-weight/achievement/export', {
        ...this.queryParams
      }, `achievement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
