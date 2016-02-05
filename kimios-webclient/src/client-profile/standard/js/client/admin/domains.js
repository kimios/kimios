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
Admin.Domains = {

    getPanel: function () {
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            collapsible: true,
            hideCollapseTool: true,
            title: kimios.lang('AuthenticationSources'),
            margins: '3 0 3 3',
            cmargins: '3 3 3 3',
            rootVisible: false,
            autoSize: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode(),
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        domainsListStore.reload();
                    }
                },
                {
                    id: 'plus',
                    handler: function (event, toolEl, panel) {
                        Admin.Domains.setContextPanel(domainsListStore, contextPanel, [
                            Admin.Domains.getParametersPanel(contextPanel, domainsListStore)
                        ], 0);
                    }
                }
            ]
        });

        var contextPanel = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit',
            margins: '3 3 3 0',
            bodyStyle: 'background-color: transparent;'
        });
        var node = treePanel.getRootNode();

        var domainsListStore = kimios.store.AdminStore.getDomainsStore();


        domainsListStore.on('load', function (st, domainsListRecord) {
            while (node.hasChildNodes()) {
                node.removeChild(node.item(0));
            }
            Ext.each(domainsListRecord, function (domainRecord, ind) {
                var domainNode = new Ext.tree.TreeNode({
                    text: domainRecord.data.name,
                    allowChildren: true,
                    iconCls: 'admin-domain-tree-node'
                });
                domainNode.on('contextMenu', function (node, e) {
                    node.select();
                    var contextMenu = new Ext.menu.Menu({
                        shadow: false,
                        items: [
                            {
                                text: kimios.lang('Properties'),
                                iconCls: 'qaction-properties',
                                handler: function () {
                                    Admin.Domains.setContextPanel(domainsListStore, contextPanel, [
                                        Admin.Domains.getParametersPanel(contextPanel, domainsListStore, domainRecord),
                                        Admin.Domains.getUsersPanel(domainRecord),
                                        Admin.Domains.getGroupsPanel(domainRecord)
                                    ], 0, domainRecord);
                                }
                            },
                            {
                                text: kimios.lang('Delete'),
                                iconCls: 'trash',
                                handler: function () {
                                    kimios.request.AdminRequest.removeDomain(domainsListStore, domainRecord, contextPanel);
                                }
                            }
                        ]
                    });
                    contextMenu.showAt(e.getXY());
                });
                domainNode.on('click', function () {
                    Admin.Domains.setContextPanel(domainsListStore, contextPanel, [
                        Admin.Domains.getParametersPanel(contextPanel, domainsListStore, domainRecord),
                        Admin.Domains.getUsersPanel(domainRecord),
                        Admin.Domains.getGroupsPanel(domainRecord)
                    ], 0, domainRecord);
                });
                node.appendChild(domainNode);
            });
        });

        var panel = Admin.getPanel(kimios.lang('AuthenticationSources'), 'admin-acc-domain', treePanel, contextPanel);
        domainsListStore.load();
        panel.doLayout();
        return panel;
    },

    setContextPanel: function (domainsListStore, contextPanel, subPanels, activeTab, domainRecord) {
        var tabPanel = new Ext.TabPanel({
            id: 'admin-domains-tab-panel',
            activeTab: activeTab,
            border: false,
            bodyStyle: 'background-color:transparent;'
        });
        for (var i = 0; i < subPanels.length; i++) {
            tabPanel.add(subPanels[i]);
        }

        contextPanel.removeAll();
        contextPanel.add(new Ext.Panel({
            id: 'admin-domains-panel',
            title: (domainRecord ? domainRecord.data.name : kimios.lang('NewDomain')),
//            iconCls: 'admin-acc-domain',
            bodyStyle: 'background-color:transparent;',
            layout: 'fit',
            items: [tabPanel],
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        if (domainRecord) {
                            Admin.Domains.setContextPanel(domainsListStore, contextPanel, [
                                Admin.Domains.getParametersPanel(contextPanel, domainsListStore, domainRecord),
                                Admin.Domains.getUsersPanel(domainRecord),
                                Admin.Domains.getGroupsPanel(domainRecord)
                            ], 0, domainRecord);
                        }
                    }
                }
            ]
        }));

        contextPanel.doLayout();
    },

    getParametersPanel: function (contextPanel, store, domainRecord) {
        var deleteButton = new Ext.Button({
            text: kimios.lang('Delete'),
            tooltip: kimios.lang('Delete'),
            iconCls: 'trash',
            disabled: (domainRecord ? false : true),
            handler: function () {
                kimios.request.AdminRequest.removeDomain(store, domainRecord, contextPanel);
            }
        });

        var saveButton = new Ext.Button({
            text: kimios.lang('Save'),
            tooltip: kimios.lang('Save'),
            iconCls: 'save',
            handler: function () {
                kimios.request.AdminRequest.saveDomain(parametersPanel, store, contextPanel, parametersGrid.getStore(), domainTextField.getValue(), domainRecord);
            }
        });

        var parametersPanel = new kimios.FormPanel({
            title: kimios.lang('Parameters'),
            iconCls: 'admin-acc-domain-prop',
            labelWidth: 120,
            defaults: {
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 10px'
            },
            bodyStyle: 'padding: 5px;background-color:transparent;',
            bbar: ['->', saveButton, deleteButton],
            monitorValid: true
        });

        var hiddenField = new Ext.form.Hidden({
            name: 'name',
            id: 'hidden-domain-name',
            value: (domainRecord ? domainRecord.data.name : '')
        });


        var enableSsoField = new Ext.form.Checkbox({
            name: 'enableSso',
            id:'enable-sso',
            fieldLabel: kimios.lang('AuthenticationSourceSSO'),
            checked: (domainRecord ? domainRecord.data.enableSso : false)
        });

        var enableMailCheckField = new Ext.form.Checkbox({
            name: 'enableMailCheck',
            id:'enable-mail-check',
            fieldLabel: kimios.lang('AuthenticationMailCheck'),
            checked: (domainRecord ? domainRecord.data.enableMailCheck : false)
        });

        var domainTextField = new Ext.form.TextField({
            anchor: '100%',
            name: 'newName',
            fieldLabel: kimios.lang('AuthenticationSource'),
            value: (domainRecord ? domainRecord.data.name : ''),
            disabled: (domainRecord ? true : false)
        });

        var domainTypeStore = kimios.store.AdminStore.getDomainTypesStore();

        var parametersGrid = new Ext.grid.PropertyGrid({
            autoScroll: true,
            title: kimios.lang('Parameters'),
            source: {},
            loadMask: true,
            hideHeaders: true,
            viewConfig: {
                forceFit: true
            }
        });

        var classNameCombo = new Ext.form.ComboBox({
            anchor: '100%',
            name: 'className',
            fieldLabel: kimios.lang('SourceType'),
            displayField: 'className',
            valueField: 'className',
            hiddenName: 'className',
            editable: false,
            triggerAction: 'all',
            disabled: (domainRecord ? true : false),
            value: (domainRecord ? domainRecord.data.className : ''),
            store: domainTypeStore
        });

        if (domainRecord) {
            var dReader = new Ext.data.JsonReader();
            var domainDetailsStore = new Ext.data.Store({});
            Ext.apply(domainDetailsStore, {
                reader: dReader,
                proxy: new Ext.data.HttpProxy({
                    url: getBackEndUrl('Admin')
                })
            });
            Ext.apply(domainDetailsStore, {
                baseParams: {
                    action: 'domainDetails',
                    className: domainRecord.data.className,
                    name: domainRecord.data.name,
                    newName: domainRecord.data.newName
                }
            });
            domainDetailsStore.on('load', function (st, recs) {
                parametersGrid.store.removeAll();
                var rec = st.getAt(0);
                rec.fields.each(function (f, ind) {
                    var propRecord = new Ext.grid.PropertyRecord({
                        name: f.name,
                        value: (rec ? rec.get(f.name) : '')
                    });
                    parametersGrid.store.addSorted(propRecord);
                });
            });
            parametersGrid.store.removeAll();
            domainDetailsStore.load();
        } else {
            classNameCombo.on('select', function (combo, record, index) {
                parametersGrid.store.removeAll();
                var domainFieldsStore = kimios.store.AdminStore.getDomainFieldsStore(combo.value);
                domainFieldsStore.on('load', function (st, recs) {
                    Ext.each(recs, function (r, ind) {
                        var propRecord = new Ext.grid.PropertyRecord({
                            name: r.data.name,
                            value: ''
                        });
                        parametersGrid.store.addSorted(propRecord);
                    });
                });
            });
        }
        parametersPanel.add(hiddenField);
        parametersPanel.add(domainTextField);
        parametersPanel.add(classNameCombo);
        parametersPanel.add(enableMailCheckField);
        parametersPanel.add(enableSsoField);
        parametersPanel.add(new Ext.Panel({
            anchor: '100% -52',
            viewConfig: {
                forceFit: true
            },
            layout: 'fit',
            border: false,
            items: [parametersGrid]
        }));
        domainTextField.focus(true, true);
        return parametersPanel;
    },

    getUsersPanel: function (record) {


        var getDetailsPanel = function (ctn, sm, rowIndex, usersListStore, domainRecord, usersDetailsRecord) {

            var tabs = new Ext.TabPanel({
                bodyStyle: 'background-color:transparent;',
                border: false,
                //                id: 'admin-domains-users-details-tabs',
                activeTab: 0,
                enableTabScroll: true
            });
            tabs.add(getParametersTab(domainRecord, usersListStore, usersDetailsRecord, ctn, sm, rowIndex));
            if (usersDetailsRecord) {
                tabs.add(getLinkedGroupsTab(domainRecord, usersDetailsRecord));
                tabs.add(getAssignedRolesTab(usersDetailsRecord.data.uid, domainRecord.data.name, domainRecord.data.className));
            }
            return tabs;
        };

        var getParametersTab = function (domainRecord, usersListStore, usersDetailsRecord, ctn, sm, rowIndex) {
            var uidTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('UserUid'),
                name: 'uid',
                disabled: (usersDetailsRecord ? true : false),
                value: (usersDetailsRecord ? usersDetailsRecord.data.uid : '')
            });

            var firstNameTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('Firstname'),
                name: 'firstName',
                value: (usersDetailsRecord ? usersDetailsRecord.data.firstName : '')
            });

            var lastNameTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('Lastname'),
                name: 'lastName',
                value: (usersDetailsRecord ? usersDetailsRecord.data.lastName : '')
            });

            var phoneNumberTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('PhoneNumber'),
                name: 'phoneNumber',
                value: (usersDetailsRecord ? usersDetailsRecord.data.phoneNumber : '')
            });


            var detailsItems = [
                uidTextField,
                firstNameTextField,
                lastNameTextField,
                phoneNumberTextField,
                {
                    id: 'kimios-admin-domains-password',
                    name: 'password',
                    inputType: 'password',
                    fieldLabel: kimios.lang('DMSAuthPasswordLabel')
                }, {
                    id: 'kimios-admin-domains-password2',
                    name: 'confirm-password',
                    inputType: 'password',
                    fieldLabel: kimios.lang('ConfirmNewPassword')
                }, {
                    fieldLabel: kimios.lang('UserMail'),
                    name: 'mail',
                    vtype: 'email',
                    value: (usersDetailsRecord ? usersDetailsRecord.data.mail : '')
                }];

            if(usersDetailsRecord && usersDetailsRecord.data && usersDetailsRecord.data.emails && usersDetailsRecord.data.emails.length > 0) {
                for (var idx = 0; idx < usersDetailsRecord.data.emails.length; idx++) {
                    if(usersDetailsRecord.data.mail != usersDetailsRecord.data.emails[idx]){
                        detailsItems.push({
                            fieldLabel: kimios.lang('UserMail') + ' ' + (idx+1),
                            name: 'mail' + idx,
                            vtype: 'email',
                            value: usersDetailsRecord.data.emails[idx]
                        })
                    }
                }
            }


            detailsItems.push({
                xtype: 'hidden',
                name: 'authenticationSourceName',
                value: domainRecord.data.name
            }, {
                fieldLabel: kimios.lang('UserEnabled'),
                xtype: 'checkbox',
                name: 'enabled',
                disabled: (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource' ? false : true),
                checked: (usersDetailsRecord ? usersDetailsRecord.data.enabled : 'false')
            })
            var form = new kimios.FormPanel({
                title: kimios.lang('User'),
                iconCls: 'admin-user-tree-node',
                autoScroll: true,
                monitorValid: true,
                labelWidth: 200,
                bodyStyle: 'padding:5px;background-color:transparent;',
                defaults: {
                    anchor: '-20',
                    selectOnFocus: true,
                    style: 'font-size: 10px',
                    labelStyle: 'font-size: 10px',
                    height: 18
                },
                defaultType: 'textfield',
                items: detailsItems,
                bbar: ['->', {
                    text: kimios.lang('Save'),
                    iconCls: 'save',
                    handler: function () {

                        var p1 = Ext.getCmp('kimios-admin-domains-password').getValue();
                        var p2 = Ext.getCmp('kimios-admin-domains-password2').getValue();
                        if(p1.length > 0 || p2.length > 0){
                            var match = p1 == p2;
                            var empty = p1 == '' || p2 == '';
                            if (empty || !match) {
                                Ext.MessageBox.alert(kimios.lang('InvalidPassword'), kimios.lang('NoPasswordMatchJS'));
                                return;
                            }
                            if(!kimios.checkPassword(p1))
                                return;
                        }
                        kimios.request.AdminRequest.saveUser(ctn, sm, rowIndex, usersListStore, form, usersDetailsRecord);
                    }
                }]
            });

            if (usersDetailsRecord) {
                form.add({
                    xtype: 'hidden',
                    name: 'uid',
                    value: usersDetailsRecord.data.uid
                });
                firstNameTextField.focus(true, true);
            } else {
                uidTextField.focus(true, true);
            }
            return form;
        };

        var getLinkedGroupsTab = function (record, userRecord) {
            var userGroupsStore = kimios.store.AdminStore.getUserGroupsStore(userRecord.data.uid, record.data.name);

            var addButton = new Ext.Button({
                text: kimios.lang('Add'),
                iconCls: 'add-group-icon',
                disabled: (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource' ? false : true),
                handler: function () {
                    var el = new kimios.picker.SecurityEntityPicker({
                        title: kimios.lang('LinkedGroups'),
                        iconCls: 'add-group-icon',
                        entityMode: 'group',
                        lockedSourceValue: record.data.name
                    });
                    el.on('entitySelected', function (usersRecords, groupsRecords, _pickerWindow) {
                        kimios.request.AdminRequest.saveUserToGroup(userGroupsStore, userRecord.data.uid, groupsRecords);
                        _pickerWindow.close();
                    });
                    el.show();
                }
            });

            var removeButton = new Ext.Button({
                text: kimios.lang('Remove'),
                iconCls: 'delete-group',
                disabled: true,
                handler: function () {
                    kimios.request.AdminRequest.removeUserToGroup(userGroupsStore, userRecord.data.uid, sm.getSelections());
                }
            });

            var sm = new Ext.grid.CheckboxSelectionModel({
                checkOnly: true,
                listeners: {
                    selectionchange: function (sm) {
                        var count = sm.getCount();
                        if (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource') {
                            if (count > 0) {
                                removeButton.enable();
                            } else {
                                removeButton.disable();
                            }
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
                        metaData.css = 'admin-group-tree-node';
                    }
                }, {
                    id: 'gid',
                    header: "#",
                    width: 80,
                    sortable: true,
                    dataIndex: 'gid',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' + record.get('name') + '</span>';
                    }
                }
            ]);

            var groupsGrid = new Ext.grid.GridPanel({
                title: kimios.lang('LinkedGroups'),
                iconCls: 'group-icon',
                autoScroll: true,
                loadMask: true,
                bodyStyle: 'background-color:transparent;',
                store: userGroupsStore,
                stripeRows: true,
                cm: cm,
                sm: sm,
                viewConfig: {
                    forceFit: true
                },
                hideHeaders: true,
                columnLines: false,
                tbar: [
                    addButton, '-',
                    removeButton
                ]
            });
            groupsGrid.on('activate', function () {
                userGroupsStore.load();
            });
            return groupsGrid;
        };

        var getAssignedRolesTab = function (userName, userSource, className) {
            var userRolesStore = kimios.store.AdminStore.getUserRolesStore(userName, userSource);

            var removeButton = new Ext.Button({
                text: kimios.lang('Remove'),
                iconCls: 'remove',
                disabled: true,
                handler: function () {
                    kimios.request.AdminRequest.removeRole(userRolesStore, rolesGrid.getSelectionModel().getSelections());
                }
            });

            var sm = new Ext.grid.CheckboxSelectionModel({
                checkOnly: true,
                listeners: {
                    selectionchange: function (sm) {
                        var count = sm.getCount();
                        if (className == 'org.kimios.kernel.user.impl.HAuthenticationSource') {
                            if (count > 0) {
                                removeButton.enable();
                            } else {
                                removeButton.disable();
                            }
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
                        metaData.css = 'admin-acc-role';
                    }
                },
                {
                    id: 'role',
                    header: kimios.lang('Roles'),
                    sortable: true,
                    dataIndex: 'role',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        switch (value) {
                            case 1:
                                return kimios.lang('WorkspaceCreator');
                            case 2:
                                return kimios.lang('StudioUser');
                            case 3:
                                return kimios.lang('Admin');
                            case 4:
                                return kimios.lang('MetaFeedAccessDenied');
                            case 5:
                                return kimios.lang('ReportingUser');
                            default:
                                return '';
                        }
                    }
                }
            ]);

            var rolesGrid = new Ext.grid.GridPanel({
                title: kimios.lang('AssignedRoles'),
                iconCls: 'admin-acc-role',
                autoScroll: true,
                loadMask: true,
                bodyStyle: 'background-color:transparent;',
                id: 'admin-domains-user-roles-panel',
                store: userRolesStore,
                stripeRows: true,
                sm: sm,
                cm: cm,
                viewConfig: {
                    forceFit: true
                },
                hideHeaders: true,
                columnLines: false,
                tbar: [
                    removeButton]
            });

            rolesGrid.on('activate', function () {
                userRolesStore.load();
            });

            return rolesGrid;
        };

        var usersListStore = kimios.store.AdminStore.getUsersStore(record.data.name);

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            tooltip: kimios.lang('Add'),
            iconCls: 'add-user-icon',
            disabled: (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource' ? false : true),
            handler: function () {
                var ctn = Ext.getCmp('admin-domains-users-details-container');
                ctn.removeAll();
                ctn.add(getDetailsPanel(ctn, sm, -1, usersListStore, record));
                ctn.getLayout().setActiveItem(0);
                ctn.expand();
                ctn.setVisible(true);
                p.doLayout();
            }
        });

        var removeButton = new Ext.Button({
            text: kimios.lang('Remove'),
            tooltip: kimios.lang('Remove'),
            iconCls: 'delete-user',
            disabled: true,
            handler: function () {
                kimios.request.AdminRequest.removeUser(usersListStore, sm.getSelections());
            }
        });

        var clearButton = new Ext.Button({
            text: kimios.lang('Clear'),
            tooltip: kimios.lang('Clear'),
            iconCls: 'add-user-icon',
            handler: function () {
                var ctn = Ext.getCmp('admin-domains-users-details-container');
                ctn.add(getDetailsPanel(ctn, sm, -1, usersListStore, record));
                ctn.getLayout().setActiveItem(0);
                ctn.expand();
                ctn.setVisible(true);
                p.doLayout();
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            //            checkOnly : true,
            listeners: {
                selectionchange: function (sm) {
                    var count = sm.getCount();
                    if (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource') {
                        if (count > 0) {
                            removeButton.enable();
                        } else {
                            removeButton.disable();
                        }
                    }
                }
            }
        });

        var cm = new Ext.grid.ColumnModel([
            {
                width: 16,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'icon',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'admin-user-tree-node';
                }
            },
            {
                id: 'uid',
                sortable: true,
                dataIndex: 'uid',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' + record.get('name') + '</span>';
                }
            },
            {
                width: 20,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'enabled',
                renderer: function (enabled, metaData) {
                    if (enabled)
                        metaData.css = 'asterisk';
                }
            }
        ]);

        var panel = new Ext.grid.GridPanel({
            region: 'center',
            store: usersListStore,
            stripeRows: true,
            loadMask: true,
            border: false,
            bodyStyle: 'background-color:transparent;',
            cm: cm,
            sm: sm,
            viewConfig: {
                forceFit: true
            },
            hideHeaders: true,
            columnLines: false,
            buttonAlign: 'left',
            tbar: [
                addButton,
                removeButton
            ]
        });

        var p = new Ext.Panel({
            id: 'admin-domains-users-panel',
            title: kimios.lang('Users'),
            iconCls: 'admin-user-tree-node',
            layout: 'border',
            bodyStyle: 'background-color:transparent;',
            items: [panel, new Ext.Panel({
                region: 'south',
                id: 'admin-domains-users-details-container',
                border: false,
                animCollapse: false,
                collapsed: true,
                hidden: true,
                layout: new Ext.layout.CardLayout({
                    layoutOnCardChange: true
                }),
                height: 250
            })],
            listeners: {
                activate: function () {
                    usersListStore.load({
                        callback: function (a, b) {
                            var ctn = Ext.getCmp('admin-domains-users-details-container');
                            ctn.collapse();
                            ctn.setVisible(false);
                            p.doLayout();
                        }
                    });
                }
            }
        });

        panel.on('rowclick', function (grid, rowIndex, e) {
            var ctn = Ext.getCmp('admin-domains-users-details-container');
            if (sm.getSelections().length == 1) {
                ctn.removeAll(); // admin infinite loop fix
                ctn.add(getDetailsPanel(ctn, sm, rowIndex, usersListStore, record, sm.getSelected()));
                ctn.getLayout().setActiveItem(0);
                ctn.expand();
                ctn.setVisible(true);
            } else {
                ctn.collapse();
                ctn.setVisible(false);
            }
            p.doLayout();
        });
        return p;
    },


    getGroupsPanel: function (record) {
        var getDetailsPanel = function (ctn, sm, rowIndex, groupsListStore, domainRecord, groupsDetailsRecord) {
            var tabs = new Ext.TabPanel({
                bodyStyle: 'background-color: transparent;',
                border: false,
                region: 'south',
                activeTab: 0,
                enableTabScroll: true
            });
            tabs.add(getParametersTab(domainRecord, groupsListStore, groupsDetailsRecord, ctn, sm, rowIndex));
            if (groupsDetailsRecord)
                tabs.add(getLinkedUsersTab(domainRecord, groupsDetailsRecord));
            return tabs;
        };

        var getParametersTab = function (domainRecord, groupsListStore, groupsDetailsRecord, ctn, sm, rowIndex) {
            var gidTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('GroupGid'),
                name: 'gid',
                disabled: (groupsDetailsRecord ? true : false),
                value: (groupsDetailsRecord ? groupsDetailsRecord.data.gid : '')
            });

            var fullNameTextField = new Ext.form.TextField({
                fieldLabel: kimios.lang('GroupName'),
                name: 'name',
                value: (groupsDetailsRecord ? groupsDetailsRecord.data.name : '')
            });

            var form = new kimios.FormPanel({
                title: kimios.lang('Group'),
                iconCls: 'admin-group-tree-node',
                autoScroll: true,
                monitorValid: true,
                labelWidth: 200,
                bodyStyle: 'padding:5px;',
                defaults: {
                    anchor: '-20',
                    selectOnFocus: true,
                    style: 'font-size: 10px',
                    labelStyle: 'font-size: 10px',
                    height: 18
                },
                defaultType: 'textfield',
                items: [
                    gidTextField,
                    fullNameTextField,
                    {
                        xtype: 'hidden',
                        name: 'authenticationSourceName',
                        value: domainRecord.data.name
                    }],
                bbar: ['->', {
                    text: kimios.lang('Save'),
                    iconCls: 'save',
                    handler: function () {
                        kimios.request.AdminRequest.saveGroup(ctn, sm, rowIndex, groupsListStore, form, groupsDetailsRecord);
                    }
                }]
            });

            if (groupsDetailsRecord) {
                form.add({
                    xtype: 'hidden',
                    name: 'gid',
                    value: groupsDetailsRecord.data.gid
                });
                fullNameTextField.focus(true, true);
            } else {
                gidTextField.focus(true, true);
            }
            return form;
        };

        var getLinkedUsersTab = function (record, groupRecord) {
            var groupUsersStore = kimios.store.AdminStore.getGroupUsersStore(groupRecord.data.gid, record.data.name);

            var addButton = new Ext.Button({
                text: kimios.lang('Add'),
                iconCls: 'add-user-icon',
                disabled: (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource' ? false : true),
                handler: function () {
                    var el = new kimios.picker.SecurityEntityPicker({
                        title: kimios.lang('LinkedUsers'),
                        iconCls: 'add-user-icon',
                        entityMode: 'user',
                        lockedSourceValue: record.data.name
                    });
                    el.on('entitySelected', function (usersRecords, groupsRecords, _pickerWindow) {
                        kimios.request.AdminRequest.saveGroupToUser(groupUsersStore, groupRecord.data.gid, usersRecords);
                        _pickerWindow.close();
                    });
                    el.show();
                }
            });

            var removeButton = new Ext.Button({
                text: kimios.lang('Remove'),
                iconCls: 'delete-user',
                disabled: true,
                handler: function () {
                    kimios.request.AdminRequest.removeGroupToUser(groupUsersStore, groupRecord.data.gid, sm.getSelections());
                }
            });

            var sm = new Ext.grid.CheckboxSelectionModel({
                checkOnly: true,
                listeners: {
                    selectionchange: function (sm) {
                        var count = sm.getCount();
                        if (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource') {
                            if (count > 0) {
                                removeButton.enable();
                            } else {
                                removeButton.disable();
                            }
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
                        metaData.css = 'admin-user-tree-node';
                    }
                }, {
                    id: 'uid',
                    width: 80,
                    sortable: true,
                    dataIndex: 'uid',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' + record.get('name') + '</span>';
                    }
                }
            ]);

            var usersGrid = new Ext.grid.GridPanel({
                title: kimios.lang('LinkedUsers'),
                iconCls: 'user-icon',
                autoScroll: true,
                loadMask: true,
                store: groupUsersStore,
                stripeRows: true,
                cm: cm,
                sm: sm,
                viewConfig: {
                    forceFit: true
                },
                hideHeaders: true,
                columnLines: false,
                tbar: [
                    addButton, '-',
                    removeButton
                ]
            });

            usersGrid.on('activate', function () {
                groupUsersStore.load();
            });

            return usersGrid;
        };

        var groupsListStore = kimios.store.AdminStore.getGroupsStore(record.data.name);

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            tooltip: kimios.lang('Add'),
            iconCls: 'add-group-icon',
            disabled: (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource' ? false : true),
            handler: function () {
                var ctn = Ext.getCmp('admin-domains-groups-details-container');
                ctn.add(getDetailsPanel(ctn, sm, -1, groupsListStore, record));
                ctn.getLayout().setActiveItem(0);
                ctn.expand();
                ctn.setVisible(true);
                p.doLayout();
            }
        });

        var removeButton = new Ext.Button({
            text: kimios.lang('Remove'),
            tooltip: kimios.lang('Remove'),
            iconCls: 'delete-group',
            disabled: true,
            handler: function () {
                kimios.request.AdminRequest.removeGroup(groupsListStore, sm.getSelections());
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            //            checkOnly: true,
            listeners: {
                selectionchange: function (sm) {
                    var count = sm.getCount();
                    if (record.data.className == 'org.kimios.kernel.user.impl.HAuthenticationSource') {
                        if (count > 0) {
                            removeButton.enable();
                        } else {
                            removeButton.disable();
                        }
                    }
                }
            }
        });

        var cm = new Ext.grid.ColumnModel([
            //            sm,
            {
                width: 16,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'icon',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'admin-group-tree-node';
                }
            },
            {
                id: 'gid',
                sortable: false,
                dataIndex: 'gid',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' + record.get('name') + '</span>';
                }
            }
        ]);

        var panel = new Ext.grid.GridPanel({
            region: 'center',
            store: groupsListStore,
            stripeRows: true,
            loadMask: true,
            border: false,
            cm: cm,
            sm: sm,
            viewConfig: {
                forceFit: true
            },
            hideHeaders: true,
            columnLines: false,
            buttonAlign: 'left',
            tbar: [
                addButton,
                removeButton
            ]
        });

        var p = new Ext.Panel({
            id: 'admin-domains-groups-panel',
            title: kimios.lang('Groups'),
            iconCls: 'admin-group-tree-node',
            layout: 'border',
            items: [panel, new Ext.Panel({
                region: 'south',
                id: 'admin-domains-groups-details-container',
                border: false,
                animCollapse: false,
                collapsed: true,
                hidden: true,
                layout: new Ext.layout.CardLayout({
                    layoutOnCardChange: true
                }),
                height: 175
            })],
            listeners: {
                activate: function () {
                    groupsListStore.load({
                        callback: function () {
                            var ctn = Ext.getCmp('admin-domains-groups-details-container');
                            ctn.collapse();
                            ctn.setVisible(false);
                            p.doLayout();
                        }
                    });
                }
            }
        });
        panel.on('rowclick', function (grid, rowIndex, e) {
            var ctn = Ext.getCmp('admin-domains-groups-details-container');
            if (sm.getSelections().length == 1) {
                ctn.removeAll();
                ctn.add(getDetailsPanel(ctn, sm, rowIndex, groupsListStore, record, sm.getSelected()));
                ctn.getLayout().setActiveItem(0);
                ctn.expand();
                ctn.setVisible(true);
            } else {
                ctn.collapse();
                ctn.setVisible(false);
            }
            p.doLayout();
        });
        return p;
    }
};
