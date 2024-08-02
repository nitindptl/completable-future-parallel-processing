package org.example;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CustomCompletableFuture<T> extends CompletableFuture {
	private CompletableFuture<T> completableFuture;

	public static <T> CompletableFuture<T> supplyWithExecutor(Supplier<T> supplier,
		Executor executor) {
		return CompletableFuture.supplyAsync(supplier, executor);
	}

	public static <T> CustomCompletableFuture<T> supply(Supplier<T> supplier, Executor executor) {
		CustomCompletableFuture<T> customCompletableFuture = new CustomCompletableFuture<>();
		customCompletableFuture.completableFuture =
			CompletableFuture.supplyAsync(supplier, executor);
		return customCompletableFuture;
	}

	public static <T> CompletableFuture<T> supplyAsyncWithRetry(Supplier<T> supplier, int retry) {
		return retry(() -> CompletableFuture.supplyAsync(supplier), retry, 1);
	}

	public static <T> CompletableFuture<T> supplyAsyncWithRetryAndExecutor(Supplier<T> supplier,
		Executor executor, int retry) {
		return retry(() -> CompletableFuture.supplyAsync(supplier, executor), retry, 1);
	}

	public static <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier, Executor executor,
		int retry) {
		return retry(() -> CompletableFuture.supplyAsync(supplier, executor), retry, 1)
			.handle((result, throwable) -> {
				if (throwable != null) {
					return result;
				}
				return result;
			});
	}

	private static <T> CompletableFuture<T> retry(Supplier<CompletableFuture<T>> operation,
		int maxRetryAttempts, int retryCount) {
		CompletableFuture<T> future = new CompletableFuture<>();
		retryHelper(operation, maxRetryAttempts, future, retryCount);
		return future;
	}

	private static <T> void retryHelper(Supplier<CompletableFuture<T>> operation,
		int remainingAttempts, CompletableFuture<T> future, int retry) {
		operation.get()
			.whenComplete((result, ex) -> {
				if (ex == null) {
					future.complete(result);
				} else if (remainingAttempts > 0) {
					retryHelper(operation, remainingAttempts - 1, future, retry + 1);
				} else {
					future.completeExceptionally(ex);
				}
			});
	}

	public static <T> List<T> getAsyncData(List<CompletableFuture<T>> future) {
		return future.stream()
			.map(cf -> {
				try {
					return cf.join();
				} catch (CompletionException | CancellationException ex) {
					ex.printStackTrace();
					throw ex;
				}
			})
			.collect(Collectors.toList());
	}
}
