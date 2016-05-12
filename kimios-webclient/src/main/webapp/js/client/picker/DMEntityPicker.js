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

kimios.picker.DMEntityPicker = Ext.extend(Ext.util.Observable,{

    constructor:function(config){
        if(config.title)
            this.title = config.title;
        if(config.iconCls)
            this.iconCls = config.iconCls;
        if (config.withDoc)
            this.withDoc = config.withDoc;
        if (config.buttonText)
          this.buttonText = config.buttonText;
        else
          this.buttonText = '';

        kimios.picker.DMEntityPicker.superclass.constructor.call(this, config);
    },

    show: function(){

        var title = (this.title ? this.title : 'No title');
        var icon = (this.iconCls ? this.iconCls : 'q_logo_small');
        var explorer = new kimios.util.DMEntityTree({
            withDoc : this.withDoc,
            loadMask : true,
            border : false
        });
        
        var selectButton = new Ext.Button({
            text: this.buttonText,
            iconCls: 'select',
            disabled: true
        });

        selectButton.on('click', function(){
          this.fireEvent('entitySelected', this.selectedNode);
          window.close();
        }, this);
 
        var window = new Ext.Window({
            width: 250,
            height: 350,
            modal: true,
            layout: 'fit',
            title: title,
            iconCls: icon,
            maximizable: true,
            bbar : ['->', selectButton]
        });

        window.add(explorer);

        explorer.on('click', function(node, e){
          this.selectedNode = node;
            node.expand();
            selectButton.enable();
        }, this);

        window.show();
    }
    ,
    initComponent: function(){
        this.addEvents('entitySelected');
    }
});
