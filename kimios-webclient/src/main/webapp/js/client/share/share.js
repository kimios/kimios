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
        /*config.layout = {
            type: 'vbox',
            align: 'stretch'  // Child items are stretched to full width
        };*/
        config.defaults = {
            xtype: 'textfield'
        };


        config.items = [];
        this.documents = config.documents;
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
            xtype:'radiogroup',
            id:'shareType',
            fieldLabel: kimios.lang('ShareType'),
            items:[
                {xtype: 'radio', name:'shareType', inputValue:'external', fieldLabel: kimios.lang('ShareExternal'), value: 'external'},
                {id:'radioInternal', xtype: 'radio', inputValue:'internal', name:'shareType', fieldLabel: kimios.lang('ShareInternal'), value: 'internal', checked: true},
            ],
            listeners: {
                change: {
                    fn: function(me, checked){
                        if(console){
                            console.log(checked.getRawValue());
                        }
                        if(checked.getRawValue() == 'external'){
                            //
                            Ext.getCmp('shareMailSubject').show();
                            Ext.getCmp('shareMail').show();
                            Ext.getCmp('shareMailContent').show();

                            Ext.getCmp('shareUserId').hide();
                            Ext.getCmp('shareUserNotify').hide();
                            Ext.getCmp('shareUserIdDisplay').hide();

                        } else {
                            Ext.getCmp('shareMailSubject').hide();
                            Ext.getCmp('shareMail').hide();
                            Ext.getCmp('shareMailContent').hide();

                            Ext.getCmp('shareUserId').show();
                            Ext.getCmp('shareUserNotify').show();
                            Ext.getCmp('shareUserIdDisplay').show();
                        }
                    },
                    scope: this
                },
                afterrender: {
                    fn: function(){
                        Ext.getCmp('shareMailSubject').hide();
                        Ext.getCmp('shareMail').hide();
                        Ext.getCmp('shareMailContent').hide();

                        Ext.getCmp('shareUserId').show();
                        Ext.getCmp('shareUserNotify').show();
                        Ext.getCmp('shareUserIdDisplay').show();
                    }
                }
            }
        })


        config.items.push({
            xtype: 'compositefield',
            id:'shareExpiration',
            fieldLabel: kimios.lang('ShareExpirationDate'),
            items: [
                {
                    xtype: 'datefield',
                    id: 'shareExpirationDate',
                    scope: this
                },
                {
                    xtype: 'timefield',
                    id: 'shareExpirationTime',
                    format: 'H:i',
                    scope: this
                }]
        });




        //internal share


        config.items.push({
            xtype: 'compositefield',
            id:'shareUserIdDisplay',
            items: [
                {
                    xtype: 'displayfield',
                    id: 'shareUserDisplay',
                    scope: this,
                    value: kimios.lang('NoUserSelected'),
                    fieldLabel: kimios.lang('ShareChooseUser'),
                    flex: 1,
                },
                {
                    xtype: 'button',
                    id:'shareUserId',
                    fieldLabel: kimios.lang('ShareChooseUser'),
                    text: kimios.lang('ShareChooseUser'),
                    handler: function(){
                        this.shareWith(config.documents[0].uid);
                    },
                    width: 150,
                    scope: this
                },{
                    xtype: 'checkbox',
                    id:'readChx',
                    fieldLabel: kimios.lang('Read'),
                    text: kimios.lang('Read'),
                    scope: this
                },
                {
                    xtype: 'checkbox',
                    id:'writeChx',
                    fieldLabel: kimios.lang('Write'),
                    text: kimios.lang('Write'),
                    scope: this
                },
                {
                    xtype: 'checkbox',
                    id:'faChx',
                    fieldLabel: kimios.lang('FullAccess'),
                    text: kimios.lang('FullAccess'),
                    scope: this
                }]
        });
        config.items.push( {
            xtype: 'checkbox',
            id:'shareUserNotify',
            fieldLabel: kimios.lang('ShareUserNotify'),
            text: kimios.lang('ShareUserNotify'),
            scope: this
        })



        config.items.push({
                xtype: 'compositefield',
                items: [{
                        xtype: 'combo',
                        id: 'shareMail',
                        plugins: [ Ext.ux.FieldLabeler ],
                        store: [],
                        mode: 'local',
                        triggerAction: 'query',
                        typeAhead: true,
                        fieldLabel: kimios.lang('ShareSendTo'),
                        name: 'recipients',
                        displayField: 'emailAddress',
                        valueField: 'emailAdddress'
                    }, {
                        xtype: 'button',
                        text: kimios.lang('ShareAddRecipient'),
                        listeners: {
                            click: {
                                fn: function(){

                                }
                            }
                        }
                    }
                ]
            }
        );
        config.items.push({
            id:'shareMailSubject',
            fieldLabel: kimios.lang('ShareSubject'),
            name: 'subject'
        });
        config.items.push( {
            xtype: 'htmleditor',
            id:'shareMailContent',
            fieldLabel: kimios.lang('ShareContent'),
            hideLabel: true,
            name: 'content',
            flex: 1,
            plugins: Ext.ux.form.HtmlEditor.plugins(),
        });

        kimios.share.MailPanel.superclass.constructor.call(this, config);
    },


    shareWith: function(){
        var me = this;
        var picker = new kimios.picker.SecurityEntityPicker({
            title: kimios.lang('ShareWith'),
            iconCls: 'admin-user-tree-node',
            entityMode: 'user',
            singleSelect: true
        });
        picker.on('entitySelected', function (records) {
            var r = records[0];
            picker.hide();
            //set display

            Ext.getCmp('shareUserDisplay').setValue(r.data.uid + '@' + r.data.source + (r.data.fullName && r.data.fullName.length > 0 ? ' - ' + r.data.fullName : ''));
            me.pickedUser = r;
        });
        picker.show();
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
                    if(Ext.getCmp('shareType')){
                        //share for 30 jours
                        if(!me.pickedUser){
                            alert(kimios.lang('ShareSelectUser'));
                            return;
                        }
                        var expDateStr = null
                        try {
                            var d = Ext.getCmp('shareExpirationDate').getValue();
                            var time = Ext.getCmp('shareExpirationTime').getValue();
                            expDateStr = Ext.util.Format.dateRenderer('d-m-Y')(d);
                            expDateStr += ' ' + time;
                        }catch(e){

                        }
                        for(var u = 0; u < me.documents.length; u++){
                            if(me.documents[u].type == 3){
                                kimios.request.ShareRequest.shareWith(
                                    me.pickedUser.data.uid,
                                    me.pickedUser.data.source,
                                    me.documents[u].uid,
                                    Ext.getCmp('readChx').getValue(),
                                    Ext.getCmp('writeChx').getValue(),
                                    Ext.getCmp('faChx').getValue(),
                                    expDateStr,
                                    Ext.getCmp('shareUserNotify').getValue(), function(){
                                        w.close();
                                    });
                            }
                        }

                    } else {
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