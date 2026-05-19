import { BrowserRouter, Routes, Route, Outlet } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/SharedComponents';
import Navbar from './components/Navbar';
import Sidebar from './components/Sidebar';
import Home from './pages/Home';
import { Login, Register } from './pages/AuthPages';
import Dashboard from './pages/Dashboard';
import { Companies, Practice, QuestionDetail } from './pages/PracticePages';
import { AiFeedback, StudyPlan, Profile } from './pages/OtherPages';

function AppLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar/>
      <div className="flex flex-1 max-w-screen-2xl mx-auto w-full px-4 sm:px-6 py-6 gap-6">
        <Sidebar/>
        <main className="flex-1 min-w-0">
          <Outlet/>
        </main>
      </div>
    </div>
  );
}

function PublicLayout() {
  return (
    <>
      <Navbar/>
      <Outlet/>
    </>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public */}
          <Route element={<PublicLayout/>}>
            <Route path="/"         element={<Home/>}/>
            <Route path="/login"    element={<Login/>}/>
            <Route path="/register" element={<Register/>}/>
          </Route>

          {/* Protected with sidebar */}
          <Route element={<ProtectedRoute><AppLayout/></ProtectedRoute>}>
            <Route path="/dashboard"       element={<Dashboard/>}/>
            <Route path="/companies"       element={<Companies/>}/>
            <Route path="/practice"        element={<Practice/>}/>
            <Route path="/question/:id"    element={<QuestionDetail/>}/>
            <Route path="/ai-feedback"     element={<AiFeedback/>}/>
            <Route path="/study-plan"      element={<StudyPlan/>}/>
            <Route path="/profile"         element={<Profile/>}/>
          </Route>

          {/* 404 */}
          <Route path="*" element={
            <div className="flex flex-col items-center justify-center min-h-screen text-center">
              <p className="text-8xl font-black text-gray-800">404</p>
              <p className="text-gray-400 mt-4 mb-6">Page not found</p>
              <a href="/" className="btn btn-primary">Go Home</a>
            </div>
          }/>
        </Routes>
      </BrowserRouter>
      <Toaster position="top-right" toastOptions={{
        style: { background:'#111827', color:'#f3f4f6', border:'1px solid #1f2937', borderRadius:12, fontSize:14 },
        success: { iconTheme: { primary:'#10b981', secondary:'#111827' } },
        error:   { iconTheme: { primary:'#ef4444', secondary:'#111827' } },
      }}/>
    </AuthProvider>
  );
}
