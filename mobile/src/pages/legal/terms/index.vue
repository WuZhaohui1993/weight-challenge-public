<template>
  <view class="legal-page">
    <prototype-header title="用户协议" back-url="/pages/login/index" />
    <scroll-view scroll-y class="legal-scroll">
      <view class="legal-content wc-page-enter">
        <view class="legal-hero">
          <text class="legal-title">星绾同行用户协议</text>
          <text class="legal-meta">生效日期：2026-04-24</text>
        </view>

        <view class="legal-section" v-for="section in sections" :key="section.title">
          <text class="section-title">{{ section.title }}</text>
          <text v-for="item in section.items" :key="item" class="section-text">{{ item }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup lang="ts">
import { useDefaultPageShare } from '@/utils/share'

useDefaultPageShare()

interface LegalSection {
  title: string
  items: string[]
}

const sections: LegalSection[] = [
  {
    title: '一、服务内容',
    items: [
      '星绾同行提供体重、饮食、运动、饮水、习惯打卡、趋势分析、圈子互动、动态评论、点赞和通知等健康管理辅助功能。',
      '本小程序展示的数据和建议仅供日常记录参考，不构成医疗诊断、治疗建议或专业处方。'
    ]
  },
  {
    title: '二、账号使用',
    items: [
      '你可以通过微信授权登录使用本服务。你应确保提交的个人资料和健康记录真实、合法、不会侵犯他人权益。',
      '因你主动分享、转发或公开发布内容导致的信息可见范围变化，由你自行承担相应责任。'
    ]
  },
  {
    title: '三、内容规范',
    items: [
      '你在圈子、动态、评论、头像和昵称中发布的内容不得包含违法违规、侵权、欺诈、骚扰、低俗、暴力、诱导交易或损害他人权益的信息。',
      '运营方有权根据法律法规、平台规则或用户投诉，对违规内容采取删除、隐藏、限制功能或封禁账号等处理。'
    ]
  },
  {
    title: '四、健康风险提示',
    items: [
      '体重变化、饮食控制和运动计划应结合个人身体情况谨慎执行。如你存在疾病、孕期、用药或其他特殊情况，请先咨询专业医生。',
      '你因自行采用记录数据或社区内容而产生的健康风险，由你自行判断并承担。'
    ]
  },
  {
    title: '五、服务变更与中断',
    items: [
      '我们会尽力保障服务稳定，但因系统维护、网络故障、第三方服务异常、不可抗力或监管要求导致的服务中断，可能无法完全避免。',
      '涉及重要服务变更时，我们会在合理范围内通过小程序公告、页面提示或其他方式告知。'
    ]
  },
  {
    title: '六、协议更新',
    items: [
      '本协议可能随业务和合规要求更新。更新后的协议在小程序内展示后生效；你继续使用服务即表示接受更新后的条款。'
    ]
  }
]
</script>

<style scoped lang="scss">
.legal-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--wc-bg);
}

.legal-scroll {
  flex: 1;
  min-height: 0;
}

.legal-content {
  padding: 28rpx 30rpx calc(56rpx + env(safe-area-inset-bottom));
}

.legal-hero {
  padding: 30rpx 0 24rpx;
  border-bottom: 1px solid var(--wc-line);
}

.legal-title {
  display: block;
  font-size: 38rpx;
  font-weight: 800;
  color: var(--wc-text);
  line-height: 1.25;
}

.legal-meta {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: var(--wc-text-faint);
}

.legal-section {
  padding: 28rpx 0;
  border-bottom: 1px solid var(--wc-line);
}

.section-title {
  display: block;
  margin-bottom: 16rpx;
  font-size: 30rpx;
  font-weight: 700;
  color: var(--wc-text);
  line-height: 1.35;
}

.section-text {
  display: block;
  margin-top: 12rpx;
  font-size: 27rpx;
  line-height: 1.75;
  color: var(--wc-text-soft);
}
</style>
