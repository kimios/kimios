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
Ext.onReady(function () {
    var defaultLang = kimios.getLanguage();
    Ext.QuickTips.init();
    kimios.store.getLangStore(defaultLang).load({
        callback: function (records, options, success) {
            new kimios.explorer.Viewport({
                checkSession: 120, // in seconds
                i18n: new kimios.i18n.Internationalization({
                    lang: defaultLang,
                    records: records
                })
            });
        }
    });
});

