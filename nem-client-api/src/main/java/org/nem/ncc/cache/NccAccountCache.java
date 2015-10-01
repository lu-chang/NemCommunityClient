package org.nem.ncc.cache;

import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.time.*;
import org.nem.ncc.exceptions.NccException;
import org.nem.ncc.services.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * An account cache used by NCC.
 */
public class NccAccountCache implements AccountMetaDataPairLookup {
	private final AccountServices accountServices;
	private final TimeProvider timeProvider;
	private final int refreshInSeconds;
	private final ConcurrentMap<Address, FreshnessPair> cache;

	/**
	 * Creates a new account cache.
	 *
	 * @param accountServices The account services.
	 * @param timeProvider The time provider.
	 * @param refreshInSeconds The number of seconds that a cached entry is valid.
	 */
	public NccAccountCache(
			final AccountServices accountServices,
			final TimeProvider timeProvider,
			final int refreshInSeconds) {
		this.accountServices = accountServices;
		this.timeProvider = timeProvider;
		this.refreshInSeconds = refreshInSeconds;
		this.cache = new ConcurrentHashMap<>();
	}

	/**
	 * Gets all accounts in this cache.
	 *
	 * @return All accounts in this cache.
	 */
	public List<AccountInfo> getAccounts() {
		return this.cache.entrySet().stream()
				.map(e -> e.getValue().accountMetaDataPair.getEntity())
				.collect(Collectors.toList());
	}

	/**
	 * Seeds this cache with the specified accounts.
	 *
	 * @param seedAccounts The initial seed accounts.
	 */
	public void seedAccounts(final Collection<AccountInfo> seedAccounts) {
		seedAccounts.forEach(info -> this.cache.put(info.getAddress(), this.createFreshnessPairFromSeedAccount(info)));
	}

	private FreshnessPair createFreshnessPairFromSeedAccount(final AccountInfo info) {
		// accounts are polled on a regular interval, so it should be ok to use current time stamp

		// TODO 20150204: G-Br: so list of cosignatories is not saved so it won't be avail until first refresh... :/
		// > not sure how to fix that
		// TODO 20150207 BR -> G: updating now every 3 seconds and all seeded accounts are marked for update.
		return new FreshnessPair(
				new AccountMetaDataPair(
						info,
						new AccountMetaData(AccountStatus.UNKNOWN, AccountRemoteStatus.INACTIVE, Collections.emptyList(), Collections.emptyList())),
				this.timeProvider.getCurrentTime().addSeconds(-this.refreshInSeconds));
	}

	@Override
	public Account findByAddress(final Address id) {
		return this.findFreshnessPairByAddress(id).account;
	}

	@Override
	public AccountMetaDataPair findPairByAddress(final Address id) {
		return this.findFreshnessPairByAddress(id).accountMetaDataPair;
	}

	// TODO 20150131 J-G: should add a test for refreshAccount
	@Override
	public void refreshAccount(final Address address) {
		if (this.isKnownAddress(address)) {
			this.cache.remove(address);
			this.findFreshnessPairByAddress(address);
		}
	}

	/**
	 * Checks if an account is known.
	 *
	 * @param id The account id.
	 * @return true if the account is known, false if unknown.
	 */
	public boolean isKnownAddress(final Address id) {
		return this.cache.containsKey(id);
	}

	private FreshnessPair findFreshnessPairByAddress(final Address id) {
		FreshnessPair freshnessPair = this.cache.getOrDefault(id, null);
		if (null == freshnessPair) {
			final TimeInstant currentTime = this.timeProvider.getCurrentTime();
			freshnessPair = new FreshnessPair(this.update(id, null), currentTime);
			this.cache.put(id, freshnessPair);
		}

		return freshnessPair;
	}

	private AccountMetaDataPair update(
			final Address id,
			final AccountMetaDataPair originalPair) {
		try {
			return this.accountServices.getAccountMetaDataPair(id);
		} catch (final NccException ex) {
			if (null == originalPair) {
				throw ex;
			}

			return originalPair;
		}
	}

	/**
	 * Updates all expired accounts. This method is called by a timer.
	 */
	public java.util.concurrent.CompletableFuture<java.lang.Void> updateCache() {
		return CompletableFuture.runAsync(() -> {
			final TimeInstant currentTime = this.timeProvider.getCurrentTime();
			final List<SerializableAccountId> requests = this.cache.values().stream()
					.filter(f -> this.shouldUpdate(f, currentTime))
					.map(f -> new SerializableAccountId(f.account.getAddress()))
					.collect(Collectors.toList());
			if (requests.isEmpty()) {
				return;
			}

			final Collection<AccountMetaDataPair> pairs = this.accountServices.getAccountMetaDataPairs(requests);
			pairs.stream().forEach(p -> this.cache.put(p.getEntity().getAddress(), new FreshnessPair(p, currentTime)));
		});
	}

	private boolean shouldUpdate(final FreshnessPair freshnessPair, final TimeInstant currentTime) {
		return null == freshnessPair || currentTime.subtract(freshnessPair.refreshTime) > this.refreshInSeconds;
	}

	private static class FreshnessPair {
		public final AccountMetaDataPair accountMetaDataPair;
		public final Account account;
		public final TimeInstant refreshTime;

		public FreshnessPair(
				final AccountMetaDataPair accountMetaDataPair,
				final TimeInstant refreshTime) {
			this.accountMetaDataPair = accountMetaDataPair;
			this.account = new Account(this.accountMetaDataPair.getEntity().getAddress());
			this.refreshTime = refreshTime;
		}
	}
}
