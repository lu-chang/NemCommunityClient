"use strict";

define(['NccModal', 'Utils',
    'rv!layout/partialTransferTransaction',
    'rv!layout/partialImportanceTransaction',
    'rv!layout/partialAggregateModification',
    'rv!layout/partialProvisionNamespace',
    'rv!layout/partialMosaicCreation',
    'rv!layout/partialMosaicSupply'],
    function(NccModal, Utils,
        partialTransferTransaction,
        partialImportanceTransaction,
        partialAggregateModification,
        partialProvisionNamespace,
        partialMosaicCreation,
        partialMosaicSupply)
{
	return NccModal.extend({
	    partials: {
            transferTransactionPartial: partialTransferTransaction,
            importanceTransactionPartial: partialImportanceTransaction,
            aggregateModificationPartial: partialAggregateModification,
            provisionNamespacePartial: partialProvisionNamespace,
            mosaicCreationPartial: partialMosaicCreation,
            mosaicSupplyPartial: partialMosaicSupply
        },
	    onrender: function() {
            this.set('privateLabels', ncc.get('privateLabels'));

            this.viewAccount = ncc.viewAccount;
		}
	});
});