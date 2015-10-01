package org.nem.ncc.wallet;

import org.nem.ncc.exceptions.*;

/**
 * Exception that is used when a wallet operation fails.
 */
public class WalletException extends NccException {
	/**
	 * Wallet storage exception codes.
	 */
	public enum Code implements ValueBasedEnum {
		/**
		 * An account can not be added to the wallet multiple times.
		 */
		WALLET_ALREADY_CONTAINS_ACCOUNT(110),

		/**
		 * The primary account cannot be removed from a wallet.
		 */
		WALLET_PRIMARY_ACCOUNT_CANNOT_BE_REMOVED(108),

		/**
		 * An account is not in the wallet.
		 */
		WALLET_ACCOUNT_NOT_IN_WALLET(107);

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
	 * Creates a new wallet exception.
	 *
	 * @param code The exception code.
	 */
	public WalletException(final Code code) {
		super(code);
	}
}