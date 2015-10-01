package org.nem.ncc.controller.viewmodels;

import org.nem.core.crypto.Hash;
import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.BlockHeight;
import org.nem.core.serialization.Serializer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO 20150131 J-G: fix empty comments
 * TODO 20150131 J-G: some basic tests
 */
public class MultisigTransactionViewModel extends TransactionViewModel {
	private final TransactionViewModel otherTransactionViewModel;
	private final List<MultisigSignatureViewModel> signatureViewModel;
	private final int requiresSignature;
	private final Hash innerTransactionHash;
	private final Address issuer;

	private static Type innerTypeToViewModelType(final int innerType) {
		switch (innerType) {
			case TransactionTypes.MULTISIG_AGGREGATE_MODIFICATION:
				return Type.Multisig_Aggregate_Modification;
			case TransactionTypes.IMPORTANCE_TRANSFER:
				return Type.Multisig_Importance_Transfer;
			case TransactionTypes.TRANSFER:
				return Type.Multisig_Transfer;
			case TransactionTypes.PROVISION_NAMESPACE:
				return Type.Multisig_Provision_Namespace;
			case TransactionTypes.MOSAIC_DEFINITION_CREATION:
				return Type.Multisig_Mosaic_Creation;
			case TransactionTypes.MOSAIC_SUPPLY_CHANGE:
				return Type.Multisig_Mosaic_Supply;
			default:
				return Type.Unknown;
		}
	}

	public MultisigTransactionViewModel(final TransactionMetaDataPair metaDataPair, final AccountMetaDataPair relativeAccountData, final BlockHeight lastBlockHeight) {

		super(innerTypeToViewModelType(((MultisigTransaction)metaDataPair.getEntity()).getOtherTransaction().getType()),
				metaDataPair,
				lastBlockHeight);
		final MultisigTransaction multisigTransaction = (MultisigTransaction)metaDataPair.getEntity();
		final Transaction other = multisigTransaction.getOtherTransaction();
		final Address relativeAccountAddress = relativeAccountData.getEntity().getAddress();
		final TransactionMetaData innerMetaData = metaDataPair.getMetaData() == null
				? null
				: new TransactionMetaData(metaDataPair.getMetaData().getHeight(), 0L, Hash.ZERO);

		this.issuer = multisigTransaction.getSigner().getAddress();

		final TransactionMetaDataPair innerMetaDataPair = new TransactionMetaDataPair(other, innerMetaData);
		this.otherTransactionViewModel = TransactionToViewModelMapper.map(innerMetaDataPair, relativeAccountAddress, lastBlockHeight);

		this.innerTransactionHash = multisigTransaction.getOtherTransactionHash();
		this.signatureViewModel = multisigTransaction.getCosignerSignatures().stream()
				.map(t -> new MultisigSignatureViewModel(new TransactionMetaDataPair(t, innerMetaData), lastBlockHeight))
				.collect(Collectors.toList());

		this.requiresSignature = this.requiresSignature(metaDataPair, relativeAccountAddress, relativeAccountData);
	}

	private int requiresSignature(final TransactionMetaDataPair metaDataPair, final Address relativeAccountAddress, final AccountMetaDataPair relativeAccoundData) {
		final MultisigTransaction multisigTransaction = (MultisigTransaction)metaDataPair.getEntity();
		if (metaDataPair.getMetaData() != null) {
			return 0;
		}

		if (relativeAccoundData.getMetaData() == null) {
			return 0;
		}

		// multisig account is on the list of accounts that relativeAccountAddress is eligible to sign
		final Address multisigAddress = multisigTransaction.getOtherTransaction().getSigner().getAddress();
		if (relativeAccoundData.getMetaData().getCosignatoryOf().stream()
				.map(AccountInfo::getAddress)
				.anyMatch(t -> t.equals(multisigAddress))) {
			// TODO 20150131 J-G: does it make sense to add a hasSignature(Account) to MultisigTransaction?
			return multisigTransaction.getSigner().getAddress().equals(relativeAccountAddress)
					|| multisigTransaction.getSigners().stream()
					.anyMatch(t -> t.getAddress().equals(relativeAccountAddress))
					? 0 : 1;
		}

		return 0;
	}

	@Override
	protected void serializeImpl(final Serializer serializer) {
		super.serializeImpl(serializer);

		Address.writeTo(serializer, "issuer", this.issuer);
		serializer.writeObject("inner", this.otherTransactionViewModel);
		serializer.writeObject("innerHash", this.innerTransactionHash);
		serializer.writeObjectArray("signatures", this.signatureViewModel);
		serializer.writeInt("requiresSignature", this.requiresSignature);
	}
}
