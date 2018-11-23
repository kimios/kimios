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
Ext.onReady(function () {
    var defaultLang = kimios.getLanguage();
    Ext.QuickTips.init();
    kimios.store.getLangStore(defaultLang).load({
        callback: function (records, options, success) {
            new kimios.explorer.Viewport({
                checkSession: 120, // in seconds
                i18n: new kimios.i18n.Internationalization({
                    lang: defaultLang,
                    records: records
                }),
                afterBuild: function () {
                    var newsPanel = new kimios.explorer.NewsPanel({});
                    Ext.getCmp('kimios-center-panel').add(newsPanel);
                    Ext.getCmp('kimios-center-panel').setActiveTab(newsPanel);
                    // call
                    var store = new Ext.data.JsonStore({
                        url: srcContextPath + '/register?action=check',
                        root: 'result',
                        fields: [
                            {
                                name: 'success',
                                type: 'boolean'
                            }
                        ]
                    });

                    store.load({
                        callback: function (rec) {
                            if (!rec[0].data.success) {
                                new Ext.Window({
                                    id: 'registrationWindowId',
                                    title: 'Registration',
                                    layout: 'fit',
                                    width: 500,
                                    height: 200,
                                    modal: true,
                                    items: [
                                        new kimios.FormPanel({
                                            padding: '8px',
                                            items: [
                                                {
                                                    title: kimios.lang('UserName'),
                                                    xtype: 'fieldset',
                                                    items: [
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'email',
                                                            fieldLabel: kimios.lang('Email') + ' <span style="color:red;">*</span>',
                                                            id: 'registrationFormEmail',
                                                            anchor: '100%',
                                                            emptyText: 'user@domain'
                                                        },
                                                        {
                                                            xtype: 'checkbox',
                                                            name: 'telemetryLink',
                                                            fieldLabel: kimios.lang('TelemetryStatShare'),
                                                            id: 'registrationFormStatShare',
                                                            anchor: '100%'
                                                        }
                                                    ]
                                                },
                                                {
                                                    title: kimios.lang('More'),
                                                    xtype: 'fieldset',
                                                    checkboxToggle: true,
                                                    collapsed: true,
                                                    previousHeight: 0,
                                                    items: [
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'firstname',
                                                            fieldLabel: kimios.lang('Firstname') + ' <span style="color:red;">*</span>',
                                                            id: 'registrationFormFirstname',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'lastname',
                                                            fieldLabel: kimios.lang('Lastname') + ' <span style="color:red;">*</span>',
                                                            id: 'registrationFormLastname',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'address',
                                                            fieldLabel: kimios.lang('Address'),
                                                            id: 'registrationFormAddress',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'zipCode',
                                                            fieldLabel: kimios.lang('ZipCode'),
                                                            id: 'registrationFormZipCode',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'city',
                                                            fieldLabel: kimios.lang('City'),
                                                            id: 'registrationFormCity',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'state',
                                                            fieldLabel: kimios.lang('State'),
                                                            id: 'registrationFormState',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textfield',
                                                            name: 'number',
                                                            fieldLabel: kimios.lang('Number'),
                                                            id: 'registrationFormNumber',
                                                            anchor: '100%'
                                                        },{
                                                            xtype: 'textfield',
                                                            name: 'occupation',
                                                            fieldLabel: kimios.lang('Occupation'),
                                                            id: 'registrationFormOccupation',
                                                            anchor: '100%'
                                                        },
                                                        {
                                                            xtype: 'textarea',
                                                            name: 'comment',
                                                            fieldLabel: kimios.lang('Comment'),
                                                            id: 'registrationFormComment',
                                                            anchor: '100%',
                                                            height: 40
                                                        }
                                                    ],
                                                    listeners: {
                                                        scope: this,
                                                        beforeexpand: function (panel) {
                                                            panel.previousHeight = panel.getHeight();
                                                        },
                                                        expand: function (panel) {
                                                            Ext.getCmp('registrationWindowId').setHeight(Ext.getCmp('registrationWindowId').getHeight()
                                                                + panel.getHeight()
                                                                - panel.previousHeight);
                                                        },
                                                        beforecollapse: function (panel) {
                                                            panel.previousHeight = panel.getHeight();
                                                        },
                                                        collapse: function (panel) {
                                                            Ext.getCmp('registrationWindowId').setHeight(Ext.getCmp('registrationWindowId').getHeight()
                                                                + panel.getHeight()
                                                                - panel.previousHeight);
                                                        },
                                                    }
                                                }
                                            ],
                                            buttons: [
                                                {
                                                    text: kimios.lang('DMSSubmitButtonLabel'),
                                                    handler: function () {
                                                        if (!Ext.getCmp('registrationFormFirstname').getValue() || !Ext.getCmp('registrationFormLastname').getValue() || !Ext.getCmp('registrationFormEmail').getValue()) {
                                                            Ext.Msg.alert(
                                                                kimios.lang('Registration'),
                                                                kimios.lang('RegistrationMissing') + '.');
                                                            return;
                                                        }


                                                        var regObj = {
                                                            firstname: Ext.getCmp('registrationFormFirstname').getValue(),
                                                            lastname: Ext.getCmp('registrationFormLastname').getValue(),
                                                            email: Ext.getCmp('registrationFormEmail').getValue(),
                                                            number: Ext.getCmp('registrationFormNumber').getValue(),
                                                            address: Ext.getCmp('registrationFormAddress').getValue(),
                                                            city: Ext.getCmp('registrationFormCity').getValue(),
                                                            state: Ext.getCmp('registrationFormState').getValue(),
                                                            zipCode: Ext.getCmp('registrationFormZipCode').getValue(),
                                                            occupation: Ext.getCmp('registrationFormOccupation').getValue(),
                                                            comment: Ext.getCmp('registrationFormComment').getValue(),
                                                            shareStats: Ext.getCmp('registrationFormStatShare').getValue()
                                                        };

                                                        var str = encodeURI(JSON.stringify(regObj));

                                                        var store = new Ext.data.JsonStore({
                                                            url: srcContextPath + '/register?action=register&content=' + str,
                                                            root: 'result',
                                                            fields: [
                                                                {
                                                                    name: 'success',
                                                                    type: 'boolean'
                                                                }
                                                            ]
                                                        });
                                                        store.load({
                                                            callback: function (rec) {
                                                                Ext.getCmp('registrationWindowId').close();
                                                                if (rec[0]) {
                                                                    //true
                                                                    if (rec[0].data.success) {
                                                                        Ext.Msg.alert(
                                                                            kimios.lang('Registration'),
                                                                            kimios.lang('RegistrationSuccess') + '.');
                                                                    }
                                                                    //false
                                                                    else {
                                                                        Ext.Msg.alert(
                                                                            kimios.lang('Registration'),
                                                                            kimios.lang('RegistrationError') + '.');
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            ]
                                        })
                                    ]
                                }).show();
                            }
                        }
                    });
                }
            });
        }
    });
});

