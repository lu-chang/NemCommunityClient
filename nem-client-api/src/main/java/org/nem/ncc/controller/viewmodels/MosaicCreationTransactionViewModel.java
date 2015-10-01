package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.MosaicDefinitionCreationTransaction;
import org.nem.core.model.mosaic.MosaicProperties;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.Serializer;

/**
 * A mosaic creation transaction view model.
 */
public class MosaicCreationTransactionViewModel extends TransactionViewModel {
	private final String description;
	private final String namespaceName;
	private final String mosaicName;
	private final MosaicProperties mosaicProperties;

	/**
	 * Creates a new mosaic creation transaction view model.
	 *
	 * @param metaDataPair The metadata pair.
	 * @param lastBlockHeight The last block height.
	 */
	public MosaicCreationTransactionViewModel(final TransactionMetaDataPair metaDataPair, final BlockHeight lastBlockHeight) {
		super(Type.Mosaic_Creation, metaDataPair, lastBlockHeight);

		final MosaicDefinitionCreationTransaction transaction = (MosaicDefinitionCreationTransaction)metaDataPair.getEntity();
		this.description = transaction.getMosaicDefinition().getDescriptor().toString();
		this.namespaceName = transaction.getMosaicDefinition().getId().getNamespaceId().toString();
		this.mosaicName = transaction.getMosaicDefinition().getId().getName();
		this.mosaicProperties = transaction.getMosaicDefinition().getProperties();
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		serializer.writeString("description", this.description);
		serializer.writeString("mosaicName", this.mosaicName);
		serializer.writeString("namespaceName", this.namespaceName);
		serializer.writeObjectArray("properties", this.mosaicProperties.asCollection());
	}

	/**
	 * Gets the description.
	 *
	 * @return The description.
	 */
	public String getDescription() {
		return this.description;
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
	 * Gets the mosaic properties.
	 *
	 * @return The mosaic properties.
	 */
	public MosaicProperties getMosaicProperties() {
		return this.mosaicProperties;
	}
}
