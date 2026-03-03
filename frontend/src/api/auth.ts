import client from './client';
import type { LoginRequest, LoginResponse } from '../types/auth';
import type { MemberCreateRequest, MemberResponse } from '../types/member';

export const login = (data: LoginRequest) =>
    client.post<LoginResponse>('/auth/login', data);

export const signup = (data: MemberCreateRequest) =>
    client.post<MemberResponse>('/auth/signup', data);
