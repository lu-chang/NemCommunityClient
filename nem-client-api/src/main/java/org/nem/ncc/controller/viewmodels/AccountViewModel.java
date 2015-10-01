package org.nem.ncc.controller.viewmodels;

import org.nem.core.crypto.PublicKey;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.*;

import java.util.List;

/**
 * Represents an account.
 */
public class AccountViewModel implements SerializableEntity {
	private final Address address;
	private final AccountRemoteStatus remoteStatus;
	private final PublicKey publicKey;
	private final Amount balance;
	private final Amount vestedBalance;
	private final BlockAmount harvestedBlocks;
	private final Double importance;
	private final AccountStatus status;
	private final List<AccountInfo> multisigAccounts;
	private final List<AccountInfo> cosignatories;
	private final PublicKey remotePublicKey;

	/**
	 * Creates a new account view model around an account metadata pair.
	 *
	 * @param accountMetaDataPair The account metadata pair.
	 */
	public AccountViewModel(
			final AccountMetaDataPair accountMetaDataPair,
			final PublicKey remotePublicKey) {
		this(
				accountMetaDataPair.getEntity(),
				accountMetaDataPair.getMetaData().getStatus(),
				accountMetaDataPair.getMetaData().getRemoteStatus(),
				accountMetaDataPair.getMetaData().getCosignatoryOf(),
				accountMetaDataPair.getMetaData().getCosignatories(),
				remotePublicKey);
	}

	/**
	 * Creates a new account view model.
	 *
	 * @param info The account info.
	 * @param status The account status.
	 * @param remoteStatus The remote account status.
	 * @param multisigAccounts The list of multisig accounts that this account can cosign.
	 */
	public AccountViewModel(
			final AccountInfo info,
			final AccountStatus status,
			final AccountRemoteStatus remoteStatus,
			final List<AccountInfo> multisigAccounts,
			final List<AccountInfo> cosignatories,
			final PublicKey remotePublicKey) {
		this.address = info.getAddress();
		this.remoteStatus = remoteStatus;

		this.publicKey = null == info.getKeyPair()
				? null
				: info.getKeyPair().getPublicKey();

		this.balance = info.getBalance();
		this.vestedBalance = info.getVestedBalance();
		this.harvestedBlocks = info.getNumHarvestedBlocks();
		this.importance = info.getImportance();
		this.status = status;
		this.multisigAccounts = multisigAccounts;
		this.cosignatories = cosignatories;
		this.remotePublicKey = remotePublicKey;
	}

	/**
	 * Gets the account address.
	 *
	 * @return The address.
	 */
	public Address getAddress() {
		return this.address;
	}

	/**
	 * Gets the account remote status.
	 *
	 * @return The remote status.
	 */
	public AccountRemoteStatus getRemoteStatus() {
		return this.remoteStatus;
	}

	/**
	 * Gets the account public key.
	 *
	 * @return The public key.
	 */
	public PublicKey getPublicKey() {
		return this.publicKey;
	}

	/**
	 * Gets the account balance.
	 *
	 * @return The balance.
	 */
	public Amount getBalance() {
		return this.balance;
	}

	/**
	 * Gets the account vested balance.
	 *
	 * @return The balance.
	 */
	public Amount getVestedBalance() {
		return this.vestedBalance;
	}

	/**
	 * Gets the number of blocks harvested by the account.
	 *
	 * @return The number of harvested blocks.
	 */
	public BlockAmount getHarvestedBlocks() {
		return this.harvestedBlocks;
	}

	/**
	 * Gets the importance of the account.
	 *
	 * @return The importance.
	 */
	public Double getImportance() {
		return this.importance;
	}

	/**
	 * Gets the status of the account.
	 *
	 * @return The status.
	 */
	public AccountStatus getStatus() {
		return this.status;
	}

	/**
	 * Gets the list of accounts (info) that this account is cosignatory of.
	 *
	 * @return The list of accounts info.
	 */
	public List<AccountInfo> getMultisigAccounts() {
		return this.multisigAccounts;
	}

	/**
	 * Gets the list of accounts (info) that are cosignatories of this account.
	 *
	 * @return The list of accounts info.
	 */
	public List<AccountInfo> getCosignatories() {
		return this.cosignatories;
	}

	/**
	 * Gets the account's remote public key.
	 *
	 * @return The public key.
	 */
	public PublicKey getRemotePublicKey() {
		return this.remotePublicKey;
	}

	@Override
	public void serialize(final Serializer serializer) {
		Address.writeTo(serializer, "address", this.address);
		AccountRemoteStatus.writeTo(serializer, "remoteStatus", this.remoteStatus);
		serializer.writeBytes("publicKey", null == this.publicKey ? null : this.publicKey.getRaw());
		Amount.writeTo(serializer, "balance", this.balance);
		Amount.writeTo(serializer, "vestedBalance", this.vestedBalance);
		serializer.writeDouble("importance", this.importance);
		BlockAmount.writeTo(serializer, "harvestedBlocks", this.harvestedBlocks);
		AccountStatus.writeTo(serializer, "status", this.status);
		serializer.writeObjectArray("multisigAccounts", this.multisigAccounts);
		serializer.writeObjectArray("cosignatories", this.cosignatories);

		if (this.remotePublicKey != null) {
			serializer.writeBytes("remotePublicKey", this.remotePublicKey.getRaw());
			Address.writeTo(serializer, "remoteAddress", Address.fromPublicKey(this.remotePublicKey));
		}
	}
}
