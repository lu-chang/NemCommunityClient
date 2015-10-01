package org.nem.ncc.wallet.storage;

import org.nem.ncc.exceptions.ValueBasedEnum;
import org.nem.ncc.storable.entity.storage.StorableEntityStorageException;

import java.util.HashMap;

/**
 * Exception that is used when a wallet storage operation fails.
 */
public class WalletStorageException extends StorableEntityStorageException {

	/**
	 * Wallet storage exception codes.
	 */
	public enum Code implements ValueBasedEnum {
		/**
		 * The specified wallet file does not exist.
		 */
		WALLET_DOES_NOT_EXIST(101),

		/**
		 * The wallet could not be saved.
		 */
		WALLET_COULD_NOT_BE_SAVED(102),

		/**
		 * The wallet could not be read.
		 */
		WALLET_COULD_NOT_BE_READ(103),

		/**
		 * The wallet password was incorrect.
		 */
		WALLET_PASSWORD_INCORRECT(104),

		/**
		 * The wallet password cannot be a null sting.
		 */
		WALLET_PASSWORD_CANNOT_BE_NULL(105),

		/**
		 * The wallet creation failed because a wallet of the same name already exists.
		 */
		WALLET_ALREADY_EXISTS(109),

		/**
		 * The specified wallet file is actually a directory.
		 */
		WALLET_CANNOT_BE_DIRECTORY(111),

		/**
		 * The specified wallet file has an invalid extension.
		 */
		WALLET_HAS_INVALID_EXTENSION(112),

		/**
		 * The wallet could not be deleted.
		 */
		WALLET_COULD_NOT_BE_DELETED(113);

		private static final HashMap<Integer, Code> lookup = new HashMap<>();

		static {
			for (final Code c : Code.values()) {
				lookup.put(c.value(), c);
			}
		}

		private final int value;

		Code(final int value) {
			this.value = value;
		}

		@Override
		public int value() {
			return this.value;
		}
	}

	/**
	 * Creates a new wallet storage exception.
	 *
	 * @param code The exception code.
	 */
	public WalletStorageException(final ValueBasedEnum code) {
		super(code);
	}

	/**
	 * Creates a new wallet storage exception.
	 *
	 * @param code The exception code.
	 * @param cause The exception cause.
	 */
	public WalletStorageException(final ValueBasedEnum code, final Throwable cause) {
		super(code, cause);
	}

	/**
	 * Creates a new wallet storage exception.
	 *
	 * @param value The exception code value.
	 */
	public WalletStorageException(final int value) {
		super(Code.lookup.get(value));
	}

	/**
	 * Creates a new wallet storage exception.
	 *
	 * @param value The exception code value.
	 * @param cause The exception cause.
	 */
	public WalletStorageException(final int value, final Throwable cause) {
		super(Code.lookup.get(value), cause);
	}
}