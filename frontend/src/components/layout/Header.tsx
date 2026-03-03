import { useNavigate } from 'react-router';

import useAuthStore from '../../stores/useAuthStore';

function Header() {
    const email = useAuthStore((state) => state.email);
    const clearAuth = useAuthStore((state) => state.clearAuth);
    const navigate = useNavigate();

    const handleLogout = () => {
        clearAuth();
        navigate('/login');
    };

    return (
        <header className="flex h-16 items-center justify-between border-b border-gray-200 bg-white px-6">
            <h1 className="text-xl font-bold text-primary">mymo</h1>
            <div className="flex items-center gap-4">
                {email && (
                    <span className="text-sm text-gray-600">{email}</span>
                )}
                <button
                    onClick={handleLogout}
                    className="rounded-lg bg-gray-100 px-3 py-1.5 text-sm text-gray-700 transition-colors hover:bg-gray-200"
                >
                    로그아웃
                </button>
            </div>
        </header>
    );
}

export default Header;
