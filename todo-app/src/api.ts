import { Platform } from 'react-native';

import { Todo, TodoFilter, TodoPriority } from './types';
import { withRetry } from './retry';

type ApiPriority = 'HIGH' | 'MEDIUM' | 'LOW';

type ApiTodo = {
  id: string;
  title: string;
  completed: boolean;
  priority: ApiPriority;
  createdAt: string;
};

const API_BASE_URL =
  process.env.EXPO_PUBLIC_API_BASE_URL ??
  (Platform.OS === 'android' ? 'http://10.0.2.2:8080' : 'http://localhost:8080');

function toApiPriority(priority: TodoPriority): ApiPriority {
  if (priority === 'high') {
    return 'HIGH';
  }
  if (priority === 'low') {
    return 'LOW';
  }
  return 'MEDIUM';
}

function fromApiPriority(priority: ApiPriority): TodoPriority {
  if (priority === 'HIGH') {
    return 'high';
  }
  if (priority === 'LOW') {
    return 'low';
  }
  return 'medium';
}

function mapTodo(apiTodo: ApiTodo): Todo {
  return {
    id: apiTodo.id,
    title: apiTodo.title,
    completed: apiTodo.completed,
    priority: fromApiPriority(apiTodo.priority),
    createdAt: new Date(apiTodo.createdAt).getTime(),
  };
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  return withRetry(
    async () => {
      const response = await fetch(`${API_BASE_URL}${path}`, {
        headers: {
          'Content-Type': 'application/json',
          ...(init?.headers ?? {}),
        },
        ...init,
      });

      if (!response.ok) {
        const text = await response.text();
        throw new Error(text || `Request failed: ${response.status}`);
      }

      if (response.status === 204) {
        return undefined as T;
      }

      const contentType = response.headers.get('content-type') ?? '';
      if (!contentType.includes('application/json')) {
        return undefined as T;
      }

      return (await response.json()) as T;
    },
    { maxAttempts: 3, initialDelayMs: 300, maxDelayMs: 5000, backoffMultiplier: 2 },
  );
}

export async function fetchTodos(filter: TodoFilter, search: string): Promise<Todo[]> {
  const params = new URLSearchParams({ filter, search });
  const apiTodos = await request<ApiTodo[]>(`/api/todos?${params.toString()}`);
  return apiTodos.map(mapTodo);
}

export async function createTodoApi(title: string, priority: TodoPriority): Promise<Todo> {
  const body = JSON.stringify({ title, priority: toApiPriority(priority) });
  const apiTodo = await request<ApiTodo>('/api/todos', { method: 'POST', body });
  return mapTodo(apiTodo);
}

export async function updateTodoApi(
  id: string,
  patch: { title?: string; completed?: boolean; priority?: TodoPriority },
): Promise<Todo> {
  const body = JSON.stringify({
    ...(patch.title !== undefined ? { title: patch.title } : {}),
    ...(patch.completed !== undefined ? { completed: patch.completed } : {}),
    ...(patch.priority !== undefined ? { priority: toApiPriority(patch.priority) } : {}),
  });

  const apiTodo = await request<ApiTodo>(`/api/todos/${id}`, { method: 'PATCH', body });
  return mapTodo(apiTodo);
}

export async function toggleTodoApi(id: string): Promise<Todo> {
  const apiTodo = await request<ApiTodo>(`/api/todos/${id}/toggle`, { method: 'PATCH' });
  return mapTodo(apiTodo);
}

export async function deleteTodoApi(id: string): Promise<void> {
  await request<void>(`/api/todos/${id}`, { method: 'DELETE' });
}

export async function clearCompletedApi(): Promise<void> {
  await request<{ deleted: number }>('/api/todos/completed', { method: 'DELETE' });
}

export async function clearAllApi(): Promise<void> {
  await request<void>('/api/todos', { method: 'DELETE' });
}

export { API_BASE_URL };

