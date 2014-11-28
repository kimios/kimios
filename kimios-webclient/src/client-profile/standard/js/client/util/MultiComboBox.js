/**
 * Ext.ux.form.LovCombo, List of Values Combo
 *
 * @author    Ing. Jozef Sakáloš
 * @copyright (c) 2008, by Ing. Jozef Sakáloš
 * @date      16. April 2008
 * @version   $Id: Ext.ux.form.LovCombo.js 285 2008-06-06 09:22:20Z jozo $
 *
 * @license Ext.ux.form.LovCombo.js is licensed under the terms of the Open
 * Source
 * LGPL 3.0 license. Commercial use is permitted to the extent that the
 * code/component(s) do NOT become part of another Open Source or Commercially
 * licensed development library or toolkit without explicit permission.
 *
 * License details: http://www.gnu.org/licenses/lgpl.html
 */

/*global Ext */

// add RegExp.escape if it has not been already added
if('function' !== typeof RegExp.escape) {
    RegExp.escape = function(s) {
        if('string' !== typeof s) {
            return s;
        }
        // Note: if pasting from forum, precede ]/\ with backslash
        // manually
        return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');
    }; // eo function escape
}

// create namespace
Ext.ns('Ext.ux.form');

/**
 * @class Ext.ux.form.LovCombo
 * @extends Ext.form.ComboBox
 */
