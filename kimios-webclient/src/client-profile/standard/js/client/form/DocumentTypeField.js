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
kimios.form.DocumentTypeField = Ext.extend(Ext.form.ComboBox, {
    constructor: function (config) {
        this.fieldLabel = config.fieldLabel ? config.fieldLabel : kimios.lang('DocumentType');
        this.editable = false;
        this.triggerAction = 'all';
        this.displayField = 'name';
        this.valueField = 'uid';
        this.hiddenName = config.name;
        if (config.store == null) {
            this.store = new DmsJsonStore({
                fields: kimios.record.documentTypeRecord,
                url: 'DmsMeta',
                baseParams: {action: 'types'}
            });
        }
        kimios.form.DocumentTypeField.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.form.DocumentTypeField.superclass.initComponent.apply(this, arguments);

        this.store.on('beforeload', function (store, options) {
            store.removeAll();
        }, this);

        this.store.on('load', function (store, records, options) {
            store.insert(0, new store.recordType({
                uid: -1,
                name: kimios.lang('NoDocumentType')
            }));
        }, this);
    }
});
