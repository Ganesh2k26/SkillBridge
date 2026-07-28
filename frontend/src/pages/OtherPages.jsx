import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import API from '../api/axiosConfig';
import { useAuth } from '../context/AuthContext';
import { AiFeedbackBox, StudyPlanCard } from '../components/Charts';
import { Spinner, EmptyState } from '../components/SharedComponents';
import { getApiErrorMessage } from '../utils/apiError';
import { Brain, ChevronDown, ChevronUp, CalendarDays, Wand2, AlertCircle, RefreshCw, Target, BookOpen } from 'lucide-react';
import { formatDate } from '../utils/helpers';
import toast from 'react-hot-toast';
import clsx from 'clsx';

function ErrorBanner({ message, onRetry }) {
  return (
    <div className="card p-5 border-l-4 border-danger-500 bg-danger-500/5 flex items-start gap-4">
      <AlertCircle size={20} className="text-danger-400 flex-shrink-0 mt-0.5" />
      <div className="flex-1 min-w-0">
        <p className="text-danger-300 font-semibold text-sm">Something went wrong</p>
        <p className="text-gray-400 text-sm mt-0.5">{message}</p>
      </div>
      {onRetry && (
        <button onClick={onRetry} className="btn btn-ghost p-2 flex-shrink-0">
          <RefreshCw size={16} className="text-gray-400" />
        </button>
      )}
    </div>
  );
}

