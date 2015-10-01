package org.nem.ncc.wallet;

import org.nem.ncc.wallet.storage.WalletDescriptor;

/**
 * Repository class for loading and saving wallets.
 */
public interface WalletRepository {

	/**
	 * Saves the wallet to the specified descriptor.
	 *
	 * @param descriptor The output descriptor.
	 * @param wallet The wallet.
	 */
	void save(final WalletDescriptor descriptor, final StorableWallet wallet);

	/**
	 * Loads a wallet from the specified descriptor.
	 *
	 * @param descriptor The input descriptor.
	 * @return The wallet.
	 */
	StorableWallet load(final WalletDescriptor descriptor);
}
