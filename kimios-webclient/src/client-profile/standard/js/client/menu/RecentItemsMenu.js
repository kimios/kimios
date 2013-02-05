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
kimios.menu.RecentItemsMenu = Ext.extend(kimios.menu.BaseMenu,{
  constructor : function(config) {
    this.id = 'kimios-recent-items';
    this.text = kimios.lang('RecentItems');
    this.iconCls = 'value';
    this.context = 'recentItems';
    this.noContentText = kimios.lang('NoRecentItem');
    this.store = new DmsJsonStore( {
      url : 'Version',
      fields : kimios.record.dmEntityRecord,
      baseParams : {
        action : 'recents'
      }
    });
    kimios.menu.RecentItemsMenu.superclass.constructor.call(this, config);
  },
  
  refresh : function(){
    this.setIconClass('loading');
    this.store.reload();
  },
  
  refreshLanguage : function(){
    this.setText(kimios.lang('RecentItems'));
    this.noContentText = kimios.lang('NoRecentItem');
  }
});
