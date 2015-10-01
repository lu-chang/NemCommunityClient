package org.nem.ncc;

import org.nem.core.async.*;
import org.nem.core.time.TimeProvider;
import org.nem.ncc.cache.*;
import org.nem.ncc.time.synchronization.NccTimeSynchronizer;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Scheduler that keeps track of all scheduled, recurring timers.
 */
public class NccScheduler implements AutoCloseable {
	private static final int ONE_SECOND = 1000;
	private static final int ONE_MINUTE = 60 * ONE_SECOND;

	private static final int TIME_SYNC_INITIAL_DELAY = 10 * ONE_SECOND;
	private static final int TIME_SYNC_INTERVAL = ONE_MINUTE;

	private static final int CACHE_UPDATE_INITIAL_DELAY = 3 * ONE_SECOND;
	private static final int CACHE_UPDATE_INTERVAL = 3 * ONE_SECOND;

	private final TimeProvider timeProvider;
	private final Optional<Integer> customInitialDelay;
	private final List<NemAsyncTimerVisitor> timerVisitors = new ArrayList<>();
	private final List<AsyncTimer> timers = new ArrayList<>();

	/**
	 * Creates a new scheduler.
	 *
	 * @param timeProvider The time provider.
	 */
	public NccScheduler(final TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
		this.customInitialDelay = Optional.empty();
	}

	/**
	 * Creates a new scheduler with a custom initial delay.
	 *
	 * @param timeProvider The time provider.
	 * @param initialDelay The custom initial delay.
	 */
	public NccScheduler(final TimeProvider timeProvider, final int initialDelay) {
		this.timeProvider = timeProvider;
		this.customInitialDelay = Optional.of(initialDelay);
	}

	/**
	 * Gets all timer visitors.
	 *
	 * @return All timer visitors.
	 */
	public List<NemAsyncTimerVisitor> getVisitors() {
		return this.timerVisitors;
	}

	/**
	 * Adds the NCC time synchronization task.
	 *
	 * @param nccTimeSynchronizer The time synchronizer.
	 */
	public void addTimeSynchronizationTask(final NccTimeSynchronizer nccTimeSynchronizer) {
		final AsyncTimerVisitor timerVisitor = this.createNamedVisitor("TIME SYNCHRONIZATION");
		final AsyncTimerOptions options = new AsyncTimerOptionsBuilder()
				.setRecurringFutureSupplier(nccTimeSynchronizer::synchronizeTime)
				.setInitialDelay(this.customInitialDelay.orElse(TIME_SYNC_INITIAL_DELAY))
				.setDelayStrategy(new UniformDelayStrategy(TIME_SYNC_INTERVAL))
				.setVisitor(timerVisitor)
				.create();
		this.timers.add(new AsyncTimer(options));
	}

	/**
	 * Adds the NCC account cache update task.
	 *
	 * @param cache The ncc account cache.
	 * @param repository The ncc account file repository.
	 */
	public void addAccountCacheUpdateTask(final NccAccountCache cache, final AccountsFileRepository repository) {
		final AsyncTimerVisitor timerVisitor = this.createNamedVisitor("CACHE UPDATE");
		final AsyncTimerOptions options = new AsyncTimerOptionsBuilder()
				.setRecurringFutureSupplier(
						() -> cache.updateCache()
								.thenCompose(v -> CompletableFuture.runAsync(() -> repository.save(cache.getAccounts()))))
				.setInitialDelay(this.customInitialDelay.orElse(CACHE_UPDATE_INITIAL_DELAY))
				.setDelayStrategy(new UniformDelayStrategy(CACHE_UPDATE_INTERVAL))
				.setVisitor(timerVisitor)
				.create();
		this.timers.add(new AsyncTimer(options));
	}

	/**
	 * Gets the number of timers currently scheduled to run.
	 *
	 * @return The number of timers currently scheduled to run.
	 */
	public int getNumScheduledTimers() {
		return (int)this.timers.stream().filter(t -> !t.isStopped()).count();
	}

	@Override
	public void close() {
		this.timers.forEach(AsyncTimer::close);
	}

	private NemAsyncTimerVisitor createNamedVisitor(final String name) {
		final NemAsyncTimerVisitor timerVisitor = new NemAsyncTimerVisitor(name, this.timeProvider);
		this.timerVisitors.add(timerVisitor);
		return timerVisitor;
	}
}
