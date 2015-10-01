package org.nem.ncc.controller.requests;

import org.nem.core.model.Address;
import org.nem.core.serialization.*;

/**
 * Helper class to pass partial signature transaction data to NCC.
 */
public class PartialSignatureInformationRequest {
	private final Address multisigAddress;
	private final Address cosignatoryAddress;

	/**
	 * Creates a partial signature information request.
	 *
	 * @param multisigAddress The multisig address.
	 * @param cosignatoryAddress The cosignatory address.
	 */
	public PartialSignatureInformationRequest(final Address multisigAddress, final Address cosignatoryAddress) {
		this.multisigAddress = multisigAddress;
		this.cosignatoryAddress = cosignatoryAddress;
	}

	/**
	 * Deserializes a partial signature information request.
	 *
	 * @param deserializer The deserializer.
	 */
	public PartialSignatureInformationRequest(final Deserializer deserializer) {
		this.multisigAddress = Address.readFromOptional(deserializer, "multisig", AddressEncoding.COMPRESSED);
		this.cosignatoryAddress = Address.readFromOptional(deserializer, "cosignatory", AddressEncoding.COMPRESSED);
	}

	/**
	 * Gets the multisig account id.
	 *
	 * @return The multisig account id.
	 */
	public Address getMultisigAddress() {
		return this.multisigAddress;
	}

	/**
	 * Gets the cosignatory account id.
	 *
	 * @return The cosignatory account id.
	 */
	public Address getCosignatoryAddress() {
		return this.cosignatoryAddress;
	}
}
