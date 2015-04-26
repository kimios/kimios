/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
kimios = {

    defaultLanguage: 'en',

    implPackage: undefined,

    getImplPackage: function () {
        return this.implPackage;
    },
    setImplPackage: function (impl) {
        this.implPackage = impl;
    },
    viewableExtensions: new Array('png', 'jpg', 'jpeg', 'tif', 'tiff', 'gif', 'pdf', 'PDF', 'odt', 'odp', 'docx', 'doc','txt', 'java', 'cs', 'cpp', 'c', 'py', 'sql', 'xml', 'eml'),

    isViewableExtension: function (ext) {
        var exts = kimios.viewableExtensions;
        for (var i = 0; i < exts.length; i++) {
            if (ext.toLowerCase() == exts[i]) {
                return true;
            }
        }
        return false;
    },

    viewImg: function (pojo, links, ext) {
        var centerPanel = Ext.getCmp('kimios-center-panel');
        var p = new kimios.util.DocumentViewer({
            pojo: pojo,
            links: links
        });
        centerPanel.add(p);
        centerPanel.setActiveTab(p);
    },

    copyToClipBoard: function (dataText) {
        var cpFunction = function copyToClipboardFF(sText) {
            try {
                netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
                var gClipboardHelper =
                    Components.classes["@mozilla.org/widget/clipboardhelper;1"]
                        .getService(Components.interfaces.nsIClipboardHelper);
                gClipboardHelper.copyString(sText);
            }
            catch (e) {
                alert("Please Check youf Firefox security settings, to enable ClipBoard Access.");

            }
        };

        var cpFunctionIe = function (sText) {
            window.clipboardData.setData('Text', sText);
            return false;
        };
        if (Ext.isIE) {
            cpFunctionIe(dataText);
        } else if (Ext.isGecko) {
            cpFunction(dataText);
        } else {
            alert("Unavailable feature.");
        }
    },
    viewDoc: function (pojo) {
        if (pojo.extension == 'pdf' || pojo.extension == 'PDF') {
            kimios.mask();
            var store = new DmsJsonStore({
                url: 'DocumentVersion',
                baseParams: {
                    action: 'pdfToImage',
                    uid: pojo.uid
                },
                fields: [ 'path', 'num' ],
                autoLoad: true
            });
            store.on('exception', function () {
                kimios.unmask();
            });
            store.on('load', function (store, record) {
                var links = [];
                Ext.each(record, function (rec, ind) {
                    links.push({
                        num: rec.data.num,
                        path: rec.data.path
                    });
                });
                kimios.viewImg(pojo, links, 'pdf');
                kimios.unmask();
            });
        } else {
            var links = [];
            links.push({
                num: 0,
                path: kimios.util.getDocumentLink(pojo.uid)
            });
            kimios.viewImg(pojo, links);
        }
    },
    barcode: function (pojo) {
        if (pojo.extension == 'pdf' || pojo.extension == 'PDF') {
            window.location.href = srcContextPath + '/Converter?sessionId=' + sessionUid + '&documentId=' + pojo.uid +
                '&converterImpl=org.kimios.kernel.converter.impl.BarcodeTransformer';
        }
    },

    date: function (timestamp) {
        return new Date(timestamp == null ? 0 : timestamp).format(kimios.lang('SimpleDateJSFormat'));
    },

    dateWithoutTime: function (timestamp) {
        return new Date(timestamp == null ? 0 : timestamp).format(kimios.lang('ShortJSDateFormat'));
    },

    util: {
        setTitle: function (path) {
            if (path == undefined || path == '' || path == '/')
                document.title = 'kimios Web Client';
            else
                document.title = 'kimios Web Client - ' + path;
        },
        getDocumentLink: function (uid) {
            return getBackEndUrl('DocumentVersion')
                + '&action=GetLastVersion&uid=' + uid;
        },
        getDocumentVersionLink: function (uid, versionUid) {
            return getBackEndUrl('DocumentVersion')
                + '&action=GetDocumentVersion&docUid=' + uid + '&verUid='
                + versionUid;
        },
        download: function (url) {

            if (!kimios.util.kdliframe) {
                kimios.util.kdliframe = Ext.DomHelper.createDom(
                    {
                        tag: 'iframe',
                        id: 'kdl-iframe'

                    });
                var first = true;
                var elem = new Ext.Element(kimios.util.kdliframe, false);
                elem.addListener('load', function () {
                    if (!first || (Ext.isIE || Ext.isGecko || Ext.isGecko2 || Ext.isGecko3)) {
                        alert('An error happend with Document Access. Please contact your administrator');
                    }
                    first = false;
                });
                Ext.getBody().appendChild(elem);
            }

            var it = Ext.get(kimios.util.kdliframe);
            kimios.util.kdliframe.setAttribute('src', url);
        }
    },

    ajaxRequest: function (url, params, successHandler, failureHandler) {
        kimios.mask();

        var fHandler = function (resp, opt) {
            kimios.unmask();
            kimios.MessageBox.exception({
                exception: 'HTTP error: ' + resp.status + ' '
                    + resp.statusText,
                stackTrace: resp.responseText
            });
        };

        var sHandler = function (resp, opt) {
            kimios.unmask();
            if (resp.responseText == '' || resp.responseText == '{"success":true}') {
                if (successHandler != null)
                    successHandler(resp);
            } else {
                var t = Ext.util.JSON.decode(resp.responseText);
                if (failureHandler) {
                    failureHandler(t);
                } else {
                    kimios.MessageBox.exception({
                        exception: t.exception,
                        stackTrace: t.trace
                    });
                }
            }
        };

        Ext.Ajax.request({
            url: getBackEndUrl(url),
            params: params,
            success: sHandler,
            failure: fHandler
        });

    },
    ajaxRequestWithAnswer : function(url, params, handler) {
        kimios.mask();

        var fHandler = function(resp, opt) {
            kimios.unmask();
            kimios.MessageBox.exception( {
                                             exception : 'HTTP error: ' + resp.status + ' '
                                                     + resp.statusText,
                                             stackTrace : resp.responseText
                                         });
        };

        var sHandler = function(resp, opt) {
            kimios.unmask();
            handler(resp);
        };

        Ext.Ajax.request( {
                              url : getBackEndUrl(url),
                              params : params,
                              success : sHandler,
                              failure : fHandler
                          });

    },
    ajaxSubmit: function (url, params, successHandler, failureHandler) {
        kimios.mask();

        var fHandler = function (form, action) {
            kimios.unmask();
            var resp = action.response;
            if (action.failureType == Ext.form.Action.CONNECT_FAILURE) {
                kimios.MessageBox.exception({
                    exception: 'HTTP error: ' + resp.status + " "
                        + resp.statusText,
                    stackTrace: resp.responseText
                });
            } else {
                if (failureHandler)
                    failureHandler(action.result);
                else {
                    kimios.MessageBox.exception({
                        exception: action.result.exception,
                        stackTrace: action.result.trace
                    });
                }
            }
        };

        var sHandler = function (form, action) {
            kimios.unmask();
            if (successHandler)
                successHandler(action.result);
        };

        return {
            url: getBackEndUrl(url),
            params: params,
            success: sHandler,
            failure: fHandler
        };
    },

    getToolsMenu: function () {
        return Ext.getCmp('kimios-tools');
    },

    getLanguageMenu: function () {
        return Ext.getCmp('kimios-language');
    },

    getBrowserLanguage: function () {
        var lang = navigator.language ? navigator.language
            : (navigator.userLanguage ? navigator.userLanguage
            : kimios.defaultLanguage);
        return lang.length > 2 ? lang.substring(0, 2).toLowerCase()
            : (lang.length == 2 ? lang : kimios.defaultLanguage);
    },

    getLanguage: function () {
        var defaultLang = kimios.Cookies
            .getCookie('kimios-web-client-language');
        if (defaultLang == null) {
            var languages = new Array('en', 'fr', 'es', 'pl');
            var lang = kimios.getBrowserLanguage();
            var found = false;
            for (var len = 0; len < languages.length; len++) {
                if (languages[len] == lang) {
                    found = true;
                    break;
                }
            }
            defaultLang = found == true ? lang : kimios.defaultLanguage;
        }
        return defaultLang;
    },

    lang: function (label) {
        return Ext.getCmp('kimios-viewport').i18n.getValue(label);
    },

    mask: function () {
        Ext.getCmp('kimios-viewport').mask.show();
    },

    unmask: function () {
        Ext.getCmp('kimios-viewport').mask.hide();
    },

    explorer: {
        getViewport: function () {
            return Ext.getCmp('kimios-viewport');
        },

        getWestPanel: function () {
            return Ext.getCmp('kimios-west-panel');
        },

        getNorthPanel: function () {
            return Ext.getCmp('kimios-north-panel');
        },

        getToolbar: function () {
            return Ext.getCmp('kimios-toolbar');
        },

        getTreePanel: function () {
            return Ext.getCmp('kimios-dm-entity-tree-panel');
        },

        getSearchRequestsPanel: function () {
            return Ext.getCmp('kimios-queries-panel');
        },

        getPublicSearchRequestsPanel: function () {
            return Ext.getCmp('kimios-queries-panel3');
        },

        getPublishedSearchRequestsPanel: function () {
            return Ext.getCmp('kimios-queries-panel2');
        },

        getBookmarksPanel: function () {
            return Ext.getCmp('kimios-bookmarks-panel');
        },

        getRecentItemsPanel: function () {
            return Ext.getCmp('kimios-recent-items-panel');
        },

        getAdvancedSearchPanel: function () {
            return Ext.getCmp('kimios-advanced-search-panel');
        },

        getTasksPanel: function () {
            if(bonitaEnabled){
                return Ext.getCmp('kimios-tasks-panel');
            }else {
                return Ext.getCmp('kimios-tasks-panel-legacy');
            }

        },
        getCartPanel: function () {
            return Ext.getCmp('kimios-cart');
        },

        getMainPanel: function () {
            return Ext.getCmp('kimios-center-panel');
        },

        getActivePanel: function () {
            return this.getMainPanel().getActiveTab();
        },

        addNewPanel: function(uid, type){
            var gridPanel = new kimios.explorer.DMEntityGridPanel({});
            Ext.getCmp('kimios-center-panel').add(gridPanel);
            Ext.getCmp('kimios-center-panel').setActiveTab(gridPanel);
            gridPanel.loadEntity({
                uid: uid,
                type: type
            });
        }
    },

    checkPassword: function (pwd) {
        if (pwd.length < 6) {
            Ext.MessageBox.alert(kimios.lang('InvalidPassword'), kimios
                .lang('PasswordLength'));
            return false;
        }
        if (pwd.indexOf(' ') > -1) {
            Ext.MessageBox.alert(kimios.lang('InvalidPassword'), kimios
                .lang('PasswordSpaces'));
            return false;
        }
        if (/[^A-Za-z0-9_\.@]+/.test(pwd)) {
            Ext.MessageBox.alert(kimios.lang('InvalidPassword'), kimios
                .lang('PasswordAuthorizedChar'));
            return false;
        }
        return true;
    },

    search: {},

    security: {},

    tasks: {},

    properties: {},

    picker: {},

    form: {},

    record: {},

    store: {},

    reporting: {},

    i18n: {},

    menu: {}
};

