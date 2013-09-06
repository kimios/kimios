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
kimios.explorer.CommentsPanel = Ext.extend(Ext.grid.GridPanel, {
    constructor: function (config) {
        this.enableComments = false;
        this.title = kimios.lang('Comments');
        this.iconCls = 'Comments';
        this.closable = true;
        this.iconCls = 'comment';
        this.autoScroll = true;
        this.stripeRows = true;
        this.hideHeaders = true;
        this.viewConfig = {forceFit: true, scrollOffset: 0};
        this.split = true;
        this.animCollapse = false;
        this.collapseMode = 'mini';
        this.hideCollapseTool = true;
        this.border = true;
        this.enableTabScroll = true;

        this.store = new DmsJsonStore({
            fields: [
                {
                    name: 'authorName',
                    type: 'string'
                },
                {
                    name: 'authorSource',
                    type: 'string'
                },
                {
                    name: 'comment',
                    type: 'string'
                },
                {
                    name: 'date',
                    type: 'long'
                },
                {
                    name: 'documentVersionUid',
                    type: 'long'
                },
                {
                    name: 'uid',
                    type: 'long'
                }
            ],
            sortInfo: {
                field: 'date',
                direction: 'DESC'
            },
            url: 'DocumentVersion'
        }),
            this.cm = this.getColumns();
        this.sm = new Ext.grid.RowSelectionModel({
            singleSelect: true
        });
        this.tools = [
            {
                id: 'plus',
                handler: function (event, toolEl, panel) {
                    Ext.MessageBox.prompt(
                        kimios.lang('AddComment'),
                        kimios.lang('Comment'),
                        function (button, comment) {
                            if (button == 'ok') {
                                kimios.request.addComment(kimios.explorer.getActivePanel().commentsPanel.lastVersionUid, comment, function () {
                                    kimios.Info.msg(kimios.lang('Comment'), kimios.lang('AddComment') + ' ' + kimios.lang('Completed'));
                                    kimios.explorer.getActivePanel().commentsPanel.getStore().reload();
                                });
                            }
                        },
                        this,
                        true);
                }
            },
            {
                id: 'refresh',
                handler: function (event, toolEl, panel) {
                    kimios.explorer.getActivePanel().commentsPanel.loadComments();
                    kimios.explorer.getViewport().doLayout();
                }
            },
            {
                id: 'close',
                handler: function (event, toolEl, panel) {
                    kimios.explorer.getActivePanel().commentsPanel.collapse();
                    kimios.explorer.getActivePanel().commentsPanel.setVisible(false);
                    kimios.explorer.getActivePanel().commentsPanel.enableComments = false;
                    kimios.explorer.getViewport().doLayout();
                }
            }
        ];
        kimios.explorer.CommentsPanel.superclass.constructor.call(this, config);
    },

    refreshLanguage: function () {
//    this.setTitle(kimios.lang('Comments'));
    },

    initComponent: function () {
        kimios.explorer.CommentsPanel.superclass.initComponent.apply(this, arguments);

        this.store.on('beforeload', function (store, options) {
            this.setIconClass('loading');
        }, this);

        this.store.on('load', function (store, options) {
            this.setIconClass(kimios.util.IconHelper.getIconClass(this.pojo.type, this.pojo.extension));
            this.setTitle(this.pojo.name);
        }, this);

        this.on('rowcontextmenu', function (grid, rowIndex, e) {
            e.preventDefault();
            var sm = grid.getSelectionModel();
            sm.selectRow(rowIndex);
            var pojo = new kimios.DMEntityPojo(sm.getSelected().data);
            pojo.comment = sm.getSelected().data;
            pojo.comment.store = grid.getStore();
            kimios.ContextMenu.show(pojo, e, 'comments');
        }, this);

        this.on('containercontextmenu', function (grid, e) {
            e.preventDefault();
            kimios.ContextMenu.show(new kimios.DMEntityPojo({}), e, 'commentsContainer');
        }, this);
    },

    loadComments: function (pojo) {
        if (!pojo)
            pojo = this.pojo;
        this.pojo = pojo;
        var lastVersionStore = kimios.store.getLastVersionStore(this.pojo.uid);
        lastVersionStore.on('beforeload', function (store, options) {
            this.setIconClass('loading');
        }, this);
        lastVersionStore.on('load', function (store, lastVersionRecords, options) {
            this.lastVersionUid = lastVersionRecords[0].data.uid;
            this.store.load({
                params: {
                    action: 'getComments',
                    documentVersionUid: this.lastVersionUid
                },
                callback: function (r, opt, success) {
                    kimios.explorer.getViewport().doLayout();
                }
            });
        }, this);
        lastVersionStore.load();
    },

    getColumns: function () {
        return new Ext.grid.ColumnModel([
            {
                readOnly: true,
                sortable: true,
                hideable: false,
                renderer: function (value, metaData, record) {
                    var html = '<div style="color:gray;font-size:11px;">' + kimios.date(record.get('date')) + '&nbsp;<span style="font-weight:normal;">&lt;' + record.get('authorName') + '@' + record.get('authorSource') + '&gt;</span></div>';
                    html += '<span style="white-space:normal;font-size:11px;">' + record.get('comment') + '</span>';
                    return html;
                }
            }
        ]);
    }
});
