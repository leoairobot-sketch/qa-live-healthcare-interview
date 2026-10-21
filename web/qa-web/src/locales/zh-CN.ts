// 中文语言资源（对照 Home.vue 模板逐条搬运）
// 结构：home.hero（介绍区）/ home.stats（统计区）/ home.rooms（开放诊室区）
// nav（顶部导航）/ footer（页脚）—— 站点共享外壳（范围扩展，用户确认）
export default {
  nav: {
    home: '首页',
    consultation: '问诊',
    doctors: '医生',
    about: '关于',
    doctorLogin: '医生登录',
    patientLogin: '问诊用户登录',
    hello: '你好，{name}',
    logout: '退出登录',
  },
  footer: {
    tagline: '专业的在线医疗问诊平台',
    subtitle: '为您提供便捷、专业的医疗咨询服务',
    quickLinks: '快速链接',
    links: {
      home: '首页',
      consultation: '问诊',
      doctors: '医生团队',
      about: '关于我们',
    },
    contact: '联系我们',
    hotline: '客服热线',
    serviceTime: '服务时间',
    serviceTimeValue: '周一至周日 8:00-22:00',
    email: '邮箱',
    legal: '法律信息',
    privacy: '隐私政策',
    terms: '服务条款',
    disclaimer: '医疗免责声明',
  },
  home: {
    hero: {
      title: '专业在线医疗问诊平台',
      subtitle: '连接专业医生与患者,提供便捷、高效的医疗咨询服务',
      features: {
        team: '专业医生团队',
        realtime: '实时在线问诊',
        privacy: '隐私安全保护',
      },
      actions: {
        consult: '立即问诊',
        doctors: '查看医生',
      },
    },
    stats: {
      doctors: '专业医生',
      questions: '问题总数',
      pending: '待响应问题',
      sessions: '在线诊室',
    },
    rooms: {
      title: '开放诊室',
      subtitle: '以下医生诊室正在开放,欢迎咨询',
      online: '在线',
      enter: '进入诊室',
    },
  },
};
