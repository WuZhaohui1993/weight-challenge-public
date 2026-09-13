<template>
  <el-drawer 
    :title="title" 
    :visible="visible" 
    :size="size" 
    direction="rtl"
    :append-to-body="true"
    :modal-append-to-body="true"
    @close="$emit('update:visible', false)"
    class="detail-drawer"
  >
    <div class="drawer-content">
      <!-- 头部区域：头像/图标 + 标题信息 -->
      <el-card v-if="showHeader" class="header-card">
        <div class="header-wrapper">
          <!-- 头像或图标 -->
          <div class="header-avatar">
            <slot name="avatar">
              <el-avatar v-if="avatar" :size="80" :src="avatar" />
              <span v-else-if="icon" class="header-icon">{{ icon }}</span>
            </slot>
          </div>
          <!-- 标题信息 -->
          <div class="header-info">
            <slot name="header">
              <h3 class="header-title">{{ headerTitle }}</h3>
              <p v-if="headerSubtitle" class="header-subtitle">{{ headerSubtitle }}</p>
            </slot>
            <div v-if="headerTags && headerTags.length" class="header-tags">
              <el-tag 
                v-for="(tag, index) in headerTags" 
                :key="index" 
                :type="tag.type || ''" 
                size="small"
                class="header-tag"
              >
                {{ tag.text }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 详情信息区域 -->
      <slot name="sections">
        <!-- 默认信息展示卡片 -->
        <el-card v-for="(section, sIndex) in sections" :key="sIndex" class="section-card">
          <div slot="header" class="section-header">
            <span>{{ section.icon }} {{ section.title }}</span>
          </div>
          <el-descriptions 
            :column="section.column || 2" 
            border 
            size="small"
          >
            <el-descriptions-item 
              v-for="(item, iIndex) in section.items" 
              :key="iIndex" 
              :label="item.label"
            >
              <template v-if="item.type === 'tag'">
                <el-tag :type="item.tagType || ''" size="small">{{ item.value }}</el-tag>
              </template>
              <template v-else>
                {{ item.value || '-' }}
              </template>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </slot>

      <!-- 自定义内容插槽 -->
      <slot name="content"></slot>

      <!-- 操作按钮区域 -->
      <el-card v-if="$slots.actions || actions.length" class="section-card actions-card">
        <div slot="header" class="section-header">
          <span>⚡ 快捷操作</span>
        </div>
        <div class="actions-wrapper">
          <slot name="actions">
            <el-button 
              v-for="(action, index) in actions" 
              :key="index"
              :size="action.size || 'small'"
              :type="action.type || 'default'"
              :icon="action.icon"
              @click="$emit('action', action.name)"
            >
              {{ action.text }}
            </el-button>
          </slot>
        </div>
      </el-card>
    </div>
  </el-drawer>
</template>

<script>
export default {
  name: 'DetailDrawer',
  props: {
    // 抽屉可见性
    visible: {
      type: Boolean,
      default: false
    },
    // 抽屉标题
    title: {
      type: String,
      default: '详情'
    },
    // 抽屉宽度
    size: {
      type: String,
      default: '40%'
    },
    // 是否显示头部区域
    showHeader: {
      type: Boolean,
      default: true
    },
    // 头像URL
    avatar: {
      type: String,
      default: ''
    },
    // 图标（emoji）
    icon: {
      type: String,
      default: ''
    },
    // 头部标题
    headerTitle: {
      type: String,
      default: ''
    },
    // 头部副标题
    headerSubtitle: {
      type: String,
      default: ''
    },
    // 头部标签数组 [{text, type}]
    headerTags: {
      type: Array,
      default: () => []
    },
    // 详情区块数组 [{icon, title, column, items: [{label, value, type, tagType}]}]
    sections: {
      type: Array,
      default: () => []
    },
    // 操作按钮数组 [{name, text, type, icon, size}]
    actions: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style scoped>
.detail-drawer ::v-deep .el-drawer__body {
  padding: 0;
  overflow-y: auto;
}

.drawer-content {
  padding: 16px;
}

.header-card {
  margin-bottom: 16px;
}

.header-wrapper {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-avatar {
  flex-shrink: 0;
}

.header-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  font-size: 56px;
}

.header-info {
  flex: 1;
}

.header-title {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-subtitle {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #909399;
}

.header-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.header-tag {
  margin: 0;
}

.section-card {
  margin-bottom: 16px;
}

.section-card:last-child {
  margin-bottom: 0;
}

.section-header {
  font-weight: 500;
  font-size: 14px;
}

.actions-wrapper {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
