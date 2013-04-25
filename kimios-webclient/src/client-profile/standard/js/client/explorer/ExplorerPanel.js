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
kimios.explorer.ExplorerPanel = Ext.extend(kimios.util.DMEntityTree, {
    constructor: function (config) {
        this.id = 'kimios-dm-entity-tree-panel';
        this.withDoc = false;
        this.autoScroll = true;
        this.rootText = config.rootText;
        this.title = kimios.lang('WorkspaceExplorer');
//        this.title = '<span style="font-size:10px;">' + kimios.lang('WorkspaceExplorer') + '</span>';
//        this.iconCls = 'dm-entity-tab-properties-workspace';
        this.singleExpand = false;
        this.enableDD = true;
        this.ddGroup = 'grid2tree';
        this.ddScroll = true;
        this.enableDragDrop = true;
        this.listeners = {
            beforenodedrop: {
                fn: function (e) {
                    if (Ext.isArray(e.data.selections)) {
                        var target = e.target.id.split('_');
                        var source = e.data.selections[0].data;
                        if (source.type == 1)
                            return false;
                        if (source.type == 2 && target[1] == undefined)
                            return false;
                        if (e.data.selections.length == 1) {
                            kimios.request.moveDMEntity(source.uid, source.type, target[0], target[1],
                                function () {
                                    if (source.type != 3)
                                        Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                                    kimios.explorer.getViewport().refreshGrids();
                                },
                                function (resp, opt) {
                                    if (resp.status == undefined)
                                        resp.status = 500;
                                    if (resp.statusText == undefined)
                                        resp.statusText = 'Unknown error';
                                    kimios.MessageBox.exception({
                                        exception: 'HTTP error: ' + resp.status + ' ' + resp.statusText,
                                        stackTrace: resp.responseText
                                    });
                                }
                            );
                        } else {
                            var pojos = [];
                            for (var i = 0; i < e.data.selections.length; i++)
                                pojos.push(new kimios.DMEntityPojo(e.data.selections[i].data));
                            kimios.request.moveDMEntities(pojos, target[0], target[1], function () {
                                kimios.Info.msg(kimios.lang('Entities'), kimios.lang('Move') + ' ' + kimios.lang('Completed'));
                                kimios.explorer.getViewport().refreshGrids();
                                Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                            });
                        }
                    }
                }
            },

            beforemovenode: {
                fn: function (tree, node, oldParent, newParent, index) {
                    if (oldParent.parentNode == null || newParent.parentNode == null)
                        return false;
                    var myNode = node.id.split('_');
                    var srcNodeId = myNode[0];
                    var srcNodeType = myNode[1];
                    var dstNode = newParent.id.split('_');
                    var dstNodeId = dstNode[0];
                    var dstNodeType = dstNode[1];
                    kimios.request.moveDMEntity(srcNodeId, srcNodeType, dstNodeId, dstNodeType,
                        function () {
                            kimios.Info.msg(kimios.lang('Entity'), kimios.lang('Move') + ' ' + kimios.lang('Completed'));
                            kimios.explorer.getViewport().refreshGrids();
                        },
                        function (resp, opt) {
                            Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
                            if (resp.status == undefined)
                                resp.status = 500;
                            if (resp.statusText == undefined)
                                resp.statusText = 'Unknown error';
                            kimios.MessageBox.exception({
                                exception: 'HTTP error: ' + resp.status + ' ' + resp.statusText,
                                stackTrace: resp.responseText
                            });

                        }
                    );
                }
            }
        };
        this.animate = true; // set to false if tree node synchronization fail
        kimios.explorer.ExplorerPanel.superclass.constructor.call(this, config);
    },

    refresh: function () {
        this.setIconClass('loading');
        this.getRootNode().reload(function () {
            this.setIconClass(null);
        }, this);
    },

    refreshLanguage: function () {
        this.setTitle(kimios.lang('WorkspaceExplorer'));
        this.doLayout();
    },

    initComponent: function () {
        kimios.explorer.ExplorerPanel.superclass.initComponent.apply(this, arguments);
        this.on('click', function (node, e) {
            node.expand();
            var centerPanel = Ext.getCmp('kimios-center-panel');
            var tabsCounter = centerPanel.items.length;
            var tab = null;

            if (tabsCounter > 0 && centerPanel.getActiveTab() instanceof kimios.explorer.DMEntityGridPanel) {
                tab = centerPanel.getActiveTab();
            } else {
                tab = new kimios.explorer.DMEntityGridPanel({});
                centerPanel.add(tab);
                centerPanel.setActiveTab(tab);
            }
            if (tab.advancedSearchPanel.isVisible())
                tab.advancedSearchPanel.hidePanel();

            kimios.explorer.getActivePanel().searchToolbar.searchField.isSearchMode = false;
            kimios.explorer.getActivePanel().advancedSearchPanel.isSearchMode = false;
            tab.searchToolbar.searchField.setValue('');

            tab.loadEntity({
                uid: node.attributes.dmEntityUid,
                type: node.attributes.type
            });
        }, this);

        this.on('contextMenu', function (node, e) {
            node.select();
            kimios.ContextMenu.show(new kimios.DMEntityPojo(node.attributes), e, 'tree');
        }, this);

        this.on('containercontextmenu', function (tree, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'treeContainer');
        }, this);
    },

    toDmsPath: function (treePath) {
        return treePath.substr(this.getRootNode().text.length + 1);
    },

    synchronize: function (dmsPath) {
        if (dmsPath == '/')
            dmsPath = undefined;
        this.selectPath(
            dmsPath == undefined ? '' : '/' + this.getRootNode().text + dmsPath, 'text',
            null, // since extjs 3.3.0
            function (success, selectedNode) {
                if (success == false) {
                    kimios.explorer.getActivePanel().destroy();
                } else {
                    selectedNode.expand();
                }
            });
    }
});
