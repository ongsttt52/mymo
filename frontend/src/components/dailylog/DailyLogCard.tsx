import type { DailyLogResponse } from '../../types/dailyLog';
import { formatDate } from '../../utils/format';

interface DailyLogCardProps {
    log: DailyLogResponse;
    onEdit: (log: DailyLogResponse) => void;
    onDelete: (log: DailyLogResponse) => void;
}

function DailyLogCard({ log, onEdit, onDelete }: DailyLogCardProps) {
    return (
        <div className="rounded-xl border border-indigo-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
            <div className="mb-3 flex items-center justify-between">
                <span className="inline-flex items-center rounded-lg bg-indigo-50 px-3 py-1 text-sm font-medium text-indigo-600">
                    {formatDate(log.date)}
                </span>
                <div className="flex gap-1">
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

            {log.resolution && (
                <div className="mb-2">
                    <h4 className="text-xs font-semibold uppercase tracking-wide text-gray-400">다짐</h4>
                    <p className="mt-1 line-clamp-2 text-sm text-gray-700">{log.resolution}</p>
                </div>
            )}

            {log.reflection && (
                <div>
                    <h4 className="text-xs font-semibold uppercase tracking-wide text-gray-400">회고</h4>
                    <p className="mt-1 line-clamp-2 text-sm text-gray-700">{log.reflection}</p>
                </div>
            )}

            {!log.resolution && !log.reflection && (
                <p className="text-sm italic text-gray-400">내용이 없습니다.</p>
            )}
        </div>
    );
}

export default DailyLogCard;
