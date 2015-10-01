"use strict";

require.config({
    baseUrl: 'scripts',
    paths: {
        'layout': '../layouts',
        'jquery': 'plugins/jquery-2.1.0.min',
        'expand': 'plugins/expand',
        'filesaver': 'plugins/FileSaver.min',
        'modernizr': 'plugins/modernizr.custom.00846',
        'ractive': 'plugins/ractive',
         'ractive-events-tap': 'plugins/ractive-events-tap',
        'tinycarousel': 'plugins/jquery.tinycarousel.min',
        'scroller': 'plugins/jquery.fs.scroller.min',
        'selecter': 'plugins/jquery.fs.selecter.min',
        'stepper': 'plugins/jquery.fs.stepper.min',
        'gridster': 'plugins/jquery.gridster.min',
        'maskedinput': 'plugins/jquery.mask.min',
        'dropit': 'plugins/dropit.min',
        'zeroClipboard': 'plugins/ZeroClipboard.min',
        'tooltipster': 'plugins/jquery.tooltipster.min',
        'mustache': 'plugins/mustache.min',
        'draggable': 'plugins/jquery-ui.draggable.min',
        'typeahead': 'plugins/typeahead.jquery-dist',
        'handlebars': 'plugins/handlebars-v2.0.0-dist'
    },
    shim: {
        'scroller': { deps: ['jquery'] },
        'selecter': { deps: ['jquery'] },
        'stepper': { deps: ['jquery'] },
        'gridster': { deps: ['jquery'] },
        'maskedinput': { deps: ['jquery'] },
        'dropit': { deps: ['jquery'] },
        'tooltipster': { deps: ['jquery'] },
        'typeahead': {
            deps: ['jquery'],
            init: function () {
                // this is an ugly hack to get 'click' selection working for typeahead
                // without modifying the typeahead code
                //
                // the problem is that when clicking on a 'suggestion':
                // - mousedown is triggered
                // - click is not triggered (this is the event that actually selects an item)
                // - blur (of the input) is triggered, which closes the dropdown without selecting anything
                //
                // after a lot of testing, it seems that a mousedown event handler *somewhere* is triggering blur
                // (the typeahead plugin works fine on a standalone page)
                //
                // the more 'correct' fix would be to modify the plugin code itself, but this is unobtrusive
                // when a node is inserted into the page, if it is a 'tt-suggestions' node (added by the typeahead)
                // suppress the mousedown event on any of the items ('tt-suggestion')
                //
                // (DOMNodeInserted is called for the entire 'tt-suggestions' tree)
                $('body').on('DOMNodeInserted', function (e) {
                    var newNode = $(e.target);
                    if (!newNode.hasClass('tt-suggestions')) {
                        return;
                    }

                    $.each(newNode.find('.tt-suggestion'), function () {
                        newNode.on('mousedown', function (e) {
                            return false;
                        });
                    });
                });
            }
        }
    }
});

define(['ncc'], function(ncc) {
    ncc.getRequest('configuration/get', function(data) {
        ncc.set('settings', data);
        ncc.loadPage(entryPage, null, false, true);
    });

    window.onpopstate = function(event) {
        ncc.loadPage(event.state.page, event.state.params, true, false);
    };

    ncc.refreshVersionStatus = function(complete) {
        ncc.getRequest('version', function(d){
            var nisData = ncc.get('nis');
            var currentVersion = (nisData && 'nodeInfo' in nisData) ? nisData.nodeInfo.nisInfo.version.match(/(\d+)\.(\d+)\.(\d+)/) : null;
            var versionKey = 'latest';
            if (versionKey in d) {
                var remoteVersion = d[versionKey].match(/(\d+)\.(\d+)\.(\d+)/);
                if (remoteVersion) {
                    ncc.set('latestVersion', d[versionKey]);
                    if (currentVersion != null) {
                        var r = remoteVersion;
                        var c = currentVersion;
                        if (!ncc.get('displayed.newBuildMessage')) {
                            if (r[1] > c[1] || (r[1] === c[1] && r[2] > c[2]) || (r[1] === c[1] && r[2] === c[2] && r[3] > c[3])) {
                                ncc.showMessage(
                                    ncc.get('texts.common.newBuild'),
                                    ncc.fill(ncc.get('texts.common.newBuildNumber'), remoteVersion[0]),
                                    null,
                                    'modal--wide'
                                );
                                ncc.set('displayed.newBuildMessage', true);
                            }
                        }
                    }
                }
            }
        });
    };
    ncc.refreshVersionStatus();
    setInterval(ncc.refreshVersionStatus, 10000);

    ncc.refreshNccStatus = function(complete) {
        var success = false;
        ncc.getRequest('status',
            function(data) {
                ncc.set('nccStatus', data);
                success = true;
            },
            {
                complete: function() {
                    if (!success) {
                        ncc.set('nccStatus.code', ncc.Status.STATUS_STOPPED);
                    }

                    if (complete) complete();
                }
            },
            true
        );
    };
    ncc.refreshNccStatus();
    setInterval(ncc.refreshNccStatus, 3000);

    ncc.refreshNisStatus = function(complete) {
        var success = false;
        ncc.getRequest('node/status',
            function(data) {
                ncc.set('nisStatus', data);
                success = true;
            },
            {
                complete: function(jqXHR) {
                    if (jqXHR.status === 0) {
                        ncc.set('nccStatus.code', ncc.Status.STATUS_STOPPED);
                    } else if (!success) {
                        ncc.set('nisStatus.code', ncc.Status.STATUS_STOPPED);
                    }

                    if (complete) complete();
                }
            },
            true
        );
    };
    ncc.refreshNisStatus();
    setInterval(ncc.refreshNisStatus, 3000);

    ncc.refreshAppStatus = function(complete) {
        ncc.refreshNccStatus(function() {
            ncc.refreshNisStatus(complete);
        });
    };

    (function() {
        var waitTime = 30000;
        var t = 0;

        ncc.refreshNisInfo = function() {
            clearTimeout(t);
            ncc.set('nis.nodeMetaData.lastBlockBehind', null);
            ncc.getRequest('info/nis',
                function(data) {
                    var blockchainHeight = ncc.get('blockchainHeight') || data.nodeMetaData.nodeBlockChainHeight;
                    var lastBlockBehind = (data.nodeMetaData.maxBlockChainHeight - blockchainHeight) * 60;
                    ncc.set('nis', data);
                    ncc.set('nis.nodeMetaData.lastBlockBehind', lastBlockBehind < 0? 0 : lastBlockBehind);
                    if (data.nodeMetaData.maxBlockChainHeight === blockchainHeight) {
                        waitTime = 60000;
                    } else {
                        waitTime = 30000;
                    }
                },
                {
                    complete: function() {
                        t = setTimeout(ncc.refreshNisInfo, waitTime);
                    }
                },
                true
            );
        };

        ncc.observe('nodeBooted', function(booted) {
            if (booted) {
                ncc.refreshNisInfo();
            } else {
                clearTimeout(t);
            }
        });
    })();

    ncc.refreshChainHeight = function() {
        ncc.getRequest('info/nis/chain/height', function(data) {
            ncc.set('blockchainHeight', data.height);
        }, null, true);
    };
    ncc.refreshChainHeight();
    setInterval(ncc.refreshChainHeight, 3000);
});