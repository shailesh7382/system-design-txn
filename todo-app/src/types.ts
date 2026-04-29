export type TodoPriority = 'high' | 'medium' | 'low';

export type TodoFilter = 'all' | 'active' | 'completed';

export type Todo = {
  id: string;
  title: string;
  completed: boolean;
  priority: TodoPriority;
  createdAt: number;
};

