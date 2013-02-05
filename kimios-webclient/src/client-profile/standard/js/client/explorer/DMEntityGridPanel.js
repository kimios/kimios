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
kimios.explorer.DMEntityGridPanel = Ext.extend(Ext.Panel, {
    constructor:function (config) {
        var _this = this;
        this.loadable = true;
        this.alreadyLoad = false;
        this.lockSearch = false;
        this.closable = true;
        this.title = kimios.lang('Loading');
        this.iconCls = 'loading';
        this.border = false;
        this.layout = 'border';
        this.entityStore = kimios.store.getEntityStore();
        this.breadcrumbToolbar = new kimios.explorer.BreadcrumbToolbar({height:26});
        this.searchToolbar = new kimios.search.SearchToolbar({height:26});
        this.contextToolbar = new Ext.Panel({
            border:false,
            layout:'hbox',
            height:26,
            defaults:{height:26},
            items:[
                new Ext.Container({
                    flex:1,
                    items:[this.breadcrumbToolbar]
                }),
                new Ext.Container({
                    items:[this.searchToolbar]
                })
            ]
        });

        this.tbar = this.contextToolbar;

        this.commentsPanel = new kimios.explorer.CommentsPanel({
            collapsed:true,
            hidden:true,
            width:250,
            region:'east',
            margins:'-1 -1 -1 0'
        });
        this.advancedSearchPanel = new kimios.search.AdvancedSearchPanel({
            region:'north',
            height:180,
            hidden:true,
            border:false
        });
        this.gridPanel = new Ext.grid.GridPanel({
            region:'center',
            border:true,
            stripeRows:true,
            margins:'-1 -1 -1 -1',
            store:kimios.store.getEntitiesStore(),
            columnLines:false,
            enableDragDrop:true,
            enableDD:true,
            ddGroup:'grid2tree',
            ddScroll:true,
            cm:this.getColumns(),
            sm:new Ext.grid.RowSelectionModel({
                singleSelect:false
            }),
            viewConfig:{
                forceFit:true,
                scrollOffset:0
            },
            listeners:{
                "render":{
                    scope:this,
                    fn:function (grid) {
                        var ddrow = new Ext.dd.DropTarget(grid.container, {
                            ddGroup:'grid2tree',
                            copy:false,
                            notifyDrop:function (dd, e, data) {
                                var ds = grid.store;
                                var sm = grid.getSelectionModel();
                                var rows = sm.getSelections();

                                if (dd.getDragData(e)) {
                                    // grid to grid
                                    var line = dd.getDragData(e).rowIndex;
                                    if (typeof(line) != "undefined") {
                                        var dstUid = ds.getAt(line).data.uid;
                                        var dstType = ds.getAt(line).data.type;

                                        if (dstType == 2) {
                                            if (rows.length == 1) {
                                                var srcUid = rows[0].data.uid;
                                                var srcType = rows[0].data.type;
                                                kimios.request.moveDMEntity(srcUid, srcType, dstUid, dstType,
                                                    function () {
                                                        ds.reload();
                                                        Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                                                        kimios.explorer.getViewport().refreshGrids();
                                                    },
                                                    function (resp, opt) {
                                                        if (resp.status == undefined)
                                                            resp.status = 500;
                                                        if (resp.statusText == undefined)
                                                            resp.statusText = 'Unknown error';
                                                        kimios.MessageBox.exception({
                                                            exception:'HTTP error: ' + resp.status + ' ' + resp.statusText,
                                                            stackTrace:resp.responseText
                                                        });

                                                    }
                                                );
                                            } else if (rows.length > 1) {
                                                var pojos = [];
                                                for (var i = 0; i < rows.length; i++) {
                                                    pojos.push(new kimios.DMEntityPojo(rows[i].data));
                                                }
                                                kimios.request.moveDMEntities(pojos, dstUid, dstType,
                                                    function () {
                                                        ds.reload();
                                                        Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                                                        kimios.explorer.getViewport().refreshGrids();
                                                    }
                                                );
                                            }
                                        }
                                    }
                                } else {
                                    // tree to grid
                                    var srcArray = data.node.id.split('_');
                                    var srcUid = srcArray[0];
                                    var srcType = srcArray[1];
                                    var p = _this;

                                    kimios.request.moveDMEntity(srcUid, srcType, p.uid, p.type,
                                        function () {
                                            ds.reload();
                                            Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                                            kimios.explorer.getViewport().refreshGrids();
                                        },
                                        function (resp, opt) {
                                            if (resp.status == undefined)
                                                resp.status = 500;
                                            if (resp.statusText == undefined)
                                                resp.statusText = 'Unknown error';
                                            kimios.MessageBox.exception({
                                                exception:'HTTP error: ' + resp.status + ' ' + resp.statusText,
                                                stackTrace:resp.responseText
                                            });

                                        }
                                    );
                                }
                            }
                        });
                    }
                }
            }
        });

        this.items = [this.advancedSearchPanel, this.gridPanel, this.commentsPanel];
        kimios.explorer.DMEntityGridPanel.superclass.constructor.call(this, config);
    },

    initComponent:function () {
        kimios.explorer.DMEntityGridPanel.superclass.initComponent.apply(this, arguments);

        //Drag'n Drop Across Tab Panel
//    this.on('beforetabchange', function(tabPanel, newTab, currentTab){
//      console.log('beforetabchange');
//    }, this);
//    this.on('contextmenu', function(){
//      console.log('contextmenu');
//    }, this);
//    this.on('tabchange', function(tabPanel, panel){
//      console.log('tabchange');
//    }, this);

        // synchronize explorer with active tab
        this.on('activate', function () {
            if (this.alreadyLoad == true) {
                kimios.explorer.getTreePanel().synchronize(this.path);
                kimios.util.setTitle(this.path == undefined ? '/' : this.path);
            }
            // search field fix
            var sf = this.searchToolbar.searchField;
            sf.setValue(sf.getValue());
            this.doLayout();
        }, this);

        this.gridPanel.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({
                uid:this.uid,
                type:this.type,
                name:this.name,
                path:this.path,
                owner:this.owner,
                ownerSource:this.ownerSource,
                creationDate:this.creationDate
            }), e, 'gridContainer');
        }, this);

        this.gridPanel.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            this.lastSelectedRow = rowIndex;
            var sm = grid.getSelectionModel();
            sm.selectRow(rowIndex, sm.getSelections().length > 1);
            var selections = sm.getSelections();
            if (selections.length == 1) {
                kimios.ContextMenu.show(new kimios.DMEntityPojo(selections[0].data), e);
            } else {
                var multiple = [];
                for (var i = 0; i < selections.length; i++)
                    multiple.push(new kimios.DMEntityPojo(selections[i].data));
                kimios.ContextMenu.show(multiple, e);
            }
        }, this);

        this.gridPanel.getSelectionModel().on('rowselect', function (sm, rowIndex, record) {
            if (this.commentsPanel.enableComments == true) {
                var selections = sm.getSelections();
                if (selections && selections.length == 1 && selections[0].data.type == 3) {
                    this.commentsPanel.loadComments(new kimios.DMEntityPojo(selections[0].data));
                    this.commentsPanel.setVisible(true);
                    this.commentsPanel.expand();
                } else {
                    this.commentsPanel.collapse();
                    this.commentsPanel.setVisible(false);
                    this.commentsPanelenableComments = false;
                    kimios.explorer.getViewport().doLayout();
                }
            }
        }, this);

        this.gridPanel.on('rowdblclick', function (grid, rowIndex, ev) {
            if (ev.ctrlKey || ev.shiftKey) {
                this.lastSelectedRow = rowIndex;
            } else {
                var selected = grid.getStore().getAt(rowIndex);
                var uid = selected.get('uid');
                var type = selected.get('type');
                var name = selected.get('name');
                if (type == 3) {
                    if (kimios.isViewableExtension(selected.data.extension)) {
                        kimios.viewDoc(selected.data);
                    } else {
                        window.location.href = kimios.util.getDocumentLink(selected.data.uid);
                    }
                } else {
                    this.loadEntity({
                        uid:uid,
                        type:type
                    });
                }
            }
        }, this);

