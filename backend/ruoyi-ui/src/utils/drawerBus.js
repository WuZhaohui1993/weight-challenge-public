/**
 * 全局抽屉管理器
 * 使用事件总线模式管理抽屉的打开/关闭，避免嵌套抽屉导致遮罩层叠加
 */
import Vue from 'vue'

// 创建事件总线
export const drawerBus = new Vue()

// 事件类型
export const DRAWER_EVENTS = {
    OPEN_USER_PROFILE: 'open-user-profile',
    OPEN_CIRCLE_PROFILE: 'open-circle-profile',
    CLOSE_ALL_DRAWERS: 'close-all-drawers'
}
