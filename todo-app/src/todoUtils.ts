import { Todo, TodoFilter, TodoPriority } from './types';

export function createTodo(title: string, priority: TodoPriority = 'medium'): Todo {
  const trimmed = title.trim();
  if (!trimmed) {
    throw new Error('Todo title cannot be empty.');
  }

  return {
    id: `${Date.now()}-${Math.random().toString(16).slice(2)}`,
    title: trimmed,
    completed: false,
    priority,
    createdAt: Date.now(),
  };
}

export function toggleTodo(todos: Todo[], id: string): Todo[] {
  return todos.map((todo) =>
    todo.id === id ? { ...todo, completed: !todo.completed } : todo,
  );
}

export function deleteTodo(todos: Todo[], id: string): Todo[] {
  return todos.filter((todo) => todo.id !== id);
}

export function updateTodoTitle(todos: Todo[], id: string, title: string): Todo[] {
  const trimmed = title.trim();
  if (!trimmed) {
    return todos;
  }

  return todos.map((todo) => (todo.id === id ? { ...todo, title: trimmed } : todo));
}

export function clearCompleted(todos: Todo[]): Todo[] {
  return todos.filter((todo) => !todo.completed);
}

export function filterAndSearchTodos(
  todos: Todo[],
  filter: TodoFilter,
  searchTerm: string,
): Todo[] {
  const normalizedSearch = searchTerm.trim().toLowerCase();

  return todos
    .filter((todo) => {
      if (filter === 'active') {
        return !todo.completed;
      }
      if (filter === 'completed') {
        return todo.completed;
      }
      return true;
    })
    .filter((todo) =>
      normalizedSearch ? todo.title.toLowerCase().includes(normalizedSearch) : true,
    )
    .sort((a, b) => b.createdAt - a.createdAt);
}

