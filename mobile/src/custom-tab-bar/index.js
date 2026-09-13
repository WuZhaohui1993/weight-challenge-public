Component({
    data: {
        selected: 0,
        actionSheetVisible: false,
        visible: true,
        navigating: false
    },
    lifetimes: {
        attached() {
            const app = getApp()
            if (app?.globalData) {
                app.globalData.tabBar = this
            }
        },
        detached() {
            const app = getApp()
            if (app?.globalData?.tabBar === this) {
                app.globalData.tabBar = null
            }
        }
    },
    methods: {
        runAfterRender(callback) {
            if (typeof wx.nextTick === 'function') {
                wx.nextTick(() => {
                    setTimeout(callback, 0)
                })
                return
            }
            setTimeout(callback, 0)
        },
        setDataAfterRender(data, callback) {
            this.runAfterRender(() => {
                this.setData(data, callback)
            })
        },
        switchTab(e) {
            const data = e.currentTarget.dataset
            const url = data.path
            if (this.data.navigating) {
                return
            }
            this.setDataAfterRender({ actionSheetVisible: false, navigating: true }, () => {
                this.runAfterRender(() => {
                    wx.switchTab({
                        url,
                        complete: () => {
                            this.setDataAfterRender({ navigating: false })
                        }
                    })
                })
            })
        },
        goQuickRecord() {
            if (this.data.navigating) {
                return
            }
            this.setDataAfterRender({ actionSheetVisible: true })
        },
        closeActionSheet() {
            if (this.data.navigating) {
                return
            }
            this.setDataAfterRender({ actionSheetVisible: false })
        },
        stopPropagation() {},
        openRecord(e) {
            const tab = e.currentTarget.dataset.tab || 'weight'
            if (this.data.navigating) {
                return
            }
            this.setDataAfterRender({ actionSheetVisible: false, navigating: true }, () => {
                this.runAfterRender(() => {
                    wx.navigateTo({
                        url: `/pages/record/index?tab=${tab}`,
                        complete: () => {
                            setTimeout(() => {
                                this.setDataAfterRender({ navigating: false })
                            }, 300)
                        }
                    })
                })
            })
        }
    }
})
