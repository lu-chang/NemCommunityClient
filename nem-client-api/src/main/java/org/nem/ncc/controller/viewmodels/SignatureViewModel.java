package org.nem.ncc.controller.viewmodels;

import org.nem.core.crypto.Signature;
import org.nem.core.serialization.*;

public class SignatureViewModel implements SerializableEntity {
	private final Signature signature;

	/**
	 * Creates a new Signature view model.
	 *
	 * @param signature The signature.
	 */
	public SignatureViewModel(final Signature signature) {
		this.signature = signature;
	}

	/**
	 * Gets the signature.
	 *
	 * @return The signature.
	 */
	public Signature getSignature() {
		return this.signature;
	}

	@Override
	public void serialize(final Serializer serializer) {
		Signature.writeTo(serializer, "signature", this.signature);
	}
}
