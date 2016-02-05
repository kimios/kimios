/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @contributor Somani - http://www.sencha.com/forum/member.php?51567-Somani
 * @class Ext.ux.form.HtmlEditor.HeadingButtons
 * @extends Ext.ux.form.HtmlEditor.MidasCommand
 * <p>A plugin that creates a button on the HtmlEditor that will have H1 and H2 options. This is used when you want to restrict users to just a few heading types.</p>
 * NOTE: while 'heading' should be the command used, it is not supported in IE, so 'formatblock' is used instead. Thank you IE.
 */

Ext.ux.form.HtmlEditor.HeadingButtons = Ext.extend(Ext.ux.form.HtmlEditor.MidasCommand, {
    // private
    midasBtns: ['|', {
        enableOnSelection: true,
        cmd: 'formatblock',
        value: '<h1>',
        tooltip: {
            title: '1st Heading'
        },
        overflowText: '1st Heading'
    }, {
        enableOnSelection: true,
        cmd: 'formatblock',
        value: '<h2>',
        tooltip: {
            title: '2nd Heading'
        },
        overflowText: '2nd Heading'
    }]
}); 

/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.HeadingMenu
 * @extends Ext.util.Observable
 * <p>A plugin that creates a menu on the HtmlEditor for selecting a heading size. Takes up less room than the heading buttons if your going to have all six heading sizes available.</p>
 */
Ext.ux.form.HtmlEditor.HeadingMenu = Ext.extend(Ext.util.Observable, {
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addItem({
            xtype: 'combo',
            displayField: 'display',
            valueField: 'value',
            name: 'headingsize',
            forceSelection: true,
            mode: 'local',
            triggerAction: 'all',
            width: 65,
            emptyText: 'Heading',
            store: {
                xtype: 'arraystore',
                autoDestroy: true,
                fields: ['value','display'],
                data: [['H1','1st Heading'],['H2','2nd Heading'],['H3','3rd Heading'],['H4','4th Heading'],['H5','5th Heading'],['H6','6th Heading']]
            },
            listeners: {
                'select': function(combo,rec){
                    this.relayCmd('formatblock', '<'+rec.get('value')+'>');
                    combo.reset();
                },
                scope: cmp
            }
        });
    }
});
