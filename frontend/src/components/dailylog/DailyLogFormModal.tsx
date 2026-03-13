import { useState, useEffect } from 'react';

import Modal from '../common/Modal';
import FormField from '../common/FormField';
import type { DailyLogResponse } from '../../types/dailyLog';

interface DailyLogFormModalProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (data: { date: string; resolution?: string; reflection?: string }) => Promise<void>;
    editTarget: DailyLogResponse | null;
}

function DailyLogFormModal({ open, onClose, onSubmit, editTarget }: DailyLogFormModalProps) {
    const [date, setDate] = useState('');
    const [resolution, setResolution] = useState('');
    const [reflection, setReflection] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (open) {
            if (editTarget) {
                setDate(editTarget.date);
                setResolution(editTarget.resolution ?? '');
                setReflection(editTarget.reflection ?? '');
            } else {
                setDate(new Date().toISOString().split('T')[0]);
                setResolution('');
                setReflection('');
            }
            setError('');
        }
    }, [open, editTarget]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await onSubmit({
                date,
                resolution: resolution || undefined,
                reflection: reflection || undefined,
            });
            onClose();
        } catch (err) {
            const message = err instanceof Error ? err.message : '저장에 실패했습니다.';
            setError(message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal open={open} onClose={onClose} title={editTarget ? '일일 기록 수정' : '새 일일 기록'}>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                {error && (
                    <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                )}

                <FormField
                    label="날짜"
                    id="dailylog-date"
                    type="date"
                    value={date}
                    onChange={setDate}
                    required
                    // 수정 시 날짜 변경 불가 (백엔드 UpdateRequest에 date 필드 없음)
                />

                <FormField
                    label="오늘의 다짐"
                    id="dailylog-resolution"
                    type="textarea"
                    value={resolution}
                    onChange={setResolution}
                    placeholder="오늘의 다짐을 작성해보세요"
                    rows={3}
                />

                <FormField
                    label="오늘의 회고"
                    id="dailylog-reflection"
                    type="textarea"
                    value={reflection}
                    onChange={setReflection}
                    placeholder="오늘 하루를 돌아보세요"
                    rows={3}
                />

                <div className="mt-2 flex justify-end gap-2">
                    <button
                        type="button"
                        onClick={onClose}
                        className="rounded-lg bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 transition-colors hover:bg-gray-200"
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

export default DailyLogFormModal;
