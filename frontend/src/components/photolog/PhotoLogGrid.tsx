import type { PhotoLogResponse } from '../../types/photoLog';
import PhotoLogCard from './PhotoLogCard';

interface PhotoLogGridProps {
    logs: PhotoLogResponse[];
    onEdit: (log: PhotoLogResponse) => void;
    onDelete: (log: PhotoLogResponse) => void;
}

function PhotoLogGrid({ logs, onEdit, onDelete }: PhotoLogGridProps) {
    return (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {logs.map((log) => (
                <PhotoLogCard key={log.id} log={log} onEdit={onEdit} onDelete={onDelete} />
            ))}
        </div>
    );
}

export default PhotoLogGrid;
