package org.nem.ncc.wallet;

import org.nem.ncc.wallet.storage.WalletDescriptor;

import java.util.List;

/**
 * This class provides an abstraction on top of wallet storage.
 */
public interface WalletStorage {

	/**
	 * Gets all wallet descriptors.
	 *
	 * @return A list of all wallet descriptors.
	 */
	List<WalletDescriptor> getAllWallets();

	/**
	 * Gets a wallet descriptor given a wallet name.
	 *
	 * @return The wallet descriptor.
	 */
	WalletDescriptor getWallet(final WalletName walletName);
}