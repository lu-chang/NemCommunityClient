package org.nem.ncc.wallet;

import org.nem.ncc.wallet.storage.WalletDescriptor;

import java.util.List;

/**
 * Interface for locating wallets.
 */
public interface WalletLocator {

	/**
	 * Gets all wallet descriptors.
	 *
	 * @return A list of all wallet descriptors.
	 */
	List<WalletDescriptor> getAllWallets();
}
