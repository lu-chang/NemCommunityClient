package org.nem.ncc.services;

import org.nem.core.crypto.*;
import org.nem.core.messages.*;
import org.nem.core.model.*;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.SimpleAccountLookup;
import org.nem.core.time.*;
import org.nem.core.utils.*;
import org.nem.ncc.controller.requests.*;
import org.nem.ncc.controller.viewmodels.*;
import org.nem.ncc.exceptions.NccException;
import org.nem.ncc.wallet.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Helper class that is able to map a TransactionViewModel to a Transaction.
 * TODO 20150131 J-G: i guess we need some new tests for this class?
 */
public class TransactionMapper {
	private final WalletServices walletServices;
	private final SimpleAccountLookup accountLookup;
	private final TimeProvider timeProvider;

	/**
	 * Creates a new transaction mapper.
	 *
	 * @param walletServices The wallet services.
	 * @param accountLookup The account lookup.
	 * @param timeProvider The time provider.
	 */
	public TransactionMapper(
			final WalletServices walletServices,
			final SimpleAccountLookup accountLookup,
			final TimeProvider timeProvider) {
		this.walletServices = walletServices;
		this.accountLookup = accountLookup;
		this.timeProvider = timeProvider;
	}

	/**
	 * Converts the specified request to a model.
	 *
	 * @param request The request.
	 * @return The model.
	 */
	public Transaction toModel(final TransferSendRequest request) {
		return this.toModel(request, request.getPassword());
	}

	/**
	 * Converts the specified request to a model.
	 *
	 * @param request The request.
	 * @return The model.
	 */
	public Transaction toModel(final MultisigSignatureRequest request) {
		return this.toModel(request, request.getPassword());
	}

	/**
	 * Converts the specified request to a model.
	 *
	 * @param request The request.
	 * @return The model.
	 */
	public Transaction toModel(final MultisigModificationRequest request) {
		return this.toModel(request, request.getPassword());
	}

	/**
	 * Converts the specified request to a view model.
	 * TODO 20141025 J-J: not sure if this makes sense in this class
	 *
	 * @param request The request.
	 * @return The view model.
	 */
	public PartialTransferInformationViewModel toViewModel(final PartialTransferInformationRequest request) {
		// use a fake accounts so that encryption and signing are always possible
		final Account dummyAccount = new Account(new KeyPair());
		final Message message = this.createMessage(request.getMessage(), request.isHexMessage(), request.shouldEncrypt(), dummyAccount, dummyAccount);

		// if the amount isn't provided, assume a zero amount
		final Amount amount = null == request.getAmount() ? Amount.ZERO : request.getAmount();
		final TransferTransaction transaction = new TransferTransaction(
				TimeInstant.ZERO,
				dummyAccount,
				dummyAccount,
				amount,
				new TransferTransactionAttachment(message));
		final MultisigTransaction multisigTransaction = new MultisigTransaction(
				TimeInstant.ZERO,
				dummyAccount,
				transaction);

		final boolean isEncryptionSupported =
				null != request.getRecipientAddress() && this.accountLookup.findByAddress(request.getRecipientAddress()).hasPublicKey();
		return new PartialTransferInformationViewModel(
				transaction.getFee(),
				multisigTransaction.getFee(),
				isEncryptionSupported);
	}

	// currently empty used for Importance Transfer, as it's not providing any data...
	// TODO 20150601 J-G: i guess you need to finish this?
	public PartialTransferInformationViewModel toViewModel() {
		final Account dummyAccount = new Account(new KeyPair());

		final ImportanceTransferTransaction transaction = new ImportanceTransferTransaction(
				TimeInstant.ZERO,
				dummyAccount,
				ImportanceTransferMode.Activate,
				dummyAccount);

		final MultisigTransaction multisigTransaction = new MultisigTransaction(
				TimeInstant.ZERO,
				dummyAccount,
				transaction);

		return new PartialTransferInformationViewModel(
				transaction.getFee(),
				multisigTransaction.getFee(),
				false);
	}

	public PartialFeeInformationViewModel toViewModel(final PartialSignatureInformationRequest request) {
		final Account cosignatory = this.accountLookup.findByAddress(request.getCosignatoryAddress());
		final MultisigSignatureTransaction transaction = new MultisigSignatureTransaction(
				TimeInstant.ZERO,
				cosignatory,
				cosignatory, // DUMMY
				HashUtils.nextHash(Hash.ZERO, (new KeyPair()).getPublicKey()));
		return new PartialFeeInformationViewModel(
				transaction.getFee()
		);
	}

