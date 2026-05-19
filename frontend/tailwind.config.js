/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html","./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        brand: { 50:'#f0f4ff',100:'#e0eaff',400:'#6b8eff',500:'#4a6cf7',600:'#3451e8',700:'#2541c5',900:'#0f1a5c' },
        success: { 400:'#34d399',500:'#10b981' },
        warning: { 400:'#fbbf24',500:'#f59e0b' },
        danger:  { 400:'#f87171',500:'#ef4444' },
      },
      fontFamily: { sans: ['Inter var','Inter','system-ui','sans-serif'] },
      backgroundImage: {
        'hero-gradient': 'radial-gradient(ellipse 80% 60% at 50% -10%, rgba(74,108,247,0.25), transparent)',
        'card-gradient': 'linear-gradient(135deg, rgba(255,255,255,0.04), rgba(255,255,255,0.01))',
      },
      boxShadow: {
        'glow': '0 0 30px rgba(74,108,247,0.25)',
        'glow-sm': '0 0 12px rgba(74,108,247,0.15)',
        'card': '0 4px 24px rgba(0,0,0,0.4)',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'slide-up': 'slideUp 0.45s cubic-bezier(0.16,1,0.3,1)',
        'slide-right': 'slideRight 0.4s ease-out',
        'bounce-in': 'bounceIn 0.6s cubic-bezier(0.34,1.56,0.64,1)',
        'shimmer': 'shimmer 2s infinite linear',
        'ping-slow': 'ping 2s cubic-bezier(0,0,0.2,1) infinite',
      },
      keyframes: {
        fadeIn:    { '0%':{ opacity:'0' }, '100%':{ opacity:'1' } },
        slideUp:   { '0%':{ opacity:'0', transform:'translateY(24px)' }, '100%':{ opacity:'1', transform:'translateY(0)' } },
        slideRight:{ '0%':{ opacity:'0', transform:'translateX(-16px)' }, '100%':{ opacity:'1', transform:'translateX(0)' } },
        bounceIn:  { '0%':{ opacity:'0', transform:'scale(0.8)' }, '100%':{ opacity:'1', transform:'scale(1)' } },
        shimmer:   { '0%':{ backgroundPosition:'-500px 0' }, '100%':{ backgroundPosition:'500px 0' } },
      },
    },
  },
  plugins: [],
}
