package org.nem.ncc.services;

import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.*;
import org.nem.core.connect.client.NisApiId;
import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.*;
import org.nem.core.time.TimeInstant;
import org.nem.ncc.connector.PrimaryNisConnector;
import org.nem.ncc.test.*;

import java.util.*;
import java.util.stream.Collectors;

public class AccountServicesTest {

	@Test
	public void getAccountMetaDataPairDelegatesToConnector() {
		// Arrange:
		final TestContext context = new TestContext();
		final AccountMetaDataPair originalPair = new AccountMetaDataPair(
				Utils.createAccountInfoFromAddress(Address.fromEncoded("FOO")),
				new AccountMetaData(AccountStatus.UNLOCKED, AccountRemoteStatus.INACTIVE, Collections.emptyList(), Collections.emptyList()));

		Mockito.when(context.connector.get(NisApiId.NIS_REST_ACCOUNT_LOOK_UP, "address=FOO"))
				.thenReturn(serialize(originalPair));

		// Act:
		final AccountMetaDataPair pair = context.services.getAccountMetaDataPair(Address.fromEncoded("FOO"));

		// Assert:
		Mockito.verify(context.connector, Mockito.times(1)).get(NisApiId.NIS_REST_ACCOUNT_LOOK_UP, "address=FOO");
		Assert.assertThat(pair.getEntity().getAddress(), IsEqual.equalTo(Address.fromEncoded("FOO")));
		Assert.assertThat(pair.getMetaData().getStatus(), IsEqual.equalTo(AccountStatus.UNLOCKED));
	}

	@Test
	public void getAccountMetaDataPairsDelegatesToConnector() {
		// Arrange:
		final TestContext context = new TestContext();
		final Address address = Utils.generateRandomAddress();

		// Act:
		final List<AccountMetaDataPair> pairs = getAccountMetaDataPairs(context, address);
		// Assert:
		Mockito.verify(context.connector, Mockito.times(1)).post(Mockito.eq(NisApiId.NIS_REST_ACCOUNT_BATCH_LOOK_UP), Mockito.any());
		Assert.assertThat(pairs.get(0).getEntity().getAddress(), IsEqual.equalTo(address));
		Assert.assertThat(pairs.get(0).getMetaData().getStatus(), IsEqual.equalTo(AccountStatus.UNLOCKED));
	}

	@Test
	public void getAccountMetaDataPairsIsBypassedIfDisconnectedFromNis() {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.connector.isConnected()).thenReturn(false);
		final Address address = Utils.generateRandomAddress();

		// Act:
		final List<AccountMetaDataPair> pairs = getAccountMetaDataPairs(context, address);

