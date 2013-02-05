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
kimios.menu.BaseMenu = Ext.extend(Ext.Button,{
  constructor : function(config) {
    this.trueIcon = this.iconCls;
    this.menu = new Ext.menu.Menu({
      showSeparator : false,
      enableScrolling : true
    });
    kimios.menu.BaseMenu.superclass.constructor.call(this, config);
  },
  
  checkActionItem : function(){
    if (this.actionItem != undefined)
      this.actionItem.setText(this.actionText);
    var disabled = false;
    var currentUid = kimios.explorer.getActivePanel().uid;
    if (currentUid == undefined){
      disabled = true;
    }else{
      for (var i=0; i<this.menu.items.length; i++){
        var pojo = this.menu.items.get(i).pojo;
        if (pojo == undefined) continue;
        if (this.menu.items.get(i).pojo.uid == currentUid){
          disabled = true;
          break;
        }
      }
    }
    this.actionItem.setDisabled(disabled);
  },
  
  initComponent : function(arguments){
    kimios.menu.BaseMenu.superclass.initComponent.apply(this, arguments);
    
    this.setIconClass('loading');
    this.store.load();
    
    this.menu.on('beforeshow', function(){
      if (this.actionHandler != undefined)
        this.checkActionItem();
    }, this);
    
    this.store.on('load', function(store, records, options){
      this.menu.removeAll();
      
      if (this.actionHandler != undefined){
        this.actionItem = new Ext.menu.Item({
          text : this.actionText,
          iconCls : this.actionIconCls,
          hideOnClick : false,
          scope : this,
          disabled : true,
          handler : this.actionHandler
        });
        this.menu.add(this.actionItem);
        this.menu.addSeparator();
      }
      
      if (records.length == 0){
        this.menu.add(new Ext.menu.Item({
          text : '<span style="color:gray;">'+this.noContentText+'</span>',
          hideOnClick : false
        }));
      }else{
        for (var i=0; i<records.length; i++){
          var obj = this;
          var item = new Ext.menu.Item({
            text : records[i].data.name,
            pojo : new kimios.DMEntityPojo(records[i].data),
            iconCls : kimios.util.IconHelper.getIconClass(records[i].data.type, records[i].data.extension),
            handler : function(){
              if (this.pojo.type != 3) {
                var centerPanel = kimios.explorer.getMainPanel();
                var tabsCounter = centerPanel.items.length;
                var currentTab = null;
                
                if (tabsCounter > 0 && centerPanel.getActiveTab() instanceof kimios.explorer.DMEntityGridPanel){
                  currentTab = centerPanel.getActiveTab();
                  
                } else {
                  currentTab = new kimios.explorer.DMEntityGridPanel({});
                  centerPanel.add(currentTab);
                  centerPanel.setActiveTab(currentTab);
                }
                
                currentTab.loadEntity({
                  uid : this.pojo.uid,
                  type : this.pojo.type
                });
              } else {
                window.location.href = kimios.util.getDocumentLink(this.pojo.uid);
              }
            },
            listeners : {
              render: function(item){
                item.el.on('contextmenu', function(e){
                  e.stopEvent();
                  kimios.ContextMenu.show(item.pojo, e, obj.context);
                });
              }
            }
          });
          this.menu.add(item);
        }
      }
      if (this.actionHandler != undefined){
        this.checkActionItem();
      }
      this.setIconClass(this.trueIcon);
    }, this);
  }

});
