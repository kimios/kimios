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
kimios.tasks.BonitaTasksPanel = Ext.extend(Ext.grid.GridPanel, {

    constructor: function (config) {
        this.tasksCounter = 0;
        this.id = 'kimios-tasks-panel';
        this.title = kimios.lang('MyTasks');
//        this.iconCls = 'tasks';
        this.hideHeaders = true;
        this.stripeRows = true;
        this.store = kimios.store.TasksStore.getMyBonitaTasksStore(false);
        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };
        this.columnLines = false;
        this.sm = new Ext.grid.RowSelectionModel({singleSelect: true});
        this.cm = new Ext.grid.ColumnModel([

            {
                sortable: false,
                menuDisabled: true,
                align: 'left',
                flex: 1,
                dataIndex: 'name',
                renderer: function (value, meta, record) {
                    var state = record.data.state;
                    var date = kimios.date(record.data.expectedEndDate);

                    var html = '';

                    if (state == 'failed') {
                        html = '<span style="color:red;">' + value;
                        html += '<br/><span style="font-size:10px;">' + date + '</span></span>';
                    } else {
                        html = value;
                        html += '<br/><span style="font-size:10px;color:#666;">' + date + '</span>';
                    }

                    return html;
                }
            }  ,
            {
                align: 'center',
                readOnly: true,
                width: 16,
                hidden: false,
                sortable: false,
                hideable: false,
                fixed: true,
                resizable: false,
                menuDisabled: true,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.state == 'failed') {
                        metaData.css = 'warn';
                    }

                }
            }             ,
            {
                readOnly: true,
                width: 32,
                sortable: false,
                hideable: false,
                fixed: true,
                resizable: false,
                menuDisabled: true
            }
//            ,
//            {
//                sortable: false,
//                menuDisabled: true,
//                dataIndex: 'state',
//                align: 'center',
//                renderer: function (value) {
//                    if (value == 'failed') {
//                        return '<span style="font-weight:normal;color:red;">' + kimios.lang('TaskFailed') + '</span>';
//                    }
//                    return '';
//                }
//            }
//            ,
//            {
//                dataIndex: 'expectedEndDate',
//                width: 120,
//                fixed: true,
//                readOnly: true,
//                sortable: true,
//                resizable: false,
//                menuDisabled: true,
//                align: 'left',
//                renderer: function (value) {
//                    return kimios.date(value);
//                }
//            },
//            {
//                sortable: true,
//                menuDisabled: true,
//                dataIndex: 'priority'
//            },
//
//            {
//                sortable: true,
//                menuDisabled: true,
//                dataIndex: 'stateCategory'
//            }
//            ,
//            {
//                sortable: true,
//                menuDisabled: true,
//                dataIndex: 'type'
//            }

        ]);

        kimios.tasks.BonitaTasksPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.tasks.BonitaTasksPanel.superclass.initComponent.apply(this, arguments);

        this.store.on('load', function (store, records, options) {
            this.tasksCounter = records.length;
            var newTitle = kimios.lang('MyTasks') + ' ' + (this.tasksCounter > 0 ? '(' + this.tasksCounter + ')' : '');
            var tasksButton = kimios.explorer.getToolbar().myTasksButton;
            this.setTitle(newTitle);
            tasksButton.setText(newTitle);
            this.setIconClass(null);
            tasksButton.setIconClass('tasks');
            if (this.lastSelectedRow != undefined)
                this.getSelectionModel().selectRow(this.lastSelectedRow);
            kimios.explorer.getToolbar().doLayout(); // My Tasks button GUI fix
        }, this);

//        this.on('rowclick', function (grid, rowIndex, e) {
//            if (grid.getStore().getAt(rowIndex).data.type == 9)
//                return false;
//            this.lastSelectedRow = rowIndex;
//        }, this);

//        this.on('rowdblclick', function (grid, rowIndex, e) {
//            if (grid.getStore().getAt(rowIndex).data.type == 9)
//                return false;
//            var pojo = grid.getSelectionModel().getSelected().data;
//            if (kimios.isViewableExtension(pojo.extension)) {
//                kimios.viewDoc(pojo);
//            } else {
//                window.location.href = kimios.util.getDocumentLink(pojo.uid);
//            }
//        });

        this.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            var sm = this.getSelectionModel();
            sm.selectRow(rowIndex);
            var selectedRecord = sm.getSelected();
            kimios.ContextMenu.show(selectedRecord.data, e, 'myBonitaTasks');
        });

        this.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'myTasksContainer');
        }, this);
    },

    refresh: function () {
        this.setIconClass('loading');
        kimios.explorer.getToolbar().myTasksButton.setIconClass('loading');
        this.store.reload({
            scope: this,
            callback: function (records) {
//                if (!records || records.length == 0) {
//                    this.store.insert(0, new Ext.data.Record({
//                        name: kimios.lang('NoTasks'),
//                        type: 9,
//                        extension: null
//                    }));
//                }
            }
        });
    },

    refreshLanguage: function () {
        var newTitle = kimios.lang('MyTasks') + ' ' + (this.tasksCounter > 0 ? '(' + this.tasksCounter + ')' : '');
        this.setTitle(newTitle);
        kimios.explorer.getToolbar().myTasksButton.setText(newTitle);
        this.refresh();
        this.doLayout();
    }
});
