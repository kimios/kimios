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
kimios.search.SearchToolbar = Ext.extend(Ext.Toolbar, {
  constructor : function(config) {
    this.width = 276;
    this.criteriaButton = new kimios.search.SearchButton();
    this.searchField = new kimios.search.SearchField();
    this.items = [ this.criteriaButton, this.searchField];
    kimios.search.SearchToolbar.superclass.constructor.call(this, config);
  },
  initComponent : function(){
    kimios.search.SearchToolbar.superclass.initComponent.apply(this, arguments);
    this.doLayout();
  },
  refreshLanguage : function(){
    this.criteriaButton.refreshLanguage();
    this.searchField.refreshLanguage();
    this.doLayout();
  }
});
