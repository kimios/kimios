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

kimios.form.DMEntityField = Ext.extend(Ext.form.TwinTriggerField, {

    constructor: function (config) {
//        this.editable = (config.editable ? config.editable : false);
        this.fieldLabel = kimios.lang('Entity');
        this.hiddenUid = -1;
        this.hiddenType = -1;
        this.hideTrigger1 = true;
        this.hideTrigger2 = false;
        this.trigger1Class = 'x-form-clear-trigger';
        this.trigger2Class = 'x-form-trigger';
        this.triggerAction = 'all';

        this.listeners = {
            scope: this,
            valid: function (field, event) {
                var value = this.getRawValue();
                if (value != '') {
                    this.triggers[0].show();
                } else {
                    this.triggers[0].hide();
                }
            }
        };

        kimios.form.DMEntityField.superclass.constructor.call(this, config);
    },

    validateValue: function (value) {
        if (!kimios.form.DMEntityField.superclass.validateValue.call(this, value)) {
            return false;
        }
        if (value.length < 1) {
            return true;
        }
        return true;
    },

    validateBlur: function () {
        return !this.menu || !this.menu.isVisible();
    },

    getValue: function () {
        return kimios.form.DMEntityField.superclass.getValue.call(this);
    },

    setValue: function (value) {
        kimios.form.DMEntityField.superclass.setValue.call(this, value);
    },

    menuListeners: {
        select: function (m, d) {
            this.setValue(d);
        },
        show: function () {
            this.onFocus();
        }
    },

    onTriggerClick: function () {
        this.onTrigger2Click();
    },

    onTrigger1Click: function (event) {
        this.hiddenUid = -1;
        this.hiddenType = -1;
        this.setValue('');
    },

    onTrigger2Click: function () {
        if (this.disabled) {
            return;
        }

        if (this.picker == null) {
            this.picker = new kimios.picker.DMEntityPicker({
                title: this.fieldLabel,
                iconCls: 'add',
                withDoc: true
            });
        }

        this.picker.on('entitySelected', function (node) {
            this.hiddenUid = node.attributes.dmEntityUid;
            this.hiddenType = node.attributes.type;
            this.setValue(node.getPath('text').substr(1));
        }, this);

        this.picker.show();
    },

    beforeBlur: function () {
        var v = this.getRawValue();
        if (v) {
            this.setValue(v);
        }
    }
});
