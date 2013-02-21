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
kimios.search.SearchButton = Ext.extend(Ext.Toolbar.Button, {
    constructor: function (config) {
        this.autoWidth = true;
        this.text = kimios.lang('SearchOptions');
        this.menu = new Ext.menu.Menu({
            showSeparator: false,
            enableScrolling: false
        });
        kimios.search.SearchButton.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.search.SearchButton.superclass.initComponent.apply(this, arguments);
        this.nameSearchItem = new Ext.menu.CheckItem({
            text: kimios.lang('SearchByName'),
            checked: true,
            group: 'criteria'
        });
        this.textSearchItem = new Ext.menu.CheckItem({
            text: kimios.lang('SearchByText'),
            checked: false,
            group: 'criteria'
        });
//        this.advancedSearchItem = new Ext.menu.CheckItem({
//            text: kimios.lang('AdvancedSearch'),
//            checked: false,
//            group: 'criteria'
//        });
        this.menu.add(this.nameSearchItem);
        this.menu.add(this.textSearchItem);
//        this.menu.addSeparator();
//        this.menu.add(this.advancedSearchItem);

        var handle = function (item, changed) {
            if (changed == true) {
                var tab = kimios.explorer.getActivePanel();
                var sf = kimios.explorer.getActivePanel().searchToolbar.searchField;
                var needClear = sf.getValue() == '';
                sf.emptyText = item.text;
                var sp = kimios.explorer.getActivePanel().advancedSearchPanel;
                if (needClear == true) {
                    sf.clearSearch();
                }
//                else if (this.isAdvancedSearch() == false) {
//                    tab.search({
//                        name: this.isSearchByName() == true ? sf.getValue() : undefined,
//                        text: this.isSearchByText() == true ? sf.getValue() : undefined,
//                        fromUid: tab.uid,
//                        fromType: tab.type
//                    });
//                }
//                if (this.isAdvancedSearch() == true) {
//                    sf.clearSearch();
//                    sp.showPanel();
//                }
//                else {
                    sp.hidePanel();
//                }
            }
            kimios.explorer.getActivePanel().doLayout();
        };
        this.nameSearchItem.on('checkchange', handle, this);
        this.textSearchItem.on('checkchange', handle, this);
//        this.advancedSearchItem.on('checkchange', handle, this);
    },

    refreshLanguage: function () {
        this.setText(kimios.lang('SearchOptions'));
        this.nameSearchItem.setText(kimios.lang('SearchByName'));
        this.textSearchItem.setText(kimios.lang('SearchByText'));
//        this.advancedSearchItem.setText(kimios.lang('AdvancedSearch'));
    },

    isSearchByName: function () {
        return this.nameSearchItem.checked;
    },

    isSearchByText: function () {
        return this.textSearchItem.checked;
    }
//    ,
//
//    isAdvancedSearch: function () {
//        return this.advancedSearchItem.checked;
//    }
});

