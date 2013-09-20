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

        this.store.on('load', function (store, records, options) {
            this.tasksCounter = store.totalLength;

            this.setTitle(kimios.lang('BonitaPendingTasks') + ' ' + (this.tasksCounter > 0 ? '(' + this.tasksCounter + ')' : ''));

            Ext.getCmp('bonitaTabPanelId').refresh(this.tasksCounter, undefined);
            Ext.getCmp('bonitaTabPanelId').setIconClass(undefined);

            kimios.explorer.getToolbar().myTasksButton.refresh(this.tasksCounter, undefined);
            kimios.explorer.getToolbar().myTasksButton.setIconClass('tasks');

            if (records.length == 0 && this.tasksCounter > 0) {
                Ext.getCmp('kimios-tasks-panel-pbar').moveLast();
            }

            kimios.explorer.getToolbar().doLayout(); // My Tasks button GUI fix
        }, this);

        this.on('rowdblclick', function (grid, rowIndex, e) {
            var pojo = grid.getSelectionModel().getSelected().data;
            this.getTaskWindow(pojo).show();
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
    },

    getTaskWindow: function (myTask) {
        var task = null;
        var process = null;
        var comments = null;
        var actor = null;
        var assignee = null;

        if (myTask == undefined) {
            task = this.dmEntityPojo;
            process = this.dmEntityPojo.processWrapper;
            comments = this.dmEntityPojo.commentWrappers;
            actor = this.dmEntityPojo.actor;
            assignee = this.dmEntityPojo.assignee;
        } else {
            task = myTask;
            process = myTask.processWrapper;
            comments = myTask.commentWrappers;
            actor = myTask.actor;
            assignee = myTask.assignee;
        }

        /* task fields */

        this.appsField = new Ext.form.DisplayField({
            name: 'apps',
            anchor: '100%',
            fieldLabel: 'Apps',
            value: process.name
        });

        this.versionField = new Ext.form.DisplayField({
            name: 'apps',
            anchor: '100%',
            fieldLabel: 'Apps version',
            value: process.version
        });

        this.caseField = new Ext.form.DisplayField({
            name: 'apps',
            anchor: '100%',
            fieldLabel: 'Case',
            value: task.rootContainerId
        });

        this.stateField = new Ext.form.DisplayField({
            name: 'apps',
            anchor: '100%',
            fieldLabel: 'State',
            value: task.state == 'failed' ? kimios.lang('BonitaTaskFailed') : kimios.lang('BonitaTaskReady')
        });

        this.priorityField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Priority',
            value: task.priority
        });

        this.expectedEndDateField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Due date',
            value: task.expectedEndDate > 0 ? kimios.date(task.expectedEndDate) : ''
        });

        this.lastUpdateDateField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Last update date',
            value: task.lastUpdateDate > 0 ? kimios.date(task.lastUpdateDate) : ''
        });

        this.claimedField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Claimed date',
            value: task.claimedDate > 0 ? kimios.date(task.claimedDate) : ''
        });

        this.reachedStateField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Reached state date',
            value: task.reachedStateDate > 0 ? kimios.date(task.reachedStateDate) : ''
        });

        this.descriptionField = new Ext.form.DisplayField({
            anchor: '100%',
            fieldLabel: 'Description',
            value: task.description ? task.description : 'No description'
        });

        return new Ext.Window({
            id: 'BonitaTaskWindowID',
            width: 640,
            height: 460,
            layout: 'border',
            border: true,
            title: task.name,
            iconCls: 'accept-status',
            maximizable: true,
            modal: true,
            autoScroll: true,
            items: [
                new kimios.FormPanel({
                    bodyStyle: 'padding:10px;background-color:transparent;',
                    autoScroll: true,
                    labelWidth: 140,
                    border: false,
                    width: 315,
                    region: 'west',
                    defaults: {
                        style: 'font-size: 11px',
                        labelStyle: 'font-size: 11px;font-weight:bold;'
                    },
                    items: [
                        new Ext.form.FieldSet({
                            title: 'Task Details',
                            layout: 'form',
                            collapsible: false,
                            defaults: {
                                style: 'font-size: 11px',
                                labelStyle: 'font-size: 11px;font-weight:bold;'
                            },
                            bodyStyle: 'padding:3px;background-color:transparent;',
                            items: [
                                this.appsField,
                                this.versionField,
                                this.caseField,
                                this.stateField,
                                this.priorityField,
                                this.descriptionField
                            ]
                        }),
                        new Ext.form.FieldSet({
                            title: 'Task Dates',
                            layout: 'form',
                            collapsible: false,
                            defaults: {
                                style: 'font-size: 11px',
                                labelStyle: 'font-size: 11px;font-weight:bold;'
                            },
                            bodyStyle: 'padding:3px;background-color:transparent;',
                            items: [
                                this.expectedEndDateField,
                                this.lastUpdateDateField,
                                this.claimedField,
                                this.reachedStateField
                            ]
                        })
                    ]
                }),
                new kimios.tasks.CommentsPanel({
                    frame:true,
                    title:kimios.lang('Comments'),
                    taskId: task.id,
                    comments: comments,
                    bodyStyle: 'background-color:transparent;',
                    border: false,
                    region: 'center'
                })
            ],
            fbar: [
                {
                    text: kimios.lang('BonitaDoIt'),
                    iconCls: 'studio-cls-wf',
                    handler: function () {
                        Ext.getCmp('BonitaTaskWindowID').close();

                        var url = task.url;

                        var iframe = new Ext.Window({
                            width: 640,
                            height: 460,
                            layout: 'fit',
                            title: task.name,
                            iconCls: 'accept-status',
                            maximizable: true,
                            closable: false,
                            modal: true,
                            autoScroll: true,
                            items: [
                                {
                                    html: '<iframe id="reportframe" border="0" width="100%" height="100%" ' +
                                        'frameborder="0" marginheight="12" marginwidth="16" scrolling="auto" ' +
                                        'src="' + url + '"></iframe>'
                                }
                            ],
                            fbar: [
                                {
                                    text: kimios.lang('Close'),
                                    handler: function () {
                                        iframe.close();
                                        Ext.getCmp('kimios-tasks-panel').refresh();
                                        Ext.getCmp('kimios-assigned-tasks-panel').refresh();
                                    }
                                }
                            ]
                        }).show();
                    }
                },
                {
                    text: kimios.lang('BonitaTake'),
                    iconCls: 'studio-cls-wf-down',
                    handler: function () {
                        kimios.ajaxRequest('Workflow', {
                                action: 'takeTask',
                                taskId: task.id
                            },
                            function () {
                                Ext.getCmp('kimios-tasks-panel').refresh();
                                Ext.getCmp('kimios-assigned-tasks-panel').refresh();
                            }
                        );
                        Ext.getCmp('BonitaTaskWindowID').close();
                    }
                },

                {
                    text: kimios.lang('BonitaHide'),
                    iconCls: 'delete',
                    handler: function () {
                        kimios.ajaxRequest('Workflow', {
                                action: 'hideTask',
                                taskId: task.id
                            },
                            function () {
                                Ext.getCmp('kimios-tasks-panel').refresh();
                                Ext.getCmp('kimios-assigned-tasks-panel').refresh();
                            }
                        );
                        Ext.getCmp('BonitaTaskWindowID').close();
                    }
                },
                {
                    text: kimios.lang('Close'),
                    handler: function () {
                        Ext.getCmp('BonitaTaskWindowID').close();
                    }
                }
            ]
        });
    }
});
