import type { MusicLogResponse } from '../../types/musicLog';
import MusicLogCard from './MusicLogCard';

interface MusicLogListProps {
    logs: MusicLogResponse[];
    onEdit: (log: MusicLogResponse) => void;
    onDelete: (log: MusicLogResponse) => void;
}

function MusicLogList({ logs, onEdit, onDelete }: MusicLogListProps) {
    return (
        <div className="flex flex-col gap-4">
            {logs.map((log) => (
                <MusicLogCard key={log.id} log={log} onEdit={onEdit} onDelete={onDelete} />
            ))}
        </div>
    );
}

export default MusicLogList;
