import { describe, expect, it } from 'vitest';
import {
  clearCompleted,
  createTodo,
  deleteTodo,
  filterAndSearchTodos,
  toggleTodo,
  updateTodoTitle,
} from './todoUtils';
import { Todo } from './types';
describe('todoUtils', () => {
  it('creates a todo with trimmed title', () => {
    const todo = createTodo('  Ship mobile app  ', 'high');
    expect(todo.title).toBe('Ship mobile app');
    expect(todo.completed).toBe(false);
    expect(todo.priority).toBe('high');
  });
  it('throws for empty title', () => {
    expect(() => createTodo('   ')).toThrowError();
  });
  it('supports toggle, update and delete', () => {
    const initial: Todo[] = [
      { id: 'a', title: 'One', completed: false, priority: 'low', createdAt: 2 },
      { id: 'b', title: 'Two', completed: true, priority: 'medium', createdAt: 1 },
    ];
    const toggled = toggleTodo(initial, 'a');
    expect(toggled[0].completed).toBe(true);
    const updated = updateTodoTitle(toggled, 'a', ' Updated ');
    expect(updated[0].title).toBe('Updated');
    const deleted = deleteTodo(updated, 'b');
    expect(deleted).toHaveLength(1);
  });
  it('filters, searches, and clears completed', () => {
    const initial: Todo[] = [
      { id: 'a', title: 'Write docs', completed: false, priority: 'low', createdAt: 1 },
      { id: 'b', title: 'Deploy app', completed: true, priority: 'high', createdAt: 2 },
    ];
    expect(filterAndSearchTodos(initial, 'active', '')).toHaveLength(1);
    expect(filterAndSearchTodos(initial, 'all', 'deploy')).toHaveLength(1);
    expect(clearCompleted(initial)).toHaveLength(1);
  });
});
