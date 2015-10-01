package org.nem.ncc.controller.requests;

import org.nem.core.model.*;
import org.nem.core.model.primitive.Amount;
import org.nem.core.serialization.*;
import org.nem.ncc.wallet.*;

import java.util.List;

/**
 * A request containing all information necessary to create a multisig aggregate modification transaction.
 * TODO 20150131 J-G: probably makes sense to have a base class for these
 */
public class MultisigModificationRequest {
	private final WalletName walletName;
	private final int type; // TODO 20150621 J-G: probably can be boolean?
	private final WalletPassword password;
	private final Address multisigAddress;
	private final Address issuerAddress;
	private final List<Address> addedCosignatories;
	private final List<Address> removedCosignatories;
	private final MultisigMinCosignatoriesModification minCosignatoriesModification;
	private final int hoursDue;
	private final Amount fee;
	private final Amount multisigFee;

	/**
	 * Creates a new multisig modification request.
	 *
	 * @param walletName The wallet name.
	 * @param type The modification type (multisig or not).
	 * @param password The wallet password.
	 * @param multisigAddress The multisig address.
	 * @param issuerAddress The issuer address.
	 * @param addedCosignatories The list of cosignatory addresses to add.
	 * @param removedCosignatories The list of cosignatory addresses to remove.
	 * @param minCosignatoriesModification The minimum cosignatories modification.
	 * @param hoursDue The number of hours for the transaction to be valid.
	 * @param fee The fee.
	 * @param multisigFee The multisig fee.
	 */
	public MultisigModificationRequest(
			final WalletName walletName,
			final int type,
			final WalletPassword password,
			final Address multisigAddress,
			final Address issuerAddress,
			final List<Address> addedCosignatories,
			final List<Address> removedCosignatories,
			final MultisigMinCosignatoriesModification minCosignatoriesModification,
			final int hoursDue,
			final Amount fee,
			final Amount multisigFee) {
		this.walletName = walletName;
		this.type = type;
		this.password = password;
		this.multisigAddress = multisigAddress;
		this.issuerAddress = issuerAddress;
		this.addedCosignatories = addedCosignatories;
		this.removedCosignatories = removedCosignatories;
		this.minCosignatoriesModification = minCosignatoriesModification;
		this.hoursDue = hoursDue;
		this.fee = fee;
		this.multisigFee = multisigFee;
	}

	/**
	 * Deserializes a multisig modification request.
	 *
	 * @param deserializer The deserializer.
	 */
	public MultisigModificationRequest(final Deserializer deserializer) {
		this.walletName = WalletName.readFrom(deserializer, "wallet");
		this.type = deserializer.readInt("type");
		this.password = WalletPassword.readFrom(deserializer, "password");
		this.multisigAddress = Address.readFrom(deserializer, "account");
		this.issuerAddress = Address.readFromOptional(deserializer, "issuer", AddressEncoding.COMPRESSED);
		this.addedCosignatories = deserializer.readObjectArray("addedCosignatories", obj -> Address.readFrom(obj, "address", AddressEncoding.COMPRESSED));
		this.removedCosignatories = deserializer.readObjectArray("removedCosignatories", obj -> Address.readFrom(obj, "address", AddressEncoding.COMPRESSED));
		this.minCosignatoriesModification = deserializer.readOptionalObject("minCosignatories", MultisigMinCosignatoriesModification::new);
		this.hoursDue = deserializer.readInt("hoursDue");
		this.fee = Amount.readFrom(deserializer, "fee");
		this.multisigFee = Amount.readFrom(deserializer, "multisigFee");
	}

	/**
	 * Gets the wallet name.
	 *
	 * @return The wallet name.
	 */
	public WalletName getWalletName() {
		return this.walletName;
	}

	/**
	 * Gets an account that should be converted to multisig
	 * or an existing multisig account that needs to be modified.
	 *
	 * @return The multisig account.
	 */
	public Address getMultisigAccount() {
		return this.multisigAddress;
	}

	/**
	 * Gets the issuer account id (can be null).
	 *
	 * @return The issuer account.
	 */
	public Address getIssuerAddress() {
		return this.issuerAddress;
	}

	/**
	 * Gets list of cosignatories to add.
	 *
	 * @return The cosignatories.
	 */
	public List<Address> getAddedCosignatories() {
		return this.addedCosignatories;
	}

	/**
	 * Gets list of cosignatories to remove.
	 *
	 * @return The cosignatories.
	 */
	public List<Address> getRemovedCosignatories() {
		return this.removedCosignatories;
	}

	/**
	 * Gets the min cosignatories modification.
	 *
	 * @return The min cosignatories modification.
	 */
	public MultisigMinCosignatoriesModification getMinCosignatoriesModification() {
		return this.minCosignatoriesModification;
	}

	/**
	 * Gets the hours due.
	 *
	 * @return The hours due.
	 */
	public int getHoursDue() {
		return this.hoursDue;
	}

	/**
	 * Gets the password.
	 *
	 * @return The password.
	 */
	public WalletPassword getPassword() {
		return this.password;
	}

	/**
	 * Gets the fee.
	 *
	 * @return The fee.
	 */
	public Amount getFee() {
		return this.fee;
	}

	/**
	 * Gets the multisig fee
	 *
	 * @return The multisig fee.
	 */
	public Amount getMultisigFee() {
		return this.multisigFee;
	}

	/**
	 * Gets the type of transfer (multisig or normal).
	 *
	 * @return The type of transfer.
	 */
	public int getType() {
		return this.type;
	}
}
