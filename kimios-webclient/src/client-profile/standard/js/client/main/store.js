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
DmsJsonStore = Ext.extend(Ext.data.JsonStore, {
    constructor: function (config) {
        if (config.normalMode == undefined)
            config.normalMode = false;
        if (config && config.url && config.normalMode == false) {
            config.url = getBackEndUrl(config.url);
        }
        var successHandler = null;
        var failureHandler = null;
        if (config.successHandler) this.successHandler = config.successHandler;
        if (config.failureHandler) this.failureHandler = config.failureHandler;
        DmsJsonStore.superclass.constructor.call(this, config);

        var cmp = this;
        cmp.addListener('exception', function (dp, type, action, opts, resp, arg) {
            var fHandler = function (resp) {
                kimios.MessageBox.exception({
                    exception: (resp.status != 0 ? 'HTTP error: ' + resp.status + ' ' : '') + resp.statusText,
                    stackTrace: resp.responseText
                });
            };
            var sHandler = function (resp) {
                if (resp.status == 200) {
                    if (cmp.successHandler && cmp.successHandler != null)
                        cmp.successHandler(resp);
                } else {
                    if (cmp.failureHandler && cmp.failureHandler != null)
                        cmp.failureHandler(resp);
                    else {
                        kimios.MessageBox.exception({
                            exception: resp.exception,
                            stackTrace: resp.trace
                        });
                    }
                }
            };

            // dms exception
            if (resp.status == 200) {
                if(opts.reader.jsonData){
                    kimios.MessageBox.exception({
                        exception: opts.reader.jsonData.exception,
                        stackTrace: opts.reader.jsonData.trace
                    });
                }
                else {
                    if(console) { console.log(resp) };
                    kimios.MessageBox.exception({
                        exception: arg
                    });
                }
            }

            // server exception
            else if (resp.raw) {
                if (resp.raw.exception == 'Error 01 : Invalid session') {
                    document.location.href = contextPath + '/../logout.jsp';
                } else {
                    kimios.MessageBox.exception({
                        exception: resp.raw.exception,
                        stackTrace: resp.raw.trace
                    });
                }
            }

            // client exception
            else {
                kimios.MessageBox.exception({
                    exception: resp.statusText,
                    stackTrace: resp.responseText
                });
            }
        });
    },

    initComponent: function (arg) {
        DmsJsonStore.superclass.initComponent.apply(this, arg);
    }
});

