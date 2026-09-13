<template>
  <div class="global-drawer-container">
    <!-- 用户 360° 画像抽屉 -->
    <user-profile-drawer
      :visible.sync="userDrawerVisible"
      :user="currentUser"
      size="45%"
      @edit="handleUserEdit"
    />
    <!-- 圈子 360° 画像抽屉 -->
    <circle-profile-drawer
      :visible.sync="circleDrawerVisible"
      :circle-id="currentCircleId"
      size="45%"
      @edit="handleCircleEdit"
    />
  </div>
</template>

<script>
import { drawerBus, DRAWER_EVENTS } from '@/utils/drawerBus'
import { getUser } from '@/api/weight/user'
import { WEIGHT_ADMIN_ROUTES } from '@/utils/weightAdminRoutes'

export default {
  name: 'GlobalDrawerContainer',
  data() {
    return {
      userDrawerVisible: false,
      currentUser: null,
      circleDrawerVisible: false,
      currentCircleId: null
    }
  },
  created() {
    // 监听打开用户画像事件
    drawerBus.$on(DRAWER_EVENTS.OPEN_USER_PROFILE, this.openUserProfile)
    // 监听打开圈子画像事件
    drawerBus.$on(DRAWER_EVENTS.OPEN_CIRCLE_PROFILE, this.openCircleProfile)
    // 监听关闭所有抽屉事件
    drawerBus.$on(DRAWER_EVENTS.CLOSE_ALL_DRAWERS, this.closeAllDrawers)
  },
  beforeDestroy() {
    drawerBus.$off(DRAWER_EVENTS.OPEN_USER_PROFILE, this.openUserProfile)
    drawerBus.$off(DRAWER_EVENTS.OPEN_CIRCLE_PROFILE, this.openCircleProfile)
    drawerBus.$off(DRAWER_EVENTS.CLOSE_ALL_DRAWERS, this.closeAllDrawers)
  },
  methods: {
    async openUserProfile(userId) {
      // 关闭其他抽屉
      this.circleDrawerVisible = false
      
      // 获取用户信息并打开抽屉
      try {
        const res = await getUser(userId)
        if (res.data) {
          this.currentUser = res.data
          this.userDrawerVisible = true
        }
      } catch (e) {
        console.error('Failed to get user:', e)
        this.$message.error('获取用户信息失败')
      }
    },
    openCircleProfile(circleId) {
      // 关闭其他抽屉
      this.userDrawerVisible = false
      
      // 设置圈子 ID 并打开抽屉
      this.currentCircleId = circleId
      this.circleDrawerVisible = true
    },
    handleUserEdit(user) {
      if (!user || !user.userId) return
      this.closeAllDrawers()
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.user,
        query: {
          userId: String(user.userId),
          action: 'edit'
        }
      }).catch(() => {})
    },
    handleCircleEdit(circle) {
      const circleId = circle && (circle.id || circle.circleId)
      if (!circleId) return
      this.closeAllDrawers()
      this.$router.push({
        path: WEIGHT_ADMIN_ROUTES.circle,
        query: {
          circleId: String(circleId),
          action: 'edit'
        }
      }).catch(() => {})
    },
    closeAllDrawers() {
      this.userDrawerVisible = false
      this.circleDrawerVisible = false
    }
  }
}
</script>
