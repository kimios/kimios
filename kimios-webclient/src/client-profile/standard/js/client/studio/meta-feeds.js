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
Studio.MetaFeeds = {
    getPanel: function(){
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            hideCollapseTool : true,
            title: kimios.lang('MetaFeeds'),
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
            rootVisible: false,
            collapsible: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode(),
            tools:[{
                id:'refresh',
                handler: function(event, toolEl, panel){
                    metaFeedsStore.reload();
                }
            },{
                id:'plus',
                handler: function(event, toolEl, panel){
                    var p = Studio.MetaFeeds.getMetaFeedPanel(contextPanel, metaFeedsStore);
                    contextPanel.removeAll();
                    contextPanel.add(p);
                    contextPanel.doLayout();
                }
            }]
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
        
        metaFeedsStore.on('load', function(st, metaFeedsRecord){
            while (node.hasChildNodes()){
                node.removeChild(node.item(0));
            }
            Ext.each(metaFeedsRecord, function(metaFeedRecord, ind){
                var metaFeedNode = new Ext.tree.TreeNode({
                    text: metaFeedRecord.data.name,
                    allowChildren: true,
                    iconCls: 'studio-cls-meta-feed'
                });
                metaFeedNode.on('contextMenu', function(node, e){
                    node.select();
                    var contextMenu = new Ext.menu.Menu({
                        shadow: false,
                        items: [{
                            text: kimios.lang('MetaFeedValue'),
                            iconCls: 'value',
                            handler: function(){
                                Studio.MetaFeeds.getValuesWindow(contextPanel, metaFeedRecord).show();
                            }
                        },{
                            text: kimios.lang('Properties'),
                            iconCls: 'qaction-properties',
                            handler: function(){
                                var p = Studio.MetaFeeds.getMetaFeedPanel(contextPanel, metaFeedsStore, metaFeedRecord);
                                contextPanel.removeAll();
                                contextPanel.add(p);
                                contextPanel.doLayout();
                            }
                        },{
                            text: kimios.lang('Delete'),
                            iconCls: 'trash',
                            handler: function(){
                                kimios.request.StudioRequest.removeMetaFeed(metaFeedsStore, metaFeedRecord, contextPanel);
                            }
                        }]
                    });
                    contextMenu.showAt(e.getXY());
                });
                
                metaFeedNode.on('click', function(){
                    var p =  Studio.MetaFeeds.getMetaFeedPanel(contextPanel, metaFeedsStore, metaFeedRecord);
                    contextPanel.removeAll();
                    contextPanel.add(p);
                    contextPanel.doLayout();
                });
                
                node.appendChild(metaFeedNode);
            });
        });

        var panel = new Ext.Panel({
            title: kimios.lang('MetaFeeds'),
            iconCls: 'studio-cls-meta-feed',
            layout: 'border',
            contextPanel : contextPanel,
            items: [treePanel, contextPanel],
            border: false
        });
        
        metaFeedsStore.load();
        panel.doLayout();
        return panel;
    },

    getMetaFeedPanel: function(contextPanel, metaFeedsStore, metaFeedRecord){

        var valuesButton = new Ext.Button({
            text: kimios.lang('MetaFeedValue'),
            tooltip: kimios.lang('MetaFeedValue'),
            iconCls: 'value',
            hidden: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? false : true),
            handler: function(){
                Studio.MetaFeeds.getValuesWindow(contextPanel, metaFeedRecord).show();
            }
        });

        var deleteButton = new Ext.Button({
            text: kimios.lang('Delete'),
            tooltip: kimios.lang('Delete'),
            iconCls: 'trash',
            disabled: (metaFeedRecord ? false : true),
            handler: function(){
                kimios.request.StudioRequest.removeMetaFeed(metaFeedsStore, metaFeedRecord, contextPanel);
            }
        });

        var saveButton = new Ext.Button({
            text: kimios.lang('Save'),
            tooltip: kimios.lang('Save'),
            iconCls: 'save',
            handler: function(){
                kimios.request.StudioRequest.saveMetaFeed(Ext.getCmp('mf-classname').getValue(), Ext.getCmp('mf-name').getValue(), Ext.getCmp('hidden-mf-uid').getValue(), metaFeedsStore, contextPanel, null, metaFeedTextField.getValue(), metaFeedRecord);
            }
        });

        var formPanel = new kimios.FormPanel({
            title: (metaFeedRecord ? metaFeedRecord.data.name : kimios.lang('New')+' '+kimios.lang('MetaFeed')),
            iconCls: 'studio-cls-meta-feed',
            bodyStyle:'padding:5px;background-color:transparent;',
            labelWidth: 120,
            defaults: {
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 10px'
            },
            tools:[{
                id:'refresh',
                handler: function(event, toolEl, panel){
                    if (metaFeedRecord){
                        var p = Studio.MetaFeeds.getMetaFeedPanel(contextPanel, metaFeedsStore, metaFeedRecord);
                        contextPanel.removeAll();
                        contextPanel.add(p);
                        contextPanel.doLayout();
                    }
                }
            }],
            bbar: [valuesButton, '->', saveButton, deleteButton],
            monitorValid: true
        });

        var hiddenField = new Ext.form.Hidden({
            name: 'uid',
            id: 'hidden-mf-uid',
            value: (metaFeedRecord ? metaFeedRecord.data.uid : -1)
        });

        var metaFeedTextField = new Ext.form.TextField({
            anchor: '100%',
            name: 'name',
            id : 'mf-name',
            fieldLabel: kimios.lang('MetaFeed'),
            value: (metaFeedRecord ? metaFeedRecord.data.name : '')
        });

        var availableMetaFeedsStore =  kimios.store.StudioStore.getAvailableMetaFeedsStore();

        var metaFeedComboBox = new Ext.form.ComboBox({
          id : 'mf-classname',
            anchor: '100%',
            name: 'className',
            fieldLabel: kimios.lang('JavaClassName'),
            displayField: 'className',
            valueField: 'className',
            hiddenName: 'className',
            value: (metaFeedRecord ? metaFeedRecord.data.className : ''),
            editable: false,
            disabled: (metaFeedRecord ? true : false),
            triggerAction: 'all',
            store: availableMetaFeedsStore
        });

        formPanel.add(hiddenField);
        formPanel.add(metaFeedTextField);
        formPanel.add(metaFeedComboBox);
        metaFeedTextField.focus(true, true);
        return formPanel;
    },

    getValuesWindow: function(contextPanel, metaFeedRecord){
        var window = new Ext.Window({
            title: metaFeedRecord.data.name,
            iconCls: 'studio-cls-meta-feed',
            layout: 'fit',
            width: 250,
            border:false,
            height: 300,
            maximizable: true,
            modal: true,
            tools:[{
                id: 'refresh',
                handler: function(){
                    metaFeedStore.reload();
                }
            }]
        });

        var metaFeedStore = kimios.store.StudioStore.getMetaFeedValuesStore(metaFeedRecord.data.uid, true);

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            iconCls: 'add',
            disabled: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? false : true),
            handler : function(){
                var metadata = grid.getStore().recordType;
                var p = new metadata({
                    name: '',
                    metaType: '',
                    metaFeedUid: ''
                });
                grid.stopEditing();
                metaFeedStore.insert(grid.getStore().getCount(), p);
                grid.startEditing(grid.getStore().getCount()-1, 2);
            }
        });

        var removeButton = new Ext.Button({
            text:kimios.lang('Remove'),
            iconCls:'remove',
            disabled: true,
            handler: function(){
                var records = sm.getSelections();
                for (var i=0; i<records.length; i++){
                    metaFeedStore.remove(records[i]);
                }
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            hidden: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? false : true),
            checkOnly: true,
            listeners: {
                selectionchange: function(sm) {
                    if (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration'){
                        var count = sm.getCount();
                        if (count > 0) {
                            removeButton.enable();
                        } else {
                            removeButton.disable();
                        }
                    }
                }
            }
        });

        var cm = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true
            },
            columns: [
            sm,
            {
                width: 30,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'icon',
                align : 'center',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'value';
                }
            },{
                id: 'value',
                sortable: true,
                editable: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? true : false),
                dataIndex: 'value',
                editor: new Ext.form.TextField(),
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    if (!value)
                        return '<span style="font-style: italic; color: red;">?</span>';
                    return value;
                }
            }]
        });

        var grid = new Ext.grid.EditorGridPanel({
            store: metaFeedStore,
            autoScroll: true,
            stripeRows: true,
            //            border: false,
            cm: cm,
            sm: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? sm : null),
            clicksToEdit: 1,
            viewConfig: {
                forceFit:true,
                scrollOffset:0
            },
            hideHeaders: true,
            columnLines: false,
            tbar: [addButton, '-', removeButton],
            bbar: ['->',new Ext.Button({
                text: kimios.lang('Save'),
                iconCls: 'save',
                disabled: (metaFeedRecord && metaFeedRecord.data.className == 'org.kimios.kernel.dms.metafeeds.impl.Enumeration' ? false : true),
                handler: function(){
                    kimios.request.StudioRequest.updateEnumerationValues(contextPanel, metaFeedRecord.data.uid, grid.getStore(), window);
                }
            })]
        });

        window.add(grid);

        return window;
    }

};
