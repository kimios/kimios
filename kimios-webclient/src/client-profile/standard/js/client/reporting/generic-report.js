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
Reporting.Generic = {
    getPanel: function(record){
        var attributesStore = kimios.store.ReportingStore.getAttributesStore(record.data.className);

        var getReportButton = new Ext.Button({
            text: kimios.lang('GetReport'),
            handler: function(){
                var params = [];
                for (var i=0; i<formPanel.items.length; ++i){
                    var item = formPanel.items.get(i);
                    if (item instanceof Ext.form.Field){
                      var name = item.getName();
                      var value = null;
                      if (item instanceof Ext.form.DateField){
                        value = item.getValue().format('Uu');
                      }else if (item instanceof kimios.form.DMEntityField){
                        value = item.hiddenUid;
                      }else if(item instanceof Ext.ux.form.LovCombo) {
                        value = item.getCheckedValue();
                      }else {
                        value = item.getValue();
                      }
                        params.push({
                            name: name,
                            value: value 
                        });
                    }
                }
                
                var genericReportPanel = new kimios.reporting.GenericReportPanel({
                    id: 'reporting-generic-report-panel',
                    className: record.data.className,
                    params: params,
                    loadMask: true,
                    border: false,
                    container : genericContainer
                });

                var container = Ext.getCmp('reporting-generic-container');
                container.removeAll();
                container.add(genericReportPanel);
                formPanel.doLayout();
            }
        });

        var formPanel = new kimios.FormPanel({
          region : 'north',
          border : false,
          margins : '0 5 0 5',
            id: 'reporting-generic-report-form-panel',
            autoHeight : true,
            bodyStyle: 'padding:5px;background-color:transparent;',
            labelWidth: 250,
            defaults: {
                anchor: '100%',
                selectOnFocus: true,
                style: 'font-size: 11px',
                labelStyle: 'font-size: 11px;font-weight:bold;'
            },
            buttonAlign : 'left',
            fbar: [getReportButton]
        });

        attributesStore.on('load', function(st, recs){
            Ext.each(recs, function(rec, ind){
                formPanel.add(Reporting.Generic.getFieldByClassName(rec.data.name, rec.data.type, rec.data.listType, rec.data.availableValues));
            });
            formPanel.doLayout();
            genericContainer.doLayout();
            panel.doLayout();
        });
        
        var genericContainer = new Ext.Panel({
          region : 'center',
          margins : '0 5 5 5',
            id: 'reporting-generic-container',
            viewConfig : {
                forceFit: true
            },
            layout: 'fit'
        });
        
        var panel = new Ext.Panel({
          title: record.data.name,
          iconCls: 'reporting',
          layout : 'border',
          items : [formPanel, genericContainer]
        });
        attributesStore.load();
        return panel;
    },

    getFieldByClassName: function(name, impl, listType,listData){
        var field = null;
        if (impl == 'java.lang.Boolean'){
            field = new Ext.form.Checkbox({
                name: name,
                fieldLabel: name
            });
        } else if (impl == 'java.util.Date'){
            field = new Ext.form.DateField({
                name: name,
                fieldLabel: name,
                format: 'Y-m-d',
                editable: false,
                value: new Date()
            });
        } else if (impl == 'org.kimios.kernel.user.model.User'){
            field = new kimios.form.SecurityEntityField({
                name: name,
                fieldLabel: name,
                entityMode: 'user',
                singleSelect: true
            });
        } else if (impl == 'org.kimios.kernel.dms.model.DMEntityImpl'){
            field = new kimios.form.DMEntityField({
                name: name,
                fieldLabel : name,
                displayField: 'name',
                valueField: 'dmEntityUid',
                hiddenName: name,
                withDoc: true
            });
        } else if (impl == 'java.lang.Integer'){
          field = new Ext.form.NumberField({
        name : name,
        fieldLabel : name
        });
        } else if (impl == 'java.util.List'){
            //display selector
            if(listType == 'org.kimios.kernel.user.model.User') {
                field = new kimios.form.SecurityEntityField({
                    name: name,
                    fieldLabel: name,
                    entityMode: 'user',
                    singleSelect: false
                });

            } else if(listType == 'java.lang.String') {

                var valuesStore = new Ext.data.ArrayStore({
                    autoDestroy: true,
                    // reader configs
                    idIndex: 0,
                    fields: [
                        'value'
                    ]
                });
                var data = listData.split(',');
                if(data){
                    var fData = [];
                    for(var u = 0; u < data.length; u++){
                        fData.push([data[u]]);
                    }
                }
                valuesStore.loadData(fData);

                field = new Ext.ux.form.LovCombo({
                    fieldLabel: name,
                    hiddenName: name,
                    valueField:'value',
                    displayField :'value',
                    triggerAction:'all',
                    maxHeight:200,
                    multiSelect:true,
                    mode: 'local',
                    hideOnSelect:false,
                    triggerAction:'all',
                    store: valuesStore
                });

            }
        } else if (impl == 'java.lang.Long'){
          field = new Ext.form.NumberField({
        name : name,
        fieldLabel : name
      });
        } else {
            field = new Ext.form.TextField({
                name: name,
                fieldLabel: name
            });
        }

        return field;
    }

};