	public PartialTransferInformationViewModel toViewModel(final PartialModificationInformationRequest request) {
		// use a fake accounts so that encryption and signing are always possible
		final Account dummyAccount = new Account(new KeyPair());
		final Account multisig = this.accountLookup.findByAddress(request.getMultisigAddress());

		final List<MultisigCosignatoryModification> modifications = request.getCosignatoriesAddresses().stream()
				.map(this.accountLookup::findByAddress)
				.map(o -> new MultisigCosignatoryModification(MultisigModificationType.AddCosignatory, o))
				.collect(Collectors.toList());

		// TODO 20150201 J-G: quick and bit dirty hack for UI...
		if (modifications.isEmpty()) {
			return new PartialTransferInformationViewModel(
					Amount.ZERO,
					Amount.ZERO,
					false);
		}

		final MultisigAggregateModificationTransaction transaction = new MultisigAggregateModificationTransaction(
				TimeInstant.ZERO,
				multisig,
				modifications,
				request.getMinCosignatoriesModification());

		final MultisigTransaction multisigTransaction = new MultisigTransaction(
				TimeInstant.ZERO,
				dummyAccount,
				transaction);

		return new PartialTransferInformationViewModel(
				transaction.getFee(),
				multisigTransaction.getFee(),
				false);
	}

	/**
	 * Converts the specified request to a model.
	 *
	 * @param request The request.
	 * @return The model.
	 */
	public Transaction toModel(final TransferImportanceRequest request, final ImportanceTransferMode mode) {
		final boolean isMultisig = request.getType() == TransactionViewModel.Type.Multisig_Importance_Transfer.getValue();

		final Account sender = this.getSenderAccount(request.getWalletName(), request.getAddress(), request.getPassword());
		final Account remoteAccount = null == request.getPublicKey()
				? this.getRemoteAccount(request.getWalletName(), request.getAddress(), request.getPassword())
				: new Account(new KeyPair(request.getPublicKey()));

		final TimeInstant timeStamp = this.timeProvider.getCurrentTime();

		if (isMultisig) {
			final Account multisig = this.accountLookup.findByAddress(request.getMultisigAddress());
			final ImportanceTransferTransaction transaction = new ImportanceTransferTransaction(
					timeStamp,
					multisig,
					mode,
					remoteAccount);
			transaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			transaction.setFee(request.getFee());

			final MultisigTransaction multisigTransaction = new MultisigTransaction(timeStamp,
					sender,
					transaction);
			multisigTransaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			multisigTransaction.setFee(request.getMultisigFee());
			return multisigTransaction;
		} else {
			final ImportanceTransferTransaction transaction = new ImportanceTransferTransaction(
					timeStamp,
					sender,
					mode,
					remoteAccount);
			transaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			transaction.setFee(request.getFee());
			return transaction;
		}
	}

	private Transaction toModel(final TransferSendRequest request, final WalletPassword password) {
		final boolean isMultisig = request.getType() == TransactionViewModel.Type.Multisig_Transfer.getValue();
		final Account signer = this.getSenderAccount(request.getWalletName(), request.getSenderAddress(), password);
		final Account recipient = this.accountLookup.findByAddress(request.getRecipientAddress());
		final Message message = this.createMessage(request.getMessage(), request.isHexMessage(), request.shouldEncrypt(), signer, recipient);

		final TimeInstant timeStamp = this.timeProvider.getCurrentTime();

		if (isMultisig) {
			final Account multisig = this.accountLookup.findByAddress(request.getMultisigAddress());
			final TransferTransaction transaction = new TransferTransaction(
					request.getVersion(),
					timeStamp,
					multisig,
					recipient,
					request.getAmount(),
					new TransferTransactionAttachment(message));
			transaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			transaction.setFee(request.getFee());

			final MultisigTransaction multisigTransaction = new MultisigTransaction(timeStamp,
					signer,
					transaction);
			multisigTransaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			multisigTransaction.setFee(request.getMultisigFee());
			return multisigTransaction;
		} else {
			final TransferTransaction transaction = new TransferTransaction(
					request.getVersion(),
					timeStamp,
					signer,
					recipient,
					request.getAmount(),
					new TransferTransactionAttachment(message));
			transaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
			transaction.setFee(request.getFee());
			return transaction;
		}
	}

