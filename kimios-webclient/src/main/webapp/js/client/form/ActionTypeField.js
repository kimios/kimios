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

kimios.form.ActionTypeField = Ext.extend(Ext.form.ComboBox,{

    constructor : function(config){
        config.typeAhead = true;
        config.triggerAction = 'all';
        config.emptyText = 'Action Type select';
        config.readOnly = true;
        config.fieldLabel = 'Action Type';
        config.width = 140;
        config.allowBlank = false;
        config.valueField = 'name';
        kimios.form.ActionTypeField.superclass.constructor.call(this, config);
    },
    initComponent: function(){
        kimios.form.ActionTypeField.superclass.initComponent.apply(this, arguments);
        var actionTypeStore = new Ext.data.ArrayStore({
            fields: ['name', 'value']
            
        });
        actionTypeStore.loadData([
            ['Create', 0],
            ['Read', 1],
            ['Update', 2]
            ]);
        this.store = actionTypeStore;
        this.displayField = 'name';
    //        this.store.on('load', function(store, rec){
    //            if(rec.length > 0){
    //                this.setValue(rec[0].data.name);
    //                this.fireEvent('select', this, rec[0]);
    //            }
    //        }
    //        , this);
    }
//    ,
//    init: function(sourceLocked){
//        if(sourceLocked){
//            this.setValue(sourceLocked);
//        }
//    },
//    loadMe : function(){
//        this.store.load();
//    }
});


