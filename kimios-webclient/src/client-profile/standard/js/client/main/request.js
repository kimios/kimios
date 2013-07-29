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
kimios.request = {
    updateVersion: function (form, docUid, handle) {
        var uploader = new DmsSimpleUpload();
        uploader.uploadFileImport(form.parent, undefined, form, docUid);
        uploader.on('finished', handle, this);
    },

    uploadDocument: function (newDocument, form, name, parentUid, isSecurityInherited, securityDatas, documentTypeUid, metaValues, handle) {
        if (newDocument == true) {
            var uploader = new DmsSimpleUpload();
            uploader.uploadFile(form.parent, form, 'AddDocument', isSecurityInherited, securityDatas, parentUid, {
                documentTypeUid: documentTypeUid,
                metaValues: metaValues
            });
            uploader.on('finished', handle, this);
        }
    },

    uploadDocumentWithProperties: function (form, name, parentUid, isSecurityInherited, securityDatas, documentTypeUid, metaValues, handle) {
        var uploader = new DmsSimpleUpload();
        uploader.uploadFile(form.parent, form, 'AddDocumentWithProperties', isSecurityInherited, securityDatas, parentUid, {
            documentTypeUid: documentTypeUid,
            metaValues: metaValues
        });
        uploader.on('finished', handle, this);
    },

    newWorkspace: function (name, sec, handle) {
        kimios.ajaxRequest('Workspace', {
            action: 'NewWorkspace',
            name: name,
            sec: sec
        }, handle);
    },

    newFolder: function (name, parentType, parentUid, isSecurityInherited, sec, handle) {
        kimios.ajaxRequest('Folder', {
            action: 'NewFolder',
            name: name,
            parentType: parentType,
            parentUid: parentUid,
            isSecurityInherited: isSecurityInherited,
            sec: sec
        }, handle);
    },

    updateWorkspace: function (uid, name, sec, isRecursive, handle, changeSecurity) {
        kimios.ajaxRequest('Workspace', {
            action: 'UpdateWorkspace',
            uid: uid,
            name: name,
            sec: sec,
            isRecursive: isRecursive,
            changeSecurity: changeSecurity
        }, handle);
    },

    updateFolder: function (uid, name, sec, isRecursive, handle, changeSecurity) {
        kimios.ajaxRequest('Folder', {
            action: 'UpdateFolder',
            uid: uid,
            name: name,
            sec: sec,
            isRecursive: isRecursive,
            changeSecurity: changeSecurity
        }, handle);
    },

    updateDocument: function (uid, name, documentTypeUid, metaValues, sec, newVersion, handle, changeSecurity) {
        kimios.ajaxRequest('Version', {
            action: 'UpdateDocument',
            uid: uid,
            name: name,
            documentTypeUid: documentTypeUid,
            metaValues: metaValues,
            sec: sec,
            newVersion: newVersion,
            changeSecurity: changeSecurity
        }, handle);
    },

    updateEntities: function (dmEntityPojos, jsonSecurityValues, isRecursive, documentTypeUid, jsonMetaValues, handle, changeSecurity) {
        if (documentTypeUid == undefined || documentTypeUid == null || documentTypeUid == '') documentTypeUid = -1;
        kimios.ajaxRequest('DmsEntity', {
            action: 'UpdateEntities',
            dmEntityPojosJson: Ext.util.JSON.encode(dmEntityPojos),
            documentTypeUid: documentTypeUid,
            metaValues: jsonMetaValues,
            sec: jsonSecurityValues,
            isRecursive: isRecursive,
            changeSecurity: changeSecurity
        }, handle);
    },

    moveDMEntity: function (uid, type, targetUid, targetType, hdl, eHdl) {
        var unmovable = false;
        targetUid = '' + targetUid + '';
        targetType = '' + targetType + '';

        switch (targetType) {
            case '1':
                // target is workspace
                if (type == 1 || type == 3) {
                    unmovable = true;
                }
                break;
            case '2':
                // target is folder
                if (type == 1) {
                    unmovable = true;
                }
                break;
            default: //target is root
                if (type == 2 || type == 3) {
                    unmovable = true;
                }
        }

        // source is root or workspace
        if (type == undefined || type == 1) {
            unmovable = true;
        }
        if (unmovable == true) {
            return false;
        }
        if (!hdl) {
            hdl = function () {
                kimios.Info.msg(kimios.lang('Entity'), kimios.lang('Move') + ' ' + kimios.lang('Completed'));
                kimios.explorer.getViewport().refreshGrids();
            };
        }
        kimios.ajaxRequest('DmsEntity', {
            action: 'moveEntity',
            uid: uid,
            type: type,
            targetUid: targetUid,
            targetType: targetType
        }, hdl, eHdl);
    },

    moveDMEntities: function (dmEntityPojos, targetUid, targetType, hdl) {
        if (!hdl) {
            hdl = function () {
                kimios.Info.msg(kimios.lang('Entities'), kimios.lang('Move') + ' ' + kimios.lang('Completed'));
                kimios.explorer.getViewport().refreshGrids();
                Ext.getCmp('kimios-dm-entity-tree-panel').refresh();
            }
        }
        kimios.ajaxRequest('DmsEntity', {
            action: 'moveEntities',
            dmEntityPojosJson: Ext.util.JSON.encode(dmEntityPojos),
            targetUid: targetUid,
            targetType: targetType
        }, hdl);
    },

    deleteDMEntity: function (uid, type, name, handle) {
        var deleteEntity = function () {
            kimios.ajaxRequest('DmsEntity', {
                    action: 'deleteEntity',
                    dmEntityUid: uid,
                    dmEntityType: type
                },
                function () {
                    if (handle == null) {
                        kimios.Info.msg(kimios.lang('Entity'), kimios.lang('Delete') + ' ' + kimios.lang('Completed'));
                        if (type == 1 || type == 2) {
                            var gridTabPanel = kimios.explorer.getMainPanel();
                            for (var i = 0; i < gridTabPanel.items.length; i++) {
                                var tab = gridTabPanel.items.get(i);
                                if (tab.loadable == true && tab.uid == uid) {
                                    tab.destroy();
                                }
                            }
                        }
                        if (type != 3) {
                            kimios.explorer.getTreePanel().refresh();
                        }
                    } else {
                        handle();
                    }
                    kimios.explorer.getViewport().refreshGrids();
                });
        };
        Ext.MessageBox.confirm(
            kimios.lang('Delete'),
            kimios.lang('ConfirmDelete'),
            function (btn) {
                if (btn == 'yes') {
                    deleteEntity();
                }
            }
        );
    },

    deleteDMEntities: function (dmEntityPojos) {
        var deleteDMEntities = function () {
            kimios.ajaxRequest('DmsEntity', {
                    action: 'deleteEntities',
                    dmEntityPojosJson: Ext.util.JSON.encode(dmEntityPojos)
                },
                function () {
                    kimios.Info.msg(kimios.lang('Entities'), kimios.lang('Delete') + ' ' + kimios.lang('Completed'));
                    kimios.explorer.getViewport().refreshGrids();
                    kimios.explorer.getTreePanel().refresh();
                });
        };

        Ext.MessageBox.confirm(
            kimios.lang('Delete'),
            kimios.lang('ConfirmDelete'),
            function (btn) {
                if (btn == 'yes') {
                    deleteDMEntities();
                }
            }
        );

    },

    addToBookmarks: function (uid, type) {
        kimios.ajaxRequest('Version', {
                action: 'AddBookmarkItem',
                uid: uid,
                type: type
            },
            function () {
                kimios.explorer.getViewport().bookmarksPanel.refresh();
            }
        );
    },

    addAllToBookmarks: function (array) {
        kimios.ajaxRequest('Version', {
                action: 'AddBookmarksItem',
                dmEntityPojosJson: Ext.util.JSON.encode(array)
            },
            function () {
                kimios.Info.msg(kimios.lang('BookmarksExplorer'), kimios.lang('Add') + ' ' + kimios.lang('Completed'));
                kimios.explorer.getViewport().bookmarksPanel.refresh();
            }
        );

    },

    removeBookmarks: function (uid, type) {
        kimios.ajaxRequest('Version', {
                action: 'RemoveBookmarkItem',
                uid: uid,
                type: type
            },
            function () {
                kimios.explorer.getViewport().bookmarksPanel.refresh();
            }
        );

    },

    addRelatedDocument: function (documentUid, relatedDocumentUid, store) {
        kimios.ajaxRequest('Version', {
                action: 'AddRelatedDocument',
                uid: documentUid,
                relatedUid: relatedDocumentUid
            },
            function () {
                kimios.Info.msg(kimios.lang('RelatedDocuments'), kimios.lang('AddRelatedDocument') + ' ' + kimios.lang('Completed'));
                store.reload();
            }
        );

    },

    removeRelatedDocument: function (documentUid, relatedDocumentUid, store) {
        kimios.ajaxRequest('Version', {
                action: 'RemoveRelatedDocument',
                uid: documentUid,
                relatedUid: relatedDocumentUid
            },
            function () {
                kimios.Info.msg(kimios.lang('RelatedDocuments'), kimios.lang('RemoveRelatedDocument') + ' ' + kimios.lang('Completed'));
                store.reload();
            }
        );
    },

    startWorkflow: function (documentUid, workflowStatusUid, store, handle) {
        kimios.ajaxRequest('Workflow', {
                action: 'startWorkflowRequest',
                documentUid: documentUid,
                workflowStatusUid: workflowStatusUid
            },
            function () {
                kimios.explorer.getViewport().refreshGrids();
                kimios.explorer.getTasksPanel().refresh();
                kimios.Info.msg(kimios.lang('Workflow'), kimios.lang('WorkflowRequestCreated'));
                if (store != undefined)
                    store.reload();
            }
        );

    },

    cancelWorkflow: function (documentUid) {
        kimios.ajaxRequest('Workflow', {
                action: 'cancelWorkflow',
                documentUid: documentUid
            },
            function () {
                kimios.Info.msg(kimios.lang('Workflow'), kimios.lang('NoWorkflowStarted'));
                kimios.explorer.getViewport().refreshGrids();
                kimios.explorer.getTasksPanel().refresh();
            }
        );
    },

    checkOut: function (documentUid) {
        kimios.ajaxRequest('Version', {
                action: 'checkoutDocument',
                documentUid: documentUid
            },
            function () {
                kimios.explorer.getActivePanel().loadEntity();
                Ext.Msg.alert(kimios.lang('Checkedout'), kimios.lang('DocumentCOOK'), function () {
                    window.location.href = kimios.util.getDocumentLink(documentUid);
                });
            }
        );
    },

    checkIn: function (documentUid, handle) {
        kimios.ajaxRequest('Version', {
                action: 'checkinDocument',
                documentUid: documentUid
            }, handle
        );
    },

    addComment: function (documentVersionUid, comment, handle) {
        kimios.ajaxRequest('DocumentVersion', {
            action: 'AddComment',
            docVersionUid: documentVersionUid,
            comment: comment
        }, handle);
    },

    updateComment: function (documentVersionUid, commentUid, comment, store) {
        kimios.ajaxRequest('DocumentVersion', {
                action: 'UpdateComment',
                docVersionUid: documentVersionUid,
                commentUid: commentUid,
                comment: comment
            },
            function () {
                kimios.Info.msg(kimios.lang('Comment'), kimios.lang('ModifyComment') + ' ' + kimios.lang('Completed'));
                store.reload();
            }
        );
    },

    removeComment: function (commentUid, store) {
        kimios.ajaxRequest('DocumentVersion', {
                action: 'DeleteComment',
                commentUid: commentUid
            },
            function () {
                kimios.Info.msg(kimios.lang('Comment'), kimios.lang('DeleteComment') + ' ' + kimios.lang('Completed'));
                store.reload();
            }
        );

    },

    AdminRequest: {
        saveDomain: function (form, store, contextPanel, gridStore, newName, record) {
            var getRecord = function () {
                for (var i = 0; i < store.getCount(); i++) {
                    var r = store.getAt(i);
                    if (r.get('name') == newName) {
                        return r;
                    }
                }
                return null;
            };

            var out = [];
            gridStore.each(function (rec) {
                out.push({
                    name: rec.get('name'),
                    value: rec.get('value')
                });
            });
            var jsonParams = Ext.util.JSON.encode(out);


            form.getForm().submit(kimios.ajaxSubmit('Admin', {
                    action: (record ? 'updateDomain' : 'createDomainDetails'),
                    jsonParameters: jsonParams
                },
                function (form, action) {
                    store.load({
                        callback: function () {
                            var domainRecord = getRecord();
                            if (domainRecord != null) {
                                Admin.Domains.setContextPanel(store, contextPanel, [
                                    Admin.Domains.getParametersPanel(contextPanel, store, domainRecord),
                                    Admin.Domains.getUsersPanel(domainRecord),
                                    Admin.Domains.getGroupsPanel(domainRecord)
                                ], 0, domainRecord);
                            }
                        }
                    });
                }
            ));
        },

        removeDomain: function (store, record, contextPanel) {
            var remove = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'deleteAuthenticationSource',
                        authenticationSourceName: record.data.name
                    },
                    function (form, action) {
                        store.load();
                        var domainsPanel = contextPanel.get('admin-domains-panel');
                        if (domainsPanel != null && domainsPanel.title == record.data.name) {
                            contextPanel.remove(domainsPanel);
                        }
                    }
                );

            };

            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmAuthSourceDeleteJS'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },

        saveMyAccount: function (f, hd) {
            f.getForm().submit(kimios.ajaxSubmit('Admin', {
                action: 'UpdateDmsUser'
            }, hd));
        },

        saveUser: function (ctn, sm, rowIndex, store, form, rec) {
            form.getForm().submit(kimios.ajaxSubmit('Admin', {
                    action: rec != undefined ? 'UpdateDmsUser' : 'CreateDmsUser'
                },
                function (form, action) {
                    store.load({
                        callback: function () {
                            if (rowIndex != -1)
                                sm.selectRow(rowIndex);
                            else {
                                ctn.collapse();
                                ctn.setVisible(false);
                                Ext.getCmp('admin-domains-users-panel').doLayout();
                            }
                        }
                    });
                }
            ));
        },

        removeUser: function (store, records) {
            var recordsCounter = 0;
            var remove = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'DeleteDmsUser',
                        uid: records[recordsCounter].data.uid,
                        authenticationSourceName: records[recordsCounter].data.source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            remove();
                        else {
                            store.load({
                                callback: function () {
                                    Ext.getCmp('admin-domains-users-details-container').collapse();
                                    Ext.getCmp('admin-domains-users-details-container').setVisible(false);
                                    Ext.getCmp('admin-domains-users-panel').doLayout();
                                }
                            });

                        }
                    }
                );
            };

            Ext.MessageBox.confirm(
                kimios.lang('remove'),
                kimios.lang('ConfirmUserDeleteJS'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },

        saveUserToGroup: function (store, uid, records) {
            if (records.length == 0)
                return;
            var recordsCounter = 0;
            var save = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'AddDmsUserToGroup',
                        uid: uid,
                        gid: records[recordsCounter].data.gid,
                        authenticationSourceName: records[recordsCounter].data.source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            save();
                        else {
                            store.load();
                        }
                    }
                );

            };

            save();
        },

        removeUserToGroup: function (store, uid, records) {
            var recordsCounter = 0;
            var unlink = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'RemoveDmsUserFromGroup',
                        uid: uid,
                        gid: records[recordsCounter].data.gid,
                        authenticationSourceName: records[recordsCounter].data.source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length) {
                            unlink();
                        } else {
                            store.load();
                        }
                    }
                );

            };

            unlink();
        },

        saveGroup: function (ctn, sm, rowIndex, store, form, rec) {

            form.getForm().submit(kimios.ajaxSubmit('Admin', {
                    action: (rec ? 'UpdateDmsGroup' : 'CreateDmsGroup')
                },
                function (form, action) {
                    store.load({
                        callback: function () {
                            if (rowIndex != -1)
                                sm.selectRow(rowIndex);
                            else {
                                ctn.collapse();
                                ctn.setVisible(false);
                                Ext.getCmp('admin-domains-groups-panel').doLayout();
                            }
                        }
                    });
                }
            ));

        },

        removeGroup: function (store, records) {
            var deleteGroup = function (gid, source) {

                kimios.ajaxRequest('Admin', {
                        action: 'DeleteDmsGroup',
                        gid: gid,
                        authenticationSourceName: source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            remove();
                        else {
                            store.load({
                                callback: function () {
                                    Ext.getCmp('admin-domains-groups-details-container').collapse();
                                    Ext.getCmp('admin-domains-groups-details-container').setVisible(false);
                                    Ext.getCmp('admin-domains-groups-panel').doLayout();
                                }
                            });
                        }
                    }
                );

            };
            var recordsCounter = 0;
            var remove = function () {
                var usersGroupsStore = kimios.store.AdminStore.getGroupUsersStore(records[recordsCounter].data.gid, records[recordsCounter].data.source, true);
                usersGroupsStore.on('load', function (st, recs) {
                    if (recs.length > 0) {
                        var linkedUsers = '<b>';
                        Ext.each(recs, function (rec, ind) {
                            linkedUsers += rec.data.uid + ' ';
                        });
                        linkedUsers += '</b>';
                        Ext.MessageBox.confirm(
                            kimios.lang('Delete'),
                            'The following users are linked to this group:<br/><br/>' + linkedUsers + '<br/><br/>Confirm deleting  <b>' + records[recordsCounter].data.gid + '</b>?',
                            function (btn) {
                                if (btn == 'yes') {
                                    deleteGroup(records[recordsCounter].data.gid, records[recordsCounter].data.source);
                                } else {
                                    recordsCounter++;
                                    if (recordsCounter < records.length) {
                                        remove();
                                    } else {
                                        store.load();
                                    }
                                }
                            });
                    } else {
                        deleteGroup(records[recordsCounter].data.gid, records[recordsCounter].data.source);
                    }
                });
            };
            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmGroupDeleteJS'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },

        saveGroupToUser: function (store, gid, records) {
            if (records.length == 0)
                return;
            var recordsCounter = 0;
            var save = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'AddDmsUserToGroup',
                        gid: gid,
                        uid: records[recordsCounter].data.uid,
                        authenticationSourceName: records[recordsCounter].data.source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            save();
                        else {
                            store.load();
                        }
                    }
                );

            };

            save();
        },

        removeGroupToUser: function (store, gid, records) {
            var recordsCounter = 0;
            var unlink = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'RemoveDmsUserFromGroup',
                        gid: gid,
                        uid: records[recordsCounter].data.uid,
                        authenticationSourceName: records[recordsCounter].data.source
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            unlink();
                        else {
                            store.load();
                        }
                    }
                );

            };

            unlink();
        },

        saveRole: function (store, records, roleId) {
            if (records.length == 0)
                return;
            var recordsCounter = 0;
            var save = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'CreateRole',
                        roleId: roleId,
                        uid: records[recordsCounter].get('uid'),
                        source: records[recordsCounter].get('source')
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            save();
                        else {
                            store.load();
                        }
                    }
                );

            };

            save();
        },

        removeRole: function (store, records) {
            var recordsCounter = 0;
            var remove = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'DeleteRole',
                        uid: records[recordsCounter].data.userName,
                        source: records[recordsCounter].data.userSource,
                        roleId: records[recordsCounter].data.role
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            remove();
                        else {
                            store.load();
                        }
                    }
                );

            };

            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmDelete'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },

        disconnectUser: function (usersStore, sessionsStore, uid, source) {

            kimios.ajaxRequest('Admin', {
                    action: 'disconnectUser',
                    userName: uid,
                    userSource: source
                },
                function () {
                    usersStore.reload();
                    sessionsStore.reload();
                }
            );

        },

        eraseSessions: function (usersStore, sessionsStore, records) {
            var recordsCounter = 0;
            var erase = function () {
                if (records[recordsCounter].data.sessionUid == sessionUid) {
                    recordsCounter++;
                    if (recordsCounter < records.length)
                        erase();
                    else {
                        usersStore.reload();
                        sessionsStore.reload();
                    }
                } else {

                    kimios.ajaxRequest('Admin', {
                            action: 'eraseSessions',
                            sessionUidToRemove: records[recordsCounter].data.sessionUid
                        },
                        function () {
                            recordsCounter++;
                            if (recordsCounter < records.length)
                                erase();
                            else {
                                usersStore.reload();
                                sessionsStore.reload();
                            }
                        }
                    );

                }
            };
            erase();
        },

        clearDeadLock: function (store, records) {
            var recordsCounter = 0;
            var clear = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'clearDeadLock',
                        documentUid: records[recordsCounter].data.uid
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            clear();
                        else {
                            store.load();
                        }
                    }
                );

            };

            clear();
        },

        reindex: function (getProgressThread) {

            kimios.ajaxRequest('Admin', {
                    action: 'reindex'
                },
                function () {
                    Ext.TaskMgr.start(getProgressThread);
                }
            );

        },

        changeOwner: function (store, records, userName, userSource) {
            var recordsCounter = 0;
            var change = function () {

                kimios.ajaxRequest('Admin', {
                        action: 'changeOwner',
                        dmEntityUid: records[recordsCounter].data.uid,
                        dmEntityType: records[recordsCounter].data.type,
                        userName: userName,
                        userSource: userSource
                    },
                    function () {
                        recordsCounter++;
                        if (recordsCounter < records.length)
                            change();
                        else {
                            store.load();
                        }
                    }
                );

            };

            change();
        }
    },

    ReportingRequest: {


    },

    StudioRequest: {

        saveDocumentType: function (metaFeedsStore, uid, name, heritedfrom, store, contextPanel, gridStore, newName, record) {
            if (name == '') {
                kimios.MessageBox.exception({
                    exception: kimios.lang('DocumentTypeNameJS')
                });
                return;
            }

            var getRecord = function () {
                for (var i = 0; i < store.getCount(); i++) {
                    var r = store.getAt(i);
                    if (r.get('name') == newName) {
                        return r;
                    }
                }
                return null;
            };
            var out = [];

            for (var i = 0; i < gridStore.getCount(); i++) {
                var rec = gridStore.getAt(i);
                if (rec.get('name') == '') {
                    kimios.MessageBox.exception({
                        exception: kimios.lang('MetaDataNameUndefined')
                    });
                    return;
                }
                if (rec.get('metaType') == '') {
                    kimios.MessageBox.exception({
                        exception: kimios.lang('TypeUndefined')
                    });
                    return;
                }
                out.push({
                    uid: rec.get('uid'),
                    name: rec.get('name'),
                    metaType: rec.get('metaType'),
                    metaFeedUid: rec.get('metaFeedUid'),
                    mandatory: rec.get('mandatory')
                });
            }

            var jsonParams = Ext.util.JSON.encode(out);

            kimios.ajaxRequest('Studio', {
                    action: (record ? 'UpdateDocumentType' : 'AddDocumentType'),
                    uid: uid,
                    name: name,
                    heritedfrom: heritedfrom,
                    jsonParameters: jsonParams
                },
                function (form, action) {
                    store.load({
                        callback: function () {
                            var documentTypeRecord = getRecord();
                            var p = Studio.DocumentTypes.getDocumentTypePanel(metaFeedsStore, contextPanel, store, documentTypeRecord);
                            contextPanel.removeAll();
                            contextPanel.add(p);
                            contextPanel.doLayout();
                        }
                    });
                });

        },

        removeDocumentType: function (store, record, contextPanel) {
            var remove = function () {

                kimios.ajaxRequest('Studio', {
                        action: 'RemoveDocumentType',
                        uid: record.data.uid
                    },
                    function () {
                        store.load();
                        var documentTypePanel = contextPanel.get('studio-document-types-form-panel');
                        if (documentTypePanel != null && documentTypePanel.title == record.data.name) {
                            contextPanel.remove(documentTypePanel);
                        }
                    }
                );

            };

            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmDocTypeDeleteJS'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },


        saveMetaFeed: function (className, name, uid, store, contextPanel, gridStore, newName, record) {
            if (name == '') {
                kimios.MessageBox.exception({
                    exception: kimios.lang('MetaFeedNameEmptyJS')
                });
                return;
            }

            var getRecord = function () {
                for (var i = 0; i < store.getCount(); i++) {
                    var r = store.getAt(i);
                    if (r.get('name') == newName) {
                        return r;
                    }
                }
                return null;
            };

            kimios.ajaxRequest('Studio', {
                    action: (record ? 'UpdateMetaFeed' : 'AddMetaFeed'),
                    uid: uid,
                    name: name,
                    className: className
                },
                function (form, action) {
                    store.load({
                        callback: function () {
                            var metaFeedRecord = getRecord();
                            var p = Studio.MetaFeeds.getMetaFeedPanel(contextPanel, store, metaFeedRecord);
                            contextPanel.removeAll();
                            contextPanel.add(p);
                            contextPanel.doLayout();
                        }
                    });
                });

        },

        removeMetaFeed: function (store, record, contextPanel) {

            var remove = function () {

                kimios.ajaxRequest('Studio', {
                        action: 'RemoveMetaFeed',
                        uid: record.data.uid
                    },
                    function () {
                        store.load();
                        var metaFeedPanel = contextPanel.get('studio-meta-feeds-form-panel');
                        if (metaFeedPanel != null && metaFeedPanel.title == record.data.name) {
                            contextPanel.remove(metaFeedPanel);
                        }
                    }
                );

            };

            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmMetaFeedDeleteJS'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        },

        updateEnumerationValues: function (contextPanel, metaFeedUid, valuesStore, _window) {
            var out = [];
            valuesStore.each(function (rec) {
                out.push({
                    value: rec.get('value')
                });
            });
            var jsonParams = Ext.util.JSON.encode(out);

            kimios.ajaxRequest('Studio', {
                    action: 'UpdateEnumerationValues',
                    uid: metaFeedUid,
                    json: jsonParams
                },
                function (form, action) {
                    _window.close();
                }
            );

        },

        saveWorkflow: function (name, uid, desc, workflowsStore, contextPanel, statusGridStore, newName, workflowRecord) {
            if (name == '') {
                kimios.MessageBox.exception({
                    exception: kimios.lang('WorkflowNameEmptyJS')
                });
                return;
            }

            var getRecord = function () {
                for (var i = 0; i < workflowsStore.getCount(); i++) {
                    var r = workflowsStore.getAt(i);
                    if (r.get('name') == newName) {
                        return r;
                    }
                }
                return null;
            };

            var getManagersArray = function (managerStore) {
                var managersArray = [];
                for (var j = 0; j < managerStore.getCount(); j++) {
                    var r = managerStore.getAt(j);
                    managersArray.push({
                        uid: r.get('securityEntityName'),
                        source: r.get('securityEntitySource'),
                        type: r.get('securityEntityType')
                    });
                }
                return managersArray;
            };

            var statusArray = [];
            statusGridStore.each(function (rec) {
                statusArray.push({
                    uid: rec.get('uid'),
                    name: rec.get('name'),
                    successorUid: rec.get('successorUid'),
                    managers: getManagersArray(rec.data.managerStore)
                });
            });

            var jsonParams = Ext.util.JSON.encode(statusArray);

            kimios.ajaxRequest('Studio', {
                    action: (workflowRecord ? 'UpdateWorkflow' : 'CreateWorkflow'),
                    jsonParameters: jsonParams,
                    name: name,
                    uid: uid,
                    description: desc
                },
                function (form, action) {
                    workflowsStore.load({
                        callback: function () {
                            var p = Studio.Workflows.getWorkflowPanel(contextPanel, workflowsStore, getRecord());
                            contextPanel.removeAll();
                            contextPanel.add(p);
                            contextPanel.doLayout();
                        }
                    });
                });

        },

        removeWorkflow: function (workflowsStore, workflowRecord, contextPanel) {
            var remove = function () {

                kimios.ajaxRequest('Studio', {
                        action: 'RemoveWorkflow',
                        uid: workflowRecord.data.uid
                    },
                    function () {
                        workflowsStore.load();
                        var workflowPanel = contextPanel.get('studio-workflows-form-panel');
                        if (workflowPanel != null && workflowPanel.title == workflowRecord.data.name) {
                            contextPanel.remove(workflowPanel);
                        }
                    }
                );
            };

            Ext.MessageBox.confirm(
                kimios.lang('Delete'),
                kimios.lang('ConfirmDelete'),
                function (btn) {
                    if (btn == 'yes') {
                        remove();
                    }
                });
        }
    },

    TasksRequest: {
        acceptWorkflowRequest: function (documentUid, workflowStatusUid, userName, userSource, statusDate, comment) {
            kimios.ajaxRequest('Workflow', {
                    action: 'acceptWorkflowRequest',
                    documentUid: documentUid,
                    workflowStatusUid: workflowStatusUid,
                    userName: userName,
                    userSource: userSource,
                    statusDate: statusDate,
                    comment: comment
                },
                function (form, action) {
                    kimios.Info.msg(kimios.lang('Workflow'), kimios.lang('WorkflowRequestApproved'));
                    kimios.explorer.getViewport().refreshGrids();
                    kimios.explorer.getTasksPanel().refresh();
                }
            );
        },

        rejectWorkflowRequest: function (documentUid, workflowStatusUid, userName, userSource, statusDate, comment) {
            kimios.ajaxRequest('Workflow', {
                    action: 'rejectWorkflowRequest',
                    documentUid: documentUid,
                    workflowStatusUid: workflowStatusUid,
                    userName: userName,
                    userSource: userSource,
                    statusDate: statusDate,
                    comment: comment
                },
                function (form, action) {
                    kimios.Info.msg(kimios.lang('Workflow'), kimios.lang('WorkflowRequestRejected'));
                    kimios.explorer.getViewport().refreshGrids();
                    kimios.explorer.getTasksPanel().refresh();
                }
            );

        }
    }

};
