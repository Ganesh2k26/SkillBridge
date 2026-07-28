import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import API from '../api/axiosConfig';
import { StatCard, ScoreRing, Spinner, EmptyState } from '../components/SharedComponents';
import { WeakAreaChart, RadarProgressChart, ProgressCard } from '../components/Charts';
import { TrendingUp, Target, CheckCircle, Zap, BookOpen, Brain, ArrowRight, AlertCircle, RefreshCw } from 'lucide-react';
import { Link } from 'react-router-dom';
import { scoreLabel, scoreColor, strengthPill } from '../utils/helpers';
import { getApiErrorMessage } from '../utils/apiError';
import clsx from 'clsx';

export default function Dashboard() {
  const { user }           = useAuth();
  const [data, setData]     = useState(null);
  const [busy, setBusy]     = useState(true);
  const [error, setError]   = useState(null);

  const load = () => {
    setBusy(true);
    setError(null);
    API.get('/dashboard/summary')
      .then(r => setData(r.data))
      .catch(err => setError(getApiErrorMessage(err, 'Could not load dashboard data.')))
      .finally(() => setBusy(false));
  };

  useEffect(() => { load(); }, []);

  if (busy) return (
    <div className="flex items-center justify-center min-h-[60vh]">
      <Spinner size="lg" text="Loading your dashboard..." />
    </div>
  );

  if (error) return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">
            Hey, <span className="text-gradient">{user?.name?.split(' ')[0]}</span> 👋
          </h1>
          <p className="text-gray-400 text-sm mt-1">Let's get you back on track.</p>
        </div>
      </div>
      <div className="card p-5 border-l-4 border-danger-500 bg-danger-500/5 flex items-start gap-4">
        <AlertCircle size={20} className="text-danger-400 flex-shrink-0 mt-0.5" />
        <div className="flex-1">
          <p className="text-danger-300 font-semibold text-sm">Dashboard failed to load</p>
          <p className="text-gray-400 text-sm mt-0.5">{error}</p>
        </div>
        <button onClick={load} className="btn btn-ghost p-2 flex-shrink-0">
          <RefreshCw size={16} className="text-gray-400" />
        </button>
      </div>
      <div className="flex gap-3">
        <Link to="/companies" className="btn btn-primary">
          Start Practicing <ArrowRight size={15} />
        </Link>
        <Link to="/study-plan" className="btn btn-secondary">
          <Brain size={14} /> Study Plan
        </Link>
      </div>
    </div>
  );

  const rs = data?.readinessScore ?? 0;

  return (
    <div className="space-y-8 animate-fade-in">
      {/* Welcome */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">
            Hey, <span className="text-gradient">{user?.name?.split(' ')[0]}</span> 👋
          </h1>
          <p className="text-gray-400 text-sm mt-1">
            {user?.targetCompany
              ? `Preparing for ${user.targetCompany} · Keep it up!`
              : 'Pick a target company and start practicing'}
          </p>
        </div>
        <Link to="/companies" className="btn btn-primary btn-sm self-start">
          Practice Now <ArrowRight size={15} />
        </Link>
      </div>

      {/* Top Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard label="Total Attempted" value={data?.totalAttempted ?? 0}
          icon={BookOpen} color="text-brand-400" bg="bg-brand-500/10" />
        <StatCard label="Correct Answers" value={data?.totalCorrect ?? 0}
          icon={CheckCircle} color="text-success-400" bg="bg-success-500/10" />
        <StatCard label="Accuracy" value={`${data?.accuracyPercent ?? 0}%`}
          icon={TrendingUp} color="text-warning-400" bg="bg-warning-500/10" />
        <StatCard label="Readiness Score" value={`${rs}%`}
          icon={Target} color="text-purple-400" bg="bg-purple-500/10" />
      </div>

      {/* Readiness + Charts */}
      <div className="grid lg:grid-cols-2 gap-6">
        {/* Readiness Ring */}
        <div className="card p-6 flex items-center gap-6">
          <div className="relative flex-shrink-0">
            <ScoreRing score={rs} size={130} />
            <div className="absolute inset-0 flex flex-col items-center justify-center">
              <span className="text-3xl font-black text-white">{rs}</span>
              <span className="text-[10px] text-gray-500">/ 100</span>
            </div>
          </div>
          <div>
            <p className="text-xs text-gray-500 uppercase tracking-wider mb-1">Overall Readiness</p>
            <p className={clsx('text-xl font-bold', scoreColor(rs))}>{scoreLabel(rs)}</p>
            <p className="text-gray-400 text-sm mt-2 leading-relaxed">
              {rs < 50
                ? "You're just getting started. Keep practicing consistently!"
                : rs < 75
                ? 'Good progress! Focus on your weak areas to score higher.'
                : "Excellent! You're nearly placement-ready. 🎉"}
            </p>
            <Link to="/study-plan" className="btn btn-secondary btn-sm mt-4">
              <Brain size={14} /> Generate Study Plan
            </Link>
          </div>
        </div>

        <RadarProgressChart categoryBreakdown={data?.categoryBreakdown || []} />
      </div>

      {/* Category + Weak Areas */}
      <div className="grid lg:grid-cols-2 gap-6">
        <WeakAreaChart categoryBreakdown={data?.categoryBreakdown || []} />

        {/* Weak Areas */}
        <div className="card p-6">
          <div className="flex items-center gap-2 mb-5">
            <Zap size={18} className="text-danger-400" />
            <h3 className="font-bold text-white">Weak Areas</h3>
            <span className="badge badge-gray ml-auto">{data?.weakAreas?.length ?? 0}</span>
          </div>
          {!data?.weakAreas?.length ? (
            <EmptyState
              icon={CheckCircle}
              title="No weak areas yet!"
              desc="Start practicing to see your analytics"
            />
          ) : (
            <div className="space-y-3">
              {data.weakAreas.slice(0, 5).map((t, i) => (
                <ProgressCard key={i} topic={t} />
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Strong Areas */}
      {data?.strongAreas?.length > 0 && (
        <div className="card p-6">
          <div className="flex items-center gap-2 mb-5">
            <CheckCircle size={18} className="text-success-400" />
            <h3 className="font-bold text-white">Strong Areas</h3>
          </div>
          <div className="flex flex-wrap gap-2">
            {data.strongAreas.map((t, i) => (
              <span key={i} className="pill-strong badge">
                {t.topic} · {t.category}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
