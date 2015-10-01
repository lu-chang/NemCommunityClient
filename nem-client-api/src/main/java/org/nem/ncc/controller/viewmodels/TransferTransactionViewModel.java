package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.*;
import org.nem.core.model.mosaic.Mosaic;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.Serializer;
import org.nem.core.utils.*;

import java.util.*;

/**
 * TODO 20150131 J-G: fix empty comments
 * TODO 20150131 J-G: i guess the test coverage is ok for this as you mostly just split up the original transferviewmodel into two?
 * > i mean i don't think you added anything to this class's functionality?
 */
public class TransferTransactionViewModel extends TransactionViewModel {
	private static final int INCOMING_FLAG = 1;
	private static final int OUTGOING_FLAG = 2;
	private static final int SELF_FLAG = 3;

	private final Address recipient;
	private final Amount amount;
	private final String message;
	private final Collection<Mosaic> mosaics;

	private final boolean isEncrypted;
	private final boolean hexMessage;
	// TODO 20150524 J-G: you don't appear to be using this, not sure if it's for this release or not
	// TODO 20150525 G-J: it is used in isHexMessage, not sure why idea shows it as unused,
	// anyway, you're right that it's currently not used, but I've added it in case we'd like to use it in UI
	private final int direction; // 1 - incoming, 2 - outgoing, 3 - self

	/**
	 * Creates a transfer transaction view model.
	 *
	 * @param metaDataPair The meta data pair.
	 * @param relativeAccountAddress The account address (either sender or recipient or both).
	 * @param lastBlockHeight The last block height.
	 */
	public TransferTransactionViewModel(final TransactionMetaDataPair metaDataPair, final Address relativeAccountAddress, final BlockHeight lastBlockHeight) {
		super(Type.Transfer, metaDataPair, lastBlockHeight);

		final TransferTransaction transfer = (TransferTransaction)metaDataPair.getEntity();
		this.recipient = transfer.getRecipient().getAddress();
		this.amount = transfer.getAmount();

		final Message message = transfer.getMessage();
		this.message = getMessageText(message);
		this.hexMessage = getMessageHexFlag(message);
		this.isEncrypted = isEncrypted(message);

		this.mosaics = transfer.getAttachment().getMosaics();

		this.direction = (this.getSigner().equals(relativeAccountAddress) ? OUTGOING_FLAG : 0)
				+ (this.recipient.equals(relativeAccountAddress) ? INCOMING_FLAG : 0);
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		Address.writeTo(serializer, "recipient", this.recipient);
		Amount.writeTo(serializer, "amount", this.amount);
		if (this.message != null) {
			serializer.writeString("message", this.message);
			serializer.writeInt("encrypted", this.isEncrypted ? 1 : 0);
		}

		if (this.mosaics != null) {
			serializer.writeObjectArray("mosaics", this.mosaics);
		}

		serializer.writeInt("direction", this.direction);
	}

	/**
	 * Gets the recipient address.
	 *
	 * @return The recipient address.
	 */
	public Address getRecipient() {
		return this.recipient;
	}

	/**
	 * Gets the amount.
	 *
	 * @return The amount.
	 */
	public Amount getAmount() {
		return this.amount;
	}

	/**
	 * Gets the message.
	 *
	 * @return The message.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Gets a value indicating whether or not the message is encrypted.
	 *
	 * @return true if the message is encrypted.
	 */
	public boolean isEncrypted() {
		return this.isEncrypted;
	}

	/**
	 * Gets the transaction direction relative to the reference account.
	 *
	 * @return The transaction direction.
	 */
	public int getDirection() {
		return this.direction;
	}

	//region helper functions
	private static boolean isEncrypted(final Message message) {
		return null != message && MessageTypes.SECURE == message.getType();
	}

	private static String getMessageText(final Message message) {
		if (null == message) {
			return null;
		}

		if (!message.canDecode()) {
			return "Warning: message cannot be decoded!";
		}

		final byte[] payload = message.getDecodedPayload();
		if (payload.length > 1 && payload[0] == (byte)0xfe) {
			return HexEncoder.getString(Arrays.copyOfRange(payload, 1, payload.length));
		} else {
			return StringEncoder.getString(payload);
		}
	}

	private static boolean getMessageHexFlag(final Message message) {
		if (null == message) {
			return false;
		}

		if (!message.canDecode()) {
			return false;
		}
		final byte[] payload = message.getDecodedPayload();
		return (payload.length > 1 && payload[0] == (byte)0xfe);
	}
	//endregion
}
