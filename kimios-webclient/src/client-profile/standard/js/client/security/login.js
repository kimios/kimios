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
    Ext.QuickTips.init();

    var defaultLang = kimios.getLanguage();
    kimios.store.getLangStore(defaultLang).load({
        callback: function (records, options, success) {
            var i18n = new kimios.i18n.Internationalization({
                lang: defaultLang,
                records: records
            });

            var comboSources = new kimios.form.AuthenticationSourceField({fieldLabel: i18n.getValue('AuthenticationSource')});
            comboSources.init();

            var usernameField = new Ext.form.TextField({
                id: 'login-field',
                name: 'login-dms',
                fieldLabel: i18n.getValue('DMSAuthLoginLabel'),
                allowBlank: false,
                blankText: i18n.getValue('UsernameRequired')
            });

            var passwordField = new Ext.form.TextField({
                id: 'password-field',
                name: 'password-dms',
                fieldLabel: i18n.getValue('DMSAuthPasswordLabel'),
                inputType: 'password',
                allowBlank: false,
                blankText: i18n.getValue('PasswordRequired')
            });

            var login = new kimios.FormPanel({
                i18n: i18n,
                border: false,
                title:i18n.getValue('DMSAuthFormTitle'),
                width: 300,
                height: 165,
                renderTo: 'login-area',
                defaultType: 'textfield',
                monitorValid: true,
                bodyStyle: 'padding:15px 20px 10px 20px;',
//                bodyStyle: 'padding:30px;',
                labelWidth: 110,
                defaults: {
                    selectOnFocus: true,
                    labelStyle: 'font-size: 11px;',
                    style: 'font-size: 11px',
                    anchor: '100%'
                },
                items: [
                    usernameField,
                    passwordField,
                    comboSources
                ],
                buttonAlign: 'right',
                fbar: [
                    {
                        text: i18n.getValue('DMSSubmitButtonLabel'),
                        formBind: true,
                        handler: function () {
                            dmsLogin();
                        }
                    }
                ],
                listeners: {
                    render: function () {
                        usernameField.focus(false, 250);
                    }
                }
            });

            var dmsLogin = function (e) {
                if (!e || e.getKey() == e.ENTER) {
                    login.getForm().getEl().dom.action = getBackEndUrl('Security');
                    login.getForm().submit({
                        url: getBackEndUrl('Security'),
                        params: {
                            action: 'login',
                            username: usernameField.getValue(),
                            password: passwordField.getValue(),
                            domain: comboSources.getValue()
                        },
                        success: function (form, action) {
                            if (action.result.success == true) {
                                document.location.href = contextPath + '/../logged.jsp';
                            } else {
                                Ext.Msg.show({
                                    msg: i18n.getValue('InvalidAuth'),
                                    buttons: Ext.Msg.OK,
                                    icon: Ext.MessageBox.ERROR
                                });
                                Ext.getCmp('password-field').reset();
                            }
                        },
                        failure: function (form, action) {
                            Ext.Msg.show({
                                msg: i18n.getValue('InvalidAuth'),
                                buttons: Ext.Msg.OK,
                                icon: Ext.MessageBox.ERROR
                            });
                            Ext.getCmp('password-field').reset();
                        }
                    });
                }
            };

            usernameField.on('specialkey', function (field, e) {
                dmsLogin(e);
            });
            passwordField.on('specialkey', function (field, e) {
                dmsLogin(e);
            });
            comboSources.on('specialkey', function (field, e) {
                dmsLogin(e);
            });

//            var win = new Ext.Window({
//                title: i18n.getValue('DMSAuthFormTitle'),
//                layout: 'fit',
//                width: 250,
//                height: 170,
//                closable: false,
//                resizable: false,
//                border: false,
//                items: [login],
//                listeners: {
//                    show: function () {
//                        usernameField.focus(false, 250);
//                    }
//                }
//            });
//            win.show();
        }
    });
});

