import { drawerBus, DRAWER_EVENTS } from '@/utils/drawerBus'

export default {
  created() {
    this.syncRouteQueryFiltersFromOptions({ resetMissing: true })
  },
  watch: {
    '$route.query': {
      deep: true,
      handler() {
        this.handleWeightRouteQueryChange()
      }
    }
  },
  methods: {
    getRouteQueryFilterKeys() {
      return this.$options.routeQueryFilterKeys || []
    },
    normalizeRouteQueryValue(value) {
      if (value === undefined || value === null || value === '') {
        return null
      }
      return String(value)
    },
    syncRouteQueryFiltersFromOptions(options = {}) {
      const { resetMissing = false, resetPageNum = false } = options
      const keys = this.getRouteQueryFilterKeys()

      if (!keys.length || !this.queryParams || !this.$route || !this.$route.query) {
        return false
      }

      let changed = false
      keys.forEach(key => {
        if (!Object.prototype.hasOwnProperty.call(this.queryParams, key)) {
          return
        }

        const routeHasKey = Object.prototype.hasOwnProperty.call(this.$route.query, key)
        if (!routeHasKey && !resetMissing) {
          return
        }

        const nextValue = routeHasKey
          ? this.normalizeRouteQueryValue(this.$route.query[key])
          : null
        const currentValue = this.normalizeRouteQueryValue(this.queryParams[key])

        if (currentValue !== nextValue) {
          this.queryParams[key] = nextValue
          changed = true
        }
      })

      if (
        changed &&
        resetPageNum &&
        Object.prototype.hasOwnProperty.call(this.queryParams, 'pageNum')
      ) {
        this.queryParams.pageNum = 1
      }

      return changed
    },
    applyRouteQueryFilters(keys = []) {
      if (!this.queryParams || !this.$route || !this.$route.query) {
        return false
      }

      let changed = false
      keys.forEach(key => {
        const value = this.$route.query[key]
        if (
          value !== undefined &&
          value !== null &&
          Object.prototype.hasOwnProperty.call(this.queryParams, key)
        ) {
          this.queryParams[key] = value
          changed = true
        }
      })
      return changed
    },
    handleWeightRouteQueryChange() {
      const changed = this.syncRouteQueryFiltersFromOptions({
        resetMissing: true,
        resetPageNum: true
      })

      if (!changed) {
        return
      }

      if (typeof this.onRouteQueryFiltersChanged === 'function') {
        this.onRouteQueryFiltersChanged()
        return
      }

      if (typeof this.getList === 'function') {
        this.getList()
      }
    },
    openUserProfile(userId) {
      if (!userId) return
      drawerBus.$emit(DRAWER_EVENTS.OPEN_USER_PROFILE, userId)
    },
    openCircleProfile(circleId) {
      if (!circleId) return
      drawerBus.$emit(DRAWER_EVENTS.OPEN_CIRCLE_PROFILE, circleId)
    },
    toFlagText(value, trueText = '是', falseText = '否', truthyValues = ['1', 1, true, 'true']) {
      return truthyValues.includes(value) ? trueText : falseText
    },
    toFlagTagType(value, trueType = 'success', falseType = 'info', truthyValues = ['1', 1, true, 'true']) {
      return truthyValues.includes(value) ? trueType : falseType
    },
    toStatusText(value, mapping = {}) {
      return mapping[value] || mapping[String(value)] || value || '-'
    }
  }
}
