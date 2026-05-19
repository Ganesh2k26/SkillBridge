import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Target, Bell, User, LogOut } from 'lucide-react';
import { useState } from 'react';

export default function Navbar() {
  const { user, logout } = useAuth();
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-gray-800/60"
      style={{ background: 'rgba(3,7,18,0.85)', backdropFilter: 'blur(20px)' }}>
      <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between">
        {/* Logo */}
        <Link to="/" className="flex items-center gap-2.5 group">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center shadow-glow-sm group-hover:scale-105 transition-transform">
            <Target size={18} className="text-white" />
          </div>
          <div className="hidden sm:block">
            <span className="font-bold text-white text-lg leading-none">SkillBridge</span>
            <p className="text-[10px] text-gray-500 leading-none mt-0.5">AI Placement Prep</p>
          </div>
        </Link>

        {/* Right */}
        {user ? (
          <div className="flex items-center gap-2">
            <div className="hidden md:flex items-center gap-2 px-3 py-1.5 rounded-xl bg-gray-800 border border-gray-700">
              <div className="w-7 h-7 rounded-lg bg-gradient-to-br from-brand-500 to-purple-500 flex items-center justify-center">
                <User size={13} className="text-white" />
              </div>
              <div>
                <p className="text-xs font-semibold text-gray-200 leading-none">{user.name}</p>
                {user.targetCompany && <p className="text-[10px] text-gray-500 mt-0.5">🎯 {user.targetCompany}</p>}
              </div>
            </div>
            <button onClick={logout}
              className="btn btn-ghost text-sm gap-1.5 text-gray-400 hover:text-danger-400">
              <LogOut size={16}/><span className="hidden sm:block">Logout</span>
            </button>
          </div>
        ) : (
          <div className="flex items-center gap-2">
            <Link to="/login"    className="btn btn-secondary btn-sm">Login</Link>
            <Link to="/register" className="btn btn-primary  btn-sm">Get Started</Link>
          </div>
        )}
      </div>
    </header>
  );
}
