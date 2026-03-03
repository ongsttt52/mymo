import { useState } from 'react';
import { useNavigate } from 'react-router';
import { AxiosError } from 'axios';

import { signup } from '../../api/auth';
import type { ErrorResponse } from '../../types/error';

function SignupForm() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setFieldErrors({});
        setLoading(true);

        try {
            await signup({ username, email, password });
            navigate('/login');
        } catch (err) {
            const axiosError = err as AxiosError<ErrorResponse>;
            const data = axiosError.response?.data;

            if (data?.fieldErrors && data.fieldErrors.length > 0) {
                const errors: Record<string, string> = {};
                for (const fe of data.fieldErrors) {
                    errors[fe.field] = fe.reason;
                }
                setFieldErrors(errors);
            } else {
                setError(data?.message ?? '회원가입에 실패했습니다.');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            {error && (
                <div className="rounded-lg bg-red-50 px-4 py-3 text-sm text-red-600">
                    {error}
                </div>
            )}

            <div className="flex flex-col gap-1.5">
                <label htmlFor="username" className="text-sm font-medium text-gray-700">
                    사용자명
                </label>
                <input
                    id="username"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    minLength={2}
                    maxLength={20}
                    placeholder="2~20자"
                    className="rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none transition-colors focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
                {fieldErrors.username && (
                    <span className="text-xs text-red-500">{fieldErrors.username}</span>
                )}
            </div>

            <div className="flex flex-col gap-1.5">
                <label htmlFor="email" className="text-sm font-medium text-gray-700">
                    이메일
                </label>
                <input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    placeholder="example@email.com"
                    className="rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none transition-colors focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
                {fieldErrors.email && (
                    <span className="text-xs text-red-500">{fieldErrors.email}</span>
                )}
            </div>

            <div className="flex flex-col gap-1.5">
                <label htmlFor="password" className="text-sm font-medium text-gray-700">
                    비밀번호
                </label>
                <input
                    id="password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    minLength={8}
                    maxLength={50}
                    placeholder="8자 이상"
                    className="rounded-lg border border-gray-300 px-3 py-2.5 text-sm outline-none transition-colors focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
                {fieldErrors.password && (
                    <span className="text-xs text-red-500">{fieldErrors.password}</span>
                )}
            </div>

            <button
                type="submit"
                disabled={loading}
                className="mt-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-white transition-colors hover:bg-primary-dark disabled:cursor-not-allowed disabled:opacity-50"
            >
                {loading ? '가입 중...' : '회원가입'}
            </button>
        </form>
    );
}

export default SignupForm;
