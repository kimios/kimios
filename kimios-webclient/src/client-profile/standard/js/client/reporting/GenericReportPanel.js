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
kimios.reporting.GenericReportPanel = Ext.extend(Ext.grid.GridPanel, {
  constructor : function(config){
      this.store = new Ext.data.Store({
        url: getBackEndUrl('Reporting'),
        baseParams: {
            action: 'getReport',
            impl: config.className,
            jsonParameters: Ext.util.JSON.encode(config.params)
        },
        reader: new Ext.data.JsonReader()
    });
      this.columns = [];
      this.viewConfig = {
        forceFit: true
    };
      this.columnLines = true;
      this.container = config.container;
      kimios.reporting.GenericReportPanel.superclass.constructor.call(this, config);
  },
  
    initComponent: function(arguments){
        kimios.reporting.GenericReportPanel.superclass.initComponent.apply(this, arguments);
        
        this.store.on('load', function(){
            if(typeof(this.store.reader.jsonData.columns) === 'object') {
                var columns = [];
                if(this.rowNumberer)
                    columns.push(new Ext.grid.RowNumberer());
                if(this.checkboxSelModel)
                    columns.push(new Ext.grid.CheckboxSelectionModel());
                Ext.each(this.store.reader.jsonData.columns, function(column){
                    columns.push(column);
                });
                this.getColumnModel().setConfig(columns);
            }
            this.doLayout();
            this.container.doLayout();
        }, this);
        
        this.store.load();
        
    }
//  ,
//
//    onRender: function(ct, position){
//        this.colModel.defaultSortable = true;
//        kimios.reporting.GenericReportPanel.superclass.onRender.call(this, ct, position);
//        this.el.mask(kimios.lang('Loading'));
//        
//    }
});
