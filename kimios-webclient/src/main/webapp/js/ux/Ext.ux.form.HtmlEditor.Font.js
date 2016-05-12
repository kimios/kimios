/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.Font
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a font. Uses the HtmlEditors default font settings which can be overriden on that component to change the list of fonts or default font.</p>
 */
Ext.ux.form.HtmlEditor.Font = Ext.extend(Ext.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var cmp = this.cmp;
        var fonts = function(){
            var fnts = [];
            Ext.each(cmp.fontFamilies, function(itm){
                fnts.push([itm.toLowerCase(),itm]);
            });
            return fnts;
        }(); 
        var btn = this.cmp.getToolbar().addItem({
            xtype: 'combo',
            displayField: 'display',
            valueField: 'value',
            name: 'fontfamily',
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            width: 80,
            emptyText: 'Font',
            tpl: '<tpl for="."><div class="x-combo-list-item" style="font-family:{value};">{display}</div></tpl>',
            store: {
                xtype: 'arraystore',
                autoDestroy: true,
                fields: ['value','display'],
                data: fonts
            },
            listeners: {
                'select': function(combo,rec){
                    this.relayCmd('fontname', rec.get('value'));
                    this.deferFocus();
                    combo.reset();
                },
                scope: cmp
            }
        });
    }
});