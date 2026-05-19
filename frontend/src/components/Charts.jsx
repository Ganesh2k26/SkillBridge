// ── ProgressCard ──────────────────────────────────────────────────────────────
import clsx from 'clsx';
import { strengthPill } from '../utils/helpers';
import { ScoreRing } from './SharedComponents';
import { RadarChart, PolarGrid, PolarAngleAxis, Radar, ResponsiveContainer,
         BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Cell } from 'recharts';
import { Brain, Star, TrendingUp, TrendingDown } from 'lucide-react';

export function ProgressCard({ topic }) {
  const acc = topic.attempted === 0 ? 0 : Math.round(topic.correct / topic.attempted * 100);
  const pill = strengthPill(topic.strengthLevel);

  return (
    <div className="card p-4 flex items-center gap-4 animate-fade-in">
      <div className="flex-1 min-w-0">
        <div className="flex items-center gap-2 mb-1">
          <p className="text-gray-100 font-semibold text-sm truncate">{topic.topic}</p>
          <span className={clsx('badge text-[10px]', pill)}>{topic.strengthLevel}</span>
        </div>
        <p className="text-gray-500 text-xs">{topic.category} · {topic.attempted} attempted</p>
        <div className="h-1.5 bg-gray-800 rounded-full mt-2 overflow-hidden">
          <div className={clsx('h-full rounded-full transition-all duration-700',
            acc >= 75 ? 'bg-success-500' : acc >= 45 ? 'bg-warning-500' : 'bg-danger-500'
          )} style={{ width: `${acc}%` }}/>
        </div>
      </div>
      <div className="text-right flex-shrink-0">
        <p className={clsx('text-xl font-black',
          acc >= 75 ? 'text-success-400' : acc >= 45 ? 'text-warning-400' : 'text-danger-400'
        )}>{acc}%</p>
        <p className="text-gray-600 text-xs">{topic.correct}/{topic.attempted}</p>
      </div>
    </div>
  );
}

