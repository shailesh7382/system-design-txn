import AsyncStorage from '@react-native-async-storage/async-storage';
import { Todo } from './types';
const TODO_STORAGE_KEY = 'funky-professional-todos-v1';
export async function loadTodos(): Promise<Todo[]> {
  const raw = await AsyncStorage.getItem(TODO_STORAGE_KEY);
  if (!raw) {
    return [];
  }
  try {
    const parsed = JSON.parse(raw) as Todo[];
    if (!Array.isArray(parsed)) {
      return [];
    }
    return parsed;
  } catch {
    return [];
  }
}
export async function saveTodos(todos: Todo[]): Promise<void> {
  await AsyncStorage.setItem(TODO_STORAGE_KEY, JSON.stringify(todos));
}

export async function clearTodosStorage(): Promise<void> {
  await AsyncStorage.removeItem(TODO_STORAGE_KEY);
}

