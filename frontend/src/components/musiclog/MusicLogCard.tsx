import type { MusicLogResponse } from '../../types/musicLog';
import { formatDate } from '../../utils/format';

interface MusicLogCardProps {
    log: MusicLogResponse;
    onEdit: (log: MusicLogResponse) => void;
    onDelete: (log: MusicLogResponse) => void;
}

function MusicLogCard({ log, onEdit, onDelete }: MusicLogCardProps) {
    return (
        <div className="rounded-xl border border-rose-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
            <div className="flex items-start justify-between gap-4">
                <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                        <h3 className="truncate text-base font-semibold text-gray-900">{log.title}</h3>
                        {log.youtubeUrl && (
                            <a
                                href={log.youtubeUrl}
                                target="_blank"
                                rel="noopener noreferrer"
                                className="shrink-0 text-xs text-rose-500 hover:text-rose-700"
                            >
                                YouTube
                            </a>
                        )}
                    </div>
                    {log.artist && (
                        <p className="mt-0.5 text-sm text-gray-500">{log.artist}</p>
                    )}

                    <div className="mt-2 flex flex-wrap gap-1.5">
                        {log.album && (
                            <span className="inline-flex rounded-full bg-rose-50 px-2.5 py-0.5 text-xs text-rose-600">
                                {log.album}
                            </span>
                        )}
                        {log.genre && (
                            <span className="inline-flex rounded-full bg-gray-100 px-2.5 py-0.5 text-xs text-gray-600">
                                {log.genre}
                            </span>
                        )}
                        {log.date && (
                            <span className="inline-flex rounded-full bg-gray-50 px-2.5 py-0.5 text-xs text-gray-400">
                                {formatDate(log.date)}
                            </span>
                        )}
                    </div>

                    {log.description && (
                        <p className="mt-2 line-clamp-2 text-sm text-gray-600">{log.description}</p>
                    )}
                </div>

                <div className="flex shrink-0 gap-1">
                    <button
                        onClick={() => onEdit(log)}
                        className="rounded-lg px-2.5 py-1 text-sm text-gray-500 transition-colors hover:bg-gray-100 hover:text-gray-700"
                    >
                        수정
                    </button>
                    <button
                        onClick={() => onDelete(log)}
                        className="rounded-lg px-2.5 py-1 text-sm text-gray-500 transition-colors hover:bg-red-50 hover:text-red-600"
                    >
                        삭제
                    </button>
                </div>
            </div>
        </div>
    );
}

export default MusicLogCard;
