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


kimios.share = {};

kimios.share.MailPanel = Ext.extend(kimios.FormPanel, {
    constructor: function (config) {

        config.baseCls = 'x-plain';
        config.labelWidth = 55;
        config.layout = {
            type: 'vbox',
            align: 'stretch'  // Child items are stretched to full width
        };
        config.defaults = {
            xtype: 'textfield'
        };


        config.items = [];
        for(var u = 0; u < config.documents.length; u++){
            if(config.documents[u].type == 3){
                config.items.push({
                    xtype: 'label',
                    text: config.documents[u].name +
                    (config.documents[u].extension ? '.' + config.documents[u].extension :'')
                    + ' ' + (config.documents[u].length / 1024).toFixed(2) + ' ' + kimios.lang('Kb')

                });

                config.items.push({
                    xtype: 'hidden',
                    name: 'documentIds',
                    value: config.documents[u].uid
                })
            }

        }



        config.items.push({
            xtype: 'combo',
            store: [],
            mode          : 'local',
            triggerAction : 'query',
            typeAhead     : true,
            plugins: [ Ext.ux.FieldReplicator, Ext.ux.FieldLabeler ],
            fieldLabel: kimios.lang('ShareSendTo'),
            name: 'recipients',
            valueField: 'emailAddress',
            displayField: 'emailAddress'
        });
        config.items.push({
            plugins: [ Ext.ux.FieldLabeler ],
            fieldLabel: kimios.lang('ShareSubject'),
            name: 'subject'
        });
        config.items.push( {
            xtype: 'textarea',
            fieldLabel: kimios.lang('ShareContent'),
            hideLabel: true,
            name: 'content',
            flex: 1  // Take up all *remaining* vertical space
        });

        kimios.share.MailPanel.superclass.constructor.call(this, config);
    },

    showInWindow: function(){
        var me = this;
        var w = new Ext.Window({
            title: kimios.lang('Share'),
            collapsible: true,
            maximizable: true,
            width: 750,
            height: 500,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            bodyStyle: 'padding:5px;',
            buttonAlign: 'center',
            items: this,
            buttons: [{
                text: kimios.lang('ShareSend'),
                handler: function(){
                    //submit
                    me.getForm().submit({
                        clientValidation: true,
                        url: getBackEndUrl('Share'),
                        params: {
                            action: 'share'
                        },
                        success: function(form, action) {
                            w.close();
                        },
                        failure: function(form, action) {
                            switch (action.failureType) {
                                case Ext.form.Action.CLIENT_INVALID:
                                    Ext.Msg.alert('Failure', 'Form fields may not be submitted with invalid values');
                                    break;
                                case Ext.form.Action.CONNECT_FAILURE:
                                    Ext.Msg.alert('Failure', 'Ajax communication failed');
                                    break;
                                case Ext.form.Action.SERVER_INVALID:
                                    Ext.Msg.alert('Failure', action.result.msg);
                            }
                        }
                    });
                }
            }, {
                text: kimios.lang('ShareCancel'),
                handler: function(){
                    w.close();
                }
            }]
        });
        w.show();
    }


});