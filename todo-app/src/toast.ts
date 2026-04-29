import Toast from 'react-native-toast-message';

export type ToastType = 'success' | 'error' | 'info';

export interface ToastConfig {
  type: ToastType;
  text1: string;
  text2?: string;
  duration?: number;
  position?: 'top' | 'bottom';
}

/**
 * Show a toast notification
 */
export function showToast(config: ToastConfig): void {
  const durationMs = config.duration ?? 3000;
  Toast.show({
    type: config.type,
    text1: config.text1,
    text2: config.text2,
    position: config.position ?? 'top',
    topOffset: 50,
  });
  // Auto-hide after duration
  setTimeout(() => {
    Toast.hide();
  }, durationMs);
}

/**
 * Show success toast
 */
export function showSuccessToast(text: string, duration = 2000): void {
  showToast({ type: 'success', text1: text, duration });
}

/**
 * Show error toast
 */
export function showErrorToast(text: string, subtext?: string): void {
  showToast({ type: 'error', text1: text, text2: subtext, duration: 4000 });
}

/**
 * Show info toast
 */
export function showInfoToast(text: string): void {
  showToast({ type: 'info', text1: text });
}

