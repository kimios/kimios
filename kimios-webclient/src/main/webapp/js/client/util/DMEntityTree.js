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
kimios.util.DMEntityTree = Ext.extend(Ext.tree.TreePanel, {
    constructor: function (config) {
        var getNode = function (withDoc) {
            var myTreeLoader = new Ext.tree.TreeLoader({
                dataUrl: getBackEndUrl('DmsTree'),
                baseParams: {
                    action: 'getEntities',
                    nodeUid: '',
                    dmEntityType: ''
                },
                autoLoad: true
            });
            myTreeLoader.on('beforeload', function (treeLoader, node) {
                myTreeLoader.baseParams.nodeUid = (node.attributes.dmEntityUid ? node.attributes.dmEntityUid : 0);
                myTreeLoader.baseParams.dmEntityType = (node.attributes.type ? node.attributes.type : 1);
                myTreeLoader.baseParams.action = 'getEntities';
            }, this);

            if (withDoc) {
                myTreeLoader.baseParams.withDoc = true;
            }
            myTreeLoader.on('exception', function (me, node, resp) {
                alert(node.id + " " + resp.responseText);
            });

            var rNode = new Ext.tree.AsyncTreeNode({
                draggable: false,
                text: config.rootText,
                iconCls: 'home',
                loader: myTreeLoader,
                allowChildren: true
            });

            return rNode;
        };

        this.loadMask = true;
        this.useArrows = true;
        if (config.dmEntityType) this.dmEntityType = 3;
        this.withDoc = (config.withDoc ? config.withDoc : false);
        this.root = (config.root ? config.root : getNode(this.withDoc));
        this.autoScroll = (config.autoScroll ? config.autoScroll : true);
        kimios.util.DMEntityTree.superclass.constructor.call(this, config);
        this.root.expand();
    }
});
