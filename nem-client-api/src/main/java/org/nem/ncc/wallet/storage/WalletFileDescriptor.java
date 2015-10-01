package org.nem.ncc.wallet.storage;

import org.nem.core.serialization.ObjectDeserializer;
import org.nem.ncc.storable.entity.storage.*;
import org.nem.ncc.wallet.*;

import java.io.File;
import java.util.function.Function;

/**
 * A WalletDescriptor implementation that references files stored on disk.
 */
public class WalletFileDescriptor extends StorableEntityFileDescriptor<StorableWallet, WalletName, WalletFileExtension> implements WalletDescriptor {

	/**
	 * Creates a new wallet file descriptor around a storable wallet and a file.
	 *
	 * @param file The wallet location.
	 */
	public WalletFileDescriptor(final File file) {
		this(file, MemoryWallet::new, WalletName::new, WalletFileExtension::new);
	}

	/**
	 * Creates a new wallet file descriptor around a storable wallet and a file.
	 *
	 * @param file The wallet location.
	 */
	public WalletFileDescriptor(
			final File file,
			final ObjectDeserializer<StorableWallet> deserializer,
			final Function<String, WalletName> nameActivator,
			final Function<String, WalletFileExtension> fileExtensionActivator) {
		super(file, deserializer, nameActivator, fileExtensionActivator);
	}

	@Override
	public WalletName getWalletName() {
		return super.getName();
	}

	@Override
	public String getWalletLocation() {
		return super.getStorableEntityLocation();
	}

	@Override
	protected StorableEntityStorageException getException(final int value, final Exception ex) {
		return null == ex ? new WalletStorageException(value) : new WalletStorageException(value, ex);
	}
}
