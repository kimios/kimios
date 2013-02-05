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
kimios.properties.MetaDataPanel = Ext.extend(Ext.Panel, {
  
  constructor : function(config){
    this.dmEntityPojo = config.dmEntityPojo;
    this.changed = false;
      this.title = kimios.lang('MetaData');
      this.iconCls = 'option';
      this.border = false;
      this.loadingRequired = true;
      this.loaded = false;
      this.layout = 'fit';
      this.bodyStyle = 'background-color:transparent;';
    kimios.properties.MetaDataPanel.superclass.constructor.call(this, config);
  },
  
  forceLoad : function(handle){
    this.lastMetaValuesStore.load({
      callback : handle
    });
  },
  
  setPojo : function(pojo){
    this.dmEntityPojo = pojo;
    this.loaded = false;
  },
  
  initComponent: function(){
    kimios.properties.MetaDataPanel.superclass.initComponent.apply(this, arguments);
    this.documentTypeUid = this.dmEntityPojo.documentTypeUid;
    this.documentTypeUid_ = this.documentTypeUid;
    this.fields_ = [];    
    if (this.dmEntityPojo.versionUid != undefined){
      this.lastMetaValuesStore = kimios.store.getMetaValuesStore(this.dmEntityPojo.versionUid);
    }else{
      this.lastMetaValuesStore = kimios.store.getLastMetaValuesStore(this.dmEntityPojo.uid);
    }
    
    this.on('activate', function(){
      this.doLayout();
      if (this.loaded == false && this.dmEntityPojo.uid != undefined){
        this.lastMetaValuesStore.load();
      }
    }, this);
    
    this.lastMetaValuesStore.on('beforeload', function(store, options){
      this.setIconClass('loading');
    }, this);
    
    this.lastMetaValuesStore.on('load', function(store, records, options){
      this.loaded = true;
      this.generateMetaForm(records);
      this.doLayout();
      this.setIconClass('option');
    }, this);
    
    this.documentTypeField = new kimios.form.DocumentTypeField({
        name: 'document-type',
        displayField : 'name',
        valueField : 'uid',
        hiddenName : 'document-type',
        readOnly : this.readOnly
      });
      
      this.documentTypeForm = new kimios.FormPanel({
        border : false,
        region : 'north',
        height : 40,
        labelWidth : 200,
        bodyStyle : 'padding-left:10px;padding-top:10px;background-color:transparent;',
        defaults : {
              width : 200,
              selectOnFocus : true,
              style : 'font-size: 11px',
              labelStyle : 'font-size: 11px;font-weight:bold;'
            },
        items : [this.documentTypeField]
      });
      
      this.metaDataFieldSet = new kimios.FormPanel({
        region : 'center',
        border : false,
        labelWidth: 200,
        autoScroll : true,
        bodyStyle : 'padding-left:10px;background-color:transparent;',
        defaults : {
          width : 200,
          selectOnFocus : true,
          style : 'font-size: 11px',
          labelStyle : 'font-size: 11px;font-weight:bold;'
        }
      });
      
    this.documentTypeField.on('select', function(combo, record, index){
      this.documentTypeUid = combo.getValue();
      var docTypeStore = kimios.store.StudioStore.getDocumentTypesStore(true);
      docTypeStore.on('load', function(store, records, options){
        for (var i=0; i<records.length; i++){
          if (records[i].data.uid == this.documentTypeUid){
            if (records[i].data.documentTypeUid == -1){
              this.parentType_ = undefined;
            }else{
              if (records[i].data.documentTypeUid == this.documentTypeUid_){
                this.parentType_ = records[i].data.documentTypeUid;
              }
            }
            break;
          }else{
            this.parentType_ = undefined;
          }
        }
        this.loadMetaFields(this.documentTypeUid);
      }, this);
    }, this);
    
    this.documentTypeField.on('change', function(field, newValue, oldValue ){
      this.changed = true;
    },this);
    
    this.on('show', function(){
      this.documentTypeField.focus(true, 200);  
    },this);
    
    this.add(new Ext.Panel({
      layout : 'border',
      border : false,
      bodyStyle : 'background-color:transparent;',
      items : [this.documentTypeForm, this.metaDataFieldSet]
    }));
        
    if (this.dmEntityPojo.documentTypeUid && this.dmEntityPojo.documentTypeUid != -1){
      this.documentTypeField.setValue(this.dmEntityPojo.documentTypeName);
    }else{
      this.documentTypeField.setValue(kimios.lang('NoDocumentType'));
      this.setIconClass('option');
    }
  },
  
  loadMetaFields : function(docTypeUid){
    this.setIconClass('loading');
    kimios.store.getMetasStore(docTypeUid).on('load', function(store, metasRecords, options){
      this.generateMetaForm(metasRecords);
      this.setIconClass('option');
    }, this);
  },
  
  generateMetaForm : function(records){
    this.metaDataFieldSet.removeAll();
    var fields = [];
    Ext.each(records, function(record, index){
      var field = this.getField(record);
      field.on('change', function(field, newValue, oldValue ){
        this.changed = true;
      },this);
      fields.push(field);
    }, this);
    this.metaDataFieldSet.add(fields);
    this.doLayout();
  },
  
  getJsonMetaValues : function(){
    var out = [];
    if (this.metaDataFieldSet.items == undefined)
      return Ext.util.JSON.encode(out);
    for (var i=0; i<this.metaDataFieldSet.items.length; i++){
      var field = this.metaDataFieldSet.items.get(i);
      out.push({
        uid : field.getName(),
        value : field.getValue()
      });
    }
    return Ext.util.JSON.encode(out);
  },
  
  getField : function(record){
    var uid = record.get('uid');
    var type = record.get('type');
    var name = record.get('name');
    var value = record.get('value');
    var metaFeedUid = record.get('metaFeedUid');
    
    if (this.documentTypeUid == this.documentTypeUid_ || this.parentType_){
      this.fields_.push({
        uid:uid,
        value:value
      });
    }

    var thisValue;
    for (var i=0; i<this.fields_.length; i++){
      if (this.fields_[i].uid == uid){
        thisValue = this.fields_[i].value;
        break;
      }
    }
    
    switch (type){
      case 1:
        //string type
        if (metaFeedUid == -1){
          return new Ext.form.TextField({
            name : uid,
            fieldLabel : name,
            value : value ? value : (this.documentTypeUid == this.documentTypeUid_ || this.parentType_ ? thisValue : ''),
            emptyText : kimios.lang('SearchText'),
            readOnly : this.readOnly
          });
        }else{
          return new kimios.form.MetaFeedField({
            name : uid,
            metaFeedUid : metaFeedUid,
            fieldLabel : name,
            value : value ? value : (this.documentTypeUid == this.documentTypeUid_ || this.parentType_ ? thisValue : ''),
            emptyText : kimios.lang('MetaFeed'),
            readOnly : this.readOnly
          });
        }
      case 2:
        //int type
        return new Ext.form.NumberField({
          name : uid,
          fieldLabel : name,
          value : value ? value : (this.documentTypeUid == this.documentTypeUid_ || this.parentType_ ? thisValue : ''),
          emptyText : kimios.lang('MetaNumberValue'),
          readOnly : this.readOnly
        });
      case 3:
        //date type
        return new Ext.form.DateField({
          name : uid,
          fieldLabel : name,
          format: kimios.lang('ShortJSDateFormat'),
          value : value ? value : (this.documentTypeUid == this.documentTypeUid_ || this.parentType_ ? thisValue : ''),
          editable : false,
          emptyText : kimios.lang('MetaDateValue'),
          readOnly : this.readOnly
        });
      case 4:
        //boolean type
        return new Ext.form.Checkbox({
          name : uid,
          fieldLabel : name,
          checked : value == 'true' ? true : (this.documentTypeUid == this.documentTypeUid_ || this.parentType_ ? thisValue : ''),
          checked : value == 'true' ? true : false,
          readOnly : this.readOnly
        });
    }
  }
});
