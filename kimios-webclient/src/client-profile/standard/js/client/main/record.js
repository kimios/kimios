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
kimios.record = {

    newsRecord: Ext.data.Record.create([
        {
            name: 'id',
            type: 'long'
        },
        {
            name: 'title',
            type: 'string'
        },
        {
            name: 'content',
            type: 'string'
        },
        {
            name: 'date'
        }
    ]),

    dmEntityRecord: Ext.data.Record.create([
        {
            name: 'ownerSource',
            type: 'string'
        },
        {
            name: 'creationDate',
            type: 'long'

        },
        {
            name: 'updateDate',
            type: 'long',
            sortDir: 'desc'
        },
        {
            name: 'lastVersionCreationDate',
            type: 'long'

        },
        {
            name: 'lastVersionUpdateDate',
            type: 'long'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'owner',
            type: 'string'
        },
        {
            name: 'uid',
            type: 'long'
        },
        {
            name: 'type',
            type: 'int'
        },
        {
            name: 'length',
            type: 'long'
        },
        {
            name: 'extension',
            type: 'string'
        },
        {
            name: 'checkedOut',
            type: 'boolean'
        },
        {
            name: 'checkoutDate',
            type: 'long'
        },
        {
            name: 'checkoutUser',
            type: 'string'
        },
        {
            name: 'checkoutUserSource',
            type: 'string'
        },
        {
            name: 'documentTypeName',
            type: 'string'
        },
        {
            name: 'documentTypeUid',
            type: 'long'
        },
        {
            name: 'outOfWorkflow',
            type: 'boolean'
        },
        {
            name: 'workflowStatusName',
            type: 'string'
        },
        {
            name: 'workflowStatusUid',
            type: 'long'
        },
        {
            name: 'path',
            type: 'string'
        },
        {
            name: 'parentType',
            type: 'int'
        },
        {
            name: 'parentUid',
            type: 'long'
        },
        {
            name: 'statusUserName',
            type: 'string'
        },
        {
            name: 'statusUserSource',
            type: 'string'
        },
        {
            name: 'statusDate',
            type: 'long'
        },
        {
            name: 'status',
            type: 'string'
        },
        {
            name: 'dmEntityAddonData',
            type: 'string'
        }
    ]),

    dmEntityVersionRecord: Ext.data.Record.create([
        {
            name: 'creationDate',
            type: 'string'
        },
        {
            name: 'documentTypeName',
            type: 'string'
        },
        {
            name: 'documentTypeUid',
            type: 'int'
        },
        {
            name: 'documentUid',
            type: 'int'
        },
        {
            name: 'length',
            type: 'int'
        },
        {
            name: 'modificationDate',
            type: 'string'
        },
        {
            name: 'owner',
            type: 'string'
        },
        {
            name: 'ownerSource',
            type: 'string'
        },
        {
            name: 'uid',
            type: 'int'
        }
    ]),

    securityEntityRecord: Ext.data.Record.create([
        {
            name: 'dmEntityType',
            type: 'int'
        },
        {
            name: 'dmEntityUid',
            type: 'int'
        },
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
        },
        {
            name: 'type',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'source',
            type: 'string'
        },
        {
            name: 'dmEntityType',
            type: 'int'
        }
    ]),

    bookmarksRecord: Ext.data.Record.create([
        {
            name: 'checkoutDate',
            type: 'string'
        },
        {
            name: 'extension',
            type: 'string'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'path',
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
            name: 'worklowStatusName',
            type: 'string'
        }
    ]),

    documentTypeRecord: Ext.data.Record.create([
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'uid',
            type: 'int'
        },
        {
            name: 'parentType',
            type: 'int'
        }
    ]),

    workflowRecord: Ext.data.Record.create([
        {
            name: 'uid',
            type: 'int'
        },
        {
            name: 'name',
            type: 'string'
        },
        {
            name: 'description',
            type: 'string'
        }
    ]),

    historyRecord: Ext.data.Record.create([
        {
            name: 'date',
            type: 'long'
        },
        {
            name: 'dmEntityType',
            type: 'int'
        },
        {
            name: 'dmEntityUid',
            type: 'long'
        },
        {
            name: 'operation',
            type: 'int'
        },
        {
            name: 'uid',
            type: 'long'
        },
        {
            name: 'user',
            type: 'string'
        },
        {
            name: 'userSource',
            type: 'string'
        }
    ]),

    AdminRecord: {

        domainsRecord: Ext.data.Record.create([
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'className',
                type: 'string'
            }
        ]),

        usersRecord: Ext.data.Record.create([
            {
                name: 'uid',
                type: 'string'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'source',
                type: 'string'
            },
            {
                name: 'mail',
                type: 'string'
            },
            {
                name: 'lastLogin'
            },
            {
                name: 'enabled',
                type: 'boolean'
            }
        ]),

        groupsRecord: Ext.data.Record.create([
            {
                name: 'gid',
                type: 'string'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'source',
                type: 'string'
            }
        ]),

        userGroupsRecord: Ext.data.Record.create([
            {
                name: 'gid',
                type: 'string'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'source',
                type: 'string'
            }
        ]),

        groupUsersRecord: Ext.data.Record.create([
            {
                name: 'uid',
                type: 'string'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'source',
                type: 'string'
            }
        ]),

        roleUsersRecord: Ext.data.Record.create([
            {
                name: 'userName',
                type: 'string'
            },
            {
                name: 'userSource',
                type: 'string'
            },
            {
                name: 'role'
            }
        ]),

        userRolesRecord: Ext.data.Record.create([
            {
                name: 'userName',
                type: 'string'
            },
            {
                name: 'userSource',
                type: 'string'
            },
            {
                name: 'role'
            }
        ]),

        deadLockRecord: Ext.data.Record.create([
            {
                name: 'uid',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'path',
                type: 'string'
            },
            {
                name: 'checkoutUser',
                type: 'string'
            },
            {
                name: 'checkoutUserSource',
                type: 'string'
            },
            {
                name: 'checkoutDate',
                type: 'string'
            }
        ])
    },

    ReportingRecord: {
        documentHitsRecord: Ext.data.Record.create([
            {
                name: 'documentName'
            },
            {
                name: 'hits'
            },
            {
                name: 'path'
            }
        ])
    },

    StudioRecord: {

        documentTypeRecord: Ext.data.Record.create([
            {
                name: 'uid'
            },
            {
                name: 'name'
            },
            {
                name: 'documentTypeUid'
            }
        ]),

        metaDataRecord: Ext.data.Record.create([
            {
                name: 'documentTypeUid'
            },
            {
                name: 'metaFeedUid'
            },
            {
                name: 'metaType'
            },
            {
                name: 'name'
            },
            {
                name: 'uid'
            },
            {
                name: 'mandatory',
                type: 'boolean'
            }
        ]),

        metaFeedRecord: Ext.data.Record.create([
            {
                name: 'uid'
            },
            {
                name: 'name'
            },
            {
                name: 'className'
            }
        ]),

        workflowsRecord: Ext.data.Record.create([
            {
                name: 'uid'
            },
            {
                name: 'name'
            },
            {
                name: 'description'
            }
        ]),

        workflowStatusRecord: Ext.data.Record.create([
            {
                name: 'uid'
            },
            {
                name: 'name'
            },
            {
                name: 'successorUid'
            },
            {
                name: 'position'
            }
        ]),

        workflowStatusManagerRecord: Ext.data.Record.create([
            {
                name: 'securityEntityName'
            },
            {
                name: 'securityEntitySource'
            },
            {
                name: 'securityEntityType'
            },
            {
                name: 'workflowStatusUid'
            }
        ])
    },
    BonitaRecord: {
        processRecord: Ext.data.Record.create([
            {
                name: 'id',
                type: 'long'
            },
            {
                name: 'processId',
                type: 'long'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'description',
                type: 'string'
            },
            {
                name: 'activationState',
                type: 'string'
            },
            {
                name: 'configurationState',
                type: 'string'
            },
            {
                name: 'deployedBy',
                type: 'long'
            },
            {
                name: 'deploymentDate',
                type: 'long'
            },
            {
                name: 'displayName',
                type: 'string'
            },
            {
                name: 'displayDescription',
                type: 'string'
            },
            {
                name: 'iconPath',
                type: 'string'
            },
            {
                name: 'lastUpdateDate',
                type: 'long'
            },
            {
                name: 'version',
                type: 'string'
            },
            {
                name: 'url',
                type: 'string'
            }
        ]),

        processInstanceRecord: Ext.data.Record.create([
            {
                name: 'id',
                type: 'long'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'calledId',
                type: 'long'
            },
            {
                name: 'description',
                type: 'string'
            },
            {
                name: 'endDate',
                type: 'long'
            },
            {
                name: 'lastUpdate',
                type: 'long'
            },
            {
                name: 'processDefinitionId',
                type: 'long'
            },
            {
                name: 'rootProcessInstanceId',
                type: 'long'
            },
            {
                name: 'startDate',
                type: 'long'
            },
            {
                name: 'startBy',
                type: 'long'
            },
            {
                name: 'state',
                type: 'string'
            },
            {
                name: 'stringIndex1',
                type: 'string'
            },
            {
                name: 'stringIndex2',
                type: 'string'
            },
            {
                name: 'stringIndex3',
                type: 'string'
            },
            {
                name: 'stringIndex4',
                type: 'string'
            },
            {
                name: 'stringIndex5',
                type: 'string'
            }
        ]),

        taskRecord: Ext.data.Record.create([
            {
                name: 'id',
                type: 'long'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'description',
                type: 'string'
            },
            {
                name: 'claimedDate',
                type: 'long'
            },
            {
                name: 'expectedEndDate',
                type: 'long'
            },
            {
                name: 'priority',
                type: 'string'
            },
            {
                name: 'displayDescription',
                type: 'string'
            },
            {
                name: 'displayName',
                type: 'string'
            },
            {
                name: 'executedBy',
                type: 'long'
            },
            {
                name: 'flownodeDefinitionId',
                type: 'long'
            },
            {
                name: 'lastUpdateDate',
                type: 'long'
            },
            {
                name: 'parentContainerId',
                type: 'long'
            },
            {
                name: 'parentProcessInstanceId',
                type: 'long'
            },
            {
                name: 'reachedStateDate',
                type: 'long'
            },
            {
                name: 'rootContainerId',
                type: 'long'
            },
            {
                name: 'state',
                type: 'string'
            },
            {
                name: 'stateCategory',
                type: 'string'
            },
            {
                name: 'type',
                type: 'string'
            },
            {
                name: 'processDefinitionId',
                type: 'long'
            },
            {
                name: 'url',
                type: 'string'
            },
            {
                name: 'actor' // UserWrapper
            },
            {
                name: 'assignee' // UserWrapper
            },
            {
                name: 'processWrapper' // ProcessWrapper
            },
            {
                name: 'commentWrappers' // List<CommentWrapper>
            }
        ]),

        commentRecord: Ext.data.Record.create([
            {
                name: 'id',
                type: 'long'
            },
            {
                name: 'content',
                type: 'string'
            },
            {
                name: 'postDate',
                type: 'long'
            },
            {
                name: 'processInstanceId',
                type: 'long'
            },
            {
                name: 'tenantId',
                type: 'long'
            },
            {
                name: 'userWrapper' // UserWrapper
            }
        ])

    },
    TasksRecord: {
        taskRecord: Ext.data.Record.create([
            {
                name: 'uid',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'workflowStatusUid',
                type: 'int'
            },
            {
                name: 'workflowStatusName',
                type: 'string'
            },
            {
                name: 'statusUserName',
                type: 'string'
            },
            {
                name: 'statusUserSource',
                type: 'string'
            },
            {
                name: 'statusDate',
                type: 'long'
            },
            {
                name: 'status',
                type: 'string'
            },
            {
                name: 'extension',
                type: 'string'
            },
            {
                name: 'type',
                type: 'int'
            }
        ])
    },
    SearchRecord: {
        queryRecord: Ext.data.Record.create([
            {
                name: 'id',
                type: 'int'
            },
            {
                name: 'name',
                type: 'string'
            },
            {
                name: 'owner',
                type: 'string'
            },
            {
                name: 'ownerSource',
                type: 'string'
            },
            {
                name:'criteriasListJson',
                type:'string'
            },{
                name: 'virtualTree',
                type: 'boolean'
            }
        ]) ,
        virtualEntityRecord: Ext.data.Record.create([
            {
                name:'uid',
                type:'int'
            }, {
                name: 'type',
                type: 'int'
            },
            {
                name:'name',
                type:'string'
            },
            {
                name:'virtualPath',
                type:'string'
            },
            {
                name:'virtualFolderCount',
                type:'string'
            }
        ])
    }

};
