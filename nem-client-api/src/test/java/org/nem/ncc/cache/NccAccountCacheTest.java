package org.nem.ncc.cache;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.*;
import org.nem.core.connect.ErrorResponse;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.time.*;
import org.nem.ncc.exceptions.NisException;
import org.nem.ncc.services.AccountServices;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NccAccountCacheTest {
	private static final TimeInstant START_TIME = new TimeInstant(112);
	private static final TimeInstant START_TIME_PLUS_REFRESH_INTERVAL = new TimeInstant(172);
	private static final TimeInstant REFRESH_INTERVAL = new TimeInstant(60);

	//region findByAddress

	@Test
	public void findByAddressDelegatesToAccountServicesIfAccountNotInCache() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final Account account = context.cache.findByAddress(context.address);

		// Assert:
		assertAreEqual(context.pair1.getEntity(), account);
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(context.address);
		Mockito.verify(context.timeProvider, Mockito.times(1)).getCurrentTime();
	}

	@Test
	public void findByAddressUsesCachedValueIfCachedAccountIsExpired() {
		// Assert:
		assertFindByAddressUsesCachedValueIfPresent(START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(1));
	}

	@Test
	public void findByAddressUsesCachedValueIfCachedAccountIsNotExpired() {
		// Assert:
		assertFindByAddressUsesCachedValueIfPresent(START_TIME.addSeconds(1));
	}

	private static void assertFindByAddressUsesCachedValueIfPresent(final TimeInstant queryTime) {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime())
				.thenReturn(START_TIME, queryTime);

		// Act:
		context.cache.findByAddress(context.address);
		final Account account = context.cache.findByAddress(context.address);

		// Assert:
		assertAreEqual(context.pair1.getEntity(), account);
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(context.address);
		Mockito.verify(context.timeProvider, Mockito.times(1)).getCurrentTime();
	}

	@Test
	public void cacheAutomaticallyMapsAccountInfoToAccount() {
		// Arrange:
		final Address address = Utils.generateRandomAddressWithPublicKey();
		final AccountInfo accountInfo = Utils.createAccountInfoFromAddress(address);

		final TestContext context = new TestContext();
		Mockito.when(context.accountServices.getAccountMetaDataPair(address))
				.thenReturn(new AccountMetaDataPair(accountInfo, null));

		// Act:
		final Account account = context.cache.findByAddress(address);

		// Assert:
		Assert.assertThat(account.getAddress(), IsEqual.equalTo(address));
		Assert.assertThat(account.getAddress().getPublicKey(), IsEqual.equalTo(address.getPublicKey()));
	}

	//endregion

	//region isKnownAddress

	@Test
	public void isKnownAddressReturnsTrueIfAccountIsCachedAndExpired() {
		// Assert:
		assertIsKnownAddressReturnsTrueAtTime(START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(1));
	}

	@Test
	public void isKnownAddressReturnsTrueIfAccountIsCachedAndNotExpired() {
		// Assert:
		assertIsKnownAddressReturnsTrueAtTime(START_TIME.addSeconds(1));
	}

	private static void assertIsKnownAddressReturnsTrueAtTime(final TimeInstant queryTime) {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime())
				.thenReturn(START_TIME, queryTime);

		// Act:
		context.cache.findByAddress(context.address);
		final boolean result = context.cache.isKnownAddress(context.address);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(true));
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(Mockito.any());
		Mockito.verify(context.timeProvider, Mockito.times(1)).getCurrentTime();
	}

	@Test
	public void isKnownAddressReturnsFalseIfAccountIsNotCached() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final boolean result = context.cache.isKnownAddress(context.address);

		// Assert:
		Assert.assertThat(result, IsEqual.equalTo(false));
		Mockito.verify(context.accountServices, Mockito.times(0)).getAccountMetaDataPair(Mockito.any());
		Mockito.verify(context.timeProvider, Mockito.times(0)).getCurrentTime();
	}

	//endregion

	//region findPairByAddress

	@Test
	public void findPairByAddressDelegatesToAccountServicesIfAccountNotInCache() {
		// Arrange:
		final TestContext context = new TestContext();

		// Act:
		final AccountMetaDataPair pair = context.cache.findPairByAddress(context.address);

		// Assert:
		Assert.assertThat(pair, IsEqual.equalTo(context.pair1));
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(context.address);
		Mockito.verify(context.timeProvider, Mockito.times(1)).getCurrentTime();
	}

	@Test
	public void findPairByAddressUsesCachedValueIfCachedAccountIsExpired() {
		// Assert:
		assertFindPairByAddressUsesCachedValueIfPresent(START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(1));
	}

	@Test
	public void findPairByAddressUsesCachedValueIfCachedAccountIsNotExpired() {
		// Assert:
		assertFindPairByAddressUsesCachedValueIfPresent(START_TIME.addSeconds(1));
	}

	private static void assertFindPairByAddressUsesCachedValueIfPresent(final TimeInstant queryTime) {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime())
				.thenReturn(START_TIME, queryTime);

		// Act:
		context.cache.findPairByAddress(context.address);
		final AccountMetaDataPair pair = context.cache.findPairByAddress(context.address);

		// Assert:
		Assert.assertThat(pair, IsEqual.equalTo(context.pair1));
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(context.address);
		Mockito.verify(context.timeProvider, Mockito.times(1)).getCurrentTime();
	}

	//endregion

	//region getAccounts

	@Test
	public void getAccountsReturnsAllAccountsInCache() {
		// Arrange:
		final TestContext context = new TestContext();

		// add three accounts to the cache
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(3);
		context.addAccountsToCache(accounts);

		// Assert:
		Assert.assertThat(context.cache.getAccounts(), IsEquivalent.equivalentTo(accounts));
	}

	//endregion

	//region seedAccounts

	@Test
	public void allSeedAccountsAreAddedToCache() {
		// Arrange:
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(3);
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime()).thenReturn(REFRESH_INTERVAL.addSeconds(3));
		context.setAccountServicesFailure(); // fail the services lookup so that the seed account is not replaced

		// Act:
		context.cache.seedAccounts(accounts);

		// Assert:
		for (final AccountInfo account : accounts) {
			final AccountMetaDataPair pair = context.cache.findPairByAddress(account.getAddress());
			Assert.assertThat(pair.getEntity(), IsEqual.equalTo(account));
		}
	}

	@Test
	public void allSeedAccountsHaveUnknownStatus() {
		// Assert:
		assertSeedAccountMetadata(metaData -> Assert.assertThat(metaData.getStatus(), IsEqual.equalTo(AccountStatus.UNKNOWN)));
	}

	@Test
	public void allSeedAccountsHaveInactiveRemoteStatus() {
		// Assert:
		assertSeedAccountMetadata(metaData -> Assert.assertThat(metaData.getRemoteStatus(), IsEqual.equalTo(AccountRemoteStatus.INACTIVE)));
	}

	private static void assertSeedAccountMetadata(final Consumer<AccountMetaData> assertMetaData) {
		// Arrange:
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(3);
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime()).thenReturn(REFRESH_INTERVAL.addSeconds(3));
		context.setAccountServicesFailure(); // fail the services lookup so that the seed account is not replaced

		// Act:
		context.cache.seedAccounts(accounts);

		// Assert:
		for (final AccountInfo account : accounts) {
			final AccountMetaDataPair pair = context.cache.findPairByAddress(account.getAddress());
			assertMetaData.accept(pair.getMetaData());
		}
	}

	//endregion

	//region disconnected lookups

	@Test
	public void lookupFailsIfAccountIsNotInCacheAndNisIsDisconnected() {
		// Arrange:
		final TestContext context = new TestContext();
		context.setAccountServicesFailure(); // fail the services lookup

		// Act:
		ExceptionAssert.assertThrows(
				v -> context.cache.findPairByAddress(Utils.generateRandomAddress()),
				NisException.class);

		// Assert: the request went to the underlying account services (which failed)
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(Mockito.any());
	}

	@Test
	public void lookupSucceedsIfAccountIsInCacheAndNisIsDisconnected() {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.timeProvider.getCurrentTime()).thenReturn(START_TIME, START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(1000));
		context.cache.findPairByAddress(context.address); // initially cache the account
		context.setAccountServicesFailure(); // fail the services lookup

		// Act: retrieve the (expired) pair
		final AccountMetaDataPair pair = context.cache.findPairByAddress(context.address);

		// Assert: the expired pair is returned
		Assert.assertThat(pair, IsEqual.equalTo(context.pair1));

		// Assert: the request went to the underlying account services (1 - initial cache)
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPair(Mockito.any());
	}

	//endregion

	//region updateCache

	@Test
	public void firstUpdateAlwaysUpdatesSeedAccounts() {
		// Arrange:
		// - seed two accounts in the cache
		final TestContext context = new TestContext();
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(2);
		Mockito.when(context.timeProvider.getCurrentTime()).thenReturn(
				REFRESH_INTERVAL, // the time at which the first account should be added
				REFRESH_INTERVAL.addSeconds(120), // the time at which the second account should be added
				REFRESH_INTERVAL.addSeconds(130)); // the time at which the update should happen
		context.cache.seedAccounts(accounts);
		Mockito.when(context.accountServices.getAccountMetaDataPairs(Mockito.any()))
				.thenReturn(Collections.singletonList(context.pair1));

		// Act:
		context.cache.updateCache().join();

		// Assert:
		// - both accounts were updated at once because seed accounts are added to the cache with an expired timestamp
		final List<SerializableAccountId> expectedRequests = accounts.stream()
				.map(a -> new SerializableAccountId(a.getAddress()))
				.collect(Collectors.toList());
		final ArgumentCaptor<? extends List<SerializableAccountId>> captor = createAccountIdListCaptor();
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPairs(captor.capture());
		Assert.assertThat(captor.getValue(), IsEquivalent.equivalentTo(expectedRequests));
	}

	@Test
	public void updateUpdatesAllExpiredAccounts() {
		// Arrange:
		// - add two accounts to the cache
		final TestContext context = new TestContext();
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(2);
		context.addAccountsToCache(accounts);

		// - mark them as expired and return the context accounts from the services
		Mockito.when(context.timeProvider.getCurrentTime())
				.thenReturn(START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(1));
		Mockito.when(context.accountServices.getAccountMetaDataPairs(Mockito.any()))
				.thenReturn(Arrays.asList(context.pair1, context.pair2));

		// Act:
		context.cache.updateCache().join();

		// Assert:
		// - both accounts were updated at once
		final List<SerializableAccountId> expectedRequests = accounts.stream()
				.map(a -> new SerializableAccountId(a.getAddress()))
				.collect(Collectors.toList());
		final ArgumentCaptor<? extends List<SerializableAccountId>> captor = createAccountIdListCaptor();
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPairs(captor.capture());
		Assert.assertThat(captor.getValue(), IsEquivalent.equivalentTo(expectedRequests));
	}

	@Test
	public void updateDoesNotUpdateUnexpiredAccounts() {
		// Arrange:
		// - add two accounts to the cache
		final TestContext context = new TestContext();
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(2);
		Mockito.when(context.timeProvider.getCurrentTime()).thenReturn(
				REFRESH_INTERVAL, // the time at which the first account should be added
				REFRESH_INTERVAL.addSeconds(120), // the time at which the second account should be added
				REFRESH_INTERVAL.addSeconds(130)); // the time at which the update should happen
		context.addAccountsToCache(accounts);
		Mockito.when(context.accountServices.getAccountMetaDataPairs(Mockito.any()))
				.thenReturn(Collections.singletonList(context.pair1));

		// Act:
		context.cache.updateCache().join();

		// Assert:
		// - only the first account was updated because the second account hasn't expired
		final List<SerializableAccountId> expectedRequests = Collections.singletonList(new SerializableAccountId(accounts.get(0).getAddress()));
		final ArgumentCaptor<? extends List<SerializableAccountId>> captor = createAccountIdListCaptor();
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountMetaDataPairs(captor.capture());
		Assert.assertThat(captor.getValue(), IsEquivalent.equivalentTo(expectedRequests));
	}

	@Test
	public void updateDoesNotDelegateToAccountServicesIfAllAccountsAreUnexpired() {
		// Arrange:
		// - add two accounts to the cache
		final TestContext context = new TestContext();
		final List<AccountInfo> accounts = Utils.generateRandomAccountInfos(2);
		context.addAccountsToCache(accounts);
		Mockito.when(context.timeProvider.getCurrentTime())
				.thenReturn(START_TIME_PLUS_REFRESH_INTERVAL.addSeconds(-1));

		// Act:
		context.cache.updateCache().join();

		// Assert:
		// - neither account was updated because neither was expired
		Mockito.verify(context.accountServices, Mockito.never()).getAccountMetaDataPairs(Mockito.any());
	}

	//endregion

	@SuppressWarnings("unchecked")
	private static ArgumentCaptor<? extends List<SerializableAccountId>> createAccountIdListCaptor() {
		return ArgumentCaptor.forClass((Class)List.class);
	}

	private static void assertAreEqual(final AccountInfo expectedInfo, final Account actualAccount) {
		// Assert:
		Assert.assertThat(actualAccount.getAddress(), IsEqual.equalTo(expectedInfo.getAddress()));
		Assert.assertThat(actualAccount.getAddress().getPublicKey(), IsEqual.equalTo(expectedInfo.getKeyPair().getPublicKey()));
	}

	private static class TestContext {
		private final Address address = Address.fromEncoded("sigma");
		private final AccountMetaDataPair pair1 = new AccountMetaDataPair(Utils.generateRandomAccountInfo(), null);
		private final AccountMetaDataPair pair2 = new AccountMetaDataPair(Utils.generateRandomAccountInfo(), null);

		private final AccountServices accountServices = Mockito.mock(AccountServices.class);
		private final TimeProvider timeProvider = Mockito.mock(TimeProvider.class);
		private final NccAccountCache cache = new NccAccountCache(this.accountServices, this.timeProvider, REFRESH_INTERVAL.getRawTime());

		public TestContext() {
			Mockito.when(this.timeProvider.getCurrentTime()).thenReturn(START_TIME);
			Mockito.when(this.accountServices.getAccountMetaDataPair(this.address))
					.thenReturn(this.pair1, this.pair2);
		}

		public void setAccountServicesFailure() {
			Mockito.when(this.accountServices.getAccountMetaDataPair(Mockito.any()))
					.thenThrow(new NisException(new ErrorResponse(TimeInstant.ZERO, "badness", 17)));
		}

		public void addAccountsToCache(final Collection<AccountInfo> accounts) {
			for (final AccountInfo account : accounts) {
				Mockito.when(this.accountServices.getAccountMetaDataPair(account.getAddress()))
						.thenReturn(new AccountMetaDataPair(account, null));
				this.cache.findPairByAddress(account.getAddress());
			}
		}
	}
}