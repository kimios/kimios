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
kimios.properties.DMEntityPanel = Ext.extend(kimios.FormPanel, {
    constructor: function (config) {
        this.dmEntityPojo = config.dmEntityPojo;
        this.isWorkspace = this.dmEntityPojo.type == 1;
        this.isFolder = this.dmEntityPojo.type == 2;
        this.isDocument = this.dmEntityPojo.type == 3;
        this.isUpdateMode = this.dmEntityPojo.uid != null;
        this.parentPanel = config.propertiesPanel;
        this.title = this.getDMEntityType(this.dmEntityPojo.type);
        this.iconCls = 'txt';
        this.loaded = true;
        this.loadingRequired = false;
        this.border = false;
        this.bodyStyle = 'padding:10px;background-color:transparent;';
        this.labelWidth = 200;
        this.autoScroll = true;
        this.readOnly = config.readOnly;
        this.documentFileItem = config.documentFileItem;
        this.defaults = {
            width: 200,
            style: 'font-size: 11px',
            labelStyle: 'font-size: 11px;font-weight:bold;'
        };

        this.itemsArray = [];

        if (this.isUpdateMode == true) {
            this.uidField = new Ext.form.DisplayField({
                name: 'uid',
                fieldLabel: kimios.lang('DocNum'),
                value: this.dmEntityPojo.uid
            });
            this.itemsArray.push(this.uidField);
        }

        if (this.isFolder == true || this.isDocument == true) {
            this.positionField = new Ext.form.DisplayField({
                name: 'position',
                anchor: '100%',
                fieldLabel: kimios.lang('Position'),
                value: (this.dmEntityPojo.uid != null ? this.dmEntityPojo.path.substr(0, this.dmEntityPojo.path.lastIndexOf('/')) : this.dmEntityPojo.path)
            });
            this.itemsArray.push(this.positionField);
        }

        var nameString = null;
        if (this.isWorkspace == true) {
            nameString = kimios.lang('WorkspaceName');
        } else if (this.isFolder == true) {
            nameString = kimios.lang('FolderName');
        } else if (this.isDocument == true) {
            nameString = kimios.lang('DocumentName');
        }

        this.nameField = new Ext.form.TextField({
            name: 'name',
            fieldLabel: nameString,
            value: this.dmEntityPojo.name,
            selectOnFocus: true
        });
        this.itemsArray.push(this.nameField);

        if (this.isDocument == true && this.isUpdateMode == true) {
            var extension = ' (' + this.dmEntityPojo.extension.toUpperCase() + ')';
            this.extensionField = new Ext.form.DisplayField({
                name: 'extension',
                fieldLabel: kimios.lang('DocumentType'),
                value: (this.dmEntityPojo.documentTypeName != '' ? this.dmEntityPojo.documentTypeName : kimios.lang('Document')) + extension
            });
            this.itemsArray.push(this.extensionField);
        }

        if (this.isDocument == true && this.isUpdateMode == true) {
            this.sizeField = new Ext.form.DisplayField({
                name: 'length',
                fieldLabel: kimios.lang('Size'),
                value: (this.dmEntityPojo.length / 1024).toFixed(2) + ' ' + kimios.lang('Kb')
            });
            this.itemsArray.push(this.sizeField);
        }

        if (this.isUpdateMode == true) {
            this.authorField = new Ext.form.DisplayField({
                name: 'author',
                fieldLabel: kimios.lang('Author'),
                value: this.dmEntityPojo.owner + '@' + this.dmEntityPojo.ownerSource
            });

            this.creationDateField = new Ext.form.DisplayField({
                name: 'creationDate',
                fieldLabel: kimios.lang('CreationDate'),
                value: kimios.date(this.dmEntityPojo.creationDate)
            });
            this.itemsArray.push(this.authorField);
            this.itemsArray.push(this.creationDateField);
        }

        if ((this.isFolder || this.isDocument) && this.isUpdateMode == false) {
            this.inheritedPermissionsField = new Ext.form.Checkbox({
                name: 'inheritedPermissions',
                fieldLabel: kimios.lang('SecurityInherited')
            });
            this.itemsArray.push(this.inheritedPermissionsField);
        }

        if (this.dmEntityPojo.checkedOut == true) {
            this.itemsArray.push(new Ext.form.DisplayField({
                anchor: '-20',
                fieldLabel: kimios.lang('Checkedout'),
                value: '<span style="color:red;"><span style="font-weight:bold;">' + this.dmEntityPojo.checkoutUser + '@' + this.dmEntityPojo.checkoutUserSource + '</span> (' + kimios.date(this.dmEntityPojo.checkoutDate) + ')</span>'
            }));
        }

        //let in last position (important)
        if (this.isDocument && this.isUpdateMode == false) {

            //prepare form fields for datas
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'actionField',
                name: 'actionUpload',
//        value: 'AddDocument'
                value: 'AddDocumentWithProperties'
            }));
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'securityField',
                name: 'sec',
                value: ''
            }));
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'dtUidField',
                name: 'documentTypeUid',
                value: ''
            }));
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'metaValuesField',
                name: 'metaValues',
                value: ''
            }));
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'folderUidField',
                name: 'folderUid',
                value: this.dmEntityPojo.parentUid
            }));
            this.itemsArray.push(new Ext.form.Hidden({
                id: 'progressIdField',
                name: 'UPLOAD_ID',
                value: ''
            }));




            if(this.documentFileItem){
                if(this.documentFileItem.name.lastIndexOf('.') > -1){
                    this.nameField.setValue(this.documentFileItem.name.substr(0, this.documentFileItem.name.lastIndexOf('.')));
                }else
                    this.nameField.setValue(this.documentFileItem.name);
            } else {

                this.uploadField = new Ext.ux.form.FileUploadField({
                    buttonText: kimios.lang('Browse'),
                    buttonOnly: false,
                    buttonOffset: 3,
                    fieldLabel: kimios.lang('DocumentFile')
                });

                this.uploadField.on('fileselected', function () {
                    if (this.nameField.getValue() == '') {
                        var n = this.uploadField.getValue().substr(0, this.uploadField.getValue().lastIndexOf('.'));
                        n = n.substr(n.lastIndexOf('\\') + 1);
                        this.nameField.setValue(n);
                    }
                }, this);

                this.itemsArray.push(this.uploadField);
            }



            //set file upload mode
            this.fileUpload = true;
            config.fileUpload = true;
        }

        this.items = [this.itemsArray];

        kimios.properties.DMEntityPanel.superclass.constructor.call(this, config);
    },

    setPojo: function (pojo) {
        this.dmEntityPojo = pojo;
        this.loaded = false;
    },

    initComponent: function () {
        kimios.properties.DMEntityPanel.superclass.initComponent.apply(this, arguments);
        if ((this.isDocument || this.isFolder) && this.isUpdateMode == false) {
            this.inheritedPermissionsField.on('check', function (checkbox, checked) {
                this.parentPanel.securityEntityPanel.setDisabled(checked);
            }, this);
        }

        this.on('activate', function () {
            this.doLayout();
        }, this);

        this.on('show', function () {
            this.nameField.focus(true, 200);
        }, this);

    },

    getDMEntityType: function (type) {
        switch (type) {
            case 1:
                return kimios.lang('Workspace');
            case 2:
                return kimios.lang('Folder');
            case 3:
                return kimios.lang('Document');
            default:
                return null;
        }
    },

    getDMEntityIconCls: function (type) {
        switch (type) {
            case 1:
                return 'dm-entity-tab-properties-workspace';
            case 2:
                return 'dm-entity-tab-properties-folder';
            case 3:
                return 'dm-entity-tab-properties-document';
            default:
                return null;
        }
    }

});