	private Transaction toModel(final MultisigSignatureRequest request, final WalletPassword password) {
		final Account signer = this.getSenderAccount(request.getWalletName(), request.getSenderAddress(), password);
		final Account multisigAccount = this.accountLookup.findByAddress(request.getMultisigAddress());
		final TimeInstant timeStamp = this.timeProvider.getCurrentTime();

		final MultisigSignatureTransaction multisigTransaction = new MultisigSignatureTransaction(
				timeStamp,
				signer,
				multisigAccount,
				request.getInnerTransactionHash());
		multisigTransaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
		multisigTransaction.setFee(request.getFee());
		return multisigTransaction;
	}

	private Transaction toModel(final MultisigModificationRequest request, final WalletPassword password) {
		final boolean isMultisig = request.getType() == TransactionViewModel.Type.Multisig_Aggregate_Modification.getValue();
		final TimeInstant timeStamp = this.timeProvider.getCurrentTime();

		final List<MultisigCosignatoryModification> modifications = new ArrayList<>();
		modifications.addAll(this.mapToModifications(request.getAddedCosignatories(), MultisigModificationType.AddCosignatory));
		modifications.addAll(this.mapToModifications(request.getRemovedCosignatories(), MultisigModificationType.DelCosignatory));

		if (modifications.size() != request.getAddedCosignatories().size() + request.getRemovedCosignatories().size()) {
			throw new NccException(NccException.Code.COSIGNATORY_NO_PUBLIC_KEY);
		}

		final Account sender = isMultisig
				? this.accountLookup.findByAddress(request.getMultisigAccount())
				: this.getSenderAccount(request.getWalletName(), request.getMultisigAccount(), password);

		final MultisigAggregateModificationTransaction transaction = request.getMinCosignatoriesModification() == null ?
				new MultisigAggregateModificationTransaction(
						1,
						timeStamp,
						sender,
						modifications,
						null) :
				new MultisigAggregateModificationTransaction(
						timeStamp,
						sender,
						modifications,
						request.getMinCosignatoriesModification());

		transaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
		transaction.setFee(request.getFee());

		if (!isMultisig) {
			return transaction;
		}

		// wrap in a multisig
		final Account issuer = this.getSenderAccount(request.getWalletName(), request.getIssuerAddress(), password);
		final MultisigTransaction multisigTransaction = new MultisigTransaction(
				timeStamp,
				issuer,
				transaction);
		multisigTransaction.setDeadline(timeStamp.addHours(request.getHoursDue()));
		multisigTransaction.setFee(request.getMultisigFee());
		return multisigTransaction;
	}

	private Collection<MultisigCosignatoryModification> mapToModifications(final Collection<Address> addresses, final MultisigModificationType type) {
		return addresses.stream()
				.map(this.accountLookup::findByAddress)
				.filter(a -> null != a.getAddress().getPublicKey())
				.map(o -> new MultisigCosignatoryModification(type, o))
				.collect(Collectors.toList());
	}

	private Account getSenderAccount(final WalletName walletName, final Address accountId, final WalletPassword password) {
		final PrivateKey privateKey = this.getSenderWallet(walletName, password).getAccountPrivateKey(accountId);
		return new Account(new KeyPair(privateKey));
	}

	private Account getRemoteAccount(final WalletName walletName, final Address accountId, final WalletPassword password) {
		final Wallet wallet = this.getSenderWallet(walletName, password);
		final WalletAccount account = wallet.tryGetWalletAccount(accountId);
		final PrivateKey privateKey = account.getRemoteHarvestingPrivateKey();
		return new Account(new KeyPair(privateKey));
	}

	private Wallet getSenderWallet(final WalletName walletName, final WalletPassword password) {
		final Wallet wallet = this.walletServices.open(new WalletNamePasswordPair(walletName, password));
		if (null == wallet) {
			throw new IllegalArgumentException("unable to open wallet");
		}

		return wallet;
	}

	private Message createMessage(
			final String message,
			final boolean hexMessage,
			final boolean shouldEncrypt,
			final Account sender,
			final Account recipient) {
		if (null == message) {
			return null;
		}

		final byte[] messageBytes = hexMessage ?
				ArrayUtils.concat(new byte[] { (byte)0xFE }, HexEncoder.getBytes(message)) :
				StringEncoder.getBytes(message);
		if (!shouldEncrypt) {
			return new PlainMessage(messageBytes);
		}

		if (!recipient.hasPublicKey()) {
			throw new NccException(NccException.Code.NO_PUBLIC_KEY);
		}

		return SecureMessage.fromDecodedPayload(sender, recipient, messageBytes);
	}
}
