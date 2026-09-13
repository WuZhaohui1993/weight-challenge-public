<template>
  <el-link 
    type="primary" 
    :underline="false" 
    @click="handleClick"
    class="user-link"
  >
    <span v-if="showAvatar" class="user-avatar">
      <el-avatar :size="avatarSize" :src="displayAvatar" />
    </span>
    <span class="user-text">
      {{ displayName }}
    </span>
  </el-link>
</template>

<script>
import { getUserInfo, getUserFromCache } from '@/utils/userCache'
import { drawerBus, DRAWER_EVENTS } from '@/utils/drawerBus'

export default {
  name: 'UserLink',
  props: {
    userId: {
      type: [Number, String],
      required: true
    },
    nickname: {
      type: String,
      default: ''
    },
    avatar: {
      type: String,
      default: ''
    },
    showAvatar: {
      type: Boolean,
      default: true
    },
    avatarSize: {
      type: Number,
      default: 24
    },
    autoFetch: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      defaultAvatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
      fetchedUser: null
    }
  },
  computed: {
    displayName() {
      if (this.nickname) return this.nickname
      if (this.fetchedUser && this.fetchedUser.nickname) return this.fetchedUser.nickname
      return this.userId
    },
    displayAvatar() {
      if (this.avatar) return this.avatar
      if (this.fetchedUser && this.fetchedUser.avatar) return this.fetchedUser.avatar
      return this.defaultAvatar
    }
  },
  watch: {
    userId: {
      immediate: true,
      handler(newVal) {
        if (newVal && this.autoFetch && !this.nickname) {
          this.loadUserInfo()
        }
      }
    }
  },
  methods: {
    async loadUserInfo() {
      const cached = getUserFromCache(this.userId)
      if (cached) {
        this.fetchedUser = cached
        return
      }
      const user = await getUserInfo(this.userId)
      if (user) {
        this.fetchedUser = user
      }
    },
    handleClick() {
      // 通过事件总线通知全局容器打开用户画像抽屉
      drawerBus.$emit(DRAWER_EVENTS.OPEN_USER_PROFILE, this.userId)
    }
  }
}
</script>

<style scoped>
.user-link {
  display: inline-flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  margin-right: 6px;
  flex-shrink: 0;
}

.user-text {
  font-size: 14px;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-link:hover .user-text {
  text-decoration: underline;
}
</style>

