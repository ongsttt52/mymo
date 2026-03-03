import client from './client';
import type { MemberResponse, MemberUpdateRequest } from '../types/member';

export const getMyInfo = () =>
    client.get<MemberResponse>('/members/me');

export const updateMyInfo = (data: MemberUpdateRequest) =>
    client.put<MemberResponse>('/members/me', data);

export const deleteMyAccount = () =>
    client.delete<void>('/members/me');
