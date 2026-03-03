import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import { AxiosError } from 'axios';

import { getDailyLogs } from '../api/dailyLog';
import { getMemos } from '../api/memo';
import { getPhotoLogs } from '../api/photoLog';
import { getMusicLogs } from '../api/musicLog';
import { getMyInfo } from '../api/member';
import useAuthStore from '../stores/useAuthStore';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';

interface DomainCounts {
    dailyLogs: number;
    memos: number;
    photoLogs: number;
    musicLogs: number;
}

const CARDS = [
    { key: 'dailyLogs' as const, label: '일일 기록', icon: '📝', to: '/daily-logs', color: 'bg-indigo-50 text-indigo-600' },
    { key: 'memos' as const, label: '메모', icon: '📋', to: '/memos', color: 'bg-amber-50 text-amber-600' },
    { key: 'photoLogs' as const, label: '사진 기록', icon: '📷', to: '/photo-logs', color: 'bg-emerald-50 text-emerald-600' },
    { key: 'musicLogs' as const, label: '음악 기록', icon: '🎵', to: '/music-logs', color: 'bg-rose-50 text-rose-600' },
];

function DashboardPage() {
    const [counts, setCounts] = useState<DomainCounts | null>(null);
    const [username, setUsername] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const setAuth = useAuthStore((state) => state.setAuth);
    const token = useAuthStore((state) => state.token);
    const email = useAuthStore((state) => state.email);

    const fetchData = async () => {
        setLoading(true);
        setError('');

        try {
            const [dailyLogs, memos, photoLogs, musicLogs, memberInfo] = await Promise.all([
                getDailyLogs(),
                getMemos(),
                getPhotoLogs(),
                getMusicLogs(),
                getMyInfo(),
            ]);

            setCounts({
                dailyLogs: dailyLogs.data.length,
                memos: memos.data.length,
                photoLogs: photoLogs.data.length,
                musicLogs: musicLogs.data.length,
            });

            setUsername(memberInfo.data.username);

            // 로그인 직후 email이 없을 수 있으므로 스토어 갱신
            if (token && !email) {
                setAuth(token, memberInfo.data.id, memberInfo.data.email);
            }
        } catch (err) {
            if (err instanceof AxiosError && err.response?.status === 401) {
                return;
            }
            setError('데이터를 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    if (loading) {
        return <LoadingSpinner />;
    }

    if (error) {
        return <ErrorMessage message={error} onRetry={fetchData} />;
    }

    return (
        <div>
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-900">
                    안녕하세요, {username}님!
                </h2>
                <p className="mt-1 text-gray-500">오늘도 소중한 일상을 기록해보세요.</p>
            </div>

            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                {CARDS.map((card) => (
                    <Link
                        key={card.key}
                        to={card.to}
                        className="rounded-xl bg-white p-6 shadow-sm transition-shadow hover:shadow-md"
                    >
                        <div className={`inline-flex rounded-lg p-3 ${card.color}`}>
                            <span className="text-2xl">{card.icon}</span>
                        </div>
                        <h3 className="mt-4 text-sm font-medium text-gray-500">{card.label}</h3>
                        <p className="mt-1 text-3xl font-bold text-gray-900">
                            {counts?.[card.key] ?? 0}
                        </p>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default DashboardPage;
