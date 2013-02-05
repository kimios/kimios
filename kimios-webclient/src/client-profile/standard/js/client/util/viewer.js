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
DMEntityViewer = Ext.extend(Ext.DataView, {
    constructor:function(config){
        //config.autoScroll = true;
        DMEntityViewer.superclass.constructor.call(this, config);
    },
    initComponent: function(){
        DMEntityViewer.superclass.initComponent.apply(this, arguments);
        this.addEvents('dmEntitySelected');
        this.addEvents('dmEntityDeselected');
        this.on('dmEntitySelected', selectDmEntityHandler);
        this.on('dmEntityDeselected', deselectDmEntityHandler);
    },
    ppDataLarge: function(data){
        data.shortName = Ext.util.Format.ellipsis(data.name, 15);
        data.ownerString = Ext.util.Format.ellipsis(data.owner + '@'  + data.ownerSource, 20);
        return data;
    },
    ppDataList: function(data){
        data.shortName = Ext.util.Format.ellipsis(data.name, 16);
        return data;
    },
    init: function(store){
        this.store = store;
        this.prepareData =this.ppDataLarge;
        this.on('contextmenu', function(dv, ind, node, ev){
            var selRec =  dv.getRecord(node);
            qMenuContext.init();
            qMenuContext.setDmEntityUid(selRec.data.uid);
            qMenuContext.setDmEntityType(selRec.data.type);
            qMenuContext.showAt(ev.getXY());
            ev.preventDefault();
        }, this);
      
      
        this.on('dblclick', function(dv, ind, node,e){
            var selRec =  dv.getRecord(node);
            enterDmEntity({
                dmEntityType: selRec.data.type,
                dmEntityUid: selRec.data.uid
            });
        }, this);
      
      
        this.on('containerclick', function(dv, event){
            Ext.each(dv.getSelectedNodes(), function(selNode, ind){
                this.deselect(selNode);
            }, this);
            this.fireEvent('dmEntityDeselected');
        },this);
      
        this.on('selectionchange', function(dv, nodes){
            if(this.getSelectedRecords().length == 1){
                var selRec = this.getSelectedRecords()[0];
                this.fireEvent('dmEntitySelected', selRec);
            }
            else
                this.fireEvent('dmEntityDeselected');
                
        }, this);
    },
  
    largeView: function(){
      
        this.prepareData =this.ppDataLarge;
      
        var largeEntityTemplate = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="thumb-wrap" id="{type}-{uid}">',
            '<div style="float: left" class="thumb">',
            '<tpl if="type == 1">',
            '<img src="' + srcContextPath + '/images/icons/32x32/database.png" style="margin-top: 7px;width:32px; height: 32px;  padding-bottom: 6px;" border="0"  />',
            '</tpl>',
            '<tpl if="type == 2">',
            '<img src="' + srcContextPath + '/images/icons/32x32/folder.png" style="margin-top: 7px;width:32px; height: "32px;" border="0" />',
            '</tpl>',
            '<tpl if="type == 3">',
            '<img src="{[getDocumentIcon(values.extension, 32)]}" style="margin-top: 7px;width:32px; height: 32px;" border="0" />',
            '</tpl>',
            '</div>',
            '<div style="float:left" class="thumb">',
            '<b>{shortName}</b><br />',
            '<span style="font-size: 10px; text-align:left">{ownerString}</span>',
            '{creationDate}</div>',
            '</div>',
            '<div style="clear: both;"></div>',
            '</tpl>'
            );
         
     
        //     largeEntityTemplate.compile();
        this.tpl = largeEntityTemplate;
        this.store.reload();
    },
    listView: function(){
        this.prepareData =this.ppDataList;
        var shortEntityTemplate = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="thumb-wrap" id="{type}-{uid}">',
            '<div class="thumb">',
            '<tpl if="type == 1">',
            '<img src="' + srcContextPath + '/images/icons/16x16/database.png" border="0" align="absmiddle" style="width:16px; height: 16px;" title="{name} - {creationDate}" />',
            '</tpl>',
            '<tpl if="type == 2">',
            '<img src="' + srcContextPath + '/images/ftv2folderclosed.gif" border="0" align="absmiddle" style="width:16px; height: 16px;" title="{name} - {creationDate}" />',
            '</tpl>',
            '<tpl if="type == 3">',
            '<img src="{[getDocumentIcon(values.extension,16)]}" align="absmiddle" style="width:16px; height: 16px;" border="0" />',
            '</tpl>',
            '{shortName}</div>',
            '</div>',
            '</tpl>'
            );
    
        this.tpl = shortEntityTemplate;
        this.store.reload();
    }
}); 
var actionViewerList = new Ext.Action({
    text: 'List',
    handler: function(){
        if(dmEntityGridPanel.isVisible()){
            dmEntityGridPanel.hide();
            dmEntityViewer.show();
        }
        dmEntityViewer.listView();
    },
    iconCls: 'blist'
}); 

var  actionViewerDetail = new Ext.Action({
    text: 'Details',
    handler: function(){
        if(dmEntityViewer.isVisible()){
            dmEntityViewer.hide();
        }
        dmEntityGridPanel.show();
    },
    iconCls: 'blist'
}); 

var actionViewerLarge = new Ext.Action({
    text: 'Large',
    handler: function(){
        if(dmEntityGridPanel.isVisible()){
            dmEntityGridPanel.hide();
            dmEntityViewer.show();
        }
        dmEntityViewer.largeView();
    },
    iconCls: 'blist'
});
    

   



