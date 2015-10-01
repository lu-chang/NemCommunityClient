package org.nem.ncc.wallet.storage;

import org.nem.ncc.wallet.*;

/**
 * Factory for creating wallet descriptors.
 */
public interface WalletDescriptorFactory {

	/**
	 * Creates a wallet descriptor corresponding to a new wallet.
	 *
	 * @param pair The wallet name and password.
	 * @param fileExtension The wallet file extension.
	 * @return The descriptor.
	 */
	WalletDescriptor createNew(final WalletNamePasswordPair pair, final WalletFileExtension fileExtension);

	/**
	 * Opens a wallet descriptor corresponding to en existing wallet.
	 *
	 * @param pair The wallet name and password.
	 * @param fileExtension The wallet file extension.
	 * @return The descriptor.
	 */
	WalletDescriptor openExisting(final WalletNamePasswordPair pair, final WalletFileExtension fileExtension);
}
