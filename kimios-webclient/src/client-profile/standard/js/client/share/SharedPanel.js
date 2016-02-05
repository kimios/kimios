/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

kimios.explorer.share = {}
kimios.explorer.share.SharePanel = Ext.extend( Ext.grid.GridPanel, {
    constructor: function ( config )
    {
        this.id = config.id;

        this.titleKey = config.titleKey;
        this.title = config.titleKey ? kimios.lang(config.titleKey) : kimios.lang('ShareTab');
        //this.iconCls = 'search';
        this.autoScroll = true;
        this.hideHeaders = true;

        this.store = new DmsJsonStore( {
           url: 'Share',
           fields: kimios.record.sharedEntityRecord,
           baseParams: {
               action: 'sharedWithMe'
           },
           autoLoad: true,
           sortInfo: {
               field: 'name',
               direction: 'ASC'
           }
        } );

        this.noContentNode = new Ext.tree.TreeNode( {
                                                        text: kimios.lang( 'NoShare' ),
                                                        disabled: true
                                                    } );


        this.cm = new Ext.grid.ColumnModel([
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
                renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                    if(console){
                        console.log(record.data)
                    }
                    if (record.data.type == 9)
                        metaData.css = '';
                    else
                        metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
                }
            },
            {
                sortable: true,
                menuDisabled: true,
                align: 'left',
                dataIndex: 'name',
                renderer: function (value, meta, record) {
                    if (record.data.type != 9) {
                        var html = value + '<br/>';
                        html += '<span style="font-size:10px;">' + kimios.lang('SharedBy') + ': ' + record.data.creatorId + '@' + record.data.creatorSource + '</span><br/>';
                        html += '<span style="font-size:10px;color:gray;">' + kimios.lang('ShareExpirationDate') + ' - ' + kimios.date(record.data.expirationDate) + '</span><br/>';
                        return html;
                    } else
                        return value;
                }
            }
        ]);

        this.sm = new Ext.grid.RowSelectionModel( {
                                                      singleSelect: true
                                                  } );

        this.viewConfig = {
            forceFit: true,
            scrollOffset: 0
        };
        kimios.explorer.share.SharePanel.superclass.constructor.call( this, config );
    },
    refresh: function ()
    {
        this.setIconClass( 'loading' );
        this.store.reload( {
                               scope: this,
                               callback: function ( records, options, success )
                               {
                                    this.setIconClass(null);
                                    if (!records || records.length == 0) {
                                        this.store.insert(0, new Ext.data.Record({
                                        name: kimios.lang('NoShare'),
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
        this.noContentNode.setText( kimios.lang( 'NoShare' ) );
        this.doLayout();
    },


    initComponent: function ()
    {
        kimios.explorer.share.SharePanel.superclass.initComponent.apply( this, arguments );

        this.on( 'activate', function ()
        {
            this.refresh();
        }, this );

        this.on( 'rowdblclick', function ( grid, rowIndex, ev )
        {
            if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;

            var selected = grid.getStore().getAt( rowIndex );
        }, this );

        this.on( 'rowcontextmenu', function ( grid, rowIndex, e )
        {
            e.preventDefault();
            if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;
            var sm = grid.getSelectionModel();
            if(sm.getSelections().length > 1){
                kimios.ContextMenu.showMultiple( sm.getSelected().data, e, 'shares', sm.getSelections() );
            } else {
                sm.selectRow( rowIndex );
                kimios.ContextMenu.show( sm.getSelected().data, e, 'shares' );
            }

        }, this );

        this.on( 'containercontextmenu', function ( grid, e )
        {
            e.preventDefault();
            kimios.ContextMenu.show( new kimios.DMEntityPojo( {} ), e, 'sharesContainer' );
        }, this );

    }

} );
