package org.nem.ncc.controller.requests;

import org.nem.core.model.*;
import org.nem.core.serialization.*;

import java.util.List;

/**
 * A request to get information about a partially created multisig modification.
 */
public class PartialModificationInformationRequest {
	private final Address multisigAddress;
	private final List<Address> cosignatoriesAddresses;
	private final MultisigMinCosignatoriesModification minCosignatoriesModification;

	/**
	 * Creates a partial modification information request.
	 *
	 * @param multisigAddress The multisig address.
	 * @param cosignatoriesAddresses The cosignatory addresses.
	 * @param minCosignatoriesModification The minimum cosignatories Modification.
	 */
	public PartialModificationInformationRequest(
			final Address multisigAddress,
			final List<Address> cosignatoriesAddresses,
			final MultisigMinCosignatoriesModification minCosignatoriesModification) {
		if (null == multisigAddress) {
			throw new IllegalArgumentException("PartialModificationInformationRequest needs valid multisig address");
		}

		if (null == cosignatoriesAddresses) {
			throw new IllegalArgumentException("PartialModificationInformationRequest needs valid cosignatory address list");
		}

		if (null == minCosignatoriesModification) {
			throw new IllegalArgumentException("PartialModificationInformationRequest needs valid minimum cosignatories modification");
		}

		this.multisigAddress = multisigAddress;
		this.cosignatoriesAddresses = cosignatoriesAddresses;
		this.minCosignatoriesModification = minCosignatoriesModification;
	}

	/**
	 * Deserializes a partial modification information request.
	 *
	 * @param deserializer The deserializer.
	 */
	public PartialModificationInformationRequest(final Deserializer deserializer) {
		this.multisigAddress = Address.readFrom(deserializer, "multisig", AddressEncoding.COMPRESSED);
		this.cosignatoriesAddresses = deserializer.readObjectArray("cosignatories", obj -> Address.readFrom(obj, "address", AddressEncoding.COMPRESSED));
		this.minCosignatoriesModification = deserializer.readObject("minCosignatories", MultisigMinCosignatoriesModification::new);
	}

	/**
	 * Gets the multisig address.
	 *
	 * @return The multisig address.
	 */
	public Address getMultisigAddress() {
		return this.multisigAddress;
	}

	/**
	 * Gets the list of cosignatory addresses.
	 *
	 * @return The list of cosignatory addresses.
	 */
	public List<Address> getCosignatoriesAddresses() {
		return this.cosignatoriesAddresses;
	}

	/**
	 * Gets the min cosignatory modification.
	 *
	 * @return The min cosignatory modification.
	 */
	public MultisigMinCosignatoriesModification getMinCosignatoriesModification() {
		return this.minCosignatoriesModification;
	}
}
