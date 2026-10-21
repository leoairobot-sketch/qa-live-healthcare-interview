import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Home from '../views/Home.vue';
import Consultation from '../views/Consultation.vue';
import DoctorLogin from '../views/DoctorLogin.vue';
import DoctorRoom from '../views/DoctorRoom.vue';
import Doctors from '../views/Doctors.vue';
import About from '../views/About.vue';
import Login from '../views/Login.vue';
import { ensureAuthReady, patientUser } from '../store/auth';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home,
  },
  {
    path: '/consultation',
    name: 'Consultation',
    component: Consultation,
  },
  {
    path: '/consultation/:doctorUsername',
    name: 'ConsultationRoom',
    component: Consultation,
  },
  {
    path: '/doctors',
    name: 'Doctors',
    component: Doctors,
  },
  {
    path: '/about',
    name: 'About',
    component: About,
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
  },
  {
    path: '/doctor/login',
    name: 'DoctorLogin',
    component: DoctorLogin,
  },
  {
    path: '/doctor/room/:username',
    name: 'DoctorRoom',
    component: DoctorRoom,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 问诊门禁：未登录访问 /consultation* 重定向登录页并携带回跳地址；已登录访问 /login 直接回首页
router.beforeEach(async (to) => {
  await ensureAuthReady();
  if (to.path.startsWith('/consultation') && !patientUser.value) {
    return { path: '/login', query: { redirect: to.fullPath } };
  }
  if (to.path === '/login' && patientUser.value) {
    return { path: '/' };
  }
});

export default router;
