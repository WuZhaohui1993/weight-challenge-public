<template>
  <el-form-item :label="label" :prop="prop">
    <el-input
      :value="displayValue"
      :placeholder="placeholder"
      clearable
      @input="handleInput"
    />
    <div v-if="normalizedValue" class="entity-preview">
      <span class="entity-preview-label">关联对象：</span>
      <template v-if="loading">
        <span class="entity-preview-loading">加载中...</span>
      </template>
      <template v-else-if="loadFailed">
        <span class="entity-preview-error">未找到对应{{ entityLabel }}</span>
      </template>
      <template v-else-if="entityType === 'user'">
        <user-link :user-id="normalizedValue" />
      </template>
      <template v-else-if="entityType === 'circle'">
        <circle-link :circle-id="normalizedValue" />
      </template>
      <span v-if="hint" class="entity-preview-hint">{{ hint }}</span>
    </div>
  </el-form-item>
</template>

<script>
import { getUserInfo } from '@/utils/userCache'
import { getCircleInfo } from '@/utils/circleCache'

export default {
  name: 'EntityIdField',
  props: {
    value: {
      type: [Number, String],
      default: null
    },
    entityType: {
      type: String,
      required: true
    },
    label: {
      type: String,
      required: true
    },
    prop: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: ''
    },
    hint: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      loadFailed: false,
      requestId: 0
    }
  },
  computed: {
    normalizedValue() {
      if (this.value === undefined || this.value === null || this.value === '') {
        return null
      }
      return String(this.value)
    },
    displayValue() {
      return this.value === undefined || this.value === null ? '' : String(this.value)
    },
    entityLabel() {
      return this.entityType === 'circle' ? '圈子' : '用户'
    }
  },
  watch: {
    normalizedValue: {
      immediate: true,
      handler() {
        this.loadEntity()
      }
    }
  },
  methods: {
    handleInput(value) {
      const normalized = value === '' ? null : value
      this.$emit('input', normalized)
    },
    async loadEntity() {
      if (!this.normalizedValue) {
        this.loading = false
        this.loadFailed = false
        return
      }

      const currentRequestId = ++this.requestId
      this.loading = true
      this.loadFailed = false

      const entity = this.entityType === 'circle'
        ? await getCircleInfo(this.normalizedValue)
        : await getUserInfo(this.normalizedValue)

      if (currentRequestId !== this.requestId) {
        return
      }

      this.loading = false
      this.loadFailed = !entity
    }
  }
}
</script>

<style scoped>
.entity-preview {
  display: flex;
  align-items: center;
  gap: 8px;
  min-height: 22px;
  margin-top: 6px;
  line-height: 1.4;
}

.entity-preview-label {
  color: #909399;
  font-size: 12px;
}

.entity-preview-loading {
  font-size: 12px;
  color: #909399;
}

.entity-preview-error {
  font-size: 12px;
  color: #f56c6c;
}

.entity-preview-hint {
  font-size: 12px;
  color: #909399;
}
</style>
