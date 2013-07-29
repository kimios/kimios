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
kimios.properties.PropertiesPanel = Ext.extend(Ext.TabPanel, {
    getWindow: function () {
        return this.window;
    },
    constructor: function (config) {
        this.window = config.window;
        this.dmEntityPojo = config.dmEntityPojo;

        // when creation mode is enabled
        this.createMode = config.createMode == undefined ? false : config.createMode;

        // when multiple items are selected
        this.multipleMode = this.dmEntityPojo instanceof Array ? true : false;

        // set to true when getting old versions
        this.versionsMode = config.versionsMode == undefined ? false : config.versionsMode;

        // set to true if current user is owner
        this.ownerMode = this.dmEntityPojo.owner == currentUser && this.dmEntityPojo.ownerSource == currentSource;

        // set to true when meta data change
        this.newVersion = false;

        // default config
        this.closable = true;
        this.iconCls = (!(this.dmEntityPojo instanceof Array) ? 'doctype' : 'values');
        this.enableTabScroll = true;
        this.bodyStyle = 'background-color:transparent;';
        this.border = false;

        this.saveButton = new Ext.Button({
            text: kimios.lang('Save'),
            scope: this,
            hidden: true,
            handler: function (btn) {
//                btn.setDisabled(true);
                if (this.multipleMode == false) {
                    var entityName = this.dmEntityPanel.nameField.getValue();
                    if (entityName == null || entityName == '') {
                        kimios.MessageBox.exception({
                            exception: kimios.lang('DocumentNameEmptyJS')
                        });
                        return;
                    }
                }

                var saveEntity = function (propertiesPanel) {
                    // create
                    if (propertiesPanel.createMode == true) {
                        propertiesPanel.createDMEntity();
                    }
                    // if single update, force tabs to get last values
                    else if (propertiesPanel.multipleMode == false) {
                        propertiesPanel.forceLoad();
                    }
                    // multiple update
                    else {
                        if (propertiesPanel.securityEntityPanel.isEmpty() == true) {
                            Ext.MessageBox.confirm(
                                kimios.lang('SecurityEntities'),
                                'No specified security entities. Click <span style="font-weight:bold;">"yes" to ERASE</span> or "no" to keep last values.',
                                function (btn) {
                                    if (btn == 'yes') {
                                        propertiesPanel.updateDMEntities(true);
                                    } else {
                                        propertiesPanel.updateDMEntities(false);
                                    }
                                }, this
                            );
                        } else {
                            propertiesPanel.updateDMEntities(true);
                        }
                    }
                };
                if (this.createMode == false && this.dmEntityPojo.type == 3 && this.metaDataPanel.changed == true) {
                    Ext.MessageBox.confirm(
                        kimios.lang('MetaData'),
                        kimios.lang('MetasChanged'),
                        function (btn) {
                            if (btn == 'yes')
                                this.newVersion = true;
                            saveEntity(this);
                        }, this
                    );
                } else {
                    saveEntity(this);
                }
            }
        });

        this.cancelButton = new Ext.Button({
            text: kimios.lang('Close'),
            scope: this,
            handler: function (btn) {
                btn.disable();
                if (this.window != undefined)
                    this.window.close();
            }
        });

        if (this.window == undefined) {
            this.bbar = ['->', this.saveButton];
        }
        else {
            this.buttonAlign = 'right';
            this.fbar = [this.saveButton, this.cancelButton];
        }

        kimios.properties.PropertiesPanel.superclass.constructor.call(this, config);
    },

    setPojo: function (pojo) {
        this.dmEntityPojo = pojo;
        this.setActiveTab(0);
        for (var i = 0; i < this.items.length; i++) {
            this.items.get(i).setPojo(pojo);
        }
    },

    forceLoad: function (i) {
        if (i == undefined) i = 0;
        var tab = this.items.get(i);

        // force load if required only
        // do not reload alreay loaded tab

        if (tab.loadingRequired == true && tab.loaded == false) {
            var prop = this;
            tab.forceLoad(function () {
                i++;
                if (i < prop.items.length)
                    prop.forceLoad(i);
                else
                    prop.updateDMEntity();
            });
        } else {
            i++;
            if (i < this.items.length)
                this.forceLoad(i);
            else
                this.updateDMEntity();
        }
    },

    initComponent: function () {
        kimios.properties.PropertiesPanel.superclass.initComponent.apply(this, arguments);

        var title = '';

        //create
        if (this.createMode == true) {
            title = kimios.lang('New');

            // add entity tab
            this.dmEntityPanel = new kimios.properties.DMEntityPanel({
                dmEntityPojo: this.dmEntityPojo,
                propertiesPanel: this
            });
            this.add(this.dmEntityPanel);

            // add meta data tab
            if (this.dmEntityPojo.type == 3) {
                this.metaDataPanel = new kimios.properties.MetaDataPanel({
                    dmEntityPojo: this.dmEntityPojo
                });
                this.add(this.metaDataPanel);
            }

            // add security tab
            this.securityEntityPanel = new kimios.properties.SecurityEntityPanel({
                dmEntityPojo: this.dmEntityPojo
            });
            this.add(this.securityEntityPanel);

            // allow inherited permissions if entity is folder or document
            if (this.dmEntityPojo.type == 2 || this.dmEntityPojo.type == 3)
                this.dmEntityPanel.inheritedPermissionsField.setValue(true);

            this.setActiveTab(0);
            this.saveButton.setVisible(true);
        }

        // update
        else {
            //get rights
            var store = kimios.store.getNodeSecurityStore(this.dmEntityPojo.uid, this.dmEntityPojo.type);
            store.on('load', function (store, records, options) {
                var read = records[0].data.read;
                var write = records[0].data.write;
                var fullAccess = records[0].data.fullAccess;

                if (write == true || fullAccess == true) {
                    this.saveButton.setVisible(true);
                }

                //simple update
                if (this.multipleMode == false) {
                    if (this.versionsMode == false) {
                        title = this.dmEntityPojo.name;

                        // add entity tab
                        this.dmEntityPanel = new kimios.properties.DMEntityPanel({
                            dmEntityPojo: this.dmEntityPojo,
                            propertiesPanel: this,
                            readOnly: write == false && fullAccess == false
                        });
                        this.add(this.dmEntityPanel);

                        // if only is document
                        if (this.dmEntityPojo.type == 3) {

                            // add meta data tab
                            this.metaDataPanel = new kimios.properties.MetaDataPanel({
                                dmEntityPojo: this.dmEntityPojo,
                                readOnly: write == false && fullAccess == false
                            });
                            this.add(this.metaDataPanel);

                            if (this.window != undefined) {
                                // add versions tab
                                this.versionsPanel = new kimios.properties.VersionsPanel({
                                    dmEntityPojo: this.dmEntityPojo,
                                    readOnly: write == false && fullAccess == false
                                });
                                this.add(this.versionsPanel);

                                // add workflow tab
                                this.workflowPanel = new kimios.properties.WorkflowPanel({
                                    dmEntityPojo: this.dmEntityPojo,
                                    readOnly: write == false && fullAccess == false
                                });
                                this.add(this.workflowPanel);

//                // add comments tab
//                this.commentsPanel = new kimios.properties.CommentsPanel({
//                  dmEntityPojo : this.dmEntityPojo,
//                  readOnly : write == false && fullAccess == false
//                });
//                this.add(this.commentsPanel);

                                // add related documents tab
                                this.relatedDocumentsPanel = new kimios.properties.RelatedDocumentsPanel({
                                    dmEntityPojo: this.dmEntityPojo,
                                    readOnly: write == false && fullAccess == false
                                });
                                this.add(this.relatedDocumentsPanel);

                                // add history tab
                                this.historyPanel = new kimios.properties.HistoryPanel({
                                    dmEntityPojo: this.dmEntityPojo
                                });
                                this.add(this.historyPanel);

                                // add security tab
                                if (this.ownerMode == true || fullAccess == true) {
                                    this.securityEntityPanel = new kimios.properties.SecurityEntityPanel({
                                        dmEntityPojo: this.dmEntityPojo
                                    });
                                    this.add(this.securityEntityPanel);
                                }
                            }
                        } else {
                            // add security tab
                            if (this.window != undefined) {
                                if (this.ownerMode == true || fullAccess == true) {
                                    this.securityEntityPanel = new kimios.properties.SecurityEntityPanel({
                                        dmEntityPojo: this.dmEntityPojo
                                    });
                                    this.add(this.securityEntityPanel);
                                }
                            }
                        }
                        this.saveButton.setVisible(true);
                    }

                    // special properties (from versions tab)
                    else {
                        // add meta data tab
                        this.metaDataPanel = new kimios.properties.MetaDataPanel({
                            dmEntityPojo: this.dmEntityPojo,
                            readOnly: write == false && fullAccess == false
                        });
                        this.add(this.metaDataPanel);

                        // add comments tab
                        this.commentsPanel = new kimios.properties.CommentsPanel({
                            dmEntityPojo: this.dmEntityPojo,
                            readOnly: write == false && fullAccess == false
                        });
                        this.add(this.commentsPanel);
                        this.saveButton.setVisible(false);
                    }

                    this.getWindow().setTitle(title);
                    this.getWindow().setIconClass(kimios.util.IconHelper.getIconClass(this.dmEntityPojo.type, this.dmEntityPojo.extension));
                }

                //multiple update
                else {
                    //set title
                    title = this.dmEntityPojo.length + ' ' + kimios.lang('Entities') + ' - ';
                    for (var i = 0; i < this.dmEntityPojo.length && i < 3; i++) {
                        if (i > 0) title += ', ';
                        title += this.dmEntityPojo[i].name;
                    }
                    if (this.dmEntityPojo.length > 3)
                        title += ', ...';

                    var needMeta = true;
                    for (var count = 0; count < this.dmEntityPojo.length; count++) {
                        if (this.dmEntityPojo[count].type == 1 || this.dmEntityPojo[count].type == 2)
                            needMeta = false;
                    }
                    if (needMeta) {
                        // add meta data tab
                        this.metaDataPanel = new kimios.properties.MetaDataPanel({
                            dmEntityPojo: this.dmEntityPojo
                        });
                        this.add(this.metaDataPanel);
                    }

                    // add security tab
                    if (this.ownerMode == true || fullAccess == true) {
                        this.securityEntityPanel = new kimios.properties.SecurityEntityPanel({
                            dmEntityPojo: this.dmEntityPojo
                        });
                        this.add(this.securityEntityPanel);
                    }
                    this.getWindow().setTitle(title);
                    this.getWindow().setIconClass('value');
                    this.saveButton.setVisible(true);
                }
                this.setActiveTab(0);
            }, this);
            store.load();
        }
    },

    createDMEntity: function () {
        var prop = this;
        switch (this.dmEntityPojo.type) {
            case 1:
                kimios.request.newWorkspace(
                    this.dmEntityPanel.nameField.getValue(),
                    this.securityEntityPanel.getJsonSecurityValues(),
                    function () {
                        kimios.Info.msg(kimios.lang('Workspace'), kimios.lang('NewWorkspaceOK'));
                        kimios.explorer.getViewport().refreshGrids();
                        kimios.explorer.getTreePanel().refresh();
                        if (prop.window != undefined)
                            prop.window.close();
                    }
                );
                break;
            case 2:
                kimios.request.newFolder(
                    this.dmEntityPanel.nameField.getValue(),
                    this.dmEntityPojo.parentType,
                    this.dmEntityPojo.parentUid,
                    this.dmEntityPanel.inheritedPermissionsField.getValue(),
                    this.securityEntityPanel.getJsonSecurityValues(),
                    function (response, opts) {
                        kimios.Info.msg(kimios.lang('Folder'), kimios.lang('NewFolderOK'));
                        kimios.explorer.getViewport().refreshGrids();
                        kimios.explorer.getTreePanel().refresh();
                        if (prop.window != undefined)
                            prop.window.close();
                    }
                );
                break;
            case 3:
                //newDocument, form, name, parentUid, isSecurityInherited, securityDatas, documentTypeUid, metaValues
                if (this.metaDataPanel.checkMandatory() == false) {
                    kimios.MessageBox.exception({
                        exception: kimios.lang('MandatoryFieldRequired')
                    });
                } else {
                    kimios.request.uploadDocumentWithProperties(
                        this.dmEntityPanel, // need to pass the form as parameter (to update it with data fields)
                        this.dmEntityPanel.nameField.getValue(),
                        this.dmEntityPojo.parentUid,
                        this.dmEntityPanel.inheritedPermissionsField.getValue(),
                        this.securityEntityPanel.getJsonSecurityValues(),
                        this.metaDataPanel.documentTypeUid,
                        this.metaDataPanel.getJsonMetaValues(),
                        function () {
                            kimios.Info.msg(kimios.lang('Document'), kimios.lang('AddDocumentOK'));
                            kimios.explorer.getViewport().refreshGrids();
                            if (prop.window != undefined)
                                prop.window.close();
                        }
                    );

                    /*
                    kimios.request.uploadDocument(
                        true,
                        this.dmEntityPanel, // need to pass the form as parameter (to update it with data fields)
                        this.dmEntityPanel.nameField.getValue(),
                        this.dmEntityPojo.parentUid,
                        this.dmEntityPanel.inheritedPermissionsField.getValue(),
                        this.securityEntityPanel.getJsonSecurityValues(),
                        this.metaDataPanel.documentTypeUid,
                        this.metaDataPanel.getJsonMetaValues(),
                        function () {
                            kimios.Info.msg(kimios.lang('Document'), kimios.lang('AddDocumentOK'));
                            kimios.explorer.getViewport().refreshGrids();
                            if (prop.window != undefined)
                                prop.window.close();
                        }
                    );
                    */
                }
        }
    },

    updateDMEntity: function () {
        var prop = this;
        switch (this.dmEntityPojo.type) {
            case 1:
                kimios.request.updateWorkspace(
                    this.dmEntityPanel.uidField.getValue(),
                    this.dmEntityPanel.nameField.getValue(),
                    this.window != undefined ? (this.securityEntityPanel != undefined ? this.securityEntityPanel.getJsonSecurityValues() : undefined) : undefined,
                    this.window != undefined ? (this.securityEntityPanel != undefined ? this.securityEntityPanel.isRecursiveSecurity() : undefined) : false,
                    function () {
                        kimios.Info.msg(kimios.lang('Workspace'), kimios.lang('UpdateWorkspaceOK'));
                        kimios.explorer.getViewport().refreshGrids();
                        kimios.explorer.getTreePanel().refresh();
                        if (prop.window != undefined)
                            prop.window.close();
                    }, this.window != undefined);
                break;
            case 2:
                kimios.request.updateFolder(
                    this.dmEntityPanel.uidField.getValue(),
                    this.dmEntityPanel.nameField.getValue(),
                    this.window != undefined ? (this.securityEntityPanel != undefined ? this.securityEntityPanel.getJsonSecurityValues() : undefined) : undefined,
                    this.window != undefined ? (this.securityEntityPanel != undefined ? this.securityEntityPanel.isRecursiveSecurity() : undefined) : false,
                    function () {
                        kimios.Info.msg(kimios.lang('Folder'), kimios.lang('UpdateFolderOK'));
                        kimios.explorer.getViewport().refreshGrids();
                        kimios.explorer.getTreePanel().refresh();
                        if (prop.window != undefined)
                            prop.window.close();
                    }, this.window != undefined && this.securityEntityPanel != undefined);
                break;
            case 3:
                if (this.metaDataPanel.checkMandatory() == false) {
                    kimios.MessageBox.exception({
                        exception: kimios.lang('MandatoryFieldRequired')
                    });
                } else {
                    kimios.request.updateDocument(
                        this.dmEntityPanel.uidField.getValue(),
                        this.dmEntityPanel.nameField.getValue(),
                        this.metaDataPanel.documentTypeUid,
                        this.metaDataPanel.getJsonMetaValues(),
                        this.window != undefined ? (this.securityEntityPanel != undefined ? this.securityEntityPanel.getJsonSecurityValues() : undefined) : undefined,
                        this.newVersion,
                        function () {
                            kimios.Info.msg(kimios.lang('Document'), kimios.lang('UpdateDocumentOK'));
                            kimios.explorer.getViewport().refreshGrids();
                            if (prop.window != undefined)
                                prop.window.close();
                        }, this.window != undefined && this.securityEntityPanel != undefined);
                }
        }
    },

    updateDMEntities: function (changeSecurity) {
        var prop = this;
        if (this.metaDataPanel.checkMandatory() == false) {
            kimios.MessageBox.exception({
                exception: kimios.lang('MandatoryFieldRequired')
            });
        } else {
            kimios.request.updateEntities(
                this.dmEntityPojo,
                this.securityEntityPanel.getJsonSecurityValues(),
                this.securityEntityPanel.isRecursiveSecurity(),
                this.metaDataPanel == undefined ? undefined : this.metaDataPanel.documentTypeUid,
                this.metaDataPanel == undefined ? undefined : this.metaDataPanel.getJsonMetaValues(),
                function () {
                    kimios.Info.msg(kimios.lang('Entities'), kimios.lang('Update') + ' ' + kimios.lang('Completed'));
                    kimios.explorer.getViewport().refreshGrids();
                    kimios.explorer.getTreePanel().refresh();
                    if (prop.window != undefined)
                        prop.window.close();
                }, changeSecurity);
        }
    }
});
