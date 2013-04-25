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
kimios.search.SearchField = Ext.extend(Ext.form.TextField, {
    constructor: function (config) {
        this.isSearchMode = false;
        this.emptyText = kimios.lang('SearchByName');
        this.width = 160;
        this.selectOnFocus = true;
        this.style = 'font-style:italic;font-size: 11px;border: 1px solid #ddd;-webkit-border-radius: 10px;-moz-border-radius: 10px;border-radius: 10px;';
        this.listeners = {
            scope: this,
            specialkey: function (field, e) {
                if (!e || e.getKey() == e.ENTER)
                    this.onTrigger2Click(e);
            }
        };
        kimios.search.SearchField.superclass.constructor.call(this, config);
    },

    refreshLanguage: function () {
        var needClear = this.getValue() == '';
        if (kimios.explorer.getActivePanel().searchToolbar) {
            var sb = kimios.explorer.getActivePanel().searchToolbar.criteriaButton;
            if (sb.isSearchByName() == true)
                this.emptyText = kimios.lang('SearchByName');
            else if (sb.isSearchByText() == true)
                this.emptyText = kimios.lang('SearchByText');
        }
        if (needClear == true) this.clearSearch();
    },

    onTrigger1Click: function (event) {
        this.isSearchMode = false;
        this.setValue('');
        this.focus(false, true);

        var bt = kimios.explorer.getActivePanel().breadcrumbToolbar;
        var ap = kimios.explorer.getActivePanel();

        bt.enable();
        ap.hidePagingToolBar();
        ap.refresh();

    },

    onTrigger2Click: function (event) {
        var value = this.getRawValue();
        var tab = kimios.explorer.getActivePanel();
        if (value == '') {
            this.isSearchMode = false;
            tab.loadEntity();
        } else {
            this.isSearchMode = true;
            var tab = kimios.explorer.getActivePanel();
            tab.search({
                DocumentName: kimios.explorer.getActivePanel().searchToolbar.criteriaButton.isSearchByName() == true ? value : undefined,
                DocumentBody: kimios.explorer.getActivePanel().searchToolbar.criteriaButton.isSearchByText() == true ? value : undefined,
                fromUid: tab.uid,
                fromType: tab.type,
                DocumentPath: tab.breadcrumbToolbar.getPath()
            });
        }
    },

    clearSearch: function () {
        this.isSearchMode = false;
        this.setValue('');
    },

    initComponent: function () {
        kimios.search.SearchField.superclass.initComponent.apply(this, arguments);
    }
});


