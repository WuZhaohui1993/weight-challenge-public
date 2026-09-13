# Weight Challenge 后端

> 基于 RuoYi v3.9.1 的多模块后端工程，当前服务于微信小程序、管理后台和准生产部署。

## 模块说明

| 模块 | 说明 |
|------|------|
| `ruoyi-admin` | Spring Boot 启动入口 |
| `ruoyi-api` | 微信小程序 API，包含认证、用户、记录、圈子、动态、通知、习惯和反馈接口 |
| `ruoyi-weight` | 体重挑战业务模块，包含业务表、Mapper、Service、后台 Controller |
| `ruoyi-ui` | 管理后台前端，Vue 2 + Element UI |
| `sql` | 初始化脚本、演示数据、旧库升级脚本 |

## 当前业务能力

- 小程序接口：`/api/auth`、`/api/user`、`/api/dashboard`、`/api/record`、`/api/circle`、`/api/feed`、`/api/notifications`、`/api/habits`、`/api/feedback`
- 管理后台：数据概览、用户中心、圈子中心、押金中心、记录管理、社区中心、运营支持
- 记录管理：体重 / 饮食 / 运动 / 饮水 / 习惯打卡五类记录统一查看
- 内容治理：动态 / 评论状态支持正常、待审核、已屏蔽；动态支持精选
- 用户反馈：小程序提交反馈，后台处理回复，系统通知回流小程序

## 本地启动

先按仓库根目录 [开发配置模板](../docs/DEV_CONFIG.md) 准备 MySQL、Redis 和本地配置。

```bash
cd backend
mvn -pl ruoyi-framework,ruoyi-api,ruoyi-admin -am -DskipTests package
java -jar ruoyi-admin/target/ruoyi-admin.jar
```

管理员端前后端一键启动：

```bash
cd backend/bin
sh ./start-admin-all.sh
```

停止：

```bash
cd backend/bin
sh ./stop-admin-all.sh
```

## SQL 基线

fresh install 推荐顺序：

```bash
mysql -u <user> -p <database> < backend/sql/ry_20250522.sql
mysql -u <user> -p <database> < backend/sql/quartz.sql
mysql -u <user> -p <database> < backend/sql/weight_challenge.sql
mysql -u <user> -p <database> < backend/sql/weight_menu_seed.sql
mysql -u <user> -p <database> < backend/sql/weight_demo_seed.sql
mysql -u <user> -p <database> < backend/sql/test_data.sql
mysql -u <user> -p <database> < backend/sql/circle_test_data.sql
mysql -u <user> -p <database> < backend/sql/phase6_test_seed.sql
```

旧库升级到当前实现时，按需执行：

```bash
mysql -u <user> -p <database> < backend/sql/record_images_upgrade_20260425.sql
mysql -u <user> -p <database> < backend/sql/circle_goal_template_seed_20260425.sql
mysql -u <user> -p <database> < backend/sql/feedback_feature_20260426.sql
mysql -u <user> -p <database> < backend/sql/admin_operations_console_20260426.sql
```

测试账号和数据口径见 [测试账号与测试数据手册](../docs/TEST_DATA_PLAYBOOK.md)。

## 验证命令

```bash
cd backend
mvn -pl ruoyi-api,ruoyi-weight -am -DskipTests package
```

如果涉及管理后台用户可见变更，还需要：

```bash
cd backend/ruoyi-ui
npm run build:prod
```
