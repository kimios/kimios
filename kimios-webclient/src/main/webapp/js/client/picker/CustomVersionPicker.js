/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

kimios.picker.CustomVersionPicker = Ext.extend(Ext.util.Observable,{
    
    constructor:function(config){
        this.dmEntity = config.dmEntityPojo;
        this.documentUid = this.dmEntity.uid;
        this.documentVersionUid = config.documentVersionUid || (this.dmEntity ? this.dmEntity.lastVersionId : null);
        if(this.dmEntity){
            this.currentVersion = this.dmEntity.customVersion;
        } else {
            this.currentVersion = config.documentVersionCustomId;
        }
        if(config.title)
            this.title = config.title;
        if(config.iconCls)
            this.iconCls = config.iconCls;
        if (config.singleSelect){
            this.singleSelect = config.singleSelect;
        }else{
            this.singleSelect = false;
        }
        
        var title = (this.title ? this.title : kimios.lang('CustomVersion'));
        var icon = (this.iconCls ? this.iconCls : 'studio-cls-wf');
        
        this.startButton = new Ext.Button({
          text : kimios.lang('UpdateCustomVersionButton'),
          handler : function(){
            kimios.request.updateVersionId(
                this.form,
                this.documentVersionUid,
                this.versionField.getValue(),
                function(){
                    kimios.explorer.getActivePanel().loadEntity();
                }
            );
            this.window.close();
          },
          scope : this
        });
        
        this.versionField = new Ext.form.TextField({
          fieldLabel : kimios.lang('CustomVersion'),
          value: this.currentVersion || '0.1',
        });
        
      this.form = new kimios.FormPanel({
        bodyStyle : 'padding:10px;background-color:transparent;',
        border : false,
        labelWidth: 100,
        defaults : {
          anchor : '100%',
          selectOnFocus : true,
          style : 'font-size: 10px',
          labelStyle : 'font-size: 11px;font-weight:bold;'
        },
        items : [this.versionField],
        bbar : ['->', this.startButton]
       });
      
        this.window = new Ext.Window({
            width: 250,
            height: 110,
            layout: 'fit',
            border: true,
            title: kimios.lang('UpdateCustomVersionTitle'),
            iconCls: icon,
            resizable: false,
            items : [this.form]
        });
      
        kimios.picker.CustomVersionPicker.superclass.constructor.call(this, config);
    },
    initComponent : function(){
        kimios.picker.CustomVersionPicker.superclass.initComponent.apply(this, arguments);
    },
    show: function(){
        this.window.show();
    }
    
});
