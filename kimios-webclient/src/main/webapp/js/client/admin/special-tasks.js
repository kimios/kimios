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
Admin.Tasks = {
    getPanel: function () {
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            hideCollapseTool: true,
            title: kimios.lang('SpecialTasks'),
            margins: '3 0 3 3',
            cmargins: '3 3 3 3',
            rootVisible: false,
            autoSize: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode(),
            collapsible: true
        });

        var contextPanel = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit',
            margins: '3 3 3 0',
            bodyStyle: 'background-color: transparent;'
        });
        var rootNode = treePanel.getRootNode();

        rootNode.appendChild(Admin.Tasks.getSessionsNode(contextPanel));
        rootNode.appendChild(Admin.Tasks.getOwnersNode(contextPanel));
        rootNode.appendChild(Admin.Tasks.getDeadLockNode(contextPanel));
        rootNode.appendChild(Admin.Tasks.getReIndexNode(contextPanel));
        rootNode.appendChild(Admin.Tasks.getLogNode(contextPanel));
        return Admin.getPanel(kimios.lang('SpecialTasks'), 'specialtasks', treePanel,
            contextPanel);
    },

    getReIndexNode: function (contextPanel) {
        var node = new Ext.tree.TreeNode({
            text: kimios.lang('Reindex'),
            iconCls: 'reindex'
        });

        node.on('click', function (node, e) {
            var p = Admin.Tasks.getReIndexPanel(node);
            contextPanel.removeAll();
            contextPanel.add(p);
            contextPanel.doLayout();
        });

        return node;
    },

    getDeadLockNode: function (contextPanel) {
        var node = new Ext.tree.TreeNode({
            text: kimios.lang('ClearCheckoutLock'),
            iconCls: 'unlock'
        });

        node.on('click', function (node, e) {
            var deadLockStore = kimios.store.AdminStore.getDeadLockStore();
            deadLockStore.load({
                callback: function () {
                    var p = Admin.Tasks.getDeadLockPanel(deadLockStore, node);
                    contextPanel.removeAll();
                    contextPanel.add(p);
                    contextPanel.doLayout();
                }
            });
        });

        return node;
    },

    getOwnersNode: function (contextPanel) {
        var node = new Ext.tree.TreeNode({
            text: kimios.lang('ChangeOwner'),
            iconCls: 'change-owner'
        });

        node.on('click', function (node, e) {
            var p = Admin.Tasks.getOwnersPanel(node, contextPanel);
            contextPanel.removeAll();
            contextPanel.add(p);
            contextPanel.doLayout();
        });

        return node;
    },

    getSessionsNode: function (contextPanel) {
        var node = new Ext.tree.TreeNode({
            text: kimios.lang('SessionsManagement'),
            iconCls: 'session'
        });

        node.on('click', function (node, e) {
            var p = Admin.Tasks.getSessionsPanel(node);
            contextPanel.removeAll();
            contextPanel.add(p);
            contextPanel.doLayout();

        });

        return node;
    },

    getLogNode: function (contextPanel) {
        var node = new Ext.tree.TreeNode({
            text: kimios.lang('LoggingManagement'),
            iconCls: 'reporting'
        });

        node.on('click', function (node, e) {
            var p = Admin.Tasks.getLogPanel(node);
            contextPanel.removeAll();
            contextPanel.add(p);
            contextPanel.doLayout();

        });

        return node;
    },

    getLogPanel : function(node){

        var disableLogButton = new Ext.Button({
            id: 'admin-tasks-disable-btn',
            text: kimios.lang('DisableLog'),
            iconCls: 'reindex',
            disabled: false,
            handler: function () {
                //Ext.getCmp('admin-tasks-reindex-btn').disable();
                //Ext.getCmp('admin-tasks-reindex-pb').setVisible(true);
                kimios.request.AdminRequest.disableServiceLog();
            }
        });

        var enableLogButton = new Ext.Button({
            id: 'admin-tasks-enable-btn',
            text: kimios.lang('EnableLog'),
            iconCls: 'reindex',
            disabled: false,
            handler: function () {
                //Ext.getCmp('admin-tasks-reindex-btn').disable();
                //Ext.getCmp('admin-tasks-reindex-pb').setVisible(true);
                kimios.request.AdminRequest.enableServiceLog();
            }
        });


        var editHandler = function(e){

            if(e.value != e.originalValue){
                if(console){ console.log('editing ' + e.record.get('loggerName') + ' ==> '  + e.value + '(old: ' + e.originalValue + ')') };
                kimios.request.AdminRequest.setLoggerLevel(e.record.get('loggerName'), e.value, this);
            }
        }

        var logGrid = new Ext.grid.EditorGridPanel({
            store: kimios.store.AdminStore.getLoggerStore(),
            cm: new Ext.grid.ColumnModel([{
                header: kimios.lang('LoggerName'),
                dataIndex: 'loggerName',
                readOnly: true,
                sortable: true,
                width: 400,
                menuDisabled: false,
                align: 'left'
            }, {
                header: kimios.lang('LoggerLevel'),
                dataIndex: 'loggerLevel',
                width: 90,
                editable: true,
                editor: new Ext.form.TextField({}),
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left'
            }])
        });
        logGrid.on('afteredit', editHandler, logGrid);

        var p = new Ext.Panel({
            id: 'admin-tasks-logs-panel',
            title: node.text,
            layout: 'fit',
//            iconCls: 'reindex',
            bodyStyle: 'padding:5px;background-color:transparent;',
            tbar: [ disableLogButton, enableLogButton ],
            items: [ logGrid  ]
        });

        return p;
    },

    getReIndexPanel: function (node) {
        var getProgressThread = {
            run: function () {
                var progressStore = kimios.store.AdminStore.getReindexProgress();
                progressStore.load({
                    callback: function () {
                        var t = Ext.getCmp('admin-tasks-reindex-pb');
                        var b = Ext.getCmp('admin-tasks-reindex-btn');
                        try {
                            var percent = progressStore.getAt(0).get('percent');
                            if (t != undefined)
                                t.updateProgress(percent / 100, percent + '%');
                            if (percent == -1) {
                                if (t != undefined) {
                                    t.updateProgress(1, kimios.lang('Completed'));
                                    b.enable();
                                }
                                Ext.TaskMgr.stopAll();
                            }
                        } catch (e) {
                            if (t != undefined) {
                                t.updateProgress(0, kimios.lang('ErrorWhileIndexing'));
                                Ext.TaskMgr.stopAll();
                            }
                        }
                        var p = Ext.getCmp('admin-tasks-reindex-panel');
                        if (p != undefined)
                            p.doLayout();
                    }
                });
            },
            interval: 1000
            // check progression each second
        };

        var progressStore = kimios.store.AdminStore.getReindexProgress();
        progressStore.load({
            callback: function (records, options, success) {
                if (records[0] == undefined) {
                    kimios.MessageBox.exception({
                        stackTrace: kimios.lang('ErrorWhileIndexing')
                    });
                } else {
                    var percent = records[0].data.percent;
                    if (percent == -1) {
                        Ext.getCmp('admin-tasks-reindex-pb').setVisible(false);
                        Ext.getCmp('admin-tasks-reindex-btn').enable();
                    } else {
                        Ext.getCmp('admin-tasks-reindex-btn').disable();
                        Ext.getCmp('admin-tasks-reindex-pb').setVisible(true);
                        Ext.TaskMgr.start(getProgressThread);
                    }
                }
                p.doLayout();
            }
        });

        var reIndexButton = new Ext.Button({
            id: 'admin-tasks-reindex-btn',
            text: kimios.lang('Reindex'),
            iconCls: 'reindex',
            disabled: true,
            handler: function () {
                Ext.getCmp('admin-tasks-reindex-btn').disable();
                Ext.getCmp('admin-tasks-reindex-pb').setVisible(true);
                kimios.request.AdminRequest.reindex(getProgressThread);
            }
        });

        var progressBar = new Ext.ProgressBar({
            id: 'admin-tasks-reindex-pb',
            hidden: true,
            width: 150
        });

        var p = new Ext.Panel({
            id: 'admin-tasks-reindex-panel',
            title: node.text,
            layout: 'hbox',
//            iconCls: 'reindex',
            bodyStyle: 'padding:5px;background-color:transparent;',
            tbar: [ reIndexButton ],
            items: [ progressBar ]
        });

        return p;
    },

    getDeadLockPanel: function (store, node) {
        var clearButton = new Ext.Button({
            text: kimios.lang('Unlock'),
            tooltip: kimios.lang('Unlock'),
            iconCls: 'unlock',
            disabled: true,
            handler: function () {
                kimios.request.AdminRequest.clearDeadLock(store, sm
                    .getSelections());
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            listeners: {
                selectionchange: function (sm) {
                    var count = sm.getCount();
                    if (count > 0) {
                        clearButton.enable();
                    } else {
                        clearButton.disable();
                    }
                }
            }
        });

        var cm = new Ext.grid.ColumnModel([
            sm,
            {
                width: 16,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'icon',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'lock';
                }
            },
            {
                header: kimios.lang("Document"),
                sortable: true,
                menuDisabled: true,
                dataIndex: 'path'
            },
            {
                header: kimios.lang("UserUid"),
                sortable: true,
                menuDisabled: true,
                dataIndex: 'checkoutUser',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    return value + '@' + record.get('checkoutUserSource');
                }
            }, {
                header: kimios.lang("Date"),
                sortable: true,
                menuDisabled: true,
                dataIndex: 'checkoutDate',
                renderer: function (value) {
                    return kimios.date(value);
                }
            } ]);

        var deadLockPanel = new Ext.grid.GridPanel({
            store: store,
            stripeRows: true,
            region: 'center',
            loadMask: true,
            border: false,
            // hideHeaders: true,
            cm: cm,
            sm: sm,
            viewConfig: {
                forceFit: true
            },
            columnLines: false,
            buttonAlign: 'left',
            tbar: [ clearButton ]
        });

        var p = new Ext.Panel({
            id: 'admin-tasks-dead-lock-panel',
            title: node.text,
//            iconCls: 'unlock',
            layout: 'border',
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        store.reload();
                    }
                }
            ]
        });
        p.add(deadLockPanel);

        return p;
    },

    getOwnersPanel: function (node, contextPanel) {
        var getIconByType = function (type) {
            switch (type) {
                case 1:
                    return 'dm-entity-tab-properties-workspace';
                case 2:
                    return 'dm-entity-tab-properties-folder';
                case 3:
                    return 'dm-entity-tab-properties-document';
                default:
                    return 'home';
            }
        };

        var getEntityGrid = function (node) {

            var changeOwnerButton = new Ext.Button({
                text: kimios.lang('ChangeOwner'),
                iconCls: 'admin-user-tree-node',
                disabled: true,
                handler: function () {
                    var el = new kimios.picker.SecurityEntityPicker({
                        title: changeOwnerButton.getText(),
                        iconCls: 'admin-user-tree-node',
                        entityMode: 'user',
                        singleSelect: true
                    });
                    el.on('entitySelected', function (records) {
                        var r = records[0];
                        kimios.request.AdminRequest.changeOwner(grid
                            .getStore(), grid.getSelectionModel()
                            .getSelections(), r.data.uid, r.data.source);
                    });
                    el.show();
                }
            });

            var sm = new Ext.grid.CheckboxSelectionModel({
                listeners: {
                    selectionchange: function (sm) {
                        var count = sm.getCount();
                        if (count > 0) {
                            changeOwnerButton.enable();
                        } else {
                            changeOwnerButton.disable();
                        }
                    }
                }
            });

            var grid = new Ext.grid.GridPanel({
                sm: sm,
                loadMask: true,
                title: node.text,
                hideHeaders: true,
                iconCls: getIconByType(node.attributes.type),
                cm: new Ext.grid.ColumnModel(
                    [
                        sm,
                        {
                            align: 'center',
                            readOnly: true,
                            width: 18,
                            hidden: false,
                            sortable: false,
                            hideable: false,
                            fixed: true,
                            resizable: false,
                            menuDisabled: true,
                            renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                                metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
                            }
                        },
                        {
                            header: kimios.lang('Entity'),
                            sortable: true,
                            dataIndex: 'name'
                        },
                        {
                            header: kimios.lang('Owner'),
                            sortable: true,
                            dataIndex: 'owner',
                            renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                                return value + '@'
                                    + record.get('ownerSource');
                            }
                        } ]),
                viewConfig: {
                    forceFit: true,
                    scrollOffset: 0
                },
                autoScroll: true,
                stripeRows: true,
                tbar: [ changeOwnerButton ],
                store: new DmsJsonStore({
                    id: 'mainDmEntityStore',
                    url: 'DmsEntity',
                    root: 'list',
                    fields: kimios.record.dmEntityRecord,
                    baseParams: {
                        action: 'getEntities',
                        dmEntityUid: (node ? node.attributes.dmEntityUid : ''),
                        dmEntityType: (node ? node.attributes.type : '')
                    },
                    autoLoad: true
                })
            });
            return grid;
        };

        var panel = new Ext.Panel({
            title: node.text,
//            iconCls: 'change-owner',
            layout: 'border',
            tools: [
                {
                    id: 'refresh',
                    handler: function () {
                        var p = Admin.Tasks.getOwnersPanel(node, contextPanel);
                        contextPanel.removeAll();
                        contextPanel.add(p);
                        contextPanel.doLayout();
                    }
                }
            ]
        });

        var entityExplorer = new kimios.util.DMEntityTree({
            split: true,
            width: 150,
            region: 'west',
            // margins: '3 0 3 3',
            border: false,
            animate: false,
            bodyStyle: 'background-color:transparent;'
        });

        var container = new Ext.Panel({
            region: 'center',
            layout: 'fit',
            split: true,
            margins: '5 5 5 0',
            border: false,
            bodyStyle: 'background-color:transparent;'
        });

        panel.add(entityExplorer);
        panel.add(container);

        container.add(getEntityGrid(entityExplorer.getRootNode()));

        entityExplorer.on('click', function (node, e) {
            node.expand();
            container.removeAll();
            container.add(getEntityGrid(node));
            container.doLayout();
        });

        return panel;
    },

    getSessionsPanel: function (node) {
        var getSessionsDetailsPanel = function (record) {
            var kickButton = new Ext.Button({
                text: kimios.lang('KickUser'),
                iconCls: 'exit',
                handler: function () {
                    kimios.request.AdminRequest.disconnectUser(
                        connectedUsersStore, Ext.getCmp(
                                'admin-tasks-sessions-details-panel')
                            .getStore(), record.data.uid,
                        record.data.source);
                }
            });

            var removeSessionButton = new Ext.Button({
                text: kimios.lang('DestroySessions'),
                iconCls: 'delete',
                disabled: true,
                handler: function () {
                    kimios.request.AdminRequest.eraseSessions(
                        connectedUsersStore, sessionsPanel.getStore(), sm
                            .getSelections());
                }
            });

            var sm = new Ext.grid.CheckboxSelectionModel({
                listeners: {
                    selectionchange: function (sm) {
                        if (sm.getCount() > 0) {
                            removeSessionButton.enable();
                        } else {
                            removeSessionButton.disable();
                        }
                    }
                }
            });

            var sessionsPanel = new Ext.grid.GridPanel(
                {
                    id: 'admin-tasks-sessions-details-panel',
                    loadMask: true,
                    stripeRows: true,
                    viewConfig: {
                        forceFit: true
                    },
                    columnLines: false,
                    title: record.data.uid + '@' + record.data.source,
//                    iconCls: 'admin-user-tree-node',
                    tbar: [ kickButton, removeSessionButton ],
                    sm: sm,
                    cm: new Ext.grid.ColumnModel(
                        [
                            sm,
                            {
                                width: 16,
                                fixed: true,
                                editable: false,
                                sortable: false,
                                menuDisabled: true,
                                dataIndex: 'icon',
                                renderer: function (value, metaData, record) {
                                    metaData.css = (record
                                        .get('sessionUid') == sessionUid ? 'admin-tasks-current-session'
                                        : 'attach');
                                }
                            },
                            {
                                header: kimios.lang('UserLastLogin'),
                                sortable: true,
                                width: 120,
                                fixed: true,
                                dataIndex: 'lastUse',
                                menuDisabled: true,
                                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                                    if (record.get('sessionUid') == sessionUid) {
                                        return kimios.lang('Now');
                                    }
                                    else return kimios.date(value);
                                }
                            },
                            {
                                header: "ID",
                                sortable: true,
                                menuDisabled: true,
                                dataIndex: 'sessionUid',
                                renderer: function (value) {
                                    return '<span style="font-size:10px;">' + value + '</span>';
                                }
                            } ]),
                    store: kimios.store.AdminStore.getSessionsStore(
                        record, true)
                });

            return sessionsPanel;
        };

        var connectedUsersPanel = new Ext.tree.TreePanel({
            root: new Ext.tree.TreeNode({
                text: kimios.lang('ConnectedUsers')
            }),
            animate: false,
            rootVisible: false,
            split: true,
            border: false,
            useArrows: true,
            bodyStyle: 'background-color:transparent;',
            width: 150,
            region: 'west',
            loadMask: true,
            autoScroll: true
        });

        var connectedUsersStore = kimios.store.AdminStore
            .getConnectedUsersStore(true);
        connectedUsersStore.on('load', function (st, recs) {
            var rNode = connectedUsersPanel.getRootNode();

            while (rNode.hasChildNodes()) {
                rNode.removeChild(rNode.item(0));
            }

            Ext.each(recs, function (rec, ind) {

                var node = new Ext.tree.TreeNode({
                    text: rec.data.uid,
                    iconCls: 'admin-user-tree-node'
                });
                node.attributes.uid = rec.data.uid;
                node.attributes.source = rec.data.source;

                node.on('click', function (node, e) {
                    var record = {};
                    record.data = {};
                    record.data.uid = node.attributes.uid;
                    record.data.source = node.attributes.source;
                    sessionsContainer.removeAll();
                    sessionsContainer.add(getSessionsDetailsPanel(record));
                    sessionsContainer.doLayout();
                });

                var found = false;
                rNode.eachChild(function (domainNode) {
                    if (domainNode.text == rec.data.source) {
                        domainNode.appendChild(node);
                        rNode.appendChild(domainNode);
                        found = true;
                        return false;
                    }
                });
                if (!found) {
                    var domainNode = new Ext.tree.TreeNode({
                        text: rec.data.source,
                        allowChildren: true,
                        iconCls: 'admin-acc-domain'
                    });
                    domainNode.appendChild(node);
                    rNode.appendChild(domainNode);
                }
            });
            rNode.expandChildNodes();
        });

        var sessionsContainer = new Ext.Panel({
            split: true,
            margins: '5 5 5 0',
            region: 'center',
            layout: 'fit',
            border: false,
            bodyStyle: 'background-color:transparent;'
        });

        var record = {};
        record.data = {};
        record.data.uid = currentUser;
        record.data.source = currentSource;
        sessionsContainer.add(getSessionsDetailsPanel(record));

        var p = new Ext.Panel({
            id: 'admin-tasks-sessions-panel',
            title: node.text,
//            iconCls: 'session',
            layout: 'border',
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        connectedUsersStore.reload();
                        Ext.getCmp('admin-tasks-sessions-details-panel').getStore()
                            .reload();
                    }
                }
            ],
            items: [ connectedUsersPanel, sessionsContainer ]
        });

        return p;
    }
};
