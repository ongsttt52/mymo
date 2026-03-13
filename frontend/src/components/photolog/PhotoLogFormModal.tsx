import { useState, useEffect } from 'react';

import Modal from '../common/Modal';
import FormField from '../common/FormField';
import { getTodayString, isValidHttpsUrl } from '../../utils/format';
import type { PhotoLogResponse } from '../../types/photoLog';

interface PhotoLogFormModalProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (data: { imageUrl: string; location?: string; description?: string; date?: string }) => Promise<void>;
    editTarget: PhotoLogResponse | null;
}

function PhotoLogFormModal({ open, onClose, onSubmit, editTarget }: PhotoLogFormModalProps) {
    const [imageUrl, setImageUrl] = useState('');
    const [location, setLocation] = useState('');
    const [description, setDescription] = useState('');
    const [date, setDate] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [previewError, setPreviewError] = useState(false);

    useEffect(() => {
        if (open) {
            if (editTarget) {
                setImageUrl(editTarget.imageUrl);
                setLocation(editTarget.location ?? '');
                setDescription(editTarget.description ?? '');
                setDate(editTarget.date ?? '');
            } else {
                setImageUrl('');
                setLocation('');
                setDescription('');
                setDate(getTodayString());
            }
            setError('');
            setPreviewError(false);
        }
    }, [open, editTarget]);

    const handleImageUrlChange = (value: string) => {
        setImageUrl(value);
        setPreviewError(false);
    };

    // https URL이고 유효한 형태일 때만 미리보기 표시
    const showPreview = imageUrl && !previewError && isValidHttpsUrl(imageUrl);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (!isValidHttpsUrl(imageUrl)) {
            setError('이미지 URL은 https://로 시작해야 합니다.');
            return;
        }

        setLoading(true);

        try {
            await onSubmit({
                imageUrl,
                location: location || undefined,
                description: description || undefined,
                date: date || undefined,
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
        <Modal open={open} onClose={onClose} title={editTarget ? '사진 기록 수정' : '새 사진 기록'} preventClose={loading}>
            <form onSubmit={handleSubmit} className="flex max-h-[70vh] flex-col gap-4 overflow-y-auto">
                {error && (
                    <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                )}

                <FormField
                    label="이미지 URL"
                    id="photolog-imageUrl"
                    type="url"
                    value={imageUrl}
                    onChange={handleImageUrlChange}
                    placeholder="https://example.com/photo.jpg"
                    required
                />

                {showPreview && (
                    <div className="overflow-hidden rounded-lg border border-gray-200">
                        <img
                            src={imageUrl}
                            alt="미리보기"
                            className="h-40 w-full object-cover"
                            onError={() => setPreviewError(true)}
                        />
                    </div>
                )}

                <FormField
                    label="장소"
                    id="photolog-location"
                    value={location}
                    onChange={setLocation}
                    placeholder="촬영 장소"
                />

                <FormField
                    label="설명"
                    id="photolog-description"
                    type="textarea"
                    value={description}
                    onChange={setDescription}
                    placeholder="사진에 대한 설명"
                    rows={3}
                />

                <FormField
                    label="날짜"
                    id="photolog-date"
                    type="date"
                    value={date}
                    onChange={setDate}
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

export default PhotoLogFormModal;
