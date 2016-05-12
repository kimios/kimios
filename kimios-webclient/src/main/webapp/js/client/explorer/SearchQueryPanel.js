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
        this.id = config.id;
        this.queriesLoadAction = config.queriesLoadAction ? config.queriesLoadAction : 'ListMyQueries';



        this.titleKey = config.titleKey;
        this.title = config.titleKey ? kimios.lang(config.titleKey) : kimios.lang('SearchTab');
        this.iconCls = 'search';
        this.autoScroll = true;
//        this.stripeRows = true;
        this.hideHeaders = true;

//    this.ddGroup = 'firstGridDDGroup';
//    this.enableDragDrop = true;


        var loadAction = this.queriesLoadAction;
        this.store = new DmsJsonStore( {
                                           url: 'Search',
                                           fields: kimios.record.SearchRecord.queryRecord,
                                           baseParams: {
                                               action: loadAction
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
                                                      singleSelect: false
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
        this.setTitle( kimios.lang( this.titleKey ) );
        this.noContentNode.setText( kimios.lang( 'NoBookmark' ) );
        this.doLayout();
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

            var selected = grid.getStore().getAt( rowIndex );

            //reload

            kimios.ajaxRequestWithAnswer('Search', {queryId: selected.data.id, action: 'LoadQuery'}, function(resp){

                selected = Ext.util.JSON.decode(resp.responseText);
                if ( kimios.explorer.getActivePanel() == null || !(kimios.explorer.getActivePanel() instanceof kimios.explorer.DMEntityGridPanel))
                {
                    var tabbed = new kimios.explorer.DMEntityGridPanel( {} );
                    var centerPanel = Ext.getCmp( 'kimios-center-panel' );
                    centerPanel.add( tabbed );
                    centerPanel.setActiveTab( tabbed );
                }

                var virtTree = selected.virtualTree;

                if ( !virtTree )
                {
                    kimios.explorer.getActivePanel().hideVirtualTree();
                    //kimios.explorer.getActivePanel().advancedSearchPanel.loadForm( selected );
                } // auto load


                /*
                 execute saved query
                 */

                var searchStore = kimios.store.getSavedQueryExecStore( {
                    queryId: selected.id
                });
                //TODO: generalize paging Size
                this.pagingSize = 20;
                var dispPanel = kimios.explorer.getActivePanel();
                var gridPanel = dispPanel.gridPanel;
                gridPanel.reconfigure( searchStore, gridPanel.getColumnModel());
                if(virtTree){
                    dispPanel.displayVirtualTree(selected.id, searchStore);
                    dispPanel.displayPagingToolBar( searchStore, true );
                    //init load with paging

                } else{
                    dispPanel.hideVirtualTree();
                    dispPanel.displayPagingToolBar( searchStore, false );
                }
                dispPanel.loadAndExecuteQuery(selected, searchStore);
            })



        }, this );

        this.on( 'rowcontextmenu', function ( grid, rowIndex, e )
        {
            e.preventDefault();
            if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;
            var sm = grid.getSelectionModel();
            if(sm.getSelections().length > 1){
                kimios.ContextMenu.showMultiple( sm.getSelected().data, e, 'searchRequests', sm.getSelections() );
            } else {
                sm.selectRow( rowIndex );
                kimios.ContextMenu.show( sm.getSelected().data, e, 'searchRequests' );
            }

        }, this );

        this.on( 'containercontextmenu', function ( grid, e )
        {
            e.preventDefault();
            kimios.ContextMenu.show( new kimios.DMEntityPojo( {} ), e, 'searchRequestsContainer' );
        }, this );

    }

} );
