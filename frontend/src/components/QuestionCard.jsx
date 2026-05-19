import { CheckCircle2, Clock, AlertCircle, Circle, ChevronRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import clsx from 'clsx';
import { diffBadge, categoryColor } from '../utils/helpers';

const STATUS_CONFIG = {
  CORRECT:        { icon: CheckCircle2, color: 'text-success-400', bg: 'bg-success-500/10', label: 'Correct' },
  NEEDS_REVISION: { icon: AlertCircle,  color: 'text-warning-400', bg: 'bg-warning-500/10', label: 'Review' },
  ATTEMPTED:      { icon: Clock,        color: 'text-blue-400',    bg: 'bg-blue-500/10',    label: 'Attempted' },
  NOT_STARTED:    { icon: Circle,       color: 'text-gray-600',    bg: 'bg-gray-800',        label: 'Not Started' },
};

export default function QuestionCard({ question, index }) {
  const cfg = STATUS_CONFIG[question.status] || STATUS_CONFIG.NOT_STARTED;
  const StatusIcon = cfg.icon;

  return (
    <Link to={`/question/${question.id}`}
      className="card-hover p-4 flex items-center gap-4 group animate-fade-in block">
      {/* Index */}
      <div className="w-8 h-8 rounded-lg bg-gray-800 flex items-center justify-center text-gray-500 text-xs font-bold flex-shrink-0">
        {String(index + 1).padStart(2, '0')}
      </div>

      {/* Content */}
      <div className="flex-1 min-w-0">
        <p className="text-gray-100 font-medium text-sm leading-snug line-clamp-1 group-hover:text-white transition-colors">
          {question.title}
        </p>
        <div className="flex items-center gap-2 mt-1.5 flex-wrap">
          <span className={clsx('badge text-[11px]', categoryColor(question.category))}>
            {question.category}
          </span>
          <span className={clsx('badge text-[11px]', diffBadge(question.difficulty))}>
            {question.difficulty}
          </span>
          <span className="text-gray-600 text-[11px]">{question.topic}</span>
        </div>
      </div>

      {/* Status + Points */}
      <div className="flex items-center gap-3 flex-shrink-0">
        <div className={clsx('flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-medium', cfg.bg, cfg.color)}>
          <StatusIcon size={12}/>
          <span className="hidden sm:block">{cfg.label}</span>
        </div>
        <span className="text-xs text-gray-600 hidden md:block">+{question.points}pts</span>
        <ChevronRight size={16} className="text-gray-700 group-hover:text-brand-400 group-hover:translate-x-0.5 transition-all"/>
      </div>
    </Link>
  );
}
