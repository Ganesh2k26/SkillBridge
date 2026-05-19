// ── LoadingSpinner ────────────────────────────────────────────────────────────
export function Spinner({ size = 'md', text = '' }) {
  const s = { sm:'w-5 h-5', md:'w-8 h-8', lg:'w-12 h-12' }[size];
  return (
    <div className="flex flex-col items-center gap-3">
      <div className={`${s} rounded-full border-[3px] border-brand-500/30 border-t-brand-500 animate-spin`}/>
      {text && <p className="text-gray-400 text-sm">{text}</p>}
    </div>
  );
}

export function FullPageSpinner({ text = 'Loading...' }) {
  return (
    <div className="fixed inset-0 bg-gray-950 flex items-center justify-center z-50">
      <Spinner size="lg" text={text}/>
    </div>
  );
}

// ── ProtectedRoute ─────────────────────────────────────────────────────────────
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <FullPageSpinner/>;
  if (!user)   return <Navigate to="/login" replace/>;
  return children;
}

// ── Empty State ───────────────────────────────────────────────────────────────
export function EmptyState({ icon: Icon, title, desc, action }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 text-center gap-4">
      <div className="w-16 h-16 rounded-2xl bg-gray-800 flex items-center justify-center">
        {Icon && <Icon size={28} className="text-gray-600"/>}
      </div>
      <div>
        <p className="text-gray-300 font-semibold">{title}</p>
        {desc && <p className="text-gray-500 text-sm mt-1">{desc}</p>}
      </div>
      {action}
    </div>
  );
}

// ── ScoreRing ────────────────────────────────────────────────────────────────
export function ScoreRing({ score = 0, size = 130 }) {
  const r = 46;
  const c = 2 * Math.PI * r;
  const offset = c - (Math.min(score, 100) / 100) * c;
  const color = score >= 75 ? '#10b981' : score >= 50 ? '#f59e0b' : '#ef4444';
  return (
    <svg width={size} height={size} viewBox="0 0 100 100" style={{ transform:'rotate(-90deg)' }}>
      <circle cx="50" cy="50" r={r} fill="none" stroke="#1f2937" strokeWidth="7"/>
      <circle cx="50" cy="50" r={r} fill="none" stroke={color} strokeWidth="7"
        strokeDasharray={c} strokeDashoffset={offset} strokeLinecap="round"
        style={{ transition:'stroke-dashoffset 1.2s cubic-bezier(0.4,0,0.2,1)' }}/>
    </svg>
  );
}

// ── StatCard ──────────────────────────────────────────────────────────────────
export function StatCard({ label, value, sub, icon: Icon, color = 'text-brand-400', bg = 'bg-brand-500/10' }) {
  return (
    <div className="stat-card animate-fade-in">
      <div className="flex items-start justify-between">
        <div className={`w-10 h-10 rounded-xl ${bg} flex items-center justify-center`}>
          {Icon && <Icon size={20} className={color}/>}
        </div>
      </div>
      <p className="text-2xl font-black text-white mt-2">{value ?? '—'}</p>
      <p className="text-gray-400 text-sm font-medium">{label}</p>
      {sub && <p className="text-gray-600 text-xs">{sub}</p>}
    </div>
  );
}
