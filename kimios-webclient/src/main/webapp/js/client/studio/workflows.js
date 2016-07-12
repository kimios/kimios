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
Studio.Workflows = {
    getPanel: function(){
        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            hideCollapseTool : true,
            title: kimios.lang('Workflow'),
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
            rootVisible: false,
            collapsible: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode(),
            tools:[{
                id:'refresh',
                handler: function(event, toolEl, panel){
                    workflowsStore.reload();
                }
            },{
                id:'plus',
                handler: function(event, toolEl, panel){
                    var p = Studio.Workflows.getWorkflowPanel(contextPanel, workflowsStore);
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

        var workflowsStore = kimios.store.StudioStore.getWorkflowsStore();

        workflowsStore.on('load', function(st, workflowsRecord){
            while (node.hasChildNodes()){
                node.removeChild(node.item(0));
            }
            Ext.each(workflowsRecord, function(workflowRecord, ind){
                var workflowNode = new Ext.tree.TreeNode({
                    text: workflowRecord.data.name,
                    allowChildren: true,
                    iconCls: 'studio-cls-wf'
                });
                workflowNode.on('contextMenu', function(node, e){
                    node.select();
                    var contextMenu = new Ext.menu.Menu({
                        shadow: false,
                        items: [{
                            text: kimios.lang('Properties'),
                            iconCls: 'qaction-properties',
                            handler: function(){
                                var p = Studio.Workflows.getWorkflowPanel(contextPanel, workflowsStore, workflowRecord);
                                contextPanel.removeAll();
                                contextPanel.add(p);
                                contextPanel.doLayout();
                            }
                        },{
                            text: kimios.lang('Delete'),
                            iconCls: 'trash',
                            handler: function(){
                                kimios.request.StudioRequest.removeWorkflow(workflowsStore, workflowRecord, contextPanel);
                            }
                        }]
                    });
                    contextMenu.showAt(e.getXY());
                });

                workflowNode.on('click', function(){
                    var p =  Studio.Workflows.getWorkflowPanel(contextPanel, workflowsStore, workflowRecord);
                    contextPanel.removeAll();
                    contextPanel.add(p);
                    contextPanel.doLayout();
                });

                node.appendChild(workflowNode);
            });
        });

        var panel = new Ext.Panel({
            title: kimios.lang('Workflow'),
            iconCls: 'studio-cls-wf',
            layout: 'border',
            contextPanel : contextPanel,
            items: [treePanel, contextPanel],
            border: false
        });
        
        workflowsStore.load();
        panel.doLayout();
        return panel;
    },

    getWorkflowPanel: function(contextPanel, workflowsStore, workflowRecord){

        var statusGrid = Studio.Workflows.getStatusPanel(workflowRecord);

        var deleteButton = new Ext.Button({
            text: kimios.lang('Delete'),
            tooltip: kimios.lang('Delete'),
            iconCls: 'trash',
            disabled: (workflowRecord ? false : true),
            handler: function(){
                kimios.request.StudioRequest.removeWorkflow(workflowsStore, workflowRecord, contextPanel);
            }
        });

        var saveButton = new Ext.Button({
            text: kimios.lang('Save'),
            tooltip: kimios.lang('Save'),
            iconCls: 'save',
            handler: function(){
                kimios.request.StudioRequest.saveWorkflow(Ext.getCmp('wf-name').getValue(), 
                    Ext.getCmp('hidden-workflow-uid').getValue(),
                    Ext.getCmp('wf-desc').getValue(),
                    Ext.getCmp('wf-autorestart').getValue(),
                    workflowsStore, 
                    contextPanel, 
                    statusGrid.getStore(), 
                    workflowNameTextField.getValue(), 
                    workflowRecord);
            }
        });

        var formPanel = new kimios.FormPanel({
            title: (workflowRecord ? workflowRecord.data.name : kimios.lang('NewWorkflow')),
//            iconCls: 'studio-cls-wf',
            labelWidth: 220,
            bodyStyle:'padding:5px;background-color:transparent;',
            defaults: {
                selectOnFocus: true,
                style: 'font-size: 10px',
                labelStyle: 'font-size: 10px'
            },
            tools:[{
                id:'refresh',
                handler: function(event, toolEl, panel){
                    if (workflowRecord){
                        var p = Studio.Workflows.getWorkflowPanel(contextPanel, workflowsStore, workflowRecord);
                        contextPanel.removeAll();
                        contextPanel.add(p);
                        contextPanel.doLayout();
                    }
                }
            }],
            bbar: ['->', saveButton, deleteButton],
            monitorValid: true
        });

        var hiddenField = new Ext.form.Hidden({
            name: 'uid',
            id: 'hidden-workflow-uid',
            value: (workflowRecord ? workflowRecord.data.uid : -1)
        });

        var workflowNameTextField = new Ext.form.TextField({
            anchor: '100%',
            name: 'name',
            id : 'wf-name',
            fieldLabel: kimios.lang('Workflow'),
            value: (workflowRecord ? workflowRecord.data.name : '')
        });

        var descriptionTextField = new Ext.form.TextField({
            anchor: '100%',
            id : 'wf-desc',
            name: 'description',
            fieldLabel: kimios.lang('Description'),
            value: (workflowRecord ? workflowRecord.data.description : ''),
            enableKeyEvents: true
        });

        var automaticRestartField = new Ext.form.Checkbox({
            anchor: '100%',
            id : 'wf-autorestart',
            name: 'automaticRestart',
            inputValue: "true",
            fieldLabel: kimios.lang('AutomaticRestart'),
            checked: workflowRecord ? workflowRecord.data.automaticStatusRestart : false,
            value: (workflowRecord ? workflowRecord.data.automaticStatusRestart : "false"),
            enableKeyEvents: true
        });

        formPanel.add(hiddenField);
        formPanel.add(workflowNameTextField);
        formPanel.add(descriptionTextField);
        formPanel.add(automaticRestartField);
        formPanel.add(new Ext.Panel({
            anchor: '100% -78',
            layout: 'fit',
            border: false,
            items: [statusGrid]
        }));

        workflowNameTextField.focus(true, true);

        return formPanel;
    },

    getStatusPanel: function(workflowRecord){
        var expandMode = false;
        var statusStore = kimios.store.StudioStore.getAllStatus(workflowRecord);
        Ext.Ajax.request({
            url: getBackEndUrl('Studio'),
            params: {
                uid: (workflowRecord ? workflowRecord.data.uid : -1),
                action: 'statusList'
            },
            success: function(resp){
                var data = eval('(' + resp.responseText + ')');
                statusStore.on('load', function(store){
                    store.each(function(rec, ind){
                        var managerStore = kimios.store.StudioStore.getAllStatusManager(data[ind].workflowStatusManagers);
                        rec.data.managerStore = managerStore;
                        if (expandMode)
                            expander.expandRow(ind);
                    });
                });
                if (workflowRecord)
                    statusStore.loadData(data);
            }
        });

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            iconCls: 'add',
            handler : function(){
                var status = statusGrid.getStore().recordType;
                var p = new status({
                    name: ''
                });
                p.data.managerStore = kimios.store.StudioStore.getAllStatusManager();
                statusGrid.stopEditing();
                statusStore.insert(statusGrid.getStore().getCount(), p);
                if (expandMode)
                    expander.expandRow(statusGrid.getStore().getCount()-1);
                statusGrid.startEditing(statusGrid.getStore().getCount()-1, 3);
                
            }
        });

        var removeButton = new Ext.Button({
            text:kimios.lang('Remove'),
            iconCls:'remove',
            disabled: true,
            handler: function(){
                var records = sm.getSelections();
                for (var i=0; i<records.length; i++){
                    statusStore.remove(records[i]);
                }
            }
        });

        var expandButton = new Ext.Button({
          text : kimios.lang('Expand'),
            iconCls: 'studio-wf-expand',
            enableToggle: true,
            listeners: {
                toggle: function(button, pressed){
                    button.setIconClass(pressed ? 'studio-wf-expand' : 'studio-wf-expand');
                }
            },
            handler: function(){
                if (expandMode){
                    statusStore.each(function(r, i){
                        expander.collapseRow(i);
                    });
                }else{
                    statusStore.each(function(r, i){
                        expander.expandRow(i);
                    });
                }
                expandMode = !expandMode;
            }
        });

        var upButton = new Ext.Button({
            iconCls:'studio-cls-wf-up',
            disabled: true,
            handler: function(){
                var selectedStatus = sm.getSelected();
                var index = statusStore.indexOf(selectedStatus);
                if (index != 0){
                    statusStore.removeAt(index);
                    index = index - 1;
                    statusStore.insert(index, selectedStatus);
                    if (expandMode)
                        expander.expandRow(index);
                    sm.selectRow(index);
                }
            }
        });

        var downButton = new Ext.Button({
            iconCls:'studio-cls-wf-down',
            disabled: true,
            handler: function(){
                var selectedStatus = sm.getSelected();
                var index = statusStore.indexOf(selectedStatus);
                if (index != statusStore.getCount()-1){
                    statusStore.removeAt(index);
                    index = index + 1;
                    statusStore.insert(index, selectedStatus);
                    if (expandMode)
                        expander.expandRow(index);
                    sm.selectRow(index);
                }
            }
        });

        var sm = new Ext.grid.CheckboxSelectionModel({
            checkOnly: true,
            listeners: {
                selectionchange: function(sm) {
                    var count = sm.getCount();
                    if (count > 0) {
                        if (count == 1){
                            var index = statusStore.indexOf(sm.getSelected());
                            if (index != 0){
                                upButton.enable();
                            }
                            if (index != statusStore.getCount()-1){
                                downButton.enable();
                            }
                        }else{
                            upButton.disable();
                            downButton.disable();
                        }
                        removeButton.enable();
                    } else {
                        upButton.disable();
                        downButton.disable();
                        removeButton.disable();
                    }
                }
            }
        });

        var expander = new Ext.grid.RowExpander( {
            tpl: new Ext.XTemplate('<div class="detailData">', '', '</div>'),
            listeners : {
                expand : function(ex, record, body, rowIndex) {
                    Studio.Workflows.getManagersPanel(record, record.data.managerStore, body);
                }
            }
        });

        var cm = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true
            },
            columns: [
            expander,
            sm,
            {
                width: 30,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                align : 'center',
                dataIndex: 'icon',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'studio-wf-status';
                }
            },{
                id: 'name',
                sortable: false,
                menuDisabled: true,
                dataIndex: 'name',
                editor: new Ext.form.TextField(),
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    if (!value)
                        return '<span style="font-style: italic; color: red;">'+kimios.lang('Workflow')+'?</span>';
                    return value;
                }
            }]
        });

        var statusGrid = new Ext.grid.EditorGridPanel({
            store: statusStore,
            autoScroll: true,
            autoExpandColumn: 'name',
            cm: cm,
            sm: sm,
            plugins: expander,
            clicksToEdit: 1,
            viewConfig: {
                forceFit:true,
                scrollOffset : 0
            },
            hideHeaders: true,
            columnLines: false,
            tbar:[addButton, '-', removeButton, '-', upButton, ' ', downButton, '->',  expandButton]
        });

        statusGrid.on('afteredit', function(e){
            if (expandMode)
                expander.expandRow(e.row);
        });

        return statusGrid;
    },

    getManagersPanel: function(statusRecord, managersStore, body){

        var addButton = new Ext.Button({
            text: kimios.lang('Add'),
            handler: function(){
                var el = new kimios.picker.SecurityEntityPicker({
                    title: addButton.getText(),
                    iconCls: 'roles'
                });
                el.on('entitySelected', function(usersRecords, groupsRecords, _pickerWindow){
                    var managers = managersGrid.getStore().recordType;
                    if (usersRecords != null){
                        for (var usersCount=0; usersCount<usersRecords.length; usersCount++){
                            managersStore.insert(managersGrid.getStore().getCount(), new managers({
                                securityEntityType: 1, // user type
                                securityEntityName: usersRecords[usersCount].data.uid,
                                securityEntitySource: usersRecords[usersCount].data.source
                            }));
                        }
                    }
                    if (groupsRecords != null){
                        for (var groupsCount=0; groupsCount<groupsRecords.length; groupsCount++){
                            managersStore.insert(managersGrid.getStore().getCount(), new managers({
                                securityEntityType: 2, // group type
                                securityEntityName: groupsRecords[groupsCount].data.gid,
                                securityEntitySource: groupsRecords[groupsCount].data.source
                            }));
                        }
                    }
                    statusRecord.data.managersStore = managersGrid.getStore();

                    _pickerWindow.close();
                });
                el.show();
            }
        });

        var sm = new Ext.grid.RowSelectionModel({
            checkOnly: true,
            singleSelect: true
        });

        var cm = new Ext.grid.ColumnModel({
            defaults: {
                sortable: true
            },
            columns: [
            {
                width: 20,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    metaData.css = 'del-icon';
                }
            },{
                width: 16,
                fixed: true,
                editable: false,
                sortable: false,
                menuDisabled: true,
                dataIndex: 'securityEntityType',
                id: 'securityEntityType',
                renderer: function(value, metaData, record, rowIndex, colIndex, store) {
                    if (value == 1){
                        metaData.css = 'admin-user-tree-node';
                    }else{
                        metaData.css = 'admin-group-tree-node';
                    }
                }
            },{
                id: 'securityEntityName',
                sortable: true,
                menuDisabled: true,
                dataIndex: 'securityEntityName',
                renderer: function(value, metaData, record, rowIndex, colIndex, store){
                    return '<span style="font-size: 10px">'+value+'@'+record.get('securityEntitySource')+'</span>';
                }
            }]
        });

        var managersGrid = new Ext.grid.GridPanel({
            id: 'studio-workflows-status-managers-panel',
            autoHeight: true,
            hideHeaders: true,
            store: managersStore,
            border: false,
            viewConfig: {
                forceFit:true,
                scrollOffset: 0
            },
            buttonAlign: 'left',
            fbar: [addButton],
            cm: cm,
            sm: sm
        });

        managersGrid.on('cellclick', function(grid, rowIndex, columnIndex, e){
            if (columnIndex == 0){
                managersStore.remove(managersStore.getAt(rowIndex));
            }else{
                return false;
            }
        });
        new Ext.Panel({
            renderTo: Ext.DomQuery.select('div.detailData', body)[0],
            layout: 'fit',
            border: false,
            bodyStyle: 'margin: 0 0 0 18px',
            items: [managersGrid]
        });
        return managersGrid;
    }
};
