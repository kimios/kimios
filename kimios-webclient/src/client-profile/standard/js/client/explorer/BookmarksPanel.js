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

kimios.explorer.BookmarksPanel = Ext.extend(Ext.grid.GridPanel,{
  constructor : function(config) {
    this.id = 'kimios-bookmarks-panel',
    this.title = kimios.lang('BookmarksExplorer');
//    this.iconCls = 'qbookmark-cls';
    this.autoScroll = true;
    this.stripeRows = true;
    this.hideHeaders = true;
    
//    this.ddGroup = 'firstGridDDGroup';
//    this.enableDragDrop = true;

    this.store = new DmsJsonStore( {
      url : 'Version',
      fields : kimios.record.dmEntityRecord,
      baseParams : {
        action : 'bookmarks'
      },
      autoLoad : false,
      sortInfo: {
        field: 'name',
        direction: 'ASC'
      }
    });
    
        this.noContentNode = new Ext.tree.TreeNode({
      text : kimios.lang('NoBookmark'),
//      iconCls : 'qbookmark-cls',
      disabled : true
    });
        
        this.cm = new Ext.grid.ColumnModel([{
      align : 'center',
      readOnly : true,
      width : 20,
      hidden : false,
      sortable : false,
      hideable : false,
      fixed: true,
      resizable : false,
      menuDisabled : true,
      renderer : function(val, metaData, record, rowIndex, colIndex, store) {
        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
      }
    },{
      header : kimios.lang('DocumentName'),
      dataIndex : 'name',
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true
    }]);
        
        this.sm = new Ext.grid.RowSelectionModel({
          singleSelect : true
        });
        
        this.viewConfig = {
            forceFit : true,
            scrollOffset : 0
        };
    kimios.explorer.BookmarksPanel.superclass.constructor.call(this, config);
  },
  
  refresh : function(){
    this.setIconClass('loading');
    this.store.reload({
      scope : this,
      callback : function(records, options, success){
        this.setIconClass(null);
        if (kimios.explorer.getActivePanel().view != null){
          if (kimios.explorer.getActivePanel().searchMode == false){
            var type = kimios.explorer.getActivePanel().type;
            kimios.explorer.getActivePanel().dmEntityToolbar.addToBookmarksButton.setDisabled(type != 1 && type != 2);
          }
        }
      }
    });
  },
  
  refreshLanguage : function(){
    this.setTitle(kimios.lang('BookmarksExplorer'));
    this.noContentNode.setText(kimios.lang('NoBookmark'));
    this.doLayout();
  },
  
  initComponent : function(){
    kimios.explorer.BookmarksPanel.superclass.initComponent.apply(this, arguments);
    
    this.on('activate', function(){
      this.refresh();
    }, this);
    
    this.on('rowdblclick', function(grid, rowIndex, ev) {
      var selected = grid.getStore().getAt(rowIndex);
      if (selected.data.type != 3) {
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
          uid : selected.get('uid'),
          type : selected.get('type')
        });
      } else {
        if (kimios.isViewableExtension(selected.data.extension)){
          kimios.viewDoc(selected.data);
        }else{
          window.location.href = kimios.util.getDocumentLink(selected.data.uid);
        }
      }
    }, this);

    this.on('rowcontextmenu', function(grid, rowIndex, e){
      e.preventDefault();
      var sm = grid.getSelectionModel();
      sm.selectRow(rowIndex);
      kimios.ContextMenu.show(new kimios.DMEntityPojo(sm.getSelected().data), e, 'bookmarks');
    }, this);
    
    this.on('containercontextmenu', function(grid, e){
      e.preventDefault();
      kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'bookmarksContainer');
    }, this);
    
  }
  
});
