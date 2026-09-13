<template>
  <div class="login">
    <section class="login-hero" aria-label="星绾同行运营后台">
      <div class="brand-lockup">
        <div class="brand-mark">
          <span class="brand-mark__ring"></span>
          <span class="brand-mark__needle"></span>
        </div>
        <div>
          <p class="brand-kicker">Xingwan Tongxing Admin</p>
          <h1>{{ title }}</h1>
        </div>
      </div>

      <div class="hero-copy">
        <p class="hero-eyebrow">圈子、打卡、体重记录统一运营</p>
        <h2>把用户健康挑战的每一步，收拢到一个清晰后台。</h2>
        <p class="hero-desc">聚焦圈子活跃、每日任务、押金记录和内容治理，减少无关入口干扰。</p>
      </div>

      <div class="signal-list">
        <div class="signal-item">
          <span>01</span>
          <strong>圈子运营</strong>
          <em>成员、任务、排行和动态集中处理</em>
        </div>
        <div class="signal-item">
          <span>02</span>
          <strong>健康数据</strong>
          <em>体重、饮食、运动和饮水记录可追踪</em>
        </div>
        <div class="signal-item">
          <span>03</span>
          <strong>提醒闭环</strong>
          <em>系统通知、未完成任务和用户反馈可跟进</em>
        </div>
      </div>
    </section>

    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="form-heading">
        <p>管理员入口</p>
        <h3>登录控制台</h3>
      </div>

      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="请输入管理员账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="请输入登录密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <div class="captcha-row">
          <el-input
            v-model="loginForm.code"
            auto-complete="off"
            placeholder="验证码"
            @keyup.enter.native="handleLogin"
          >
            <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
          </el-input>
          <button type="button" class="login-code" @click="getCode">
            <img :src="codeUrl" class="login-code-img"/>
          </button>
        </div>
      </el-form-item>
      <div class="form-options">
        <el-checkbox v-model="loginForm.rememberMe">记住登录信息</el-checkbox>
        <router-link v-if="register" class="link-type" :to="'/register'">申请账号</router-link>
      </div>
      <el-form-item class="submit-item">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          class="login-submit"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">进入后台</span>
          <span v-else>正在登录...</span>
        </el-button>
      </el-form-item>
      <p class="security-note">仅限授权运营人员访问，请妥善保管账号和验证码。</p>
    </el-form>

    <div class="el-login-footer">
      <site-filing :copyright="footerContent" />
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import defaultSettings from '@/settings'
import SiteFiling from '@/components/SiteFiling'

