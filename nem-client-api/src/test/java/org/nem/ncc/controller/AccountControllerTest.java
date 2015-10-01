package org.nem.ncc.controller;

import net.minidev.json.*;
import org.hamcrest.core.IsEqual;
import org.junit.*;
import org.mockito.*;
import org.nem.core.connect.HttpPostRequest;
import org.nem.core.connect.client.NisApiId;
import org.nem.core.crypto.*;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.node.NodeEndpoint;
import org.nem.core.serialization.*;
import org.nem.core.time.TimeInstant;
import org.nem.ncc.connector.PrimaryNisConnector;
import org.nem.ncc.controller.requests.*;
import org.nem.ncc.controller.viewmodels.*;
import org.nem.ncc.services.*;
import org.nem.ncc.test.*;
import org.nem.ncc.wallet.*;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collectors;

public class AccountControllerTest {
	private static final SecureRandom random = new SecureRandom();

	//region findAccount

	@Test
	public void getAccountInfoDelegatesToAccountMapper() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		final AccountViewModel originalAccountViewModel = createViewModel(account);
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(originalAccountViewModel);

		// Act:
		final AccountId request = new AccountId(account.getAddress());
		final AccountViewModel accountViewModel = context.controller.getAccountInfo(request);

		// Assert:
		Mockito.verify(context.accountMapper, Mockito.times(1)).toViewModel(account.getAddress());
		Assert.assertThat(accountViewModel, IsEqual.equalTo(originalAccountViewModel));
	}

	//endregion

	//region getAccountTransactionsAll

	@Test
	public void getAccountTransactionsAllDelegatesToAccountMapperForAccountInformation() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		final AccountViewModel originalAccountViewModel = createViewModel(account);
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(originalAccountViewModel);

		// Act:
		final AccountDatabaseIdRequest request = createIdRequest(account.getAddress());
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsAll(request);
		final AccountViewModel accountViewModel = pair.getAccount();

		// Assert:
		Mockito.verify(context.accountMapper, Mockito.times(1)).toViewModel(account.getAddress());
		Assert.assertThat(accountViewModel, IsEqual.equalTo(originalAccountViewModel));
	}

	@Test
	public void getAccountTransactionsAllDelegatesToAccountServicesForUnconfirmedTransaction() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(createViewModel(account));

		final List<Transaction> transactions = Arrays.asList(
				createTransfer(Utils.generateRandomAccount(), Amount.fromNem(124)),
				createTransfer(account, Amount.fromNem(572)),
				createTransfer(Utils.generateRandomAccount(), Amount.fromNem(323)));
		Mockito.when(context.accountServices.getUnconfirmedTransactions(account.getAddress()))
				.thenReturn(transactions);

		// Act:
		final AccountDatabaseIdRequest request = createIdRequest(account.getAddress());
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsAll(request);
		final Collection<TransactionViewModel> transactionViewModels = pair.getTransactions();

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(1)).getUnconfirmedTransactions(account.getAddress());
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getAmount()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getDirection()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0, 2, 0)));
		Assert.assertThat(
				transactionViewModels.stream().map(TransactionViewModel::getConfirmations).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0L, 0L, 0L)));
	}

	@Test
	public void getAccountTransactionsAllDelegatesToAccountServicesForConfirmedTransaction() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(createViewModel(account));
		context.setLastBlockHeight(27);

		final AccountDatabaseIdRequest request = createIdRequest(account.getAddress());
		final List<TransactionMetaDataPair> pairs = Arrays.asList(
				createTransferMetaDataPair(Utils.generateRandomAccount(), Amount.fromNem(124), 19, 12L),
				createTransferMetaDataPair(account, Amount.fromNem(572), 17, 23L),
				createTransferMetaDataPair(Utils.generateRandomAccount(), Amount.fromNem(323), 27, 34L));
		Mockito.when(context.accountServices.getTransactions(TransactionDirection.ALL, account.getAddress(), request.getDatabaseId()))
				.thenReturn(pairs);

		// Act:
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsAll(request);
		final Collection<TransactionViewModel> transactionViewModels = pair.getTransactions();

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(1))
				.getTransactions(TransactionDirection.ALL, account.getAddress(), request.getDatabaseId());
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getAmount()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getDirection()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0, 2, 0)));
		Assert.assertThat(
				transactionViewModels.stream().map(TransactionViewModel::getConfirmations).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(9L, 11L, 1L)));
	}

	@Test
	public void getAccountTransactionsAllMergesUnconfirmedAndConfirmedTransactions() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(createViewModel(account));
		context.setLastBlockHeight(27);

		final List<Transaction> transactions = Collections.singletonList(
				createTransfer(account, Amount.fromNem(124)));
		Mockito.when(context.accountServices.getUnconfirmedTransactions(account.getAddress()))
				.thenReturn(transactions);

		final AccountDatabaseIdRequest request = createIdRequest(account.getAddress());
		final List<TransactionMetaDataPair> pairs = Collections.singletonList(
				createTransferMetaDataPair(Utils.generateRandomAccount(), Amount.fromNem(323), 25, 34L));
		Mockito.when(context.accountServices.getTransactions(TransactionDirection.ALL, account.getAddress(), request.getDatabaseId()))
				.thenReturn(pairs);

		// Act:
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsAll(request);
		final Collection<TransactionViewModel> transactionViewModels = pair.getTransactions();

		// Assert:
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getAmount()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(323))));
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getDirection()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(2, 0)));
		Assert.assertThat(
				transactionViewModels.stream().map(TransactionViewModel::getConfirmations).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0L, 3L)));
	}

	private static TransactionMetaDataPair createTransferMetaDataPair(
			final Account sender,
			final Amount amount,
			final int blockHeight,
			final Long transactionId) {
		return new TransactionMetaDataPair(
				createTransfer(sender, amount),
				new TransactionMetaData(new BlockHeight(blockHeight), transactionId, Hash.ZERO));
	}

	private static Transaction createTransfer(final Account sender, final Amount amount) {
		return new TransferTransaction(
				new TimeInstant(125),
				sender,
				Utils.generateRandomAccount(),
				amount,
				null);
	}

	//endregion

	//region mosaics definitions
	@Test
	public void accountMosaicDefinitionsBatchDelegatesToConnector() {
		// Arrange:
		final TestContext context = new TestContext();
		final Account account1 = Utils.generateRandomAccount();
		final Account account2 = Utils.generateRandomAccount();

		final List<SerializableAccountId> accounts = Arrays.asList(
				new SerializableAccountId(account1.getAddress()),
				new SerializableAccountId(account2.getAddress()));
		final Deserializer deserializer = Utils.roundtripSerializableEntity(new SerializableList<>(accounts), null);

		Mockito.when(context.connector.post(Mockito.any(), Mockito.any())).thenReturn(Mockito.mock(Deserializer.class));

		// Act:
		// TODO 20150810 J-G: i guess result should also be validated
		context.controller.accountMosaicDefinitionsBatch(deserializer);

		// Assert:
		final ArgumentCaptor<HttpPostRequest> requestCaptor = ArgumentCaptor.forClass(HttpPostRequest.class);
		Mockito.verify(context.connector, Mockito.only()).post(
				Mockito.eq(NisApiId.NIS_REST_ACCOUNT_MOSAIC_DEFINITIONS_BATCH_LOOK_UP), requestCaptor.capture());
		final JSONObject jsonRequest = (JSONObject)JSONValue.parse(requestCaptor.getValue().getPayload());

		Assert.assertThat(jsonRequest.size(), IsEqual.equalTo(1));
		final JSONArray elements = (JSONArray)jsonRequest.get("data");
		Assert.assertThat(elements.size(), IsEqual.equalTo(2));
		Assert.assertThat(((JSONObject)elements.get(0)).get("account"), IsEqual.equalTo(account1.getAddress().getEncoded()));
		Assert.assertThat(((JSONObject)elements.get(1)).get("account"), IsEqual.equalTo(account2.getAddress().getEncoded()));
	}
	//endregion

	//region transactions/unconfirmed

	@Test
	public void getAccountTransactionsUnconfirmedDelegatesToAccountServicesForUnconfirmedTransaction() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(createViewModel(account));

		final AccountHashRequest request = createHashRequest(account.getAddress(), null);
		final List<Transaction> pairs = Arrays.asList(
				createTransfer(Utils.generateRandomAccount(), Amount.fromNem(124)),
				createTransfer(account, Amount.fromNem(572)),
				createTransfer(Utils.generateRandomAccount(), Amount.fromNem(323)));
		Mockito.when(context.accountServices.getUnconfirmedTransactions(account.getAddress()))
				.thenReturn(pairs);

		// Act:
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsUnconfirmed(request);
		final Collection<TransactionViewModel> transactionViewModels = pair.getTransactions();

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(1)).getUnconfirmedTransactions(account.getAddress());
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getAmount()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getDirection()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0, 2, 0)));
		Assert.assertThat(
				transactionViewModels.stream().map(TransactionViewModel::getConfirmations).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0L, 0L, 0L)));
	}

	@Test
	public void getAccountTransactionsUnconfirmedReturnsEmptyListWhenHashIsProvided() {
		// Arrange:
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();

		final AccountHashRequest request = createHashRequest(account.getAddress(), Utils.generateRandomHash());
		Mockito.when(context.accountServices.getUnconfirmedTransactions(account.getAddress()))
				.thenReturn(Collections.singletonList(createTransfer(account, Amount.fromNem(572))));

		// Act:
		final AccountTransactionsPair pair = context.controller.getAccountTransactionsUnconfirmed(request);

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(0)).getUnconfirmedTransactions(Mockito.any());
		Assert.assertThat(pair.getTransactions().size(), IsEqual.equalTo(0));
	}

	//endregion

	//region new transaction Handlers

	@Test
	public void getAccountTransactionsConfirmedDelegatesToAccountService() {
		this.assertGetTransactionsDelegateToAccountService(
				TransactionDirection.ALL,
				(TestContext ctx) -> ctx.controller::getAccountTransactionsConfirmed);
	}

	@Test
	public void getAccountTransactionsIncomingDelegatesToAccountService() {
		this.assertGetTransactionsDelegateToAccountService(
				TransactionDirection.INCOMING,
				(TestContext ctx) -> ctx.controller::getAccountTransactionsIncoming);
	}

	@Test
	public void getAccountTransactionsOutgoingDelegatesToAccountService() {
		this.assertGetTransactionsDelegateToAccountService(
				TransactionDirection.OUTGOING,
				(TestContext ctx) -> ctx.controller::getAccountTransactionsOutgoing);
	}

	private void assertGetTransactionsDelegateToAccountService(
			final TransactionDirection direction,
			final Function<TestContext, Function<AccountDatabaseIdRequest, AccountTransactionsPair>> handlerFactory) {
		final Account account = Utils.generateRandomAccount();
		final TestContext context = new TestContext();
		Mockito.when(context.accountMapper.toViewModel(account.getAddress()))
				.thenReturn(createViewModel(account));
		context.setLastBlockHeight(34);

		final AccountDatabaseIdRequest request = new AccountDatabaseIdRequest(account.getAddress(), null);
		final List<TransactionMetaDataPair> pairs = Arrays.asList(
				createTransferMetaDataPair(Utils.generateRandomAccount(), Amount.fromNem(124), 19, 12L),
				createTransferMetaDataPair(account, Amount.fromNem(572), 17, 23L),
				createTransferMetaDataPair(Utils.generateRandomAccount(), Amount.fromNem(323), 27, 34L));
		Mockito.when(context.accountServices.getTransactions(direction, account.getAddress(), request.getDatabaseId()))
				.thenReturn(pairs);

		// Act:
		final AccountTransactionsPair pair = handlerFactory.apply(context).apply(request);
		final Collection<TransactionViewModel> transactionViewModels = pair.getTransactions();

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(1))
				.getTransactions(direction, account.getAddress(), request.getDatabaseId());
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getAmount()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(Amount.fromNem(124), Amount.fromNem(572), Amount.fromNem(323))));
		Assert.assertThat(
				transactionViewModels.stream().map(t -> ((TransferTransactionViewModel)t).getDirection()).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(0, 2, 0)));
		Assert.assertThat(
				transactionViewModels.stream().map(TransactionViewModel::getConfirmations).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(16L, 18L, 8L)));
	}

	//endregion

	//region getAccountHarvests

	@Test
	public void getAccountHarvestsDelegatesToAccountServices() {
		// Arrange:
		final AccountDatabaseIdRequest request = createIdRequest(Utils.generateRandomAddress());
		final TestContext context = new TestContext();
		final List<HarvestInfo> originalHarvestInfos = Arrays.asList(
				new HarvestInfo(0L, new BlockHeight(7), TimeInstant.ZERO, Amount.ZERO, 0L),
				new HarvestInfo(0L, new BlockHeight(5), TimeInstant.ZERO, Amount.ZERO, 0L),
				new HarvestInfo(0L, new BlockHeight(9), TimeInstant.ZERO, Amount.ZERO, 0L));

		Mockito.when(context.accountServices.getAccountHarvests(request.getAddress(), request.getDatabaseId()))
				.thenReturn(originalHarvestInfos);

		// Act:
		final SerializableList<HarvestInfoViewModel> harvestInfos = context.controller.getAccountHarvests(request);

		// Assert:
		Mockito.verify(context.accountServices, Mockito.times(1)).getAccountHarvests(request.getAddress(), request.getDatabaseId());
		Assert.assertThat(
				harvestInfos.asCollection().stream().map(HarvestInfoViewModel::getBlockHeight).collect(Collectors.toList()),
				IsEqual.equalTo(Arrays.asList(new BlockHeight(7), new BlockHeight(5), new BlockHeight(9))));
	}

	//endregion

	//region unlock / lock

	@Test
	public void unlockDelegatesToNisConnector() {
		// Assert:
		assertPrivateKeyConnectorRequest(AccountController::unlock, NisApiId.NIS_REST_ACCOUNT_UNLOCK);
	}

	@Test
	public void lockDelegatesToNisConnector() {
		// Assert:
		assertPrivateKeyConnectorRequest(AccountController::lock, NisApiId.NIS_REST_ACCOUNT_LOCK);
	}

	private static void assertPrivateKeyConnectorRequest(
			final BiConsumer<AccountController, AccountWalletRequest> action,
			final NisApiId apiId) {
		// Arrange:
		final TestContext context = new TestContext();
		final Wallet wallet = Mockito.mock(Wallet.class);
		final KeyPair keyPair = new KeyPair();
		final Account account = new Account(new KeyPair(keyPair.getPublicKey()));
		Mockito.when(context.walletServices.get(new WalletName("wallet"))).thenReturn(wallet);
		Mockito.when(wallet.getAccountPrivateKey(account.getAddress())).thenReturn(keyPair.getPrivateKey());

		// Act:
		action.accept(context.controller, createWalletRequest(account.getAddress(), new WalletName("wallet")));

		final ArgumentCaptor<HttpPostRequest> requestCaptor = ArgumentCaptor.forClass(HttpPostRequest.class);
		Mockito.verify(context.connector, Mockito.only()).voidPost(Mockito.eq(apiId), requestCaptor.capture());
		final JSONObject jsonRequest = (JSONObject)JSONValue.parse(requestCaptor.getValue().getPayload());

		// Assert:
		Assert.assertThat(jsonRequest.size(), IsEqual.equalTo(1));
		Assert.assertThat(
				jsonRequest.get("value"),
				IsEqual.equalTo(keyPair.getPrivateKey().toString()));
	}

	@Test
	public void remoteUnlockDelegatesToNisConnector() {
		// Assert:
		assertRemotePrivateKeyConnectorRequest(AccountController::remoteUnlock, NisApiId.NIS_REST_ACCOUNT_UNLOCK);
	}

	@Test
	public void remoteLockDelegatesToNisConnector() {
		// Assert:
		assertRemotePrivateKeyConnectorRequest(AccountController::remoteLock, NisApiId.NIS_REST_ACCOUNT_LOCK);
	}

	private static void assertRemotePrivateKeyConnectorRequest(
			final BiConsumer<AccountController, AccountWalletPasswordRequest> action,
			final NisApiId apiId) {
		// Arrange:
		final TestContext context = new TestContext();
		final KeyPair keyPair = new KeyPair();
		final Account account = new Account(new KeyPair(keyPair.getPublicKey()));
		final WalletAccount walletAccount = new WalletAccount();
		Mockito.when(context.walletServices.tryFindOpenAccount(account.getAddress())).thenReturn(walletAccount);

		// Act:
		action.accept(context.controller, createWalletPasswordRequest(account.getAddress(), new WalletName("wallet"), new WalletPassword("pass")));

		final ArgumentCaptor<HttpPostRequest> requestCaptor = ArgumentCaptor.forClass(HttpPostRequest.class);
		Mockito.verify(context.connector, Mockito.only()).voidPost(Mockito.eq(apiId), requestCaptor.capture());
		final JSONObject jsonRequest = (JSONObject)JSONValue.parse(requestCaptor.getValue().getPayload());

		// Assert:
		Assert.assertThat(jsonRequest.size(), IsEqual.equalTo(1));
		Assert.assertThat(
				jsonRequest.get("value"),
				IsEqual.equalTo(walletAccount.getRemoteHarvestingPrivateKey().toString()));
	}

	//endregion

	//region remoteStatus

	@Test
	public void remoteStatusDelegatesToConnector() {
		// Arrange:
		final TestContext context = new TestContext();
		final KeyPair keyPair = new KeyPair();
		final Account account = new Account(new KeyPair(keyPair.getPublicKey()));
		final WalletAccount walletAccount = new WalletAccount();
		final Address remoteAddress = Address.fromPublicKey(new KeyPair(walletAccount.getRemoteHarvestingPrivateKey()).getPublicKey());
		Mockito.when(context.walletServices.tryFindOpenAccount(account.getAddress())).thenReturn(walletAccount);
		Mockito.when(context.connector.get(Mockito.eq(NisApiId.NIS_REST_ACCOUNT_STATUS), Mockito.any()))
				.thenReturn(Utils.roundtripSerializableEntity(new AccountStatusViewModel(AccountStatus.LOCKED), null));

		// Act:
		final AccountStatusViewModel viewModel = context.controller.remoteStatus(createWalletRequest(account.getAddress(), new WalletName("wallet")));

		// Assert:
		Assert.assertThat(viewModel.getStatus(), IsEqual.equalTo(AccountStatus.LOCKED));

		final String expectedQueryString = "address=" + remoteAddress.getEncoded();
		Mockito.verify(context.connector, Mockito.only()).get(NisApiId.NIS_REST_ACCOUNT_STATUS, expectedQueryString);
	}

	//endregion

	private static AccountViewModel createViewModel(final Account account) {
		return Utils.createAccountViewModelFromAddress(account.getAddress());
	}

	private static AccountDatabaseIdRequest createIdRequest(final Address address) {
		return new AccountDatabaseIdRequest(address, random.nextLong());
	}

	private static AccountHashRequest createHashRequest(final Address address, final Hash hash) {
		return new AccountHashRequest(address, hash);
	}

	private static AccountWalletRequest createWalletRequest(final Address address, final WalletName walletName) {
		return new AccountWalletRequest(address, walletName);
	}

	private static AccountWalletPasswordRequest createWalletPasswordRequest(final Address address, final WalletName walletName, final WalletPassword walletPassword) {
		return new AccountWalletPasswordRequest(address, walletName, walletPassword);
	}

	private static class TestContext {
		private final NodeEndpoint nisEndpoint = NodeEndpoint.fromHost("10.0.0.99");
		private final AccountServices accountServices = Mockito.mock(AccountServices.class);
		private final AccountMapper accountMapper = Mockito.mock(AccountMapper.class);
		private final WalletServices walletServices = Mockito.mock(WalletServices.class);
		private final ChainServices chainServices = Mockito.mock(ChainServices.class);
		private final PrimaryNisConnector connector = Mockito.mock(PrimaryNisConnector.class);

		private final AccountController controller = new AccountController(
				this.accountServices,
				this.accountMapper,
				this.walletServices,
				this.chainServices,
				this.connector);

		private TestContext() {
			this.setLastBlockHeight(1);
			ServicesUtils.setupForwarding(this.connector, this.nisEndpoint);

			// getAccountMetaDataPair should return a pair containing an account info matching the address parameter
			Mockito.when(this.accountServices.getAccountMetaDataPair(Mockito.any()))
					.thenAnswer(invocationOnMock -> {
						final Address address = (Address)invocationOnMock.getArguments()[0];
						return new AccountMetaDataPair(
								Utils.createAccountInfoFromAddress(address),
								Mockito.mock(AccountMetaData.class));
					});
		}

		private void setLastBlockHeight(final int height) {
			Mockito.when(this.chainServices.getChainHeightAsync(this.nisEndpoint))
					.thenReturn(CompletableFuture.completedFuture(new BlockHeight(height)));
		}
	}
}