import type { MemoResponse } from '../../types/memo';
import MemoCard from './MemoCard';

interface MemoGridProps {
    memos: MemoResponse[];
    onEdit: (memo: MemoResponse) => void;
    onDelete: (memo: MemoResponse) => void;
}

function MemoGrid({ memos, onEdit, onDelete }: MemoGridProps) {
    return (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {memos.map((memo) => (
                <MemoCard key={memo.id} memo={memo} onEdit={onEdit} onDelete={onDelete} />
            ))}
        </div>
    );
}

export default MemoGrid;
