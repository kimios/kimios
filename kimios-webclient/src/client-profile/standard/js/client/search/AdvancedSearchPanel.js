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
    isSearchMode: false,

    search: function () {
        this.isSearchMode = true;
        kimios.explorer.getActivePanel().advancedSearch({
            DocumentName: this.nameField.getValue(),
            DocumentBody: this.textField.getValue(),
            DocumentUid: this.uidField.getValue(),
            DocumentParent: this.locationField.getValue(),
            DocumentTypeUid: this.documentTypeField.getValue() == -1 ? '' : this.documentTypeField.getValue(),
            DocumentVersionUpdateDate_from: this.documentDateFromField.getValue() ? this.documentDateFromField.getValue().format('Y-m-d') : null,
            DocumentVersionUpdateDate_to: this.documentDateToField.getValue() ? this.documentDateToField.getValue().format('Y-m-d') : null
        }, this.form2);
        this.clearButton.setDisabled(false);
    },

    clearForm: function () {
        this.searchRequestId = null;
        this.searchRequestName = null;
        this.saveButton.setTooltip(
            this.searchRequestId ? kimios.lang('Update') : kimios.lang('Create')
        );
        this.nameField.setValue("");
        this.uidField.setValue("");
        this.textField.setValue("");
        this.locationField.setValue("");
        this.documentTypeField.setValue("");
        this.documentDateFromField.setValue("");
        this.documentDateToField.setValue("");
        this.form2.removeAll();
        this.clearButton.setDisabled(true);
    },


    constructor: function (config) {
        var _t = this;
        this.layout = 'border';
        this.bodyStyle = 'background-color:#eee;';

        this.submitButton = new Ext.Button({
            text: kimios.lang('SearchEmptyText'),
            scope: this,
            iconCls: 'search',
            handler: function () {
                this.search();
            }
        });

        this.saveButton = new Ext.Button({
            text: _t.searchRequestId ? kimios.lang('Update') : kimios.lang('Create'),
            scope: this,
            iconCls: 'save',
            handler: function () {
                var fields = this.form2.getForm().getFieldValues();
                var obj = "({";
                for (var key in fields) {
                    var value = null;

                    // is date
                    console.log(key);
                    console.log(this.form2.metaFieldsMapping);
                    if (fields[key] && fields[key] instanceof Date)
                        value = fields[key] ? fields[key].format('Y-m-d') : '';
                    else
                    //handle meta feed linked field (String or List)
                    if(this.form2.metaFieldsMapping.filter(function(it){
                        return it.crit == key;
                         }).length == 1){
                        var tmpVal = fields[key];
                        if(tmpVal.indexOf(',') > -1){
                            value = JSON.stringify(tmpVal.split(','));
                        } else {
                            value = JSON.stringify([ fields[key] ]);
                        }
                    } else
                        value = fields[key] ? fields[key] : '';

                    obj += "'" + key + "':'" + value + "',";
                }
                if (obj.length > 2) {
                    obj = obj.substring(0, obj.length - 1);
                }
                var params = eval(obj + "})");
                params.DocumentBody = this.textField.getValue();
                params.DocumentName = this.nameField.getValue();
                params.DocumentUid = this.uidField.getValue();
                params.DocumentTypeUid = this.documentTypeField.getValue() == -1 ? '' : this.documentTypeField.getValue();
                params.DocumentParent = this.locationField.getValue();
                if (this.documentDateFromField.getValue())
                    params.DocumentVersionUpdateDate_from = this.documentDateFromField.getValue().format('Y-m-d');
                if (this.documentDateToField.getValue())
                    params.DocumentVersionUpdateDate_to = this.documentDateToField.getValue().format('Y-m-d');

                var searchRequestId = this.searchRequestId;
                var searchRequestName = this.searchRequestName;
                var _this = this;

                Ext.MessageBox.prompt(
                    kimios.lang('SearchSaveButton'),
                    kimios.lang('SearchEnterName'),
                    function (btn, value) {
                        if (btn == 'ok') {
                            if (searchRequestId)
                                params.searchQueryId = searchRequestId;
                            params.action = 'SaveQuery';
                            params.searchQueryName = value;
                            kimios.ajaxRequest('Search', params, function () {
                                kimios.Info.msg(kimios.lang('SearchTab'), kimios.lang('SearchSaveDone'));
                                Ext.getCmp('kimios-queries-panel').getStore().reload();
                                _this.saveButton.setTooltip(kimios.lang('Update'));
                            });
                        }
                    },
                    this, false, searchRequestName ? searchRequestName : kimios.lang('SearchNewBookmark'));
            }
        });

        this.clearButton = new Ext.Button({
            text: kimios.lang('ClearField'),
            scope: this,
            disabled: true,
            iconCls: 'delete',
            handler: function (btn) {
                this.clearForm();
            }
        });

        this.form1 = new kimios.FormPanel({
            region: 'west',
            width: 350,
            autoScroll: true,
            border: false,
            margins: '5 10 5 10',
            bodyStyle: 'padding:5px;',
            labelWidth: 120,
            defaults: {
                anchor: '95%',
                selectOnFocus: true,
                style: 'font-size: 11px',
                labelStyle: 'font-size: 11px;'
            },
            bodyStyle: 'background-color:transparent;padding:10px 0 10px 10px;',
            buttonAlign: 'left',
            fbar: [this.saveButton, '->', this.clearButton, this.submitButton]
        });

        this.form2 = new kimios.FormPanel({
            region: 'center',
            border: false,
            margins: '5 10 5 10',
            bodyStyle: 'padding:5px;',
            labelWidth: 140,
            autoScroll: true,
            hidden: true,
            defaults: {
//                width: 200,
                anchor: '100%',
                selectOnFocus: true,
                style: 'font-size: 11px',
                labelStyle: 'font-size: 11px;'
            },
            bodyStyle: 'background-color:transparent;padding:10px 10px 10px 0;'
        });
        this.form2.addEvents('metafieldload');
        this.form2.on('metafieldload', function () {
            if (this.loadedMetadatas) {
                for (var i = 0; i < this.loadedMetadatas.length; ++i) {
                    var criteria = this.loadedMetadatas[i];
                    var query = criteria.query;
                    var fieldName = criteria.fieldName;
                    var rangeMin = criteria.rangeMin;
                    var rangeMax = criteria.rangeMax;

                    if (!rangeMin && !rangeMax) {
                        for (var j = 0; j < this.form2.items.length; ++j) {
                            var f = this.form2.items.items[j];
                            if (f.name == fieldName) {

                                f.setValue(query);
                                if(f.refreshValue){
                                    f.refreshValue(query);
                                }

                                break;
                            }
                        }
                    } else {
                        for (var j = 0; j < this.form2.items.length; ++j) {
                            var f = this.form2.items.items[j];
                            if (f.name == fieldName + '_from') {
                                f.setValue(rangeMin);
                                continue;
                            }
                            if (f.name == fieldName + '_to') {
                                f.setValue(rangeMax);
                                continue;
                            }
                        }
                    }
                }
            }
        }, this);

        this.buttonAlign = 'left';
        this.items = [this.form1, this.form2];
        kimios.search.AdvancedSearchPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.search.AdvancedSearchPanel.superclass.initComponent.apply(this, arguments);
        this.build();
    },

    // Load the advanced search form with the given SearchRequest object
    loadForm: function (searchRequest) {
        this.documentTypeField.getStore().load({
            scope: this,
            callback: function () {
                // for update
                this.searchRequestId = searchRequest.id;
                this.searchRequestName = searchRequest.name;

                this.nameField.setValue("");
                this.uidField.setValue("");
                this.textField.setValue("");
                this.locationField.setValue("");
                this.documentTypeField.setValue("");
                this.documentDateFromField.setValue("");
                this.documentDateToField.setValue("");
                this.form2.removeAll();

                // Display the advanced search panel
                this.showPanel();

                // Unserialize the search criteria list
                var obj = Ext.decode(searchRequest.criteriasListJson);
                var hasDocumentType = false;
                this.loadedMetadatas = [];
                for (var i = 0; i < obj.length; ++i) {
                    var criteria = obj[i];
                    var query = criteria.query;
                    var fieldName = criteria.fieldName;
                    var rangeMin = criteria.rangeMin;
                    var rangeMax = criteria.rangeMax;

                    if (fieldName == 'DocumentName') {
                        this.nameField.setValue(query);
                    } else if (fieldName == 'DocumentUid') {
                        this.uidField.setValue(query);
                    } else if (fieldName == 'DocumentBody') {
                        this.textField.setValue(query);
                    } else if (fieldName == 'DocumentParent') {
                        this.locationField.setValue(query);
                    } else if (fieldName == 'DocumentVersionUpdateDate') {
                        this.documentDateFromField.setValue(rangeMin);
                        this.documentDateToField.setValue(rangeMax);
                    } else if (fieldName == 'DocumentTypeUid') {
                        this.documentTypeField.setValue(query);
                        this.documentTypeField.fireEvent('select');
                    }

                    // Meta Data parsing when document type set
                    else {
                        if (fieldName.indexOf('MetaDataList') == 0) {
                            var tabData = eval('(' + criteria.query + ')');
                            var strQ = '';
                            for(var u = 0; u < tabData.length; u++){
                                strQ += tabData[u] + ','
                            };
                            if(strQ.length > 0){
                                strQ = strQ.substr(0, strQ.length - 1);
                            }
                            criteria.query = strQ;
                            this.loadedMetadatas.push(criteria);
                        } else if (fieldName.substr(0, 8) == 'MetaData') {
                            this.loadedMetadatas.push(criteria);
                        }

                    }
                }

                this.saveButton.setTooltip(
                    this.searchRequestId ? kimios.lang('Update') : kimios.lang('Create')
                );

                // to fix location field resize on loadForm
                this.locationField.setValue(this.locationField.getValue());

                this.clearButton.setDisabled(false);
            }
        });


    },

    loadFromCriterias: function (criteriaString) {
        var obj = eval('(' + criteriaString + ')');
        for (var key in obj) {
            var value = null;

            // is date
            if (fields[key] && fields[key] instanceof Date)
                value = fields[key] ? fields[key].format('Y-m-d') : '';
            else
                value = fields[key] ? fields[key] : '';

            obj += "'" + key + "':'" + value + "',";
        }
    },

    build: function () {
        this.form1.removeAll();
        this.form2.removeAll();

        var setFieldLabelHandler = function (text) {
            this.el.up('.x-form-item', 10, true).child('.x-form-item-label').update(text);
        };

        this.nameField = new Ext.form.TextField({
            name: 'DocumentName',
            fieldLabel: kimios.lang('DocumentName'),
            labelSeparator: kimios.lang('LabelSeparator'),
            setFieldLabel: setFieldLabelHandler
        });
        this.uidField = new Ext.form.NumberField({
            name: 'DocumentUid',
            fieldLabel: kimios.lang('DocNum'),
            labelSeparator: kimios.lang('LabelSeparator'),
            setFieldLabel: setFieldLabelHandler
        });
        this.textField = new Ext.form.TextField({
            name: 'DocumentBody',
            fieldLabel: kimios.lang('SearchText'),
            labelSeparator: kimios.lang('LabelSeparator'),
            setFieldLabel: setFieldLabelHandler
        });
        this.locationField = new kimios.form.DMEntityField({
            name: 'dmEntityUid',
            fieldLabel: kimios.lang('InFolder'),
            labelSeparator: kimios.lang('LabelSeparator'),
            setFieldLabel: setFieldLabelHandler
        });
        this.documentTypeField = new kimios.form.DocumentTypeField({
            fieldLabel: kimios.lang('DocumentType'),
            name: 'DocumentTypeUid',
            displayField: 'name',
            valueField: 'uid',
            hiddenName: 'documentType',
            width: 200,
            labelSeparator: kimios.lang('LabelSeparator'),
            setFieldLabel: setFieldLabelHandler
        });
        this.documentDateFromField = new Ext.form.DateField({
            name: 'DocumentVersionUpdateDate_from',
            fieldLabel: kimios.lang('Date') + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MIN</span>',
            format: 'Y-m-d',
            editable: false,
            labelSeparator: kimios.lang('LabelSeparator')
        });
        this.documentDateToField = new Ext.form.DateField({
            name: 'DocumentVersionUpdateDate_to',
            fieldLabel: kimios.lang('Date') + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MAX</span>',
            format: 'Y-m-d',
            editable: false,
            labelSeparator: kimios.lang('LabelSeparator')
        });

        //TODO: add fields regarding document/version creation/update date and owner/ownerSource
        this.documentTypeField.on('select', function (store, metasRecords, options) {
            kimios.store.getMetasStore(this.documentTypeField.getValue()).on('load', function (store, metasRecords, options) {
                this.form2.removeAll();
                var fields = [];
                var metaFieldsMapping = [];
                this.form2.metaFieldsMapping =  metaFieldsMapping;
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
                                    fieldLabel: name,
                                    value: value,
                                    labelSeparator: kimios.lang('LabelSeparator')
                                }));
                            } else {
                                /*fields.push(new kimios.form.MetaFeedField({
                                    name: 'MetaDataString_' + uid,
                                    metaFeedUid: metaFeedUid,
                                    fieldLabel: name,
                                    value: value,
                                    labelSeparator: kimios.lang('LabelSeparator')
                                }));*/
                                metaFieldsMapping.push({crit: 'MetaDataString_' + uid, metaFeed: metaFeedUid});
                                fields.push(new kimios.form.MetaFeedMultiField({
                                    name: 'MetaDataString_' + uid,
                                    metaFeedUid: metaFeedUid,
                                    fieldLabel: name,
                                    passedValue: value,
                                    labelSeparator: kimios.lang('LabelSeparator')
                                }));
                            }
                            break;
                        case 2:
                            //int type
                            fields.push(new Ext.form.NumberField({
                                name: 'MetaDataNumber_' + uid + '_from',
                                fieldLabel: name + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MIN</span>',
                                value: value,
                                labelSeparator: kimios.lang('LabelSeparator')
                            }));
                            fields.push(new Ext.form.NumberField({
                                name: 'MetaDataNumber_' + uid + '_to',
                                fieldLabel: name + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MAX</span>',
                                value: value,
                                labelSeparator: kimios.lang('LabelSeparator')
                            }));
                            break;
                        case 3:
                            //date type
                            fields.push(new Ext.form.DateField({
                                name: 'MetaDataDate_' + uid + '_from',
                                fieldLabel: name + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MIN</span>',
                                format: 'Y-m-d',
                                value: value,
                                editable: false,
                                labelSeparator: kimios.lang('LabelSeparator')
                            }));
                            fields.push(new Ext.form.DateField({
                                name: 'MetaDataDate_' + uid + '_to',
                                fieldLabel: name + '&nbsp;&nbsp;<span style="color:#45af43;font-weight:bolder;font-size:.8em;">MAX</span>',
                                format: 'Y-m-d',
                                value: value,
                                editable: false,
                                labelSeparator: kimios.lang('LabelSeparator')
                            }));
                            break;
                        case 4:
                            //boolean type
                            fields.push(new Ext.form.Checkbox({
                                name: 'MetaDataBoolean_' + uid,
                                fieldLabel: name,
                                checked: value == 'true' ? true : false,
                                labelSeparator: kimios.lang('LabelSeparator')
                            }));
                            break;
                        case 5:
                            //list type
                            if (metaFeedUid == -1) {
                                fields.push(new Ext.form.TextField({
                                    name: 'MetaDataList_' + uid,
                                    fieldLabel: name,
                                    value: value,
                                    labelSeparator: kimios.lang('LabelSeparator')
                                }));
                            } else {
                                metaFieldsMapping.push({crit: 'MetaDataList_' + uid, metaFeed: metaFeedUid});
                                fields.push(new kimios.form.MetaFeedMultiField({
                                    name: 'MetaDataList_' + uid,
                                    metaFeedUid: metaFeedUid,
                                    fieldLabel: name,
                                    passedValue: value,
                                    labelSeparator: kimios.lang('LabelSeparator')
                                }));
                            }
                            break;
                    }
                }, this);
                this.form2.add(fields);
                this.form2.setVisible(fields.length > 0);
                this.form1.doLayout();
                this.form2.doLayout();
                this.form2.fireEvent('metafieldload');
                this.doLayout();
            }, this);
        }, this);
        this.form1.add(this.nameField);
        this.form1.add(this.uidField);
        this.form1.add(this.textField);
        this.form1.add(this.locationField);
        this.form1.add(this.documentDateFromField);
        this.form1.add(this.documentDateToField);
        this.form1.add(this.documentTypeField);
        this.form1.doLayout();
        this.form2.doLayout();
        this.doLayout();
    },

    showPanel: function () {
        kimios.explorer.getActivePanel().searchToolbar.searchField.isSearchMode = false;
        kimios.explorer.getActivePanel().advancedSearchPanel.isSearchMode = true;

        this.setVisible(true);
        var st = kimios.explorer.getActivePanel().searchToolbar;
        var bt = kimios.explorer.getActivePanel().breadcrumbToolbar;
        st.disable();
        bt.disable();
        kimios.explorer.getActivePanel().contextToolbar.hide();
        kimios.explorer.getToolbar().advancedSearchButton.toggle(true, true);
        kimios.explorer.getViewport().centerPanel.doLayout();

        // auto set document parent
//        var path = bt.currentPath;
//        if (path) this.locationField.setValue(path);

        // field resize bug fix
        this.locationField.setValue(this.locationField.getValue());

        // clean paging toolbar page counter
        kimios.explorer.getActivePanel().pagingToolBar.bindStore(
            kimios.explorer.getActivePanel().gridPanel.getStore(), false);
        kimios.explorer.getActivePanel().pagingToolBar.updateInfo();


    },

    hidePanel: function () {
        kimios.explorer.getActivePanel().searchToolbar.searchField.isSearchMode = true;
        kimios.explorer.getActivePanel().advancedSearchPanel.isSearchMode = false;


        this.setVisible(false);
        var st = kimios.explorer.getActivePanel().searchToolbar;
        var bt = kimios.explorer.getActivePanel().breadcrumbToolbar;
        st.enable();
        bt.enable();
        kimios.explorer.getActivePanel().contextToolbar.show();
        kimios.explorer.getToolbar().advancedSearchButton.toggle(false, true);
        st.searchField.setValue(st.searchField.getValue()); // fix
//        st.searchField.setValue('');

        var ap = kimios.explorer.getActivePanel();
        ap.hidePagingToolBar();


        kimios.explorer.getViewport().centerPanel.doLayout();
        this.loadedMetadatas = null;
        this.locationField.setValue(null);

        this.clearForm();
    },

    refreshLanguage: function () {
        if (!this.isVisible())
            return;
        this.setTitle(kimios.lang('AdvancedSearch'));
        this.clearButton.setTooltip(kimios.lang('ClearField'));
        this.submitButton.setText(kimios.lang('SearchEmptyText'));
        this.saveButton.setTooltip(
            this.searchRequestId ? kimios.lang('Update') : kimios.lang('Create')
        );

        this.nameField.setFieldLabel(kimios.lang('DocumentName'));
        this.uidField.setFieldLabel(kimios.lang('DocNum'));
        this.textField.setFieldLabel(kimios.lang('SearchText'));
        this.locationField.setFieldLabel(kimios.lang('InFolder'));
        this.documentTypeField.setFieldLabel(kimios.lang('DocumentType'));

        this.doLayout();
    }
})
;
