import type { DailyLogResponse } from '../../types/dailyLog';
import DailyLogCard from './DailyLogCard';

interface DailyLogListProps {
    logs: DailyLogResponse[];
    onEdit: (log: DailyLogResponse) => void;
    onDelete: (log: DailyLogResponse) => void;
}

function DailyLogList({ logs, onEdit, onDelete }: DailyLogListProps) {
    return (
        <div className="flex flex-col gap-4">
            {logs.map((log) => (
                <DailyLogCard key={log.id} log={log} onEdit={onEdit} onDelete={onDelete} />
            ))}
        </div>
    );
}

export default DailyLogList;
