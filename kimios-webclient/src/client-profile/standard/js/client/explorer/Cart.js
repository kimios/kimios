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

kimios.explorer.Cart = Ext.extend(Ext.Panel, {
    refreshCartCounter: function (store) {
        var selected = store.getRange();
        if (selected && selected.length > 0) {
            Ext.getCmp('kimios-cart-button').setText(kimios.lang('Cart') + '&nbsp;(' + selected.length + ')');
            this.setTitle(kimios.lang('Cart') + '&nbsp;(' + selected.length + ')');
            Ext.getCmp('kimios-cart-compress-button').setDisabled(false);
            Ext.getCmp('kimios-cart-clear-button').setDisabled(false);
        } else {
            Ext.getCmp('kimios-cart-button').setText(kimios.lang('Cart'));
            this.setTitle(kimios.lang('Cart'));
            Ext.getCmp('kimios-cart-compress-button').setDisabled(true);
            Ext.getCmp('kimios-cart-clear-button').setDisabled(true);
        }
    },
    constructor: function (config) {

        this.id = 'kimios-cart';
        this.title = kimios.lang('Cart');
        this.iconCls = 'cart';
        this.width = 300;
        this.height = 400;

        this.listeners = {
            minimize: function () {
                Ext.getCmp('kimios-cart-button').toggle(false);
                return false;
            }
        }

        var _this = this;


        this.cartGrid = new Ext.grid.GridPanel({
            buttonAlign: 'left',
            fbar: [
                {
                    id: 'kimios-cart-clear-button',
                    tooltip: kimios.lang('ClearCart'),
                    iconCls: 'trash',
                    disabled: true,
                    scope: this,
                    handler: function () {
                        _this.cartGrid.getStore().removeAll();
                    }
                },
                '->',
                {
                    id: 'kimios-cart-compress-button',
                    text: 'ZIP',
                    iconCls: 'compress',
                    scope: this,
                    disabled: true,
                    handler: function () {
                        var records = _this.cartGrid.getStore().getRange();
                        if (!records && records.length == 0)
                            return false;
                        var link = srcContextPath + '/Converter?sessionId=' + sessionUid;
                        for (var i = 0; i < records.length; ++i)
                            link += '&documentId=' + records[i].data.uid;
                        link += '&converterImpl=org.kimios.kernel.converter.impl.FileToZip';
                        window.location.href = link;
                    }
                }
            ],
            store: new Ext.data.Store({
                id: 'kimios-cart-store',
                reader: new Ext.data.ArrayReader(
                    {
                        idIndex: 0
                    },
                    kimios.record.dmEntityRecord
                ),
                listeners: {
                    add: function (store) {
                        _this.refreshCartCounter(store);
                    },
                    clear: function (store) {
                        _this.refreshCartCounter(store);
                    },
                    remove: function (store) {
                        _this.refreshCartCounter(store);
                    }
                }
            }),
            colModel: new Ext.grid.ColumnModel([
                {
                    align: 'center',
                    readOnly: true,
                    width: 20,
                    sortable: false,
                    hideable: false,
                    fixed: true,
                    resizable: false,
                    menuDisabled: true,
                    renderer: function (val, metaData, record, rowIndex, colIndex, store) {
                        if (record.data.type == 9)
                            metaData.css = '';
                        else
                            metaData.css = kimios.util.IconHelper.getIconClass(record.data.type, record.data.extension);
                    }
                },
                {
                    dataIndex: 'name',
                    readOnly: true,
                    sortable: true,
                    hideable: false,
                    menuDisabled: true
                }
            ]),
            viewConfig: {
                forceFit: true,
                scrollOffset: 0
            },
            sm: new Ext.grid.RowSelectionModel({singleSelect: true}),
            border: false,
            hideHeaders: true
        });

        this.layout = 'fit';
        this.items = [this.cartGrid];


        kimios.explorer.Cart.superclass.constructor.call(this, config);


        this.cartGrid.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            if (grid.getStore().getAt(rowIndex).data.type == 9)
                return false;
            var sm = this.getSelectionModel();
            sm.selectRow(rowIndex);
            var selectedRecord = sm.getSelected();
            kimios.ContextMenu.show(new kimios.DMEntityPojo(selectedRecord.data), e, 'cart');
        });

        this.cartGrid.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'cartContainer');
        }, this);

    },

    refresh: function () {
        this.refreshLanguage();
        Ext.getCmp('kimios-cart').doLayout();
    },

    refreshLanguage: function () {
        this.refreshCartCounter(this.cartGrid.getStore());
        Ext.getCmp('kimios-cart-clear-button').setTooltip(kimios.lang('ClearCart'));
    }


});