//    this.gridPanel.getSelectionModel().on('selectionchange', function(sm){
//      if (sm.getSelections().length != 1){
//        this.commentsPanel.setVisible(false);
//        this.doLayout();
//      }
////      else{
////        if (sm.getSelected().data.type == 3){
////          this.commentsPanel.setVisible(true);
////        }
//      
////        var data = sm.getSelected().data;
////        var pojo = new kimios.DMEntityPojo(data);
////        kimios.explorer.getViewport().quickViewPanel.remove(this.propertiesPanel);
////        this.propertiesPanel = new kimios.properties.PropertiesPanel({
////          dmEntityPojo : new kimios.DMEntityPojo(data)
////        });
////        kimios.explorer.getViewport().quickViewPanel.add(this.propertiesPanel);
////        kimios.explorer.getViewport().quickViewPanel.doLayout();
//      
////      }
//      
//    }, this);

        this.gridPanel.getStore().on('load', function (store, records, options) {
            if (this.lastSelectedRow != undefined) {
                this.gridPanel.getSelectionModel().selectRow(this.lastSelectedRow);
            }
        }, this);
    },

    search:function (searchConfig) {
        this.setTitle(kimios.lang('Searching'));
        this.setIconClass('loading');

        // quick search
        if (searchConfig.uid == undefined && searchConfig.text == undefined && searchConfig.documentType == undefined) {
            this.quickSearch(searchConfig);
        }

        // advanced search
        else {
            this.advancedSearch(searchConfig);
        }
    },

    quickSearch:function (searchConfig, handle) {
        this.gridPanel.reconfigure(kimios.store.getQuickSearchStore(), this.gridPanel.getColumnModel());

        // search by document name
        this.gridPanel.getStore().load({
            scope:this,
            params:{
                name:searchConfig.name,
                dmEntityUid:searchConfig.fromUid,
                dmEntityType:searchConfig.fromType
            },
            callback:function (records, options, success) {
                this.lockSearch = true;
                this.setTitle(kimios.lang('DocumentsFound') + ' (' + records.length + ')');
                this.setIconClass('view');
            }
        });
    },

    advancedSearch:function (searchConfig, form) {
        this.gridPanel.reconfigure(kimios.store.getAdvancedSearchStore(), this.gridPanel.getColumnModel());

        // search by document body
        if (form == undefined) {
            this.gridPanel.getStore().load({
                scope:this,
                params:{
                    text:searchConfig.text,
                    dmEntityUid:searchConfig.fromUid,
                    dmEntityType:searchConfig.fromType
                },
                callback:function (records, options, success) {
                    this.lockSearch = true;
                    this.setTitle(kimios.lang('DocumentsFound') + ' (' + records.length + ')');
                    this.setIconClass('view');
                }
            });
        }

        // advanced search
        else {
            this.gridPanel.getStore().setBaseParam('action', 'Advanced');
            this.gridPanel.getStore().setBaseParam('name', searchConfig.name);
            this.gridPanel.getStore().setBaseParam('uid', searchConfig.uid);
            this.gridPanel.getStore().setBaseParam('text', searchConfig.text);
            this.gridPanel.getStore().setBaseParam('dmEntityUid', searchConfig.fromUid);
            this.gridPanel.getStore().setBaseParam('dmEntityType', searchConfig.fromType);
            this.gridPanel.getStore().setBaseParam('documentType', searchConfig.documentType);
            var fields = form.getForm().getFieldValues();

            var obj = "({";
            for (var key in fields) {
                var value = null;

                // is date
                if (key.indexOf('dfrom') > 0 || key.indexOf('dto') > 0)
                    value = fields[key] ? fields[key].format('Y-m-d') : '';
                else
                    value = fields[key] ? fields[key] : '';

                obj += "'" + key + "':'" + value + "',";
            }
            if (obj.length > 2) {
                obj = obj.substring(0, obj.length - 1);
            }
            this.gridPanel.getStore().load({
                scope:this,
                params:eval(obj + "})"),
                callback:function (records, options, success) {
                    this.lockSearch = true;
                    this.setTitle(kimios.lang('DocumentsFound') + ' (' + records.length + ')');
                    this.setIconClass('view');
                }
            });
        }
    },

    refresh:function () {
        this.loadEntity();
    },

    loadEntity:function (entityConfig) {
        // if configuration is not specified, keep the last values
        if (entityConfig != null) {
            this.uid = entityConfig.uid;
            this.type = entityConfig.type;
        }
        if (this.lockSearch == true) {
            this.gridPanel.reconfigure(kimios.store.getEntitiesStore(), this.gridPanel.getColumnModel());
            this.lockSearch = false;
        }
        this.setIconClass('loading');

        // load home (root node)
        if (this.uid == undefined && this.type == undefined) {
            this.name = kimios.explorer.getTreePanel().getRootNode().text;
            this.path = undefined;
            this.setTitle(this.name);
            this.breadcrumbToolbar.setPath(this.path);
            this.loadEntities();
        }
        // load another sub-node
        else {
            // get the current entity properties
            this.entityStore.load({
                scope:this,
                params:{
                    dmEntityUid:this.uid,
                    dmEntityType:this.type
                },
                callback:function (records, options, success) {
                    if (records[0] != undefined) {
                        this.name = records[0].data.name;
                        this.path = records[0].data.path;
                        this.owner = records[0].data.owner;
                        this.ownerSource = records[0].data.ownerSource;
                        this.creationDate = records[0].data.creationDate;
                        this.setTitle(this.name);
                        this.breadcrumbToolbar.setPath(this.path);
                        this.loadEntities();
                    }
                }
            });
            // close tab corresponding to deleted entity
            this.entityStore.on('exception', function () {
                this.destroy();
            }, this);
        }
    },

    loadEntities:function () {
        this.gridPanel.getStore().setDefaultSort('creationDate', 'desc');
        this.gridPanel.getStore().load({
            scope:this,
            params:{
                dmEntityUid:this.uid,
                dmEntityType:this.type
            },
            callback:function (records, options, success) {
                // only synchronize the current active panel
                if (this.getId() == kimios.explorer.getActivePanel().getId()) {
                    kimios.explorer.getTreePanel().synchronize(this.path);
                }
                this.alreadyLoad = true;
                kimios.util.setTitle(this.path == undefined ? '/' : this.path);
                this.setIconClass(this.type == undefined || this.type == '' ? 'home' : 'dm-entity-tab-' + this.type);
                this.breadcrumbToolbar.refreshButton.enable();


            }
        });
    },

    refreshLanguage:function () {
        this.breadcrumbToolbar.refreshLanguage();
        this.searchToolbar.refreshLanguage();
        this.advancedSearchPanel.refreshLanguage();
        this.gridPanel.reconfigure(this.gridPanel.getStore(), this.getColumns());
        this.gridPanel.getView().refresh(true);
        this.contextToolbar.doLayout();
        kimios.explorer.getActivePanel().commentsPanel.refreshLanguage();
        this.doLayout();
    },

    getColumns:function () {
        return new Ext.grid.ColumnModel([
            {
                align:'center',
                readOnly:true,
                width:18,
                hidden:false,
                sortable:false,
                hideable:false,
                fixed:true,
                resizable:false,
                menuDisabled:true,
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);

                }
            },
            {
                header:kimios.lang('DocumentName'),
                dataIndex:'name',
//        width : 100,
                readOnly:true,
                sortable:true,
                hideable:false,
                menuDisabled:true,
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.get('checkedOut'))
                        val = '<span style="color:red;">' + val + '</span>';
                    return val;
                }
            },
            {
                align:'center',
                readOnly:true,
                width:20,
                hidden:false,
                sortable:false,
                hideable:false,
                fixed:true,
                resizable:false,
                menuDisabled:true,
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.get('checkedOut'))
                        metaData.css = 'checked-out';
                }
            },
            {
                header:kimios.lang('CreationDate'),
                dataIndex:'creationDate',
                width:120,
                fixed:true,
                readOnly:true,
                sortable:true,
                resizable:false,
                menuDisabled:true,
                align:'left',
                renderer:function (value) {
                    return kimios.date(value);
                }
            },
            {
                header:kimios.lang('UpdateDate'),
                dataIndex:'updateDate',
                width:120,
                fixed:true,
                readOnly:true,
                sortable:true,
                resizable:false,
                menuDisabled:true,
                align:'left',
                renderer:function (value, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type <= 2)
                        return 'N/A';
                    else
                        return kimios.date(value);
                }
            },
            {
                header:kimios.lang('Author'),
                dataIndex:'owner',
                width:80,
                readOnly:true,
                sortable:true,
                menuDisabled:true,
                align:'left',
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                    return val + '@' + record.get('ownerSource');
                }
            },
            {
                header:kimios.lang('DocumentType'),
                dataIndex:'documentTypeName',
                width:50,
                readOnly:true,
                sortable:true,
                menuDisabled:true,
                align:'left',
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                    switch (record.get('type')) {
                        case 1:
                            return kimios.lang('Workspace');
                        case 2:
                            return kimios.lang('Folder');
                        case 3:
                            var extension = record.get('extension') == undefined ? '' : record.get('extension').toUpperCase();
                            return (val == undefined || val == '' ? kimios.lang('Document') : val) + ' (' + extension.toUpperCase() + ')';
                    }
                }
            },
            {
                header:kimios.lang('Size'),
                dataIndex:'length',
                width:30,
                readOnly:true,
                sortable:true,
                menuDisabled:true,
                align:'right',
                renderer:function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.get('type') == 3)
                        return (val / 1024).toFixed(2) + ' ' + kimios.lang('Kb');
                }
            },
            {
                header:kimios.lang('WorkflowStatus'),
                dataIndex:'workflowStatusName',
                width:50,
                readOnly:true,
                sortable:true,
                menuDisabled:true,
                align:'left',
                renderer:function (value, metaData, record, rowIndex, colIndex, store) {
                    if (record.get('type') == 3) {
                        var getStyle = function (bg) {
                            return 'font-weight:bold;display:block;color:white;background-color:' + bg + ';padding-left:2px;margin-right:20px;';
                        };
                        var val = value == '' ? '&nbsp;' : value;
                        if (record.get('outOfWorkflow') == false) {
                            return '<span style="' + getStyle('tomato') + '">' + val + '</span>';
                        } else if (value != '') {
                            return '<span style="' + getStyle('olive') + '">' + val + '</span>';
                        }
                    }
                }
            }
//      ,{
//        fixed : true,
//        align : 'right',
//        readOnly : true,
//        dataIndex : 'uid',
//        width : 50,
//        hidden : false,
//        sortable : true,
//        menuDisabled : true,
//        renderer : function(val, metaData, record, rowIndex, colIndex, store) {
//          return '<span style="color:#999;">' + val + '</span>';
//        }
//      }
        ]);
    }

});
