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

kimios.explorer.SearchVirtualBreadcrumbToolbar = Ext.extend(Ext.Toolbar, {

    constructor: function (config) {
        this.pathSeparator = '<img src="' + srcContextPath +
                '/images/icons/16x16/select.png" border="0" align="absmiddle" alt="&nbsp;&raquo;&nbsp;"/>';
        kimios.explorer.SearchVirtualBreadcrumbToolbar.superclass.constructor.call(this, config);
        this.virtualTreePanel = config.virtualTreePanel;
    },

    getPath: function () {
        return this.currentPath;
    },

    setPath: function (path) {
        this.currentPath = path;
        var ap = this.virtualTreePanel;
        this.removeAll();
        this.upButton = new Ext.Toolbar.Button({
           disabled: path == undefined,
           tooltip: kimios.lang('Up'),
           iconCls: 'undo',
           handler: function (btn, evt) {
               btn.disable();
               ap.setPath(ap.currentPath.substr(0, ap.lastIndexOf('/')));
           }
        });
        this.refreshButton = new Ext.Toolbar.Button({
            tooltip: kimios.lang('Refresh'),
            iconCls: 'refresh',
            scope: this,
            disabled: true,
            handler: function (btn, evt) {
                btn.disable();
                ap.setPath(ap.currentPath);
            }
        });

        this.add(this.refreshButton);
        this.add(this.upButton);
        this.addSeparator();
        this.add(' ');


        var me = this;
        if (path != undefined) {
            var n = path.substr(1).split('/');
            var url = '';
            for (var i = 0; i < n.length; i++) {
                url += '/' + n[i];
                this.add('/');
                this.add(new Ext.Toolbar.Button({
                    text: n[i],
                    targetUrl: url,
                    handler: function () {
                        if (this.handleMouseEvents == true) {
                            me.back(targetUrl);
                        }
                    }
                }));
            }
        }

        this.doLayout();
    },

    back: function (url) {
        this.virtualTreePanel.setPath(url);
    },

    refreshLanguage: function () {
        if (!this.upButton)
            return;
        this.upButton.setTooltip(kimios.lang('Up'));
        this.refreshButton.setTooltip(kimios.lang('Refresh'));
        this.doLayout();
    }
});
