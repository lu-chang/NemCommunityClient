"use strict";

define(function() {
    var TransactionType = {
        Transfer: 20
        , Importance_Transfer: 21
        , Aggregate_Modification: 22
        , Provision_Namespace: 23
        , Mosaic_Creation: 24
        , Mosaic_Supply: 25

        , Multisig_Signature: 40

        , Multisig_Transfer: 50
        , Multisig_Importance_Transfer: 51
        , Multisig_Aggregate_Modification: 52
        , Multisig_Provision_Namespace: 53
        , Multisig_Mosaic_Creation: 54
        , Multisig_Mosaic_Supply: 55
    };

    return TransactionType;
});