// ── AI Feedback Page ──────────────────────────────────────────────────────────
export function AiFeedback() {
  const [feedbacks, setFeedbacks] = useState([]);
  const [busy, setBusy]           = useState(true);
  const [error, setError]         = useState(null);
  const [open, setOpen]           = useState(null);

  const load = () => {
    setBusy(true);
    setError(null);
    API.get('/ai/feedback')
      .then(r => { setFeedbacks(r.data); if (r.data.length > 0) setOpen(r.data[0].id); })
      .catch(err => setError(getApiErrorMessage(err, 'Could not load feedback history.')))
      .finally(() => setBusy(false));
  };

  useEffect(() => { load(); }, []);

  if (busy) return (
    <div className="flex justify-center py-24">
      <Spinner size="lg" text="Loading feedback..." />
    </div>
  );

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="section-title">AI Feedback History</h1>
        <p className="section-sub">Review AI evaluations of your past answers</p>
      </div>

      {error && <ErrorBanner message={error} onRetry={load} />}

      {!error && feedbacks.length === 0 && (
        <EmptyState
          icon={Brain}
          title="No AI feedback yet"
          desc="Submit an answer on a question page and click 'Get AI Feedback'"
          action={<a href="/companies" className="btn btn-primary btn-sm">Start Practicing</a>}
        />
      )}

      {feedbacks.length > 0 && (
        <div className="space-y-3">
          {feedbacks.map(fb => {
            const isOpen = open === fb.id;
            const scoreColor =
              fb.score >= 75 ? 'text-success-400' :
              fb.score >= 50 ? 'text-warning-400' : 'text-danger-400';
            return (
              <div key={fb.id} className="card overflow-hidden">
                <button
                  onClick={() => setOpen(isOpen ? null : fb.id)}
                  className="w-full p-4 flex items-center gap-4 text-left hover:bg-gray-800/40 transition-colors"
                >
                  <div className="w-12 h-12 rounded-xl bg-gray-800 flex items-center justify-center flex-shrink-0">
                    <span className={clsx('text-xl font-black', scoreColor)}>{fb.score}</span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-gray-100 font-medium text-sm truncate">{fb.questionTitle}</p>
                    <p className="text-gray-500 text-xs mt-0.5">{formatDate(fb.createdAt)}</p>
                  </div>
                  {isOpen
                    ? <ChevronUp size={18} className="text-gray-500" />
                    : <ChevronDown size={18} className="text-gray-500" />
                  }
                </button>
                {isOpen && (
                  <div className="px-4 pb-4 border-t border-gray-800 pt-4">
                    <AiFeedbackBox feedback={fb} />
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

// ── Study Plan Page ───────────────────────────────────────────────────────────
export function StudyPlan() {
  const [plans, setPlans]             = useState([]);
  const [generating, setGenerating]   = useState(false);
  const [loaded, setLoaded]           = useState(false);
  const [loadError, setLoadError]     = useState(null);
  const [genError, setGenError]       = useState(null);
  const [open, setOpen]               = useState(null);
  const [form, setForm]               = useState({
    companyName: 'TCS',
    planDays: 7,
    weakTopics: '',
  });

  const COMPANIES = [
    'TCS', 'Infosys', 'Wipro', 'Zoho', 'Amazon',
    'Accenture', 'Cognizant', 'Capgemini',
  ];

  const DAY_OPTIONS = [3, 5, 7, 10, 14, 21, 30];

  const loadPlans = () => {
    setLoadError(null);
    API.get('/ai/study-plans')
      .then(r => {
        setPlans(r.data);
        if (r.data.length > 0) setOpen(r.data[0].id);
        setLoaded(true);
      })
      .catch(err => setLoadError(getApiErrorMessage(err, 'Could not load your plans.')));
  };

  useEffect(() => { if (!loaded) loadPlans(); }, [loaded]);

  const generate = async () => {
    // Validate weak topics input
    const weakTopics = form.weakTopics
      ? form.weakTopics.split(',').map(s => s.trim()).filter(Boolean)
      : [];

    setGenerating(true);
    setGenError(null);
    try {
      const payload = {
        companyName: form.companyName,
        planDays: Number(form.planDays),
        weakTopics,          // empty list = server fetches from user's history
      };
      const { data } = await API.post('/ai/study-plan', payload);
      setPlans(prev => [data, ...prev]);
      setOpen(data.id);
      toast.success('Study plan generated! 📅');
    } catch (err) {
      const msg = getApiErrorMessage(err, 'Generation failed.');
      setGenError(msg);
      toast.error(msg);
    } finally {
      setGenerating(false);
    }
  };

  // Hint text based on form values
  const topicHint = form.weakTopics.trim()
    ? `${form.weakTopics.split(',').filter(t => t.trim()).length} topic(s) specified`
    : 'Leave blank to auto-detect from your practice history';

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="section-title">AI Study Planner</h1>
        <p className="section-sub">Generate a personalised day-by-day preparation plan based on your profile</p>
      </div>

      {/* Generator card */}
      <div className="card p-6 border-brand-500/20 bg-brand-500/5">
        <div className="flex items-center gap-2 mb-5">
          <Wand2 size={18} className="text-brand-400" />
          <h3 className="font-bold text-white">Generate New Plan</h3>
        </div>

        <div className="grid sm:grid-cols-3 gap-4 mb-2">
          {/* Company */}
          <div>
            <label className="label flex items-center gap-1.5">
              <Target size={12} className="text-brand-400" /> Target Company
            </label>
            <select
              className="input"
              value={form.companyName}
              onChange={e => setForm({ ...form, companyName: e.target.value })}
            >
              {COMPANIES.map(c => <option key={c}>{c}</option>)}
            </select>
          </div>

          {/* Duration */}
          <div>
            <label className="label flex items-center gap-1.5">
              <CalendarDays size={12} className="text-brand-400" /> Plan Duration
            </label>
            <select
              className="input"
              value={form.planDays}
              onChange={e => setForm({ ...form, planDays: e.target.value })}
            >
              {DAY_OPTIONS.map(d => (
                <option key={d} value={d}>{d} Days</option>
              ))}
            </select>
          </div>

          {/* Weak Topics */}
          <div>
            <label className="label flex items-center gap-1.5">
              <BookOpen size={12} className="text-brand-400" /> Weak Topics
            </label>
            <input
              className="input"
              placeholder="SQL Joins, OOP, Arrays…"
              value={form.weakTopics}
              onChange={e => setForm({ ...form, weakTopics: e.target.value })}
            />
          </div>
        </div>

        {/* Hint */}
        <p className="text-gray-600 text-xs mb-5 pl-0.5">{topicHint}</p>

        {genError && (
          <div className="mb-4 p-3 rounded-xl bg-danger-500/10 border border-danger-500/20 flex items-start gap-2">
            <AlertCircle size={15} className="text-danger-400 flex-shrink-0 mt-0.5" />
            <p className="text-danger-300 text-sm">{genError}</p>
          </div>
        )}

        <button
          onClick={generate}
          disabled={generating}
          className="btn btn-primary"
        >
          {generating ? <Spinner size="sm" /> : <Wand2 size={16} />}
          {generating ? 'Generating with Gemini AI…' : 'Generate Plan'}
        </button>
      </div>

      {/* Plans list */}
      {loadError && <ErrorBanner message={loadError} onRetry={loadPlans} />}

      {!loadError && loaded && plans.length === 0 && (
        <EmptyState
          icon={CalendarDays}
          title="No plans yet"
          desc="Generate your first personalised study plan above"
        />
      )}

      {plans.length > 0 && (
        <div className="space-y-3">
          <h3 className="font-bold text-gray-300 text-sm uppercase tracking-wider">Your Plans</h3>
          {plans.map(p => (
            <div key={p.id} className="card overflow-hidden">
              <button
                onClick={() => setOpen(open === p.id ? null : p.id)}
                className="w-full p-4 flex items-center gap-4 text-left hover:bg-gray-800/40 transition-colors"
              >
                <div className="w-10 h-10 rounded-xl bg-brand-500/10 flex items-center justify-center text-brand-400 font-bold flex-shrink-0">
                  {p.planDays}d
                </div>
                <div className="flex-1">
                  <p className="text-white font-semibold">{p.companyName} · {p.planDays}-Day Plan</p>
                  <p className="text-gray-500 text-xs">{formatDate(p.createdAt)}</p>
                </div>
                {p.readinessScore != null && (
                  <span className={clsx(
                    'text-xs font-semibold px-2 py-1 rounded-lg',
                    p.readinessScore >= 70 ? 'bg-success-500/10 text-success-400' :
                    p.readinessScore >= 40 ? 'bg-warning-500/10 text-warning-400' :
                                            'bg-danger-500/10 text-danger-400'
                  )}>
                    {p.readinessScore}%
                  </span>
                )}
                {open === p.id
                  ? <ChevronUp size={18} className="text-gray-500" />
                  : <ChevronDown size={18} className="text-gray-500" />
                }
              </button>
              {open === p.id && (
                <div className="px-4 pb-4 border-t border-gray-800 pt-4">
                  <StudyPlanCard plan={p} />
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

// ── Profile Page ──────────────────────────────────────────────────────────────
export function Profile() {
  const { user }             = useAuth();
  const [data, setData]      = useState(null);
  const [busy, setBusy]      = useState(true);
  const [error, setError]    = useState(null);

  useEffect(() => {
    API.get('/dashboard/summary')
      .then(r => setData(r.data))
      .catch(err => setError(getApiErrorMessage(err, 'Could not load profile data.')))
      .finally(() => setBusy(false));
  }, []);

  const row = (label, value) => (
    <div className="flex justify-between py-3 border-b border-gray-800 last:border-0">
      <span className="text-gray-500 text-sm">{label}</span>
      <span className="text-gray-200 text-sm font-medium">{value || '—'}</span>
    </div>
  );

  return (
    <div className="max-w-2xl space-y-6 animate-fade-in">
      <h1 className="section-title">Profile</h1>

      {error && <ErrorBanner message={error} />}

      <div className="card p-6">
        <div className="flex items-center gap-5 mb-6">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-brand-500 to-purple-500 flex items-center justify-center text-2xl font-black text-white">
            {user?.name?.charAt(0)}
          </div>
          <div>
            <p className="text-xl font-bold text-white">{user?.name}</p>
            <p className="text-gray-400 text-sm">{user?.email}</p>
            <span className="badge badge-brand mt-1">{user?.role}</span>
          </div>
        </div>
        {busy
          ? <div className="flex justify-center py-6"><Spinner size="sm" text="Loading stats…" /></div>
          : <>
              {row('Target Company', user?.targetCompany)}
              {row('Readiness Score', data ? `${data.readinessScore}%` : '—')}
              {row('Total Attempted', data?.totalAttempted)}
              {row('Accuracy', data ? `${data.accuracyPercent}%` : '—')}
              {row('Correct Answers', data?.totalCorrect)}
            </>
        }
      </div>
    </div>
  );
}
