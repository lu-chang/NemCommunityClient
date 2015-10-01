package org.nem.ncc.controller;

import org.nem.core.connect.HttpJsonPostRequest;
import org.nem.core.connect.client.NisApiId;
import org.nem.core.crypto.*;
import org.nem.core.model.Address;
import org.nem.core.model.mosaic.MosaicDefinition;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.*;
import org.nem.ncc.connector.PrimaryNisConnector;
import org.nem.ncc.controller.annotations.RequiresTrustedNis;
import org.nem.ncc.controller.requests.*;
import org.nem.ncc.controller.viewmodels.*;
import org.nem.ncc.services.*;
import org.nem.ncc.wallet.WalletAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles requests related to the REST resource "account".
 */
@RestController
public class AccountController {
	private final AccountServices accountServices;
	private final AccountMapper accountMapper;
	private final WalletServices walletServices;
	private final ChainServices chainServices;
	private final PrimaryNisConnector nisConnector;

	/**
	 * Creates a new account controller.
	 *
	 * @param accountServices The account services.
	 * @param accountMapper The account mapper.
	 * @param walletServices The wallet services.
	 * @param chainServices The chain services.
	 * @param nisConnector The NIS connector.
	 */
	@Autowired(required = true)
	public AccountController(
			final AccountServices accountServices,
			final AccountMapper accountMapper,
			final WalletServices walletServices,
			final ChainServices chainServices,
			final PrimaryNisConnector nisConnector) {
		this.accountServices = accountServices;
		this.accountMapper = accountMapper;
		this.walletServices = walletServices;
		this.chainServices = chainServices;
		this.nisConnector = nisConnector;
	}

	//region find

	/**
	 * Gets information about the specified account.
	 * Looks-up an account which is known by its address id
	 *
	 * @param aidRequest The account identifier.
	 * @return The account information.
	 */
	@RequestMapping(value = "/account/find", method = RequestMethod.POST)
	public AccountViewModel getAccountInfo(@RequestBody final AccountId aidRequest) {
		final Address address = aidRequest.getAddress();
		return this.accountMapper.toViewModel(address);
	}

	//endregion

	//region additional data

	@RequestMapping(value = "/account/mosaic/owned/definition/batch", method = RequestMethod.POST)
	public SerializableList<MosaicDefinition> accountMosaicDefinitionsBatch(@RequestBody final Deserializer deserializerIn) {
		final SerializableList<SerializableAccountId> accountIds = new SerializableList<>(deserializerIn, SerializableAccountId::new);
		final Deserializer deserializerOut = this.nisConnector.post(
				NisApiId.NIS_REST_ACCOUNT_MOSAIC_DEFINITIONS_BATCH_LOOK_UP,
				new HttpJsonPostRequest(accountIds));
		return new SerializableList<>(deserializerOut, MosaicDefinition::new);
	}
	//endregion

	//region transactions/unconfirmed

	/**
	 * Gets information about the specified account and unconfirmed transactions.
	 *
	 * @param ahRequest The account identifier.
	 * @return The account and transactions information.
	 */
	@RequestMapping(value = "/account/transactions/unconfirmed", method = RequestMethod.POST)
	public AccountTransactionsPair getAccountTransactionsUnconfirmed(@RequestBody final AccountHashRequest ahRequest) {
		final AccountViewModel account = this.getAccountInfo(ahRequest);
		return new AccountTransactionsPair(account, this.getUnconfirmedTransactions(ahRequest));
	}

	private Collection<TransactionViewModel> getUnconfirmedTransactions(final AccountHashRequest ahRequest) {
		// the unconfirmed transactions api does not support paging
		// in order to simplify the UX code, pretend all subsequent pages are empty
		if (null != ahRequest.getHash()) {
			return new ArrayList<>();
		}

		final Address address = ahRequest.getAddress();
		final AccountMetaDataPair accountData = this.accountServices.getAccountMetaDataPair(address);
		return this.accountServices.getUnconfirmedTransactions(address).stream()
				.map(t -> TransactionToViewModelMapper.map(t, accountData))
				.collect(Collectors.toList());
	}

