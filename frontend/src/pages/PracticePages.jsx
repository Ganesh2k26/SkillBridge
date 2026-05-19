import { useEffect, useState, useCallback } from 'react';
import { useSearchParams, useParams, useNavigate } from 'react-router-dom';
import API from '../api/axiosConfig';
import QuestionCard from '../components/QuestionCard';
import CompanyCard from '../components/CompanyCard';
import { Spinner, EmptyState } from '../components/SharedComponents';
import { AiFeedbackBox } from '../components/Charts';
import { Building2, Filter, Search, CheckCircle2, XCircle, ArrowLeft, Send, Brain } from 'lucide-react';
import { categoryColor, diffBadge } from '../utils/helpers';
import toast from 'react-hot-toast';
import clsx from 'clsx';

// ── Companies ─────────────────────────────────────────────────────────────────
export function Companies() {
  const [companies, setCompanies] = useState([]);
  const [readiness, setReadiness] = useState({});
  const [busy, setBusy] = useState(true);

  useEffect(() => {
    API.get('/companies').then(async r => {
      setCompanies(r.data);
      const scores = {};
      await Promise.all(r.data.map(async c => {
        try { const s = await API.get(`/dashboard/readiness/${c.id}`); scores[c.id] = s.data.score; }
        catch { scores[c.id] = 0; }
      }));
      setReadiness(scores);
    }).finally(() => setBusy(false));
  }, []);

  if (busy) return <div className="flex justify-center py-24"><Spinner size="lg" text="Loading companies..."/></div>;

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="section-title">Choose Your Target Company</h1>
        <p className="section-sub">Practice company-specific questions and track your readiness</p>
      </div>
      {companies.length === 0
        ? <EmptyState icon={Building2} title="No companies yet" desc="Check back soon!"/>
        : <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {companies.map(c => <CompanyCard key={c.id} company={c} readiness={readiness[c.id]}/>)}
          </div>}
    </div>
  );
}