		// Assert:
		Mockito.verify(context.connector, Mockito.never()).post(Mockito.eq(NisApiId.NIS_REST_ACCOUNT_BATCH_LOOK_UP), Mockito.any());
		Assert.assertThat(pairs.isEmpty(), IsEqual.equalTo(true));
	}

	private static List<AccountMetaDataPair> getAccountMetaDataPairs(final TestContext context, final Address requestAddress) {
		// Arrange:
		final AccountMetaDataPair originalPair = new AccountMetaDataPair(
				Utils.createAccountInfoFromAddress(requestAddress),
				new AccountMetaData(AccountStatus.UNLOCKED, AccountRemoteStatus.INACTIVE, Collections.emptyList(), Collections.emptyList()));
		final Collection<SerializableAccountId> requests = Collections.singletonList(new SerializableAccountId(requestAddress));

		Mockito.when(context.connector.post(Mockito.eq(NisApiId.NIS_REST_ACCOUNT_BATCH_LOOK_UP), Mockito.any()))
				.thenReturn(serialize(new SerializableList<>(Collections.singletonList(originalPair))));

		// Act:
		return context.services.getAccountMetaDataPairs(requests).stream().collect(Collectors.toList());
	}

	//region NEW account/transactions API

	private static void assertGetTransactionsWithIdDelegatesToConnector(
			final NisApiId nisApiId,
			final TransactionDirection direction,
			final Long id,
			final String expectedQueryString) {
		// Arrange:
		final TestContext context = new TestContext();
		final List<TransactionMetaDataPair> originalPairs = Arrays.asList(
				createTransferMetaDataPair(Amount.fromNem(124), 5, 12),
				createTransferMetaDataPair(Amount.fromNem(572), 9, 23),
				createTransferMetaDataPair(Amount.fromNem(323), 4, 34));

		Mockito.when(context.connector.get(Matchers.eq(nisApiId), Matchers.anyString()))
				.thenReturn(serialize(new SerializableList<>(originalPairs)));

		// Act:
		final List<TransactionMetaDataPair> pairs = context.services.getTransactions(direction, Address.fromEncoded("FOO"), id);

		// Assert:
		Mockito.verify(context.connector, Mockito.times(1)).get(nisApiId, expectedQueryString);
		Assert.assertThat(
				pairs.stream().map(p -> p.getEntity().getFee()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
		Assert.assertThat(
				pairs.stream().map(p -> p.getMetaData().getHeight()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(new BlockHeight(5), new BlockHeight(9), new BlockHeight(4))));
		Assert.assertThat(
				pairs.stream().map(p -> p.getMetaData().getId()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(12L, 23L, 34L)));
	}

	@Test
	public void transactionsAllDelegateToConnectorWithoutId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_ALL,
				TransactionDirection.ALL,
				null,
				"address=FOO"
		);
	}

	@Test
	public void transactionsAllDelegateToConnectorWithId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_ALL,
				TransactionDirection.ALL,
				123L,
				"address=FOO&id=123"
		);
	}

	@Test
	public void transactionsIncomingDelegateToConnectorWithoutId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_INCOMING,
				TransactionDirection.INCOMING,
				null,
				"address=FOO"
		);
	}

	@Test
	public void transactionsIncomingDelegateToConnectorWithId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_INCOMING,
				TransactionDirection.INCOMING,
				123L,
				"address=FOO&id=123"
		);
	}

	@Test
	public void transactionsOutgoingDelegateToConnectorWithoutId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_OUTGOING,
				TransactionDirection.OUTGOING,
				null,
				"address=FOO"
		);
	}

	@Test
	public void transactionsOutgoingDelegateToConnectorWithId() {
		assertGetTransactionsWithIdDelegatesToConnector(
				NisApiId.NIS_REST_ACCOUNT_TRANSFERS_OUTGOING,
				TransactionDirection.OUTGOING,
				123L,
				"address=FOO&id=123"
		);
	}

	//endregion

	//region getUnconfirmedTransactions

	@Test
	public void getUnconfirmedTransactionsDelegatesToConnector() {
		// Arrange:
		final List<UnconfirmedTransactionMetaDataPair> originalTransactions = Arrays.asList(
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(124)),
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(572)),
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(323)));

		// Assert:
		final List<Amount> amounts = assertGetUnconfirmedTransactionsDelegation(originalTransactions);
		Assert.assertThat(
				amounts,
				IsEquivalent.equivalentTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
	}

	@Test
	public void getUnconfirmedTransactionsReturnsTransactionsSortedNewestFirst() {
		// Arrange:
		final List<UnconfirmedTransactionMetaDataPair> originalTransactions = Arrays.asList(
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(124), new TimeInstant(200)),
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(572), new TimeInstant(100)),
				createUnconfirmedTransactionMetaDataPair(Amount.fromNem(323), new TimeInstant(300)));

		// Assert:
		final List<Amount> amounts = assertGetUnconfirmedTransactionsDelegation(originalTransactions);
		Assert.assertThat(
				amounts,
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(323), Amount.fromNem(124), Amount.fromNem(572))));
	}

	private static List<Amount> assertGetUnconfirmedTransactionsDelegation(final List<UnconfirmedTransactionMetaDataPair> originalPairs) {
		// Arrange:
		final TestContext context = new TestContext();
		Mockito.when(context.connector.get(NisApiId.NIS_REST_ACCOUNT_UNCONFIRMED, "address=FOO"))
				.thenReturn(serialize(new SerializableList<>(originalPairs)));

		// Act:
		final List<Transaction> transactions = context.services.getUnconfirmedTransactions(Address.fromEncoded("FOO"));

		// Assert:
		Mockito.verify(context.connector, Mockito.times(1)).get(NisApiId.NIS_REST_ACCOUNT_UNCONFIRMED, "address=FOO");
		return transactions.stream().map(Transaction::getFee).collect(Collectors.toList());
	}

	//endregion

	//region getAccountHarvests

	@Test
	public void getAccountHarvestsWithoutIdDelegatesToNisConnector() {
		// Assert:
		final Address address = Address.fromEncoded("TB2IF4HDMCIMVCT6WYUDXONSUCVMUL4AM373VPR5");
		assertHarvestConnectorRequest(
				address,
				null,
				"address=TB2IF4HDMCIMVCT6WYUDXONSUCVMUL4AM373VPR5");
	}

	@Test
	public void getAccountHarvestsWithIdDelegatesToNisConnector() {
		// Assert:
		final Address address = Address.fromEncoded("TB2IF4HDMCIMVCT6WYUDXONSUCVMUL4AM373VPR5");
		final Long id = 123L;
		assertHarvestConnectorRequest(
				address,
				id,
				"address=TB2IF4HDMCIMVCT6WYUDXONSUCVMUL4AM373VPR5&id=123");
	}

	private static void assertHarvestConnectorRequest(
			final Address address,
			final Long id,
			final String queryString) {
		// Arrange:
		final TestContext context = new TestContext();
		final SerializableList<HarvestInfo> originalHarvestInfos = new SerializableList<>(Arrays.asList(
				new HarvestInfo(0L, new BlockHeight(7), TimeInstant.ZERO, Amount.ZERO, 0L),
				new HarvestInfo(0L, new BlockHeight(5), TimeInstant.ZERO, Amount.ZERO, 0L),
				new HarvestInfo(0L, new BlockHeight(9), TimeInstant.ZERO, Amount.ZERO, 0L)
		));

		Mockito.when(context.connector.get(NisApiId.NIS_REST_ACCOUNT_HARVESTS, queryString))
				.thenReturn(new JsonDeserializer(JsonSerializer.serializeToJson(originalHarvestInfos), null));

		// Act:
		final List<HarvestInfo> harvestInfos = context.services.getAccountHarvests(address, id);

		// Assert:
		Mockito.verify(context.connector, Mockito.times(1)).get(NisApiId.NIS_REST_ACCOUNT_HARVESTS, queryString);
		Assert.assertThat(
				harvestInfos.stream().map(HarvestInfo::getBlockHeight).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(new BlockHeight(7), new BlockHeight(5), new BlockHeight(9))));
	}

	private static Deserializer serialize(final SerializableEntity entity) {
		return new JsonDeserializer(
				JsonSerializer.serializeToJson(entity),
				new DeserializationContext(new MockAccountLookup()));
	}

	private static TransactionMetaDataPair createTransferMetaDataPair(final Amount fee, final int blockHeight, final long transactionId) {
		return new TransactionMetaDataPair(
				createTransfer(fee),
				new TransactionMetaData(new BlockHeight(blockHeight), transactionId, Hash.ZERO));
	}

	private static Transaction createTransfer(final Amount fee) {
		return createTransfer(fee, new TimeInstant(125));
	}

	private static Transaction createTransfer(final Amount fee, final TimeInstant timeInstant) {
		final Transaction transaction = new TransferTransaction(
				timeInstant,
				Utils.generateRandomAccount(),
				Utils.generateRandomAccount(),
				Amount.fromNem(75),
				null);
		transaction.setFee(fee);
		transaction.sign();
		return transaction;
	}

	private static UnconfirmedTransactionMetaDataPair createUnconfirmedTransactionMetaDataPair(final Amount fee, final TimeInstant timeInstant) {
		return new UnconfirmedTransactionMetaDataPair(createTransfer(fee, timeInstant), new UnconfirmedTransactionMetaData((Hash)null));
	}

	private static UnconfirmedTransactionMetaDataPair createUnconfirmedTransactionMetaDataPair(final Amount fee) {
		return new UnconfirmedTransactionMetaDataPair(createTransfer(fee), new UnconfirmedTransactionMetaData((Hash)null));
	}

	private static class TestContext {
		private final PrimaryNisConnector connector = Mockito.mock(PrimaryNisConnector.class);
		private final AccountServices services = new AccountServices(this.connector);

		public TestContext() {
			Mockito.when(this.connector.isConnected()).thenReturn(true);
		}
	}
}