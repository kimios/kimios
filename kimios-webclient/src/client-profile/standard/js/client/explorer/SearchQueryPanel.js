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

kimios.explorer.SearchQueryPanel = Ext.extend( Ext.grid.GridPanel, {
    constructor: function ( config )
    {
        this.id = 'kimios-queries-panel';
        this.title = kimios.lang('SearchTab');
        this.iconCls = 'search';
        this.autoScroll = true;
//        this.stripeRows = true;
        this.hideHeaders = true;

//    this.ddGroup = 'firstGridDDGroup';
//    this.enableDragDrop = true;

        this.store = new DmsJsonStore( {
                                           url: 'Search',
                                           fields: kimios.record.SearchRecord.queryRecord,
                                           baseParams: {
                                               action: 'ListQueries'
                                           },
                                           autoLoad: false,
                                           sortInfo: {
                                               field: 'name',
                                               direction: 'ASC'
                                           }
                                       } );

        this.noContentNode = new Ext.tree.TreeNode( {
                                                        text: kimios.lang( 'NoBookmark' ),
                                                        iconCls: 'search',
                                                        disabled: true
                                                    } );

        this.cm = new Ext.grid.ColumnModel( [
                                                {
                                                    align: 'center',
                                                    readOnly: true,
                                                    width: 20,
                                                    hidden: false,
                                                    sortable: false,
                                                    hideable: false,
                                                    fixed: true,
                                                    resizable: false,
                                                    menuDisabled: true,
                                                    renderer: function ( val, metaData, record, rowIndex, colIndex,
                                                                         store )
                                                    {
                                                        if (record.data.type == 9)
                                                        metaData.css = '';
                                                        else
                                                        metaData.css = 'search';
                                                    }
                                                },
                                                {
                                                    dataIndex: 'name',
                                                    readOnly: true,
                                                    sortable: true,
                                                    hideable: false,
                                                    menuDisabled: true
                                                }
                                            ] );


        this.sm = new Ext.grid.RowSelectionModel( {
                                                      singleSelect: true
                                                  } );

        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };
        kimios.explorer.SearchQueryPanel.superclass.constructor.call( this, config );
    },
    refresh: function ()
    {
        this.setIconClass( 'loading' );
        this.store.reload( {
                               scope: this,
                               callback: function ( records, options, success )
                               {
                                   this.setIconClass( 'search' );
                                    if (!records || records.length == 0) {
                                    this.store.insert(0, new Ext.data.Record({
                                    name: kimios.lang('NoSearchRequest'),
                                    type: 9,
                                    extension: null
                                    }));
                                    }
                               }
                           } );
    },

    refreshLanguage: function ()
    {
        this.setTitle( kimios.lang( 'SearchTab' ) );
        this.noContentNode.setText( kimios.lang( 'NoBookmark' ) );
        this.doLayout();
    },

    virtualTreeColumnModel: function ()
    {
        return new Ext.grid.ColumnModel( [
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
                     renderer: function ( val, metaData, record, rowIndex, colIndex, store )
                     {
                         metaData.css =
                                 kimios.util.IconHelper.getIconClass( record.data.type,
                                                                      record.data.extension );

                     }
                 },
                 {
                     header: kimios.lang( 'DocumentName' ),
                     dataIndex: 'name',
                     readOnly: true,
                     sortable: true,
                     hideable: false,
                     menuDisabled: true
                 },{
                header: 'Path',
                dataIndex: 'virtualPath',
                readOnly: true,
                sortable: true,
                hideable: false,
                menuDisabled: true
                },{
                     header: 'Count',
                     dataIndex: 'virtualFolderCount',
                     readOnly: true,
                     sortable: true,
                     hideable: false,
                     menuDisabled: true
                 }

                                         ]);

    },

    initComponent: function ()
    {
        kimios.explorer.SearchQueryPanel.superclass.initComponent.apply( this, arguments );

        this.on( 'activate', function ()
        {
            this.refresh();
        }, this );

        this.on( 'rowdblclick', function ( grid, rowIndex, ev )
        {
             if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;
/*<<<<<<< HEAD
            var selected = grid.getStore().getAt(rowIndex);

            var tabbed = new kimios.explorer.DMEntityGridPanel({});
            var centerPanel = Ext.getCmp('kimios-center-panel');
            centerPanel.add(tabbed);
            centerPanel.setActiveTab(tabbed);

            kimios.explorer.getActivePanel().advancedSearchPanel.loadForm(selected.data); // auto load
=======*/
            var selected = grid.getStore().getAt( rowIndex );
            if ( kimios.explorer.getActivePanel() == null )
            {
                var tabbed = new kimios.explorer.DMEntityGridPanel( {} );
                var centerPanel = Ext.getCmp( 'kimios-center-panel' );
                centerPanel.add( tabbed );
                centerPanel.setActiveTab( tabbed );
            }

            var virtTree = selected.data.virtualTree;

            if ( !virtTree )
            {
                kimios.explorer.getActivePanel().advancedSearchPanel.loadForm( selected.data );
            } // auto load


            /*
             execute saved query
             */
            var searchStore = kimios.store.getSavedQueryExecStore( {
                   queryId: selected.get( 'id' )
               } );



            //TODO: generalize paging Size

            var selectedQueryId =  selected.get( 'id' );
            this.pagingSize = 10;
            var dispPanel = kimios.explorer.getActivePanel();
            var gridPanel = dispPanel.gridPanel;
            gridPanel.reconfigure( searchStore, gridPanel.getColumnModel());
            if(virtTree){
                var virTreeStore = kimios.store.getVirtualEntityStore({
                  queryId: selected.get( 'id' )
                });
                this.virtualTreeGridPanel = new Ext.grid.GridPanel({
                    border: false,
                    stripeRows: true,
                    margins: '-1 -1 -1 -1',
                    store: virTreeStore,
                    columnLines: false,
                    enableDragDrop: true,
                    enableDD: true,
                    ddGroup: 'grid2tree',
                    ddScroll: true,
                    cm: this.virtualTreeColumnModel(),
                    sm: new Ext.grid.RowSelectionModel({
                                                           singleSelect: false
                                                       }),
                    viewConfig: {
                        forceFit: true,
                        scrollOffset: 0
                    },
                    width: 300,
                    listeners: {
                        "rowdblclick": function(grid, rowIndex, ev){
                            /*
                                Replay query with selected path
                             */
                            var rec = grid.getStore().getAt(rowIndex);
                            grid.setPath(grid.currentPath + rec.data.virtualPath);
                        }
                    }
                });


                var virtualGridPanel = this.virtualTreeGridPanel;
                this.virtualTreeGridPanel.setPath = function(virtualPath){
                    virtualGridPanel.currentPath = virtualPath;
                    kimios.store.getVirtualTreeCaller(
                            {
                                queryId: selectedQueryId,
                                virtualPath: virtualGridPanel.currentPath
                            },
                            virTreeStore,
                            searchStore
                    );
                };
                this.virtualTreeGridPanel.currentPath = '';
                dispPanel.breadcrumbToolbar.virtualMode = true;
                dispPanel.displayVirtualTree(this.virtualTreeGridPanel);
                dispPanel.displayPagingToolBar( gridPanel.getStore(), true );
                kimios.store.getVirtualTreeCaller(
                        {
                            queryId: selectedQueryId
                        },
                        this.virtualTreeGridPanel.getStore(),
                        gridPanel.getStore()
                );

            } else{
                dispPanel.displayPagingToolBar( gridPanel.getStore() );

                gridPanel.getStore().load( {
                   scope: this,
                   params: {
                       start: 0,
                       limit: this.pagingSize
                   },
                   callback: function ( records, options, success )
                   {
                       dispPanel.lockSearch = true;
                       dispPanel.setTitle( kimios.lang( 'DocumentsFound' ) + ' ('
                                                   + searchStore.getTotalCount() + ')' );
                       dispPanel.setIconClass( 'view' );
                   }
               } );
            }

        }, this );

        this.on( 'rowcontextmenu', function ( grid, rowIndex, e )
        {
            e.preventDefault();
            if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;
            var sm = grid.getSelectionModel();
            sm.selectRow( rowIndex );
            kimios.ContextMenu.show( sm.getSelected().data, e, 'searchRequests' );
        }, this );

        this.on( 'containercontextmenu', function ( grid, e )
        {
            e.preventDefault();
            kimios.ContextMenu.show( new kimios.DMEntityPojo( {} ), e, 'searchRequestsContainer' );
        }, this );

    }

} );
