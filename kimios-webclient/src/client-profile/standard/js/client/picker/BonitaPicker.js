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


        this.window = new Ext.Window({
            width: 640,
            height: 480,
            layout: 'fit',
            border: true,
            maximizable: true,
            title: kimios.lang('Workflow'),
            iconCls: 'studio-cls-wf',
            modal: true,
            bodyStyle: 'padding:10px;background-color:transparent;',
            items: [this.form],
            bbar: ['->', this.nextButton]
        });

        kimios.picker.BonitaPicker.superclass.constructor.call(this, config);
    },

    show: function () {
        this.window.show();
    }
});
