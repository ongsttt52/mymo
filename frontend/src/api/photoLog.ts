import client from './client';
import type {
    PhotoLogCreateRequest,
    PhotoLogResponse,
    PhotoLogUpdateRequest,
} from '../types/photoLog';
import type { DateRangeSearchParams, PagedResponse } from '../types/common';

export const createPhotoLog = (data: PhotoLogCreateRequest) =>
    client.post<PhotoLogResponse>('/photo-logs', data);

export const getPhotoLog = (id: number) =>
    client.get<PhotoLogResponse>(`/photo-logs/${id}`);

export const getPhotoLogs = (params?: DateRangeSearchParams) =>
    client.get<PagedResponse<PhotoLogResponse>>('/photo-logs', { params });

export const updatePhotoLog = (id: number, data: PhotoLogUpdateRequest) =>
    client.put<PhotoLogResponse>(`/photo-logs/${id}`, data);

export const deletePhotoLog = (id: number) =>
    client.delete<void>(`/photo-logs/${id}`);
