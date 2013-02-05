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
kimios.form.WorkflowField = Ext.extend(Ext.form.ComboBox,{
  constructor : function(config){
      this.fieldLabel = kimios.lang('Workflow');
      this.editable = false;
      this.triggerAction = 'all';
        this.displayField = 'name';
        this.valueField = 'uid';
        this.hiddenName = config.name;
        this.mode = 'local';
        this.typeAhead = true;
        this.typeAheadDelay = 1000;
        this.lazyRender = true;
        this.autoSelect = true;
      this.store = kimios.store.StudioStore.getWorkflowsStore(true);
    kimios.form.WorkflowField.superclass.constructor.call(this, config);
  },
  
  initComponent : function(){
    kimios.form.WorkflowField.superclass.initComponent.apply(this, arguments);
    this.store.on('load', function(store, records, options){
      this.setValue(records[0].data.uid, true);
    }, this);
  }
});