export default {
  name: "Login",
  components: { SiteFiling },
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      footerContent: defaultSettings.footerContent,
      codeUrl: "",
      loginForm: {
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入管理员账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入登录密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true,
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        ...this.loginForm,
        username: username === undefined ? this.loginForm.username : username,
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
.login {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 460px;
  min-height: 100%;
  overflow: hidden;
  background:
    radial-gradient(circle at 12% 18%, rgba(49, 111, 212, 0.24), transparent 28%),
    linear-gradient(135deg, #081425 0%, #0f2a38 48%, #183b34 100%);
  color: #ffffff;
}

.login::before {
  content: "";
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: linear-gradient(90deg, rgba(0, 0, 0, 0.72), transparent 78%);
  pointer-events: none;
}

.login-hero {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 64px 7vw 58px;
}

.brand-lockup {
  display: flex;
  align-items: center;
  gap: 18px;
}

.brand-mark {
  position: relative;
  width: 54px;
  height: 54px;
  border-radius: 18px;
  background: linear-gradient(145deg, #f9fffc, #c9f6e2);
  box-shadow: 0 18px 44px rgba(5, 17, 31, 0.26);
}

.brand-mark__ring {
  position: absolute;
  inset: 12px;
  border: 4px solid #173b35;
  border-bottom-color: transparent;
  border-radius: 50%;
}

.brand-mark__needle {
  position: absolute;
  left: 26px;
  top: 18px;
  width: 4px;
  height: 21px;
  border-radius: 999px;
  background: #2bb37d;
  transform: rotate(34deg);
  transform-origin: 50% 100%;
}

.brand-kicker {
  margin: 0 0 4px;
  color: rgba(255, 255, 255, 0.62);
  font-size: 12px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.brand-lockup h1,
.hero-copy h2,
.form-heading h3 {
  margin: 0;
}

.brand-lockup h1 {
  font-size: 25px;
  font-weight: 700;
}

.hero-copy {
  max-width: 690px;
  padding: 10vh 0 8vh;
}

.hero-eyebrow {
  margin: 0 0 18px;
  color: #8ce1b7;
  font-size: 15px;
  font-weight: 700;
}

.hero-copy h2 {
  max-width: 760px;
  font-size: 54px;
  line-height: 1.12;
  font-weight: 800;
}

.hero-desc {
  max-width: 520px;
  margin: 24px 0 0;
  color: rgba(255, 255, 255, 0.72);
  font-size: 16px;
  line-height: 1.8;
}

.signal-list {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  max-width: 900px;
}

.signal-item {
  border-top: 1px solid rgba(255, 255, 255, 0.22);
  padding-top: 18px;
}

.signal-item span {
  display: block;
  color: rgba(255, 255, 255, 0.42);
  font-size: 12px;
  margin-bottom: 12px;
}

.signal-item strong,
.signal-item em {
  display: block;
  font-style: normal;
}

.signal-item strong {
  font-size: 17px;
  margin-bottom: 8px;
}

.signal-item em {
  color: rgba(255, 255, 255, 0.62);
  font-size: 13px;
  line-height: 1.6;
}

.login-form {
  position: relative;
  z-index: 1;
  align-self: center;
  justify-self: end;
  width: 380px;
  margin-right: 8vw;
  padding: 38px 36px 30px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 34px 90px rgba(5, 17, 31, 0.34);
  backdrop-filter: blur(18px);

  ::v-deep .el-input__inner {
    height: 44px;
    border: 1px solid #dce5ee;
    border-radius: 12px;
    background: #f8fafc;
    color: #142033;
  }

  ::v-deep .el-input__inner:focus {
    border-color: #2bb37d;
    background: #ffffff;
  }

  .input-icon {
    height: 44px;
    width: 15px;
    margin-left: 4px;
    color: #6f7f91;
  }
}

.form-heading {
  margin-bottom: 28px;
}

.form-heading p {
  margin: 0 0 8px;
  color: #2bb37d;
  font-size: 13px;
  font-weight: 700;
}

.form-heading h3 {
  color: #132033;
  font-size: 30px;
  font-weight: 800;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 118px;
  gap: 12px;
}

.login-code {
  width: 118px;
  height: 44px;
  padding: 0;
  overflow: hidden;
  border: 1px solid #dce5ee;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
}

.login-code-img {
  display: block;
  width: 100%;
  height: 100%;
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin: -2px 0 24px;
}

.submit-item {
  margin-bottom: 0;
}

.login-submit {
  width: 100%;
  height: 46px;
  border: 0;
  border-radius: 13px;
  background: linear-gradient(135deg, #1b7f66, #2bb37d);
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 16px 32px rgba(43, 179, 125, 0.28);
}

.security-note {
  margin: 18px 0 0;
  color: #708095;
  font-size: 12px;
  line-height: 1.6;
}

.el-login-footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 18px;
  z-index: 1;
  text-align: center;
  color: rgba(255, 255, 255, 0.52);
  font-size: 12px;
}

@media (max-width: 1080px) {
  .login {
    grid-template-columns: 1fr;
    align-items: center;
    padding: 34px 22px 64px;
  }

  .login-hero {
    padding: 0;
  }

  .hero-copy {
    padding: 54px 0 34px;
  }

  .hero-copy h2 {
    font-size: 38px;
  }

  .signal-list {
    grid-template-columns: 1fr;
    margin-bottom: 28px;
  }

  .login-form {
    justify-self: stretch;
    width: auto;
    max-width: 420px;
    margin: 0 auto;
  }
}

@media (max-width: 560px) {
  .hero-copy h2 {
    font-size: 30px;
  }

  .hero-desc {
    font-size: 14px;
  }

  .login-form {
    padding: 30px 22px 24px;
    border-radius: 20px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .login-code {
    width: 100%;
  }
}
</style>
