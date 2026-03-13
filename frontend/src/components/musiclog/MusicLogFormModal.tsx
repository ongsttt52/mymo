import { useState, useEffect } from 'react';

import Modal from '../common/Modal';
import FormField from '../common/FormField';
import { getTodayString, isYouTubeUrl } from '../../utils/format';
import type { MusicLogResponse } from '../../types/musicLog';

interface MusicLogFormModalProps {
    open: boolean;
    onClose: () => void;
    onSubmit: (data: {
        title: string;
        artist?: string;
        album?: string;
        genre?: string;
        youtubeUrl?: string;
        description?: string;
        date?: string;
    }) => Promise<void>;
    editTarget: MusicLogResponse | null;
}

function MusicLogFormModal({ open, onClose, onSubmit, editTarget }: MusicLogFormModalProps) {
    const [title, setTitle] = useState('');
    const [artist, setArtist] = useState('');
    const [album, setAlbum] = useState('');
    const [genre, setGenre] = useState('');
    const [youtubeUrl, setYoutubeUrl] = useState('');
    const [description, setDescription] = useState('');
    const [date, setDate] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (open) {
            if (editTarget) {
                setTitle(editTarget.title);
                setArtist(editTarget.artist ?? '');
                setAlbum(editTarget.album ?? '');
                setGenre(editTarget.genre ?? '');
                setYoutubeUrl(editTarget.youtubeUrl ?? '');
                setDescription(editTarget.description ?? '');
                setDate(editTarget.date ?? '');
            } else {
                setTitle('');
                setArtist('');
                setAlbum('');
                setGenre('');
                setYoutubeUrl('');
                setDescription('');
                setDate(getTodayString());
            }
            setError('');
        }
    }, [open, editTarget]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (youtubeUrl && !isYouTubeUrl(youtubeUrl)) {
            setError('YouTube URL은 youtube.com 또는 youtu.be 도메인이어야 합니다.');
            return;
        }

        setLoading(true);

        try {
            await onSubmit({
                title,
                artist: artist || undefined,
                album: album || undefined,
                genre: genre || undefined,
                youtubeUrl: youtubeUrl || undefined,
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
        <Modal open={open} onClose={onClose} title={editTarget ? '음악 기록 수정' : '새 음악 기록'} preventClose={loading}>
            <form onSubmit={handleSubmit} className="flex max-h-[70vh] flex-col gap-4 overflow-y-auto">
                {error && (
                    <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">{error}</div>
                )}

                <FormField
                    label="노래 제목"
                    id="musiclog-title"
                    value={title}
                    onChange={setTitle}
                    placeholder="노래 제목"
                    required
                />

                <FormField
                    label="아티스트"
                    id="musiclog-artist"
                    value={artist}
                    onChange={setArtist}
                    placeholder="가수/아티스트"
                />

                <div className="grid grid-cols-2 gap-4">
                    <FormField
                        label="앨범"
                        id="musiclog-album"
                        value={album}
                        onChange={setAlbum}
                        placeholder="앨범명"
                    />

                    <FormField
                        label="장르"
                        id="musiclog-genre"
                        value={genre}
                        onChange={setGenre}
                        placeholder="장르"
                    />
                </div>

                <FormField
                    label="YouTube URL"
                    id="musiclog-youtubeUrl"
                    type="url"
                    value={youtubeUrl}
                    onChange={setYoutubeUrl}
                    placeholder="https://youtube.com/watch?v=..."
                />

                <FormField
                    label="감상 기록"
                    id="musiclog-description"
                    type="textarea"
                    value={description}
                    onChange={setDescription}
                    placeholder="이 음악에 대한 감상을 기록하세요"
                    rows={3}
                />

                <FormField
                    label="감상 날짜"
                    id="musiclog-date"
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

export default MusicLogFormModal;
