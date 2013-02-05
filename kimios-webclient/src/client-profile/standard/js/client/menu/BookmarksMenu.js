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
kimios.menu.BookmarksMenu = Ext.extend(kimios.menu.BaseMenu,{
  constructor : function(config) {
    this.id = 'kimios-bookmarks';
    this.text = kimios.lang('BookmarksExplorer');
    this.iconCls = 'qbookmark-cls';
    this.context = 'bookmarks';
    this.noContentText = kimios.lang('NoBookmark');
    this.store = new DmsJsonStore( {
      url : 'Version',
      fields : kimios.record.dmEntityRecord,
      baseParams : {
        action : 'bookmarks'
      },
      sortInfo: {
        field: 'type',
        direction: 'ASC'
      }
    });
    this.actionText = kimios.lang('AddToBookmark');
    this.actionIconCls = 'qaction-bookmarks';
    this.actionHandler = function(){
      var p = kimios.explorer.getActivePanel();
      if (p.uid != undefined)
        kimios.request.addToBookmarks(p.uid, p.type);
    };
    
    kimios.menu.BookmarksMenu.superclass.constructor.call(this, config);
  },
  
  refresh : function(){
    this.setIconClass('loading');
    this.store.reload();
  },
  
  refreshLanguage : function(){
    this.setText(kimios.lang('BookmarksExplorer'));
    this.noContentText = kimios.lang('NoBookmark');
    this.actionText = kimios.lang('AddToBookmark');
  }
});
