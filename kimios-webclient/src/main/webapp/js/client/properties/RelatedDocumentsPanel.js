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
kimios.properties.RelatedDocumentsPanel = Ext.extend(Ext.Panel, {
  
  constructor : function(config){
    this.dmEntityPojo = config.dmEntityPojo;
      this.title = kimios.lang('RelatedDocuments');
      this.iconCls = 'attach';
      this.layout = 'border';
      this.loadingRequired = false;
      this.loaded = false;
    this.bodyStyle = 'background-color:transparent;';
      this.buttonAlign = 'left';
      this.border = false;
      this.readOnly = config.readOnly;
      
      this.grid = new Ext.grid.GridPanel({
        region : 'center',
        border : true,
        margins : '5 5 0 5',
          stripeRows : true,
      viewConfig : {forceFit : true,scrollOffset:0},
        store : new DmsJsonStore( {
        fields : kimios.record.dmEntityRecord,
        url : 'Version',
        baseParams : {
          action : 'relatedDocuments',
          documentUid : this.dmEntityPojo.uid
        },
              sortInfo: {
                  field: 'name',
                  direction: 'ASC'
              }
      }),
        cm : this.getColumns(),
        sm : new Ext.grid.RowSelectionModel( {singleSelect : true})
      });
      
      this.addButton = new Ext.Button({
            text: kimios.lang('Add'),
            scope : this,
           hidden : this.readOnly,
            handler: function(){
                var picker = new kimios.picker.DMEntityPicker({
                    title: kimios.lang('AddDocument'),
                    iconCls : 'add',
                    buttonText : kimios.lang('AddDocument'),
                    withDoc : true
                });
                picker.on('entitySelected', function(node){
                  if (node.attributes.type != 3){
                    return false;
                  }
                  kimios.request.addRelatedDocument(
                   this.dmEntityPojo.uid,
                   node.attributes.dmEntityUid,
                   this.grid.store
                  );
                }, this);
                picker.show();
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
      this.items = [this.grid];
    kimios.properties.RelatedDocumentsPanel.superclass.constructor.call(this, config);
  },
  
  setPojo : function(pojo){
    this.dmEntityPojo = pojo;
    this.loaded = false;
  },
  
  initComponent: function(){
    kimios.properties.RelatedDocumentsPanel.superclass.initComponent.apply(this, arguments);
    
    this.on('activate', function(){
      this.doLayout();
      if (this.loaded == false){
        this.grid.store.load();
      }
    }, this);
    
    this.grid.store.on('beforeload', function(store, options){
      this.setIconClass('loading');
    }, this);
    
    this.grid.store.on('load', function(store, records, options){
      this.loaded = true;
      this.setIconClass('attach');
    }, this);
    
    this.grid.on('rowcontextmenu', function(grid, rowIndex, e){
      e.preventDefault();
      var sm = grid.getSelectionModel();
      sm.selectRow(rowIndex);
      var dmEntityPojo = sm.getSelected().data;
      dmEntityPojo.current = this.dmEntityPojo;
      dmEntityPojo.current.store = grid.getStore();
      kimios.ContextMenu.show(dmEntityPojo, e, 'relatedDocuments');
    }, this);
    
    this.grid.on('rowdblclick', function(grid, rowIndex, ev) {
      var selectedRecord = grid.store.getAt(rowIndex);
      window.location.href = kimios.util.getDocumentLink(selectedRecord.data.uid);
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
        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
      }
    },{
      header : kimios.lang('DocumentName'),
      dataIndex : 'name',
      width : 300,
      readOnly : true,
      menuDisabled : true,
      sortable : true,
      hideable : false
    }, {
      header : kimios.lang('DocumentType'),
      dataIndex : 'documentTypeName',
      width : 150,
      readOnly : true,
      menuDisabled : true,
      sortable : true
    },{
      header : kimios.lang('Author'),
      dataIndex : 'owner',
      width : 150,
      readOnly : true,
      menuDisabled : true,
      sortable : true,
      renderer : function(val, metaData, record, rowIndex, colIndex, store) {
        return val+ '@'+ record.get('ownerSource');
      }
    }, {
      header : kimios.lang('CreationDate'),
      dataIndex : 'creationDate',
      width : 140,
      fixed : true,
      readOnly : true,
      menuDisabled : true,
      sortable : true,
      renderer : function(value){
        return kimios.date(value);
      }
    }]);
  }

});
