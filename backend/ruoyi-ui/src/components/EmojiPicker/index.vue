<template>
  <div class="emoji-picker">
    <el-popover
      placement="bottom-start"
      width="320"
      trigger="click"
      v-model="visible"
    >
      <div class="emoji-container">
        <div class="emoji-category" v-for="(emojis, category) in emojiGroups" :key="category">
          <div class="category-title">{{ category }}</div>
          <div class="emoji-grid">
            <span
              v-for="emoji in emojis"
              :key="emoji"
              class="emoji-item"
              :class="{ active: value === emoji }"
              @click="selectEmoji(emoji)"
            >
              {{ emoji }}
            </span>
          </div>
        </div>
      </div>
      
      <el-input
        slot="reference"
        :value="value"
        readonly
        :placeholder="placeholder"
        class="emoji-input"
      >
        <template slot="prefix">
          <span class="emoji-preview">{{ value || '🎯' }}</span>
        </template>
        <template slot="suffix">
          <i class="el-icon-arrow-down"></i>
        </template>
      </el-input>
    </el-popover>
  </div>
</template>

<script>
export default {
  name: 'EmojiPicker',
  props: {
    value: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '选择图标'
    }
  },
  data() {
    return {
      visible: false,
      emojiGroups: {
        '健康': ['⚖️', '🏃', '🏋️', '🧘', '🚴', '🏊', '💪', '🍎', '🥗', '🥤', '💧', '🥦'],
        '习惯': ['✅', '⏰', '📖', '🛏️', '🌅', '🌙', '📝', '🎯', '🔥', '⭐', '💡', '🚀'],
        '社交': ['👥', '🏆', '🎉', '👏', '❤️', '💬', '🔔', '📣', '🤝', '💯', '🌟', '🎖️'],
        '通用': ['📊', '📈', '💰', '🏠', '🎨', '🎮', '🎵', '📸', '✨', '🌈', '☀️', '🌙']
      }
    }
  },
  methods: {
    selectEmoji(emoji) {
      this.$emit('input', emoji)
      this.visible = false
    }
  }
}
</script>

<style scoped>
.emoji-picker {
  width: 100%;
}

.emoji-input {
  cursor: pointer;
}

.emoji-input >>> .el-input__inner {
  cursor: pointer;
}

.emoji-preview {
  font-size: 18px;
  line-height: 1;
}

.emoji-container {
  max-height: 280px;
  overflow-y: auto;
}

.emoji-category {
  margin-bottom: 12px;
}

.category-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
  padding-left: 4px;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 4px;
}

.emoji-item {
  font-size: 22px;
  padding: 6px;
  text-align: center;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.emoji-item:hover {
  background: #f5f7fa;
  transform: scale(1.1);
}

.emoji-item.active {
  background: #ecf5ff;
  border: 1px solid #409eff;
}
</style>
