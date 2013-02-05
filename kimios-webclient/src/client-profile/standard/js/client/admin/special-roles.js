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
Admin.Roles = {
    getPanel: function(){
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            collapsible: true,
            hideCollapseTool : true,
            title: kimios.lang('Roles'),
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
            rootVisible: false,
            autoSize: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode()
        });

        var contextPanel = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit',
            margins: '3 3 3 0',
            bodyStyle: 'background-color: transparent;'
        });
        var rootNode = treePanel.getRootNode();

        var workspaceCreatorsNode = new Ext.tree.TreeNode({
            text: kimios.lang('WorkspaceCreator'),
            iconCls: 'admin-acc-role'
        });

        var studioUsersNode = new Ext.tree.TreeNode({
            text: kimios.lang('StudioUser'),
            iconCls: 'admin-acc-role'
        });

        var administratorNode = new Ext.tree.TreeNode({
            text: kimios.lang('Admin'),
            iconCls: 'admin-acc-role'
        });

        var metaFeedDeniedNode = new Ext.tree.TreeNode({
            text: kimios.lang('MetaFeedAccessDenied'),
            iconCls: 'admin-acc-role'
        });

        var reportingUsersNode = new Ext.tree.TreeNode({
            text: kimios.lang('ReportingUser'),
            iconCls: 'admin-acc-role'
        });

        workspaceCreatorsNode.on('click', function(node, e){
            var usersListStore = kimios.store.AdminStore.getRoleUsersStore(1);
      contextPanel.removeAll();
            contextPanel.add(Admin.Roles.getGridPanel(usersListStore, 1, node));
            contextPanel.doLayout();
        });
        studioUsersNode.on('click', function(node, e){
            var usersListStore = kimios.store.AdminStore.getRoleUsersStore(2);
            contextPanel.removeAll();
            contextPanel.add(Admin.Roles.getGridPanel(usersListStore, 2, node));
            contextPanel.doLayout();
        });
        administratorNode.on('click', function(node, e){
            var usersListStore = kimios.store.AdminStore.getRoleUsersStore(3);
            contextPanel.removeAll();
            contextPanel.add(Admin.Roles.getGridPanel(usersListStore, 3, node));
            contextPanel.doLayout();
        });
        metaFeedDeniedNode.on('click', function(node, e){
            var usersListStore = kimios.store.AdminStore.getRoleUsersStore(4);
            contextPanel.removeAll();
            contextPanel.add(Admin.Roles.getGridPanel(usersListStore, 4, node));
            contextPanel.doLayout();
        });
        reportingUsersNode.on('click', function(node, e){
            var usersListStore = kimios.store.AdminStore.getRoleUsersStore(5);
            contextPanel.removeAll();
            contextPanel.add(Admin.Roles.getGridPanel(usersListStore, 5, node));
            contextPanel.doLayout();
        });

        rootNode.appendChild(administratorNode);
        rootNode.appendChild(studioUsersNode);
        rootNode.appendChild(reportingUsersNode);
        rootNode.appendChild(workspaceCreatorsNode);
        rootNode.appendChild(metaFeedDeniedNode);

        return Admin.getPanel(kimios.lang('Roles'), 'admin-acc-role', treePanel, contextPanel);
    },

    getGridPanel: function(usersListStore, roleId, node){
        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            tooltip: kimios.lang('Add'),
            iconCls:'add-user-icon',
            handler: function(){
                var el = new kimios.picker.SecurityEntityPicker({
                    title: kimios.lang('Add'),
                    iconCls: 'add-user-icon',
                    entityMode: 'user'
                });
                el.on('entitySelected', function(usersRecords, groupsRecords){
                    kimios.request.AdminRequest.saveRole(usersListStore, usersRecords, roleId);
                });
                el.show();
            }
        });

        var removeButton = new Ext.Button({
            text: kimios.lang('Remove'),
            tooltip: kimios.lang('Remove'),
            iconCls:'delete-user',
            disabled: true,
            handler: function(){
                kimios.request.AdminRequest.removeRole(usersListStore, sm.getSelections());
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            listeners: {
                selectionchange: function(sm) {
                    var count = sm.getCount();
                    if (count > 0) {
                        removeButton.enable();
                    } else {
                        removeButton.disable();
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
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'admin-user-tree-node';
                }
            },
            {
                sortable: true,
                dataIndex: 'userName',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    return value+'@'+record.get('userSource');
                }
            }
            ]);

        var panel = new Ext.grid.GridPanel({
            title: node.text,
            iconCls: 'admin-acc-role',
            id:'admin-roles-panel',
            store: usersListStore,
            stripeRows: true,
            hideHeaders: true,
            loadMask: true,
            cm: cm,
            sm: sm,
            viewConfig: {
                forceFit:true
            },
            columnLines: false,
            buttonAlign: 'left',
            tbar:[
            addButton,
            removeButton
            ],
            tools:[{
                id:'refresh',
                handler: function(event, toolEl, panel){
                    usersListStore.reload();
                }
            }]
        });
        usersListStore.load();
        panel.doLayout();
        return panel;
    }

};
