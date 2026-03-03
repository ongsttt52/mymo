import { Link, Navigate } from 'react-router';

import LoginForm from '../components/auth/LoginForm';
import useAuthStore from '../stores/useAuthStore';

function LoginPage() {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

    if (isAuthenticated) {
        return <Navigate to="/dashboard" replace />;
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-surface">
            <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg">
                <div className="mb-8 text-center">
                    <h1 className="text-3xl font-bold text-primary">mymo</h1>
                    <p className="mt-2 text-sm text-gray-500">나의 일상을 기록하세요</p>
                </div>

                <LoginForm />

                <p className="mt-6 text-center text-sm text-gray-500">
                    계정이 없으신가요?{' '}
                    <Link to="/signup" className="font-medium text-primary hover:text-primary-dark">
                        회원가입
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default LoginPage;
