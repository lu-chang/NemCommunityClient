"use strict";

define([
    'languages',
    'TransactionType', 'jquery', 'ractive', 'mustache', 'tooltipster', 'Utils', 'NccModal',
    'ConfirmModal', 'InputModal', 'SettingsModal', 'SendNemModal', 'SignMultisigModal',
    'ModificationConfirmModal', 'SignatureConfirmModal', 'TransactionConfirmModal', 'TransactionDetailsModal', 'AccountDetailsModal', 'ConvertMultisigModal',
    'GenericDelegatedModal', 'GenericDelegatedConfirmModal'
],
function(languages,
    TransactionType, $, Ractive, Mustache, tooltipster, Utils, NccModal,
    ConfirmModal, InputModal, SettingsModal, SendNemModal, SignMultisigModal,
    ModificationConfirmModal, SignatureConfirmModal, TransactionConfirmModal, TransactionDetailsModal, AccountDetailsModal, ConvertMultisigModal,
    GenericDelegatedModal, GenericDelegatedConfirmModal) {

    var NccRactive = Ractive.extend({
        el: document.body,
        template: '#template',
        components: {
            errorModal: NccModal,
            yikesModal: NccModal,
            messageModal: NccModal,
            confirmModal: ConfirmModal,
            inputModal: InputModal,
            settingsModal: SettingsModal,
            sendNemModal: SendNemModal,
            signMultisigModal: SignMultisigModal,
            modificationConfirmModal: ModificationConfirmModal,
            signatureConfirmModal: SignatureConfirmModal,
            clientInfoModal: NccModal,
            transactionDetailsModal: TransactionDetailsModal,
            transactionConfirmModal: TransactionConfirmModal,
            accountDetailsModal: AccountDetailsModal,
            convertMultisigModal: ConvertMultisigModal,
            activateDelegatedModal: GenericDelegatedModal,
            deactivateDelegatedModal: GenericDelegatedModal,
            genericDelegatedConfirmModal: GenericDelegatedConfirmModal
        },
        Status:
        {
                STATUS_UNKNOWN: 0,
                STATUS_STOPPED: 1,
                STATUS_STARTING: 2,
                STATUS_RUNNING: 3,
                STATUS_BOOTING: 4,
                STATUS_BOOTED: 5,
                STATUS_SYNCHRONIZED: 6,
                STATUS_NO_REMOTE_NIS_AVAILABLE: 7,
                STATUS_LOADING: 8
        },
        sortAccounts: function(accounts) {
            var contactsRef = this.get('privateLabels');

            accounts.sort(function(a,b){
                var cA = (a.address in contactsRef) ? contactsRef[a.address] : null;
                var cB = (b.address in contactsRef) ? contactsRef[b.address] : null;
                if (cA !== null && cB !== null) {
                    return cA.localeCompare(cB);
                }
                if (cA !== null && cB === null) {
                    return -11;
                }
                if (cA === null && cB !== null) {
                    return 1;
                }
                return a.address.localeCompare(b.address);
            });
            return accounts;
        },
        computed: {
            allAccounts: function() {
                return [this.get('wallet.primaryAccount')].concat(this.sortAccounts(this.get('wallet.otherAccounts')));
            },
            appStatus: function() {
                switch (this.get('nccStatus.code')) {
                    case null:
                    case undefined:
                    case this.Status.STATUS_UNKNOWN:
                        return {
                            type: 'critical',
                            message: this.get('texts.common.appStatus.nccUnknown')
                        };
                    case  this.Status.STATUS_STOPPED:
                        return {
                            type: 'critical',
                            message: this.get('texts.common.appStatus.nccUnavailable')
                        };
                    case  this.Status.STATUS_STARTING:
                        return {
                            type: 'warning',
                            message: this.get('texts.common.appStatus.nccStarting')
                        };
                    default: // probably RUNNING
                        switch (this.get('nisStatus.code')) {
                            case null:
                            case undefined:
                            case this.Status.STATUS_UNKNOWN:
                                return {
                                    type: 'critical',
                                    message: this.get('texts.common.appStatus.nisUnknown')
                                };
                            case this.Status.STATUS_STOPPED:
                                return {
                                    type: 'critical',
                                    message: this.get('texts.common.appStatus.nisUnavailable')
                                };
                            case this.Status.STATUS_STARTING:
                                return {
                                    type: 'warning',
                                    message: this.get('texts.common.appStatus.nisStarting')
                                };
                            case this.Status.STATUS_LOADING:
                                return {
                                    type: 'warning',
                                    message: this.get('texts.common.appStatus.loading') + this.get('blockchainHeight')
                                };
                            case this.Status.STATUS_RUNNING:
                                if (this.get('status.booting')) {
                                    return {
                                        type: 'warning',
                                        message: this.get('texts.common.appStatus.booting')
                                    };
                                } else {
                                    return {
                                        type: 'warning',
                                        message: this.get('texts.common.appStatus.notBooted')
                                    };
                                }
                            case this.Status.STATUS_BOOTING:
                                return {
                                    type: 'warning',
                                    message: this.get('texts.common.appStatus.booting')
                                };
                            case this.Status.STATUS_BOOTED:
                                var lastBlockBehind = this.get('nis.nodeMetaData.lastBlockBehind');
                                if (lastBlockBehind !== 0) {
                                    if (!lastBlockBehind) {
                                        return {
                                            type: 'warning',
                                            message: this.get('texts.common.appStatus.nisInfoNotAvailable')
                                        };
                                    } else {
                                        var daysBehind = Math.floor(this.get('nis.nodeMetaData.lastBlockBehind') / (60 * 1440));
                                        var daysBehindText;
                                        switch (daysBehind) {
                                            case 0:
                                                daysBehindText = this.get('texts.common.appStatus.daysBehind.0');
                                                break;
                                            case 1:
                                                daysBehindText = this.get('texts.common.appStatus.daysBehind.1');
                                                break;
                                            default:
                                                daysBehindText = this.fill(this.get('texts.common.appStatus.daysBehind.many'), daysBehind);
                                                break;
                                        }

                                        return {
                                            type: 'warning',
                                            message: this.fill(this.get('texts.common.appStatus.synchronizing'), this.get('blockchainHeight'), daysBehindText)
                                        };
                                    }
                                } else {
                                    return {
                                        type: 'message',
                                        message: this.get('texts.common.appStatus.synchronized')
                                    };
                                }

                                return {
                                    type: 'warning',
                                    message: this.get('texts.common.appStatus.synchronizing')
                                };
                            case this.Status.STATUS_SYNCHRONIZED:
                                return {
                                    type: 'message',
                                    message: this.get('texts.common.appStatus.synchronized')
                                };
                            case this.Status.STATUS_NO_REMOTE_NIS_AVAILABLE:
                                return {
                                    type: 'warning',
                                    message: this.get('texts.common.appStatus.noRemoteNisAvailable')
                                };
                       }
                }
            },
            loadingDb: function() {
                return this.get('nisStatus.code') === this.Status.STATUS_LOADING;
            },
            nodeBooting: function() {
                return this.get('status.booting') || this.get('nisStatus.code') === this.Status.STATUS_BOOTING;
            },
            nodeBooted: function() {
                var nisStatus = this.get('nisStatus.code');
                return nisStatus === this.Status.STATUS_BOOTED || nisStatus === this.Status.STATUS_SYNCHRONIZED || nisStatus == this.Status.STATUS_NO_REMOTE_NIS_AVAILABLE;
            },
            nisUnavailable: function() {
                return this.get('nisStatus.code') === this.Status.STATUS_STOPPED;
            },
            sendNemDisabled: function() {
                return this.get('nisUnavailable') || ncc.get('activeAccount').isMultisig;
            },
            enableManualBoot: function() {
                return !(this.get('nisUnavailable') || this.get('nodeBooting') || this.get('nodeBooted'));
            },
            nisSynchronized: function() {
                // 5 status code is not implemented yet
                // so we cannot completely rely on NIS status code
                return this.get('nisStatus.code') === this.Status.STATUS_SYNCHRONIZED || this.get('nis.nodeMetaData.lastBlockBehind') === 0;
            },
            lcwNameValid: function() {
            	// TODO 20150711 J-G: could you use Utils.valid.walletName()?
                var walletName = this.get('landingPage.createWalletForm.wallet');
                return !!walletName && !walletName.match(/[\\/.:*?"<>|]/);
            },
            lcwPasswordValid: function() {
                return !!this.get('landingPage.createWalletForm.password');
            },
            lcwConfirmPasswordValid: function() {
                return this.get('lcwPasswordValid') && this.get('landingPage.createWalletForm.password') === this.get('landingPage.confirmPassword');
            },
            lcwFormValid: function() {
                return this.get('lcwNameValid') && this.get('lcwPasswordValid') && this.get('lcwConfirmPasswordValid');
            },
            privateLabels: function() {
                var contacts = this.get('contacts') || {};
                var result = {};
                var contact;
                for (var i = 0; i < contacts.length; i++) {
                    contact = contacts[i];
                    result[contact.address] = contact.privateLabel;
                }
                return result;
            }
        },
        ajaxError: function(jqXHR, textStatus, errorThrown) {
            this.showError(textStatus, errorThrown);
        },
        apiPath: function(api) {
            return '../api/' + api;
        },
        checkSuccess: function(data, silent, settings) {
            var self = this;
            function showError(faultId, error) {
                if (settings && settings.altFailCb) {
                    var noErrorModal = settings.altFailCb(faultId, error);
                }
                if (!silent && !noErrorModal) {
                    self.showError(faultId, error);
                }
                return false;
            }

            if (undefined !== data.error) {
                var propertyName = undefined;
                if (400 == data.status)
                switch (data.status) {
                    case 200:
                        return true;

                    case 400:
                        var propertyNameRegex = /property (.*),/i;
                        propertyName = data.message.match(propertyNameRegex)[1];
                        break;

                    case 601: // NODE_ALREADY_BOOTED
                        silent = true;
                        break;
                }

                var message = undefined;
                if (propertyName) {
                    message = this.get('texts.faults.' + data.status);
                    message += ' (' + propertyName + ')';
                }

                var dataMessage = "NIS: " + data.message.replace(/_/g, " ");
                return showError(data.status, message || dataMessage);
            }

            return true;
        },
        syncRequest: function(url) {
            return $.ajax(url, {
                async: false,
                type: 'GET',
                error: this.ajaxError
            }).responseText;
        },
        /**
         * @param {string} type
         * @param {string} api
         * @param {object} requestData
         * @param {string} success
         */
        _ajaxRequest: function(type, api, requestData, successCb, settings, silent, dataType) {
            var self = this;
            successCb = successCb || function() {};

            // set the dataType to 'text' instead of 'json' because  NCC will return "" (empty string)
            // from void functions, but JQuery treats this as invalid JSON
            var s = {
                contentType: 'application/json',
                dataType: dataType || 'text',
                type: type,
                data: requestData ? JSON.stringify(requestData) : undefined,
                error: function (jqXHR, textStatus, errorThrown) {
                    // first check the success (in case this is a new API)
                    var data = jqXHR.responseText ? JSON.parse(jqXHR.responseText) : {};
                    if (!self.checkSuccess(data, silent, settings))
                        return;

                    // since we are in an error callback, handle the error as an unknown ajax error
                    return silent ? [] : self.ajaxError(jqXHR, textStatus, errorThrown);
                },
                success: function(data, textStatus, request) {
                    // if the data is an empty string, emulate original NCC behavior by returning { ok: 1 }
                    if (!data) {
                        successCb({ ok: 1 });
                        return;
                    }

                    var contentType = request.getResponseHeader("content-type") || "";
                    if (contentType.indexOf('application/octet-stream') > -1) {
                        successCb(data);

                    } else {
                        // otherwise, parse the json check the success (for legacy API handling)
                        var parsedData = $.parseJSON(data);
                        if (self.checkSuccess(parsedData, silent, settings)) {
                            successCb(parsedData);
                        }
                    }
                }
            };
            $.extend(s, settings);
            $.ajax(this.apiPath(api), s);
        },
        getRequest: function(api, successCb, settings, silent) {
            return this._ajaxRequest('get', api, undefined, successCb, settings, silent);
        },
        postRequest: function(api, requestData, successCb, settings, silent, dataType) {
            return this._ajaxRequest('post', api, requestData, successCb, settings, silent, dataType);
        },
        postRawRequest: function(api, requestData, successCb) {
            // ugly but has to do the trick for now,
            // TODO: handle errors, to be compatible with other calls
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function(){
                if (this.readyState == 4 && this.status == 200){
                    successCb(this.response);
                }
            }
            xhr.open('POST', this.apiPath(api));
            xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
            xhr.responseType = 'blob';
            xhr.send(JSON.stringify(requestData));
        },
        getModal: function(modalName) {
            return this.findComponent(modalName + 'Modal');
        },
        openModal: function(id) {
            this.getModal(id).open();
        },
        showError: function(errorId, message) {
            var modal = this.getModal('error');
            if (!message && typeof errorId === 'number') {
                message = this.get('texts.faults.' + errorId);
            }
            if (errorId == 602) {
                modal = this.getModal('yikes');
            }
            modal.set({
                errorId: errorId,
                message: message || 'Unknown error',
            });
            modal.open();
        },
        showMessage: function(title, message, closeCallback, extraModalClass) {
            var modal = this.getModal('message');
            modal.set({
                modalTitle: title,
                message: message,
                extraModalClass: extraModalClass
            });
            modal.open();
            if (closeCallback) {
                var ob = modal.observe('isActive', function(newValue, oldValue) {
                    if (newValue === false) {
                        closeCallback();
                        ob.cancel();
                    }
                });
            }
        },
        showConfirmation: function(title, message, callbacks, actions, extraModalClass) {
            var modal = this.getModal('confirm');
            modal.set({
                modalTitle: title,
                message: message,
                callbacks: callbacks,
                actions: actions || [
                    {
                        action: 'yes',
                        label: ncc.get('texts.modals.confirmDefault.yes'),
                        actionType: 'primary'
                    },
                    {
                        action: 'no',
                        label: ncc.get('texts.modals.confirmDefault.no'),
                        actionType: 'secondary'
                    }
                ],
                extraModalClass: extraModalClass
            });
            modal.open();
        },
        showInputForm: function(title, message, fields, initValues, submitCb, submitLabel, processingLabel) {
            var modal = this.getModal('input');
            modal.set({});

            modal.set({
                modalTitle: title,
                message: message,
                fields: fields,
                values: initValues,
                submitCb: submitCb,
                submitLabel: submitLabel,
                processingLabel: processingLabel
            });
            modal.open();
        },
        toggleOn: function(id) {
            Utils.toggleOn(this, id);
        },
        toggleOff: function(id) {
            Utils.toggleOff(this, id);
        },
        fill: function(template) {
            // The first argument could be whether it should return the HTML decoded version
            if (typeof arguments[0] === 'boolean') {
                var decode = arguments[0];
                Array.prototype.splice.call(arguments, 0, 1);
                template = arguments[0];

                if (decode) {
                    return Utils.htmlDecode(Mustache.render(template, arguments));
                } else {
                    return Mustache.render(template, arguments);
                }
            } else {
                return Mustache.render(template, arguments);
            }
        },

        refreshAccount : function(wallet, account, silent) {
            if (!wallet) wallet = ncc.get('wallet.wallet');
            if (!account) account = ncc.get('activeAccount.address');

            if (wallet === undefined || account === undefined) { return; }
            var success = false;
            ncc.postRequest('account/transactions/all',
                {
                    wallet: wallet,
                    account: account
                },
                function(data) {
                    success = true;
                    ncc.set('activeAccount', Utils.processAccount(data));
                    ncc.set('status.lostConnection', false);
                },
                {
                    complete: function() {
                        if (!success) {
                            ncc.set('status.lostConnection', true);
                        }
                    }
                },
                silent
            );

            ncc.refreshRemoteHarvestingStatus(wallet, account, silent);

            ncc.fire('refreshAccount');
        },

        signToken: function(address) {
            var wallet = ncc.get('wallet.wallet');
            var accountLabel = ncc.get('privateLabels')[address];
            ncc.showInputForm(ncc.get('texts.modals.signToken.title'), '',
                [
                    {
                        name: 'token',
                        type: 'text',
                        label: {
                            content: ncc.get('texts.modals.signToken.label')
                        },
                        isValid: function() {
                            return Utils.valid.notEmpty(this.get("values")['token']);
                        }
                    },
                    {
                        name: 'account',
                        type: 'text',
                        readonly: true,
                        unimportant: true,
                        label: {
                            content: ncc.get('texts.common.address')
                        },
                        sublabel: accountLabel ?
                        {
                            content: accountLabel
                        } :
                        {
                            // reuse string
                            content: ncc.get('texts.modals.bootLocalNode.noLabel'),
                            nullContent: true
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
                    account: address,
                    privateLabel: accountLabel
                },
                function(values, closeModal) {
                    var a2h = function(y) { return y.split('').map(function(x){ var t=encodeURIComponent(x); return (t.length === 1) ? t.charCodeAt(0).toString(16) : t.replace(/%/g, ''); }).join(''); };
                    values['origToken'] = values['token'];
                    values['token'] = a2h(values['origToken']);
                    ncc.postRequest('wallet/account/signToken', values, function(data) {
                        ncc.showInputForm(ncc.get('texts.modals.signToken.title'), '',
                            [
                                {
                                    name: 'token',
                                    type: 'text',
                                    readonly: true,
                                    unimportant: true,
                                    label: {
                                        content: ncc.get('texts.modals.signToken.label')
                                    }
                                },
                                {
                                    name: 'account',
                                    type: 'text',
                                    readonly: true,
                                    unimportant: true,
                                    label: {
                                        content: ncc.get('texts.common.address')
                                    },
                                    sublabel: accountLabel ?
                                    {
                                        content: accountLabel
                                    } :
                                    {
                                        // reuse string
                                        content: ncc.get('texts.modals.bootLocalNode.noLabel'),
                                        nullContent: true
                                    }
                                },
                                {
                                    name: 'signedToken',
                                    type: 'textarea',
                                    readonly: true,
                                    label: {
                                        content: ncc.get('texts.modals.signToken.signature')
                                    }
                                }
                            ],
                            {
                                wallet: wallet,
                                account: address,
                                privateLabel: accountLabel,
                                token: values['origToken'],
                                signedToken: data['signature']
                            },
                            function(values, closeModal) {
                                $('#signedToken').focus();
                                closeModal();
                            },
                            ncc.get('texts.common.closeButton')
                        );
                    });
                },
                ncc.get('texts.modals.signToken.sign')
            );
        },
        showKey: function(address, title, message, requestPath) {
            var self = this;
            var wallet = ncc.get('wallet.wallet');
            var account = address;
            var accountLabel = ncc.get('privateLabels')[account];

            // 1st modal
            ncc.showInputForm(ncc.get(title), ncc.get(message),
                [
                    {
                        name: 'wallet',
                        type: 'text',
                        readonly: true,
                        unimportant: true,
                        label: {
                            // reuse string
                            content: ncc.get('texts.modals.createAccount.wallet')
                        }
                    },
                    {
                        name: 'account',
                        type: 'text',
                        readonly: true,
                        unimportant: true,
                        label: {
                            content: ncc.get('texts.common.address')
                        },
                        sublabel: accountLabel ?
                        {
                            content: accountLabel
                        } :
                        {
                            // reuse string
                            content: ncc.get('texts.modals.bootLocalNode.noLabel'),
                            nullContent: true
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
                    account: Utils.format.address.format(account),
                },
                function(values, closeModal) {
                    var values = {
                        wallet: values.wallet,
                        account: account,
                        password: values.password
                    };
                    ncc.postRequest(requestPath, values, function(data) {
                        // 2st modal - call results
                        ncc.showInputForm(
                            ncc.get(title),
                            ncc.get(message),
                            [
                                {
                                    name: 'address',
                                    type: 'text',
                                    readonly: true,
                                    label: {
                                        content: ncc.get('texts.common.address')
                                    }
                                },
                                {
                                    name: 'publicKey',
                                    type: 'textarea',
                                    readonly: true,
                                    label: {
                                        content: ncc.get('texts.modals.showPrivateKey.publicKey')
                                    }
                                },
                                {
                                    name: 'privateKey',
                                    type: 'textarea',
                                    readonly: true,
                                    label: {
                                        content: ncc.get('texts.modals.showPrivateKey.privateKey')
                                    }
                                }
                            ],
                            {
                                address: Utils.format.address.format(data.address),
                                publicKey: data.publicKey,
                                privateKey: data.privateKey
                            },
                            function(values, closeModal) {
                                closeModal();
                            },
                            ncc.get('texts.common.closeButton')
                        );
                    });
                },
                ncc.get('texts.modals.showPrivateKey.show')
            );
        },
        globalSetup: function() {
            require(['draggable'], function() {
                $('.modal').draggable({
                    handle: '.modal-head'
                });
            });
        },
        loadPage: function(page, params, isBack, isInit) {
            var self = this;

            // We use require(string) instead of require(array, function) to make page loading process synchronous
            // require(string) needs dependencies to be loaded before being used
            // So we have to load all layout files and templates first
            var layouts = [];
            var loadLayout = function(layoutName) {
                require([layoutName], function(layout) {
                    layouts.unshift(layout);
                    require([layout.template], function() {
                        if (layout.parent) {
                            loadLayout(layout.parent);
                        } else {
                            replaceLayouts();
                        }
                    });
                });
            };

            var replaceLayouts = function() {
                var oldParams = ncc.get('params');
                var paramsChanged = JSON.stringify(oldParams) !== JSON.stringify(params);

                for (var i = 0; i < layouts.length; i++) {
                    var layout = layouts[i];
                    var keypath = 'layout.' + i;
                    var currentLayout = self.get(keypath);

                    if (!layout.alreadyInit && layout.initOnce) {
                        layout.initOnce();
                        layout.alreadyInit = true;
                    }
                        
                    if (paramsChanged && layout.paramsChanged) {
                        var abort = layout.paramsChanged(params);
                        if (abort) return;
                    }

                    if (!currentLayout || (currentLayout.name !== layout.name)) {
                        var template = require(layout.template);
                        if (currentLayout && currentLayout.leave) {
                            $.each(currentLayout.leave, function() {
                                this.apply(currentLayout);
                            });
                        }

                        // Init
                        if (layout.initEverytime) {
                            var abort = layout.initEverytime(params);
                            if (abort) return;
                        }

                        self.set(keypath, null);
                        self.partials[i] = template;
                        $.extend(self.partials, layout.realPartials);
                        self.set(keypath, layout);

                        // Setup
                        if (!layout.alreadySetup && layout.setupOnce) {
                            layout.setupOnce();
                            layout.alreadySetup = true;
                        }
                        if (layout.setupEverytime) {
                            var abort = layout.setupEverytime(params);
                            if (abort) return;
                        }
                    }
                }

                self.globalSetup();

                // Clear the old layouts
                var currLayouts = self.get('layout');
                if (currLayouts && currLayouts.length) {
                    for (; i < currLayouts.length; i++) {
                        self.set('layout.' + i, null);
                    }
                }

                if (!isBack) {
                    var queryString = '';
                    if (params) {
                        queryString = Utils.toQueryString(params);
                    } else if (isInit) {
                        queryString = location.search;
                        params = Utils.queryStringToJson(location.search);
                    }
                    var url = layouts[layouts.length - 1].url + queryString;
                    
                    if (isInit) {
                        history.replaceState({ page: page, params: params }, 'entry', url);
                    } else {
                        history.pushState({ page: page, params: params }, null, url);
                    }
                }
                ncc.set('params', params);
            };

            loadLayout(page);
        },
        onrender: function(options) {
            var self = this;
            require(['languages'], function(languages) {
                self.set('languages', languages);
                self.observe('settings.language', function(newValue) {
					newValue = newValue || Utils.config.defaultLanguage;
                    for (var i = 0; i < languages.length; i++) {
                        if (languages[i].id.toLowerCase() === newValue.toLowerCase()) {
                            self.set('texts', languages[i].texts);
                            return;
                        }
                    }
                });
            });

            this.on({
                redirect: function(e, page, params) {
                    this.loadPage(page, params);
                },
                shutdown: function() {
                    var self = this;
                    this.showConfirmation(ncc.get('texts.modals.shutdown.title'), ncc.get('texts.modals.shutdown.message'), {
                        yes: function() {
                            self.loadPage('start');
                        }
                    });
                },
                selectFile: function(e, id) {
                    var input = this.nodes[id];
                    var evt = document.createEvent("MouseEvents");
                    evt.initEvent('click', true, false);
                    input.dispatchEvent(evt);
                },
                registerToolTip: function(e) {
                    var $node = $(e.node);
                    if (!$node.data('tooltipster-ns') && $node.attr('title')) {
                        $node.tooltipster({
                            position: 'bottom',
                            delay: 50,
                            functionBefore: function($origin, continueTooltip) {
                                var title = $origin.attr('title');
                                if (title) {
                                    $origin.tooltipster('content', title);
                                    $origin.removeAttr('title');
                                }
                                continueTooltip();
                            }
                        });
                        $node.tooltipster('show');
                    }
                },
                set: function(e, keypath, val) {
                    this.set(keypath, val);
                }
            });

            this.set('fill', this.fill);

            this.global = {
                $window: $(window),
                $document: $(document),
                $html: $('html'),
                $body: $('body')
            };
        }
    });

    window.ncc = new NccRactive();
    return ncc;
});
