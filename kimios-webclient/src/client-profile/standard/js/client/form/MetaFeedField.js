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

kimios.form.MetaFeedField = Ext.extend(Ext.form.ComboBox,{
   
    constructor : function(config){
        this.editable = false;
        this.valueField = 'value';
        this.displayField = 'value';
        this.triggerAction = 'all';
        if (config.fieldLabel != null)
          this.fieldLabel = kimios.lang('MetaFeeds');
        this.store = kimios.store.StudioStore.getMetaFeedValuesStore(config.metaFeedUid, false);
        kimios.form.MetaFeedField.superclass.constructor.call(this, config);
    }
});



kimios.form.MetaFeedMultiField = Ext.extend(Ext.ux.form.LovCombo, {
    constructor: function(config){
        this.editable = false;
        this.valueField = 'value';
        this.displayField = 'value';
        this.triggerAction = 'all';
        this.maxHeight = 200
        config.multiSelect = true;
        config.hideOnSelect = false;
        config.queryMode = 'local';
        config.triggerAction = 'all';

        config.id = 'metalistval-' + config.name;

        this.passedValue = config.passedValue;

        if (config.fieldLabel != null)
            this.fieldLabel = kimios.lang('MetaFeeds');
        this.store = kimios.store.StudioStore.getMetaFeedValuesStore(config.metaFeedUid, false);

        kimios.form.MetaFeedMultiField.superclass.constructor.call(this, config);
    }
    ,refreshValue: function(pValue){
        this.passedValue = pValue;

        if(typeof pValue == 'string'){
            if(pValue.indexOf('[') == 0 && pValue.lastIndexOf(']') == (pValue.length -1)){
                //eval array
                this.passedValue = eval('(' + pValue + ')');
            } else {
                this.passedValue = eval('([' + pValue + '])')
            }
        }

        console.log(this.passedValue);
        if(this.passedValue){
            this.setValue(this.passedValue);
            var me = this;
            this.store.on('load', function(rec){
                rec.each(function(it){
                    var a = pValue.split(',');
                    for(var e in a){
                        if(a[e] == it.data.value){
                            it.set('checked', true);
                        }
                    }
                });

                me.setValue(me.passedValue);
            })

            this.store.load();


        }
    }
    ,initComponent:function() {
        this.refreshValue(this.passedValue);
        kimios.form.MetaFeedMultiField.superclass.initComponent.apply(this, arguments);
    }
})


