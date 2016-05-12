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
kimios.properties.CommentsPanel = Ext.extend(Ext.Panel, {
  
  constructor : function(config){
    this.dmEntityPojo = config.dmEntityPojo;
      this.title = kimios.lang('Comments');
      this.iconCls = 'comment';
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
          hideHeaders : true,
        viewConfig : {forceFit : true, scrollOffset: 0},
          store : new DmsJsonStore( {
          fields : [{
            name : 'authorName',
            type : 'string'
          },{
            name : 'authorSource',
            type : 'string'
          },{
            name : 'comment',
            type : 'string'
          },{
            name : 'date',
            type : 'long'
          },{
            name : 'documentVersionUid',
            type : 'long'
          },{
            name : 'uid',
            type : 'long'
          }],
          sortInfo: {
                    field: 'date',
                    direction: 'DESC'
                },
          url : 'DocumentVersion'
          
        }),
          cm : this.getColumns(),
          sm : new Ext.grid.RowSelectionModel({
            singleSelect : true
          })
        });
      
      this.addButton = new Ext.Button({
	      text : kimios.lang('AddComment'),
	      scope : this,
	      hidden : this.readOnly,
	      handler : function(){
	    	  Ext.MessageBox.prompt(
		          kimios.lang('AddComment'),
		          kimios.lang('Comment'),
		          function(button, comment){
		        	  if (button == 'ok'){
		        		  var str = this.grid.store;
		        		  kimios.request.addComment(this.lastVersionUid, comment, function(){
		        			  str.reload();
		        			  });
	        		  }
		          }, this, true);
    	  }
      });
      
      this.refreshButton = new Ext.Button({
        iconCls : 'refresh',
        tooltip : kimios.lang('Refresh'),
        scope : this,
        handler : function(){
          this.lastVersionStore.reload();
        }
      });
      
    this.fbar = [this.addButton, '->', this.refreshButton];
      
 
      
      this.items = [this.grid];
    kimios.properties.CommentsPanel.superclass.constructor.call(this, config);
  },
  
  setPojo : function(pojo){
    this.dmEntityPojo = pojo;
    this.loaded = false;
  },
  
  initComponent: function(){
    kimios.properties.CommentsPanel.superclass.initComponent.apply(this, arguments);
    this.lastVersionStore = kimios.store.getLastVersionStore(this.dmEntityPojo.uid);
    
    this.on('activate', function(){
      this.doLayout();
      if (this.loaded == false){
        this.lastVersionStore.load();
      }
    }, this);
    
    if (this.dmEntityPojo.versionUid != undefined){
      this.lastVersionUid = this.dmEntityPojo.versionUid; 
      this.grid.store.load({
        params : {
          action : 'getComments',
          documentVersionUid : this.dmEntityPojo.versionUid
        },
        callback : function(){
          this.setIconClass('comment');
        },
        scope : this
      });
      
    }else{
      this.grid.store.on('beforeload', function(store, options){
        this.setIconClass('loading');
      }, this);
      
      this.lastVersionStore.on('beforeload', function(store, options){
        this.setIconClass('loading');
      }, this);
      
      this.lastVersionStore.on('load', function(store, lastVersionRecords, options){
        this.lastVersionUid = lastVersionRecords[0].data.uid;
        this.grid.store.load({
          params : {
            action : 'getComments',
            documentVersionUid : this.lastVersionUid
          }
        });
      }, this);
    }
    
    this.grid.store.on('load', function(store, records, options){
      this.loaded = true;
      this.setIconClass('comment');
    }, this);
    
    this.grid.on('rowdblclick', function(grid, rowIndex, e) {
      var record = grid.getStore().getAt(rowIndex);
      
      Ext.MessageBox.prompt(
        kimios.lang('ModifyComment'),
        kimios.lang('Comment'),
        function(button, comment){
          if (button == 'ok'){
            kimios.request.updateComment(record.data.documentVersionUid, record.data.uid, comment, grid.getStore());
          }
        },
        this,
        true,
        record.data.comment);
    }, this);
    
    this.grid.on('rowcontextmenu', function(grid, rowIndex, e){
      e.preventDefault();
      var sm = grid.getSelectionModel();
      sm.selectRow(rowIndex);
      
      // attach comment data and comment store to POJO (because ContextMenu need a DM Entity POJO!)
      this.dmEntityPojo.comment = sm.getSelected().data;
      this.dmEntityPojo.comment.store = grid.getStore();
      
      kimios.ContextMenu.show(this.dmEntityPojo, e, 'comments');
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
        metaData.css = 'comment';
      }
    },{
      readOnly : true,
      sortable : true,
      hideable : false,
      renderer : function(value, metaData, record){
        var html = '<span style="font-size:10px;">'+record.get('comment')+'</span><br/>';
        html += '<span style="color:gray;font-size:10px;">'+record.get('authorName')+ '@'+ record.get('authorSource')+' - '+kimios.date(record.get('date'))+'</span>';
        return html;
      }
    }]);
  }

});
