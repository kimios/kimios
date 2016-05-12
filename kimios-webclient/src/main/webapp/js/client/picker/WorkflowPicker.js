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

kimios.picker.WorkflowPicker = Ext.extend(Ext.util.Observable,{
    
    constructor:function(config){
    this.documentUid = config.documentUid;
    this.documentType = config.documentType;
    this.step = 1;
        if(config.title)
            this.title = config.title;
        if(config.iconCls)
            this.iconCls = config.iconCls;
        if (config.singleSelect){
            this.singleSelect = config.singleSelect;
        }else{
            this.singleSelect = false;
        }
        
        var title = (this.title ? this.title : kimios.lang('StartWorkflow')+' ('+this.step+'/2)');
        var icon = (this.iconCls ? this.iconCls : 'studio-cls-wf');
        
        this.startButton = new Ext.Button({
          text : kimios.lang('StartWorkflow'),
          iconCls : 'qaction-startwf',
          disabled : true,
          handler : function(){
            kimios.request.startWorkflow(
                this.documentUid,
                this.statusField.getValue()
                );
            this.window.close();
          },
          scope : this
        });
        
        this.workflowField = new kimios.form.WorkflowField({
          name : 'workflowUid',
          documentUid : this.documentUid
        });
        
        this.backButton = new Ext.Button({
        scope : this,
        text : kimios.lang('BackLabel'),
        iconCls : 'x-tbar-page-prev',
        disabled : true,
        handler : function(){
            this.step -= 1;
            this.window.setTitle(kimios.lang('StartWorkflow')+' ('+this.step+'/2)');
          this.backButton.disable();
            this.form.remove(this.statusField);
            this.workflowField.enable();
            this.form.doLayout();
        }
        });
        
        this.nextButton = new Ext.Button({
        scope : this,
        text : kimios.lang('NextLabel'),
        iconCls : 'x-tbar-page-next',
        iconAlign : 'right',
        handler : function(){
            this.nextButton.disable();
            this.step += 1;
            if (this.step > 2){
              kimios.request.startWorkflow(
                    this.documentUid,
                    this.statusField.getValue()
                    );
                this.window.close();
            }else{
              this.window.setTitle(kimios.lang('StartWorkflow')+' ('+this.step+'/2)');
              this.backButton.enable();
              this.workflowField.disable();
              this.statusField = new kimios.form.WorkflowStatusField({
              name : 'workflowStatusUid',
              workflowUid : this.workflowField.getValue()
            });
              this.statusField.store.on('load', function(){
                this.nextButton.enable();
              }, this);
            this.form.add(this.statusField);
            this.form.doLayout();
            }
        }
        });
        
      this.form = new kimios.FormPanel({
        bodyStyle : 'padding:10px;background-color:transparent;',
        border : false,
        labelWidth: 150,
        defaults : {
          anchor : '100%',
          selectOnFocus : true,
          style : 'font-size: 10px',
          labelStyle : 'font-size: 11px;font-weight:bold;'
        },
        items : [this.workflowField],
        bbar : [this.backButton,'->',this.nextButton]
       });
      
        this.window = new Ext.Window({
            width: 320,
            height: 130,
            layout: 'fit',
            border: true,
            title: title,
            iconCls: icon,
            resizable: false,
            items : [this.form]
        });
      
        kimios.picker.WorkflowPicker.superclass.constructor.call(this, config);
    },
    
    initComponent : function(){
      kimios.picker.WorkflowPicker.superclass.initComponent.apply(this, arguments);
    },
    
    show: function(){
      // check if document is already involved in a workflow
        var checkStore = kimios.store.getEntityStore(this.documentUid, this.documentType);
        checkStore.load({
          scope : this,
          callback : function(records, options, success){
            if (records[0].data.outOfWorkflow == true){
              this.window.show();
            }else{
              Ext.MessageBox.alert(
            'Workflow',
            kimios.lang('DocumentAlreadyInAWorkflow')
          );
            }
          }
        });
      
    }
    
});
