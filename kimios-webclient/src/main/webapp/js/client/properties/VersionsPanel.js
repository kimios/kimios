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
kimios.properties.VersionsPanel = Ext.extend(Ext.Panel, {
  
  constructor : function(config){
    this.dmEntityPojo = config.dmEntityPojo;
      this.title = kimios.lang('Versions');
      this.iconCls = 'value';
      this.border = false;
      this.loadingRequired = false;
      this.loaded = false;
      this.layout = 'border';
      this.bodyStyle = 'background-color:transparent;';
      this.buttonAlign = 'left';
      this.readOnly = config.readOnly;
      
      var panel = this;
  
      this.addButton = new Ext.Button({
      text : kimios.lang('CreateNewVersion'),
      scope : this,
      hidden : this.readOnly,
      handler : function(){
          new kimios.UploaderWindow({
            context : 'createNewVersion',
            dmEntityPojo : this.dmEntityPojo,
            handle : function(){
              panel.grid.getStore().reload();
            }
          }).show();
      }
      });
      
      this.refreshButton = new Ext.Button({
        iconCls : 'refresh',
        tooltip : kimios.lang('Refresh'),
        scope : this,
        handler : function(){
          this.grid.store.reload();
        }
      });
      
      this.fbar = [this.addButton, '->', this.refreshButton];
      
      this.grid = new Ext.grid.GridPanel({
        region : 'center',
        stripeRows : true,
      viewConfig : {forceFit : true, scrollOffset: 0},
        cm : this.getColumns(),
        sm : new Ext.grid.RowSelectionModel({
          singleSelect : true
        }),
        border : true,
        margins : '5 5 0 5',
        store : new DmsJsonStore( {
        fields : [{
          name : 'author',
          type : 'string'
        },{
          name : 'authorSource',
          type : 'string'
        },{
          name : 'creationDate',
          type : 'long'
        },{
          name : 'modificationDate',
          type : 'long'
        },{
          name : 'documentTypeName',
          type : 'string'
        },{
          name : 'customVersion',
          type : 'string'
        },{
          name : 'customVersionPending',
          type : 'string'
        },{
          name : 'documentTypeUid',
          type : 'int'
        },{
          name : 'documentUid',
          type : 'int'
        },{
          name : 'length',
          type : 'int'
        },{
          name : 'uid',
          type : 'int'
        }],
              sortInfo: {
                  field: 'documentUid',
                  direction: 'DESC'
              },
        url : 'DocumentVersion',
        baseParams : {
          action : 'getDocumentVersions',
          documentUid : this.dmEntityPojo.uid
        }
      })
      });
      var store = this.grid.getStore();
      this.items = [this.grid];
    kimios.properties.VersionsPanel.superclass.constructor.call(this, config);
  },
  
  setPojo : function(pojo){
    this.dmEntityPojo = pojo;
    this.loaded = false;
  },
  
  initComponent: function(){
    kimios.properties.VersionsPanel.superclass.initComponent.apply(this, arguments);
    
    this.on('activate', function(){
      this.doLayout();
      if (this.loaded == false)
        this.grid.store.load();
    }, this);
    
    this.grid.store.on('beforeload', function(store, options){
      this.setIconClass('loading');
    }, this);
    
    this.grid.store.on('load', function(store, records, options){
      this.loaded = true;
      this.setIconClass('value');
    }, this);
    
    this.grid.on('rowcontextmenu', function(grid, rowIndex, e){
      e.preventDefault();
      var sm = grid.getSelectionModel();
      sm.selectRow(rowIndex);
      var data = sm.getSelected().data;
      console.log(data);
      data.type = 3; // missing type
      var pojo = new kimios.DMEntityPojo(data);
      pojo.type = 3;
      pojo.uid = data.documentUid;
      pojo.versionUid = data.uid;
      pojo.lastVersionId = data.uid;
      kimios.ContextMenu.show(pojo, e, 'versions');
    }, this);
    
    this.grid.on('rowdblclick', function(grid, rowIndex, ev) {
      var selectedRecord = grid.getStore().getAt(rowIndex);
      window.location.href = kimios.util.getDocumentVersionLink(selectedRecord.data.uid, selectedRecord.data.documentUid);
    }, this);
    
    this.grid.on('containercontextmenu', function(grid, e){
      e.preventDefault();
    }, this);
  },
  
  getColumns : function(){
    return new Ext.grid.ColumnModel([{
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
        metaData.css = 'meta';
      }
    },{
      header : kimios.lang('VersionNum'),
      dataIndex : 'uid',
      width : 80,
      fixed : true,
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true,
      align : 'left'
    },{
      header : kimios.lang('DocumentType'),
      dataIndex : 'documentTypeName',
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true,
      align : 'left'
    },{
      header : kimios.lang('Size'),
      dataIndex : 'length',
      width : 100,
      fixed : true,
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true,
      align : 'right',
      renderer : function(val, metaData, record, rowIndex, colIndex, store) {
        return (val/1024).toFixed(2)+' '+kimios.lang('Kb');
      }
    },{
      header : kimios.lang('Author'),
      dataIndex : 'author',
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true,
      renderer : function(val, metaData, record, rowIndex, colIndex, store) {
        return val+ '@'+ record.get('authorSource');
      }
    },{
      header : kimios.lang('CreationDate'),
      dataIndex : 'creationDate',
      width : 120,
      fixed : true,
      readOnly : true,
      menuDisabled : true,
      sortable : true,
      hideable : false,
      renderer : function(value){
        return kimios.date(value);
      }
    }, {
      header : kimios.lang('ModificationDate'),
      dataIndex : 'modificationDate',
      width : 140,
      fixed : true,
      readOnly : true,
      menuDisabled : true,
      sortable : true,
      hideable : false,
      renderer : function(value){
        return kimios.date(value);
      }
    },{
      header : kimios.lang('CustomVersion'),
      dataIndex : 'customVersion',
      readOnly : true,
      sortable : true,
      hideable : false,
      menuDisabled : true,
      align : 'left'
    }]);
  }
});
