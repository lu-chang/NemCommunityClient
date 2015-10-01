package org.nem.ncc.wallet.storage;

import org.nem.ncc.storable.entity.storage.StorableEntityDescriptor;
import org.nem.ncc.wallet.*;

/**
 * An interface that describes a wallet.
 */
public interface WalletDescriptor extends StorableEntityDescriptor<StorableWallet, WalletName, WalletFileExtension> {

	/**
	 * Gets the wallet name.
	 *
	 * @return The wallet name.
	 */
	WalletName getWalletName();

	/**
	 * Gets the wallet's location.
	 *
	 * @return The wallet's location.
	 */
	String getWalletLocation();
}