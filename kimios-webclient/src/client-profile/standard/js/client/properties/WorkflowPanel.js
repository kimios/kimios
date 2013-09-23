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
kimios.properties.WorkflowPanel = Ext.extend(Ext.Panel, {
    constructor: function (config) {
        this.dmEntityPojo = config.dmEntityPojo;
        this.title = kimios.lang('Notifications');
        this.iconCls = 'studio-cls-wf';
        this.layout = 'border';
        this.border = false;
        this.loadingRequired = false;
        this.loaded = false;
        this.bodyStyle = 'background-color:transparent;';
        this.readOnly = config.readOnly;

        this.cancelWorkflowButton = new Ext.Button({
            text: kimios.lang('CancelWorkflow'),
            scope: this,
            hidden: this.readOnly,
            disabled: true,
            handler: function (b) {
                Ext.MessageBox.confirm(
                    kimios.lang('CancelWorkflow'),
                    kimios.lang('ConfirmDelete'),
                    function (btn) {
                        if (btn == 'yes') {
                            b.disable();
                            kimios.request.cancelWorkflow(this.dmEntityPojo.uid);
                            this.clear();
                            this.doLayout();
                        }
                    },
                    this
                );
            }
        });

        this.refreshButton = new Ext.Button({
            iconCls: 'refresh',
            tooltip: kimios.lang('Refresh'),
            scope: this,
            handler: function () {
                if (this.dmEntityPojo.outOfWorkflow == false || this.dmEntityPojo.workflowStatusUid > 0)
                    this.workflowStatusRequestsStore.reload();
            }
        });

        this.buttonAlign = 'left';
        this.fbar = [this.cancelWorkflowButton, '->', this.refreshButton];

        this.defaults = {
            border: false,
            bodyStyle: 'background-color:transparent;'
        };

        this.currentWorkflowContainer = new Ext.Panel({
            region: 'center',
            layout: 'fit',
            defaults: {
                labelWidth: 200,
                border: false,
                bodyStyle: 'padding:10px;background-color:transparent;'
            }
        });

        this.newRequestForStatusContainer = new Ext.Panel({
            region: 'east',
            layout: 'fit',
            width: 400,
            border: false,
            defaults: {
                labelWidth: 200,
                border: false,
                bodyStyle: 'padding:10px;background-color:transparent;'
            }
        });

        this.northContainer = new Ext.Panel({
            region: 'north',
            layout: 'border',
            height: 70,
            border: false,
            defaults: this.defaults,
            items: [this.currentWorkflowContainer, this.newRequestForStatusContainer]
        });

        this.centerContainer = new Ext.Panel({
            region: 'center',
            layout: 'fit',
            border: false,
            margins: '0 5 0 5'
        });

        this.items = [this.northContainer, this.centerContainer];
        kimios.properties.WorkflowPanel.superclass.constructor.call(this, config);
    },

    setPojo: function (pojo) {
        this.dmEntityPojo = pojo;
        this.loaded = false;
    },

    initComponent: function () {
        kimios.properties.WorkflowPanel.superclass.initComponent.apply(this, arguments);

        this.workflowStatusRequestsStore = kimios.store.getWorkflowStatusRequestsStore(this.dmEntityPojo.uid);

        this.on('activate', function () {
            this.doLayout();
            if (this.loaded == false) {
                if (this.dmEntityPojo.outOfWorkflow == false || this.dmEntityPojo.workflowStatusUid > 0) {
                    this.workflowStatusRequestsStore.load();
                } else {
                    this.cancelWorkflowButton.disable();
                    this.setIconClass('studio-cls-wf');
                }
            }
        }, this);

        this.workflowStatusRequestsStore.on('beforeload', function (store, options) {
            this.setIconClass('loading');
        }, this);

        this.workflowStatusRequestsStore.on('load', function (store, records, options) {
            this.loaded = true;
            this.clear();
            this.lastStatus = records[0].data.status;
            this.workflowStatusUid = records[0].data.workflowStatusUid;
            this.workflowStatusStore = kimios.store.getWorkflowStatusStore(this.workflowStatusUid);

            this.workflowStatusStore.load({
                scope: this,
                callback: function (records, options, success) {
                    this.workflowUid = records[0].data.workflowUid;
                    this.statusName = records[0].data.name;

                    this.requestForStatusField = new kimios.form.WorkflowStatusField({
                        fieldLabel: kimios.lang('NewRequestForStatus'),
                        name: 'workflowStatusUid',
                        workflowUid: this.workflowUid,
                        selectOnFocus: true,
                        anchor: '100%'
                    });

                    this.newRequestForStatusButton = new Ext.Button({
                        text: kimios.lang('StartWorkflow'),
                        scope: this,
                        handler: function () {
                            kimios.request.startWorkflow(
                                this.dmEntityPojo.uid,
                                this.requestForStatusField.getValue(),
                                this.workflowStatusRequestsStore
                            );
                        }
                    });

                    // new request for status form panel

                    this.newRequestForStatusFormPanel = new kimios.FormPanel({
                        items: [this.requestForStatusField, this.newRequestForStatusButton],
                        hidden: this.dmEntityPojo.outOfWorkflow == true || this.lastStatus != 'RequestStatus3',
                        defaults: {
                            selectOnFocus: true,
                            style: 'font-size: 11px',
                            labelStyle: 'font-size: 11px;font-weight:bold;'
                        }
                    });

                    this.newRequestForStatusContainer.add(this.newRequestForStatusFormPanel);

                    kimios.store.StudioStore.getWorkflowsStore().load({
                        scope: this,
                        callback: function (records, options, success) {
                            for (var i = 0; i < records.length; i++) {
                                if (records[i].data.uid == this.workflowUid) {
                                    this.workflowName = records[i].data.name;
                                    break;
                                }
                            }

                            this.currentWorkflow = new Ext.form.DisplayField({
                                fieldLabel: kimios.lang('CurrentWorkflow'),
                                value: this.workflowName
                            });

                            this.currentStatus = new Ext.form.DisplayField({
                                fieldLabel: kimios.lang('CurrentWorkflowStatus'),
                                value: this.statusName + ' (' + kimios.lang(this.lastStatus) + ')'
                            });

                            // current workflow form panel

                            this.currentWorkflowFormPanel = new kimios.FormPanel({
                                items: [this.currentWorkflow, this.currentStatus],
                                buttonAlign: 'left',
                                defaults: {
                                    anchor: '100%',
                                    selectOnFocus: true,
                                    style: 'font-size: 11px',
                                    labelStyle: 'font-size: 11px;font-weight:bold;'
                                }
                            });

                            this.currentWorkflowContainer.add(this.currentWorkflowFormPanel);

                            // requests grid

                            this.grid = new Ext.grid.GridPanel({
                                store: this.workflowStatusRequestsStore,
                                cm: this.getColumns(),
                                sm: new Ext.grid.RowSelectionModel({
                                    singleSelect: true
                                }),
                                margins: '0 -1 0 -1',
                                border: true,
                                hideHeaders: true,
                                viewConfig: {
                                    forceFit: true,
                                    scrollOffset: 0
                                },
                                listeners: {
                                    //no context menu
                                    rowcontextmenu: function (grid, rowIndex, e) {
                                        e.preventDefault();
                                    },
                                    containercontextmenu: function (grid, e) {
                                        e.preventDefault();
                                    }
                                }
                            });

                            this.cancelWorkflowButton.enable();
                            this.centerContainer.add(this.grid);
                            this.doLayout();
                            this.setIconClass('studio-cls-wf');
                        }
                    });
                }
            }, this);
        }, this);
    },

    clear: function () {
        this.currentWorkflowContainer.removeAll();
        this.newRequestForStatusContainer.removeAll();
        this.centerContainer.removeAll();
    },

    getColumns: function () {
        return new Ext.grid.ColumnModel([
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
                    metaData.css = 'send';
                }
            },
            {
                readOnly: true,
                menuDisabled: true,
                sortable: true,
                scope: this,
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    var html = '';
                    var store = this.requestForStatusField.getStore();
                    for (var i = 0; i < store.getCount(); i++) {
                        if (record.get('workflowStatusUid') == store.getAt(i).data.uid) {
                            html += kimios.lang('RequestedStatus') + ': ' + store.getAt(i).data.name + '<br/>';
                            break;
                        }
                    }
                    html += '<span style="font-size:10px;color:gray;">' + record.get('userName') + '@' + record.get('userSource') + ' - ' + kimios.date(record.get('date')) + '</span>';
                    return html;
                }
            },
            {
                readOnly: true,
                menuDisabled: true,
                sortable: true,
                renderer: function (value, metadata, record) {
                    var html = kimios.lang(record.get('status'));
                    if (record.get('status') != 'RequestStatus1') {
                        html += '<br/><span style="font-size:10px;color:gray;">' + record.get('validatorUserName') + '@' + record.get('validatorUserSource') + ' - ' + kimios.date(record.get('validationDate')) + '</span>';
                    }
                    return html;
                }
            },
            {
                readOnly: true,
                menuDisabled: true,
                sortable: true,
                renderer: function (value, metadata, record) {
                    if (record.get('comment') != '')
                        return '<span style="font-size:10px;">' + record.get('comment') + '</span>';
                }
            }
        ]);
    }

});
