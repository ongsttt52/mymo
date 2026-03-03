import { Link, Navigate } from 'react-router';

import SignupForm from '../components/auth/SignupForm';
import useAuthStore from '../stores/useAuthStore';

function SignupPage() {
    const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

    if (isAuthenticated) {
        return <Navigate to="/dashboard" replace />;
    }

    return (
        <div className="flex min-h-screen items-center justify-center bg-surface">
            <div className="w-full max-w-md rounded-2xl bg-white p-8 shadow-lg">
                <div className="mb-8 text-center">
                    <h1 className="text-3xl font-bold text-primary">mymo</h1>
                    <p className="mt-2 text-sm text-gray-500">새 계정을 만들어보세요</p>
                </div>

                <SignupForm />

                <p className="mt-6 text-center text-sm text-gray-500">
                    이미 계정이 있으신가요?{' '}
                    <Link to="/login" className="font-medium text-primary hover:text-primary-dark">
                        로그인
                    </Link>
                </p>
            </div>
        </div>
    );
}

export default SignupPage;
