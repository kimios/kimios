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
kimios.search.AdvancedSearchPanel = Ext.extend(Ext.Panel, {

    constructor:function (config) {
        this.layout = 'border';
        this.bodyStyle = 'background-color:transparent;',

        this.submitButton = new Ext.Button({
            text:kimios.lang('SearchEmptyText'),
            scope:this,
            handler:function () {
                kimios.explorer.getActivePanel().advancedSearch({
                    name:this.nameField.getValue(),
                    text:this.textField.getValue(),
                    uid:this.uidField.getValue(),
                    fromUid:this.locationField.hiddenUid,
                    fromType:this.locationField.hiddenType,
                    documentType:this.documentTypeField.getValue()
                }, this.form2);
            }
        });

        this.saveButton = new Ext.Button({
           text:kimios.lang('SearchSaveButton'),
           scope:this,
           handler:function () {
               //TODO: implement save
           }
       });



        this.clearButton = new Ext.Button({
            text:kimios.lang('ClearField'),
            scope:this,
            handler:function () {
                this.nameField.setValue("");
                this.uidField.setValue("");
                this.textField.setValue("");
                this.locationField.setValue("");
                this.documentTypeField.setValue("");
                this.form2.removeAll();
            }
        });

        this.form1 = new kimios.FormPanel({
            region:'west',
            width:315,
            border:false,
            margins:'5 10 5 10',
            bodyStyle:'padding:5px;',
            labelWidth:150,
            defaults:{
                width:150,
                selectOnFocus:true,
                style:'font-size: 11px',
                labelStyle:'font-size: 11px;'
            },
            bodyStyle:'background-color:transparent;',
            fbar:[this.clearButton, this.submitButton, this.saveButton]
        });

        this.form2 = new kimios.FormPanel({
            region:'center',
            border:false,
            margins:'5 10 5 10',
            bodyStyle:'padding:5px;',
            labelWidth:150,
            autoScroll:true,
            hidden:true,
            defaults:{
                width:150,
                selectOnFocus:true,
                style:'font-size: 11px',
                labelStyle:'font-size: 11px;'
            },
            bodyStyle:'background-color:transparent;'
        });

        this.buttonAlign = 'left';
        this.items = [this.form1, this.form2];
        kimios.search.AdvancedSearchPanel.superclass.constructor.call(this, config);
    },

    initComponent:function () {
        kimios.search.AdvancedSearchPanel.superclass.initComponent.apply(this, arguments);
        this.build();
    },

    build:function () {
        this.form1.removeAll();
        this.form2.removeAll();

        this.nameField = new Ext.form.TextField({
            name:'DocumentName',
            fieldLabel:kimios.lang('DocumentName'),
            labelSeparator:kimios.lang('LabelSeparator')
        });
        this.uidField = new Ext.form.NumberField({
            name:'DocumentUid',
            fieldLabel:kimios.lang('DocNum'),
            labelSeparator:kimios.lang('LabelSeparator')
        });
        this.textField = new Ext.form.TextField({
            name:'DocumentBody',
            fieldLabel:kimios.lang('SearchText'),
            labelSeparator:kimios.lang('LabelSeparator')
        });
        this.locationField = new kimios.form.DMEntityField({
            name:'dmEntityUid',
            fieldLabel:kimios.lang('InFolder'),
            labelSeparator:kimios.lang('LabelSeparator')
        });
        this.documentTypeField = new kimios.form.DocumentTypeField({
            fieldLabel:kimios.lang('DocumentType'),
            name:'DocumentTypeUid',
            displayField:'name',
            valueField:'uid',
            hiddenName:'documentType',
            width:200,
            labelSeparator:kimios.lang('LabelSeparator')
        });
        //TODO: add fields regarding document/version creation/update date and owner/ownerSource
        this.documentTypeField.on('select', function (store, metasRecords, options) {
            kimios.store.getMetasStore(this.documentTypeField.getValue()).on('load', function (store, metasRecords, options) {
                this.form2.removeAll();
                var fields = [];
                Ext.each(metasRecords, function (record, index) {
                    var type = record.get('type');
                    var uid = record.get('uid');
                    var name = record.get('name');
                    var value = record.get('value');
                    var metaFeedUid = record.get('metaFeedUid');

                    switch (type) {
                        case 1:
                            //string type
                            if (metaFeedUid == -1) {
                                fields.push(new Ext.form.TextField({
                                    name: 'MetaDataString_' + uid,
                                    fieldLabel:name,
                                    value:value,
                                    emptyText:kimios.lang('SearchText'),
                                    labelSeparator:kimios.lang('LabelSeparator')
                                }));
                            } else {
                                fields.push(new kimios.form.MetaFeedField({
                                    name:'MetaDataString_' + uid,
                                    metaFeedUid:metaFeedUid,
                                    fieldLabel:name,
                                    value:value,
                                    emptyText:kimios.lang('MetaFeed'),
                                    labelSeparator:kimios.lang('LabelSeparator')
                                }));
                            }
                            break;
                        case 2:
                            //int type
                            fields.push(new Ext.form.NumberField({
                                name: 'MetaDataNumber_' + uid + '_from',
                                fieldLabel:name + ' (min)',
                                value:value,
                                emptyText:kimios.lang('MetaNumberValue'),
                                labelSeparator:kimios.lang('LabelSeparator')
                            }));
                            fields.push(new Ext.form.NumberField({
                                name:'MetaDataNumber_' + uid + '_to',
                                fieldLabel:name + ' (max)',
                                value:value,
                                emptyText:kimios.lang('MetaNumberValue'),
                                labelSeparator:kimios.lang('LabelSeparator')
                            }));
                            break;
                        case 3:
                            //date type
                            fields.push(new Ext.form.DateField({
                                name:'MetaDataDate_' + uid + '_from',
                                fieldLabel:name + ' (min)',
                                format:'Y-m-d',
                                value:value,
                                editable:false,
                                emptyText:kimios.lang('MetaDateValue'),
                                labelSeparator:kimios.lang('LabelSeparator')
                            }));
                            fields.push(new Ext.form.DateField({
                                name:'MetaDataDate_' + uid + '_to',
                                fieldLabel:name + ' (max)',
                                format:'Y-m-d',
                                value:value,
                                editable:false,
                                emptyText:kimios.lang('MetaDateValue'),
                                labelSeparator:kimios.lang('LabelSeparator')
                            }));
                            break;
                        case 4:
                            //boolean type
                            fields.push(new Ext.form.Checkbox({
                                name:'MetaDataBoolean_' + uid,
                                fieldLabel:name,
                                checked:value == 'true' ? true : false,
                                labelSeparator:kimios.lang('LabelSeparator')
                            }));
                            break;
                    }
                }, this);
                this.form2.add(fields);
                this.form2.setVisible(fields.length > 0);
                this.form1.doLayout();
                this.form2.doLayout();
                this.doLayout();
            }, this);
        }, this);
        this.form1.add(this.nameField);
        this.form1.add(this.uidField);
        this.form1.add(this.textField);
        this.form1.add(this.locationField);
        this.form1.add(this.documentTypeField);
        this.form1.doLayout();
        this.form2.doLayout();
        this.doLayout();
    },

    showPanel:function () {
        this.setVisible(true);
        var st = kimios.explorer.getActivePanel().searchToolbar;
        st.searchField.disable();
        kimios.explorer.getViewport().centerPanel.doLayout();
    },

    hidePanel:function () {
        this.setVisible(false);
        var st = kimios.explorer.getActivePanel().searchToolbar;
        st.searchField.enable();
        st.searchField.setValue(st.searchField.getValue()); // fix
        kimios.explorer.getViewport().centerPanel.doLayout();
    },

    refreshLanguage:function () {
        this.setTitle(kimios.lang('AdvancedSearch'));
        this.clearButton.setText(kimios.lang('ClearField'));
        this.submitButton.setText(kimios.lang('SearchEmptyText'));
        this.build();
        this.doLayout();
    }
});
