package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.*;
import org.nem.core.model.ncc.TransactionMetaDataPair;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO 20150131 J-G: some basic tests
 */
public class MultisigAggregateViewModel extends TransactionViewModel {
	private class ModificationWrapper implements SerializableEntity {
		private final MultisigModificationType modificationType;
		private final Address address;

		/**
		 * Creates a new modification wrapper around a cosignatory modification.
		 *
		 * @param multisigCosignatoryModification The cosignatory modification.
		 */
		public ModificationWrapper(final MultisigCosignatoryModification multisigCosignatoryModification) {
			this.modificationType = multisigCosignatoryModification.getModificationType();
			this.address = multisigCosignatoryModification.getCosignatory().getAddress();
		}

		@Override
		public void serialize(final Serializer serializer) {
			Address.writeTo(serializer, "address", this.address);
			serializer.writeString("type", this.modificationType == MultisigModificationType.AddCosignatory ? "add" : "del");
		}
	}

	private final List<ModificationWrapper> cosignatoryModifications;
	private final MultisigMinCosignatoriesModification minCosignatoriesModification;

	/**
	 * Creates a new multisig aggregate view model around a multisig aggregate modification transaction.
	 *
	 * @param metaDataPair The meta data pair.
	 * @param blockHeight The last block height (for calculating confirmations).
	 */
	public MultisigAggregateViewModel(final TransactionMetaDataPair metaDataPair, final BlockHeight blockHeight) {
		super(Type.Aggregate_Modification, metaDataPair, blockHeight);

		final MultisigAggregateModificationTransaction transaction = (MultisigAggregateModificationTransaction)metaDataPair.getEntity();
		this.cosignatoryModifications = transaction.getCosignatoryModifications().stream()
				.map(ModificationWrapper::new)
				.collect(Collectors.toList());
		this.minCosignatoriesModification = transaction.getMinCosignatoriesModification();
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		serializer.writeObjectArray("modifications", this.cosignatoryModifications);
		serializer.writeObject("minCosignatories", this.minCosignatoriesModification);
	}
}
