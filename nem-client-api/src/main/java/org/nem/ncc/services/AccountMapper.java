package org.nem.ncc.services;

import org.nem.core.crypto.*;
import org.nem.core.model.Address;
import org.nem.ncc.controller.viewmodels.AccountViewModel;
import org.nem.ncc.wallet.WalletAccount;

/**
 * Helper class that is able to map various account representations to an AccountViewModel.
 */
public class AccountMapper {
	private final AccountMetaDataPairLookup accountLookup;

	/**
	 * Creates a wallet mapper.
	 *
	 * @param accountLookup The account lookup.
	 */
	public AccountMapper(final AccountMetaDataPairLookup accountLookup) {
		this.accountLookup = accountLookup;
	}

	/**
	 * Converts the specified model to a view model.
	 *
	 * @param account The model.
	 * @return The view model.
	 */
	public AccountViewModel toViewModel(final WalletAccount account) {
		// the remote harvesting public key is stored in the view model because it is displayed in the ux
		return this.toViewModel(account.getAddress(), getRemotePublicKey(account));
	}

	/**
	 * Creates a view model for the account with the specified address.
	 *
	 * @param address The address.
	 * @return The view model.
	 */
	public AccountViewModel toViewModel(final Address address) {
		return this.toViewModel(address, null);
	}

	private static PublicKey getRemotePublicKey(final WalletAccount account) {
		return (new KeyPair(account.getRemoteHarvestingPrivateKey())).getPublicKey();
	}

	private AccountViewModel toViewModel(final Address address, final PublicKey remotePublicKey) {
		return new AccountViewModel(this.accountLookup.findPairByAddress(address), remotePublicKey);
	}

	// TODO 20150131 J-G: not sure if this should be here (in this class).

	/**
	 * Forces refresh of account data.
	 *
	 * @param address The address.
	 */
	public void refreshAccount(final Address address) {
		this.accountLookup.refreshAccount(address);
	}
}
