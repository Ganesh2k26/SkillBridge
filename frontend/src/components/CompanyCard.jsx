import { Building2, ChevronRight, Zap, TrendingUp } from 'lucide-react';
import { Link } from 'react-router-dom';
import clsx from 'clsx';

const LOGOS = {
  TCS: '🔵', Infosys: '🟣', Wipro: '🟢', Zoho: '🔴',
  Amazon: '🟠', Accenture: '🟤', Cognizant: '⚫', Capgemini: '🔷',
};

const DIFF_STYLES = {
  Easy:   'badge-easy',
  Medium: 'badge-medium',
  Hard:   'badge-hard',
};

export default function CompanyCard({ company, readiness }) {
  const emoji = LOGOS[company.name] || '🏢';

  return (
    <Link to={`/practice?company=${company.id}`}
      className="card-hover p-6 flex flex-col gap-4 cursor-pointer group animate-slide-up block">
      {/* Header */}
      <div className="flex items-start justify-between">
        <div className="flex items-center gap-3">
          <div className="w-12 h-12 rounded-2xl bg-gray-800 border border-gray-700 flex items-center justify-center text-2xl group-hover:scale-110 transition-transform">
            {emoji}
          </div>
          <div>
            <h3 className="font-bold text-white text-lg leading-tight">{company.name}</h3>
            <span className={clsx('badge mt-1', DIFF_STYLES[company.difficultyLevel] || 'badge-gray')}>
              {company.difficultyLevel}
            </span>
          </div>
        </div>
        <ChevronRight size={18} className="text-gray-600 group-hover:text-brand-400 group-hover:translate-x-1 transition-all mt-1"/>
      </div>

      {/* Description */}
      <p className="text-gray-400 text-sm line-clamp-2 leading-relaxed">{company.description}</p>

      {/* Stats */}
      <div className="grid grid-cols-2 gap-3">
        <div className="bg-gray-800/60 rounded-xl p-3">
          <p className="text-xs text-gray-500 mb-1">Questions</p>
          <p className="text-white font-bold text-lg">{company.questionCount || 0}</p>
        </div>
        <div className="bg-gray-800/60 rounded-xl p-3">
          <p className="text-xs text-gray-500 mb-1">Avg Package</p>
          <p className="text-white font-bold text-sm">{company.avgPackage || 'N/A'}</p>
        </div>
      </div>

      {/* Readiness bar */}
      {readiness !== undefined && (
        <div>
          <div className="flex justify-between text-xs mb-1.5">
            <span className="text-gray-500">Your Readiness</span>
            <span className={clsx('font-semibold',
              readiness >= 70 ? 'text-success-400' :
              readiness >= 40 ? 'text-warning-400' : 'text-danger-400'
            )}>{readiness}%</span>
          </div>
          <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
            <div className={clsx('h-full rounded-full transition-all duration-1000',
              readiness >= 70 ? 'bg-success-500' :
              readiness >= 40 ? 'bg-warning-500' : 'bg-danger-500'
            )} style={{ width: `${readiness}%` }}/>
          </div>
        </div>
      )}

      {/* Footer */}
      <div className="flex items-center gap-2 pt-1 border-t border-gray-800">
        <Zap size={13} className="text-brand-400"/>
        <p className="text-xs text-gray-500 truncate">{company.testPattern}</p>
      </div>
    </Link>
  );
}