	//endregion

	//region transactions/(all|confirmed|incoming|outgoing)

	/**
	 * Gets information about the specified account and incoming and outgoing confirmed and unconfirmed transactions.
	 *
	 * @param request The account (and optional) transaction identifier.
	 * @return The account and transactions information.
	 */
	@RequestMapping(value = "/account/transactions/all", method = RequestMethod.POST)
	public AccountTransactionsPair getAccountTransactionsAll(@RequestBody final AccountDatabaseIdRequest request) {
		final AccountViewModel account = this.getAccountInfo(request);
		final List<TransactionViewModel> allTransfers = new ArrayList<>();
		allTransfers.addAll(this.getUnconfirmedTransactions(new AccountHashRequest(request.getAddress(), null)));
		allTransfers.addAll(this.getConfirmedTransactions(TransactionDirection.ALL, request));
		return new AccountTransactionsPair(account, allTransfers);
	}

	/**
	 * Gets information about the specified account and incoming and outgoing confirmed transactions.
	 *
	 * @param request The account (and optional) transaction identifier.
	 * @return The account and transactions information.
	 */
	@RequestMapping(value = "/account/transactions/confirmed", method = RequestMethod.POST)
	public AccountTransactionsPair getAccountTransactionsConfirmed(@RequestBody final AccountDatabaseIdRequest request) {
		return this.getAccountTransactions(TransactionDirection.ALL, request);
	}

	/**
	 * Gets information about the specified account and incoming confirmed transactions.
	 *
	 * @param request The account (and optional) transaction identifier.
	 * @return The account and transactions information.
	 */
	@RequestMapping(value = "/account/transactions/incoming", method = RequestMethod.POST)
	public AccountTransactionsPair getAccountTransactionsIncoming(@RequestBody final AccountDatabaseIdRequest request) {
		return this.getAccountTransactions(TransactionDirection.INCOMING, request);
	}

	/**
	 * Gets information about the specified account and outgoing confirmed transactions.
	 *
	 * @param request The account (and optional) transaction identifier.
	 * @return The account and transactions information.
	 */
	@RequestMapping(value = "/account/transactions/outgoing", method = RequestMethod.POST)
	public AccountTransactionsPair getAccountTransactionsOutgoing(@RequestBody final AccountDatabaseIdRequest request) {
		return this.getAccountTransactions(TransactionDirection.OUTGOING, request);
	}

	private AccountTransactionsPair getAccountTransactions(final TransactionDirection direction, final AccountDatabaseIdRequest request) {
		final AccountViewModel account = this.getAccountInfo(request);
		return new AccountTransactionsPair(account, this.getConfirmedTransactions(direction, request));
	}

	private Collection<TransactionViewModel> getConfirmedTransactions(final TransactionDirection direction, final AccountDatabaseIdRequest ahRequest) {
		final Address address = ahRequest.getAddress();
		final BlockHeight lastBlockHeight = this.nisConnector.forward(this.chainServices::getChainHeightAsync);
		return this.accountServices.getTransactions(direction, address, ahRequest.getDatabaseId()).stream()
				.map(p -> TransactionToViewModelMapper.map(p, address, lastBlockHeight))
				.collect(Collectors.toList());
	}

	//endregion

	//region harvests

	/**
	 * Retrieves a list of infos on harvested blocks for an account.
	 *
	 * @param request The account identifier.
	 * @return The list of harvest infos.
	 */
	@RequestMapping(value = "/account/harvests", method = RequestMethod.POST)
	public SerializableList<HarvestInfoViewModel> getAccountHarvests(@RequestBody final AccountDatabaseIdRequest request) {
		final List<HarvestInfo> harvestInfos = this.accountServices.getAccountHarvests(request.getAddress(), request.getDatabaseId());
		return new SerializableList<>(harvestInfos.stream().map(HarvestInfoViewModel::new).collect(Collectors.toList()));
	}

