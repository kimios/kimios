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
kimios.explorer.Toolbar = Ext.extend(Ext.Toolbar, {
    constructor: function (config) {
        this.id = 'kimios-toolbar';
        this.toolsMenu = new kimios.menu.ToolsMenu({
            hidden: true
        });

        this.advancedSearchButton = new Ext.Button({
            text: kimios.lang('SearchButton'),
            iconCls: 'search',
            handler: function(){

                /*var panEdit = new kimios.search.AdvancedSearchPanel({
                    hidden: false,
                    border: false,
                    title: 'New Search'
                });*/
                var windowSearch = new kimios.search.AdvancedSearchPanel({
                    title: 'New Search',
                    listeners: {
                        close: function(){

                            if(windowSearch.shouldUpdateRights){

                            }
                        }
                    }
                });
                //panEdit.loadForm(this.searchRequest)
                windowSearch.on('reqlaunched', function(){
                    windowSearch.close();
                });
                windowSearch.on('reqreload', function(){
                    windowSearch.close();
                });
                windowSearch.show();

            }
            /*,
            toggleHandler: function (b, s) {


                var vp = kimios.explorer.getActivePanel();

                if (vp == null || !vp.advancedSearchPanel) {
//                    this.toggle(false, true);
//                    return false;
                    vp = new kimios.explorer.DMEntityGridPanel({
                        emptyPanel: true
                    });
                    var centerPanel = Ext.getCmp('kimios-center-panel');
                    centerPanel.add(vp);
                    centerPanel.setActiveTab(vp);
                }
                if (s == true) {
                    vp.advancedSearchPanel.build();
                    vp.advancedSearchPanel.showPanel();

                    kimios.explorer.getViewport().westPanel.setActiveGroup(1);     // search requests panel
                    kimios.explorer.getViewport().westPanel.activeGroup.setActiveTab(0);

                } else {
                    vp.advancedSearchPanel.hidePanel();
                }

                if (vp.advancedSearchPanel.isVisible())
                    vp.advancedSearchPanel.search();
                else
                    vp.refresh();

                vp.searchToolbar.searchField.setValue('');

                vp.doLayout();
            }*/
        });

        this.cartButton = new Ext.Button({
            id: 'kimios-cart-button',
            text: kimios.lang('Cart'),
            iconCls: 'cart',
//            enableToggle: true,
            handler: function (b, s) {
                var vp = kimios.explorer.getViewport();
                vp.westPanel.setActiveGroup(4);     // cart panel
                vp.westPanel.activeGroup.setActiveTab(0);
            }
        });

        this.myTasksButton = new Ext.Button({
            text: kimios.lang('MyTasks'),
            iconCls: 'tasks',
            hidden: true,
            pending: 0,
            assigned: 0,
            handler: function () {
                var vp = kimios.explorer.getViewport();
                vp.westPanel.setActiveGroup(6);     // my tasks panel
                vp.westPanel.activeGroup.setActiveTab(0);
            },
            refresh: function (p, a) {
                if (p != undefined) this.pending = p;
                if (a != undefined) this.assigned = a;
                if (this.pending == 0 && this.assigned == 0)
                    this.setText(kimios.lang('MyTasks'));
                else if (this.pending == '?' || this.assigned == '?')
                    this.setText(kimios.lang('MyTasks') + ' (?)');
                else
                    this.setText(kimios.lang('MyTasks') + ' (' + (this.pending + this.assigned) + ')');
            }
        });


        this.logoutButton = new Ext.Button({
            tooltip: kimios.lang('Logout'),
            iconCls: 'exit',
            hidden: true,
            handler: function () {
                window.location.href = contextPath + '/../logout.jsp';
            }
        });

        this.loggedAsLabel = new Ext.form.DisplayField({
            id: 'kimios-logged-label',
            value: this.getLoggedAsString()
        });

        this.languageMenu = new kimios.menu.LanguageMenu();

        this.myAccountButton = new Ext.Button({
            text: kimios.lang('MyAccount'),
            iconCls: 'admin-user-tree-node',
            hidden: true,
            handler: function () {
                new Ext.Window({
                    id: 'kimios-my-account',
                    title: kimios.lang('MyAccount'),
                    iconCls: 'admin-user-tree-node',
                    closable: true,
                    border: true,
                    layout: 'fit',
                    maximizable: false,
                    modal: true,
                    width: 350,
                    height: 300,
                    items: [new kimios.MyAccountPanel({})]
                }).show();
            }
        });

        this.trashButton = new Ext.Button({
            text: kimios.lang('Trash'),
            iconCls: 'trash',
            hidden: true,
            handler: function () {
                new Ext.Window({
                    id: 'kimios-trash',
                    title: kimios.lang('Trash'),
                    iconCls: 'trash',
                    closable: true,
                    border: true,
                    layout: 'fit',
                    maximizable: false,
                    modal: true,
                    width: 350,
                    height: 300,
                    items: [new kimios.explorer.TrashPanel({})]
                }).show();
            }
        });

        kimios.explorer.Toolbar.superclass.constructor.call(this, config);
    },

    initComponent: function () {
        kimios.explorer.Toolbar.superclass.initComponent.apply(this, arguments);
        this.add('->');
        this.add(this.loggedAsLabel);
        this.add(' ');
        this.add(' ');

        this.add(this.languageMenu);
        this.add(' ');
        this.add(' ');
        this.add(' ');
        this.add(' ');
        this.add(' ');
        this.add(' ');
        this.add(' ');

        var buttonsArray = [];
        buttonsArray.push(this.trashButton);
        buttonsArray.push(this.advancedSearchButton);
        buttonsArray.push(this.cartButton);
        buttonsArray.push(this.myTasksButton);
        buttonsArray.push(this.myAccountButton);
        buttonsArray.push(this.toolsMenu);
        buttonsArray.push('-');
        buttonsArray.push(this.logoutButton);
//        this.add(new Ext.ButtonGroup({
//            columns: buttonsArray.length,
//            items: buttonsArray
//        }));
        this.add(buttonsArray);


        var domainsListStore = kimios.store.AdminStore.getDomainsStore();
        domainsListStore.load({
            scope: this,
            callback: function (r) {
                for (var i = 0; i < r.length; i++) {
                    if (currentSource == r[i].data.name) {
                        kimios.setImplPackage(r[i].data.className);
                        break;
                    }
                }
                var isVisible = kimios.getImplPackage() == 'org.kimios.kernel.user.impl.HAuthenticationSource';
                this.myTasksButton.setVisible(true);
                this.trashButton.setVisible(kimios.explorer.getViewport().rights.isAdmin);
                this.myAccountButton.setVisible(isVisible);
                this.toolsMenu.setVisible(!Ext.getCmp('kimios-tools').simpleUser);
                this.logoutButton.setVisible(true);
                this.doLayout(); // IE bug fix
            }
        });
    },

    getLoggedAsString: function () {
        var html = '<span style="color:#333">' + kimios.lang('Welcome') + ', ';

        if (currentName != null && currentName != '' && currentName != 'null')
            html += currentName;
        else
            html += currentUser + '@' + currentSource;
        return html + '</span>';
    },

    refreshLanguage: function (lg) {
        this.languageMenu.refreshLanguage();
        this.toolsMenu.refreshLanguage();
        this.advancedSearchButton.setText(kimios.lang('SearchButton'));
        this.myTasksButton.setText(kimios.lang('MyTasks'));
        this.myAccountButton.setText(kimios.lang('MyAccount'));
        this.logoutButton.setTooltip(kimios.lang('Logout'));
        this.loggedAsLabel.setValue(lg != undefined ? lg : this.getLoggedAsString());
        this.trashButton.setText(kimios.lang('Trash'));
        this.doLayout();
    },

    refresh: function () {
        this.toolsMenu.refresh();
        this.doLayout();
    }
});