kimios.store = {

    getLangStore: function (lang) {
        return new DmsJsonStore({
            url: 'Lang',
            fields: [
                {
                    name: 'label',
                    type: 'string'
                },
                {
                    name: 'value',
                    type: 'string'
                }
            ],
            baseParams: {
                action: lang
            }
        });
    },

    getRightsStore: function () {
        return new DmsJsonStore({
            url: 'DmsSecurity',
            fields: [
                {
                    name: 'canCreateWorkspace',
                    type: 'boolean'
                },
                {
                    name: 'isAdmin',
                    type: 'boolean'
                },
                {
                    name: 'isStudioUser',
                    type: 'boolean'
                },
                {
                    name: 'isRulesUser',
                    type: 'boolean'
                },
                {
                    name: 'isReportingUser',
                    type: 'boolean'
                }
            ],
            baseParams: {
                action: 'stdRights'
            }
        });
    },

    getEntityStore: function (uid, type) {
        return new DmsJsonStore({
            url: 'DmsEntity',
            fields: kimios.record.dmEntityRecord,
            baseParams: {
                action: 'getEntity',
                dmEntityType: type,
                dmEntityUid: uid
            }
        });
    },

    getEntitiesStore: function () {
        return new DmsJsonStore({
            url: 'DmsEntity',
            root: 'list',
            fields: kimios.record.dmEntityRecord,
            baseParams: {
                action: 'getEntities'
            }
        });
    },

    getTrashStore: function () {
        return new DmsJsonStore({
            url: 'Version',
            fields: kimios.record.dmEntityRecord,
            baseParams: {
                action: 'viewTrash'
            }
        });
    },

    getQuickSearchStore: function (searchParams) {

        var baseParams = searchParams;
        baseParams.action = 'Quick';
        return new DmsJsonStore({
            url: 'Search',
            root: 'list',
            totalProperty: 'total',
            idProperty: 'uid',
            remoteSort: true,
            fields: kimios.record.dmEntityRecord,
            baseParams: baseParams
        });
    },

    getAdvancedSearchStore: function (searchParams) {
        var baseParams = searchParams;
        baseParams.action = 'Advanced';
        return new DmsJsonStore({
            url: 'Search',
            remoteSort: true,
            root: 'list',
            totalProperty: 'total',
            idProperty: 'uid',
            fields: kimios.record.dmEntityRecord,
            baseParams: baseParams
        });
    },
    getQueriesStore: function () {
        var baseParams = {};
        baseParams.action = 'ListQueries';
        return new DmsJsonStore({
            url: 'Search',
            idProperty: 'id',
            fields: kimios.record.SearchRecord.queryRecord,
            baseParams: baseParams
        });
    },
    getSavedQueryExecStore: function (searchParams) {
        var baseParams = searchParams;
        baseParams.action = 'ExecuteSaved';
        return new DmsJsonStore({
            url: 'Search',
            remoteSort: true,
            root: 'list',
            totalProperty: 'total',
            idProperty: 'uid',
            fields: kimios.record.dmEntityRecord,
            baseParams: baseParams
        });
    },

    getContactsStore: function (searchParams) {
        var baseParams = searchParams;
        baseParams.action = 'contacts';
        return new DmsJsonStore({
            url: 'Share',
            idProperty: 'emailAddress',
            fields: kimios.record.mailContactRecord,
            baseParams: baseParams
        });
    },

    getNodeSecurityStore: function (uid, type) {
        return new DmsJsonStore({
            url: 'DmsSecurity',
            baseParams: {
                action: 'nodeSecurity',
                dmEntityType: type,
                dmEntityUid: uid
            },
            fields: [
                {
                    name: 'read',
                    type: 'boolean'
                },
                {
                    name: 'write',
                    type: 'boolean'
                },
                {
                    name: 'fullAccess',
                    type: 'boolean'
                }
            ]
        });
    },

    getLastVersionStore: function (documentUid) {
        return new DmsJsonStore({
            url: 'Version',
            baseParams: {
                action: 'lastVersion',
                documentUid: documentUid
            },
            fields: kimios.record.dmEntityVersionRecord
        });
    },

    getLastVersionStore: function (documentUid, hdl, scope) {
        return new DmsJsonStore({
            url: 'Version',
            baseParams: {
                action: 'lastVersion',
                documentUid: documentUid
            },
            successHandler: hdl,
            scope: scope,
            fields: kimios.record.dmEntityVersionRecord
        });
    },

    getMetaValuesStore: function (lastVersionUid) {
        return new DmsJsonStore({
            url: 'Version',
            root: 'metaValues',
            baseParams: {
                action: 'metaValues',
                versionUid: lastVersionUid
            },
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'value',
                    type: 'string'
                },
                {
                    name: 'type',
                    type: 'int'
                },
                {
                    name: 'uid',
                    type: 'int'
                },
                {
                    name: 'metaFeedUid',
                    type: 'int'
                },
                {
                    name: 'mandatory',
                    type: 'boolean'
                }
            ]
        });
    },
    getVirtualTreeCaller: function(searchParams, virtualStore, entitiesStore, callback){
        var baseParams = searchParams;
        baseParams.action = 'ExecuteSaved';
        baseParams.start = 0;
        baseParams.limit = 10;

        kimios.ajaxRequestWithAnswer('Search', baseParams, function(resp)
        {
            var searchRes = Ext.util.JSON.decode(resp.responseText);
            virtualStore.loadData(searchRes);
            entitiesStore.loadData(searchRes);
            if(callback){
                callback(searchRes);
            }
        });

    },
    getVirtualEntityStore : function(searchParams){
        var baseParams = searchParams;
        baseParams.action = 'ExecuteSaved';
        return new DmsJsonStore( {
             url : 'Search',
             root: 'virtualTreeRows',
             idProperty: 'uid',
             fields: kimios.record.SearchRecord.virtualEntityRecord,
             baseParams : baseParams
        });
    },

    getLastMetaValuesStore: function (uid) {
        return new DmsJsonStore({
            url: 'Version',
            root: 'lastMetaValues',
            baseParams: {
                action: 'lastMetaValues',
                uid: uid
            },
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'value',
                    type: 'string'
                },
                {
                    name: 'type',
                    type: 'int'
                },
                {
                    name: 'uid',
                    type: 'int'
                },
                {
                    name: 'metaFeedUid',
                    type: 'int'
                },
                {
                    name: 'mandatory',
                    type: 'boolean'
                }
            ]
        });
    },

    getMetasStore: function (documentTypeUid) {
        return new DmsJsonStore({
            url: 'DmsMeta',
            baseParams: {
                action: 'metas',
                documentTypeUid: documentTypeUid
            },
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'value',
                    type: 'string'
                },
                {
                    name: 'type',
                    type: 'int'
                },
                {
                    name: 'uid',
                    type: 'int'
                },
                {
                    name: 'metaFeedUid',
                    type: 'int'
                },
                {
                    name: 'mandatory',
                    type: 'boolean'
                }
            ],
            autoLoad: true
        });
    },

    getLastDocumentWorkflowStatusStore: function (documentUid) {
        return new DmsJsonStore({
            url: 'Workflow',
            baseParams: {
                action: 'getLastDocumentWorkflowStatus',
                documentUid: documentUid
            }
        });
    },

    getWorkflowStatusStore: function (workflowStatusUid) {
        return new DmsJsonStore({
            url: 'Workflow',
            baseParams: {
                action: 'getWorkflowStatus',
                workflowStatusUid: workflowStatusUid
            },
            fields: [
                {
                    name: 'name',
                    type: 'string'
                },
                {
                    name: 'successorUid',
                    type: 'long'
                },
                {
                    name: 'uid',
                    type: 'long'
                },
                {
                    name: 'workflowUid',
                    type: 'long'
                }
            ]
        });
    },

    getWorkflowStatusRequestsStore: function (documentUid) {
        return new DmsJsonStore({
            url: 'Workflow',
            baseParams: {
                action: 'getWorkflowStatusRequests',
                documentUid: documentUid
            },
            sortInfo: {
                field: 'date',
                direction: 'DESC'
            },
            fields: [
                {
                    name: 'comment',
                    type: 'string'
                },
                {
                    name: 'date',
                    type: 'long'
                },
                {
                    name: 'documentUid',
                    type: 'int'
                },
                {
                    name: 'status',
                    type: 'string'
                },
                {
                    name: 'userName',
                    type: 'string'
                },
                {
                    name: 'userSource',
                    type: 'string'
                },
                {
                    name: 'validationDate',
                    type: 'long'
                },
                {
                    name: 'validatorUserName',
                    type: 'string'
                },
                {
                    name: 'validatorUserSource',
                    type: 'string'
                },
                {
                    name: 'workflowStatusUid',
                    type: 'int'
                }
            ]
        });
    },

    AdminStore: {

        getDomainsStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'domains'
                },
                fields: kimios.record.AdminRecord.domainsRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getDomainTypesStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'domainType'
                },
                root: 'impl',
                fields: [
                    'name',
                    'className'
                ],
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getDomainFieldsStore: function (className) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'domainTypeFields',
                    className: className
                },
                root: 'fields',
                fields: ['name'],
                autoLoad: true,
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getUsersStore: function (source, autoLoad) {
            return new DmsJsonStore({
                url: 'DmsSecurity',
                baseParams: {
                    action: 'getUsers',
                    sourceUid: (source ? source : -1)
                },
                fields: kimios.record.AdminRecord.usersRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'uid',
                    direction: 'ASC'
                }
            });
        },

        getGroupsStore: function (source, autoLoad) {
            return new DmsJsonStore({
                url: 'DmsSecurity',
                baseParams: {
                    action: 'getGroups',
                    sourceUid: (source ? source : -1)
                },
                fields: kimios.record.AdminRecord.groupsRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'gid',
                    direction: 'ASC'
                }
            });
        },

        getUserGroupsStore: function (uid, source, autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getUsersGroups',
                    uid: uid,
                    authenticationSourceName: source
                },
                fields: kimios.record.AdminRecord.userGroupsRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'gid',
                    direction: 'ASC'
                }
            });
        },

        getGroupUsersStore: function (gid, source, autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getGroupsUsers',
                    gid: gid,
                    authenticationSourceName: source
                },
                fields: kimios.record.AdminRecord.groupUsersRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'uid',
                    direction: 'ASC'
                }
            });
        },

        getRoleUsersStore: function (roleId) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'roles',
                    roleId: roleId
                },
                fields: kimios.record.AdminRecord.roleUsersRecord,
                root: 'list',
                autoload: false,
                sortInfo: {
                    field: 'userName',
                    direction: 'ASC'
                }
            });
        },

        getUserRolesStore: function (uid, source, autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'userRoles',
                    userName: uid,
                    userSource: source
                },
                fields: kimios.record.AdminRecord.userRolesRecord,
                root: 'list',
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'userName',
                    direction: 'ASC'
                }
            });
        },

        getConnectedUsersStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getConnectedUsers'
                },
                fields: [
                    {
                        name: 'uid',
                        type: 'string'
                    },
                    {
                        name: 'source',
                        type: 'string'
                    }
                ],
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'uid',
                    direction: 'ASC'
                }
            });
        },

        getSessionsStore: function (record, autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getSessions',
                    userName: record.data.uid,
                    userSource: record.data.source
                },
                fields: [
                    {
                        name: 'metaDatas',
                        type: 'string'
                    },
                    {
                        name: 'sessionUid',
                        type: 'string'
                    },
                    {
                        name: 'userName',
                        type: 'string'
                    },
                    {
                        name: 'userSource',
                        type: 'string'
                    },
                    {
                        name: 'lastUse',
                        type: 'string'
                    }
                ],
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'lastUse',
                    direction: 'DESC'
                }
            });
        },

        getDeadLockStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getDeadLock'
                },
                fields: kimios.record.AdminRecord.deadLockRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'checkoutDate',
                    direction: 'DESC'
                }
            });
        },

        getReindexProgress: function () {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'getReindexProgress'
                },
                fields: ['percent'],
                autoLoad: false
            });
        },

        getLoggerStore: function () {
            return new DmsJsonStore({
                url: 'Admin',
                baseParams: {
                    action: 'listLoggers'
                },
                fields: ['loggerName', 'loggerLevel'],
                autoLoad: true
            });
        }
    },

    ReportingStore: {

        getAttributesStore: function (className) {
            return new DmsJsonStore({
                url: 'Reporting',
                baseParams: {
                    action: 'getAttributes',
                    impl: className
                },
                fields: [
                    {
                        name: 'name',
                        type: 'string'
                    },
                    {
                        name: 'type',
                        type: 'string' //javaClass
                    }
                ]
            });
        },

        getReportsList: function () {

            return new DmsJsonStore({
                url: 'Reporting',
                baseParams: {
                    action: 'getReportsList'
                },
                fields: [
                    {
                        name: 'name',
                        type: 'string'
                    },
                    {
                        name: 'className',
                        type: 'string'
                    }
                ],
                autoLoad: true,
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        }
    },

    StudioStore: {

        getDocumentTypesStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'documentTypes'
                },
                fields: kimios.record.StudioRecord.documentTypeRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getMetaFeedsStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'metaFeeds'
                },
                fields: kimios.record.StudioRecord.metaFeedRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getMetaFeedValuesStore: function (uid, autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'GetMetaFeedValues',
                    uid: (uid ? uid : -1)
                },
                fields: [
                    {
                        name: 'value',
                        type: 'string'
                    }
                ],
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'value',
                    direction: 'ASC'
                }
            });
        },

        getAvailableMetaFeedsStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'AvailableMetaFeeds'
                },
                fields: [
                    {
                        name: 'className'
                    }
                ],
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'className',
                    direction: 'ASC'
                }
            });
        },

        getUnheritedMetasStore: function (documentTypeRecord) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'getUnheritedMetas',
                    documentTypeUid: (documentTypeRecord ? documentTypeRecord.data.uid : -1)
                },
                fields: kimios.record.StudioRecord.metaDataRecord,
                autoLoad: (documentTypeRecord ? true : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getWorkflowsStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'workflows'
                },
                fields: kimios.record.StudioRecord.workflowsRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getProcessesStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'processes'
                },
                fields: kimios.record.BonitaRecord.processRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'name',
                    direction: 'ASC'
                }
            });
        },

        getWorkflowStatusStore: function (workflowRecord, autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'GetWorkflowStatus',
                    workflowUid: (workflowRecord ? workflowRecord.data.uid : -1)
                },
                fields: kimios.record.StudioRecord.workflowStatusRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'position',
                    direction: 'ASC'
                }
            });

        },

        getWorkflowStatusManagerStore: function (statusUid, autoLoad) {
            return new DmsJsonStore({
                url: 'Studio',
                baseParams: {
                    action: 'GetWorkflowStatusManagers',
                    statusUid: (statusUid ? statusUid : -1)
                },
                fields: kimios.record.StudioRecord.workflowStatusManagerRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'securityEntityName',
                    direction: 'ASC'
                }
            });
        },

        getAllStatus: function () {
            var store = new Ext.data.Store({
                proxy: new Ext.data.MemoryProxy(),
                reader: new Ext.data.JsonReader({}, kimios.record.StudioRecord.workflowStatusRecord),
                fields: kimios.record.StudioRecord.workflowStatusRecord,
                sortInfo: {
                    field: 'position',
                    direction: 'ASC'
                }
            });
            return store;
        },

        getAllStatusManager: function (data) {
            var store = new Ext.data.Store({
                proxy: new Ext.data.MemoryProxy(),
                reader: new Ext.data.JsonReader({}, kimios.record.StudioRecord.workflowStatusManagerRecord),
                fields: kimios.record.StudioRecord.workflowStatusRecord
            });
            if (data)
                store.loadData(data);
            return store;
        }
    },

    TasksStore: {
        getMyTasksStore: function (autoLoad) {
            return new DmsJsonStore({
                url: 'Workflow',
                baseParams: {
                    action: 'getMyTasks'
                },
                fields: kimios.record.dmEntityRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'statusDate',
                    direction: 'DESC'
                }
            });
        },

        getBonitaTasksByInstanceStore: function (autoLoad, pageSize) {
            return new Ext.data.JsonStore({
                url: getBackEndUrl('Workflow'),
                baseParams: {
                    action: 'getBonitaTasksByInstance',
                    start: 0,
                    limit: pageSize > 0 ? pageSize : 2147483647
                },
                root: 'tasks',
                totalProperty: 'totalProperty',
                fields: kimios.record.BonitaRecord.taskRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'expectedEndDate',
                    direction: 'DESC'
                }
            });
        },

        getBonitaPendingTasksStore: function (autoLoad, pageSize) {
            return new Ext.data.JsonStore({
                url: getBackEndUrl('Workflow'),
                baseParams: {
                    action: 'getBonitaPendingTasks',
                    start: 0,
                    limit: pageSize > 0 ? pageSize : 2147483647
                },
                root: 'tasks',
                totalProperty: 'totalProperty',
                fields: kimios.record.BonitaRecord.taskRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'expectedEndDate',
                    direction: 'DESC'
                }
            });
        },
        getBonitaAssignedTasksStore: function (autoLoad, pageSize) {
            return new Ext.data.JsonStore({
                url: getBackEndUrl('Workflow'),
                baseParams: {
                    action: 'getBonitaAssignedTasks',
                    start: 0,
                    limit: pageSize > 0 ? pageSize : 2147483647
                },
                root: 'tasks',
                totalProperty: 'totalProperty',
                fields: kimios.record.BonitaRecord.taskRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'expectedEndDate',
                    direction: 'DESC'
                }
            });
        },
        getBonitaCommentsStore: function (autoLoad, taskId) {
            return new DmsJsonStore({
                url: 'Workflow',
                baseParams: {
                    action: 'getComments',
                    taskId: taskId
                },
                fields: kimios.record.BonitaRecord.commentRecord,
                autoLoad: (autoLoad ? autoLoad : false),
                sortInfo: {
                    field: 'id',
                    direction: 'DESC'
                }
            });
        }

    }
};
