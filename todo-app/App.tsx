import { StatusBar } from 'expo-status-bar';
import { LinearGradient } from 'expo-linear-gradient';
import { useEffect, useState } from 'react';
import {
  Alert,
  FlatList,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaProvider, SafeAreaView } from 'react-native-safe-area-context';
import Toast from 'react-native-toast-message';

import {
  clearAllApi,
  clearCompletedApi,
  createTodoApi,
  deleteTodoApi,
  fetchTodos,
  toggleTodoApi,
  updateTodoApi,
} from './src/api';
import { Todo, TodoFilter, TodoPriority } from './src/types';
import { showErrorToast, showSuccessToast } from './src/toast';

const priorities: TodoPriority[] = ['high', 'medium', 'low'];
const filters: TodoFilter[] = ['all', 'active', 'completed'];

const priorityColors: Record<TodoPriority, string> = {
  high: '#FF5C7A',
  medium: '#FFC145',
  low: '#4BD0B0',
};

export default function App() {
  const [todos, setTodos] = useState<Todo[]>([]);
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState<TodoPriority>('medium');
  const [filter, setFilter] = useState<TodoFilter>('all');
  const [search, setSearch] = useState('');
  const [editingId, setEditingId] = useState<string | null>(null);
  const [editingValue, setEditingValue] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    let isMounted = true;

    async function loadFromApi() {
      setIsLoading(true);
      try {
        const loadedTodos = await fetchTodos(filter, search);
        if (!isMounted) {
          return;
        }
        setTodos(loadedTodos);
      } catch (error) {
        if (!isMounted) {
          return;
        }
        const message = error instanceof Error ? error.message : 'Unable to connect to todo-app-api';
        showErrorToast('Failed to load tasks', message);
      } finally {
        if (isMounted) {
          setIsLoading(false);
        }
      }
    }

    loadFromApi();

    return () => {
      isMounted = false;
    };
  }, [filter, search]);

  const completedCount = todos.filter((todo) => todo.completed).length;

  async function reloadTodos() {
    const loadedTodos = await fetchTodos(filter, search);
    setTodos(loadedTodos);
  }

  async function handleAddTodo() {
    const trimmed = title.trim();
    if (!trimmed) {
      return;
    }

    try {
      await createTodoApi(trimmed, priority);
      setTitle('');
      setPriority('medium');
      await reloadTodos();
      showSuccessToast('Task added successfully');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Unable to create todo';
      showErrorToast('Failed to add task', message);
    }
  }

  function beginEdit(todo: Todo) {
    setEditingId(todo.id);
    setEditingValue(todo.title);
  }

  async function saveEdit(todoId: string) {
    const trimmed = editingValue.trim();
    if (!trimmed) {
      setEditingId(null);
      setEditingValue('');
      return;
    }

    try {
      await updateTodoApi(todoId, { title: trimmed });
      await reloadTodos();
      setEditingId(null);
      setEditingValue('');
      showSuccessToast('Task updated');
    } catch (error) {
      const message = error instanceof Error ? error.message : 'Unable to update todo';
      showErrorToast('Failed to update task', message);
    }
  }

  async function performClearAllData() {
    await clearAllApi();
    setTodos([]);
    setTitle('');
    setPriority('medium');
    setFilter('all');
    setSearch('');
    setEditingId(null);
    setEditingValue('');
  }

  function handleClearAllData() {
    if (Platform.OS === 'web') {
      const shouldClear = globalThis.confirm?.('Delete all tasks from the API database?') ?? false;
      if (!shouldClear) {
        return;
      }
      performClearAllData().catch((error) => {
        const message = error instanceof Error ? error.message : 'Unable to clear todos';
        showErrorToast('Failed to clear tasks', message);
      });
      return;
    }

    Alert.alert('Reset all todos', 'Delete all tasks from the API database?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Clear',
        style: 'destructive',
        onPress: () => {
          performClearAllData().catch((error) => {
            const message = error instanceof Error ? error.message : 'Unable to clear todos';
            showErrorToast('Failed to clear tasks', message);
          });
        },
      },
    ]);
  }

  return (
    <SafeAreaProvider>
      <LinearGradient colors={['#120528', '#2E1A47', '#1D3557']} style={styles.gradient}>
        <SafeAreaView style={styles.safeArea}>
          <StatusBar style="light" />
          <KeyboardAvoidingView
            behavior={Platform.OS === 'ios' ? 'padding' : undefined}
            style={styles.screen}
          >
            <View style={styles.headerCard}>
              <Text style={styles.title}>Pulse Tasks</Text>
              <Text style={styles.subtitle}>Funky look. Professional output.</Text>
              <Text style={styles.counter}>
                {todos.length - completedCount} open / {completedCount} done
              </Text>
            </View>

          <View style={styles.panel}>
            <TextInput
              placeholder="Add a task that matters"
              placeholderTextColor="#B4A3CE"
              style={styles.input}
              value={title}
              onChangeText={setTitle}
              onSubmitEditing={handleAddTodo}
              returnKeyType="done"
            />
            <View style={styles.rowWrap}>
              {priorities.map((item) => (
                <Pressable
                  key={item}
                  onPress={() => setPriority(item)}
                  style={[
                    styles.chip,
                    priority === item && {
                      borderColor: priorityColors[item],
                      backgroundColor: `${priorityColors[item]}33`,
                    },
                  ]}
                >
                  <Text style={styles.chipText}>{item}</Text>
                </Pressable>
              ))}
              <Pressable style={styles.addButton} onPress={handleAddTodo}>
                <Text style={styles.addButtonText}>Add</Text>
              </Pressable>
            </View>

            <TextInput
              placeholder="Search tasks"
              placeholderTextColor="#B4A3CE"
              style={styles.input}
              value={search}
              onChangeText={setSearch}
            />

            <View style={styles.rowWrap}>
              {filters.map((item) => (
                <Pressable
                  key={item}
                  onPress={() => setFilter(item)}
                  style={[styles.chip, filter === item && styles.selectedFilterChip]}
                >
                  <Text style={styles.chipText}>{item}</Text>
                </Pressable>
              ))}
              <Pressable
                style={styles.secondaryButton}
                onPress={async () => {
                  try {
                    await clearCompletedApi();
                    await reloadTodos();
                    showSuccessToast('Completed tasks cleared');
                  } catch (error) {
                    const message = error instanceof Error ? error.message : 'Unable to clear completed';
                    showErrorToast('Failed to clear completed', message);
                  }
                }}
              >
                <Text style={styles.secondaryButtonText}>Clear done</Text>
              </Pressable>
              <Pressable style={styles.dangerButton} onPress={handleClearAllData}>
                <Text style={styles.dangerButtonText}>Reset all</Text>
              </Pressable>
            </View>
          </View>

            <FlatList
              data={todos}
              keyExtractor={(item) => item.id}
              contentContainerStyle={styles.listContent}
              renderItem={({ item }) => {
              const isEditing = editingId === item.id;

              return (
                <View style={styles.todoCard}>
              <Pressable
                style={[styles.checkbox, item.completed && styles.checkboxChecked]}
                onPress={async () => {
                  try {
                    await toggleTodoApi(item.id);
                    await reloadTodos();
                  } catch (error) {
                    const message = error instanceof Error ? error.message : 'Unable to toggle todo';
                    showErrorToast('Failed to update task', message);
                  }
                }}
              >
                    <Text style={styles.checkboxText}>{item.completed ? '✓' : ''}</Text>
                  </Pressable>

                  <View style={styles.todoMain}>
                    {isEditing ? (
                      <TextInput
                        style={styles.editInput}
                        value={editingValue}
                        onChangeText={setEditingValue}
                        onSubmitEditing={() => {
                          void saveEdit(item.id);
                        }}
                        autoFocus
                      />
                    ) : (
                      <Text style={[styles.todoTitle, item.completed && styles.todoTitleDone]}>
                        {item.title}
                      </Text>
                    )}
                    <View
                      style={[
                        styles.priorityBadge,
                        { backgroundColor: `${priorityColors[item.priority]}22` },
                      ]}
                    >
                      <Text style={[styles.priorityBadgeText, { color: priorityColors[item.priority] }]}>
                        {item.priority.toUpperCase()}
                      </Text>
                    </View>
                  </View>

                  <View style={styles.todoActions}>
                    {isEditing ? (
                      <Pressable
                        onPress={() => {
                          void saveEdit(item.id);
                        }}
                      >
                        <Text style={styles.actionText}>Save</Text>
                      </Pressable>
                    ) : (
                      <Pressable onPress={() => beginEdit(item)}>
                        <Text style={styles.actionText}>Edit</Text>
                      </Pressable>
                    )}
                    <Pressable
                      onPress={async () => {
                        try {
                          await deleteTodoApi(item.id);
                          await reloadTodos();
                          showSuccessToast('Task deleted');
                        } catch (error) {
                          const message = error instanceof Error ? error.message : 'Unable to delete todo';
                          showErrorToast('Failed to delete task', message);
                        }
                      }}
                    >
                      <Text style={styles.deleteText}>Delete</Text>
                    </Pressable>
                  </View>
                </View>
              );
              }}
              ListEmptyComponent={
                <Text style={styles.emptyText}>
                  {isLoading
                    ? 'Loading tasks from API...'
                    : 'No tasks here yet. Add one and ship something awesome.'}
                </Text>
              }
            />
          </KeyboardAvoidingView>
        </SafeAreaView>
      </LinearGradient>
      <Toast />
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  gradient: {
    flex: 1,
  },
  safeArea: {
    flex: 1,
  },
  screen: {
    flex: 1,
    paddingHorizontal: 16,
    paddingBottom: 10,
    gap: 10,
  },
  headerCard: {
    backgroundColor: '#FFFFFF16',
    borderRadius: 18,
    padding: 16,
    borderWidth: 1,
    borderColor: '#FFFFFF24',
  },
  title: {
    color: '#FDFDFF',
    fontSize: 32,
    fontWeight: '800',
  },
  subtitle: {
    color: '#D8CCF2',
    marginTop: 4,
    fontSize: 14,
  },
  counter: {
    color: '#B5F4E7',
    marginTop: 8,
    fontWeight: '700',
  },
  panel: {
    backgroundColor: '#FFFFFF14',
    borderRadius: 18,
    padding: 14,
    borderWidth: 1,
    borderColor: '#FFFFFF20',
    gap: 10,
  },
  input: {
    backgroundColor: '#FFFFFF1A',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#FFFFFF2A',
    color: '#F8F5FF',
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 15,
  },
  rowWrap: {
    flexDirection: 'row',
    gap: 8,
    flexWrap: 'wrap',
    alignItems: 'center',
  },
  chip: {
    borderWidth: 1,
    borderColor: '#FFFFFF3A',
    borderRadius: 999,
    paddingHorizontal: 10,
    paddingVertical: 6,
    backgroundColor: '#FFFFFF12',
  },
  selectedFilterChip: {
    backgroundColor: '#7CE2F233',
    borderColor: '#7CE2F2',
  },
  chipText: {
    color: '#EDE7FB',
    fontWeight: '600',
    textTransform: 'capitalize',
  },
  addButton: {
    marginLeft: 'auto',
    borderRadius: 999,
    backgroundColor: '#4BD0B0',
    paddingHorizontal: 16,
    paddingVertical: 8,
  },
  addButtonText: {
    color: '#113A33',
    fontWeight: '800',
  },
  secondaryButton: {
    marginLeft: 'auto',
    borderRadius: 999,
    borderWidth: 1,
    borderColor: '#FFFFFF4A',
    paddingHorizontal: 12,
    paddingVertical: 7,
  },
  secondaryButtonText: {
    color: '#F3EBFF',
    fontWeight: '700',
  },
  dangerButton: {
    borderRadius: 999,
    borderWidth: 1,
    borderColor: '#FF8AA066',
    backgroundColor: '#FF8AA022',
    paddingHorizontal: 12,
    paddingVertical: 7,
  },
  dangerButtonText: {
    color: '#FFB6C3',
    fontWeight: '700',
  },
  listContent: {
    paddingBottom: 30,
    gap: 10,
  },
  todoCard: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: '#FFFFFF24',
    backgroundColor: '#FFFFFF10',
    padding: 10,
  },
  checkbox: {
    width: 24,
    height: 24,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#EADFFB',
    alignItems: 'center',
    justifyContent: 'center',
  },
  checkboxChecked: {
    backgroundColor: '#4BD0B0',
    borderColor: '#4BD0B0',
  },
  checkboxText: {
    color: '#102F28',
    fontWeight: '900',
  },
  todoMain: {
    flex: 1,
    gap: 5,
  },
  todoTitle: {
    color: '#F8F6FF',
    fontSize: 16,
    fontWeight: '700',
  },
  todoTitleDone: {
    textDecorationLine: 'line-through',
    color: '#C3B9DA',
  },
  editInput: {
    borderWidth: 1,
    borderColor: '#FFFFFF3D',
    borderRadius: 10,
    color: '#FFFFFF',
    paddingHorizontal: 10,
    paddingVertical: 6,
  },
  priorityBadge: {
    alignSelf: 'flex-start',
    borderRadius: 999,
    paddingHorizontal: 8,
    paddingVertical: 3,
  },
  priorityBadgeText: {
    fontSize: 11,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  todoActions: {
    gap: 8,
    alignItems: 'flex-end',
  },
  actionText: {
    color: '#7CE2F2',
    fontWeight: '700',
  },
  deleteText: {
    color: '#FF8AA0',
    fontWeight: '700',
  },
  emptyText: {
    textAlign: 'center',
    marginTop: 26,
    color: '#D5CBEA',
    fontSize: 15,
  },
});
