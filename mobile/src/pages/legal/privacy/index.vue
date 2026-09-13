<template>
  <view class="legal-page">
    <prototype-header title="隐私政策" back-url="/pages/login/index" />
    <scroll-view scroll-y class="legal-scroll">
      <view class="legal-content wc-page-enter">
        <view class="legal-hero">
          <text class="legal-title">星绾同行隐私政策</text>
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
    title: '一、我们收集的信息',
    items: [
      '为提供健康记录、趋势分析、习惯打卡和圈子互动服务，我们会收集微信登录标识、昵称、头像、性别、生日、身高、当前体重、目标体重、饮水目标、体重记录、饮食记录、运动记录、饮水记录、习惯记录、圈子动态、评论、点赞、通知和上传图片。',
      '在你使用上传头像、发布动态图片或评论图片时，我们会处理你主动选择上传的图片文件。'
    ]
  },
  {
    title: '二、信息使用目的',
    items: [
      '我们会将上述信息用于账号识别、记录保存、健康趋势展示、圈子互动、通知提醒、资料同步、问题排查和安全风控。',
      '健康记录仅用于本小程序内的记录与展示，不会作为医疗诊断、治疗或处方依据。'
    ]
  },
  {
    title: '三、信息存储与保护',
    items: [
      '我们会采取访问控制、传输加密、日志审计和最小化授权等措施保护你的个人信息。',
      '正式上线环境会使用 HTTPS 域名访问接口与上传资源，并将文件存储在持久化服务目录中。'
    ]
  },
  {
    title: '四、共享与公开',
    items: [
      '你主动发布到公开动态或公开圈子的内容，其他用户可以在相应页面看到。',
      '你选择同步到私密圈子的内容，仅对该圈子成员可见。除法律法规要求或你主动授权外，我们不会向无关第三方提供你的个人信息。'
    ]
  },
  {
    title: '五、你的权利',
    items: [
      '你可以在“我的”页面查看和修改个人资料，也可以删除自己发布的动态或评论。',
      '如需注销账号、删除历史数据或反馈隐私问题，请通过小程序内反馈入口或运营方公布的联系方式提出。'
    ]
  },
  {
    title: '六、未成年人保护',
    items: [
      '未成年人使用本小程序前，应取得监护人同意。监护人发现未成年人信息被不当收集或使用时，可以联系运营方处理。'
    ]
  },
  {
    title: '七、政策更新',
    items: [
      '本政策如有更新，会在小程序内展示新版本。涉及重要权益变化时，我们会以显著方式提示。'
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
