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
kimios.tasks.CommentsPanel = Ext.extend(Ext.grid.GridPanel, {
    constructor: function (config) {
        this.enableComments = false;
        this.closable = true;
        this.autoScroll = true;
        this.stripeRows = false;
        this.hideHeaders = true;
        this.viewConfig = {forceFit: true, scrollOffset: 16};
        this.split = true;
        this.animCollapse = false;
        this.collapseMode = 'mini';
        this.hideCollapseTool = true;
        this.enableTabScroll = true;

        this.store = kimios.store.TasksStore.getBonitaCommentsStore(true, config.taskId);
        var _store = this.store;

        this.cm = this.getColumns();
        this.sm = new Ext.grid.RowSelectionModel({
            singleSelect: true
        });
        this.fbar = [
            {
                id: 'bonitaAddCommentButtonId',
                text: kimios.lang('AddComment'),
                iconCls: 'comment',
                handler: function (event, toolEl, panel) {
                    Ext.MessageBox.prompt(
                        kimios.lang('AddComment'),
                        kimios.lang('Comment'),
                        function (button, comment) {
                            if (button == 'ok') {

                                Ext.Ajax.request({
                                    url: getBackEndUrl('Workflow'),
                                    params: {
                                        action: 'addComment',
                                        taskId: config.taskId,
                                        comment: comment
                                    },
                                    success: function () {
                                        kimios.Info.msg(kimios.lang('Comment'), kimios.lang('AddComment') + ' ' + kimios.lang('Completed'));
                                        _store.load({
                                            baseParams: {
                                                action: 'getComments',
                                                taskId: config.taskId
                                            }
                                        });
                                    },
                                    failure: function () {
                                        kimios.MessageBox.exception({
                                            exception: 'Error while receiving task comments!'
                                        });
                                    }
                                });
                            }
                        },
                        this,
                        true);
                }
            }
        ];
        kimios.tasks.CommentsPanel.superclass.constructor.call(this, config);
    },

    refreshLanguage: function () {
        Ext.getCmp('bonitaAddCommentButtonId').setText(kimios.lang('AddComment'));
    },

    getColumns: function () {
        return new Ext.grid.ColumnModel([
            {
                readOnly: true,
                sortable: true,
                hideable: false,
                renderer: function (value, metaData, record) {
                    var html = '<div style="color:gray;font-size:11px;">' + kimios.date(record.get('postDate')) + '&nbsp;<span style="font-weight:normal;">&lt;'
                        + (record.data.userWrapper != null ? record.data.userWrapper.userName : 'System') + '&gt;</span></div>';
                    html += '<span style="white-space:normal;font-size:11px;">' + record.get('content') + '</span>';
                    return html;
                }
            }
        ]);
    }
});