// ── Practice ──────────────────────────────────────────────────────────────────
export function Practice() {
  const [params] = useSearchParams();
  const [questions, setQuestions] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(params.get('company') || '');
  const [category, setCategory] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [search, setSearch] = useState('');
  const [busy, setBusy] = useState(false);

  const CATS  = ['', 'Aptitude', 'SQL', 'Java', 'DSA', 'HR'];
  const DIFFS = ['', 'Easy', 'Medium', 'Hard'];

  const fetchQ = useCallback(async () => {
    if (!selectedCompany) { setQuestions([]); return; }
    setBusy(true);
    try {
      const q = new URLSearchParams();
      if (category)   q.set('category', category);
      if (difficulty) q.set('difficulty', difficulty);
      const r = await API.get(`/questions/company/${selectedCompany}?${q}`);
      setQuestions(r.data);
    } catch {} finally { setBusy(false); }
  }, [selectedCompany, category, difficulty]);

  useEffect(() => { API.get('/companies').then(r => setCompanies(r.data)); }, []);
  useEffect(() => { fetchQ(); }, [fetchQ]);

  const filtered = questions.filter(q =>
    !search || q.title.toLowerCase().includes(search.toLowerCase()) ||
    q.topic.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="space-y-6 animate-fade-in">
      <div>
        <h1 className="section-title">Practice Questions</h1>
        <p className="section-sub">Filter by company, category and difficulty</p>
      </div>
      <div className="card p-4 flex flex-wrap gap-3 items-center">
        <Filter size={16} className="text-gray-500"/>
        <select className="input w-auto min-w-[140px]" value={selectedCompany} onChange={e => setSelectedCompany(e.target.value)}>
          <option value="">Select Company</option>
          {companies.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <select className="input w-auto" value={category} onChange={e => setCategory(e.target.value)}>
          {CATS.map(c => <option key={c} value={c}>{c || 'All Categories'}</option>)}
        </select>
        <select className="input w-auto" value={difficulty} onChange={e => setDifficulty(e.target.value)}>
          {DIFFS.map(d => <option key={d} value={d}>{d || 'All Levels'}</option>)}
        </select>
        <div className="relative ml-auto">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500"/>
          <input className="input pl-9 w-52" placeholder="Search questions..." value={search} onChange={e => setSearch(e.target.value)}/>
        </div>
      </div>
      {questions.length > 0 && (
        <div className="flex gap-3 flex-wrap">
          <span className="badge badge-easy">{questions.filter(q=>q.status==='CORRECT').length} correct</span>
          <span className="badge badge-medium">{questions.filter(q=>q.status==='NEEDS_REVISION').length} needs revision</span>
          <span className="badge badge-gray">{questions.filter(q=>q.status==='NOT_STARTED').length} not started</span>
        </div>
      )}
      {!selectedCompany
        ? <div className="flex flex-col items-center justify-center py-20 text-center">
            <Building2 size={40} className="text-gray-700 mb-4"/>
            <p className="text-gray-400 font-medium">Select a company to load questions</p>
          </div>
        : busy ? <div className="flex justify-center py-16"><Spinner size="md" text="Loading..."/></div>
        : filtered.length === 0 ? <EmptyState icon={Search} title="No questions found" desc="Try changing your filters"/>
        : <div className="space-y-2">{filtered.map((q, i) => <QuestionCard key={q.id} question={q} index={i}/>)}</div>
      }
    </div>
  );
}

// ── Question Detail ───────────────────────────────────────────────────────────
export function QuestionDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [question, setQuestion] = useState(null);
  const [answer, setAnswer]     = useState('');
  const [result, setResult]     = useState(null);
  const [feedback, setFeedback] = useState(null);
  const [submitBusy, setSubmitBusy] = useState(false);
  const [aiBusy, setAiBusy]         = useState(false);
  const [busy, setBusy]             = useState(true);

  useEffect(() => {
    API.get(`/questions/${id}`).then(r => setQuestion(r.data)).finally(() => setBusy(false));
  }, [id]);

  const handleSubmit = async () => {
    if (!answer.trim()) return toast.error('Enter your answer');
    setSubmitBusy(true);
    try {
      const r = await API.post('/practice/submit', { questionId: Number(id), userAnswer: answer });
      setResult(r.data);
      toast[r.data.isCorrect ? 'success' : 'error'](r.data.message);
    } catch (err) { toast.error(err.response?.data?.message || 'Submit failed'); }
    finally { setSubmitBusy(false); }
  };

  const handleAI = async () => {
    if (!answer.trim()) return toast.error('Enter your answer first');
    setAiBusy(true);
    try {
      const r = await API.post('/ai/feedback', { questionId: Number(id), userAnswer: answer });
      setFeedback(r.data);
    } catch { toast.error('AI feedback failed. Check your Gemini API key.'); }
    finally { setAiBusy(false); }
  };

  if (busy) return <div className="flex justify-center py-24"><Spinner size="lg" text="Loading question..."/></div>;
  if (!question) return <div className="text-center py-24 text-gray-500">Question not found.</div>;

  const isMCQ = question.questionType === 'MCQ';

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-fade-in">
      <div className="flex items-center gap-3">
        <button onClick={() => navigate(-1)} className="btn btn-ghost p-2"><ArrowLeft size={20}/></button>
        <div className="flex flex-wrap gap-2">
          <span className={clsx('badge', categoryColor(question.category))}>{question.category}</span>
          <span className={clsx('badge', diffBadge(question.difficulty))}>{question.difficulty}</span>
          <span className="badge badge-gray">{question.topic}</span>
          <span className="badge badge-gray">+{question.points} pts</span>
        </div>
      </div>

      <div className="card p-6">
        <h2 className="text-lg font-bold text-white leading-snug mb-3">{question.title}</h2>
        <p className="text-gray-400 text-sm leading-relaxed">{question.description}</p>
        {isMCQ && (
          <div className="grid sm:grid-cols-2 gap-3 mt-5">
            {['A','B','C','D'].map(opt => {
              const val = question[`option${opt}`];
              if (!val) return null;
              const selected = answer === val;
              const correct  = result && val === result.correctAnswer;
              const wrong    = result && selected && !result.isCorrect;
              return (
                <button key={opt} onClick={() => !result && setAnswer(val)}
                  className={clsx('p-3 rounded-xl border text-left text-sm transition-all',
                    correct ? 'bg-success-500/15 border-success-500/40 text-success-300' :
                    wrong   ? 'bg-danger-500/15  border-danger-500/40  text-danger-300'  :
                    selected? 'bg-brand-500/15   border-brand-500/40   text-brand-300'   :
                              'bg-gray-800 border-gray-700 text-gray-300 hover:border-gray-500')}>
                  <span className="font-bold mr-2">{opt}.</span>{val}
                </button>
              );
            })}
          </div>
        )}
        {!isMCQ && (
          <div className="mt-5">
            <label className="label">Your Answer</label>
            <textarea className="input min-h-[150px] resize-y font-mono text-sm" rows={6}
              placeholder={question.questionType==='CODE' ? '// Write your code here...' : question.questionType==='SQL' ? '-- SQL query here...' : 'Type your answer...'}
              value={answer} onChange={e => !result && setAnswer(e.target.value)} readOnly={!!result}/>
          </div>
        )}
      </div>

      {result && (
        <div className={clsx('card p-5 border-l-4 animate-bounce-in',
          result.isCorrect ? 'border-success-500 bg-success-500/5' : 'border-danger-500 bg-danger-500/5')}>
          <div className="flex items-center gap-3 mb-3">
            {result.isCorrect ? <CheckCircle2 size={22} className="text-success-400"/> : <XCircle size={22} className="text-danger-400"/>}
            <p className={clsx('font-bold', result.isCorrect ? 'text-success-400' : 'text-danger-400')}>{result.message}</p>
          </div>
          {!result.isCorrect && <><p className="text-xs text-gray-500 mb-1">Correct Answer</p><p className="text-success-300 font-medium text-sm">{result.correctAnswer}</p></>}
          {result.explanation && <div className="mt-3 pt-3 border-t border-gray-800"><p className="text-xs text-gray-500 mb-1">Explanation</p><p className="text-gray-300 text-sm leading-relaxed">{result.explanation}</p></div>}
        </div>
      )}

      <div className="flex gap-3 flex-wrap">
        {!result && (
          <button onClick={handleSubmit} disabled={submitBusy || !answer.trim()} className="btn btn-primary flex-1 min-w-[140px]">
            {submitBusy ? <Spinner size="sm"/> : <Send size={16}/>}
            {submitBusy ? 'Submitting...' : 'Submit Answer'}
          </button>
        )}
        <button onClick={handleAI} disabled={aiBusy || !answer.trim()} className="btn btn-secondary flex-1 min-w-[140px]">
          {aiBusy ? <Spinner size="sm"/> : <Brain size={16}/>}
          {aiBusy ? 'Getting AI feedback...' : 'Get AI Feedback'}
        </button>
      </div>

      {feedback && <AiFeedbackBox feedback={feedback}/>}
    </div>
  );
}
