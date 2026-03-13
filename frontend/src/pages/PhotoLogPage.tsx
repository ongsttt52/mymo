import { useEffect, useState, useCallback } from 'react';
import axios from 'axios';

import { createPhotoLog, getPhotoLogs, updatePhotoLog, deletePhotoLog } from '../api/photoLog';
import type { PhotoLogResponse } from '../types/photoLog';
import type { ErrorResponse } from '../types/error';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import PageHeader from '../components/common/PageHeader';
import EmptyState from '../components/common/EmptyState';
import ConfirmModal from '../components/common/ConfirmModal';
import Pagination from '../components/common/Pagination';
import SearchBar from '../components/common/SearchBar';
import DateRangeFilter from '../components/common/DateRangeFilter';
import PhotoLogGrid from '../components/photolog/PhotoLogGrid';
import PhotoLogFormModal from '../components/photolog/PhotoLogFormModal';

function PhotoLogPage() {
    const [logs, setLogs] = useState<PhotoLogResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [keyword, setKeyword] = useState('');
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');

    const [formOpen, setFormOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<PhotoLogResponse | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<PhotoLogResponse | null>(null);
    const [deleting, setDeleting] = useState(false);

    const fetchLogs = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const { data } = await getPhotoLogs({
                page,
                keyword: keyword || undefined,
                startDate: startDate || undefined,
                endDate: endDate || undefined,
            });
            setLogs(data.content);
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 401) return;
            setError('사진 기록을 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [page, keyword, startDate, endDate]);

    useEffect(() => {
        fetchLogs();
    }, [fetchLogs]);

    const handleSearch = (value: string) => {
        setKeyword(value);
        setPage(0);
    };

    const handleStartDateChange = (date: string) => {
        setStartDate(date);
        setPage(0);
    };

    const handleEndDateChange = (date: string) => {
        setEndDate(date);
        setPage(0);
    };

    const handleCreate = () => {
        setEditTarget(null);
        setFormOpen(true);
    };

    const handleEdit = (log: PhotoLogResponse) => {
        setEditTarget(log);
        setFormOpen(true);
    };

    const handleFormSubmit = async (data: { imageUrl: string; location?: string; description?: string; date?: string }) => {
        try {
            if (editTarget) {
                await updatePhotoLog(editTarget.id, data);
            } else {
                await createPhotoLog(data);
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
            await deletePhotoLog(deleteTarget.id);
            setDeleteTarget(null);
            await fetchLogs();
        } catch {
            setError('삭제에 실패했습니다.');
            setDeleteTarget(null);
        } finally {
            setDeleting(false);
        }
    };

    if (loading && logs.length === 0) return <LoadingSpinner />;
    if (error && logs.length === 0) return <ErrorMessage message={error} onRetry={fetchLogs} />;

    return (
        <div>
            {logs.length > 0 || keyword || startDate || endDate ? (
                <>
                    <PageHeader title="사진 기록" count={totalElements} actionLabel="새 사진" onAction={handleCreate} />
                    <div className="mb-4 flex flex-col gap-3 sm:flex-row sm:items-center">
                        <div className="flex-1">
                            <SearchBar onSearch={handleSearch} placeholder="장소 또는 설명에서 검색" />
                        </div>
                        <DateRangeFilter
                            startDate={startDate}
                            endDate={endDate}
                            onStartDateChange={handleStartDateChange}
                            onEndDateChange={handleEndDateChange}
                        />
                    </div>
                    {error && (
                        <div className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                    )}
                    {logs.length > 0 ? (
                        <PhotoLogGrid logs={logs} onEdit={handleEdit} onDelete={setDeleteTarget} />
                    ) : (
                        <div className="py-12 text-center text-gray-500">검색 결과가 없습니다.</div>
                    )}
                    <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
                </>
            ) : (
                <EmptyState
                    icon="📷"
                    message="아직 사진 기록이 없습니다."
                    actionLabel="첫 사진 기록하기"
                    onAction={handleCreate}
                />
            )}

            <PhotoLogFormModal
                open={formOpen}
                onClose={() => setFormOpen(false)}
                onSubmit={handleFormSubmit}
                editTarget={editTarget}
            />

            <ConfirmModal
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={handleDelete}
                title="사진 기록 삭제"
                message="이 사진 기록을 삭제하시겠습니까? 삭제된 기록은 복구할 수 없습니다."
                loading={deleting}
            />
        </div>
    );
}

export default PhotoLogPage;
