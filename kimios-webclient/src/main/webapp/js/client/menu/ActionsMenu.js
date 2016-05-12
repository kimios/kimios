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
kimios.menu.ActionsMenu = Ext.extend(Ext.Button,{
  constructor : function(config) {
    this.text = kimios.lang('Actions');
    this.iconCls = 'owner';

    this.menu = new Ext.menu.Menu({
      showSeparator : false,
      enableScrolling : true
    });
    
    kimios.menu.ActionsMenu.superclass.constructor.call(this, config);
  },
  
  initComponent : function(arguments){
    kimios.menu.ActionsMenu.superclass.initComponent.apply(this, arguments);
    
    this.menu.on('beforeshow', function(){
      var p = kimios.explorer.getActivePanel();
      this.newWorkspaceItem.setVisible(kimios.explorer.getViewport().rights.isWorkspaceCreator);
      this.newFolderItem.setDisabled(p.type != 1 && p.type != 2);
      this.importDocumentItem.setDisabled(p.type != 2);
      this.propertiesItem.setDisabled(p.type != 1 && p.type != 2);
    }, this);
    
    this.build();
  },
  
  build : function(){
    this.newWorkspaceItem = new Ext.menu.Item({
      text: kimios.lang('NewWorkspace'),
      iconCls : 'newworkspace',
      hidden : true,
      scope : this,
      handler: function(){
        new kimios.properties.PropertiesWindow({
          createMode : true,
          dmEntityPojo : new kimios.DMEntityPojo({
            type : 1
          })
        }).show();
      }
    });
    this.newFolderItem = new Ext.menu.Item({
      text: kimios.lang('NewFolder'),
      iconCls : 'newfolder',
      scope : this,
      handler: function(){
        new kimios.properties.PropertiesWindow({
          createMode : true,
          dmEntityPojo : new kimios.DMEntityPojo({
            parentType : kimios.explorer.getActivePanel().type,
               parentUid : kimios.explorer.getActivePanel().uid,
               path : kimios.explorer.getActivePanel().path,
               type : 2
          })
        }).show();
      }
    });
    this.importDocumentItem = new Ext.menu.Item({
      text: kimios.lang('ImportDocument'),
      iconCls : 'import',
      scope : this,
      handler: function(){
        new kimios.properties.PropertiesWindow({
          createMode : true,
          dmEntityPojo : new kimios.DMEntityPojo({
            parentType : kimios.explorer.getActivePanel().type,
            parentUid : kimios.explorer.getActivePanel().uid,
            path : kimios.explorer.getActivePanel().path,
            type : 3
          })
        }).show();
      }
    });
    this.refreshItem = new Ext.menu.Item({
      text : kimios.lang('Refresh'),
      iconCls : 'refresh',
      scope : this,
      handler: function(btn, evt){
        kimios.explorer.getToolbar().refresh();
        kimios.explorer.getActivePanel().refresh();
        kimios.explorer.getTreePanel().refresh();
        kimios.explorer.getTasksPanel().refresh();
      }
     });
    this.propertiesItem = new Ext.menu.Item({
      text : kimios.lang('Properties'),
      iconCls: 'properties',
      scope : this,
      handler : function(){
        new kimios.properties.PropertiesWindow({
          versionsMode : false,
          dmEntityPojo : new kimios.DMEntityPojo({
            uid : kimios.explorer.getActivePanel().uid,
            type : kimios.explorer.getActivePanel().type,
            name : kimios.explorer.getActivePanel().name,
            path : kimios.explorer.getActivePanel().path,
            owner : kimios.explorer.getActivePanel().owner,
            ownerSource : kimios.explorer.getActivePanel().ownerSource,
            creationDate : kimios.explorer.getActivePanel().creationDate
          })
        }).show();
      }
    });
    this.menu.add(this.newWorkspaceItem);
    this.menu.add(this.newFolderItem);
    this.menu.add(this.importDocumentItem);
    this.menu.addSeparator();
    this.menu.add(this.refreshItem);
    this.menu.addSeparator();
    this.menu.add(this.propertiesItem);
  },
  
  refresh : function(){
  },
  
  refreshLanguage : function(){
    this.setText(kimios.lang('Actions'));
    this.menu.removeAll();
    this.build();
  }
});