// ── ReadinessScore ─────────────────────────────────────────────────────────────
export function ReadinessScore({ score = 0, label = '', companyName = '' }) {
  return (
    <div className="card p-6 flex flex-col sm:flex-row items-center gap-6">
      <div className="relative flex-shrink-0">
        <ScoreRing score={score} size={140}/>
        <div className="absolute inset-0 flex flex-col items-center justify-center">
          <span className="text-4xl font-black text-white">{score}</span>
          <span className="text-xs text-gray-500">/ 100</span>
        </div>
      </div>
      <div className="text-center sm:text-left">
        {companyName && <p className="text-xs text-gray-500 uppercase tracking-wider mb-1">{companyName} Readiness</p>}
        <p className="text-2xl font-bold text-white">{label || 'Readiness Score'}</p>
        <div className="flex gap-3 mt-4 flex-wrap justify-center sm:justify-start">
          {[['Practice', '40%'],['Accuracy','30%'],['Consistency','20%'],['AI Feedback','10%']].map(([k,v]) => (
            <div key={k} className="text-center">
              <p className="text-xs text-gray-500">{k}</p>
              <p className="text-brand-400 font-bold text-sm">{v}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

// ── WeakAreaChart ──────────────────────────────────────────────────────────────
const CAT_COLORS = {
  Aptitude:'#a855f7', SQL:'#3b82f6', Java:'#f97316', DSA:'#06b6d4', HR:'#ec4899'
};

export function WeakAreaChart({ categoryBreakdown = [] }) {
  const data = categoryBreakdown.map(c => ({
    name: c.category,
    accuracy: Math.round(c.accuracy),
    attempted: c.attempted,
  }));

  if (data.length === 0) return (
    <div className="card p-6 flex items-center justify-center h-64 text-gray-600 text-sm">
      No data yet — start practicing!
    </div>
  );

  return (
    <div className="card p-6">
      <div className="flex items-center gap-2 mb-6">
        <TrendingUp size={18} className="text-brand-400"/>
        <h3 className="font-bold text-white">Category Accuracy</h3>
      </div>
      <ResponsiveContainer width="100%" height={200}>
        <BarChart data={data} margin={{ top:0, right:0, left:-20, bottom:0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#1f2937"/>
          <XAxis dataKey="name" tick={{ fill:'#6b7280', fontSize:12 }} axisLine={false} tickLine={false}/>
          <YAxis tick={{ fill:'#6b7280', fontSize:11 }} axisLine={false} tickLine={false} domain={[0,100]}/>
          <Tooltip contentStyle={{ background:'#111827', border:'1px solid #1f2937', borderRadius:12, fontSize:12 }}
            formatter={(v) => [`${v}%`, 'Accuracy']}/>
          <Bar dataKey="accuracy" radius={[6,6,0,0]} maxBarSize={48}>
            {data.map((entry, i) => (
              <Cell key={i} fill={CAT_COLORS[entry.name] || '#6366f1'}/>
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export function RadarProgressChart({ categoryBreakdown = [] }) {
  const data = categoryBreakdown.length > 0
    ? categoryBreakdown.map(c => ({ subject: c.category, value: Math.round(c.accuracy) }))
    : [
        { subject:'Aptitude', value:0 },
        { subject:'SQL',      value:0 },
        { subject:'Java',     value:0 },
        { subject:'DSA',      value:0 },
        { subject:'HR',       value:0 },
      ];

  return (
    <div className="card p-6">
      <div className="flex items-center gap-2 mb-4">
        <Brain size={18} className="text-purple-400"/>
        <h3 className="font-bold text-white">Skill Radar</h3>
      </div>
      <ResponsiveContainer width="100%" height={220}>
        <RadarChart data={data}>
          <PolarGrid stroke="#1f2937"/>
          <PolarAngleAxis dataKey="subject" tick={{ fill:'#6b7280', fontSize:12 }}/>
          <Radar dataKey="value" stroke="#6366f1" fill="#6366f1" fillOpacity={0.2} strokeWidth={2}/>
          <Tooltip contentStyle={{ background:'#111827', border:'1px solid #1f2937', borderRadius:12, fontSize:12 }}
            formatter={(v) => [`${v}%`, 'Accuracy']}/>
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
}

// ── StudyPlanCard ──────────────────────────────────────────────────────────────
export function StudyPlanCard({ plan }) {
  let parsed = null;
  try {
    let raw = plan.planContent?.trim() || '';
    if (raw.startsWith('```json')) raw = raw.slice(7);
    if (raw.startsWith('```'))     raw = raw.slice(3);
    if (raw.endsWith('```'))       raw = raw.slice(0, -3);
    parsed = JSON.parse(raw.trim());
  } catch {}

  if (!parsed) return (
    <div className="card p-6">
      <p className="text-gray-400 text-sm whitespace-pre-wrap">{plan.planContent}</p>
    </div>
  );

  return (
    <div className="space-y-4 animate-fade-in">
      {parsed.summary && (
        <div className="card p-5 border-l-4 border-brand-500">
          <p className="text-gray-300 text-sm leading-relaxed">{parsed.summary}</p>
        </div>
      )}
      <div className="space-y-3">
        {(parsed.days || []).map((day) => (
          <div key={day.day} className="card p-5">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-8 h-8 rounded-lg bg-brand-500/20 flex items-center justify-center text-brand-400 font-bold text-sm">
                {day.day}
              </div>
              <div>
                <p className="text-white font-semibold text-sm">{day.title}</p>
                <p className="text-gray-500 text-xs">{day.estimatedHours}h · {day.practiceCount} questions</p>
              </div>
            </div>
            <ul className="space-y-1.5">
              {(day.tasks || []).map((task, i) => (
                <li key={i} className="flex items-start gap-2 text-sm text-gray-400">
                  <span className="text-brand-400 mt-0.5 flex-shrink-0">›</span>
                  {task}
                </li>
              ))}
            </ul>
          </div>
        ))}
      </div>
      {parsed.tips && (
        <div className="card p-5 bg-warning-500/5 border-warning-500/20">
          <p className="text-warning-400 font-semibold text-sm mb-2">💡 Tips</p>
          <ul className="space-y-1">
            {parsed.tips.map((t, i) => <li key={i} className="text-gray-400 text-sm">• {t}</li>)}
          </ul>
        </div>
      )}
    </div>
  );
}

// ── AiFeedbackBox ──────────────────────────────────────────────────────────────
export function AiFeedbackBox({ feedback }) {
  if (!feedback) return null;
  const scoreColor = feedback.score >= 75 ? 'text-success-400' :
                     feedback.score >= 50 ? 'text-warning-400' : 'text-danger-400';
  const scoreBg    = feedback.score >= 75 ? 'bg-success-500/10' :
                     feedback.score >= 50 ? 'bg-warning-500/10' : 'bg-danger-500/10';

  return (
    <div className="space-y-4 animate-slide-up">
      {/* Score */}
      <div className={clsx('card p-5 flex items-center gap-4', scoreBg)}>
        <div className="text-center">
          <p className={clsx('text-4xl font-black', scoreColor)}>{feedback.score}</p>
          <p className="text-gray-500 text-xs">/ 100</p>
        </div>
        <div>
          <p className="text-white font-bold">AI Score</p>
          <p className="text-gray-400 text-sm mt-1">{feedback.questionTitle}</p>
        </div>
      </div>

      {/* Feedback */}
      {feedback.feedback && (
        <div className="card p-5">
          <p className="text-brand-400 text-xs font-semibold uppercase tracking-wider mb-2">Feedback</p>
          <p className="text-gray-300 text-sm leading-relaxed">{feedback.feedback}</p>
        </div>
      )}

      {/* Missing Points */}
      {feedback.missingPoints && (
        <div className="card p-5 border-danger-500/20">
          <p className="text-danger-400 text-xs font-semibold uppercase tracking-wider mb-2">Missing Points</p>
          <p className="text-gray-400 text-sm leading-relaxed">{feedback.missingPoints}</p>
        </div>
      )}

      {/* Improved Answer */}
      {feedback.improvedAnswer && (
        <div className="card p-5 border-success-500/20">
          <p className="text-success-400 text-xs font-semibold uppercase tracking-wider mb-2">Model Answer</p>
          <p className="text-gray-300 text-sm leading-relaxed whitespace-pre-wrap">{feedback.improvedAnswer}</p>
        </div>
      )}
    </div>
  );
}
