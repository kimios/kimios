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

        this.startButton = new Ext.Button({
            text: kimios.lang('StartWorkflow'),
            iconCls: 'qaction-startwf',
            disabled: true,
            handler: function () {
//                kimios.request.startWorkflow(
//                    this.documentUid,
//                    this.statusField.getValue()
//                );
//                this.window.close();
            },
            scope: this
        });

        this.workflowField = new kimios.form.ProcessField({
            name: 'workflowUid'
        });

//        this.backButton = new Ext.Button({
//            scope : this,
//            text : kimios.lang('BackLabel'),
//            iconCls : 'x-tbar-page-prev',
//            disabled : true,
//            handler : function(){
//                this.step -= 1;
//                this.window.setTitle(kimios.lang('StartWorkflow'));
//                this.backButton.disable();
//                this.form.remove(this.statusField);
//                this.workflowField.enable();
//                this.form.doLayout();
//            }
//        });
//
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
                            }
                        }
                    ]
                }).show();
            }
        });

        this.form = new kimios.FormPanel({
            bodyStyle: 'padding:10px;background-color:transparent;',
            border: false,
            labelWidth: 150,
            defaults: {
                anchor: '100%',
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 11px;font-weight:bold;'
            },
            items: [this.workflowField],
            bbar: ['->', this.nextButton]
//            ,
//            bbar : [this.backButton,'->',this.nextButton]
        });

        this.window = new Ext.Window({
            width: 320,
            height: 130,
            layout: 'fit',
            border: true,
            title: kimios.lang('Workflow'),
            resizable: false,
            modal:true,
            items: [this.form]
        });

        kimios.picker.BonitaPicker.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.picker.BonitaPicker.superclass.initComponent.apply(this, arguments);
    },

    show: function () {
        this.window.show();
        /*
         // check if document is already involved in a workflow
         var checkStore = kimios.store.getEntityStore(this.documentUid, this.documentType);
         checkStore.load({
         scope : this,
         callback : function(records, options, success){
         if (records[0].data.outOfWorkflow == true){
         this.window.show();
         }else{
         Ext.MessageBox.alert(
         'Workflow',
         kimios.lang('DocumentAlreadyInAWorkflow')
         );
         }
         }
         });
         */
    }
});
