import { Outlet } from 'react-router';

import Header from './Header';
import Sidebar from './Sidebar';

function Layout() {
    return (
        <div className="flex h-screen flex-col bg-surface">
            <Header />
            <div className="flex flex-1 overflow-hidden">
                <Sidebar />
                <main className="flex-1 overflow-y-auto p-6">
                    <Outlet />
                </main>
            </div>
        </div>
    );
}

export default Layout;
