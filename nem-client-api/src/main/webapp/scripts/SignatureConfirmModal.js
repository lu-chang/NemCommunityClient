"use strict";

define(['NccModal', 'Utils'], function(NccModal, Utils) {
	return NccModal.extend({
	    computed: {
	        privateLabels: function() {
                return ncc.get('privateLabels');
            }
	    },
		confirm: function() {
            this.lockAction();

            var self = this;
            var success = false;
            ncc.postRequest('wallet/account/signature/send', this.get('requestData'), function(data) {
                success = true;
                self.closeModal();
            	ncc.getModal('signMultisig').closeModal();

                ncc.showMessage(
                    ncc.get('texts.common.success'),
                    ncc.fill(ncc.get('texts.modals.sendNem.successMessage'), data['transactionHash']['data']),
                    function(){},
                    'modal--wide'
                );
                ncc.refreshInfo();
            },
            {
                complete: function() {
                    if (! success) {
                        self.closeModal();
                        ncc.getModal('signMultisig').closeModal();

                        ncc.showMessage(ncc.get('texts.common.unknown'), ncc.get('texts.common.unknownMessage'));
                    }

                    self.unlockAction();
                }
            });
		}
	});
});