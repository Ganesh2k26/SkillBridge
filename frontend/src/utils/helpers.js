// tokenUtils.js
export const getToken  = () => localStorage.getItem('sb_token');
export const clearAuth = () => localStorage.clear();

// scoreUtils.js
export const scoreColor = (score) => {
  if (score >= 80) return 'text-success-400';
  if (score >= 60) return 'text-warning-400';
  return 'text-danger-400';
};

export const scoreLabel = (score) => {
  if (score >= 85) return 'Excellent';
  if (score >= 70) return 'Good';
  if (score >= 50) return 'Average';
  return 'Needs Work';
};

export const diffBadge = (diff) => {
  if (!diff) return 'badge-gray';
  const d = diff.toLowerCase();
  if (d === 'easy')   return 'badge-easy';
  if (d === 'medium') return 'badge-medium';
  return 'badge-hard';
};

export const strengthPill = (level) => {
  if (level === 'STRONG') return 'pill-strong';
  if (level === 'MEDIUM') return 'pill-medium';
  return 'pill-weak';
};

export const categoryColor = (cat) => {
  const map = {
    Aptitude: 'bg-purple-500/10 text-purple-400 border-purple-500/20',
    SQL:      'bg-blue-500/10   text-blue-400   border-blue-500/20',
    Java:     'bg-orange-500/10 text-orange-400 border-orange-500/20',
    DSA:      'bg-cyan-500/10   text-cyan-400   border-cyan-500/20',
    HR:       'bg-pink-500/10   text-pink-400   border-pink-500/20',
  };
  return map[cat] || 'bg-gray-700/50 text-gray-400 border-gray-700';
};

export const formatDate = (dt) =>
  dt ? new Date(dt).toLocaleDateString('en-IN', { day:'2-digit', month:'short', year:'numeric' }) : '—';
