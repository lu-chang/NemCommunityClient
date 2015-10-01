package org.nem.ncc.controller.requests;

import org.nem.core.crypto.PrivateKey;
import org.nem.core.model.Address;
import org.nem.core.serialization.Deserializer;
import org.nem.ncc.wallet.*;

/**
 * Request object that requires a wallet name and a wallet password
 * but can contain any number of optional properties too.
 */
public class WalletNamePasswordBag extends WalletNamePasswordPair {
	private final Deserializer deserializer;

	/**
	 * Deserializes a wallet name / password bag.
	 *
	 * @param deserializer The deserializer.
	 */
	public WalletNamePasswordBag(final Deserializer deserializer) {
		super(deserializer);
		this.deserializer = deserializer;
	}

	/**
	 * Gets the account private key if it was specified.
	 *
	 * @return The account private key.
	 */
	public PrivateKey getAccountPrivateKey() {
		final String value = this.deserializer.readString("accountKey");
		return PrivateKey.fromHexString(value);
	}

	/**
	 * Gets the new name if it was specified.
	 *
	 * @return The new name.
	 */
	public WalletName getNewName() {
		return WalletName.readFrom(this.deserializer, "newName");
	}

	/**
	 * Gets the new password if it was specified.
	 *
	 * @return The new password.
	 */
	public WalletPassword getNewPassword() {
		return WalletPassword.readFrom(this.deserializer, "newPassword");
	}

	/**
	 * Gets the account address if it was specified.
	 *
	 * @return The account address.
	 */
	public Address getAccountAddress() {
		return Address.readFrom(this.deserializer, "account");
	}

	/**
	 * Gets the label if it was specified.
	 *
	 * @return The label.
	 */
	public String getLabel() {
		final String label = this.deserializer.readOptionalString("label");
		return null == label ? "" : label;
	}

	/**
	 * Gets the token if it was specified.
	 *
	 * @return The token.
	 */
	public String getToken() {
		final String token = this.deserializer.readOptionalString("token", 1024);
		return null == token ? "" : token;
	}
}
