/**
 * @author Shea Frederick - http://www.vinylfox.com
 * @class Ext.ux.form.HtmlEditor.HR
 * @extends Ext.util.Observable
 * <p>A plugin that creates a button on the HtmlEditor for inserting a horizontal rule.</p>
 */
Ext.ux.form.HtmlEditor.HR = Ext.extend(Ext.util.Observable, {
    // HR language text
    langTitle   : 'Horizontal Rule',
    langHelp    : 'Enter the width of the Rule in percentage<br/> followed by the % sign at the end, or to<br/> set a fixed width ommit the % symbol.',
    langInsert  : 'Insert',
    langCancel  : 'Cancel',
    langWidth   : 'Width',
    // defaults
    defaultHRWidth: '100%',
    // private
    cmd: 'hr',
    // private
    init: function(cmp){
        this.cmp = cmp;
        this.cmp.on('render', this.onRender, this);
    },
    // private
    onRender: function(){
        var cmp = this.cmp;
        var btn = this.cmp.getToolbar().addButton({
            iconCls: 'x-edit-hr',
            handler: function(){
                if (!this.hrWindow) {
                    this.hrWindow = new Ext.Window({
                        title: this.langTitle,
                        width: 240,
                        closeAction: 'hide',
                        items: [{
                            itemId: 'insert-hr',
                            xtype: 'form',
                            border: false,
                            plain: true,
                            bodyStyle: 'padding: 10px;',
                            labelWidth: 60,
                            labelAlign: 'right',
                            items: [{
                                xtype: 'label',
                                html: this.langHelp + '<br/>&nbsp;'
                            }, {
                                xtype: 'textfield',
                                maskRe: /[0-9]|%/,
                                regex: /^[1-9][0-9%]{1,3}/,
                                fieldLabel: this.langWidth,
                                name: 'hrwidth',
                                width: 60,
                                value: this.defaultHRWidth,
                                listeners: {
                                    specialkey: function(f, e){
                                        if ((e.getKey() == e.ENTER || e.getKey() == e.RETURN) && f.isValid()) {
                                            this.doInsertHR();
                                        }
                                    },
                                    scope: this
                                }
                            }]
                        }],
                        buttons: [{
                            text: this.langInsert,
                            handler: function(){
                                var frm = this.hrWindow.getComponent('insert-hr').getForm();
                                if (frm.isValid()) {
                                    this.doInsertHR();
                                } else {
                                    frm.findField('hrwidth').getEl().frame();
                                }
                            },
                            scope: this
                        }, {
                            text: this.langCancel,
                            handler: function(){
                                this.hrWindow.hide();
                            },
                            scope: this
                        }],
                        listeners: {
                            render: (Ext.isGecko) ? this.focusHRLong : this.focusHR,
                            show: this.focusHR,
                            move: this.focusHR,
                            scope: this
                        }
                    });
                } else {
                    this.hrWindow.getEl().frame();
                }
                this.hrWindow.show();
            },
            scope: this,
            tooltip: {
                title: this.langInsert + ' ' + this.langTitle
            },
            overflowText: this.langTitle
        });
    },
    // private
    focusHRLong: function(w){
        this.focus(w, 600);
    },
    // private
    focusHR: function(w){
        this.focus(w, 100);
    },
    /**
     * This method is just for focusing the text field use for entering the width of the HR.
     * It's extra messy because Firefox seems to take a while longer to render the window than other browsers, 
     * particularly when Firbug is enabled, which is all the time if your like me.
     * Had to crank up the delay for focusing on render to 600ms for Firefox, and 100ms for all other focusing.
     * Other browsers seem to work fine in all cases with as little as 50ms delay. Compromise bleh!
     * @param {Object} win the window to focus
     * @param {Integer} delay the delay in milliseconds before focusing
     */
    focus: function(win, delay){
        win.getComponent('insert-hr').getForm().findField('hrwidth').focus(true, delay);
    },
    // private
    doInsertHR: function(){
        var frm = this.hrWindow.getComponent('insert-hr').getForm();
        if (frm.isValid()) {
            var hrwidth = frm.findField('hrwidth').getValue();
            if (hrwidth) {
                this.insertHR(hrwidth);
            } else {
                this.insertHR(this.defaultHRWidth);
            }
            frm.reset();
            this.hrWindow.hide();
        }
    },
    /**
     * Insert a horizontal rule into the document.
     * @param w String The width of the horizontal rule as the <tt>width</tt> attribute of the HR tag expects. ie: '100%' or '400' (pixels).
     */
    insertHR: function(w){
        this.cmp.insertAtCursor('<hr width="' + w + '">');
    }
});
