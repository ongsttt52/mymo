import client from './client';
import type {
    DailyLogCreateRequest,
    DailyLogResponse,
    DailyLogUpdateRequest,
} from '../types/dailyLog';

export const createDailyLog = (data: DailyLogCreateRequest) =>
    client.post<DailyLogResponse>('/daily-logs', data);

export const getDailyLog = (id: number) =>
    client.get<DailyLogResponse>(`/daily-logs/${id}`);

export const getDailyLogs = () =>
    client.get<DailyLogResponse[]>('/daily-logs');

export const updateDailyLog = (id: number, data: DailyLogUpdateRequest) =>
    client.put<DailyLogResponse>(`/daily-logs/${id}`, data);

export const deleteDailyLog = (id: number) =>
    client.delete<void>(`/daily-logs/${id}`);
