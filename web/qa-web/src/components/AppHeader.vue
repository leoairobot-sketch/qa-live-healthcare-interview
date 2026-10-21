<template>
  <a-layout-header class="header">
    <div class="header-content">
      <div class="logo">
        <img src="https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=100" alt="QA Live Healthcare" />
        <span>QA Live Healthcare</span>
      </div>
      <a-menu v-model:selectedKeys="selectedKeys" mode="horizontal" class="nav-menu">
        <a-menu-item key="home" @click="navigateTo('/')">
          <HomeOutlined />
          {{ t('nav.home') }}
        </a-menu-item>
        <a-menu-item key="consultation" @click="navigateTo('/consultation')">
          <MessageOutlined />
          {{ t('nav.consultation') }}
        </a-menu-item>
        <a-menu-item key="doctors" @click="navigateTo('/doctors')">
          <TeamOutlined />
          {{ t('nav.doctors') }}
        </a-menu-item>
        <a-menu-item key="about" @click="navigateTo('/about')">
          <InfoCircleOutlined />
          {{ t('nav.about') }}
        </a-menu-item>
      </a-menu>
      <div class="header-actions">
        <a-dropdown>
          <a-button class="lang-btn" @click.prevent>
            <GlobalOutlined />
            {{ currentLocaleLabel }}
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu :selected-keys="[locale]" @click="onLocaleChange">
              <a-menu-item v-for="l in LOCALES" :key="l.value">
                {{ l.label }}
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-dropdown v-if="patientUser">
          <a-button class="user-btn" @click.prevent>
            <UserOutlined />
            {{ t('nav.hello', { name: patientUser.name }) }}
            <DownOutlined />
          </a-button>
          <template #overlay>
            <a-menu @click="onUserMenuClick">
              <a-menu-item key="logout">
                <LogoutOutlined />
                {{ t('nav.logout') }}
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
        <a-button v-else type="primary" @click="navigateTo('/login')">
          <UserOutlined />
          {{ t('nav.patientLogin') }}
        </a-button>
        <a-button type="primary" class="login-btn" @click="navigateTo('/doctor/login')">
          <UserOutlined />
          {{ t('nav.doctorLogin') }}
        </a-button>
      </div>
    </div>
  </a-layout-header>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { message } from 'ant-design-vue';
import { HomeOutlined, MessageOutlined, TeamOutlined, InfoCircleOutlined, UserOutlined, GlobalOutlined, DownOutlined, LogoutOutlined } from '@ant-design/icons-vue';
import { LOCALES, LOCALE_STORAGE_KEY, SupportedLocale } from '../locales';
import { auth, patientUser } from '../store/auth';

const router = useRouter();
const route = useRoute();
const { t, locale } = useI18n();
const selectedKeys = ref<string[]>(['home']);

const currentLocaleLabel = computed(
  () => LOCALES.find(l => l.value === locale.value)?.label ?? locale.value
);

const onLocaleChange = ({ key }: { key: string | number }) => {
  locale.value = key as SupportedLocale;
  localStorage.setItem(LOCALE_STORAGE_KEY, String(key));
  document.documentElement.lang = String(key);
};

watch(() => route.path, (newPath) => {
  if (newPath === '/') {
    selectedKeys.value = ['home'];
  } else if (newPath.startsWith('/consultation')) {
    selectedKeys.value = ['consultation'];
  } else if (newPath.startsWith('/doctors')) {
    selectedKeys.value = ['doctors'];
  } else if (newPath.startsWith('/about')) {
    selectedKeys.value = ['about'];
  }
}, { immediate: true });

const navigateTo = (path: string) => {
  router.push(path);
};

const onUserMenuClick = async ({ key }: { key: string | number }) => {
  if (key === 'logout') {
    await auth.logout();
    message.success('已退出登录');
    router.push('/');
  }
};
</script>

<style scoped>
.header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 0;
  height: 64px;
  line-height: 64px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 24px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo img {
  height: 40px;
  width: 40px;
  border-radius: 8px;
  object-fit: cover;
}

.logo span {
  font-size: 20px;
  font-weight: 600;
  color: #1890ff;
}

.nav-menu {
  flex: 1;
  border: none;
  margin: 0 40px;
  line-height: 64px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.lang-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.user-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.login-btn {
  background: #52c41a;
  border-color: #52c41a;
}

.login-btn:hover {
  background: #73d13d;
  border-color: #73d13d;
}
</style>
