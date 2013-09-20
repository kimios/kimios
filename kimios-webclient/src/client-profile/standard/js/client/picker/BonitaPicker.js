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

kimios.picker.BonitaPicker = Ext.extend(Ext.util.Observable, {

    constructor: function (config) {
        this.documentUid = config.documentUid;

        this.workflowField = new kimios.form.ProcessField({
            name: 'workflowUid'
        });

        this.nextButton = new Ext.Button({
            scope: this,
            text: kimios.lang('StartWorkflow'),
            iconCls: 'x-tbar-page-next',
            iconAlign: 'right',
            handler: function () {
                this.nextButton.disable();
                this.window.close();

                var url = this.workflowField.getValue();

                var iframe = new Ext.Window({
                    width: 640,
                    height: 480,
                    layout: 'fit',
                    border: false,
                    title: kimios.lang('Workflow'),
                    modal: true,
                    maximizable: true,
                    closable: false,
                    autoScroll: true,
                    items: [
                        {
                            html: '<iframe id="reportframe" border="0" width="100%" height="100%" ' +
                                'frameborder="0" marginheight="12" marginwidth="16" scrolling="auto" ' +
                                'style="padding: 16px" ' +
                                'src="' + url + '&process_documentId=' + config.documentUid + '"></iframe>'
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
        });

        this.form = new kimios.FormPanel({
            bodyStyle: 'padding:10px;background-color:transparent;',
            border: false,
            labelWidth: 200,
            height: 50,
            region: 'north',
            defaults: {
                anchor: '100%',
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 11px;font-weight:bold;'
            },
            items: [this.workflowField]
        });

        var obj = Ext.decode(config.instances);

        var instancesList = [];

        for (var key in  obj.entityAttributes) {
            if (key.indexOf('BonitaProcessInstance_') != -1) {
                instancesList.push(Ext.decode(obj.entityAttributes[key].value));
            }
        }

        var instancesStore = new Ext.data.JsonStore({
            fields: ['id', 'name', 'rootProcessInstanceId', 'startDate', 'lastUpdate', 'endDate', 'stateCategory'],
            data: instancesList
        });

        var tasksStore = kimios.store.TasksStore.getBonitaTasksByInstanceStore();

        this.instanceProcessPanel = new Ext.grid.GridPanel({
            title: kimios.lang('ProcessInstances'),
            region: 'center',
            border: false,
            frame: true,
            hideHeaders: true,
            viewConfig: {
                forceFit: true,
                scrollOffset: 0
            },
            cm: new Ext.grid.ColumnModel([
                {
                    width: 16,
                    fixed: true,
                    editable: false,
                    sortable: false,
                    menuDisabled: true,
                    dataIndex: 'icon',
                    renderer: function (value, metaData) {
                        metaData.css = 'studio-cls-wf';
                    }
                },
                {
                    header: 'Name',
                    dataIndex: 'name',
                    flex: 1,
                    autoWidth: true,
                    editable: false,
                    sortable: false,
                    menuDisabled: true,
                    renderer: function (value, css, record) {
                        var html = '#' + record.get('id') + ' <span style="font-size:.9em;color:gray;">-- ' + record.get('name') + '</span>';

                        html += '<br/><span style="font-size:.9em;color:gray;">Start: ' + kimios.date(record.get('startDate')) + '</span>';
                        if (record.get('lastUpdate') > 0)
                            html += '<br/><span style="font-size:.9em;color:gray;">Update: ' + kimios.date(record.get('lastUpdate')) + '</span>';
                        if (record.get('endDate') > 0)
                            html += '<br/><span style="font-size:.9em;color:gray;">End: ' + kimios.date(record.get('endDate')) + '</span>';

                        html += '<br/><span style="font-size:.8em;">' + record.get('stateCategory') + '</span>';

                        return html;
                    }
                }
            ]),
            store: instancesStore
        });

        this.tasksPanel = new Ext.grid.GridPanel({
            title: kimios.lang('TasksList'),
            region: 'east',
            border: false,
            width: 300,
            split: true,
            frame: true,
            hideHeaders: true,
            viewConfig: {
                forceFit: true,
                scrollOffset: 0

            },
            cm: new Ext.grid.ColumnModel([
                {
                    width: 16,
                    fixed: true,
                    editable: false,
                    sortable: false,
                    menuDisabled: true,
                    dataIndex: 'icon',
                    renderer: function (value, metaData) {
                        metaData.css = 'studio-wf-status';
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
            ]),
            store: tasksStore
        });
        var tasksPanel = this.tasksPanel;
        this.instanceProcessPanel.getSelectionModel().on('selectionchange', function (sm) {
            var recs = sm.getSelections();
            if (recs.length > 0) {
                var instanceId = recs[0].data.id;

                tasksStore.load({
                    params: {
                        processInstanceId: instanceId
                    },
                    callback: function (records) {
                        if (!records || records.length == 0)
                            this.tasksPanel.setTitle(kimios.lang('NoTasks'));
                        else
                            this.tasksPanel.setTitle(kimios.lang('TasksList') + ' (' + records.length + ')');
                    },
                    scope: this
                });
            } else {
                this.tasksPanel.setTitle(kimios.lang('TasksList') + ' (' + recs.length + ')');
            }
        }, this);

        this.window = new Ext.Window({
            width: 640,
            height: 480,
            layout: 'border',
            border: true,
            maximizable: true,
            title: kimios.lang('Workflow'),
            iconCls: 'studio-cls-wf',
            modal: true,
            bodyStyle: 'padding:10px;background-color:transparent;',
            items: [this.form, this.instanceProcessPanel, this.tasksPanel],
            bbar: ['->', this.nextButton]
        });

        kimios.picker.BonitaPicker.superclass.constructor.call(this, config);
    },

    show: function () {
        this.window.show();
    }
});
