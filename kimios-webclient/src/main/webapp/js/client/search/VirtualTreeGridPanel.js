kimios.search.VirtualTreeGridPanel = Ext.extend(Ext.grid.GridPanel, {
    constructor: function (config) {
        this.queryId = config.queryId;
        this.baseSearchStore = config.searchStore;
        this.queryId = config.queryId;
        this.currentPath = config.currentPath;
        this.prettyPath = config.prettyPath;
        this.breadcrumbToolbar = config.breadcrumbToolbar;
        config = {

            border: false,
            stripeRows: true,
            margins: '-1 -1 -1 -1',
            store: kimios.store.getVirtualEntityStore({
                queryId: config.queryId
            }),
            columnLines: false,
            enableDragDrop: true,
            enableDD: true,
            ddGroup: 'grid2tree',
            ddScroll: true,
            cm: new Ext.grid.ColumnModel( [
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
                    header: 'Count',
                    dataIndex: 'virtualFolderCount',
                    readOnly: true,
                    sortable: true,
                    hideable: false,
                    menuDisabled: true
                }

            ]),
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
                    grid.setPath(grid.currentPath + rec.data.virtualPath, (grid.prettyPath ? grid.prettyPath : '') + '/' + rec.data.name);
                }
            }
        }


        kimios.search.VirtualTreeGridPanel.superclass.constructor.call(this, config);

        this.addEvents('searchloaded');
    },
    loadResults: function(objectCall){

        var cbFunction = this.loadResultsCallBack.createDelegate(this);
        if(!objectCall)
            objectCall = {
                queryId: this.queryId
            }
        kimios.store.getVirtualTreeCaller(
            objectCall,
            this.getStore(),
            this.baseSearchStore,
            cbFunction
        )
    },
    loadResultsCallBack: function(resp){
        this.fireEvent('searchloaded', resp.total);
    },
    setPath: function(virtualPath, prettyPath){
        this.currentPath = virtualPath ? virtualPath : '';
        this.prettyPath =  prettyPath
        var objectCall = {
            queryId: this.queryId,
            virtualPath: virtualPath
        };
        if(virtualPath == '/' || virtualPath == '' || !virtualPath){
            delete objectCall.virtualPath;
            prettyPath = '';
        }
        this.loadResults(objectCall);
        this.breadcrumbToolbar.setPath(virtualPath, prettyPath);
        this.breadcrumbToolbar.refreshButton.enable();
    }
});