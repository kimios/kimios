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
kimios.MyAccountPanel = Ext.extend(kimios.FormPanel, {
    constructor : function(config) {
        var form = this;
        this.uidTextField = new Ext.form.Hidden( {
            fieldLabel : kimios.lang('UserUid'),
            name : 'uid',
            editable : false,
            value : currentUser=='null'?'':currentUser
        });
        this.firstNameTextField = new Ext.form.TextField( {
            fieldLabel : kimios.lang('Firstname'),
            name : 'firstName',
            value : currentFirstName=='null'?'':currentFirstName
        });
        this.lastNameTextField = new Ext.form.TextField( {
            fieldLabel : kimios.lang('Lastname'),
            name : 'lastName',
            value : currentLastName=='null'?'':currentLastName
        });
        this.phoneNumberTextField = new Ext.form.TextField( {
            fieldLabel : kimios.lang('PhoneNumber'),
            name : 'phoneNumber',
            value : currentPhoneNumber=='null'?'':currentPhoneNumber
        });
        this.passwordField = new Ext.form.TextField( {
            name : 'password',
            inputType : 'password',
            fieldLabel : kimios.lang('NewPassword')
        });
        this.confirmPasswordField = new Ext.form.TextField( {
            name : 'confirm-password',
            inputType : 'password',
            fieldLabel : kimios.lang('ConfirmNewPassword')
        });
        this.emailField = new Ext.form.TextField( {
            fieldLabel : kimios.lang('UserMail'),
            name : 'mail',
            vtype : 'email',
            value : currentMail=='null'?'':currentMail
        });
        this.domainField = new Ext.form.Hidden( {
            name : 'authenticationSourceName',
            value : currentSource=='null'?'':currentSource
        });
        this.enabledUser = new Ext.form.Hidden( {
            name : 'enabled',
            value : 'on'
        });
        this.border = false;
        this.autoScroll = true;
        this.monitorValid = true;
        this.labelWidth = 175;
        this.bodyStyle = 'padding:10px;background-color:transparent;';
        this.defaults = {
            anchor : '100%',
            selectOnFocus : true,
            style : 'font-size: 11px',
            labelStyle : 'font-size: 11px; font-weight:bold;'
        };
        this.items = [ this.uidTextField, this.firstNameTextField, this.lastNameTextField, this.phoneNumberTextField,
        this.passwordField, this.confirmPasswordField, this.emailField,
        this.domainField, this.enabledUser ],
        this.fbar = [
        '->',
        {
            text : kimios.lang('Save'),
            scope : this,
            handler : function() {
                if(this.passwordField.getValue().length > 0 || this.confirmPasswordField.getValue().length > 0){
                    var match = this.passwordField.getValue() == this.confirmPasswordField.getValue();
                    var empty = this.passwordField.getValue() == '' || this.confirmPasswordField.getValue() == '';
                    if (empty || !match){
                        Ext.MessageBox.alert(kimios.lang('InvalidPassword'), kimios.lang('NoPasswordMatchJS'));
                        return;
                    }
                    if(!kimios.checkPassword(this.passwordField.getValue())){
                        return;
                    }

                }

                var f = this;
                kimios.request.AdminRequest.saveMyAccount(form, function(){
                    var obj = f.getForm().getValues();
                    currentUser = obj.uid;
                    currentSource = obj.authenticationSourceName;
                    currentName = obj.firstName + ' ' + obj.lastName;
                    currentPhoneNumber = obj.phoneNumber;
                    currentFirstName = obj.firstName;
                    currentLastName = obj.lastName;
                    currentMail = obj.mail;
                    Ext.getCmp('kimios-my-account').close();
                    var html = '<span style="color:gray;">'+kimios.lang('Welcome')+', ';
                    if (obj.name != '') html += currentName;
                    else html += obj.uid + '@' + obj.authenticationSourceName;
                    html += '</span>';
                    kimios.explorer.getToolbar().refreshLanguage(html);
                    kimios.Info.msg('', kimios.lang('UpdatedAccount'));
                });
            }
        }, {
            text : kimios.lang('Close'),
            handler : function() {
                Ext.getCmp('kimios-my-account').close();
            }
        }
        ];
        kimios.MyAccountPanel.superclass.constructor.call(this, config);
    }
});
