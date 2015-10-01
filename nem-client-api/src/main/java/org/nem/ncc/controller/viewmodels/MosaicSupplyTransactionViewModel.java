package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.*;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.*;
import org.nem.core.serialization.Serializer;

/**
 * A mosaic supply transaction view model.
 */
public class MosaicSupplyTransactionViewModel extends TransactionViewModel {
	private final String namespaceName;
	private final String mosaicName;
	private final MosaicSupplyType supplyType;
	private final Supply supplyDelta;

	/**
	 * Creates a new mosaic supply transaction view model.
	 *
	 * @param metaDataPair The metadata pair.
	 * @param lastBlockHeight The last block height.
	 */
	public MosaicSupplyTransactionViewModel(final TransactionMetaDataPair metaDataPair, final BlockHeight lastBlockHeight) {
		super(Type.Mosaic_Supply, metaDataPair, lastBlockHeight);

		final MosaicSupplyChangeTransaction transaction = (MosaicSupplyChangeTransaction)metaDataPair.getEntity();
		this.namespaceName = transaction.getMosaicId().getNamespaceId().toString();
		this.mosaicName = transaction.getMosaicId().getName();
		this.supplyType = transaction.getSupplyType();
		this.supplyDelta = transaction.getDelta();
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		serializer.writeString("mosaicName", this.mosaicName);
		serializer.writeString("namespaceName", this.namespaceName);
		serializer.writeString("supplyType", this.supplyType.toString());
		Supply.writeTo(serializer, "supplyQuantity", this.supplyDelta);
	}

	/**
	 * Gets namespace in which mosaic resides.
	 *
	 * @return The namespace name.
	 */
	public String getNamespaceName() {
		return this.namespaceName;
	}

	/**
	 * Gets the mosaic expressed as a string.
	 *
	 * @return The mosaic name.
	 */
	public String getMosaicName() {
		return this.mosaicName;
	}

	/**
	 * Gets the mosaic supply type.
	 *
	 * @return The mosaic supply type.
	 */
	public MosaicSupplyType getSupplyType() {
		return this.supplyType;
	}

	/**
	 * Gets the mosaic supply quantity.
	 *
	 * @return The mosaic supply quantity.
	 */
	public Supply getSupplyDelta() {
		return this.supplyDelta;
	}
}
