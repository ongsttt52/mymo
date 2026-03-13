import { useEffect, useState, useCallback } from 'react';
import axios from 'axios';

import { createMemo, getMemos, updateMemo, deleteMemo } from '../api/memo';
import type { MemoResponse } from '../types/memo';
import type { ErrorResponse } from '../types/error';
import LoadingSpinner from '../components/common/LoadingSpinner';
import ErrorMessage from '../components/common/ErrorMessage';
import PageHeader from '../components/common/PageHeader';
import EmptyState from '../components/common/EmptyState';
import ConfirmModal from '../components/common/ConfirmModal';
import Pagination from '../components/common/Pagination';
import SearchBar from '../components/common/SearchBar';
import MemoGrid from '../components/memo/MemoGrid';
import MemoFormModal from '../components/memo/MemoFormModal';

function MemoPage() {
    const [memos, setMemos] = useState<MemoResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [keyword, setKeyword] = useState('');

    const [formOpen, setFormOpen] = useState(false);
    const [editTarget, setEditTarget] = useState<MemoResponse | null>(null);
    const [deleteTarget, setDeleteTarget] = useState<MemoResponse | null>(null);
    const [deleting, setDeleting] = useState(false);

    const fetchMemos = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const { data } = await getMemos({
                page,
                keyword: keyword || undefined,
            });
            setMemos(data.content);
            setTotalPages(data.totalPages);
            setTotalElements(data.totalElements);
        } catch (err) {
            if (axios.isAxiosError(err) && err.response?.status === 401) return;
            setError('메모를 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    }, [page, keyword]);

    useEffect(() => {
        fetchMemos();
    }, [fetchMemos]);

    const handleSearch = (value: string) => {
        setKeyword(value);
        setPage(0);
    };

    const handleCreate = () => {
        setEditTarget(null);
        setFormOpen(true);
    };

    const handleEdit = (memo: MemoResponse) => {
        setEditTarget(memo);
        setFormOpen(true);
    };

    const handleFormSubmit = async (data: { content: string }) => {
        try {
            if (editTarget) {
                await updateMemo(editTarget.id, data);
            } else {
                await createMemo(data);
            }
            await fetchMemos();
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
            await deleteMemo(deleteTarget.id);
            setDeleteTarget(null);
            await fetchMemos();
        } catch {
            setError('삭제에 실패했습니다.');
            setDeleteTarget(null);
        } finally {
            setDeleting(false);
        }
    };

    if (loading && memos.length === 0) return <LoadingSpinner />;
    if (error && memos.length === 0) return <ErrorMessage message={error} onRetry={fetchMemos} />;

    return (
        <div>
            {memos.length > 0 || keyword ? (
                <>
                    <PageHeader title="메모" count={totalElements} actionLabel="새 메모" onAction={handleCreate} />
                    <div className="mb-4">
                        <SearchBar onSearch={handleSearch} placeholder="메모 내용에서 검색" />
                    </div>
                    {error && (
                        <div className="mb-4 rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                    )}
                    {memos.length > 0 ? (
                        <MemoGrid memos={memos} onEdit={handleEdit} onDelete={setDeleteTarget} />
                    ) : (
                        <div className="py-12 text-center text-gray-500">검색 결과가 없습니다.</div>
                    )}
                    <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
                </>
            ) : (
                <EmptyState
                    icon="📋"
                    message="아직 메모가 없습니다."
                    actionLabel="첫 메모 작성하기"
                    onAction={handleCreate}
                />
            )}

            <MemoFormModal
                open={formOpen}
                onClose={() => setFormOpen(false)}
                onSubmit={handleFormSubmit}
                editTarget={editTarget}
            />

            <ConfirmModal
                open={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                onConfirm={handleDelete}
                title="메모 삭제"
                message="이 메모를 삭제하시겠습니까? 삭제된 메모는 복구할 수 없습니다."
                loading={deleting}
            />
        </div>
    );
}

export default MemoPage;
