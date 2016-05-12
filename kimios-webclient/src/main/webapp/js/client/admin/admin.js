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
var Admin = {

    getWindow: function () {
        var window = new Ext.Window({
            modal: true,
            title: kimios.lang('Administration'),
            iconCls: 'owner',
            closable: true,
            maximizable: true,
            width: 800,
            height: 500,
            plain: true,
            layout: 'fit'
        });

        var tabs = new Ext.TabPanel({
            activeTab: 0,
            border: false,
            enableTabScroll: true
        });

        tabs.add(Admin.Domains.getPanel());
        tabs.add(Admin.Roles.getPanel());
        tabs.add(Admin.Tasks.getPanel());

        window.add(tabs);
        return window;
    },

    getPanel: function (title, iconCls, treePanel, contextPanel) {
        return new Ext.Panel({
            title: title,
            iconCls: iconCls,
            layout: 'border',
            items: [treePanel, contextPanel],
            border: false
        });
    }
};
