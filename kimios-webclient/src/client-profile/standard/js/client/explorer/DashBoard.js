/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */



kimios.explorer.DashBoardGridPanel = Ext.extend(Ext.Panel, {
    constructor: function (config) {
        var _this = this;
        this.closable = true;
        this.title = kimios.lang('Loading');
        this.iconCls = 'loading';
        this.border = false;
        this.layout = 'border';

        /* dummy paging init */


        var itemTemplate =  new Ext.XTemplate(
            '<ul>',
            '<tpl for=".">',
            '<li class="phone search-lg">',
            '<strong>{name}</strong>',
            '<span class="search" style="width: 40px; height: 40px; display-block">',
            '</li>',
            '</tpl>',
            '</ul>'
        );

        /*var itemTemplate = new Ext.XTemplate(
            '<tpl for=".">',
            '<div class="thumb-wrap" id="{name}">',
            '<div class="thumb"><img src="{url}" title="{name}"></div>',
            '<span class="x-editable">{shortName}</span></div>',
            '</tpl>',
            '<div class="x-clear"></div>'
        );*/

        var store = new DmsJsonStore({
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
        });

        this.queryStore = store;

        var northContainer = new Ext.Panel({
            region: 'north',
            autoHeight: true,
            items:[
                {
                    xtype: 'fieldset',
                    border: false,
                    defaults: {
                        layout: 'hbox',
                        labelAlign: 'top'
                    },
                    items: [
                        new kimios.search.SearchFieldGeneric({ }),
                        new Ext.form.DateField({
                            id: 'eurosportDateField',
                            fieldLabel: 'Broadcast Date',
                            width: 300,
                            format: kimios.lang('ShortJSDateFormat')
                        })
                    ]
                }
            ]
        })

        var cfg = {
            store: this.queryStore,
            tpl: itemTemplate,
            region: 'center',
            autoHeight: true,
            multiSelect: true,
            overClass: 'x-view-over',
            emptyText: 'No items to display',
            id: 'dashboard',
            itemSelector: 'li.phone',
            overClass   : 'phone-hover',
            singleSelect: true,
            multiSelect : false,
            autoScroll  : true
        };


        var centerContainer = new Ext.DataView(cfg);

        centerContainer.on('dblclick', function(){
            var selNode = this.getSelectedRecords( )[0];
            //launch query
            if (selNode.data.type == 9)
                return false;
            if ( kimios.explorer.getActivePanel() == null || !(kimios.explorer.getActivePanel() instanceof kimios.explorer.DMEntityGridPanel))
            {
                var tabbed = new kimios.explorer.DMEntityGridPanel( {} );
                var centerPanel = Ext.getCmp( 'kimios-center-panel' );
                centerPanel.add( tabbed );
                centerPanel.setActiveTab( tabbed );
            }

            var virtTree = selNode.data.virtualTree;

            if ( !virtTree )
            {
                kimios.explorer.getActivePanel().hideVirtualTree();
                kimios.explorer.getActivePanel().advancedSearchPanel.loadForm( selNode.data );
            } // auto load

            var searchStore = kimios.store.getSavedQueryExecStore( {
                queryId: selNode.get( 'id' )
            });
            //TODO: generalize paging Size
            var pagingSize = 10;
            var dispPanel = kimios.explorer.getActivePanel();
            var gridPanel = dispPanel.gridPanel;
            gridPanel.reconfigure( searchStore, gridPanel.getColumnModel());
            if(virtTree){
                dispPanel.displayVirtualTree(selNode.get('id'), searchStore);
                dispPanel.displayPagingToolBar( searchStore, true );
                //init load with paging
                gridPanel.getStore().load( {
                    scope: this,
                    params: {
                        start: 0,
                        limit: pagingSize
                    }
                } );
            } else{
                dispPanel.hideVirtualTree();
                dispPanel.displayPagingToolBar( searchStore, false );
                gridPanel.getStore().load( {
                    scope: this,
                    params: {
                        start: 0,
                        limit: pagingSize
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
        }, centerContainer)

        this.items = [northContainer, centerContainer]


        kimios.explorer.DashBoardGridPanel.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.explorer.DashBoardGridPanel.superclass.initComponent.apply(this, arguments);
        loadAddonCols();
        //this.refreshMe();
    },

    refreshMe: function () {
        //this.loadEntity();
        var me = this;
        this.queryStore.load({
            callback: function(){
                me.setIconClass('home');
                me.setTitle('Dashboard');
            },
            scope: me

        });
    },


    refreshLanguage: function () {

    }
});


/*kimios.explorer.Viewport.prototype.afterBuild = function () {
    var gridPanel = new kimios.explorer.DashBoardGridPanel({ });
    Ext.getCmp('kimios-center-panel').add(gridPanel);
    Ext.getCmp('kimios-center-panel').setActiveTab(gridPanel);
} */