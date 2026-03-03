import { create } from 'zustand';

const TOKEN_KEY = 'accessToken';

interface AuthState {
    token: string | null;
    memberId: number | null;
    email: string | null;
    isAuthenticated: boolean;
    setAuth: (token: string, memberId: number, email: string) => void;
    clearAuth: () => void;
}

const useAuthStore = create<AuthState>((set) => ({
    token: localStorage.getItem(TOKEN_KEY),
    memberId: null,
    email: null,
    isAuthenticated: !!localStorage.getItem(TOKEN_KEY),

    setAuth: (token, memberId, email) => {
        localStorage.setItem(TOKEN_KEY, token);
        set({ token, memberId, email, isAuthenticated: true });
    },

    clearAuth: () => {
        localStorage.removeItem(TOKEN_KEY);
        set({ token: null, memberId: null, email: null, isAuthenticated: false });
    },
}));

export default useAuthStore;
