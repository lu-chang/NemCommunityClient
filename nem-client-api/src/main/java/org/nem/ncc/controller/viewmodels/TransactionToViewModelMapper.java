package org.nem.ncc.controller.viewmodels;

import org.nem.core.model.*;
import org.nem.core.model.ncc.*;
import org.nem.core.model.primitive.*;

/**
 * TODO 20150131 J-G: fix empty comments
 * TODO 20150131 J-G: some basic tests
 * TODO 20150131 J-G: ok for now, but would be nicer to do mapping like nis
 */
public class TransactionToViewModelMapper {
	public static TransactionViewModel map(final Transaction transaction, final Address address) {
		return map(new TransactionMetaDataPair(transaction, null), address, null);
	}

	public static TransactionViewModel map(final Transaction transaction, final AccountMetaDataPair accountData) {
		return map(new TransactionMetaDataPair(transaction, null), accountData, null);
	}

	public static TransactionViewModel map(final TransactionMetaDataPair metaDataPair, final Address address, final BlockHeight blockHeight) {
		return map(metaDataPair, new AccountMetaDataPair(new AccountInfo(address, Amount.ZERO, Amount.ZERO, BlockAmount.ZERO, "", 0.0), null), blockHeight);
	}

	static TransactionViewModel map(final TransactionMetaDataPair metaDataPair, final AccountMetaDataPair accountData, final BlockHeight blockHeight) {
		switch (metaDataPair.getEntity().getType()) {
			case TransactionTypes.TRANSFER:
				return new TransferTransactionViewModel(metaDataPair, accountData.getEntity().getAddress(), blockHeight);
			case TransactionTypes.IMPORTANCE_TRANSFER:
				return new ImportanceTransferTransactionViewModel(metaDataPair, blockHeight);
			case TransactionTypes.MULTISIG:
				return new MultisigTransactionViewModel(metaDataPair, accountData, blockHeight);
			case TransactionTypes.MULTISIG_AGGREGATE_MODIFICATION:
				return new MultisigAggregateViewModel(metaDataPair, blockHeight);
			case TransactionTypes.PROVISION_NAMESPACE:
				return new ProvisionNamespaceTransactionViewModel(metaDataPair, blockHeight);
			case TransactionTypes.MOSAIC_DEFINITION_CREATION:
				return new MosaicCreationTransactionViewModel(metaDataPair, blockHeight);
			case TransactionTypes.MOSAIC_SUPPLY_CHANGE:
				return new MosaicSupplyTransactionViewModel(metaDataPair, blockHeight);
			default:
				throw new IllegalArgumentException("transaction type is not handled inside TransactionToViewModelMapper");
		}
	}
}
