import { useState } from 'react';

import type { PhotoLogResponse } from '../../types/photoLog';
import { formatDate } from '../../utils/format';

interface PhotoLogCardProps {
    log: PhotoLogResponse;
    onEdit: (log: PhotoLogResponse) => void;
    onDelete: (log: PhotoLogResponse) => void;
}

function PhotoLogCard({ log, onEdit, onDelete }: PhotoLogCardProps) {
    const [imgError, setImgError] = useState(false);

    return (
        <div className="group overflow-hidden rounded-xl border border-emerald-100 bg-white shadow-sm transition-shadow hover:shadow-md">
            <div className="relative aspect-[4/3] bg-gray-100">
                {imgError ? (
                    <div className="flex h-full items-center justify-center text-gray-400">
                        <svg className="h-12 w-12" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M2.25 15.75l5.159-5.159a2.25 2.25 0 013.182 0l5.159 5.159m-1.5-1.5l1.409-1.409a2.25 2.25 0 013.182 0l2.909 2.909M3.75 21h16.5A2.25 2.25 0 0022.5 18.75V5.25A2.25 2.25 0 0020.25 3H3.75A2.25 2.25 0 001.5 5.25v13.5A2.25 2.25 0 003.75 21z" />
                        </svg>
                    </div>
                ) : (
                    <img
                        src={log.imageUrl}
                        alt={log.description ?? '사진'}
                        className="h-full w-full object-cover"
                        onError={() => setImgError(true)}
                    />
                )}

                <div className="absolute right-2 top-2 flex gap-1 opacity-0 transition-opacity group-focus-within:opacity-100 group-hover:opacity-100">
                    <button
                        onClick={() => onEdit(log)}
                        className="rounded-lg bg-white/90 px-2 py-1 text-xs text-gray-600 shadow-sm backdrop-blur-sm transition-colors hover:bg-white"
                    >
                        수정
                    </button>
                    <button
                        onClick={() => onDelete(log)}
                        className="rounded-lg bg-white/90 px-2 py-1 text-xs text-red-600 shadow-sm backdrop-blur-sm transition-colors hover:bg-white"
                    >
                        삭제
                    </button>
                </div>
            </div>

            <div className="p-4">
                {log.location && (
                    <p className="text-sm font-medium text-emerald-600">{log.location}</p>
                )}
                {log.description && (
                    <p className="mt-1 line-clamp-2 text-sm text-gray-600">{log.description}</p>
                )}
                {log.date && (
                    <p className="mt-2 text-xs text-gray-400">{formatDate(log.date)}</p>
                )}
            </div>
        </div>
    );
}

export default PhotoLogCard;
