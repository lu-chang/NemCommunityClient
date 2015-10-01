package org.nem.ncc.controller.viewmodels;

import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.*;
import org.nem.core.time.UnixTime;

/**
 * A view model representing a transfer transaction.
 */
public class TransactionViewModel implements SerializableEntity {
	// This enum needs to be kept in sync with JS TransactionType
	public enum Type {
		Unknown(0)
		, Transfer(20)
		, Importance_Transfer(21)
		, Aggregate_Modification(22)
		, Provision_Namespace(23)
		, Mosaic_Creation(24)
		, Mosaic_Supply(25)

		, Multisig_Signature(40)

		, Multisig_Transfer(50)
		, Multisig_Importance_Transfer(51)
		, Multisig_Aggregate_Modification(52)
		, Multisig_Provision_Namespace(53)
		, Multisig_Mosaic_Creation(54)
		, Multisig_Mosaic_Supply(55)
		;

		private final int value;

		Type(final int value) {
			this.value = value;
		}

		/**
		 * Gets the underlying integer representation of the status.
		 *
		 * @return The underlying value.
		 */
		public int getValue() {
			return this.value;
		}
	}

	private final Type transactionViewModelType;
	private final long transactionId;
	private final Hash transactionHash;

	private final Address signer;
	private final long timeStamp;
	private final long deadline;
	private final Amount fee;

	private final boolean isConfirmed;
	private final long confirmations;
	private final long blockHeight;

	/**
	 * Creates a new transaction view model around a transaction (confirmed or unconfirmed).
	 *
	 * @param transactionViewModelType The transaction view model type.
	 * @param metaDataPair The meta data pair.
	 * @param lastBlockHeight The last block height (for calculating confirmations).
	 */
	public TransactionViewModel(
			final Type transactionViewModelType,
			final TransactionMetaDataPair metaDataPair,
			final BlockHeight lastBlockHeight) {
		final Transaction transaction = metaDataPair.getEntity();

		this.transactionViewModelType = transactionViewModelType;
		this.transactionHash = HashUtils.calculateHash(transaction);

		this.signer = transaction.getSigner().getAddress();
		this.timeStamp = UnixTime.fromTimeInstant(transaction.getTimeStamp()).getMillis();
		this.deadline = UnixTime.fromTimeInstant(transaction.getDeadline()).getMillis();
		this.fee = transaction.getFee();

		if (metaDataPair.getMetaData() == null) {
			this.confirmations = 0;
			this.blockHeight = 0;
			// TODO BR -> G: can we safely set transactionId to 0 or Long.MAX_VALUE here?
			this.transactionId = this.transactionHash.getShortId();
			this.isConfirmed = false;
		} else {
			this.confirmations = lastBlockHeight.subtract(metaDataPair.getMetaData().getHeight()) + 1;
			this.blockHeight = metaDataPair.getMetaData().getHeight().getRaw();
			// TODO 20141201 J-B: is it confusing that both Transaction and TransactionMetaData have ids now?
			// we might want to rename one
			// TODO 20150202 BR -> J: there is only one id, but it is used in two different ways (see above).
			this.transactionId = metaDataPair.getMetaData().getId();
			this.isConfirmed = true;
		}
	}

	/**
	 * Gets the transaction type.
	 *
	 * @return The transaction type.
	 */
	public Type getTransactionViewModelType() {
		return this.transactionViewModelType;
	}

	/**
	 * Gets the transaction id.
	 *
	 * @return The transaction id.
	 */
	public long getId() {
		return this.transactionId;
	}

	/**
	 * Gets the transaction hash.
	 *
	 * @return The transaction hash.
	 */
	public Hash getHash() {
		return this.transactionHash;
	}

	/**
	 * Gets the signer address.
	 *
	 * @return The signer address.
	 */
	public Address getSigner() {
		return this.signer;
	}

	/**
	 * Gets the timestamp (unix time).
	 *
	 * @return The timestamp.
	 */
	public long getTimeStamp() {
		return this.timeStamp;
	}

	/**
	 * Gets the fee.
	 *
	 * @return The fee.
	 */
	public Amount getFee() {
		return this.fee;
	}

	/**
	 * Gets a value indicating whether or not the transaction is confirmed.
	 *
	 * @return true if the message is confirmed.
	 */
	public boolean isConfirmed() {
		return this.isConfirmed;
	}

	/**
	 * Gets the number of confirmations.
	 *
	 * @return The number of confirmations.
	 */
	public long getConfirmations() {
		return this.confirmations;
	}

	/**
	 * Gets the height of the block that includes the transaction
	 * or zero if the transaction is unconfirmed.
	 *
	 * @return The height of the block that includes the transaction.
	 */
	public long getBlockHeight() {
		return this.blockHeight;
	}

	@Override
	public void serialize(final Serializer serializer) {
		this.serializeImpl(serializer);
	}

	protected void serializeImpl(final Serializer serializer) {
		serializer.writeInt("type", this.transactionViewModelType.getValue());
		serializer.writeLong("id", this.transactionId);
		serializer.writeString("hash", this.transactionHash.toString());

		Address.writeTo(serializer, "sender", this.signer);
		serializer.writeLong("timeStamp", this.timeStamp);
		serializer.writeLong("deadline", this.deadline);
		Amount.writeTo(serializer, "fee", this.fee);

		serializer.writeInt("confirmed", this.isConfirmed ? 1 : 0);
		serializer.writeLong("confirmations", this.confirmations);
		serializer.writeLong("blockHeight", this.blockHeight);
	}
}
