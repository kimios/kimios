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
kimios.tasks.TasksPanel = Ext.extend(Ext.grid.GridPanel, {

    constructor: function (config) {
        this.tasksCounter = 0;
        this.id = 'kimios-tasks-panel';
        this.title = kimios.lang('MyTasks');
//        this.iconCls = 'tasks';
        this.hideHeaders = true;
//        this.stripeRows = true;
        this.store = kimios.store.TasksStore.getMyTasksStore(false);
        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };
        this.columnLines = false;
        this.sm = new Ext.grid.RowSelectionModel({singleSelect: true});
        this.cm = new Ext.grid.ColumnModel([
            {
                align: 'center',
                readOnly: true,
                width: 20,
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
                sortable: true,
                menuDisabled: true,
                align: 'left',
                dataIndex: 'name',
                renderer: function (value, meta, record) {
                    var html = value + '<br/>';
                    html += '<span style="font-size:10px;">' + kimios.lang('RequestedStatus') + ': ' + record.get('workflowStatusName') + '</span><br/>';
                    html += '<span style="font-size:10px;color:gray;">' + record.get('statusUserName') + '@' + record.get('statusUserSource') + ' - ' + kimios.date(record.get('statusDate')) + '</span><br/>';
                    return html;
                }
            }
        ]);

        kimios.tasks.TasksPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.tasks.TasksPanel.superclass.initComponent.apply(this, arguments);

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

        this.on('rowclick', function (grid, rowIndex, e) {
            this.lastSelectedRow = rowIndex;
        }, this);

        this.on('rowdblclick', function (grid, rowIndex, e) {
            var pojo = grid.getSelectionModel().getSelected().data;
            if (kimios.isViewableExtension(pojo.extension)) {
                kimios.viewDoc(pojo);
            } else {
                window.location.href = kimios.util.getDocumentLink(pojo.uid);
            }
        });

        this.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            var sm = this.getSelectionModel();
            sm.selectRow(rowIndex);
            var selectedRecord = sm.getSelected();
            kimios.ContextMenu.show(new kimios.DMEntityPojo(selectedRecord.data), e, 'myTasks');
        });

        this.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'myTasksContainer');
        }, this);
    },

    refresh: function () {
        this.setIconClass('loading');
        kimios.explorer.getToolbar().myTasksButton.setIconClass('loading');
        this.store.reload();
    },

    refreshLanguage: function () {
        var newTitle = kimios.lang('MyTasks') + ' ' + (this.tasksCounter > 0 ? '(' + this.tasksCounter + ')' : '');
        this.setTitle(newTitle);
        kimios.explorer.getToolbar().myTasksButton.setText(newTitle);
        this.refresh();
        this.doLayout();
    }
});
