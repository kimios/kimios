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
    constructor: function (config) {
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
        this.breadcrumbToolbar = new kimios.explorer.BreadcrumbToolbar({height: 26});
        this.searchToolbar = new kimios.search.SearchToolbar({height: 26});

        this.breadCrumbPanel = new Ext.Panel({
            border: false,
            region: 'center',
            items: [new Ext.Panel({
                border: false,
                items: [this.breadcrumbToolbar]
            })]
        });
        this.contextToolbar = new Ext.Panel({
            border: false,
            layout: 'border',
            height: 26,
            defaults: {height: 26},
            items: [
                this.breadCrumbPanel,
                new Ext.Panel({
                    border: false,
                    width: 205,
                    region: 'east',
                    items: [this.searchToolbar]
                })
            ]
        });


        this.tbar = this.contextToolbar;

        this.pagingSize = 20;

        /* dummy paging init */
        var comboPageSize = new Ext.form.ComboBox({
            name: 'perpage',
            width: 100,
            store: new Ext.data.ArrayStore({
                fields: ['id'],
                data: [
                    ['20'],
                    ['50'],
                    ['100'],
                    ['200'],
                    ['300'],
                    ['500']
                ]
            }),
            mode: 'local',
            value: this.pagingSize + '',
            listWidth: 100,
            triggerAction: 'all',
            displayField: 'id',
            valueField: 'id',
            editable: false,
            forceSelection: true
        });

        this.editSearchButton = new Ext.Button({
            text: 'Edit Search Query',
            handler: function(){
                if(this.searchRequest){
                    //alert edit

                    var windowSearch = new kimios.search.AdvancedSearchPanel({
                        title: this.searchRequest.name +  ' - Edit',
                        listeners: {
                            close: function(){

                            }
                        }
                    });
                    //panEdit.loadForm(this.searchRequest)
                    windowSearch.on('reqlaunched', function(tmpReq){
                        windowSearch.close();
                        this.refreshSearchRequestData(tmpReq);
                    },this);
                    windowSearch.on('reqreload', function(searchRequest){
                        windowSearch.close();
                        this.refreshSearchRequestData(searchRequest);
                    }, this);
                    windowSearch.show();
                    windowSearch.loadForm(this.searchRequest);
                }
            },
            scope: this
        });

        this.exportCsvButton = new Ext.Toolbar.Button({
            tooltip: kimios.lang('ExportCsv'),
            iconCls: 'exportcsv',
            scope: this,
            handler: function () {
                kimios.explorer.getActivePanel().csvExport();
            }
        });

        this.pagingToolBar = new Ext.PagingToolbar({
            store: this.entityStore,
            displayInfo: true,
            pageSize: this.pagingSize,
            displayMsg: '',
            emptyMsg: '',
            prependButtons: false,
            hidden: true,
            items: [
                '-',
                this.editSearchButton,
                this.exportCsvButton,
                'Page Size ',
                comboPageSize
            ]
        });

        // hide specific refresh button
        this.pagingToolBar.refresh.hideParent = true;
        this.pagingToolBar.refresh.hide();


        comboPageSize.on('select', function (combo, record) {
            this.pagingToolBar.pageSize = parseInt(record.get('id'), 10);
            this.pagingToolBar.doLoad(this.pagingToolBar.cursor);
        }, this);




        this.commentsPanel = new kimios.explorer.CommentsPanel({
            collapsed: true,
            hidden: true,
            width: 250,
            region: 'east',
            margins: '-1 -1 -1 0'
        });
        /*this.advancedSearchPanel = new kimios.search.AdvancedSearchPanel({
            region: 'north',
            height: 250,
            hidden: true,
            border: false
        });*/
        this.gridPanel = new Ext.grid.GridPanel({
            region: 'center',
            border: true,
            stripeRows: false,
            stateId: 'gridEntitiesState',
            stateful: true,
            margins: '-1 -1 -1 -1',
            store: kimios.store.getEntitiesStore(),
            columnLines: false,
            enableDragDrop: true,
            enableDD: true,
            ddGroup: 'grid2tree',
            ddScroll: true,
            cm: this.getColumns(),
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: false
            }),
            viewConfig: {
                forceFit: true,
                scrollOffset: 0
            },
            tbar: this.pagingToolBar,
            listeners: {
                afterRender: {
                    scope: this,
                    fn: function () {

                        var me = this;

                        Ext.EventManager.on(this.getEl().id, 'dragover', handleDragOver);
                        Ext.EventManager.addListener(this.getEl().id, 'drop', function (evt) {
                            var elem = me;
                            handleFileSelect(evt, elem);
                        });
                        var dropZone = this.getEl();
                        /*var dropZone = document.getElementById(this.getEl().id);
                         dropZone.addEventListener('dragover', handleDragOver, false);
                         var me = this;
                         dropZone.addEventListener('drop', function (evt) {
                         var elem = me;
                         handleFileSelect(evt, elem);
                         }, false);*/
                    }
                },
                "render": {
                    scope: this,
                    fn: function (grid) {
                        var ddrow = new Ext.dd.DropTarget(grid.container, {
                            ddGroup: 'grid2tree',
                            copy: false,
                            notifyDrop: function (dd, e, data) {


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
                                        }
                                    );
                                }
                            }
                        });


                    }
                }
            }
        });

        this.gridPanel.getState = function () {
            var ret = Ext.grid.GridPanel.prototype.getState.call(this, arguments);
            return ret;
        };

        this.gridPanel.applyState = function (state) {
            var cs = state.columns;
            if (cs.length !== 0) {
                for (var i = 0, len = cs.length; i < len; i++) {
                    var s = cs[i], c = Ext.getCmp(s.id);
                    if (typeof c !== "undefined") {
                        if (typeof s.hidden !== "undefined") {
                            c.hidden = s.hidden;
                        }
                    }
                }
            }
        };

        this.virtualTreePanel = new kimios.search.VirtualTreeGridPanel({
            //queryId: searchQueryId,
            currentPath: '',
            prettyPath: '',
            //searchStore: searchStore,
            breadcrumbToolbar: this.breadcrumbToolbar,
            border: false
            //height: 300,
            //collapseMode: 'mini',
            //collapsible: true
        });
        var tabPanel = this;
        this.virtualTreePanel.on('searchloaded', function (globalDocumentCount) {
            tabPanel.setTitle(kimios.lang('DocumentsFound') + ' (' + globalDocumentCount + ')');
        });

        this.northCenterContainer = new Ext.Panel({
            region: 'north',
            border: false,
            split: true,
            layout: 'fit',
            title: 'Search folders',
            height: 300,
            hidden: true,
            items: [this.virtualTreePanel]
        });

        this.centerContainer = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'border',
            items: [this.gridPanel, this.northCenterContainer]
        });
        this.items = [//this.advancedSearchPanel,
                this.centerContainer, this.commentsPanel];

        kimios.explorer.DMEntityGridPanel.superclass.constructor.call(this, config);
    },
    displayVirtualTree: function (searchQueryId, searchStore) {
        if (!this.virtualTreePanel) {
            this.northCenterContainer.removeAll();
            this.northCenterContainer.add(this.virtualTreePanel);
            this.northCenterContainer.setVisible(true);
            this.doLayout();
        } else {
            /*if (kimios.explorer.getActivePanel().advancedSearchPanel.isVisible())
                kimios.explorer.getActivePanel().advancedSearchPanel.hidePanel();*/

            this.northCenterContainer.removeAll(false);
            this.northCenterContainer.add(this.virtualTreePanel);
            this.northCenterContainer.setVisible(true);
            this.northCenterContainer.doLayout();
            this.northCenterContainer.enable();
            this.virtualTreePanel.currentPath = '';
            this.virtualTreePanel.prettyPath = '';
            this.virtualTreePanel.queryId = searchQueryId;
            this.virtualTreePanel.baseSearchStore = searchStore;

            this.virtualTreePanel.reconfigure(kimios.store.getVirtualEntityStore({
                queryId: this.virtualTreePanel.queryId
            }), this.virtualTreePanel.getColumnModel());


        }
        this.breadcrumbToolbar.virtualMode = true;
        this.breadcrumbToolbar.standardMode = false;
        this.searchToolbar.disable();
        this.breadcrumbToolbar.show();
        this.virtualTreePanel.setPath(undefined, '');
        this.doLayout();
        this.virtualTreePanel.doLayout();

        this.virtualTreeEnabled = true;

    },
    hideVirtualTree: function () {

        this.northCenterContainer.disable();
        this.northCenterContainer.setVisible(false);
        this.northCenterContainer.removeAll(false);
        this.northCenterContainer.doLayout();
        this.breadcrumbToolbar.virtualMode = false;
        this.breadcrumbToolbar.standardMode = true;

        this.searchToolbar.enable();
        this.doLayout();
        this.virtualTreeEnabled = false;

    },
    refreshSearchRequestData: function (savedReq) {
        this.lockSearch = true;
        if(savedReq) {
            this.searchRequest = savedReq;
            //this.advancedSearchPanel.searchRequest = savedReq;
        }
        this.setTitle(this.searchRequest.name +
            ' - ' + kimios.lang('DocumentsFound') +
            ' (' +
            this.gridPanel.getStore().getTotalCount() + ')');
        this.setIconClass('view');
    },
    loadAndExecuteQuery: function (searchRequest) {
        this.searchRequest = searchRequest;
        this.gridPanel.getStore().on('load', function(){
            this.refreshSearchRequestData(searchRequest);
        }, this);
        /*this.advancedSearchPanel.removeListener('reqreload', this.refreshSearchRequestData,  this);
        this.advancedSearchPanel.on('reqreload', this.refreshSearchRequestData, this)*/
        this.pagingToolBar.refresh.show();
        this.contextToolbar.hide();
        this.gridPanel.getStore().load({
                scope: this,
                params: {
                    start: 0,
                    limit: this.pagingToolBar.pageSize
                }
            }
        );

    },
    initComponent: function () {
        kimios.explorer.DMEntityGridPanel.superclass.initComponent.apply(this, arguments);
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
                uid: this.uid,
                type: this.type,
                name: this.name,
                path: this.path,
                owner: this.owner,
                ownerSource: this.ownerSource,
                creationDate: this.creationDate
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
                    this.commentsPanel.enableComments = false;
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
                        //window.location.href = kimios.util.getDocumentLink(selected.data.uid);
                        kimios.util.download(kimios.util.getDocumentLink(selected.data.uid));
                    }
                } else {
                    this.loadEntity({
                        uid: uid,
                        type: type
                    });
                }
            }
        }, this);

        this.gridPanel.getStore().on('load', function (store, records, options) {
            if (this.lastSelectedRow != undefined) {
                this.gridPanel.getSelectionModel().selectRow(this.lastSelectedRow);
            }
        }, this);
    },

    search: function (searchConfig) {
        this.setTitle(kimios.lang('Searching'));
        this.setIconClass('loading');

        // quick search
        if (searchConfig.DocumentUid == undefined && searchConfig.DocumentBody == undefined && searchConfig.DocumentTypeUid == undefined) {
            this.quickSearch(searchConfig);
        }
        // advanced search
        else {
            this.advancedSearch(searchConfig);
        }
    },

    searchGeneric: function (searchConfig) {
        this.setTitle(kimios.lang('Searching'));
        this.setIconClass('loading');

        this.advancedSearch(searchConfig);
    },

    quickSearch: function (searchConfig, handle) {
        var searchStore = kimios.store.getQuickSearchStore({
            name: searchConfig.DocumentName,
            dmEntityUid: searchConfig.fromUid,
            dmEntityType: searchConfig.fromType
        });
        this.gridPanel.reconfigure(searchStore, this.gridPanel.getColumnModel());
        this.displayPagingToolBar(searchStore);
        var me = this;
        // search by document name
        this.gridPanel.getStore().load({
            scope: this,
            params: {
                start: 0,
                limit: this.pagingSize
            },
            callback: function (records, options, success) {
                this.lockSearch = true;
                me.quickSearchMode = true;
                me.advancedSearchMode = false;
                this.quickSearchConfig = searchConfig;
                this.setTitle(kimios.lang('DocumentsFound') + ' (' + this.gridPanel.getStore().getTotalCount() + ')');
                this.setIconClass('view');
            }
        });
    },

    displayPagingToolBar: function (searchStore, virtualTreeEnable) {
        if (searchStore) {
            this.pagingToolBar.bindStore(searchStore);
            if (virtualTreeEnable) {
                var virtualTreePanel = this.virtualTreePanel;
                this.pagingToolBar.on('beforechange', function (pg, params) {
                    if (virtualTreePanel && virtualTreePanel.currentPath) {
                        params.virtualPath = virtualTreePanel.currentPath;
                    }
                    return true;
                });
                this.breadcrumbToolbar.virtualTreePanel = virtualTreePanel;
                this.breadcrumbToolbar.enable();
            } else {
                this.breadcrumbToolbar.disable();
            }
            this.pagingToolBar.setVisible(true);
            this.pagingToolBar.enable();
            this.pagingToolBar.refresh.show();
            this.contextToolbar.hide();
        } else {
            this.pagingToolBar.disable();
            this.pagingToolBar.hide();
            this.breadcrumbToolbar.enable();
        }
        this.breadcrumbToolbar.virtualMode = virtualTreeEnable;
        this.breadcrumbToolbar.standardMode = !virtualTreeEnable;


        this.doLayout();
    },

    hidePagingToolBar: function () {
        this.pagingToolBar.purgeListeners();
        this.pagingToolBar.disable();
        this.pagingToolBar.hide();
        this.breadcrumbToolbar.enable();
        this.breadcrumbToolbar.show();
        this.breadcrumbToolbar.virtualMode = false;
        this.breadcrumbToolbar.standardMode = true;
        this.hideVirtualTree();
        this.contextToolbar.show();
    },
    
    csvExport: function(){
        var tab = kimios.explorer.getActivePanel();

        if (tab.lockSearch) {

            if(!this.quickSearchMode){
                var params = null;
                if(!this.quickSearchConfig){
                    params = {};
                    for(var c in tab.searchRequest.criteriasList){
                        var el = tab.searchRequest.criteriasList[c];
                        if(el && el.fieldName)
                            params[el.fieldName] = el.query;
                    }
                    params.autoSave = false;
                } else {
                    params = this.quickSearchConfig;
                }
                
                var searchStore = kimios.store.getAdvancedSearchCsvExportStore(params);
                searchStore.load({
                    scope: this,
                    params: {
                        start: 0,
                        limit: this.pagingSize
                    },
                    callback: function (records, options, success) {
                        if (console) {
                            console.log(records);
                        }
                        window.location.href = getBackEndUrl('__dl__csv__') + '&__f=' + records[0].get('fileExport');
                    }
                });
            } else {
                var searchStore = kimios.store.getQuickSearchCsvStore({
                    name: this.quickSearchConfig.DocumentName,
                    dmEntityUid: this.quickSearchConfig.fromUid,
                    dmEntityType: this.quickSearchConfig.fromType
                });
                searchStore.load({
                    scope: this,
                    params: {
                        start: 0,
                        limit: this.pagingSize
                    },
                    callback: function (records, options, success) {
                        if (console) {
                            console.log(records);
                        }
                        window.location.href = getBackEndUrl('__dl__csv__') + '&__f=' + records[0].get('fileExport');
                    }
                });
            }

        } else {
            // standard folder export
            if(this.type == 2){
                kimios.ajaxRequestWithAnswer('DmsEntity', {
                    action: 'exportCsv',
                    folderId: this.uid
                }, function(data){
                    var res = Ext.util.JSON.decode(data.responseText);
                    window.location.href = getBackEndUrl('__dl__csv__') + '&__f=' + res[0]['fileExport']
                })
            }
        }

    },

    advancedSearch: function (searchConfig, form, existingRequest) {

        // search by document body
        this.hideVirtualTree();

        this.quickSearchMode = false;
        this.quickSearchConfig = null;
        this.advancedSearchMode = true;
        if (form == undefined) {

            //handle Quick Search On Content !
            this.quickSearchConfig = searchConfig;
            var searchStore = kimios.store.getAdvancedSearchStore(searchConfig);
            this.gridPanel.reconfigure(searchStore, this.gridPanel.getColumnModel());
            this.displayPagingToolBar(this.gridPanel.getStore());
            this.gridPanel.getStore().load({
                scope: this,
                params: {
                    start: 0,
                    limit: this.pagingSize
                },
                callback: function (records, options, success) {
                    this.lockSearch = true;
                    this.setTitle(searchStore.getTotalCount() + ' ' + kimios.lang('DocumentsFound'));
                    this.setIconClass('view');
                }
            });
        }

        // advanced search
        else {

            var fields = form.getForm().getFieldValues();

            var obj = "({";
            for (var key in fields) {
                var value = null;
                // is date
                if (fields[key] && fields[key] instanceof Date)
                    value = fields[key] ? fields[key].format('Y-m-d') : '';
                else if (form.metaFieldsMapping && form.metaFieldsMapping.filter(function (it) {
                        return it.crit == key;
                    }).length == 1) {
                    var tmpVal = fields[key];
                    if (tmpVal.indexOf(',') > -1) {
                        value = JSON.stringify(tmpVal.split(','));
                    } else {
                        value = JSON.stringify([fields[key]]);
                    }
                } else
                    value = fields[key] ? fields[key] : '';

                obj += "'" + key + "':'" + value + "',";
            }
            if (obj.length > 2) {
                obj = obj.substring(0, obj.length - 1);
            }


            var params = eval(obj + "})");


            params.DocumentBody = searchConfig.DocumentBody;
            params.DocumentName = searchConfig.DocumentName;
            params.DocumentUid = searchConfig.DocumentUid;
            params.DocumentTypeUid = searchConfig.DocumentTypeUid;
            params.DocumentParent = searchConfig.DocumentParent;
            params.DocumentOwner = searchConfig.DocumentOwner;
            params.DocumentVersionUpdateDate_from = searchConfig.DocumentVersionUpdateDate_from;
            params.DocumentVersionUpdateDate_to = searchConfig.DocumentVersionUpdateDate_to;
            params.DocumentWorkflowStatusName = searchConfig.DocumentWorkflowStatusName;


            params.autoSave = form.autoSave;

            var searchStore = kimios.store.getAdvancedSearchStore(params);


            var tempSearchRequest = existingRequest ? existingRequest : {};

            tempSearchRequest.criteriasList = [];
            for(var p in params){

                var crit = {
                    fieldName: p,
                    query: params[p]
                };
                tempSearchRequest.criteriasList.push(crit);
            }

            tempSearchRequest.criteriasListJson = Ext.util.JSON.encode(tempSearchRequest.criteriasList);

            this.gridPanel.reconfigure(searchStore, this.gridPanel.getColumnModel());

            var pParams = {
                start: 0,
                limit: this.pagingSize
            };

            this.displayPagingToolBar(this.gridPanel.getStore());
            //load config:
            var cfg = {
                scope: this,
                params: pParams
            };
            if(!existingRequest || !existingRequest.id){

                cfg.callback =  function (records, options, success) {
                    this.lockSearch = true;
                    this.setTitle(kimios.lang('DocumentsFound') + ' (' + this.gridPanel.getStore().getTotalCount() + ')');
                    this.setIconClass('view');
                };
            }
            this.gridPanel.getStore().load(cfg);
            this.searchRequest = tempSearchRequest;
            return tempSearchRequest;

        }
    },

    refresh: function () {
        this.loadEntity();
    },
          
    loadEntity: function (entityConfig, me) {
        var tab = kimios.explorer.getActivePanel();
        tab.contextToolbar.show();
        if (tab.searchToolbar.searchField.isSearchMode == true) {
            tab.searchToolbar.searchField.onTrigger2Click();

        } else if (tab.advancedSearchPanel && tab.advancedSearchPanel.isSearchMode == true) {
            tab.advancedSearchPanel.search();
        } else {

            // if configuration is not specified, keep the last values
            if (entityConfig != null) {
                this.uid = entityConfig.uid;
                this.type = entityConfig.type;
            }
            if (this.lockSearch || this.virtualTreeEnabled) {
                this.gridPanel.reconfigure(kimios.store.getEntitiesStore(), this.gridPanel.getColumnModel());
                this.lockSearch = false;
                this.virtualTreeEnabled = false;
            }
            this.setIconClass('loading');


            this.hideVirtualTree();
            this.hidePagingToolBar();


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
                    scope: this,
                    params: {
                        dmEntityUid: this.uid,
                        dmEntityType: this.type
                    },
                    callback: function (records, options, success) {
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
        }
    },

    loadEntities: function () {
        if (!this.gridPanel.getState().sort) {
            this.gridPanel.getStore().setDefaultSort('creationDate', 'desc');
        } else {
            var sort = this.gridPanel.getState().sort;
            this.gridPanel.getStore().setDefaultSort(sort.field, sort.direction);
        }
        this.gridPanel.getStore().load({
            scope: this,
            params: {
                dmEntityUid: this.uid,
                dmEntityType: this.type
            },
            callback: function (records, options, success) {
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

    refreshLanguage: function () {
        this.breadcrumbToolbar.refreshLanguage();
        this.searchToolbar.refreshLanguage();
        //this.advancedSearchPanel.refreshLanguage();
        this.gridPanel.reconfigure(this.gridPanel.getStore(), this.getColumns());
        this.gridPanel.getView().refresh(true);
        this.contextToolbar.doLayout();
        kimios.explorer.getActivePanel().commentsPanel.refreshLanguage();
        this.doLayout();
    },

    getColumns: function () {

        var renderSymLinkHelper = function (dataToRender, field) {
            if (dataToRender.type == 7 && dataToRender.targetEntity) {
                return dataToRender.targetEntity[field];
            } else {
                return dataToRender[field];
            }
        };
        var cmArray = [
            {
                align: 'center',
                readOnly: true,
                width: 18,
                hidden: false,
                sortable: false,
                hideable: false,
                fixed: true,
                resizable: false,
                menuDisabled: true,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.targetEntity.type, record.data.targetEntity.extension);
                    } else
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
                }
            },
            {
                header: kimios.lang('DocumentName'),
                dataIndex: 'name',
                readOnly: true,
                sortable: true,
                hideable: false,
                menuDisabled: false,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        if (record.data.targetEntity.checkedOut) {
                            return '<span style="color:red;">' + record.data.targetEntity.name + '</span>';
                        } else {
                            return record.data.targetEntity.name;
                        }
                    } else {
                        if (record.get('checkedOut'))
                            val = '<span style="color:red;">' + val + '</span>';
                        return val;
                    }
                }
            },
            {
                align: 'center',
                readOnly: true,
                width: 20,
                hidden: false,
                sortable: false,
                hideable: false,
                fixed: true,
                resizable: false,
                menuDisabled: false,
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if ((record.data.type == 7 && record.data.targetEntity.checkedOut) || record.get('checkedOut')) {
                        metaData.css = 'checked-out';
                    }

                }
            },
            {
                header: kimios.lang('CreationDate'),
                dataIndex: 'creationDate',
                width: 120,
                fixed: true,
                readOnly: true,
                sortable: true,
                resizable: false,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record) {
                    if (record.data.type == 7 && record.data.targetEntity) {
                        return kimios.date(record.data.targetEntity.creationDate);
                    } else
                        return kimios.date(value);
                }
            },
            {
                header: kimios.lang('UpdateDate'),
                dataIndex: 'lastVersionUpdateDate',
                width: 120,
                fixed: true,
                readOnly: true,
                sortable: true,
                resizable: false,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        if (record.data.targetEntity.type <= 2)
                            return 'N/A';
                        else
                            return kimios.date(record.data.targetEntity.lastVersionUpdateDate);
                    }
                    if (record.data.type <= 2)
                        return 'N/A';
                    else
                        return kimios.date(value);
                }
            },
            {
                header: kimios.lang('Author'),
                dataIndex: 'owner',
                width: 80,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7) {
                        return record.data.targetEntity.owner + '@' + record.data.targetEntity.ownerSource;
                    }
                    return val + '@' + record.get('ownerSource');
                }
            },
            {
                header: kimios.lang('LastAuthor'),
                dataIndex: 'lastUpdateAuthor',
                width: 80,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if(record.data.type == 1 || record.data.type == 2) return;
                    if (record.data.type == 7) {
                        return record.data.targetEntity.lastUpdateAuthor + '@' + record.data.targetEntity.lastUpdateAuthorSource;
                    }
                    return val + '@' + record.get('lastUpdateAuthorSource');
                }
            },
            {
                header: kimios.lang('DocumentType'),
                dataIndex: 'documentTypeName',
                width: 50,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    switch (record.get('type')) {
                        case 1:
                            return kimios.lang('Workspace');
                        case 2:
                            return kimios.lang('Folder');
                        case 3:
                            var extension = record.get('extension') == undefined ? '' : record.get('extension').toUpperCase();
                            return (val == undefined || val == '' ? kimios.lang('Document') : val) + ' (' + extension.toUpperCase() + ')';

                        case 7:
                            switch (record.data.targetEntity.type) {
                                case 1:
                                    return kimios.lang('Workspace');
                                case 2:
                                    return kimios.lang('Folder');
                                case 3:
                                    var extension = record.data.targetEntity.extension == undefined ? '' : record.data.targetEntity.extension.toUpperCase();
                                    return (record.data.targetEntity.documentTypeName == undefined || record.data.targetEntity.documentTypeName == '' ? kimios.lang('Document') : record.data.targetEntity.documentTypeName) + ' (' + extension.toUpperCase() + ')';

                            }
                    }
                }
            },
            {
                header: kimios.lang('Size'),
                dataIndex: 'length',
                width: 30,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'right',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if (record.data.type == 7 && record.data.targetEntity.type == 3) {
                        return (record.data.targetEntity.length / 1024).toFixed(2) + ' ' + kimios.lang('Kb');
                    }
                    if (record.data.type == 3)
                        return (val / 1024).toFixed(2) + ' ' + kimios.lang('Kb');

                }
            },
            {
                header: kimios.lang('CustomVersion'),
                dataIndex: 'customVersion',
                width: 20,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'right',
                renderer: function(val, metaData, record, rowIndex, colIndex, store){
                    if (record.data.type == 7) {
                        return record.data.targetEntity.customVersion;
                    } else {
                        return record.data.customVersion;
                    }
                }
            }
        ];

        if (bonitaEnabled) {
            cmArray.push(
                {
                    header: kimios.lang('Workflow'),
                    dataIndex: 'dmEntityAddonData',
                    width: 80,
                    readOnly: true,
                    sortable: true,
                    menuDisabled: false,
                    align: 'left',
                    renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                        var obj;

                        if (record.data.type == 7) {
                            obj = Ext.decode(record.data.targetEntity.dmEntityAddonData);
                        } else
                            obj = Ext.decode(val);
                        var counter = 0;
                        for (var key in  obj.entityAttributes) {
                            if (key.indexOf('BonitaProcessInstance_') != -1) {
                                counter++;
                            }
                        }
                        if (counter > 0)
                            return '<span style="color:red;">' + counter + ' ' + kimios.lang('ProcessInstances') + '</span>';
                    }
                }
            );
        } else {
            cmArray.push({
                header: kimios.lang('WorkflowStatus'),
                dataIndex: 'workflowStatusName',
                width: 50,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (value, metaData, record, rowIndex, colIndex, store) {

                    var dataItem = record.get('type') == 7 ? record.data.targetEntity : record.data;
                    if (record.get('type') == 3 || record.get('type') == 7) {
                        var getStyle = function (bg) {
                            return 'font-weight:bold;display:block;color:white;background-color:' + bg + ';padding-left:2px;margin-right:20px;';
                        };
                        var val = dataItem.workflowStatusName == '' ? '&nbsp;' : dataItem.workflowStatusName;
                        if (dataItem.outOfWorkflow == false) {
                            return '<span style="' + getStyle('tomato') + '">' + val + '</span>';
                        } else if (value != '') {
                            return '<span style="' + getStyle('olive') + '">' + val + '</span>';
                        }
                    }
                }
            });
            cmArray.push({
                header: kimios.lang('Workflow'),
                dataIndex: 'workflowName',
                width: 50,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left'
            });
            cmArray.push({
                header: kimios.lang('ValidatorUser'),
                dataIndex: 'validatorUserName',
                width: 80,
                readOnly: true,
                sortable: true,
                menuDisabled: false,
                align: 'left',
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if(record.data.type == 1 || record.data.type == 2) return;
                    if (record.data.type == 7) {
                        return record.data.targetEntity.validatorUserName + '@' + record.data.targetEntity.validatorUserSource;
                    }
                    if(val && val != '')
                        return val + '@' + record.get('validatorUserSource');
                }
            });
        }

        if (addonColumns) {
            for (var z = 0; z < addonColumns.length; z++) {
                cmArray.push(addonColumns[z]);
            }
        }

        return new Ext.grid.ColumnModel(cmArray);

    }
});


