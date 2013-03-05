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
kimios.properties.SecurityEntityPanel = Ext.extend(Ext.Panel, {

    constructor:function (config)
    {
        this.dmEntityPojo = config.dmEntityPojo;
        this.title = kimios.lang('SecurityEntities');
        this.iconCls = 'security-icon';
        this.layout = 'border';
        this.loadingRequired = true;
        this.loaded = false;
        this.bodyStyle = 'background-color:transparent;';
        this.buttonAlign = 'left';
        this.border = false;

        this.centerContainer = new Ext.Panel({
            border:false,
            region:'center',
            layout:'fit',
            bodyStyle:'background-color:transparent;',
            margins:'5 5 0 5'
        });

        this.southContainer = new kimios.FormPanel({
            border:false,
            region:'south',
            autoHeight:true,
            labelWidth:200,
            bodyStyle:'padding-left:10px;padding-top:10px;background-color:transparent;',
            defaults:{
                width:200,
                selectOnFocus:true,
                style:'font-size: 11px',
                labelStyle:'font-size: 11px;font-weight:bold;'
            }
        });
        this.items = [this.southContainer, this.centerContainer];

        this.addSecurityEntityButton = new Ext.Button({
            text:kimios.lang('Add'),
            scope:this,
            handler:function ()
            {
                var picker = new kimios.picker.SecurityEntityPicker({
                    title:kimios.lang('Add') + ' ' + kimios.lang('SecurityEntities')
                });
                picker.show();
                picker.on('entitySelected', function (usersRecords, groupsRecords, _pickerWindow)
                {
                    var securityEntities = this.grid.store.recordType;
                    for (var usersCount = 0; usersCount < usersRecords.length; usersCount++) {
                        this.grid.store.insert(this.grid.store.getCount(), new securityEntities({
                            name:usersRecords[usersCount].data.uid,
                            source:usersRecords[usersCount].data.source,
                            type:1,
                            read:false,
                            write:false,
                            fullAccess:false
                        }));
                    }
                    for (var groupsCount = 0; groupsCount < groupsRecords.length; groupsCount++) {
                        this.grid.store.insert(this.grid.store.getCount(), new securityEntities({
                            name:groupsRecords[groupsCount].data.gid,
                            source:groupsRecords[groupsCount].data.source,
                            type:2,
                            read:false,
                            write:false,
                            fullAccess:false
                        }));
                    }
                    _pickerWindow.close();
                }, this);
            }
        });

        this.refreshButton = new Ext.Button({
            iconCls:'refresh',
            tooltip:kimios.lang('Refresh'),
            scope:this,
            handler:function ()
            {
                this.grid.store.reload();
            }
        });

        this.fbar = [this.addSecurityEntityButton, '->', this.refreshButton];
        kimios.properties.SecurityEntityPanel.superclass.constructor.call(this, config);
    },

    setPojo:function (pojo)
    {
        this.dmEntityPojo = pojo;
        this.loaded = false;
    },

    forceLoad:function (handle)
    {
        this.grid.store.load({
            callback:handle
        });
    },

    initComponent:function ()
    {
        kimios.properties.SecurityEntityPanel.superclass.initComponent.apply(this, arguments);

        var readCheckColumn = new Ext.ux.grid.CheckColumn({
            header:kimios.lang('Read'),
            dataIndex:'read',
            sortable:true,
            hideable:false,
            width:80,
            menuDisabled:true,
            fixed:true
        });

        var writeCheckColumn = new Ext.ux.grid.CheckColumn({
            header:kimios.lang('Write'),
            dataIndex:'write',
            sortable:true,
            hideable:false,
            width:80,
            menuDisabled:true,
            fixed:true
        });

        var fullAccessCheckColumn = new Ext.ux.grid.CheckColumn({
            header:kimios.lang('FullAccess'),
            dataIndex:'fullAccess',
            sortable:true,
            hideable:false,
            width:80,
            menuDisabled:true,
            fixed:true
        });

        this.grid = new Ext.grid.EditorGridPanel({
            region:'center',
            border:true,
            margins:'-1 -1 0 -1',
            stripeRows:true,
            viewConfig:{forceFit:true, scrollOffset:0},
            store:new DmsJsonStore({
                fields:kimios.record.securityEntityRecord,
                url:'DmsSecurity',
                baseParams:{
                    action:'dmEntitySecurity',
                    dmEntityType:this.dmEntityPojo.type,
                    dmEntityUid:this.dmEntityPojo.uid
                }
            }),
            plugins:[readCheckColumn, writeCheckColumn, fullAccessCheckColumn],
            sm:new Ext.grid.RowSelectionModel({
                singleSelect:true
            }),
            cm:this.getColumns(readCheckColumn, writeCheckColumn, fullAccessCheckColumn)
        });

        this.centerContainer.add(this.grid);

        this.isRecursiveField = new Ext.form.Checkbox({
            fieldLabel:kimios.lang('ApplyToChildren'),
            name:'isRecursive',
            disabled:!(this.dmEntityPojo instanceof Array ||
                (this.dmEntityPojo.uid != null && (this.dmEntityPojo.type == 1 || this.dmEntityPojo.type == 2))),
            checked:false
        });
        this.southContainer.add(this.isRecursiveField);

        this.on('activate', function ()
        {
            if (this.loaded == false && this.dmEntityPojo.uid != undefined) {
                this.grid.store.load();
            }
        }, this);

        this.grid.store.on('beforeload', function (store, options)
        {
            this.setIconClass('loading');
        }, this);

        this.grid.store.on('load', function (store, records, options)
        {
            this.loaded = true;
            this.setIconClass('admin-group-tree-node');
        }, this);

        this.grid.on('cellclick', function (grid, rowIndex, columnIndex, e)
        {
            switch (columnIndex) {
                case 0: // delete
                    this.grid.store.remove(this.grid.store.getAt(rowIndex));
                    break;
            }
        }, this);

        //no context menu
        this.grid.on('rowcontextmenu', function (grid, rowIndex, e)
        {
            e.preventDefault();
        }, this);

        this.grid.on('containercontextmenu', function (grid, e)
        {
            e.preventDefault();
        }, this);
    },

    isRecursiveSecurity:function ()
    {
        return this.isRecursiveField.checked;
    },

    isEmpty:function ()
    {
        if (this.grid.store == null)
        {
            return true;
        }
        if (this.grid.store.getCount() == 0)
        {
            return true;
        }
        return false;
    },

    getJsonSecurityValues:function ()
    {
        var out = [];
        if (this.grid.store != null) {
            this.grid.store.each(function (rec)
            {
                out.push({
                    name:rec.get('name'),
                    source:rec.get('source'),
                    type:rec.get('type'),
                    dmEntityType:rec.get('dmEntityType'),
                    dmEntityUid:rec.get('dmEntityUid'),
                    read:rec.get('read'),
                    write:rec.get('write'),
                    fullAccess:rec.get('fullAccess')
                });
            });
        }
        return Ext.util.JSON.encode(out);
    },

    getColumns:function (readCheckColumn, writeCheckColumn, fullAccessCheckColumn)
    {
        return new Ext.grid.ColumnModel([
            {
                width:16,
                fixed:true,
                editable:false,
                sortable:false,
                menuDisabled:true,
                renderer:function (value, metaData, record, rowIndex, colIndex, store)
                {
                    metaData.css = 'del-icon';
                }
            },
            {
                dataIndex:'type',
                sortable:true,
                hideable:false,
                width:16,
                menuDisabled:true,
                fixed:true,
                renderer:function (value, metaData, record, rowIndex, colIndex, store)
                {
                    if (value == 1) {
                        metaData.css = 'admin-user-tree-node';
                    } else {
                        metaData.css = 'admin-group-tree-node';
                    }
                }
            },
            {
                header:kimios.lang('SecurityEntities'),
                dataIndex:'name',
                width:400,
                sortable:true,
                hideable:false,
                menuDisabled:true,
                renderer:function (val, metaData, record, rowIndex, colIndex, store)
                {
                    return val + '@' + record.get('source');
                }
            },
            readCheckColumn,
            writeCheckColumn,
            fullAccessCheckColumn
        ]);
    }

});
