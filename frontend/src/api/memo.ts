import client from './client';
import type { MemoCreateRequest, MemoResponse, MemoUpdateRequest } from '../types/memo';
import type { PagedResponse, SearchParams } from '../types/common';

export const createMemo = (data: MemoCreateRequest) =>
    client.post<MemoResponse>('/memos', data);

export const getMemo = (id: number) =>
    client.get<MemoResponse>(`/memos/${id}`);

export const getMemos = (params?: SearchParams) =>
    client.get<PagedResponse<MemoResponse>>('/memos', { params });

export const updateMemo = (id: number, data: MemoUpdateRequest) =>
    client.put<MemoResponse>(`/memos/${id}`, data);

export const deleteMemo = (id: number) =>
    client.delete<void>(`/memos/${id}`);
