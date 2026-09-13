# 星绾同行

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE) [![Vue 3](https://img.shields.io/badge/Vue-3.x-42b883.svg)](https://vuejs.org/) [![Java](https://img.shields.io/badge/Java-8%2B-ed8b00.svg)](https://www.java.com/)

跨平台健康记录与习惯管理应用，覆盖体重、饮食、饮水、运动、习惯、趋势分析、圈子互动和反馈处理，并提供管理后台。

> 本项目只包含原型、通用业务源码和配置示例，不包含用户记录、上传媒体、微信凭据、数据库备份、生产域名或发布资料。健康数据属于敏感信息，示例环境不得接入真实用户。

## 功能

- **健康记录**：体重、饮食、饮水、运动和习惯记录。
- **趋势分析**：历史趋势、目标进度、统计摘要和阶段反馈。
- **圈子互动**：动态、评论、点赞、关注、通知和邀请。
- **反馈闭环**：建议、问题反馈、处理状态和管理端查看。
- **管理后台**：用户、菜单、数据字典、运营配置和审计能力。
- **交互原型**：提供首页、记录、分析、圈子、习惯、成就和个人中心原型页面。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 移动端 | uni-app、Vue 3、TypeScript、Pinia |
| 管理端 | Vue 2、Element UI、RuoYi |
| 后端 | Java、Spring Boot、MyBatis、JWT |
| 基础设施 | MySQL、Redis、Maven |

## 快速开始

### 环境要求

Git、JDK 8+、Maven、Node.js、pnpm 和 uni-app 开发工具链。

### 预览原型

```bash
open prototype/index.html
```

### 启动后端与管理端

后端配置使用 `backend/config/application-local.example.yml`，数据库、Redis 和可选微信配置通过本地环境变量提供。不要提交 `application-local.yml` 或任何私有小程序配置。

```bash
cd backend
mvn -pl ruoyi-framework,ruoyi-api,ruoyi-admin -am -DskipTests package

cd ../mobile
pnpm install
pnpm run dev:h5
```

微信小程序构建使用 `pnpm run build:mp-weixin`，具体平台配置由使用者在本地补充。

## 测试

```bash
cd backend
mvn -DskipTests package

cd ../mobile
pnpm run type-check
```

后端完整业务回归需要专用测试数据库；公开副本不提供真实数据和恢复快照。

## 项目结构

```text
├── prototype/       # 交互原型
├── backend/         # RuoYi 后端、移动端 API 和管理端
├── mobile/          # uni-app 客户端
├── LICENSE
└── THIRD_PARTY_NOTICES.md
```

## 安全边界

健康记录、联系方式、上传图片、微信 AppSecret、Token 和数据库密钥必须通过本地环境注入并隔离存储。生产环境还需配置 HTTPS、隐私政策、数据删除、备份恢复和访问审计。

## 参与贡献

请使用虚构账号和测试数据验证功能；不要提交真实健康数据、上传媒体、微信配置、生产域名、证书、数据库备份或构建产物。提交 PR 时说明实际运行的构建和测试。

## 许可证

本项目及其自有代码采用 MIT 许可证。RuoYi、uni-app、vue-pure-admin、Element UI 和其他依赖的版权与许可证见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 及各自目录中的原始文件。
