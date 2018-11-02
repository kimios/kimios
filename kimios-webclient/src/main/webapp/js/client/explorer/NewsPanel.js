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
kimios.explorer.NewsPanel = Ext.extend(Ext.Panel, {

    constructor: function (config) {
        this.closable = true;
        var newsUrl = 'http://newsreader.kimios.com/cxf/rest/article/getLastArticle';
        this.title = kimios.lang('Loading');
        this.iconCls = 'loading';
        this.border = false;
        this.layout = 'fit';
        this.title = kimios.lang('News');
        this.layout = {align: 'center', type: 'vbox'};
        this.flex = 1;
        this.html = '<iframe id="reportframe" border="0" width="100%" height="100%" ' +
            'frameborder="0" marginheight="12" marginwidth="16" scrolling="auto" ' +
            'src="' + newsUrl + '"></iframe>';
        this.listeners = {
            beforeclose: function (panel) {
                var centerPanel = Ext.getCmp('kimios-center-panel');
                if (centerPanel.items.length <= 1)
                    return false;
            }
        };
        kimios.explorer.NewsPanel.superclass.constructor.call(this, config);
        this.setTitle(kimios.lang('News'));
        this.setIconClass('news');
    },

    refreshLanguage: function () {
        this.setTitle(kimios.lang('News'));
        this.doLayout();
    }
});
