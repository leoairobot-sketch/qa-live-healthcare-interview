import { ref } from 'vue';

// 问诊用户登录态：与后端 /api/auth/* 对应，会话由 JSESSIONID Cookie 维持（经 Vite 代理同源）
export interface PatientUser {
  id: number;
  username: string;
  name: string;
}

export const patientUser = ref<PatientUser | null>(null);

// 应用启动时恢复登录态（仅请求一次，路由守卫与 App 挂载共用）
let mePromise: Promise<void> | null = null;

export function ensureAuthReady(): Promise<void> {
  if (!mePromise) {
    mePromise = auth.fetchMe();
  }
  return mePromise;
}

async function errorMessage(response: Response): Promise<string> {
  try {
    const data = await response.json();
    return data.message ?? '请求失败';
  } catch {
    return '服务暂不可用，请稍后重试';
  }
}

export const auth = {
  patientUser,

  async login(username: string, password: string): Promise<PatientUser> {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    });
    if (!response.ok) {
      throw new Error(await errorMessage(response));
    }
    const user = (await response.json()) as PatientUser;
    patientUser.value = user;
    return user;
  },

  async register(username: string, password: string, name: string): Promise<PatientUser> {
    const response = await fetch('/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password, name }),
    });
    if (!response.ok) {
      throw new Error(await errorMessage(response));
    }
    const user = (await response.json()) as PatientUser;
    patientUser.value = user;
    return user;
  },

  async logout(): Promise<void> {
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
    } catch {
      // 后端不可用时同样清除本地态
    }
    patientUser.value = null;
  },

  async fetchMe(): Promise<void> {
    try {
      const response = await fetch('/api/auth/me');
      patientUser.value = response.ok ? await response.json() : null;
    } catch {
      patientUser.value = null;
    }
  },
};
