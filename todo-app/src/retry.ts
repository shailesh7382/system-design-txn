/**
 * Retry policy with exponential backoff
 */
export interface RetryConfig {
  maxAttempts: number;
  initialDelayMs: number;
  maxDelayMs: number;
  backoffMultiplier: number;
}

const DEFAULT_RETRY_CONFIG: RetryConfig = {
  maxAttempts: 3,
  initialDelayMs: 300,
  maxDelayMs: 5000,
  backoffMultiplier: 2,
};

/**
 * Sleep for a given number of milliseconds
 */
function sleep(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Retry a function with exponential backoff
 * @param fn - The async function to retry
 * @param config - Retry configuration
 * @returns The result of the function
 */
export async function withRetry<T>(
  fn: () => Promise<T>,
  config: Partial<RetryConfig> = {},
): Promise<T> {
  const mergedConfig = { ...DEFAULT_RETRY_CONFIG, ...config };
  let lastError: Error | null = null;
  let delay = mergedConfig.initialDelayMs;

  for (let attempt = 1; attempt <= mergedConfig.maxAttempts; attempt++) {
    try {
      return await fn();
    } catch (error) {
      lastError = error instanceof Error ? error : new Error(String(error));

      // Don't retry on client errors (4xx) except 408 (Request Timeout)
      if (error instanceof Error && error.message.match(/^Request failed: 4[0-9]{2}/)) {
        const statusMatch = error.message.match(/4([0-9]{2})/);
        const status = statusMatch ? `4${statusMatch[1]}` : '4xx';
        if (status !== '408') {
          throw lastError; // Don't retry 4xx errors (except 408)
        }
      }

      if (attempt < mergedConfig.maxAttempts) {
        await sleep(delay);
        delay = Math.min(delay * mergedConfig.backoffMultiplier, mergedConfig.maxDelayMs);
      }
    }
  }

  throw lastError || new Error('Max retry attempts reached');
}

