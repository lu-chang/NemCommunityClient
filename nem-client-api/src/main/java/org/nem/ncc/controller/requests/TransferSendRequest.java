package org.nem.ncc.controller.requests;

import org.nem.core.model.Address;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.wallet.*;

/**
 * A request containing all information necessary to create a transfer.
 * The UX uses this request for both transfer and multisig transfer requests.
 */
public class TransferSendRequest {
	private final WalletName walletName;
	private final Address multisigAddress;
	private final Address senderAddress;
	private final Address recipientAddress;
	private final Amount amount;
	private final String message;
	private final boolean hexMessage;
	private final boolean shouldEncrypt;
	private final int hoursDue;
	private final WalletPassword password;
	private final Amount fee;
	private final Amount multisigFee;
	private final int type;
	private final int version;

	/**
	 * Creates a new transfer send request.
	 */
	public TransferSendRequest(
			final WalletName walletName,
			final Address multisigAddress,
			final Address senderAddress,
			final Address recipientAddress,
			final Amount amount,
			final String message,
			final boolean hexMessage,
			final boolean shouldEncrypt,
			final int hoursDue,
			final WalletPassword password,
			final Amount fee,
			final Amount multisigFee,
			final int type,
			final int version) {
		this.walletName = walletName;
		this.multisigAddress = multisigAddress;
		this.senderAddress = senderAddress;
		this.recipientAddress = recipientAddress;
		this.amount = amount;
		this.message = message;
		this.hexMessage = hexMessage;
		this.shouldEncrypt = shouldEncrypt;
		this.hoursDue = hoursDue;
		this.password = password;
		this.fee = fee;
		this.multisigFee = multisigFee;
		this.type = type;
		this.version = version;
	}

	/**
	 * Deserializes a transfer send request.
	 *
	 * @param deserializer The deserializer.
	 */
	public TransferSendRequest(final Deserializer deserializer) {
		this.walletName = WalletName.readFrom(deserializer, "wallet");
		this.type = deserializer.readInt("type");
		this.multisigAddress = Address.readFromOptional(deserializer, "multisigAccount", AddressEncoding.COMPRESSED);
		this.senderAddress = Address.readFrom(deserializer, "account");
		this.recipientAddress = Address.readFrom(deserializer, "recipient");
		this.amount = Amount.readFrom(deserializer, "amount");
		this.message = deserializer.readOptionalString("message");
		this.hexMessage = 0 != deserializer.readInt("hexMessage");
		this.shouldEncrypt = 0 != deserializer.readInt("encrypt");
		this.hoursDue = deserializer.readInt("hoursDue");
		this.password = WalletPassword.readFrom(deserializer, "password");
		this.fee = Amount.readFrom(deserializer, "fee");
		this.multisigFee = Amount.readFrom(deserializer, "multisigFee");
		this.version = deserializer.readInt("version");
	}

	/**
	 * Gets the multisig account id (can be null).
	 *
	 * @return The multisig account id.
	 */
	public Address getMultisigAddress() {
		return this.multisigAddress;
	}

	/**
	 * Gets the wallet name.
	 *
	 * @return The wallet name.
	 */
	public WalletName getWalletName() {
		return this.walletName;
	}

	/**
	 * Gets the transfer type.
	 *
	 * @return The transfer type (multisig or normal).
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Gets the transfer version.
	 *
	 * @return The transfer version.
	 */
	public int getVersion() { return this.version; }

	/**
	 * Gets the sender account id.
	 *
	 * @return The sender account id.
	 */
	public Address getSenderAddress() {
		return this.senderAddress;
	}

	/**
	 * Gets the recipient id.
	 *
	 * @return The recipient id.
	 */
	public Address getRecipientAddress() {
		return this.recipientAddress;
	}

	/**
	 * Gets the (optional) message.
	 *
	 * @return The message.
	 */
	public String getMessage() {
		return this.message;
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
	 * Gets a value indicating whether message is in hex or not.
	 *
	 * @return true if the payload is in hex.
	 */
	public boolean isHexMessage() {
		return this.hexMessage;
	}

	/**
	 * Gets a value indicating whether or not the payload should be encrypted.
	 *
	 * @return true if the payload should be encrypted.
	 */
	public boolean shouldEncrypt() {
		return this.shouldEncrypt;
	}

	/**
	 * Gets the hours due.
	 *
	 * @return The hours due.
	 */
	public int getHoursDue() {
		return this.hoursDue;
	}

	/**
	 * Gets the password.
	 *
	 * @return The password.
	 */
	public WalletPassword getPassword() {
		return this.password;
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
	 * Gets the multisig transaction fee.
	 *
	 * @return The fee.
	 */
	public Amount getMultisigFee() {
		return this.multisigFee;
	}
}
