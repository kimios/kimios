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

kimios.form.SecurityEntityField = Ext.extend(Ext.form.TriggerField,{
    
    constructor: function(config){
        if (config.entityMode == null)
            this.entityMode = '';

        this.editable = (config.editable ? config.editable : false);

        if (config.fieldLabel == null){
            if (this.entityMode == 'user')
                this.fieldLabel = kimios.lang('UserName');
            else if (this.entityMode == 'group')
                this.fieldLabel = kimios.lang('GroupName');
            else
                this.fieldLabel = kimios.lang('UserName')+ ' & '+kimios.lang('GroupName');
        }
        kimios.form.SecurityEntityField.superclass.constructor.call(this, config);
    },

    validateValue : function(value){
        if(!kimios.form.SecurityEntityField.superclass.validateValue.call(this, value)){
            return false;
        }
        if(value.length < 1){
            return true;
        }
        return true;
    },

    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },

    getValue : function(){
        return kimios.form.SecurityEntityField.superclass.getValue.call(this);
    },

    setValue : function(value){
        kimios.form.SecurityEntityField.superclass.setValue.call(this, value);
    },

    menuListeners : {
        select: function(m, d){
            this.setValue(d);
        },
        show : function(){
            this.onFocus();
        }
    },

    onTriggerClick : function(){

        if(this.disabled){
            return;
        }
        
        if(this.picker == null){
            this.picker = new kimios.picker.SecurityEntityPicker({
                title: this.fieldLabel,
                iconCls: 'add',
                entityMode: this.entityMode,
                singleSelect: true
            });
        }

        this.picker.on('entitySelected', function(usersRecords, groupsRecords){
            var entity, source;
            if (usersRecords.length == 1){
                entity = usersRecords[0].data.uid;
                source = usersRecords[0].data.source;
            } else if (groupsRecords.length == 1){
                entity = groupsRecords[0].data.gid;
                source = groupsRecords[0].data.source;
            }
            var value = entity+'@'+source;
            this.setValue(value);
            
        }, this);

        this.picker.show();
    },

    beforeBlur : function(){
        var v = this.getRawValue();
        if(v){
            this.setValue(v);
        }
    }
});
