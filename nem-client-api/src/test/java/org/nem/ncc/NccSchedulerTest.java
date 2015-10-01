package org.nem.ncc;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.Mockito;
import org.nem.core.time.TimeProvider;
import org.nem.core.utils.ExceptionUtils;
import org.nem.ncc.cache.*;
import org.nem.ncc.time.synchronization.NccTimeSynchronizer;

public class NccSchedulerTest {

	@Test
	public void initiallyNoTasksAreStarted() {
		// Act:
		try (final NccScheduler scheduler = new NccScheduler(Mockito.mock(TimeProvider.class))) {
			// Assert:
			Assert.assertThat(scheduler.getNumScheduledTimers(), IsEqual.equalTo(0));
			Assert.assertThat(scheduler.getVisitors().size(), IsEqual.equalTo(0));
		}
	}

	@Test
	public void addTimeSynchronizationTaskAddsTimeSynchronizationTask() {
		// Arrange:
		try (final NccScheduler scheduler = new NccScheduler(Mockito.mock(TimeProvider.class))) {
			// Act:
			scheduler.addTimeSynchronizationTask(Mockito.mock(NccTimeSynchronizer.class));

			// Assert:
			Assert.assertThat(scheduler.getNumScheduledTimers(), IsEqual.equalTo(1));
			Assert.assertThat(scheduler.getVisitors().size(), IsEqual.equalTo(1));
			Assert.assertThat(scheduler.getVisitors().get(0).getTimerName(), IsEqual.equalTo("TIME SYNCHRONIZATION"));
		}
	}

	@Test
	public void addAccountCacheUpdateTaskAddsAccountCacheUpdateTask() {
		// Arrange:
		try (final NccScheduler scheduler = new NccScheduler(Mockito.mock(TimeProvider.class))) {
			// Act:
			scheduler.addAccountCacheUpdateTask(Mockito.mock(NccAccountCache.class), Mockito.mock(AccountsFileRepository.class));

			// Assert:
			Assert.assertThat(scheduler.getNumScheduledTimers(), IsEqual.equalTo(1));
			Assert.assertThat(scheduler.getVisitors().size(), IsEqual.equalTo(1));
			Assert.assertThat(scheduler.getVisitors().get(0).getTimerName(), IsEqual.equalTo("CACHE UPDATE"));
		}
	}

	@Test
	public void multipleTasksCanBeScheduled() {
		// Arrange:
		try (final NccScheduler scheduler = new NccScheduler(Mockito.mock(TimeProvider.class))) {
			// Act:
			addAllTasks(scheduler);

			// Assert:
			Assert.assertThat(scheduler.getNumScheduledTimers(), IsEqual.equalTo(2));
		}
	}

	@Test
	public void closeTransitionsAllTimersToStopped() {
		// Arrange:
		try (final NccScheduler scheduler = new NccScheduler(Mockito.mock(TimeProvider.class), 100)) {
			addAllTasks(scheduler);

			// Act:
			scheduler.close();
			ExceptionUtils.propagateVoid(() -> Thread.sleep(250));

			// Assert:
			Assert.assertThat(scheduler.getNumScheduledTimers(), IsEqual.equalTo(0));
		}
	}

	private static void addAllTasks(final NccScheduler scheduler) {
		scheduler.addTimeSynchronizationTask(Mockito.mock(NccTimeSynchronizer.class));
		scheduler.addAccountCacheUpdateTask(Mockito.mock(NccAccountCache.class), Mockito.mock(AccountsFileRepository.class));
	}
}
