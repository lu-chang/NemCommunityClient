package org.nem.ncc.wallet;

import org.nem.core.crypto.PrivateKey;
import org.nem.core.model.Address;
import org.nem.core.serialization.SerializableEntity;

import java.util.List;

/**
 * Represents a NEM wallet that can contain zero or more accounts.
 */
public interface Wallet extends SerializableEntity {

	/**
	 * Gets the wallet name.
	 *
	 * @return The wallet name.
	 */
	WalletName getName();

	/**
	 * Gets the primary account.
	 *
	 * @return The primary account.
	 */
	WalletAccount getPrimaryAccount();

	/**
	 * Gets a copy of the other accounts.
	 *
	 * @return The other accounts.
	 */
	List<WalletAccount> getOtherAccounts();

	/**
	 * Adds an additional account to this wallet.
	 *
	 * @param account The other account.
	 */
	void addOtherAccount(final WalletAccount account);

	/**
	 * Sets the primary account.
	 *
	 * @param address The address of the desired primary account.
	 */
	void setPrimaryAccount(final Address address);

	/**
	 * Removes an account from this wallet.
	 *
	 * @param address The address of the account to remove.
	 */
	void removeAccount(final Address address);

	/**
	 * Gets the private key associated with an account in this wallet.
	 *
	 * @param address The account address.
	 * @return The account private key.
	 */
	PrivateKey getAccountPrivateKey(final Address address);

	/**
	 * Tries to get the wallet account associated with the specified address.
	 *
	 * @param address The account address.
	 * @return The wallet account or null if the account is not in this wallet.
	 */
	WalletAccount tryGetWalletAccount(final Address address);
}