	//endregion

	//region unlock|lock

	/**
	 * Unlock the account on the connected NIS server (start foraging).
	 *
	 * @param awRequest The account / wallet view model.
	 */
	@RequestMapping(value = "/wallet/account/unlock", method = RequestMethod.POST)
	@RequiresTrustedNis
	public void unlock(@RequestBody final AccountWalletRequest awRequest) {
		this.lockUnlock(NisApiId.NIS_REST_ACCOUNT_UNLOCK, awRequest);
	}

	/**
	 * Lock the account on the connected NIS server (stop foraging).
	 *
	 * @param awRequest The account / wallet view model.
	 */
	@RequestMapping(value = "/wallet/account/lock", method = RequestMethod.POST)
	@RequiresTrustedNis
	public void lock(@RequestBody final AccountWalletRequest awRequest) {
		this.lockUnlock(NisApiId.NIS_REST_ACCOUNT_LOCK, awRequest);
	}

	private void lockUnlock(final NisApiId apiId, final AccountWalletRequest awRequest) {
		final PrivateKey privateKey = this.walletServices.get(awRequest.getWalletName()).getAccountPrivateKey(awRequest.getAddress());
		this.nisConnector.voidPost(apiId, new HttpJsonPostRequest(privateKey));
		// TODO 20150131 J-G: did you add refreshAccount just for unlock/lock?
		this.accountMapper.refreshAccount(awRequest.getAddress());
	}

	// TODO 20150321 J-G: for the remote/ functions, you're not actually using the wallet parameter (only the address)

	/**
	 * Unlock the account on a remote NIS server (start foraging).
	 * Remote address being used has to be announced previously.
	 *
	 * @param awpRequest The remote harvester view model.
	 */
	@RequestMapping(value = "/wallet/account/remote/unlock", method = RequestMethod.POST)
	public void remoteUnlock(@RequestBody final AccountWalletPasswordRequest awpRequest) {
		this.remoteLockUnlock(NisApiId.NIS_REST_ACCOUNT_UNLOCK, awpRequest);
	}

	/**
	 * Lock the account on a remote NIS server (stop foraging).
	 *
	 * @param awpRequest The remote harvester view model.
	 */
	@RequestMapping(value = "/wallet/account/remote/lock", method = RequestMethod.POST)
	public void remoteLock(@RequestBody final AccountWalletPasswordRequest awpRequest) {
		this.remoteLockUnlock(NisApiId.NIS_REST_ACCOUNT_LOCK, awpRequest);
	}

	private void remoteLockUnlock(final NisApiId apiId, final AccountWalletPasswordRequest awpRequest) {
		final PrivateKey privateKey = this.getRemoteHarvestingPrivateKey(awpRequest.getAddress());
		this.nisConnector.voidPost(apiId, new HttpJsonPostRequest(privateKey));
	}

	//endregion

	//region remoteStatus

	@RequestMapping(value = "/wallet/account/remote/status", method = RequestMethod.POST)
	public AccountStatusViewModel remoteStatus(@RequestBody final AccountWalletRequest awRequest) {
		final PrivateKey privateKey = this.getRemoteHarvestingPrivateKey(awRequest.getAddress());
		final Address remoteAddress = Address.fromPublicKey(new KeyPair(privateKey).getPublicKey());
		final Deserializer deserializer = this.nisConnector.get(NisApiId.NIS_REST_ACCOUNT_STATUS, "address=" + remoteAddress.getEncoded());
		return new AccountStatusViewModel(deserializer);
	}

	//endregion

	private PrivateKey getRemoteHarvestingPrivateKey(final Address address) {
		final WalletAccount account = this.walletServices.tryFindOpenAccount(address);
		return account.getRemoteHarvestingPrivateKey();
	}
}
