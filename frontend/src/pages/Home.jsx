import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Target, Brain, BarChart3, CalendarDays, CheckCircle, ArrowRight, Zap, Building2 } from 'lucide-react';

const FEATURES = [
  { icon: Building2, title: '8 Top Companies',     desc: 'TCS, Infosys, Wipro, Zoho, Amazon, Accenture, Cognizant & Capgemini', color:'text-brand-400', bg:'bg-brand-500/10' },
  { icon: Brain,     title: 'AI-Powered Feedback',  desc: 'Gemini AI reviews your answers, scores them and gives model responses',  color:'text-purple-400', bg:'bg-purple-500/10' },
  { icon: BarChart3, title: 'Weak Area Tracker',    desc: 'Auto-detects weak topics across Aptitude, SQL, Java, DSA & HR',         color:'text-warning-400',bg:'bg-warning-500/10'},
  { icon: CalendarDays,'title':'Personalized Plans', desc: 'AI generates 3/5/7-day study plans based on your weak areas',          color:'text-success-400',bg:'bg-success-500/10'},
];

const STATS = [
  { value:'8',    label:'Companies'    },
  { value:'60+',  label:'Questions'    },
  { value:'5',    label:'Categories'   },
  { value:'AI',   label:'Powered'      },
];

export default function Home() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen overflow-hidden">
      {/* ── Hero ──────────────────────────────────────────────────────── */}
      <section className="relative pt-24 pb-36 px-4 bg-hero-gradient">
        {/* Background blobs */}
        <div className="absolute top-0 left-1/2 -translate-x-1/2 w-[900px] h-[500px] bg-brand-600/10 rounded-full blur-3xl pointer-events-none"/>
        <div className="absolute top-20 right-1/4 w-64 h-64 bg-purple-600/10 rounded-full blur-3xl pointer-events-none"/>

        <div className="relative max-w-4xl mx-auto text-center">
          <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-brand-500/10 border border-brand-500/20 text-brand-400 text-sm font-medium mb-8 animate-fade-in">
            <Zap size={14}/>
            Powered by Google Gemini AI
          </div>

          <h1 className="text-5xl sm:text-6xl lg:text-7xl font-black text-white leading-[1.1] mb-6 animate-slide-up">
            Crack Your Dream
            <br/>
            <span className="text-gradient">Placement. Smarter.</span>
          </h1>

          <p className="text-xl text-gray-400 max-w-2xl mx-auto mb-10 leading-relaxed animate-fade-in">
            Company-wise questions · AI feedback · Weak-area tracking · Personalized study plans —
            everything a fresher needs in one platform.
          </p>

          <div className="flex flex-col sm:flex-row gap-4 justify-center animate-slide-up">
            <Link to={user ? '/dashboard' : '/register'}
              className="btn btn-primary btn-lg shadow-glow">
              {user ? 'Go to Dashboard' : 'Start Preparing Free'}
              <ArrowRight size={18}/>
            </Link>
            {!user && (
              <Link to="/login" className="btn btn-secondary btn-lg">
                Already have account
              </Link>
            )}
          </div>

          {/* Trust badges */}
          <div className="flex items-center justify-center gap-6 mt-10 flex-wrap">
            {['No credit card','Free forever','AI-powered','60+ questions'].map(t => (
              <span key={t} className="flex items-center gap-1.5 text-sm text-gray-500">
                <CheckCircle size={14} className="text-success-500"/>
                {t}
              </span>
            ))}
          </div>
        </div>
      </section>

      {/* ── Stats ───────────────────────────────────────────────────────── */}
      <section className="py-12 border-y border-gray-800/60 bg-gray-900/30">
        <div className="max-w-4xl mx-auto px-4 grid grid-cols-2 sm:grid-cols-4 gap-8">
          {STATS.map(s => (
            <div key={s.label} className="text-center">
              <p className="text-4xl font-black text-gradient">{s.value}</p>
              <p className="text-gray-500 text-sm mt-1">{s.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ── Features ────────────────────────────────────────────────────── */}
      <section className="py-24 px-4">
        <div className="max-w-6xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-white mb-3">Everything you need to get placed</h2>
            <p className="text-gray-400">Built for freshers targeting top IT & product companies</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {FEATURES.map(f => (
              <div key={f.title} className="card-hover p-6 group">
                <div className={`w-12 h-12 rounded-2xl ${f.bg} flex items-center justify-center mb-5 group-hover:scale-110 transition-transform`}>
                  <f.icon size={22} className={f.color}/>
                </div>
                <h3 className="font-bold text-white mb-2">{f.title}</h3>
                <p className="text-gray-400 text-sm leading-relaxed">{f.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── How it works ────────────────────────────────────────────────── */}
      <section className="py-24 px-4 bg-gray-900/30">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-3xl font-bold text-white mb-3">How SkillBridge works</h2>
            <p className="text-gray-400">From zero to placement-ready in 5 days</p>
          </div>
          <div className="space-y-4">
            {[
              ['01','Register & Set Target','Choose your target company — TCS, Infosys, Zoho, Amazon and more'],
              ['02','Practice Company-Wise','Answer Aptitude, SQL, Java, DSA, HR questions specific to that company'],
              ['03','Get AI Feedback',       'Paste your answer — Gemini AI scores it and gives a model response'],
              ['04','Track Weak Areas',      'Dashboard auto-identifies weak topics from your attempt history'],
              ['05','Generate Study Plan',   'AI creates a personalized 3/5/7-day plan focused on your gaps'],
            ].map(([num, title, desc]) => (
              <div key={num} className="card-hover p-5 flex items-center gap-5 group">
                <div className="w-12 h-12 rounded-xl bg-brand-500/10 border border-brand-500/20 flex items-center justify-center text-brand-400 font-black text-lg flex-shrink-0">
                  {num}
                </div>
                <div>
                  <p className="text-white font-semibold">{title}</p>
                  <p className="text-gray-400 text-sm mt-0.5">{desc}</p>
                </div>
                <ArrowRight size={18} className="text-gray-700 group-hover:text-brand-400 ml-auto transition-colors"/>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ──────────────────────────────────────────────────────────── */}
      <section className="py-24 px-4">
        <div className="max-w-2xl mx-auto card p-10 text-center border-brand-500/20 bg-gradient-to-br from-brand-500/5 to-purple-500/5">
          <Target size={44} className="text-brand-400 mx-auto mb-4"/>
          <h2 className="text-3xl font-bold text-white mb-4">Your placement starts today</h2>
          <p className="text-gray-400 mb-8">Join students who are preparing smarter with AI</p>
          <Link to={user ? '/dashboard' : '/register'}
            className="btn btn-primary btn-lg shadow-glow">
            {user ? 'Open Dashboard' : 'Create Free Account'}
            <ArrowRight size={18}/>
          </Link>
        </div>
      </section>
    </div>
  );
}
