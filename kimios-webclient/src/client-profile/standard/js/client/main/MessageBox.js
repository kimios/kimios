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
kimios.MessageBox = {
    exception:function (config) {
        if (Ext.getCmp('qmsgbox') != undefined && Ext.getCmp('qmsgbox').isVisible() == true)
            return;

        var ex = config.exception;
        if (config.exception.indexOf("Error 15") != -1) { //Integrity exception
            ex = kimios.lang('ObjectAlreadyUsed');
        } else if (config.exception.indexOf("Error 04") != -1) { //Access Denied exception
            ex = kimios.lang('AccessDenied');
        }

        var np = new Ext.Panel({
            region:'north',
            layout:'fit',
            border:false,
            autoHeight:true,
            bodyStyle:'padding:15px 15px 15px 15px;background-color:transparent;font-size: 11px;',
            items:[new Ext.form.DisplayField({value:ex})]
        });

        var traceTextArea = new Ext.form.TextArea({
            value:config.stackTrace,
            readOnly:true,
            editable:false,
            style:'font-size:11px;'
        });

        var sp = new Ext.Panel({
            border:false,
            collapsible:true,
            collapsed:true,
            animCollapse:false,
            hideCollapseTool:true,
            margins:'0 15 15 15',
            region:'center',
            layout:'fit',
            items:[traceTextArea]
        });

        var w = new Ext.Window({
            id:'qmsgbox',
            title:kimios.lang('ExceptionOccurred'),
            iconCls:'warn',
            width:400,
            height:200,
            layout:'border',
            modal:true,
            maximizable:true,
            buttonAlign:'right',
            fbar:[
                new Ext.Button({
                    text:kimios.lang('BugReport'),
                    disabled:config.stackTrace == undefined,
                    enableToggle:true,
                    toggleHandler:function (b, p) {
                        if (p == true) {
                            sp.expand();
                        } else {
                            sp.collapse();
                        }
                    }
                }),
                new Ext.Button({
                    text:kimios.lang('Close'),
                    handler:function (b) {
                        w.close();
                    }
                })]
        });

        w.add(np);
        w.add(sp);
        w.show();
    }
};


