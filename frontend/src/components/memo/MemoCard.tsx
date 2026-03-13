import type { MemoResponse } from '../../types/memo';
import { formatDateTime } from '../../utils/format';

interface MemoCardProps {
    memo: MemoResponse;
    onEdit: (memo: MemoResponse) => void;
    onDelete: (memo: MemoResponse) => void;
}

function MemoCard({ memo, onEdit, onDelete }: MemoCardProps) {
    const isEdited = memo.updatedAt !== memo.createdAt;

    return (
        <div className="group relative rounded-xl border border-amber-100 bg-white p-5 shadow-sm transition-shadow hover:shadow-md">
            <p className="line-clamp-6 whitespace-pre-wrap text-sm text-gray-700">
                {memo.content}
            </p>

            <div className="mt-3 text-xs text-gray-400">
                {isEdited ? (
                    <span>수정됨 {formatDateTime(memo.updatedAt)}</span>
                ) : (
                    <span>{formatDateTime(memo.createdAt)}</span>
                )}
            </div>

            <div className="absolute right-2 top-2 flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
                <button
                    onClick={() => onEdit(memo)}
                    className="rounded-lg bg-white px-2 py-1 text-xs text-gray-500 shadow-sm transition-colors hover:bg-gray-100 hover:text-gray-700"
                >
                    수정
                </button>
                <button
                    onClick={() => onDelete(memo)}
                    className="rounded-lg bg-white px-2 py-1 text-xs text-gray-500 shadow-sm transition-colors hover:bg-red-50 hover:text-red-600"
                >
                    삭제
                </button>
            </div>
        </div>
    );
}

export default MemoCard;
