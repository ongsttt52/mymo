import { useEffect, useState } from 'react';
import axios from 'axios';

import { createDailyLog, getDailyLogs, updateDailyLog, deleteDailyLog } from '../api/dailyLog';
import type { DailyLogResponse } from '../types/dailyLog';
import type { ErrorResponse } from '../types/error';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import PageHeader from '../components/common/PageHeader';
import EmptyState from '../components/common/EmptyState';
import ConfirmModal from '../components/common/ConfirmModal';
import DailyLogList from '../components/dailylog/DailyLogList';
import DailyLogFormModal from '../components/dailylog/DailyLogFormModal';

function DailyLogPage() {
    const [logs, setLogs] = useState<DailyLogResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [formOpen, setFormOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<DailyLogResponse | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<DailyLogResponse | null>(null);
    const [deleting, setDeleting] = useState(false);

    const fetchLogs = async () => {
        setLoading(true);
        setError('');
        try {
            const { data } = await getDailyLogs();
            const sorted = [...data].sort((a, b) => b.date.localeCompare(a.date));
            setLogs(sorted);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 401) return;
            setError('일일 기록을 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchLogs();
    }, []);

    const handleCreate = () => {
        setEditTarget(null);
        setFormOpen(true);
    };

    const handleEdit = (log: DailyLogResponse) => {
        setEditTarget(log);
        setFormOpen(true);
    };

    const handleFormSubmit = async (data: { date: string; resolution?: string; reflection?: string }) => {
        try {
            if (editTarget) {
                await updateDailyLog(editTarget.id, {
                    resolution: data.resolution,
                    reflection: data.reflection,
                });
            } else {
                await createDailyLog(data);
            }
            await fetchLogs();
        } catch (err) {
            if (axios.isAxiosError<ErrorResponse>(err)) {
                throw new Error(err.response?.data?.message ?? '저장에 실패했습니다.');
            }
            throw new Error('저장에 실패했습니다.');
        }
    };

    const handleDelete = async () => {
        if (!deleteTarget) return;
        setDeleting(true);
        try {
            await deleteDailyLog(deleteTarget.id);
            setDeleteTarget(null);
            await fetchLogs();
        } catch {
            setError('삭제에 실패했습니다.');
            setDeleteTarget(null);
        } finally {
            setDeleting(false);
        }
    };

    if (loading) return <LoadingSpinner />;
    if (error && logs.length === 0) return <ErrorMessage message={error} onRetry={fetchLogs} />;

    return (
        <div>
            {logs.length > 0 ? (
                <>
                    <PageHeader title="일일 기록" count={logs.length} actionLabel="새 기록" onAction={handleCreate} />
                    {error && (
                        <div className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                    )}
                    <DailyLogList logs={logs} onEdit={handleEdit} onDelete={setDeleteTarget} />
                </>
            ) : (
                <EmptyState
                    icon="📝"
                    message="아직 일일 기록이 없습니다."
                    actionLabel="첫 기록 작성하기"
                    onAction={handleCreate}
                />
            )}

            <DailyLogFormModal
                open={formOpen}
                onClose={() => setFormOpen(false)}
                onSubmit={handleFormSubmit}
                editTarget={editTarget}
            />

            <ConfirmModal
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={handleDelete}
                title="일일 기록 삭제"
                message="이 일일 기록을 삭제하시겠습니까? 삭제된 기록은 복구할 수 없습니다."
                loading={deleting}
            />
        </div>
    );
}

export default DailyLogPage;
