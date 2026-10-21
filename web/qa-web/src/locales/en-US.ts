// English locale resources (key structure mirrors zh-CN.ts exactly)
// Structure: home.hero / home.stats / home.rooms
// nav / footer — site shell (scope extension, confirmed by user)
export default {
  nav: {
    home: 'Home',
    consultation: 'Consultation',
    doctors: 'Doctors',
    about: 'About',
    doctorLogin: 'Doctor Login',
    patientLogin: 'Patient Login',
    hello: 'Hi, {name}',
    logout: 'Logout',
  },
  footer: {
    tagline: 'Professional Online Medical Consultation Platform',
    subtitle: 'Convenient, professional medical consultation services for you',
    quickLinks: 'Quick Links',
    links: {
      home: 'Home',
      consultation: 'Consultation',
      doctors: 'Our Doctors',
      about: 'About Us',
    },
    contact: 'Contact Us',
    hotline: 'Hotline',
    serviceTime: 'Service Hours',
    serviceTimeValue: 'Mon-Sun 8:00-22:00',
    email: 'Email',
    legal: 'Legal',
    privacy: 'Privacy Policy',
    terms: 'Terms of Service',
    disclaimer: 'Medical Disclaimer',
  },
  home: {
    hero: {
      title: 'Professional Online Medical Consultation Platform',
      subtitle:
        'Connecting professional doctors and patients with convenient, efficient medical consultation services',
      features: {
        team: 'Professional Medical Team',
        realtime: 'Real-time Online Consultation',
        privacy: 'Privacy & Security Protection',
      },
      actions: {
        consult: 'Consult Now',
        doctors: 'View Doctors',
      },
    },
    stats: {
      doctors: 'Professional Doctors',
      questions: 'Total Questions',
      pending: 'Pending Questions',
      sessions: 'Online Rooms',
    },
    rooms: {
      title: 'Open Consultation Rooms',
      subtitle: "The following doctors' rooms are open — consultations welcome",
      online: 'Online',
      enter: 'Enter Room',
    },
  },
};
