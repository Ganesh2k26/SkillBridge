// Login.jsx
import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Eye, EyeOff, Target, LogIn } from 'lucide-react';
import toast from 'react-hot-toast';
import { Spinner } from '../components/SharedComponents';
import { getApiErrorMessage } from '../utils/apiError';

export function Login() {
  const { login } = useAuth();
  const nav = useNavigate();
  const [form, setForm] = useState({ email:'', password:'' });
  const [show, setShow]   = useState(false);
  const [busy, setBusy]   = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    if (!form.email || !form.password) return toast.error('Fill in all fields');
    setBusy(true);
    try {
      await login(form.email, form.password);
      toast.success('Welcome back! 🎯');
      nav('/dashboard');
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Login failed'));
    } finally { setBusy(false); }
  };

  return (
    <div className="min-h-[calc(100vh-64px)] flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-md animate-slide-up">
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center mx-auto mb-4 shadow-glow">
            <Target size={28} className="text-white"/>
          </div>
          <h1 className="text-2xl font-bold text-white">Sign in to SkillBridge</h1>
          <p className="text-gray-400 mt-1 text-sm">Continue your placement journey</p>
        </div>

        <div className="card p-8">
          <form onSubmit={submit} className="space-y-5">
            <div>
              <label className="label">Email address</label>
              <input type="email" className="input" placeholder="you@example.com"
                value={form.email} onChange={e=>setForm({...form,email:e.target.value})} autoComplete="email"/>
            </div>
            <div>
              <label className="label">Password</label>
              <div className="relative">
                <input type={show?'text':'password'} className="input pr-11" placeholder="••••••••"
                  value={form.password} onChange={e=>setForm({...form,password:e.target.value})} autoComplete="current-password"/>
                <button type="button" onClick={()=>setShow(!show)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                  {show ? <EyeOff size={18}/> : <Eye size={18}/>}
                </button>
              </div>
            </div>
            <button type="submit" disabled={busy} className="btn btn-primary w-full py-3">
              {busy ? <Spinner size="sm"/> : <LogIn size={18}/>}
              {busy ? 'Signing in...' : 'Sign In'}
            </button>
          </form>
          <p className="text-center text-gray-500 text-sm mt-6">
            New to SkillBridge?{' '}
            <Link to="/register" className="text-brand-400 hover:text-brand-300 font-medium">Create account</Link>
          </p>

          {/* Demo hint */}
          <div className="mt-4 p-3 rounded-xl bg-gray-800/60 border border-gray-700">
            <p className="text-xs text-gray-500 text-center">
              Admin demo: <span className="text-brand-400">admin@skillbridge.dev</span> / <span className="text-brand-400">admin123</span>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

// Register.jsx
export function Register() {
  const { register } = useAuth();
  const nav = useNavigate();
  const [form, setForm] = useState({ name:'', email:'', password:'', targetCompany:'', collegeName:'', graduationYear:'' });
  const [show, setShow] = useState(false);
  const [busy, setBusy] = useState(false);

  const COMPANIES = ['TCS','Infosys','Wipro','Zoho','Amazon','Accenture','Cognizant','Capgemini','Any'];

  const submit = async (e) => {
    e.preventDefault();
    if (!form.name || !form.email || !form.password) return toast.error('Fill required fields');
    if (form.password.length < 6) return toast.error('Password min 6 characters');
    setBusy(true);
    try {
      await register({ ...form, graduationYear: form.graduationYear ? Number(form.graduationYear) : null });
      toast.success('Account created! Welcome to SkillBridge 🎉');
      nav('/dashboard');
    } catch (err) {
      toast.error(getApiErrorMessage(err, 'Registration failed'));
    } finally { setBusy(false); }
  };

  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  return (
    <div className="min-h-[calc(100vh-64px)] flex items-center justify-center px-4 py-12">
      <div className="w-full max-w-lg animate-slide-up">
        <div className="text-center mb-8">
          <div className="w-16 h-16 rounded-2xl bg-gradient-to-br from-brand-500 to-purple-600 flex items-center justify-center mx-auto mb-4 shadow-glow">
            <Target size={28} className="text-white"/>
          </div>
          <h1 className="text-2xl font-bold text-white">Create your account</h1>
          <p className="text-gray-400 mt-1 text-sm">Start your placement preparation today — free</p>
        </div>

        <div className="card p-8">
          <form onSubmit={submit} className="space-y-4">
            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label className="label">Full Name <span className="text-danger-400">*</span></label>
                <input className="input" placeholder="Ganesh Kumar" value={form.name} onChange={set('name')} autoComplete="name"/>
              </div>
              <div>
                <label className="label">Email <span className="text-danger-400">*</span></label>
                <input type="email" className="input" placeholder="you@example.com" value={form.email} onChange={set('email')}/>
              </div>
            </div>

            <div>
              <label className="label">Password <span className="text-danger-400">*</span></label>
              <div className="relative">
                <input type={show?'text':'password'} className="input pr-11" placeholder="Min. 6 characters" value={form.password} onChange={set('password')}/>
                <button type="button" onClick={()=>setShow(!show)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500 hover:text-gray-300">
                  {show ? <EyeOff size={18}/> : <Eye size={18}/>}
                </button>
              </div>
            </div>

            <div className="grid sm:grid-cols-2 gap-4">
              <div>
                <label className="label">Target Company</label>
                <select className="input" value={form.targetCompany} onChange={set('targetCompany')}>
                  <option value="">Select company</option>
                  {COMPANIES.map(c => <option key={c}>{c}</option>)}
                </select>
              </div>
              <div>
                <label className="label">Graduation Year</label>
                <input type="number" className="input" placeholder="2025" min="2020" max="2030"
                  value={form.graduationYear} onChange={set('graduationYear')}/>
              </div>
            </div>

            <div>
              <label className="label">College Name</label>
              <input className="input" placeholder="ABC Engineering College" value={form.collegeName} onChange={set('collegeName')}/>
            </div>

            <button type="submit" disabled={busy} className="btn btn-primary w-full py-3 mt-2">
              {busy ? <Spinner size="sm"/> : null}
              {busy ? 'Creating account...' : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-gray-500 text-sm mt-6">
            Already have an account?{' '}
            <Link to="/login" className="text-brand-400 hover:text-brand-300 font-medium">Sign in</Link>
          </p>
        </div>
      </div>
    </div>
  );
}
