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
Studio.DocumentTypes = {
    getPanel: function () {
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            hideCollapseTool: true,
            title: kimios.lang('DocumentTypes'),
            margins: '3 0 3 3',
            cmargins: '3 3 3 3',
            collapsible: true,
            rootVisible: false,
            autoScroll: true,
            root: new Ext.tree.TreeNode(),
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        documentTypesStore.reload();
                    }
                },
                {
                    id: 'plus',
                    handler: function (event, toolEl, panel) {
                        contextPanel.removeAll();
                        contextPanel.add(Studio.DocumentTypes.getDocumentTypePanel(metaFeedsStore, contextPanel, documentTypesStore));
                        contextPanel.doLayout();
                    }
                }
            ]
        });

        var contextPanel = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit',
            margins: '3 3 3 0',
            bodyStyle: 'background-color: transparent;'
        });
        var node = treePanel.getRootNode();

        var metaFeedsStore = kimios.store.StudioStore.getMetaFeedsStore();
        metaFeedsStore.on('load', function () {
            var data = {
                uid: -1,
                name: kimios.lang('NoMetaFeed')
            };
            var r = new metaFeedsStore.recordType(data);
            metaFeedsStore.insert(0, r);
        });

        var documentTypesStore = kimios.store.StudioStore.getDocumentTypesStore();
        documentTypesStore.on('load', function (st, documentTypesRecord) {
            metaFeedsStore.load();
//            documentTypesStore.insert(0, new documentTypesStore.recordType({
//                uid: -1,
//                name: 'No Document Type'
//            }));
            while (node.hasChildNodes()) {
                node.removeChild(node.item(0));
            }
            Ext.each(documentTypesRecord, function (documentTypeRecord, ind) {
                var documentTypeNode = new Ext.tree.TreeNode({
                    text: documentTypeRecord.data.name,
                    allowChildren: true,
                    iconCls: 'studio-cls-meta-document-type'
                });
                documentTypeNode.on('contextMenu', function (node, e) {
                    node.select();
                    var contextMenu = new Ext.menu.Menu({
                        shadow: false,
                        items: [
                            {
                                text: kimios.lang('Properties'),
                                iconCls: 'qaction-properties',
                                handler: function () {
                                    contextPanel.removeAll();
                                    contextPanel.add(Studio.DocumentTypes.getDocumentTypePanel(metaFeedsStore, contextPanel, documentTypesStore, documentTypeRecord));
                                    contextPanel.doLayout();
                                }
                            },
                            {
                                text: kimios.lang('Delete'),
                                iconCls: 'trash',
                                handler: function () {
                                    kimios.request.StudioRequest.removeDocumentType(documentTypesStore, documentTypeRecord, contextPanel);
                                }
                            }
                        ]
                    });
                    contextMenu.showAt(e.getXY());
                });

                documentTypeNode.on('click', function () {
                    contextPanel.removeAll();
                    contextPanel.add(Studio.DocumentTypes.getDocumentTypePanel(metaFeedsStore, contextPanel, documentTypesStore, documentTypeRecord));
                    contextPanel.doLayout();
                });

                node.appendChild(documentTypeNode);
            });
        });

        var panel = new Ext.Panel({
            title: kimios.lang('DocumentTypes'),
            iconCls: 'studio-cls-meta-document-type',
            layout: 'border',
            contextPanel: contextPanel,
            items: [treePanel, contextPanel],
            border: false
        });

        documentTypesStore.load();
        panel.doLayout();
        return panel;
    },

    getDocumentTypePanel: function (metaFeedsStore, contextPanel, documentTypesStore, documentTypeRecord) {
        var metaDataStore = kimios.store.StudioStore.getUnheritedMetasStore(documentTypeRecord);
        var metaDatasGrid = Studio.DocumentTypes.getMetaDatasPanel(metaDataStore, metaFeedsStore, documentTypeRecord);

        var deleteButton = new Ext.Button({
            text: kimios.lang('Delete'),
            tooltip: kimios.lang('Delete'),
            iconCls: 'trash',
            disabled: (documentTypeRecord ? false : true),
            handler: function () {
                kimios.request.StudioRequest.removeDocumentType(documentTypesStore, documentTypeRecord, contextPanel);
            }
        });

        var saveButton = new Ext.Button({
            text: kimios.lang('Save'),
            tooltip: kimios.lang('Save'),
            iconCls: 'save',
            handler: function () {
                kimios.request.StudioRequest.saveDocumentType(metaFeedsStore, Ext.getCmp('hidden-domain-uid').getValue(), Ext.getCmp('doctypefield').getValue(), Ext.getCmp('heritedFromComboBox').uid, documentTypesStore, contextPanel, metaDatasGrid.getStore(), documentTypeTextField.getValue(), documentTypeRecord);
            }
        });

        var formPanel = new kimios.FormPanel({
            title: (documentTypeRecord ? documentTypeRecord.data.name : kimios.lang('New') + ' ' + kimios.lang('DocumentType')),
//            iconCls: 'studio-cls-meta-document-type',
            bodyStyle: 'padding:5px;background-color:transparent;',
            labelWidth: 120,
            defaults: {
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 10px'
            },
            tools: [
                {
                    id: 'refresh',
                    handler: function (event, toolEl, panel) {
                        if (documentTypeRecord) {
                            contextPanel.removeAll();
                            contextPanel.add(Studio.DocumentTypes.getDocumentTypePanel(metaFeedsStore, contextPanel, documentTypesStore, documentTypeRecord));
                            contextPanel.doLayout();
                        }
                    }
                }
            ],
            buttonAlign: 'left',
            bbar: [ '->', saveButton, deleteButton],
            monitorValid: true
        });

        var hiddenField = new Ext.form.Hidden({
            name: 'uid',
            id: 'hidden-domain-uid',
            value: (documentTypeRecord ? documentTypeRecord.data.uid : -1)
        });

        var documentTypeTextField = new Ext.form.TextField({
            id: 'doctypefield',
            anchor: '100%',
            name: 'name',
            fieldLabel: kimios.lang('DocumentType'),
            value: (documentTypeRecord ? documentTypeRecord.data.name : '')
        });

        var documentTypeName = '';
        var documentTypeUid = -1;
        if (documentTypeRecord != undefined) {
            for (var i = 0; i < documentTypesStore.getCount(); i++) {
                if (documentTypeRecord.data.documentTypeUid == documentTypesStore.getAt(i).data.uid) {
                    documentTypeName = documentTypesStore.getAt(i).data.name;
                    documentTypeUid = documentTypesStore.getAt(i).data.uid;
                    break;
                }
            }
        }
        var heritedFromComboBox = new kimios.form.DocumentTypeField({
            id: 'heritedFromComboBox',
            name: 'heritedfrom',
            fieldLabel: kimios.lang('HeritedFrom'),
            anchor: '100%',
            forceSelection: true,
            editable: false,
            value: documentTypeName,
            uid: documentTypeUid,
            listeners: {
                change: function (thisField, newValue, oldValue) {
                    for (var i = 0; i < thisField.getStore().getCount(); i++) {
                        if (newValue == thisField.getStore().getAt(i).data.uid) {
                            thisField.uid = newValue;
                            break;
                        }
                    }
                }
            }
        });

        formPanel.add(hiddenField);
        formPanel.add(documentTypeTextField);
        formPanel.add(heritedFromComboBox);
        formPanel.add(new Ext.Panel({
            anchor: '100% -52',
            border: false,
            viewConfig: {
                forceFit: true
            },
            layout: 'fit',
            items: [metaDatasGrid]
        }));
        documentTypeTextField.focus(true, true);
        return formPanel;
    },

    getMetaDatasPanel: function (metaDataStore, metaFeedsStore, documentTypeRecord) {

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            iconCls: 'add',
            handler: function () {
                var metadata = grid.getStore().recordType;
                var p = new metadata({
                    name: '',
                    metaType: '',
                    metaFeedUid: ''
                });
                grid.stopEditing();
                metaDataStore.insert(grid.getStore().getCount(), p);
                grid.startEditing(grid.getStore().getCount() - 1, 2);
            }
        });

        var removeButton = new Ext.Button({
            text: kimios.lang('Remove'),
            iconCls: 'remove',
            disabled: true,
            handler: function () {
                var records = sm.getSelections();
                for (var i = 0; i < records.length; i++) {
                    metaDataStore.remove(records[i]);
                }
            }
        });


        var sm = new Ext.grid.CheckboxSelectionModel({
            checkOnly: true,
            listeners: {
                selectionchange: function (sm) {
                    var count = sm.getCount();
                    if (count > 0) {
                        removeButton.enable();
                    } else {
                        removeButton.disable();
                    }
                }
            }
        });

        var metaTypeCombo = new Ext.form.ComboBox({
            store: new Ext.data.ArrayStore({
                id: 0,
                fields: [
                    'value', 'name'
                ],
                data: [
                    [2, kimios.lang('MetaNumberValue')],
                    [3, kimios.lang('MetaDateValue')],
                    [4, kimios.lang('MetaBooleanValue')],
                    [1, kimios.lang('SearchText') + ' / ' + kimios.lang('MetaFeed')]
                ]
            }),
            displayField: 'name',
            valueField: 'value',
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            mode: 'local',
            editable: false
        });

        var metaFeedsCombo = new Ext.form.ComboBox({
            name: 'metaFeedUid',
            displayField: 'name',
            valueField: 'uid',
            typeAhead: true,
            triggerAction: 'all',
            lazyRender: true,
            store: metaFeedsStore,
            editable: false
        });

        var cm = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true
            },
            columns: [
                sm,
                {
                    width: 16,
                    fixed: true,
                    hidden: true,
                    editable: false,
                    sortable: false,
                    menuDisabled: true,
                    dataIndex: 'icon'
                }, {
                    id: 'name',
                    header: kimios.lang('MetaData'),
                    sortable: true,
                    dataIndex: 'name',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        if (!value) {
                            return '<span style="font-style: italic; color: red;">' + kimios.lang('MetaDataName') + '?</span>';
                        }
                        return value;
                    },
                    editor: new Ext.form.TextField({
                        blankText: 'Meta Data name is required'
                    })
                }, {
                    header: kimios.lang('Type'),
                    dataIndex: 'metaType',
                    editor: metaTypeCombo,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        var metaTypeStore = metaTypeCombo.getStore();
                        for (var i = 0; i < metaTypeStore.getCount(); ++i) {
                            var typeRecord = metaTypeStore.getAt(i);
                            if (typeRecord.data.value == value) {
                                if (value == 1) {
                                    if (record.get('metaFeedUid') == -1) {
                                        return kimios.lang('SearchText');
                                    } else {
                                        return kimios.lang('MetaFeed');
                                    }
                                }
                                grid.doLayout();
                                return typeRecord.data.name;
                            }
                        }
                        return '<span style="font-style: italic; color: red;">' + kimios.lang('Type') + '?</span>';
                    }
                }, {
                    header: kimios.lang('MetaFeed'),
                    dataIndex: 'metaFeedUid',
                    disabled: true,
                    editor: metaFeedsCombo,
                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                        var metaFeedsStore = metaFeedsCombo.getStore();
                        for (var i = 0; i < metaFeedsStore.getCount(); ++i) {
                            var metaFeedRecord = metaFeedsStore.getAt(i);
                            if (metaFeedRecord.data.uid == value) {
                                if (record.get('metaType') == 1)
                                    return metaFeedRecord.data.name;
                            }
                        }
                        if (record.get('metaType') != 1)
                            return '<span style="color: #aaa;">N/A</span>';
                        else
                            return '<span style="font-style: italic; color: red;">' + kimios.lang('MetaFeed') + '?</span>';
                    }
                }]
        });

        var grid = new Ext.grid.EditorGridPanel({
            store: metaDataStore,
            autoScroll: true,
            stripeRows: true,
            autoExpandColumn: 'name',
            cm: cm,
            sm: sm,
            clicksToEdit: 1,
            viewConfig: {
                forceFit: true,
                scrollOffset: 0
            },
            tbar: [addButton, '-', removeButton],
            listeners: {
                beforeedit: function (e) {
                    if (e.column != 4) {
                        return true;
                    }
                    if (e.record.get('metaType') == 1) {
                        return true;
                    } else {
                        return false;
                    }
                },
                afteredit: function (e) {
                    if (e.field == 'metaType') {
                        if (e.record.get('metaType') == 1) {
                            e.record.set('metaFeedUid', -1);
                        }
                    }
                }
            }
        });

        return grid;
    }

};
