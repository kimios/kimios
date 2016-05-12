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
Reporting = {

    getWindow: function(){
        var window = new Ext.Window({
          modal : true,
            title: kimios.lang('Reporting'),
            iconCls: 'reporting',
            closable: true,
            maximizable: true,
            width:800,
            height:500,
            plain:true,
            layout: 'fit'
        });

        var tabs = new Ext.Panel({
            border: false,
            layout: 'border'
        });

        var treePanel = new Ext.tree.TreePanel({
            region: 'west',
            width: 200,
            split: true,
            title: kimios.lang('Reporting'),
            hideCollapseTool : true,
            collapsible: true,
            tools: [{
                id: 'refresh',
                handler: function(){
                    reportsListStore.reload();
                }
            }],
            margins:'3 0 3 3',
            cmargins:'3 3 3 3',
            rootVisible: false,
            autoSize: true,
            autoScroll: true,
            root: new Ext.tree.TreeNode()
        });

        var contextPanel = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit',
            margins: '3 3 3 0',
            bodyStyle: 'background-color: transparent;'
        });

        var reportsListStore = kimios.store.ReportingStore.getReportsList();
        reportsListStore.on('load', function(st, recs){
            while (treePanel.getRootNode().hasChildNodes()){
                treePanel.getRootNode().removeChild(treePanel.getRootNode().item(0));
            }
            Ext.each(recs, function(rec, ind){
                treePanel.getRootNode().appendChild(new Ext.tree.TreeNode({
                    text: rec.data.name,
                    iconCls: 'reporting',
                    listeners:{
                        click: function(){
                            var p = Reporting.Generic.getPanel(rec);
                            contextPanel.removeAll();
                            contextPanel.add(p);
                            contextPanel.doLayout();
                        }
                    }
                }));
            });
        });

        tabs.add(treePanel);
        tabs.add(contextPanel);
        window.add(tabs);
        return window;
    }
};
