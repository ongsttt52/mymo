import { NavLink } from 'react-router';

const NAV_ITEMS = [
    { to: '/dashboard', label: '대시보드', icon: '📊' },
    { to: '/daily-logs', label: '일일 기록', icon: '📝' },
    { to: '/memos', label: '메모', icon: '📋' },
    { to: '/photo-logs', label: '사진 기록', icon: '📷' },
    { to: '/music-logs', label: '음악 기록', icon: '🎵' },
];

function Sidebar() {
    return (
        <aside className="w-60 border-r border-gray-200 bg-white">
            <nav className="flex flex-col gap-1 p-4">
                {NAV_ITEMS.map((item) => (
                    <NavLink
                        key={item.to}
                        to={item.to}
                        className={({ isActive }) =>
                            `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm transition-colors ${
                                isActive
                                    ? 'bg-primary/10 font-medium text-primary'
                                    : 'text-gray-700 hover:bg-gray-100'
                            }`
                        }
                    >
                        <span>{item.icon}</span>
                        <span>{item.label}</span>
                    </NavLink>
                ))}
            </nav>
        </aside>
    );
}

export default Sidebar;
