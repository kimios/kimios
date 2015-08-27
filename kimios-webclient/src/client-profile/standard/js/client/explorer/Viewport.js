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
kimios.explorer.Viewport = Ext.extend(Ext.Viewport, {

    constructor: function (config) {
        this.id = 'kimios-viewport';
        this.renderTo = 'body';
        this.layout = 'border';
        this.forceLayout = true;
        this.i18n = config.i18n;
        this.checkSession = config.checkSession;
        this.bonita = true;
        this.bonitaAlreadyCheck = false;

        this.topContainer = new Ext.Panel({
            border: false,
            region: 'north',
            layout: 'fit',
            height: 64
        });

        this.mainContainer = new Ext.Panel({
            border: false,
            region: 'center',
            layout: 'fit'
        });

        this.items = [this.topContainer, this.mainContainer];
        kimios.explorer.Viewport.superclass.constructor.call(this, config);
    },

    executeAfterBuild: function () {
        if (clientConfig.defaultdocumenttype) {
            loadAddonCols(this.newTab);
        } else {
            if (this.afterBuild) {
                this.afterBuild();

            }
            else {


                this.newTab();
            }
        }


    },

    initComponent: function () {


        kimios.explorer.Viewport.superclass.initComponent.apply(this, arguments);
        Ext.state.Manager.setProvider(new Ext.state.CookieProvider({
            expires: new Date(new Date().getTime() + (1000 * 60 * 60 * 24 * 30)) //7 days from now
        }));
        this.mask = new kimios.LoadMask(Ext.getBody());
        kimios.mask();

        this.rightsStore = kimios.store.getRightsStore();

        this.rightsStore.load({
            scope: this,
            callback: function (records, options, success) {
                this.rights = new kimios.security.Rights({
                    isWorkspaceCreator: records[0].data.canCreateWorkspace,
                    isAdmin: records[0].data.isAdmin,
                    isStudioUser: records[0].data.isStudioUser,
                    isRulesUser: records[0].data.isRulesUser,
                    isReportingUser: records[0].data.isReportingUser
                });
                kimios.ContextMenu.init();
                this.explorerPanel = new kimios.explorer.ExplorerPanel({
                    border: false,
                    rootText: 'Kimios'
                });
                this.bookmarksPanel = new kimios.explorer.BookmarksPanel({
                    border: false
                });
                this.recentItemsPanel = new kimios.explorer.RecentItemsPanel({
                    border: false
                });





                var myQueriesPanel = new kimios.explorer.SearchQueryPanel({
                    id: 'kimios-queries-panel',
                    queriesLoadAction : 'ListMyQueries',
                    titleKey: 'MySearchQueries',
                    border: false
                });
                var publishedQueriesPanel = new kimios.explorer.SearchQueryPanel({
                    id: 'kimios-queries-panel2',
                    queriesLoadAction : 'ListPublishedQueries',
                    titleKey: 'PublishedQueries',
                    border: false
                });

                var commonQueriesPanel = new kimios.explorer.SearchQueryPanel({
                    id: 'kimios-queries-panel3',
                    queriesLoadAction : 'ListPublicQueries',
                    titleKey: 'PublicQueries',
                    border: false
                });

                this.searchBookmarkPanel = new Ext.TabPanel({
                        id: 'searchQueriesPanel',
                        title: kimios.lang('SearchTab'),
                        activeTab: 0,
                        pending: 0,
                        assigned: 0,
                        items: [
                            myQueriesPanel,
                            publishedQueriesPanel,
                            commonQueriesPanel
                        ],
                        refresh: function (p, a) {
                        }
                    }
                );


                this.kimiosTasksPanel = new kimios.tasks.TasksPanel({});
                this.tasksPanel = new kimios.tasks.BonitaTasksPanel({});
                this.tasksAssignedPanel = new kimios.tasks.BonitaAssignedTasksPanel({});

                var tasksPendingPanel = this.tasksPanel;
                var tasksAssignedPanel = this.tasksAssignedPanel;

                this.cartPanel = new kimios.explorer.Cart({});

                // west

                var groupPanelItems = [{
                    items: [this.explorerPanel]
                },
                    {
                        items: [this.searchBookmarkPanel]
                    },
                    {
                        items: [this.bookmarksPanel]
                    },
                    {
                        items: [this.recentItemsPanel]
                    },
                    {
                        items: [this.cartPanel]
                    }
                ];
                if (bonitaEnabled) {
                    groupPanelItems.push({
                        hidden: true,
                        visible: false,
                        border: false,
                        items: [
                            new Ext.TabPanel({
                                id: 'bonitaTabPanelId',
                                title: kimios.lang('MyTasks'),
                                activeTab: 0,
                                pending: 0,
                                assigned: 0,
                                items: [
                                    tasksPendingPanel,
                                    tasksAssignedPanel
                                ],
                                refresh: function (p, a) {
                                    if (p != undefined)
                                        this.pending = p;
                                    if (a != undefined)
                                        this.assigned = a;

                                    if (this.pending == 0 && this.assigned == 0) {
                                        this.setTitle(kimios.lang('MyTasks'));
                                    }

                                    else {
                                        var pending = '';
                                        var assigned = '';
                                        if (this.pending == '?') {
                                            pending = '<span style="color:gray;text-decoration: line-through;">' +
                                            this.pending + ' ' + kimios.lang('BonitaPendingTasks') + '</span>';
                                        } else {
                                            pending = this.pending + ' ' + kimios.lang('BonitaPendingTasks');
                                        }

                                        if (this.assigned == '?') {
                                            assigned = '<span style="color:gray;text-decoration: line-through;">' +
                                            this.assigned + ' ' + kimios.lang('BonitaAssignedTasks') + '</span>';
                                        } else {
                                            assigned = this.assigned + ' ' + kimios.lang('BonitaAssignedTasks');
                                        }

                                        var total = 0;
                                        if (this.pending == '?' || this.assigned == '?') {
                                            total = '?';
                                        } else {
                                            total = (this.pending + this.assigned);
                                        }
                                        if (this.pending == '?' && this.assigned == '?') {
                                            this.setTitle('<span style="color:gray;text-decoration: line-through;">' +
                                            kimios.lang('MyTasks') + '</span>');
                                        } else {
                                            this.setTitle(kimios.lang('MyTasks') + ' (' + total + ')<br/>' +
                                            '<div style="font-size:.9em;font-weight:normal;"> ' +
                                            pending + '<br/>' +
                                            assigned + '</div>');
                                        }
                                    }
                                }
                            })
                        ]
                    });
                } else {

                    groupPanelItems.push({
                        items: [this.kimiosTasksPanel]
                    });
                }

                this.westPanel = new Ext.ux.GroupTabPanel({
                    id: 'kimios-west-container',
                    region: 'west',
                    width: 330,
                    split: true,
                    layoutConfig: {
                        titleCollapse: true,
                        animate: false
                    },
                    activeGroup: 0,
                    minWidth: 180,
                    border: true,
                    items: groupPanelItems,
                    margins: '5 0 5 5',
                    listeners: {
                        scope: this,
                        expand: function (p) {
                            this.doLayout();
                        },
                        collapse: function (p) {
                            this.doLayout();
                        }
                    }
                });

                // center
                this.centerPanel = new Ext.Panel({
                    border: false,
                    region: 'center',
                    layout: 'border',
                    margins: '5 5 5 0',
                    items: [
                        new Ext.TabPanel({
                            //                  // TODO Drag'n Drop Across Tab Panel
                            //                  initEvents : function(){
                            //                    Ext.TabPanel.superclass.initEvents.call(this);
                            //                    this.strip.on('mouseover', this.onMouseOver, this);
                            //                  },
                            //                  onMouseOver : function(e){
                            ////                    e.preventDefault();
                            //                    var t = this.findTargets(e);
                            //                    if(t.item && t.item != this.activeTab){
                            //                      this.setActiveTab(t.item);
                            //                    }
                            //                  },
                            id: 'kimios-center-panel',
                            region: 'center',
                            enableTabScroll: true,
                            plain: true,
                            border: true,
                            listeners: {
                                scope: this,
                                tabchange: function (panel, tab) {
                                    var panel = kimios.explorer.getActivePanel();
                                    /*if (panel && panel instanceof kimios.explorer.DMEntityGridPanel) {
                                        kimios.explorer.getToolbar().advancedSearchButton.toggle(panel.advancedSearchPanel && panel.advancedSearchPanel.isVisible(), true);
                                    } else {
                                        kimios.explorer.getToolbar().advancedSearchButton.toggle(false, true);
                                    }*/
                                },
                                contextmenu: function (panel, tab, e) {
                                    new Ext.menu.Menu({
                                        items: [
                                            {
                                                text: kimios.lang('NewTab'),
                                                iconCls: 'role-workspace',
                                                scope: this,
                                                handler: function () {
                                                    this.newTab(tab.uid, tab.type);
                                                }
                                            },
                                            {
                                                text: kimios.lang('CloseTab'),
                                                iconCls: 'del-icon',
                                                handler: function () {
                                                    var centerPanel = Ext.getCmp('kimios-center-panel');
                                                    if (centerPanel.items.length > 1)
                                                        tab.destroy();
                                                }
                                            }
                                        ]
                                    }).showAt(e.getXY());
                                }
                            }
                        })
                    ]
                });

                //top
                this.topPanel = new Ext.Panel({
                    border: false,
                    layout: 'hbox',
                    items: [
                        new Ext.Panel({
                            border: false,
                            width: 280,
                            tbar: new Ext.Toolbar({
                                style: 'background-image: url(); background-color:#fff;padding:8px;',
                                height: this.topContainer.height + 1, // ie fix
                                items: ['<img style="padding:3px; height: 40px" src="' + srcContextPath + '/images/logo.png" border="0" align="absmiddle" alt="kimios" />']
                            })
                        }),
                        new Ext.Panel({
                            border: false,
                            flex: 1,
                            tbar: new kimios.explorer.Toolbar({
                                style: 'background-image: url(); background-color:#fff;',
                                height: this.topContainer.height + 1 // ie fix
                            })
                        })
                    ]
                });

                //main
                this.mainPanel = new Ext.Container({
                    layout: 'border',
//                    items: [this.westPanel, this.eastPanel, this.centerPanel]
                    items: [this.westPanel, this.centerPanel]
                });
                this.topContainer.add(this.topPanel);
                this.mainContainer.add(this.mainPanel);

                // layout fix
                this.doLayout();

                // open news tab
//                this.initNewsTab();

                // open default tab
//                this.newTab();

                this.executeAfterBuild();

                // start tasks checker thread (also used to check session)
                if (bonitaEnabled) {
                    this.tasksChecker = {
                        run: function () {
                            Ext.getCmp('kimios-tasks-panel').refresh();
                            Ext.getCmp('kimios-assigned-tasks-panel').refresh();
                        },
                        interval: (this.checkSession * 1000)
                    };
                    Ext.TaskMgr.start(this.tasksChecker);
                } else {
                    this.tasksChecker = {
                        run: function () {
                            Ext.getCmp('kimios-tasks-panel-legacy').refresh();
                        },
                        interval: (this.checkSession * 1000)
                    };
                    Ext.TaskMgr.start(this.tasksChecker);

                }


                kimios.unmask();
            }
        });


    },

    changeLanguage: function (lang) {
        kimios.store.getLangStore(lang).load({
            callback: function (records, options, success) {
                this.i18n = new kimios.i18n.Internationalization({
                    lang: lang,
                    records: records
                });
                this.mask = new kimios.LoadMask(Ext.getBody());
                kimios.explorer.getToolbar().refreshLanguage();
                kimios.explorer.getTreePanel().refreshLanguage();
                kimios.explorer.getSearchRequestsPanel().refreshLanguage();
                kimios.explorer.getBookmarksPanel().refreshLanguage();
                kimios.explorer.getRecentItemsPanel().refreshLanguage();
                Ext.getCmp('kimios-tasks-panel').refreshLanguage();
                Ext.getCmp('kimios-assigned-tasks-panel').refreshLanguage();
                kimios.explorer.getCartPanel().refreshLanguage();
                kimios.ContextMenu.init();
                var mainPanel = kimios.explorer.getMainPanel();
                for (var i = 0; i < mainPanel.items.length; i++)
                    mainPanel.items.get(i).refreshLanguage();
                this.doLayout();
                kimios.Cookies.setCookie('kimios-web-client-language', lang, 365);
            },
            scope: this
        });
    },

    refreshGrids: function () {
        var gridTabPanel = kimios.explorer.getMainPanel();
        for (var i = 0; i < gridTabPanel.items.length; i++) {
            var tab = gridTabPanel.items.get(i);

            // check instance and reload tab
            if (tab.loadable == true) {
                tab.loadEntity();
            }
        }
    },

    refresh: function () {

        kimios.explorer.getTreePanel().refresh();
        kimios.explorer.getToolbar().refresh();
        kimios.explorer.getTasksPanel().refresh();
        kimios.explorer.getViewport().bookmarksPanel.refresh();
        kimios.explorer.getViewport().recentItemsPanel.refresh();
        this.refreshGrids();
        this.doLayout();
    },

    newTab: function (uid, type) {
        kimios.explorer.addNewPanel(uid, type);
    }
});

