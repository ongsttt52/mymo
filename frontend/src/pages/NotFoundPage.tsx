import { Link } from 'react-router';

function NotFoundPage() {
    return (
        <div className="flex min-h-screen flex-col items-center justify-center bg-surface">
            <h1 className="text-6xl font-bold text-gray-300">404</h1>
            <p className="mt-4 text-lg text-gray-500">페이지를 찾을 수 없습니다.</p>
            <Link
                to="/dashboard"
                className="mt-6 rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-primary-dark"
            >
                대시보드로 이동
            </Link>
        </div>
    );
}

export default NotFoundPage;