var colModelLoad = false;

var metaValueRenderer = function (metaName, data) {
    for (var e = 0; e < data.length; e++) {
        var el = data[e];
        if (el.meta && el.meta.name == metaName) {
            if (el.value instanceof Array) {
                var str = '';
                for (var i = 0; i < el.value.length; i++) {
                    str += el.value[i] + ',';
                }
                return str.substr(0, str.lastIndexOf(','));
            } else if (el.meta.metaType == 3) {
                return kimios.dateWithoutTime(el.value);
            } else
                return el.value;
        }
    }
    return '';
};

var addonColumns = undefined;
function loadAddonCols(handler) {
    if (clientConfig && clientConfig.defaultdocumenttype && !addonColumns) {
        //load default doctype
        /*
         Synchronous load
         */

        addonColumns = [];
        var dtStore = new DmsJsonStore({
            fields: kimios.record.documentTypeRecord,
            url: 'DmsMeta',
            baseParams: {action: 'types'}
        });
        var doctypeSet = false;
        dtStore.on('load', function (store, recs) {
            for (var u = 0; u < recs.length; u++) {
                if (recs[u].data.name == clientConfig.defaultdocumenttype) {

                    kimios.store.getMetasStore(recs[u].data.uid).on('load', function (store, metasRecords, options) {

                        for (var z = 0; z < metasRecords.length; z++) {
                            var met = metasRecords[z];
                            var createHeaderFunc = function (stumpName) {
                                var hName = stumpName;
                                return {
                                    header: hName,
                                    dataIndex: 'dmEntityAddonData',
                                    width: 50,
                                    readOnly: true,
                                    hidden: true,
                                    hideable: true,
                                    sortable: true,
                                    menuDisabled: false,
                                    align: 'left',
                                    renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                                        var tName = hName;
                                        var dataItem = record.get('type') == 7 ? record.data.targetEntity : record.data;
                                        if (record.get('type') == 3 || record.get('type') == 7) {
                                            if (record.data.metaDatas && record.data.metaDatas.length > 0) {

                                            } else {
                                                var obj = Ext.decode(value);
                                                if (obj && obj.entityMetaValues) {
                                                    var v = metaValueRenderer(tName, obj.entityMetaValues);
                                                    return v;
                                                } else {
                                                    return '';
                                                }
                                            }

                                        }
                                    }
                                };
                            };
                            addonColumns.push(createHeaderFunc(met.data.name));
                        }
                        if (handler) {
                            handler();
                        }
                        doctypeSet = true;
                    });
                    break;
                }
            }
            if (!doctypeSet) {
                if (handler) {
                    handler();
                }
            }
        });
        dtStore.load();
    }
}
