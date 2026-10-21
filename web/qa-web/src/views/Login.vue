<template>
  <div class="login-page">
    <div class="login-card">
      <h1>{{ mode === 'login' ? '问诊用户登录' : '注册问诊账号' }}</h1>
      <p>{{ mode === 'login' ? '登录后即可向在线医生发起问诊' : '注册成功后将自动登录' }}</p>

      <a-form
        v-if="mode === 'login'"
        :model="loginForm"
        :rules="rules"
        @finish="submitLogin"
        layout="vertical"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="loginForm.username" size="large" placeholder="请输入用户名">
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="loginForm.password" size="large" placeholder="请输入密码">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="submitting">
            登录
          </a-button>
        </a-form-item>
      </a-form>

      <a-form
        v-else
        :model="registerForm"
        :rules="rules"
        @finish="submitRegister"
        layout="vertical"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="registerForm.username" size="large" placeholder="3-32位字母、数字或下划线">
            <template #prefix><UserOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="registerForm.password" size="large" placeholder="至少6位密码">
            <template #prefix><LockOutlined /></template>
          </a-input-password>
        </a-form-item>
        <a-form-item label="姓名" name="name">
          <a-input v-model:value="registerForm.name" size="large" placeholder="请输入真实姓名（问诊展示用）">
            <template #prefix><IdcardOutlined /></template>
          </a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" size="large" block :loading="submitting">
            注册并登录
          </a-button>
        </a-form-item>
      </a-form>

      <div class="mode-switch">
        <span v-if="mode === 'login'">
          还没有账号？<a @click="switchMode('register')">立即注册</a>
        </span>
        <span v-else>
          已有账号？<a @click="switchMode('login')">返回登录</a>
        </span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { UserOutlined, LockOutlined, IdcardOutlined } from '@ant-design/icons-vue';
import { auth } from '../store/auth';

const route = useRoute();
const router = useRouter();

const mode = ref<'login' | 'register'>('login');
const submitting = ref(false);

const loginForm = reactive({ username: '', password: '' });
const registerForm = reactive({ username: '', password: '', name: '' });

const rules = {
  username: [
    { required: true, message: '请输入用户名' },
    { pattern: /^[A-Za-z0-9_]{3,32}$/, message: '用户名需为3-32位字母、数字或下划线' },
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 64, message: '密码长度需为6-64位' },
  ],
  name: [
    { required: true, message: '请输入姓名' },
    { min: 2, max: 32, message: '姓名需为2-32位' },
  ],
};

// 仅接受站内相对路径，防开放重定向
function redirectTarget(): string {
  const target = route.query.redirect;
  if (typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')) {
    return target;
  }
  return '/';
}

const switchMode = (next: 'login' | 'register') => {
  mode.value = next;
};

const submitLogin = async () => {
  submitting.value = true;
  try {
    await auth.login(loginForm.username, loginForm.password);
    message.success('登录成功');
    router.push(redirectTarget());
  } catch (error) {
    message.error(error instanceof Error ? error.message : '登录失败');
  } finally {
    submitting.value = false;
  }
};

const submitRegister = async () => {
  submitting.value = true;
  try {
    await auth.register(registerForm.username, registerForm.password, registerForm.name);
    message.success('注册成功');
    router.push(redirectTarget());
  } catch (error) {
    message.error(error instanceof Error ? error.message : '注册失败');
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.login-page {
  min-height: calc(100vh - 64px);
  padding-top: 64px;
  background: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  background: #fff;
  border-radius: 16px;
  padding: 48px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  margin: 24px;
}

.login-card h1 {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  text-align: center;
  margin-bottom: 8px;
}

.login-card > p {
  font-size: 14px;
  color: #666;
  text-align: center;
  margin-bottom: 32px;
}

.mode-switch {
  text-align: center;
  font-size: 14px;
  color: #666;
}
</style>
