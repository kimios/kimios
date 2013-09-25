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
        this.pageSize = 10;
        this.id = 'kimios-tasks-panel';
        this.title = kimios.lang('BonitaPendingTasks');
        this.hideHeaders = true;
        this.store = kimios.store.TasksStore.getBonitaPendingTasksStore(false, this.pageSize);
        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };
        this.columnLines = false;
        this.sm = new Ext.grid.RowSelectionModel({singleSelect: true});

        this.pagingToolBar = new Ext.PagingToolbar({
            id: 'kimios-tasks-panel-pbar',
            store: this.store,
            displayInfo: true,
            pageSize: this.pageSize,
            displayMsg: '',
            emptyMsg: '',
            prependButtons: true
        });
        this.pagingToolBar.refresh.hideParent = true;
        this.pagingToolBar.refresh.hide();

        this.tbar = [
            this.pagingToolBar
        ];
        this.bonitaError = false;

        this.cm = new Ext.grid.ColumnModel([
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
                        metaData.css = 'reject-status';
                    }
                    else {
                        metaData.css = 'accept-status';
                    }

                }
            },
            {
                sortable: false,
                menuDisabled: true,
                align: 'left',
                flex: 1,
                dataIndex: 'name',
                renderer: function (value, meta, record) {
                    var state = record.data.state;
                    var date = kimios.date(record.data.expectedEndDate);
                    var apps = record.data.processWrapper.name;
                    var desc = record.data.description;

                    var html = value + '<span style="font-size:.9em;color:gray;"> -- ' + apps + '</span>';
                    html += '<br/><span style="font-size:.9em;color:gray;">' + date + '</span>';
                    html += '<br/><span style="font-size:.8em;">' + state.toUpperCase() + '</span>';

                    return html;
                }
            }
        ]);

        kimios.tasks.BonitaTasksPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.tasks.BonitaTasksPanel.superclass.initComponent.apply(this, arguments);

        this.store.on('beforeload', function (store, records, options) {
            kimios.explorer.getToolbar().myTasksButton.setIconClass('loading');
            Ext.getCmp('bonitaTabPanelId').setIconClass('loading');
        }, this);

        this.store.on('exception', function () {
            Ext.getCmp('kimios-viewport').bonita = false;
            this.bonitaError = true;

            this.tasksCounter = '?';

            if (Ext.getCmp('kimios-assigned-tasks-panel').bonitaError) {

                kimios.explorer.getToolbar().myTasksButton.setText('<span style="color:gray;text-decoration: line-through;">' + kimios.lang('MyTasks') + '</span>');

                if (Ext.getCmp('kimios-viewport').bonita == false && Ext.getCmp('kimios-viewport').bonitaAlreadyCheck == false) {
                    Ext.Msg.show({
                        title: 'Bonita Service',
                        msg: kimios.lang('BonitaUnvailable'),
                        buttons: Ext.Msg.OK,
                        icon: Ext.MessageBox.WARNING
                    });
                    Ext.getCmp('kimios-viewport').bonitaAlreadyCheck = true;
                }
            } else {
                kimios.explorer.getToolbar().myTasksButton.setText('<span style="color:gray;text-decoration: line-through;">' + kimios.lang('MyTasks') + ' (?)</span>');
            }
            Ext.getCmp('kimios-tasks-panel').getStore().removeAll();
            Ext.getCmp('kimios-tasks-panel').setTitle('<span style="color:gray;text-decoration: line-through;">' + kimios.lang('BonitaPendingTasks') + '</span>');
            Ext.getCmp('bonitaTabPanelId').setIconClass(undefined);
            Ext.getCmp('bonitaTabPanelId').refresh('?', Ext.getCmp('kimios-assigned-tasks-panel').tasksCounter);
            kimios.explorer.getToolbar().myTasksButton.setIconClass('tasks');
        }, this);

        this.store.on('load', function (store, records, options) {
            Ext.getCmp('kimios-viewport').bonita = true;
            Ext.getCmp('kimios-viewport').bonitaAlreadyCheck = false;
            this.bonitaError = false;

            this.tasksCounter = store.totalLength;

            this.setTitle(kimios.lang('BonitaPendingTasks') + ' ' + (this.tasksCounter > 0 ? '(' + this.tasksCounter + ')' : ''));

            Ext.getCmp('bonitaTabPanelId').refresh(this.tasksCounter, undefined);
            Ext.getCmp('bonitaTabPanelId').setIconClass(undefined);

            kimios.explorer.getToolbar().myTasksButton.refresh(this.tasksCounter, Ext.getCmp('kimios-assigned-tasks-panel').tasksCounter);
            kimios.explorer.getToolbar().myTasksButton.setIconClass('tasks');

            if (records.length == 0 && this.tasksCounter > 0) {
                Ext.getCmp('kimios-tasks-panel-pbar').moveLast();
            }

            Ext.getCmp('bonitaTabPanelId').setDisabled(false);
            kimios.explorer.getToolbar().doLayout(); // My Tasks button GUI fix
        }, this);

        this.on('rowdblclick', function (grid, rowIndex, e) {
            var pojo = grid.getSelectionModel().getSelected().data;
            Ext.getCmp('kimios-assigned-tasks-panel').getTaskWindow(pojo, true, false).show();
        }, this);

        this.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            var sm = this.getSelectionModel();
            sm.selectRow(rowIndex);
            var selectedRecord = sm.getSelected();
            kimios.ContextMenu.show(selectedRecord.data, e, 'myBonitaTasks');
        });

        this.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'myBonitaTasksContainer');
        }, this);
    },

    refresh: function () {

        this.store.reload({
            scope: this
        });
    },

    refreshLanguage: function () {
        this.refresh();
        this.doLayout();
    }


});
