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

kimios.form.AuthenticationSourceField = Ext.extend(Ext.form.ComboBox, {

    constructor:function (config)
    {
        this.id = 'combo-domain';
        this.mode = 'local';
        this.typeAhead = true;
        this.triggerAction = 'all';
        this.width = 140;
        this.editable = false;
        this.allowBlank = false;
        this.valueField = 'name';
        kimios.form.AuthenticationSourceField.superclass.constructor.call(this, config);
    },

    initComponent:function ()
    {
        kimios.form.AuthenticationSourceField.superclass.initComponent.apply(this, arguments);
        var sourcesStore = new DmsJsonStore({
            url:'Security',
            baseParams:{
                action:'authenticationSources'
            },
            root:'list',
            fields:['className', 'name']
        });
        sourcesStore.load();
        this.store = sourcesStore;
        this.displayField = 'name';
        this.store.on('load', function (store, rec)
            {
                if (rec.length > 0) {
                    this.setValue(rec[0].data.name);
                    this.fireEvent('select', this, rec[0]);
                }
            }
            , this);
    },
    init:function (sourceLocked)
    {
        if (sourceLocked) {
            this.setValue(sourceLocked);
        }
    },
    loadMe:function ()
    {
        this.store.load();
    }
});


