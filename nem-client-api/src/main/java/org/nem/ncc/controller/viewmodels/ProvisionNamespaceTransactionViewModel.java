package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.*;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.Serializer;

/**
 * A provision namespace transaction view model.
 */
public class ProvisionNamespaceTransactionViewModel extends TransactionViewModel {
	private final Address rentalFeeSink;
	private final Amount rentalFee;
	private final String namespaceName;

	/**
	 * Creates a new provision namespace transaction view model.
	 *
	 * @param metaDataPair The metadata pair.
	 * @param lastBlockHeight The last block height.
	 */
	public ProvisionNamespaceTransactionViewModel(final TransactionMetaDataPair metaDataPair, final BlockHeight lastBlockHeight) {
		super(Type.Provision_Namespace, metaDataPair, lastBlockHeight);

		final ProvisionNamespaceTransaction transaction = (ProvisionNamespaceTransaction)metaDataPair.getEntity();
		this.rentalFeeSink = transaction.getRentalFeeSink().getAddress();
		this.rentalFee = transaction.getRentalFee();
		this.namespaceName = transaction.getResultingNamespaceId().toString();
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		Address.writeTo(serializer, "rentalFeeSink", this.rentalFeeSink);
		Amount.writeTo(serializer, "amount", this.rentalFee);
		serializer.writeString("namespace", this.namespaceName);
	}

	/**
	 * Gets the namespace rental fee sink account
	 *
	 * @return The rental fee sink account.
	 */
	public Address getRentalFeeSink() {
		return this.rentalFeeSink;
	}

	/**
	 * Gets the namespace rental fee.
	 *
	 * @return The rental fee.
	 */
	public Amount getRentalFee() {
		return this.rentalFee;
	}

	/**
	 * Gets the namespace expressed as a string.
	 *
	 * @return The namespace name.
	 */
	public String getNamespaceName() {
		return this.namespaceName;
	}
}
