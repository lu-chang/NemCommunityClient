"use strict";

define(['jquery', 'ncc', 'NccLayout', 'Utils', 'TransactionType', 'filesaver'], function($, ncc, NccLayout, Utils, TransactionType, FileSaver) {
	return $.extend(true, {}, NccLayout, {
        name: 'wallet',
        template: 'rv!layout/wallet',
        initOnce: function() {
            ncc.refreshAddressBook = function(addressBook, silent) {
                if (!addressBook) addressBook = ncc.get('wallet.wallet');
                
                ncc.postRequest('addressbook/info', { addressBook: addressBook }, function(data) {
                    ncc.set('addressBook', data.accountLabels);
                    ncc.set('contacts', Utils.processContacts(data.accountLabels));
                }, null, silent);
            }

            ncc.refreshWallet = function(wallet, silent) {
                if (!wallet) wallet = ncc.get('wallet.wallet');

                ncc.postRequest('wallet/info', { wallet: wallet }, function(data) {
                    Utils.processWallet(data);
                }, null, silent);

                ncc.refreshAddressBook(wallet, silent);
            };

            ncc.refreshRemoteHarvestingStatus = function(wallet, account, silent) {
                if (!wallet) wallet = ncc.get('wallet.wallet');
                if (!account) account = ncc.get('activeAccount.address');

                ncc.postRequest('wallet/account/remote/status', 
                    { 
                        wallet: wallet,
                        account: account
                    },
                    function(data) {
                        if (data.status === 'UNLOCKED') {
                            ncc.set('walletPage.remoteUnlocked', true);
                        } else {
                            ncc.set('walletPage.remoteUnlocked', false);
                        }
                    },
                    null,
                    silent
                );
            };

            ncc.refreshInfo = function(wallet, account, silent) {
                ncc.refreshWallet(wallet, silent);
                ncc.refreshAccount(wallet, account, silent);
            };

            ncc.showTempMessage = function(message, duration) {
                if (!duration) {
                    duration = 2000;
                }
                this.set('tempMessage.message', message);
                this.set('tempMessage.visible', true);
                var self = this;
                window.setTimeout(function() {
                    self.set('tempMessage.visible', false);
                }, duration);
            };

            ncc.isLocalNode = function(hostName) {
                return hostName === "localhost" || hostName === "127.0.0.1" || hostName === "::1";
            };

            ncc.showBootModal = function(message) {
                var hostName = ncc.get('settings.remoteServer.host');
                if (!ncc.isLocalNode(hostName)) {
                    var warningText = "<span class='sublabelWarning'>" +
                        ncc.fill(
                          ncc.get('texts.modals.bootLocalNode.warningText'),
                          ncc.get('activeAccount.formattedBalance'),
                          hostName)
                        + "</span>";

                    ncc.showMessage(
                        ncc.get('texts.modals.bootLocalNode.warning'),
                        warningText
                    );
                    return;
                }

                var account = ncc.get('activeAccount.address');
                var accountLabel = ncc.get('privateLabels')[account];
                var wallet = ncc.get('wallet.wallet');
                var accountBalance = ncc.get('activeAccount.balance');

                var fields = [
                    {
                        name: 'account',
                        type: 'text',
                        readonly: true,
                        unimportant: true,
                        label: {
                            content: ncc.get('texts.modals.bootLocalNode.account')
                        },
                        sublabel: accountLabel ?
                        {
                            content: accountLabel
                        } :
                        {
                            content: ncc.get('texts.modals.bootLocalNode.noLabel'),
                            nullContent: true
                        }
                    },
                    {
                        name: 'wallet',
                        type: 'text',
                        readonly: true,
                        unimportant: true,
                        label: {
                            content: ncc.get('texts.modals.bootLocalNode.wallet')
                        }
                    },
                    {
                        name: 'nodeName',
                        type: 'text',
                        label: {
                            content: ncc.get('texts.modals.bootLocalNode.node')
                        },
                        isValid: function() {
                            return Utils.valid.notEmpty(this.get("values")['nodeName']);
                        }
                    }
                ];

                ncc.showInputForm(ncc.get('texts.modals.bootLocalNode.title'), message,
                    fields,
                    {
                        account: Utils.format.address.format(account),
                        wallet: wallet,
                        nodeName: wallet + "'s node"
                    },
                    function(values, closeModal) {
                        var self = this;
                        values.account = account;

                        this.lockAction();
                        ncc.set('status.booting', true);
                        ncc.postRequest('node/boot', values, 
                            function(data) {
                                closeModal();
                                // NIS info will be automatically retrieved when NIS status becomes BOOTED
                                // so no need to manually call refreshNisInfo()
                                ncc.refreshAppStatus();
                            },
                            {
                                altFailCb: function(faultId) {
                                    if (601 === faultId) {
                                        ncc.refreshAppStatus();
                                        closeModal();
                                    }
                                },
                                complete: function() {
                                    self.unlockAction();
                                    ncc.set('status.booting', false);
                                }
                            }
                        );
                    }, 
                    ncc.get('texts.modals.bootLocalNode.boot'), 
                    ncc.get('texts.modals.bootLocalNode.booting')
                );
            };

            ncc.openActivateDelegated = function() {
                var account = ncc.get('activeAccount');
                if (ncc.get('nodeBooted')) {
                    if (account.isMultisig) {
                        ncc.showMessage(ncc.get('texts.common.warning'), ncc.get('texts.dashboard.importance.remoteHarvest.multisigInfo'));
                    } else {
                        var modifiableAccounts = {};
                        account.multisigAccounts.forEach(function (e){ modifiableAccounts[e.address] = true; });
                        modifiableAccounts[account.address] = true;

                        var walletAccounts = ncc.get('allAccounts');
                        var accountsToActivate = walletAccounts.filter(function (a){ return (a.address in modifiableAccounts) && (a.remoteStatus === 'INACTIVE');});
                        var accountsToDeactivate = walletAccounts.filter(function (a){ return (a.address in modifiableAccounts) && (a.remoteStatus === 'ACTIVE');});

                        var showDeactivateDialog = function() {
                            var m = ncc.getModal('deactivateDelegated');
                            m.set('activation', false);
                            m.set('availAccounts', accountsToDeactivate);
                            m.open();
                        };
                        var showActivateDialog = function() {
                            var m = ncc.getModal('activateDelegated');
                            m.set('activation', true);
                            m.set('availAccounts', accountsToActivate);
                            m.open();
                        };

                        if (accountsToActivate.length != 0 && accountsToDeactivate.length != 0) {
                            this.showConfirmation(ncc.get('texts.dashboard.importance.remoteHarvest.title'), '',
                                {
                                    activate: function() {
                                        showActivateDialog();
                                    },
                                    deactivate: function() {
                                        showDeactivateDialog();
                                    }
                                },
                                [
                                    {
                                        action: 'activate',
                                        label: ncc.get('texts.dashboard.importance.remoteHarvest.activate') + " (" + accountsToActivate.length +")",
                                        actionType: 'primary'
                                    },
                                    {
                                        action: 'deactivate',
                                        label: ncc.get('texts.dashboard.importance.remoteHarvest.deactivate') +  " (" + accountsToDeactivate.length +")",
                                        actionType: 'primary'
                                    }
                                ],
                                'modal--wide'
                            );
                        } else if (accountsToActivate.length != 0) {
                            showActivateDialog();

                        } else if (accountsToDeactivate.length != 0) {
                            showDeactivateDialog();
                        }
                    }
                } else if (ncc.get('loadingDb')) {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.loadingWarning.title'), ncc.get('texts.faults.602'));
                } else if (ncc.get('nodeBooting')) {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.bootingWarning.title'), ncc.get('texts.modals.sendNem.bootingWarning.message'));
                } else {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.notBootedWarning.title'), ncc.get('texts.modals.sendNem.notBootedWarning.message'), function() {
                        ncc.showBootModal();
                    });
                }
            },

            ncc.openSendNem = function(recipient) {
                if (ncc.get('nodeBooted')) {
                    var m = ncc.getModal('sendNem');
                    m.open();
                    if (recipient) {
                        m.set('formattedRecipient', Utils.format.address.format(recipient));
                    }
                } else if (ncc.get('loadingDb')) {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.loadingWarning.title'), ncc.get('texts.faults.602'));

                } else if (ncc.get('nodeBooting')) {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.bootingWarning.title'), ncc.get('texts.modals.sendNem.bootingWarning.message'));
                } else {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.notBootedWarning.title'), ncc.get('texts.modals.sendNem.notBootedWarning.message'), function() {
                        ncc.showBootModal();
                    });
                }
            },

            ncc.openConvertMultisig = function() {
                var m = ncc.getModal('convertMultisig');
                var Fork_1_Height = (ncc.get('activeAccount.address')[0] === 'T') ? 90000 : 199800;
                m.set('isAfterMofNFork', ncc.get('blockchainHeight') > Fork_1_Height);
                m.open();
            }

            ncc.openSignMultisig = function(transaction) {
                if (ncc.get('nodeBooted')) {
                    var m = ncc.getModal('signMultisig');
                    m.open();
                    m.set('txData', transaction);

                } else if (ncc.get('nodeBooting')) {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.bootingWarning.title'), ncc.get('texts.modals.sendNem.bootingWarning.message'));
                } else {
                    ncc.showMessage(ncc.get('texts.modals.sendNem.notBootedWarning.title'), ncc.get('texts.modals.sendNem.notBootedWarning.message'), function() {
                        ncc.showBootModal();
                    });
                }
            },

            ncc.viewTransaction = function(transaction) {
                var m = ncc.getModal('transactionDetails');
                m.set('TransactionType', TransactionType);
                m.set('transaction', transaction);
                m.set('privateLabels', ncc.get('privateLabels'));
                m.open();
            };

            ncc.viewAccount = function(address) {
                ncc.postRequest(
                    'account/find',
                    {account: Utils.format.address.restore(address)},
                    function(data) {
                        var walletAccount = ncc.get('allAccounts').filter(function (a){ return (a.address == address);});
                        var m = ncc.getModal('accountDetails');
                        m.set('account', data);
                        if (walletAccount.length > 0) {
                            m.set('account.wallet', walletAccount[0]);
                        }
                        m.open();
                    });
            };

            ncc.addContact = function(address) {
                ncc.showInputForm(ncc.get('texts.modals.addContact.title'), '',
                    [
                        {
                            name: 'addressBook',
                            type: 'text',
                            readonly: true,
                            unimportant: true,
                            label: {
                                content: ncc.get('texts.common.addressBook')
                            }
                        },
                        {
                            name: 'address',
                            type: 'text',
                            label: {
                                content: ncc.get('texts.common.address')
                            },
                            isValid: function() {
                                return Utils.valid.address(this.get("values")['address']);
                            }
                        },
                        {
                            name: 'privateLabel',
                            type: 'text',
                            label: {
                                content: ncc.get('texts.common.privateLabel')
                            },
                            isValid: function() {
                                return Utils.valid.notEmpty(this.get("values")['privateLabel']);
                            }
                        },
                        {
                            name: 'password',
                            type: 'password',
                            label: {
                                content: ncc.get('texts.common.password')
                            },
                            isValid: function() {
                                return Utils.valid.notEmpty(this.get("values")['password']);
                            }
                        },
                    ],
                    {
                        addressBook: ncc.get('wallet.wallet'),
                        address: Utils.format.address.format(address)
                    },
                    function(values, closeModal) {
                        values.address = Utils.format.address.restore(values.address);
                        ncc.postRequest('addressbook/accountlabel/add', values, function(data) {
                            closeModal();
                            ncc.refreshAddressBook();
                        });
                    },
                    ncc.get('texts.modals.addContact.add')
                );
            };

            ncc.editContact = function(address, privateLabel) {
                ncc.showInputForm(ncc.get('texts.modals.editContact.title'), '',
                    [
                        {
                            name: 'addressBook',
                            type: 'text',
                            readonly: true,
                            unimportant: true,
                            label: {
                                content: ncc.get('texts.common.addressBook')
                            }
                        },
                        {
                            name: 'address',
                            readonly: true,
                            unimportant: true,
                            type: 'text',
                            label: {
                                content: ncc.get('texts.common.address')
                            },
                            isValid: function() {
                                return Utils.valid.address(this.get("values")['address']);
                            }
                        },
                        {
                            name: 'privateLabel',
                            type: 'text',
                            label: {
                                content: ncc.get('texts.common.privateLabel')
                            },
                            isValid: function() {
                                return Utils.valid.notEmpty(this.get("values")['privateLabel']);
                            }
                        },
                        {
                            name: 'password',
                            type: 'password',
                            label: {
                                content: ncc.get('texts.common.password')
                            },
                            isValid: function() {
                                return Utils.valid.notEmpty(this.get("values")['password']);
                            }
                        },
                    ],
                    {
                        addressBook: ncc.get('wallet.wallet'),
                        address: Utils.format.address.format(address),
                        privateLabel: privateLabel
                    },
                    function(values, closeModal) {
                        values.address = Utils.format.address.restore(values.address);
                        ncc.postRequest('addressbook/accountlabel/change', values, function(data) {
                            closeModal();
                            ncc.refreshAddressBook();
                        });
                    },
                    ncc.get('texts.modals.editContact.saveChanges')
                );
            };

            ncc.removeContact = function(address) {
                ncc.showInputForm(ncc.get('texts.modals.removeContact.title'), '',
                    [
                        {
                            name: 'addressBook',
                            type: 'text',
                            readonly: true,
                            unimportant: true,
                            label: {
                                content: ncc.get('texts.common.addressBook')
                            }
                        },
                        {
                            name: 'address',
                            type: 'text',
                            readonly: true,
                            unimportant: true,
                            label: {
                                content: ncc.get('texts.common.address')
                            }
                        },
                        {
                            name: 'password',
                            type: 'password',
                            label: {
                                content: ncc.get('texts.common.password')
                            },
                            isValid: function() {
                                return Utils.valid.notEmpty(this.get("values")['password']);
                            }
                        },
                    ],
                    {
                        addressBook: ncc.get('wallet.wallet'),
                        address: Utils.format.address.format(address)
                    },
                    function(values, closeModal) {
                        values.address = Utils.format.address.restore(values.address);
                        ncc.postRequest('addressbook/accountlabel/remove', values, function(data) {
                            closeModal();
                            ncc.refreshAddressBook();
                        });
                    },
                    ncc.get('texts.modals.removeContact.remove')
                );
            };

            ncc.on('registerScrollableSidebar', function(e) {
                var $sidebarNav = $(e.node);
                var navBottom = $sidebarNav.offset().top + $sidebarNav.outerHeight();
                var decideSidebarScrollability = function() {
                    if (navBottom > global.$window.height()) {
                        ncc.set('walletPage.sidebarScrollable', true);
                    } else {
                        ncc.set('walletPage.sidebarScrollable', false);
                    }
                };

                global.$window.on('resize.scrollableSidebar', decideSidebarScrollability);
                decideSidebarScrollability();
            });
        },
        paramsChanged: function(params) {
            var wallet = (params && params.wallet) || Utils.getUrlParam('wallet');
            if (!wallet) {
                ncc.loadPage('landing');
                return true;
            } else {
                if (!ncc.get('wallet') || ncc.get('wallet.wallet') != wallet) {
                    ncc.set('wallet.wallet', wallet);
                    ncc.refreshWallet(wallet);
                }
            }

            var account = (params && params.account) || Utils.getUrlParam('account');
            if (!account) {
                ncc.loadPage('landing');
                return true;
            } else {
                if (!ncc.get('activeAccount') || ncc.get('activeAccount.address') != account) {
                    ncc.set('activeAccount.formattedBalance', ncc.get('texts.dashboard.transactions.loading'));
                    ncc.set('activeAccount.transactions', {});
                    ncc.set('activeAccount.remoteStatus', null);
                    ncc.set('activeAccount.status', 'UNKNOWN');
                    ncc.set('activeAccount.importance', 0);
                    ncc.set('activeAccount.address', account);
                    ncc.set('harvestedBlocks.feeEarned', 0);
                    ncc.set('harvestedBlocks.formattedFeeEarned', ncc.get('texts.dashboard.transactions.loading'));
                    ncc.set('harvestedBlocks.feeEarnedInt', 0);
                    ncc.set('harvestedBlocks.feeEarnedDec', 0);
                    ncc.refreshAccount(wallet, account);
                }
            }
        },
        setupEverytime: function() {
            var local = this.local;
            var global = ncc.global;

            require(['zeroClipboard'], function(ZeroClipboard) {
                local.client = new ZeroClipboard($('#addressClipboard'));

                ncc.set('walletPage.copyDisabled', true);
                local.client.on('ready', function() {
                    ncc.set('walletPage.copyDisabled', false);
                    local.client.on('aftercopy', function() {
                        ncc.showTempMessage(ncc.get('texts.wallet.copiedToClipboard'));
                    });
                });

                local.client.on('error', function() {
                    local.client.destroy();
                    ncc.set('walletPage.copyDisabled', true);
                });
            });

            local.listeners.push(ncc.on({
                toggleSidebar: function() {
                    ncc.set('walletPage.miniSidebar', !ncc.get('walletPage.miniSidebar'));
                },
                showClientInfo: function() {
                    var modal = ncc.getModal('clientInfo');
                    modal.open();

                    modal.set('loadingNcc', true);
                    ncc.getRequest('info/ncc',
                        function(data) {
                            ncc.set('ncc', data);
                        },
                        {
                            complete: function() {
                                modal.set('loadingNcc', false);
                            }
                        }
                    );

                    modal.set('loadingNis', true);
                    ncc.set('nis', null);
                    ncc.getRequest('info/nis',
                        function(data) {
                            var blockchainHeight = ncc.get('blockchainHeight') || data.nodeMetaData.nodeBlockChainHeight;
                        	var lastBlockBehind = (data.nodeMetaData.maxBlockChainHeight - blockchainHeight) * 60;
                            ncc.set('nis', data);
                            ncc.set('nis.nodeMetaData.lastBlockBehind', lastBlockBehind < 0? 0 : lastBlockBehind);
                        },
                        {
                            complete: function() {
                                modal.set('loadingNis', false);
                            }
                        },
                        true // silent
                    );
                },
                closeWallet: function() {
                    ncc.showConfirmation(ncc.get('texts.modals.closeWallet.title'), ncc.get('texts.modals.closeWallet.message'), {
                        yes: function() {
                            var requestData = {
                                wallet: ncc.get('wallet.wallet') 
                            };
                            ncc.postRequest('wallet/close', requestData, function(data) {
                                if (data.ok) {
                                    ncc.loadPage('landing');
                                } else {
                                    ncc.showError();
                                }
                            });
                        }
                    });
                },
                switchAccount: function(e, newAccount) {
                    var currentAccount = ncc.get('activeAccount.address');
                    if (currentAccount !== newAccount) {
                        var layouts = ncc.get('layout');
                        var currentPage = layouts[layouts.length - 1].name;
                        var wallet = ncc.get('wallet.wallet');
                        ncc.loadPage(currentPage, { wallet: wallet, account: newAccount });
                    }
                },
                refreshInfo: function() {
                    this.refreshInfo();
                    this.refreshAppStatus();
                    this.refreshNisInfo();
                },
                createNewAccount: function() {
                    var wallet = ncc.get('wallet.wallet');
                    ncc.showInputForm(ncc.get('texts.modals.createAccount.title'), '',
                        [
                            {
                                name: 'label',
                                type: 'text',
                                label: {
                                    content: ncc.get('texts.modals.createAccount.label')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['label']);
                                }
                            },
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.createAccount.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            wallet: wallet
                        },
                        function(values, closeModal) {
                            ncc.postRequest('wallet/account/new', values, function(data) {
                                if (data.address) {
                                    var label = data.label;
                                    ncc.showMessage(ncc.get('texts.common.success'), ncc.fill(ncc.get('texts.modals.createAccount.successMessage'), Utils.format.address.format(data.address), label));

                                    var layout = ncc.get('layout');
                                    var wallet = ncc.get('wallet.wallet');
                                    ncc.loadPage(layout[layout.length - 1].name, 
                                        {
                                            wallet: wallet,
                                            account: data.address
                                        }
                                    );
                                    ncc.refreshWallet();

                                    closeModal();
                                } else {
                                    ncc.showError();
                                }
                            });
                        },
                        ncc.get('texts.modals.createAccount.create')
                    );
                },
                addAccount: function() {
                    var wallet = ncc.get('wallet.wallet');

                    ncc.showInputForm(ncc.get('texts.modals.addAccount.title'), '',
                        [
                            {
                                name: 'accountKey',
                                type: 'text',
                                label: {
                                    content: ncc.get('texts.modals.addAccount.privateKey')
                                },
                                isValid: function() {
                                    return Utils.valid.privateKey(this.get("values")['accountKey']);
                                }
                            },
                            {
                                name: 'label',
                                type: 'text',
                                label: {
                                    content: ncc.get('texts.modals.addAccount.label')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['label']);
                                }
                            },
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.addAccount.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            wallet: wallet
                        },
                        function(values, closeModal) {
                            ncc.postRequest('wallet/account/add', values, function(data) {
                                if (data.address) {
                                    var label = data.label;
                                    ncc.showMessage(ncc.get('texts.common.success'), ncc.fill(ncc.get('texts.modals.addAccount.successMessage'), Utils.format.address.format(data.address), label));

                                    var layout = ncc.get('layout');
                                    var wallet = ncc.get('wallet.wallet');
                                    ncc.loadPage(layout[layout.length - 1].name, 
                                        {
                                            wallet: wallet,
                                            account: data.address
                                        }
                                    );
                                    ncc.refreshWallet();
                                    
                                    closeModal();
                                } else {
                                    ncc.showError();
                                }
                            });
                        },
                        ncc.get('texts.modals.addAccount.add')
                    );
                },
                setCurrentAccountAsPrimary: function() {
                    var account = ncc.get('activeAccount.address');
                    var accountLabel = ncc.get('privateLabels')[account];
                    var wallet = ncc.get('wallet.wallet');
                    ncc.showInputForm(ncc.get('texts.modals.setPrimary.title'), '',
                        [
                            {
                                name: 'account',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.setPrimary.account')
                                },
                                sublabel: accountLabel?
                                    {
                                        content: accountLabel
                                    } :
                                    {
                                        content: ncc.get('texts.modals.setPrimary.noLabel'),
                                        nullContent: true
                                    }
                            }, 
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.setPrimary.wallet')
                                }
                            }, 
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            account: Utils.format.address.format(account),
                            wallet: wallet
                        },
                        function(values, closeModal) {
                            values.account = account;
                            ncc.postRequest('wallet/account/primary', values, function(data) {
                                ncc.showMessage(ncc.get('texts.common.success'), ncc.fill(ncc.get('texts.modals.setPrimary.successMessage'), Utils.format.address.format(account), accountLabel));
                                Utils.processWallet(data);
                                closeModal();
                            });
                        },
                        ncc.get('texts.modals.setPrimary.set')
                    );
                },
                bootLocalNode: function(e, message) {
                    ncc.showBootModal(message);
                },
                changeWalletName: function() {
                    var wallet = ncc.get('wallet.wallet');
                    ncc.showInputForm(ncc.get('texts.modals.changeWalletName.title'), '',
                        [
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.changeWalletName.wallet')
                                }
                            },
                            {
                                name: 'newName',
                                type: 'text',
                                label: {
                                    content: ncc.get('texts.modals.changeWalletName.newName')
                                },
                                isValid: function() {
                                    return Utils.valid.walletName(this.get("values")['newName']);
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            wallet: wallet
                        },
                        function(values, closeModal) {
                            var newWalletName = values['newName'];

                            ncc.postRequest('wallet/name/change', values, function() {
                                ncc.showMessage(ncc.get('texts.common.success'), ncc.fill(ncc.get('texts.modals.changeWalletName.successMessage'), wallet, newWalletName));
                                ncc.set('wallet.wallet', newWalletName);
                                closeModal();
                            });
                        },
                        ncc.get('texts.modals.changeWalletName.change')
                    );
                },
                exportWalletZip: function() {
                    var values = {wallet: ncc.get('wallet.wallet')};
                    ncc.postRawRequest('wallet/export', values, function(data) {
                        var blob = new Blob([data], {type: 'application/octet-binary'});
                        saveAs(blob, ncc.get('wallet.wallet') + '.zip');
                    });
                },
                changeWalletPassword: function() {
                    var wallet = ncc.get('wallet.wallet');
                    ncc.showInputForm(ncc.get('texts.modals.changeWalletPassword.title'), '',
                        [
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.changeWalletPassword.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.modals.changeWalletPassword.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            },
                            {
                                name: 'newPassword',
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.modals.changeWalletPassword.newPassword')
                                },
                                isValid: function() {
                                    var newWalletPass = this.get("values")['newPassword'];
                                    var confirmWalletPass = this.get("values")['confirmPassword'];
                                    return !!newWalletPass && (!confirmWalletPass || newWalletPass === confirmWalletPass);
                                }
                            },
                            {
                                name: 'confirmPassword',
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.modals.changeWalletPassword.confirmPassword')
                                },
                                isValid: function() {
                                    var newWalletPass = this.get("values")['newPassword'];
                                    var confirmWalletPass = this.get("values")['confirmPassword'];
                                    return !!confirmWalletPass && (!newWalletPass || newWalletPass === confirmWalletPass);
                                }
                            }
                        ],
                        {
                            wallet: wallet
                        },
                        function(values, closeModal) {
                            if (values['newPassword'] === values.confirmPassword) {
                                 delete values.confirmPassword;

                                ncc.postRequest('wallet/password/change', values, function() {
                                    ncc.showMessage(ncc.get('texts.common.success'), ncc.get('texts.modals.changeWalletPassword.successMessage'));
                                    closeModal();
                                 });
                            } else {
                                ncc.showMessage(ncc.get('texts.modals.changeWalletPassword.passwordNotMatchTitle'), ncc.get('texts.modals.changeWalletPassword.passwordNotMatchMessage'));
                            }
                        },
                        ncc.get('texts.modals.changeWalletPassword.change')
                    );
                },
                viewCurrentAccount: function() {
                    var address = ncc.get('activeAccount.address');
                    ncc.viewAccount(address);
                },
                copyClipboardHtml: function(e,address)
                {
                	// copyDisabled means FLASH copying is disabled and we should go on with html one
                    if (! ncc.get('walletPage.copyDisabled')) {
                        return;
                    }
                    ncc.showInputForm(ncc.get('texts.wallet.actions.copyClipboard'), 'Press Ctrl+C/⌘+C to copy',
                        [
                            {
                                name: 'copyAddress',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.common.address')
                                }
                            }
                        ],
                        {
                            copyAddress: address
                        },
                        function(values, closeModal) {
                            closeModal();
                        },
                        ncc.get('texts.common.closeButton')
                    );
                    $('#copyAddress').focus();
                    $('#copyAddress').select();
                },
                changeAccountLabel: function() {
                    var wallet = ncc.get('wallet.wallet');
                    var address = ncc.get('activeAccount.address');
                    var accountLabel = ncc.get('privateLabels')[address];
                    ncc.showInputForm(ncc.get('texts.modals.changeAccountLabel.title'), '',
                        [
                            {
                                name: 'privateLabel',
                                type: 'text',
                                label: {
                                    content: ncc.get('texts.modals.changeAccountLabel.label')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['privateLabel']);
                                }
                            },
                            {
                                name: 'addressBook',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.changeAccountLabel.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            addressBook: wallet,
                            address: address,
                            privateLabel: accountLabel
                        },
                        function(values, closeModal) {
                            ncc.postRequest('addressbook/accountlabel/change', values, function(data) {
                                var label = values.privateLabel;
                                ncc.showMessage(
                                    ncc.get('texts.common.success'), 
                                    ncc.fill(ncc.get('texts.modals.changeAccountLabel.successMessage'), Utils.format.address.format(address), label)
                                );
                                ncc.refreshInfo();
                                closeModal();
                                ncc.refreshAddressBook();
                            });
                        }, 
                        ncc.get('texts.modals.changeAccountLabel.change')
                    );
                },
                removeAccount: function() {
                    var wallet = ncc.get('wallet.wallet');
                    var account = ncc.get('activeAccount.address');
                    var accountLabel = ncc.get('privateLabels')[account];
                    ncc.showInputForm(ncc.get('texts.modals.removeAccount.title'), 
                        ncc.get('texts.modals.removeAccount.warning'),
                        [
                            {
                                name: 'account',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.common.account')
                                }
                            },
                            {
                                name: 'accountLabel',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.removeAccount.label')
                                }
                            },
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.removeAccount.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            }
                        ],
                        {
                            wallet: wallet,
                            account: account,
                            accountLabel: accountLabel
                        },
                        function(values, closeModal) {
                            ncc.postRequest('wallet/account/remove', values, function(data) {
                                var contacts = ncc.get('contacts');
                                Utils.removeContact(contacts, account)
                                ncc.showMessage(
                                    ncc.get('texts.common.success'), 
                                    ncc.fill(ncc.get('texts.modals.removeAccount.successMessage'), Utils.format.address.format(account), accountLabel)
                                );
                                Utils.processWallet(data);
                                ncc.fire('switchAccount', null, data.primaryAccount.address);
                                closeModal();
                            });
                        }, 
                        ncc.get('texts.modals.removeAccount.remove')
                    );
                },
                startHarvesting: function() {
                    var account = ncc.get('activeAccount.address');
                    var wallet = ncc.get('wallet.wallet');
                    ncc.set('walletPage.harvestButtonProcessing', true);
                    ncc.postRequest('wallet/account/unlock', 
                    {
                        wallet: wallet,
                        account: account
                    }, function(data) {
                        if (!data.ok) {
                            ncc.showError();
                        }
                    }, 
                    {
                        complete: function() {
                            ncc.refreshAccount(null, null, true);
                            ncc.set('walletPage.harvestButtonProcessing', false);
                        }
                    });
                },
                stopHarvesting: function() {
                    var account = ncc.get('activeAccount.address');
                    var wallet = ncc.get('wallet.wallet');
                    ncc.set('walletPage.harvestButtonProcessing', true);
                    ncc.postRequest('wallet/account/lock', 
                    {
                        wallet: wallet,
                        account: account
                    }, function(data) {
                        if (!data.ok) {
                            ncc.showError();
                        }
                    }, 
                    {
                        complete: function() {
                            ncc.refreshAccount(null, null, true);
                            ncc.set('walletPage.harvestButtonProcessing', false);
                        }
                    });
                },
                startRemoteHarvesting: function() {
                    ncc.showInputForm(ncc.get('texts.modals.startRemote.title'), '',
                        [   
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.startRemote.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            },
                            {
                                name: 'account',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.common.account')
                                }
                            }
                        ],
                        {
                            wallet: ncc.get('wallet.wallet'),
                            account: ncc.get('activeAccount.address')
                        },
                        function(values, closeModal) {
                            ncc.set('walletPage.harvestButtonProcessing', true);
                            ncc.postRequest('wallet/account/remote/unlock', values, function(data) {
                                closeModal();
                            }, {
                                complete: function() {
                                    ncc.refreshAccount(null, null, true);
                                    ncc.set('walletPage.harvestButtonProcessing', false);
                                }
                            });
                        },
                        ncc.get('texts.modals.startRemote.start')
                    );
                },
                stopRemoteHarvesting: function() {
                    ncc.showInputForm(ncc.get('texts.modals.stopRemote.title'), '',
                        [   
                            {
                                name: 'wallet',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.modals.stopRemote.wallet')
                                }
                            },
                            {
                                name: 'password', 
                                type: 'password',
                                label: {
                                    content: ncc.get('texts.common.password')
                                },
                                isValid: function() {
                                    return Utils.valid.notEmpty(this.get("values")['password']);
                                }
                            },
                            {
                                name: 'account',
                                type: 'text',
                                readonly: true,
                                unimportant: true,
                                label: {
                                    content: ncc.get('texts.common.account')
                                }
                            }
                        ],
                        {
                            wallet: ncc.get('wallet.wallet'),
                            account: ncc.get('activeAccount.address')
                        },
                        function(values, closeModal) {
                            ncc.set('walletPage.harvestButtonProcessing', true);
                            ncc.postRequest('wallet/account/remote/lock', values, function(data) {
                                closeModal();
                            }, {
                                complete: function() {
                                    ncc.refreshAccount(null, null, true);
                                    ncc.set('walletPage.harvestButtonProcessing', false);
                                }
                            });
                        },
                        ncc.get('texts.modals.stopRemote.stop')
                    );
                }
            }));

            local.intervalJobs.push(setInterval(ncc.refreshAccount.bind(null, null, null, true), local.autoRefreshInterval));

            ncc.refreshAppStatus(function() {
                if (ncc.get('settings.firstStart') === 0) {
                    ncc.showMessage(
                        ncc.get('texts.modals.initialTy.title'),
                        ncc.get('texts.modals.initialTy.content'),
                        function() {
                            ncc.showMessage(
                                ncc.get('texts.modals.initialBackup.title'),
                                ncc.get('texts.modals.initialBackup.content'),
                                function() {
                                    var settings = ncc.get('settings');
                                    settings['firstStart'] = 1;
                                    ncc.postRequest('configuration/update', settings);
                                    ncc.set('settings', settings);
                                }
                            );
                        },
                        'modal--wide'
                    );
                }
                if (!ncc.get('nodeBooted')) {
                    if (ncc.get('settings.nisBootInfo.bootNis')) {
                        var account = ncc.get('activeAccount.address');
                        var accountLabel = ncc.get('privateLabels')[account];
                        var hostName = ncc.get('settings.remoteServer.host');
                        var warningText = ncc.fill(
                            ncc.get('texts.modals.bootLocalNode.warningStatement'),
                            accountLabel ? accountLabel : ncc.get('activeAccount.address'),
                            ncc.get('activeAccount.formattedBalance'),
                            hostName);

                        var doBootNodeRequest = function() {
                            // default the node name to a substring of the account name so that auto-boot works out-of-box
                            var accountName = ncc.get('settings.nisBootInfo.account') || ncc.get('wallet.primaryAccount.address');
                            var bootData = {
                                nodeName: ncc.get('settings.nisBootInfo.nodeName') || accountName.substring(0, 10),
                                wallet: ncc.get('wallet.wallet'),
                                account: accountName
                            };

                            ncc.set('status.booting', true);
                            ncc.postRequest('node/boot', bootData,
                                function(data) {
                                    // NIS info will be automatically retrieved when NIS status becomes BOOTED
                                    // so no need to manually call refreshNisInfo()
                                    ncc.refreshAppStatus();
                                },
                                {
                                    altFailCb: function(faultId) {
                                        if (601 === faultId) {
                                            ncc.refreshAppStatus();
                                        }
                                    },
                                    complete: function() {
                                        ncc.set('status.booting', false);
                                    }
                                },
                                false
                            );
                        };

                        if (ncc.isLocalNode(hostName)) {
                            doBootNodeRequest();
                        } else {
                            ncc.showMessage(
                                ncc.get('texts.modals.bootLocalNode.warning'),
                                warningText
                            );
                            /*
                            ncc.showConfirmation(ncc.get('texts.modals.bootLocalNode.warning'), warningQuestion, {
                                yes: doBootNodeRequest
                            });
                            */
                        }

                    } else {
                        var hostName = ncc.get('settings.remoteServer.host');
                        if (ncc.isLocalNode(hostName)) {
                            ncc.showBootModal(ncc.get('texts.wallet.bootNodeWarning'));
                        }
                    }
                }
            });

            global.$window.on('beforeunload', function() {
                return ncc.get('texts.modals.logoutWarning.leavePage');
            });
        },
        leave: [function() {            
            ncc.global.$window.off('resize.scrollableSidebar');
            ncc.global.$window.off('beforeunload');
            ncc.set('contacts', {});
        }]
    });
});