"use strict";

define(['NccModal', 'Utils', 'TransactionType', 'handlebars'], function(NccModal, Utils, TransactionType, Handlebars) {
	return NccModal.extend({
        data: {
            isFeeAutofilled: true,
            dueBy: 1
        },
        computed: {
            hoursDue: function() {
                return this.get('dueBy') | 0;
            },
            fee: {
                get: function() {
                    return Utils.format.nem.getNemValue(Utils.format.nem.stringToNem(this.get('formattedFee')));
                },
                set: function(fee) {
                    this.set('formattedFee', Utils.format.nem.formatNemAmount(fee));
                    this.update('fee'); // so that stupid Ractive trigger fee observers
                }
            },
            multisigFee: {
                get: function() {
                    return Utils.format.nem.getNemValue(Utils.format.nem.stringToNem(this.get('formattedMultisigFee')));
                },
                set: function(fee) {
                    this.set('formattedMultisigFee', Utils.format.nem.formatNemAmount(fee));
                    this.update('multisigFee'); // so that stupid Ractive trigger fee observers
                }
            },
            remoteError: function() {
                if (this.get('activation')) {
                    var remotePublicKey = this.get('remote.publicKey');
                    return !remotePublicKey || ((remotePublicKey.length % 2) !== 0);
                } else {
                    return false;
                }
            },
            feeValid: function() {
                return this.get('fee') >= this.get('minimumFee');
            },
            feeError: function() {
                return !this.get('feeValid') && this.get('feeChanged');
            },
            formattedMinimumFee: function() {
                return Utils.format.nem.formatNemAmount(this.get('minimumFee'));
            },
            passwordValid: function() {
                return !!this.get('password');
            },
            passwordError: function() {
                return !this.get('passwordValid') && this.get('passwordChanged');
            },
            formValid: function() {
                return !this.get('remoteError') && this.get('feeValid') && this.get('passwordValid');
            }
        },
        resetRemote: function() {
            var wallet = ncc.get('wallet');
            if (this.get('sender') === ncc.get('activeAccount.address')) {
                var activeAddress = ncc.get('activeAccount').address;
                var walletAccount = null;

                if (wallet.primaryAccount.address === activeAddress) {
                    walletAccount = wallet.primaryAccount;
                } else {
                    for (var i = 0; i < wallet.otherAccounts.length; ++i) {
                        if (wallet.otherAccounts[i].address == activeAddress) {
                            walletAccount = wallet.otherAccounts[i];
                            break;
                        }
                    }
                }
                this.set('remote', {
                    publicKey: walletAccount.remotePublicKey,
                    address: walletAccount.remoteAddress,
                    formattedAddress: walletAccount.formattedRemoteAddress,
                });
            } else {
                this.set('remote', {
                    publicKey: '',
                    address: '',
                    formattedAddress: '',
                });
            }
        },
        resetDefaultData: function() {
            var activation = this.get('activation');
            var firstAcc = this.get('availAccounts') ? this.get('availAccounts')[0].address : null;
            this.set('sender', firstAcc);
            this.resetRemote();

            this.set('fee', 0);
            this.set('multisigFee', 0);
            this.set('minimumFee', 0);
            this.set('dueBy', '1');
            this.set('password', '');
            this.set('useMinimumFee', true);
            this.set('signatories', [{}]);

            this.set('privateLabels', ncc.get('privateLabels'));
            this.set('multisigAccounts', ncc.get('activeAccount').multisigAccounts);
            this.set('fullBalance', Utils.format.nem.formatNemAmount(ncc.get('activeAccount').balance, {keepTrailingZeroes:true}));
            this.set('feeChanged', false);
            this.set('passwordChanged', false);
            this.resetFee({ silent: true });
        },
        /**
         * @param {boolean} options.silent
         */
        resetFee: function(options) {
            var requestData = {
                wallet: ncc.get('wallet.wallet'),
                account: ncc.get('activeAccount.address'),
                hoursDue: this.get('hoursDue')
            };
            var self = this;

            ncc.postRequest('wallet/account/remote/validate', requestData,
                function(data) {
                    self.set('minimumFee', data.fee);
                    self.set('multisigFee', data.multisigFee);
                },
                {},
                options.silent
            );
        },
        sendTransaction: function() {
            if (this.get('sender') === ncc.get('activeAccount.address')) {
                var requestData = {
                    wallet: ncc.get('wallet.wallet'),
                    type: TransactionType.Importance_Transfer,
                    account: ncc.get('activeAccount.address'),
                    password: this.get('password'),
                    publicKey: {'value': this.get('remote.publicKey')},
                    fee: this.get('fee'),
                    multisigFee: 0,
                    hoursDue: this.get('hoursDue')
                };
            } else {
                var requestData = {
                    wallet: ncc.get('wallet.wallet'),
                    type: TransactionType.Multisig_Importance_Transfer,
                    account: ncc.get('activeAccount.address'),
                    password: this.get('password'),
                    multisigAddress: this.get('sender'),
                    publicKey: {'value': this.get('remote.publicKey')},
                    fee: this.get('fee'),
                    multisigFee: this.get('multisigFee'),
                    hoursDue: this.get('hoursDue')
                };
            }

            var txConfirm = ncc.getModal('genericDelegatedConfirm');
            txConfirm.set('TransactionType', TransactionType);
            txConfirm.set('txData', this.get());
            txConfirm.set('requestData', requestData);
            txConfirm.open();
        },
        onrender: function() {
            this._super();
            var self = this;

            this.resetDefaultData();

            this.observe({
                sender: function(senderAddress) {
                    self.resetRemote();
                }
            });
            this.observe({
                useMinimumFee: function(useMinimumFee) {
                    if (useMinimumFee) {
                        this.set('fee', this.get('minimumFee'));
                    }
                },
                minimumFee: function(minimumFee) {
                    if (this.get('useMinimumFee')) {
                        this.set('fee', minimumFee);
                    }
                }
            });

            this.observe({
                fee: function() {
                    this.set('feeChanged', true);
                },
                password: function() {
                    this.set('passwordChanged', true);
                }
            },
            {
                init: false
            });

            this.on({
                sendFormKeypress: function(e) {
                    if (e.original.keyCode === 13 && this.get('formValid')) {
                        this.sendTransaction();
                    }
                },
                modalOpened: function() {
                    //$(":password")[0].focus();
                    this.resetDefaultData();
                },
                modalClosed: function() {
                    this.resetDefaultData();
                }
            });

            // Because this modal is reused by both activate and deactivate
            // we must take care not to register handlers twice
            // (by using event namespaces, and trying to turn them off, before adding)
            var $aDueBy = $('.js-activateDelegated-dueBy-textbox');
            $aDueBy.off('keypress.activateDelegated');
            $aDueBy.on('keypress.activateDelegated', function(e) { Utils.mask.keypress(e, 'number', self) });

            var $aFee = $('.js-activateDelegated-fee-textbox');
            $aFee.off('keypress.activateDelegated');
            $aFee.off('paste.activateDelegated');
            $aFee.off('keydown.activateDelegated');
            $aFee.on('keypress.activateDelegated', function(e) { Utils.mask.keypress(e, 'nem', self); });
            $aFee.on('paste.activateDelegated', function(e) { Utils.mask.paste(e, 'nem', self); });
            $aFee.on('keydown.activateDelegated', function(e) { Utils.mask.keydown(e, 'nem', self); });

            var $aPublicKey = $('.js-activateDelegated-remote-textbox');
            $aPublicKey.off('keypress.activateDelegated');
            $aPublicKey.off('paste.activateDelegated');
            $aPublicKey.off('keydown.activateDelegated');
            $aPublicKey.on('keypress.activateDelegated', function(e) { Utils.mask.keypress(e, 'privateKey', self); });
            $aPublicKey.on('paste.activateDelegated', function(e) { Utils.mask.paste(e, 'privateKey', self); });
            $aPublicKey.on('keydown.activateDelegated', function(e) { Utils.mask.keydown(e, 'privateKey', self); });


            var $bDueBy = $('.js-deactivateDelegated-dueBy-textbox');
            $bDueBy.off('keypress.deactivateDelegated');
            $bDueBy.on('keypress.deactivateDelegated', function(e) { Utils.mask.keypress(e, 'number', self) });

            var $bFee = $('.js-deactivateDelegated-fee-textbox');
            $bFee.off('keypress.deactivateDelegated');
            $bFee.off('paste.deactivateDelegated');
            $bFee.off('keydown.deactivateDelegated');
            $bFee.on('keypress.deactivateDelegated', function(e) { Utils.mask.keypress(e, 'nem', self); });
            $bFee.on('paste.deactivateDelegated', function(e) { Utils.mask.paste(e, 'nem', self); });
            $bFee.on('keydown.deactivateDelegated', function(e) { Utils.mask.keydown(e, 'nem', self); });

            var $bPublicKey = $('.js-deactivateDelegated-remote-textbox');
            $bPublicKey.off('keypress.deactivateDelegated');
            $bPublicKey.off('paste.deactivateDelegated');
            $bPublicKey.off('keydown.deactivateDelegated');
            $bPublicKey.on('keypress.deactivateDelegated', function(e) { Utils.mask.keypress(e, 'privateKey', self); });
            $bPublicKey.on('paste.deactivateDelegated', function(e) { Utils.mask.paste(e, 'privateKey', self); });
            $bPublicKey.on('keydown.deactivateDelegated', function(e) { Utils.mask.keydown(e, 'privateKey', self); });
        }
    });
});