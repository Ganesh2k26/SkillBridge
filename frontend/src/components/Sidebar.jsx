import { NavLink, useLocation } from 'react-router-dom';
import { LayoutDashboard, Building2, BookOpen, Brain, CalendarDays, User2, ChevronRight } from 'lucide-react';
import clsx from 'clsx';

const links = [
  { to: '/dashboard',  icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/companies',  icon: Building2,        label: 'Companies'  },
  { to: '/practice',   icon: BookOpen,         label: 'Practice'   },
  { to: '/ai-feedback',icon: Brain,            label: 'AI Feedback'},
  { to: '/study-plan', icon: CalendarDays,     label: 'Study Plan' },
  { to: '/profile',    icon: User2,            label: 'Profile'    },
];

export default function Sidebar() {
  return (
    <aside className="w-60 flex-shrink-0 hidden lg:flex flex-col gap-1 py-6 px-3">
      <p className="text-[10px] font-semibold text-gray-600 uppercase tracking-widest px-4 mb-2">Navigation</p>
      {links.map(({ to, icon: Icon, label }) => (
        <NavLink key={to} to={to} className={({ isActive }) =>
          clsx('nav-item', isActive ? 'nav-item-active' : 'nav-item-idle')}>
          <Icon size={18} className="flex-shrink-0"/>
          <span className="flex-1">{label}</span>
          <ChevronRight size={14} className="opacity-30"/>
        </NavLink>
      ))}
    </aside>
  );
}
