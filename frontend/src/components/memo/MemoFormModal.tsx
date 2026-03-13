import { useState, useEffect } from 'react';

import Modal from '../common/Modal';
import FormField from '../common/FormField';
import type { MemoResponse } from '../../types/memo';

interface MemoFormModalProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (data: { content: string }) => Promise<void>;
    editTarget: MemoResponse | null;
}

function MemoFormModal({ open, onClose, onSubmit, editTarget }: MemoFormModalProps) {
    const [content, setContent] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (open) {
            setContent(editTarget?.content ?? '');
            setError('');
        }
    }, [open, editTarget]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await onSubmit({ content });
            onClose();
        } catch (err) {
            const message = err instanceof Error ? err.message : '저장에 실패했습니다.';
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal open={open} onClose={onClose} title={editTarget ? '메모 수정' : '새 메모'} preventClose={loading}>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                {error && (
                    <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                )}

                <FormField
                    label="내용"
                    id="memo-content"
                    type="textarea"
                    value={content}
                    onChange={setContent}
                    placeholder="메모를 작성하세요"
                    required
                    rows={6}
                />

                <div className="mt-2 flex justify-end gap-2">
                    <button
                        type="button"
                        onClick={onClose}
                        disabled={loading}
                        className="rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200 disabled:opacity-50"
                    >
                        취소
                    </button>
                    <button
                        type="submit"
                        disabled={loading}
                        className="rounded-lg bg-primary px-4 py-2 text-sm font-medium text-white transition-colors hover:bg-primary-dark disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        {loading ? '저장 중...' : '저장'}
                    </button>
                </div>
            </form>
        </Modal>
    );
}

export default MemoFormModal;
