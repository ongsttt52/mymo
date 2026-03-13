import { BrowserRouter, Routes, Route, Navigate } from 'react-router';

import ProtectedRoute from './components/common/ProtectedRoute';
import Layout from './components/layout/Layout';
import LoginPage from './pages/LoginPage';
import SignupPage from './pages/SignupPage';
import DashboardPage from './pages/DashboardPage';
import DailyLogPage from './pages/DailyLogPage';
import MemoPage from './pages/MemoPage';
import PhotoLogPage from './pages/PhotoLogPage';
import MusicLogPage from './pages/MusicLogPage';
import NotFoundPage from './pages/NotFoundPage';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/signup" element={<SignupPage />} />

                <Route element={<ProtectedRoute />}>
                    <Route element={<Layout />}>
                        <Route path="/dashboard" element={<DashboardPage />} />
                        <Route path="/daily-logs" element={<DailyLogPage />} />
                        <Route path="/memos" element={<MemoPage />} />
                        <Route path="/photo-logs" element={<PhotoLogPage />} />
                        <Route path="/music-logs" element={<MusicLogPage />} />
                    </Route>
                </Route>

                <Route path="/" element={<Navigate to="/dashboard" replace />} />
                <Route path="*" element={<NotFoundPage />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
