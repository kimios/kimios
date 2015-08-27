/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


kimios.explorer.TrashPanel = Ext.extend(Ext.grid.GridPanel, {


    getState: function () {
        var ret = Ext.grid.GridPanel.prototype.getState.call(this, arguments);
        return ret;
    },
    applyState: function (state) {
        var cs = state.columns;
        if (cs.length !== 0) {
            for (var i = 0, len = cs.length; i < len; i++) {
                var s = cs[i], c = Ext.getCmp(s.id);
                if (typeof c !== "undefined") {
                    if (typeof s.hidden !== "undefined") {
                        c.hidden = s.hidden;
                    }
                }
            }
        }
    },

    constructor: function (config) {
        this.closable = true;
        //this.title = kimios.lang('Loading');
        this.iconCls = 'loading';
        this.border = false;
        this.layout = 'fit';

        //grid stuff
        this.region = 'center';
        this.border = false;
        this.stripeRows = false;
        this.stateId = 'gridEntitiesState';
        this.stateful = true;
        this.store = kimios.store.getTrashStore();

        this.columnLines = false;
        this.enableDragDrop = true;
        this.enableDD = true;
        this.ddGroup = 'grid2tree';
        this.ddScroll = true;
        this.cm = this.getColumns();
        var _this = this;
        this.sm = new Ext.grid.RowSelectionModel({
            singleSelect: false,
            listeners: {
                rowselect: {
                    fn: function(){
                        var sm = this.getSelectionModel();
                        var rows = sm.getSelections();

                        if(rows.length > 0){
                            this.permanentlyDelete.enable();
                            this.removeFromTrash.enable();
                        } else {
                            this.permanentlyDelete.disable();
                            this.removeFromTrash.disable();
                        }
                    },
                    scope: _this
                }
            }
        });
        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };

        /* dummy paging init */
        this.removeFromTrash = new Ext.Button({
            text: kimios.lang('RestoreFromTrash'),
            handler: function () {
                var sm = this.getSelectionModel();
                var rows = sm.getSelections();
                for(var u = 0; u < rows.length; u++){
                    //restore
                    kimios.ajaxRequest('Version', {
                            action: 'restoreFromTrash',
                            documentId: rows[u].data.uid
                        },
                        function () {
                            _this.getStore().load();
                        }
                    );
                }
            },
            scope: this,
            disabled: true
        });


        this.store.load();


        this.permanentlyDelete = new Ext.Button({
               text: kimios.lang('PermanentlyDelete'),
               handler: function(){
                   var sm = this.getSelectionModel();
                   var rows = sm.getSelections();
                   for(var u = 0; u < rows.length; u++){
                       //restore
                       kimios.ajaxRequest('DmsEntity', {
                               action: 'permanentDelete',
                               dmEntityUid: rows[u].data.uid
                           },
                           function () {
                               _this.getStore().load();
                           }
                       );
                   }
               },
               scope: this,
               disabled: true
        });


        this.tbar = new Ext.Toolbar({
            items: [this.permanentlyDelete, this.removeFromTrash]
        })



        kimios.explorer.TrashPanel.superclass.constructor.call(this, config);
    },

    refresh: function () {
        this.loadEntities();
    },

    loadEntities: function () {
        if (!this.gridPanel.getState().sort) {
            this.gridPanel.getStore().setDefaultSort('creationDate', 'desc');
        } else {
            var sort = this.gridPanel.getState().sort;
            this.gridPanel.getStore().setDefaultSort(sort.field, sort.direction);
        }
        this.gridPanel.getStore().load({
            scope: this,
            callback: function (records, options, success) {
                this.alreadyLoad = true;
                this.setIconClass(this.type == undefined || this.type == '' ? 'home' : 'dm-entity-tab-' + this.type);
            }
        });
    },

    refreshLanguage: function () {
        this.doLayout();
    },

    getColumns: function () {

        var renderSymLinkHelper = function (dataToRender, field) {
            if (dataToRender.type == 7 && dataToRender.targetEntity) {
                return dataToRender.targetEntity[field];
            } else {
                return dataToRender[field];
            }
        }
        var cmArray = [
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
                    if (record.data.type == 7) {
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.targetEntity.type, record.data.targetEntity.extension);
                    } else
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
                }
            },
            {
                header: kimios.lang('DocumentName'),
                dataIndex: 'name',
//        width : 100,
                readOnly: true,
                sortable: true,
                hideable: false,
                menuDisabled: false,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        if (record.data.targetEntity.checkedOut) {
                            return '<span style="color:red;">' + record.data.targetEntity.name + '</span>';
                        } else {
                            return record.data.targetEntity.name;
                        }
                    } else {
                        if (record.get('checkedOut'))
                            val = '<span style="color:red;">' + val + '</span>';
                        return val;
                    }
                }
            },
            {
                align: 'center',
                readOnly: true,
                width: 20,
                hidden: false,
                sortable: false,
                hideable: false,
                fixed: true,
                resizable: false,
                menuDisabled: false,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if ((record.data.type == 7 && record.data.targetEntity.checkedOut) || record.get('checkedOut')) {
                        metaData.css = 'checked-out';
                    }

                }
            },
            {
                header: kimios.lang('CreationDate'),
                dataIndex: 'creationDate',
                width: 120,
                fixed: true,
                readOnly: true,
                sortable: true,
                resizable: false,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record) {
                    if (record.data.type == 7 && record.data.targetEntity) {
                        return kimios.date(record.data.targetEntity.creationDate)
                    } else
                        return kimios.date(value);
                }
            },
            {
                header: kimios.lang('UpdateDate'),
                dataIndex: 'lastVersionUpdateDate',
                width: 120,
                fixed: true,
                readOnly: true,
                sortable: true,
                resizable: false,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        if (record.data.targetEntity.type <= 2)
                            return 'N/A';
                        else
                            return kimios.date(record.data.targetEntity.lastVersionUpdateDate);
                    }
                    if (record.data.type <= 2)
                        return 'N/A';
                    else
                        return kimios.date(value);
                }
            },
            {
                header: kimios.lang('Author'),
                dataIndex: 'owner',
                width: 80,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        return record.data.targetEntity.owner + '@' + record.data.targetEntity.ownerSource;
                    }
                    return val + '@' + record.get('ownerSource');
                }
            },
            {
                header: kimios.lang('DocumentType'),
                dataIndex: 'documentTypeName',
                width: 50,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    switch (record.get('type')) {
                        case 1:
                            return kimios.lang('Workspace');
                        case 2:
                            return kimios.lang('Folder');
                        case 3:
                            var extension = record.get('extension') == undefined ? '' : record.get('extension').toUpperCase();
                            return (val == undefined || val == '' ? kimios.lang('Document') : val) + ' (' + extension.toUpperCase() + ')';

                        case 7:
                            switch (record.data.targetEntity.type) {
                                case 1:
                                    return kimios.lang('Workspace');
                                case 2:
                                    return kimios.lang('Folder');
                                case 3:
                                    var extension = record.data.targetEntity.extension == undefined ? '' : record.data.targetEntity.extension.toUpperCase();
                                    return (record.data.targetEntity.documentTypeName == undefined || record.data.targetEntity.documentTypeName == '' ? kimios.lang('Document') : record.data.targetEntity.documentTypeName) + ' (' + extension.toUpperCase() + ')';

                            }
                    }
                }
            },
            {
                header: kimios.lang('Size'),
                dataIndex: 'length',
                width: 30,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'right',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7 && record.data.targetEntity.type == 3) {
                        return (record.data.targetEntity.length / 1024).toFixed(2) + ' ' + kimios.lang('Kb');
                    }
                    if (record.data.type == 3)
                        return (val / 1024).toFixed(2) + ' ' + kimios.lang('Kb');

                }
            }
        ];

        if (bonitaEnabled) {
            cmArray.push(
                {
                    header: kimios.lang('Workflow'),
                    dataIndex: 'dmEntityAddonData',
                    width: 80,
                    readOnly: true,
                    sortable: true,
                    menuDisabled: false,
                    align: 'left',
                    renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                        var obj;

                        if (record.data.type == 7) {
                            obj = Ext.decode(record.data.targetEntity.dmEntityAddonData);
                        } else
                            obj = Ext.decode(val);
                        var counter = 0;
                        for (var key in  obj.entityAttributes) {
                            if (key.indexOf('BonitaProcessInstance_') != -1) {
                                counter++;
                            }
                        }
                        if (counter > 0)
                            return '<span style="color:red;">' + counter + ' ' + kimios.lang('ProcessInstances') + '</span>';
                    }
                }
            )
        } else {

            cmArray.push({
                header: kimios.lang('WorkflowStatus'),
                dataIndex: 'workflowStatusName',
                width: 50,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {

                    var dataItem = record.get('type') == 7 ? record.data.targetEntity : record.data;
                    if (record.get('type') == 3 || record.get('type') == 7) {
                        var getStyle = function (bg) {
                            return 'font-weight:bold;display:block;color:white;background-color:' + bg + ';padding-left:2px;margin-right:20px;';
                        };
                        var val = dataItem.workflowStatusName == '' ? '&nbsp;' : dataItem.workflowStatusName;
                        if (dataItem.outOfWorkflow == false) {
                            return '<span style="' + getStyle('tomato') + '">' + val + '</span>';
                        } else if (value != '') {
                            return '<span style="' + getStyle('olive') + '">' + val + '</span>';
                        }
                    }
                }
            });
        }

        if (addonColumns) {
            for (var z = 0; z < addonColumns.length; z++) {
                cmArray.push(addonColumns[z]);
            }
        }

        return new Ext.grid.ColumnModel(cmArray);

    }
});
