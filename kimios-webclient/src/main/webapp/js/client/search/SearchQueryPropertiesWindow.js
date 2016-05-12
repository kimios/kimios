/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

kimios.search.SearchQueryPropertiesWindow = Ext.extend(Ext.Window, {
    constructor: function (config) {
        this.closable = true;
        this.border = true;
        this.width = 800,
            this.height = 400,
            this.layout = 'fit';
        this.maximizable = true;
        this.modal = true;
        kimios.search.SearchQueryPropertiesWindow.superclass.constructor.call(this, config);
    },
    initComponent: function () {
        kimios.search.SearchQueryPropertiesWindow.superclass.initComponent.apply(this, arguments);
    }
});
