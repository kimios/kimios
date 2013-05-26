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

kimios.picker.SecurityEntityPicker = Ext.extend(Ext.util.Observable, {

    constructor:function (config)
    {
        if (config.title)
        {
            this.title = config.title;
        }
        if (config.iconCls)
        {
            this.iconCls = config.iconCls;
        }
        if (config.entityMode)
        {
            this.entityMode = config.entityMode;
        }
        if (config.lockedSourceValue)
        {
            this.lockedSourceValue = config.lockedSourceValue;
        }
        if (config.singleSelect) {
            this.singleSelect = config.singleSelect;
        } else {
            this.singleSelect = false;
        }
        kimios.picker.SecurityEntityPicker.superclass.constructor.call(this, config);
    },

    show:function ()
    {

        var title = (this.title ? this.title : 'No title');
        var icon = (this.iconCls ? this.iconCls : 'admin-group-tree-node');

        var domainCombo = new Ext.form.ComboBox({
            name:'name',
            fieldLabel:kimios.lang('AuthenticationSource'),
            displayField:'name',
            valueField:'name',
            editable:false,
            triggerAction:'all',
            store:kimios.store.AdminStore.getDomainsStore((this.lockedSourceValue ? false : true)),
            disabled:(this.lockedSourceValue ? true : false),
            value:(this.lockedSourceValue ? this.lockedSourceValue : currentSource)
        });

        if (!this.lockedSourceValue) {

            domainCombo.on('select', function ()
            {
                if (!this.entityMode || this.entityMode == 'user') {
                    usersPanel.getStore().removeAll();
                    usersPanel.getStore().reload({
                        params:{
                            sourceUid:domainCombo.value
                        }
                    });
                }
                if (!this.entityMode || this.entityMode == 'group') {
                    groupsPanel.getStore().removeAll();
                    groupsPanel.getStore().reload({
                        params:{
                            sourceUid:domainCombo.value
                        }
                    });
                }
            }, this);
        }

        var selectButton = new Ext.Button({
            iconCls:'select',
            disabled:true
        });

        var tabs = new Ext.TabPanel({
            activeTab:0,
            border:false,
            bbar:['->', selectButton]
        });

        if (!this.entityMode || this.entityMode == 'user') {
            var usersSelectionModel = new Ext.grid.CheckboxSelectionModel({
                singleSelect:this.singleSelect,
                checkOnly:true,
                listeners:{
                    selectionchange:function (sm)
                    {
                        var count = sm.getCount();
                        if (count > 0) {
                            selectButton.enable();
                        } else {
                            selectButton.disable();
                        }
                    }
                }
            });

            var usersPanel = new Ext.grid.GridPanel({
                title:kimios.lang('Users'),
                iconCls:'admin-user-tree-node',
//                stripeRows: true,
                viewConfig:{
                    forceFit:true,
                    scrollOffset:0
                },
                hideHeaders:true,
                loadMask:true,
                sm:usersSelectionModel,
                cm:new Ext.grid.ColumnModel([
                    usersSelectionModel, {
                        width:16,
                        fixed:true,
                        editable:false,
                        sortable:false,
                        menuDisabled:true,
                        dataIndex:'icon',
                        renderer:function (value, metaData)
                        {
                            metaData.css = 'admin-user-tree-node';
                        }
                    }, {
                        id:'uid',
                        dataIndex:'uid',
                        autoWidth:true,
                        renderer:function (value, metaData, record)
                        {
                            return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' +
                                record.get('name') + '</span>';
                        }
                    }]),
                store:kimios.store.AdminStore.getUsersStore(domainCombo.value, true)
            });
            usersPanel.on('activate', function ()
            {
                tabs.doLayout();
            });
            tabs.add(usersPanel);
        }

        if (!this.entityMode || this.entityMode == 'group') {
            var groupsSelectionModel = new Ext.grid.CheckboxSelectionModel({
                singleSelect:this.singleSelect,
                checkOnly:true,
                listeners:{
                    selectionchange:function (sm)
                    {
                        var count = sm.getCount();
                        if (count > 0) {
                            selectButton.enable();
                        } else {
                            selectButton.disable();
                        }
                    }
                }
            });

            var groupsPanel = new Ext.grid.GridPanel({
                title:kimios.lang('Groups'),
                iconCls:'admin-group-tree-node',
                stripeRows:true,
                loadMask:true,
                viewConfig:{
                    forceFit:true
                },
                hideHeaders:true,
                sm:groupsSelectionModel,
                cm:new Ext.grid.ColumnModel([
                    groupsSelectionModel, {
                        width:16,
                        fixed:true,
                        editable:false,
                        sortable:false,
                        menuDisabled:true,
                        dataIndex:'icon',
                        renderer:function (value, metaData)
                        {
                            metaData.css = 'admin-group-tree-node';
                        }
                    }, {
                        id:'gid',
                        dataIndex:'gid',
                        autoWidth:true,
                        renderer:function (value, metaData, record)
                        {
                            return value + '&nbsp;&nbsp;<span style="color:#aaa;font-size:10px;">' +
                                record.get('name') + '</span>';
                        }
                    }]),
                store:kimios.store.AdminStore.getGroupsStore(domainCombo.value, true)
            });
            tabs.add(groupsPanel);
        }

        var window = new Ext.Window({
            width:300,
            height:300,
            modal:true,
            layout:'fit',
            border:false,
            title:title,
            iconCls:icon,
            maximizable:true
        });

        selectButton.on('click', function ()
        {
            this.fireEvent('entitySelected',
                (!this.entityMode || this.entityMode == 'user' ? this.getUserRecords() : null),
                (!this.entityMode || this.entityMode == 'group' ? this.getGroupRecords() : null),
                window);
        }, this);

        var container = new Ext.Panel({
            layout:'border'
        });

        window.add(container);

        var form = new kimios.FormPanel({
            region:'north',
            labelWidth:120,
            autoHeight:true,
            border:false,
            items:[domainCombo],
            defaults:{
                anchor:'100%',
                selectOnFocus:true,
                style:'font-size: 10px',
                labelStyle:'font-size: 10px'
            },
            bodyStyle:'background-color:transparent;padding:5px;'
        });

        if (!this.lockedSourceValue) {
            container.add(form);
        }
        container.add(new Ext.Panel({
            region:'center',
            bodyStyle:'padding: 0px;',
            layout:'fit',
            border:false,
            items:[tabs]
        }));

        this.getUserRecords = function ()
        {
            return usersPanel.getSelectionModel().getSelections();
        };
        this.getGroupRecords = function ()
        {
            return groupsPanel.getSelectionModel().getSelections();
        };

        window.show();
    },
    initComponent:function ()
    {
        this.addEvents('entitySelected');
    }
});
