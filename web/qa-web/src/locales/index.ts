import zhCN from './zh-CN';
import enUS from './en-US';

/** 语言偏好在 localStorage 中的 key */
export const LOCALE_STORAGE_KEY = 'qa-web-locale';

/** 可选语言列表（下拉菜单数据源） */
export const LOCALES = [
  { value: 'zh-CN', label: '中文' },
  { value: 'en-US', label: 'English' },
] as const;

export type SupportedLocale = (typeof LOCALES)[number]['value'];

export const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
};

/** 初始语言：localStorage 中合法的持久化值，否则默认中文 */
export function getInitialLocale(): SupportedLocale {
  const stored = localStorage.getItem(LOCALE_STORAGE_KEY);
  return LOCALES.some(l => l.value === stored)
    ? (stored as SupportedLocale)
    : 'zh-CN';
}
