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
kimios.UploaderWindow = Ext.extend(Ext.Window, {
    constructor:function (config) {
        this.context = config.context;
        this.dmEntityPojo = config.dmEntityPojo;
        this.handle = config.handle;
        this.title = this.dmEntityPojo.name;
        this.iconCls = kimios.util.IconHelper.getIconClass(this.dmEntityPojo.type, this.dmEntityPojo.extension);
        this.width = 500;
        this.height = 150;
        this.layout = 'fit';
        this.modal = true;
        this.itemsArray = [];

        this.actionHidden = new Ext.form.Hidden({
            id:'actionField',
            name:'actionUpload',
            value:''
        });
        this.itemsArray.push(this.actionHidden);
        this.itemsArray.push(new Ext.form.Hidden({
            id:'documentUidField',
            name:'documentUid',
            value:this.dmEntityPojo.uid
        }));
        this.itemsArray.push(new Ext.form.Hidden({
            id:'progressIdField',
            name:'UPLOAD_ID',
            value:''
        }));
        this.uploadField = new Ext.ux.form.FileUploadField({
            id:'kimios-upload-field',
            buttonText:kimios.lang('Browse'),
            buttonOnly:false,
            buttonOffset:3,
            fieldLabel:'File'
        });
        this.uploadField.on('fileselected', function () {
            if (this.uploadField.getValue() != '') {
                createNewVersionButton.enable();
                updateCurrentVersionButton.enable();
            }
        }, this);

        this.itemsArray.push(this.uploadField);
        var win = this;
        var availableActionsButtons = [];

        var createNewVersionButton = new Ext.Button({
            text:kimios.lang('CreateNewVersion'),
            scope:this,
            disabled:true,
            handler:function (btn) {
                this.actionHidden.setValue('Import');
                if (this.dmEntityPojo.extension == this.getExtension(Ext.getCmp('kimios-upload-field').value)) {
                    kimios.request.updateVersion(formPanel, this.dmEntityPojo.uid, function () {
                        kimios.explorer.getActivePanel().loadEntity();
                        kimios.Info.msg(kimios.lang('CreateNewVersion'), kimios.lang('VersionImportOK'));
                        win.close();
                        if (win.handle != undefined)
                            win.handle();
                    });
                } else {
                    kimios.MessageBox.exception({
                        exception:kimios.lang('ExtensionNotCorrect')
                    });
                }
            }
        });

        var updateCurrentVersionButton = new Ext.Button({
            text:kimios.lang('UpdateCurrentVersion'),
            scope:this,
            disabled:true,
            handler:function (btn) {
                this.actionHidden.setValue('UpdateCurrent');
                if (this.dmEntityPojo.extension == this.getExtension(Ext.getCmp('kimios-upload-field').value)) {
                    kimios.request.updateVersion(formPanel, this.dmEntityPojo.uid, function () {
                        kimios.explorer.getActivePanel().loadEntity();
                        kimios.Info.msg(kimios.lang('UpdateCurrentVersion'), kimios.lang('VersionImportOK'));
                        win.close();
                        if (win.handle != undefined)
                            win.handle();
                    });
                } else {
                    kimios.MessageBox.exception({
                        exception:kimios.lang('ExtensionNotCorrect')
                    });
                }
            }
        });

        var cancelCheckedOutButton = new Ext.Button({
            scope:this,
            text:kimios.lang('CancelCO'),
            handler:function (btn) {
                btn.disable();
                kimios.request.checkIn(this.dmEntityPojo.uid, function () {
                    kimios.explorer.getActivePanel().loadEntity();
                    kimios.Info.msg(kimios.lang('CancelCO'), kimios.lang('Completed'));
                    win.close();
                    if (win.handle != undefined) win.handle();
                });
            }
        });

        if (this.context == 'updateCurrentVersion') {
            availableActionsButtons.push('->');
            availableActionsButtons.push(updateCurrentVersionButton);
        }
        else if (this.context == 'createNewVersion') {
            availableActionsButtons.push('->');
            availableActionsButtons.push(createNewVersionButton);
        }
        else if (this.context == 'checkIn') {
            availableActionsButtons.push(cancelCheckedOutButton);
            availableActionsButtons.push('->');
            availableActionsButtons.push(createNewVersionButton);
            availableActionsButtons.push(updateCurrentVersionButton);
        }

        var formPanel = new kimios.FormPanel({
            fileUpload:true,
            border:false,
            items:this.itemsArray,
            buttonAlign:'left',
            fbar:availableActionsButtons,
            labelWidth:100,
            bodyStyle:'padding:10px;background-color:transparent;',
            defaults:{
                anchor:'100%',
                selectOnFocus:true,
                style:'font-size: 11px',
                labelStyle:'font-size: 11px;'
            }
        });

        this.items = [formPanel];
        kimios.UploaderWindow.superclass.constructor.call(this, config);
    },

    getExtension:function (filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
});