Ext.ux.form.LovCombo = Ext.extend(Ext.form.ComboBox, {

    // {{{
    // configuration options
    /**
     * @cfg {String} checkField name of field used to store checked state.
     * It is automatically added to existing fields.
     * Change it only if it collides with your normal field.
     */
    checkField:'checked'

    /**
     * @cfg {String} separator separator to use between values and texts
     */
    ,separator:','

    /**
     * @cfg {String/Array} tpl Template for items.
     * Change it only if you know what you are doing.
     */
    // }}}
    // {{{
    ,initComponent:function() {
        //Code Changes by Pramod starts here - on 4th Sept, 2013

        //Local variables starts here
        var combo = this,
            totalCount = 0,
            id = this.getId() + '-toolbar-panel'; //toolbar panel's id
        //Local variable declaration ends here

        // template with checkbox
        if(!this.tpl) {
            this.tpl =
                //Modification to template starts here -
                //Modification - Inserted a static div tag in value-list template.
                //We will insert a toolbar (which we will create next) before this div tag
                //using DomHelper's insertBefore method
                    '<div id="' + id + '"></div>'
                    +'<tpl for=".">'
                    +'<div class="x-combo-list-item">'
                    +'<img src="' + Ext.BLANK_IMAGE_URL + '" '
                    +'class="ux-lovcombo-icon ux-lovcombo-icon-'
                    +'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'
                    +'<div class="ux-lovcombo-item-text">{' + (this.displayField || 'text' )+ '}</div>'
                    +'</div>'
                    +'</tpl>'
            ;
        }
        //Toolbar for value-list starts here.
        //Future Modifications-there are many possibilities as per the requirement,
        //so this is a main piece of code.
        //Create a toolbar with select all, deselect all, filter textbox, etc.
        var toolbar = new Ext.Toolbar({
            height: 30,
            items : [{
                xtype : 'checkbox',
                //creating unique id because we need handle of this checkbox later
                id : id + 'selectAllChkBox',
                style : 'padding:-1px 0px 0px 1px;',
                boxLabel: 'Select All',
                handler : function(chkBox, isChecked) {
                    //Clearing any filter criterion, and reseting its value
                    Ext.getCmp(id + 'filter-field').reset();
                    combo.store.clearFilter();
                    //As checkbox button serves 2 different purposes i.e. "Select All" or "Deselect All"
                    //First if button's label/text is either "Select All" or "Deselect All"
                    //When "Select All" and checkbox is clicked - selects all records in combobox dropdown list
                    //When "Deselect All" and checkbox deselected - deselects all records.
                    //Once the selection/deselection is done - toggle its text.

                    //Flag to check if select all checkbox is checked and
                    //now we have to check/select all records/values.
                    var selectAll = (chkBox.boxLabel == "Select All")?true:false;

                    if (selectAll) {
                        combo.selectAll();
                        totalCount = combo.getStore().getRange().length;
                        chkBox.boxLabel = "Deselect All";
                    } else {
                        //else condition means, not all of them are selected
                        combo.deselectAll();
                        totalCount = 0;
                        chkBox.boxLabel = "Select All";
                    }
                    //Updating Checkbox's label.
                    if(chkBox.rendered){
                        chkBox.wrap.child('.x-form-cb-label').update(chkBox.boxLabel);
                    }
                    //Change the select count innerHTML as well to total count value.
                    Ext.getDom( id + 'total-count').innerHTML = 'Selected : '+totalCount+'/'+combo.getStore().getRange().length;
                }
            }, '-', {
                xtype : 'textfield',
                enableKeyEvents : true,
                id : id + 'filter-field',
                emptyText : 'enter search text',
                listeners : {
                    keyup : function(field, e) {
                        combo.store.clearFilter();
                        if (field.getValue()) {
                            //filter records as per typed text, case-sensitive=false and any-match=true
                            combo.store.filter(combo.displayField, field.getValue(), true, false);
                        }
                    }
                }
            }, '->',{
                xtype: 'tbtext',
                text : 'Selected : '+totalCount+'/'+combo.getStore().getRange().length,
                qtip : 'Total selected items',
                id : id + 'total-count'
            }]
        });
        //Toolbar creation ends here

        // call parent
        Ext.ux.form.LovCombo.superclass.initComponent.apply(this, arguments);

        // install internal event handlers
        this.on({
            scope:this
            ,beforequery:this.onBeforeQuery
            ,blur:this.onRealBlur

            //Adding a default select handler for combo-box,
            //to check if its checkbox's was selected or deselected...
            ,select: function (combo, record, index) {
                //Find if its deselect or select by using this.checkField
                var isDeselect = (record.get(combo.checkField) == false)?true:false;
                var chkBox = Ext.getCmp(id + 'selectAllChkBox');

                if(isDeselect){	//this condition means a record was deselected,
                    totalCount--; //decrement the total selected values count
                    //an item is deselected,
                    //When any item is deselected change the count and Total :x	Text and deselect top "Select All" checkbox
                    //and change its text back to "Select All".

                    chkBox.boxLabel = "Select All";
                    chkBox.el.dom.checked = false;
                    chkBox.checked = false;
                }else{	//this condition means a record was selected,
                    totalCount++; //Increment the total selected values count
                    //an item is selected,
                    //Check if totalCount is equals to total store records, then select "Select All" checkbox
                    //And change its text to "Deselect All".

                    if(totalCount == combo.getStore().getRange().length){
                        chkBox.el.dom.checked = true;
                        chkBox.checked = true;
                        chkBox.boxLabel = "Deselect All";
                    }
                }
                //updating "Select All"/"Deselect All" 's Checkbox text as per condition
                if(chkBox.rendered){
                    chkBox.wrap.child('.x-form-cb-label').update(chkBox.boxLabel);
                }
                //setting proper text indication how many values were selected out of total
                Ext.getDom( id + 'total-count').innerHTML = 'Selected : '+totalCount+'/'+combo.getStore().getRange().length; //Change Total : x innerHTML
            }
            ,expand: {
                fn: function(converted) {
                    //inserting above toolbar into combobox list at the top.
                    if(Ext.get(id)){
                        var dropdown = Ext.get(id).dom.parentElement; //getting a div tag handle which we have added to template above.
                        var container = Ext.DomHelper.insertBefore(dropdown, '<div id="'+id+'-container"></div>', true);
                        toolbar.render(container);
                    }


                }
                //single: true //Will execute only once, when combo-box expands for the first time
            }
        });

        // remove selection from input field
        this.onLoad = this.onLoad.createSequence(function() {
            //to increase size of dropdown list by 30, to compensate for height of toolbar added,
            //otherwise last value in dropdown list will be gone.
            if(this.el && this.hasFocus && this.store.getCount() > 0){
                this.list.setHeight(this.list.getHeight()+30);
                this.innerList.setHeight(this.innerList.getHeight()+30);
            }
            if(this.el) {
                var v = this.el.dom.value;
                this.el.dom.value = '';
                this.el.dom.value = v;
            }
        });
        //Pramod's modifications to LovCombo ends here. Happy Coding...

    } // e/o function initComponent
    // }}}
    // {{{
    ,initEvents:function() {
        Ext.ux.form.LovCombo.superclass.initEvents.apply(this, arguments);
    } // eo function initEvents
    // }}}
    // {{{
    /**
     * clears value
     */
    ,clearValue:function() {
        this.value = '';
        this.setRawValue(this.value);
        this.store.clearFilter();
        this.store.each(function(r) {
            r.set(this.checkField, false);
        }, this);
        if(this.hiddenField) {
            this.hiddenField.value = '';
        }
        this.applyEmptyText();
    } // eo function clearValue
    // }}}
    // {{{
    /**
     * @return {String} separator (plus space) separated list of selected displayFields
     * @private
     */
    ,getCheckedDisplay:function() {
        var re = new RegExp(this.separator, "g");
        return this.getCheckedValue(this.displayField).replace(re, this.separator + ' ');
    } // eo function getCheckedDisplay
    // }}}
    // {{{
    /**
     * @return {String} separator separated list of selected valueFields
     * @private
     */
    ,getCheckedValue:function(field) {
        field = field || this.valueField;
        var c = [];

        // store may be filtered so get all records
        var snapshot = this.store.snapshot || this.store.data;

        snapshot.each(function(r) {
            if(r.get(this.checkField)) {
                c.push(r.get(field));
            }
        }, this);

        return c.join(this.separator);
    } // eo function getCheckedValue

    // Code Modification by Pramod - 26th March 2014
    // before blur function - overriden to remove call to
    // assertValue() function of superclass Ext.form.ComboBox
    // because combobox by default can select only one record,
    // here, in this Ext.ux.form.LovCombo, multiple records seletion is handled
    // using checked field in the records
    ,beforeBlur : function(){
        //this.assertValue();
    }
    // Code Modification by Pramod - 26th March 2014 ends here

    // }}}
    // {{{
    /**
     * beforequery event handler - handles multiple selections
     * @param {Object} qe query event
     * @private
     */
    ,onBeforeQuery:function(qe) {
        qe.query = qe.query.replace(new RegExp(this.getCheckedDisplay() + '[ ' + this.separator + ']*'), '');
    } // eo function onBeforeQuery
    // }}}
    // {{{
    /**
     * blur event handler - runs only when real blur event is fired
     */
    ,onRealBlur:function() {
        this.list.hide();
        var rv = this.getRawValue();
        var rva = rv.split(new RegExp(RegExp.escape(this.separator) + ' *'));
        var va = [];
        var snapshot = this.store.snapshot || this.store.data;

        // iterate through raw values and records and check/uncheck items
        Ext.each(rva, function(v) {
            snapshot.each(function(r) {
                if(v === r.get(this.displayField)) {
                    va.push(r.get(this.valueField));
                }
            }, this);
        }, this);
        this.setValue(va.join(this.separator));
        this.store.clearFilter();
    } // eo function onRealBlur
    // }}}
    // {{{
    /**
     * Combo's onSelect override
     * @private
     * @param {Ext.data.Record} record record that has been selected in the list
     * @param {Number} index index of selected (clicked) record
     */
    ,onSelect:function(record, index) {
        if(this.fireEvent('beforeselect', this, record, index) !== false){

            // toggle checked field
            record.set(this.checkField, !record.get(this.checkField));

            // display full list
            if(this.store.isFiltered()) {
                this.doQuery(this.allQuery);
            }

            // set (update) value and fire event
            this.setValue(this.getCheckedValue());
            this.fireEvent('select', this, record, index);
        }
    } // eo function onSelect
    // }}}
    // {{{
    /**
     * Sets the value of the LovCombo
     * @param {Mixed} v value
     */
    ,setValue:function(v) {
        if(v) {
            v = '' + v;
            if(this.valueField) {
                this.store.clearFilter();
                this.store.each(function(r) {
                    var checked = !(!v.match(
                            '(^|' + this.separator + ')' + RegExp.escape(r.get(this.valueField))
                                +'(' + this.separator + '|$)'))
                        ;

                    r.set(this.checkField, checked);
                }, this);
                this.value = this.getCheckedValue();
                this.setRawValue(this.getCheckedDisplay());
                if(this.hiddenField) {
                    this.hiddenField.value = this.value;
                }
            }
            else {
                this.value = v;
                this.setRawValue(v);
                if(this.hiddenField) {
                    this.hiddenField.value = v;
                }
            }
            if(this.el) {
                this.el.removeClass(this.emptyClass);
            }
        }
        else {
            this.clearValue();
        }
    } // eo function setValue
    // }}}
    // {{{
    /**
     * Selects all items
     */
    ,selectAll:function() {
        this.store.each(function(record){
            // toggle checked field
            record.set(this.checkField, true);
        }, this);

        //display full list
        this.doQuery(this.allQuery);
        this.setValue(this.getCheckedValue());
    } // eo full selectAll
    // }}}
    // {{{
    /**
     * Deselects all items. Synonym for clearValue
     */
    ,deselectAll:function() {
        this.clearValue();
    } // eo full deselectAll 
    // }}}

}); // eo extend

// register xtype
Ext.reg('lovcombo', Ext.ux.form.LovCombo);
// eof