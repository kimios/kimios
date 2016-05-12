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

var currentFolderId = -1;
var filesList = [];
var lastInserted = -1;

function handleFileSelect(evt, target) {



    if (evt.browserEvent)
        evt = evt.browserEvent;

    evt.stopPropagation();
    evt.preventDefault();


    if (evt.dataTransfer == undefined) {
        alert('Votre navigateur ne supporte pas le Drag And Drop.\nVeuillez sélectionner manuellement les fichiers à envoyer.');
        target.fireEvent('filedropped');
        return;
    }

    var files = evt.dataTransfer.files; // FileList object.
    /*
     add to form
     */

    // files is a FileList of File objects. List some properties.
    for (var i = 0, f; f = files[i]; i++) {
        new kimios.properties.PropertiesWindow({
            createMode : true,
            documentFileItem: f,
            dmEntityPojo : new kimios.DMEntityPojo({
                parentType : kimios.explorer.getActivePanel().type,
                parentUid : kimios.explorer.getActivePanel().uid,
                path : kimios.explorer.getActivePanel().path,
                type : 3
            })
        }).show();
        //For now, only one file
        break;
    }

    target.fireEvent('filedropped');
}


function removeFile(item, pos) {

    var toRemoveId = item.parentNode.id;
    var itemAdded = Ext.getCmp(toRemoveId);
    itemAdded.ownerCt.remove(itemAdded, true);

    /*
     Remove File
     */
    filesList.splice(pos, 1);
    lastInserted--;

}

function handleDragOver(evt) {
    if (evt.browserEvent)
        evt = evt.browserEvent;
    evt.stopPropagation();
    evt.preventDefault();



    evt.dataTransfer.dropEffect = 'copy'; // Explicitly show this is a copy.
}

var fileDroppedTpl = null;

function getDroppedFileTemplate() {
    if (fileDroppedTpl == null) {
        fileDroppedTpl = new Ext.XTemplate('<div class="dropped-file" id="item-file-{pos}"></div>',
            '<span style="float: left;display: inline-block;">{name}</span><a href="#" onclick="removeFile(this,{pos});" style="float: right;display: inline-block; width: 20px; height: 20px; background-color: red"></a>',
            '</div><div style="clear: both;"></div>');

        fileDroppedTpl.compile();
    }
    return fileDroppedTpl;
}


MultiUploadForm = Ext.extend(Ext.Panel, {
    width:320, layout:'fit', buttonAlign:'center', initComponent:function (arguments) {
        /*
         init drop zone
         */
        this.addEvents('filedropped');
        this.addEvents('progress');
        this.addEvents('upfinished');
        var fields = [
            {name:'id', type:'text', system:true}
            ,
            {name:'shortName', type:'text', system:true}
            ,
            {name:'fileName', type:'text', system:true}
            ,
            {name:'filePath', type:'text', system:true}
            ,
            {name:'fileItem', system:true}
            ,
            {name:'fileCls', type:'text', system:true}
            ,
            {name:'input', system:true}
            ,
            {name:'form', system:true}
            ,
            {name:'state', type:'text', system:true}
            ,
            {name:'error', type:'text', system:true}
            ,
            {name:'progressId', type:'int', system:true}
            ,
            {name:'bytesTotal', type:'int', system:true}
            ,
            {name:'bytesUploaded', type:'int', system:true}
            ,
            {name:'estSec', type:'int', system:true}
            ,
            {name:'filesUploaded', type:'int', system:true}
            ,
            {name:'speedAverage', type:'int', system:true}
            ,
            {name:'speedLast', type:'int', system:true}
            ,
            {name:'timeLast', type:'int', system:true}
            ,
            {name:'timeStart', type:'int', system:true}
            ,
            {name:'pctComplete', type:'int', system:true}
        ];
        // create store
        this.store = new Ext.data.SimpleStore({
            id:'ekn-upload-store', fields:fields, data:[]
        });

        var view = new Ext.DataView({
            store:this.store,
            itemSelector:'div.dropped-file',
            style:{
                padding:'10px',
                backgroundColor:'white'
            },
            height:500,
            tpl:new Ext.XTemplate('<tpl for=".">',
                '<div class="dropped-file" style="clear: both;">',
                '<span class="dropped-file-name">{shortName}</span>',
                '<tpl if="state==\'done\'">',
                '<span class="file-status">Terminé</span>',
                '</tpl>',
                '<tpl if="state==\'queued\'">',
                '<span class="file-status">En attente</span>',
                '</tpl>',
                '</div>',
                '</tpl>')
        });

        this.items = view;
        this.view = view;

        this.progressInterval = 1000;

        MultiUploadForm.superclass.initComponent.call(this, arguments);
    },
    frame:true, fileUpload:true, style:'margin: 0 auto;', buttons:[new Ext.ux.form.FileUploadField({
        buttonText:'Ajout ...',
        buttonOnly:true,
        listeners:{
            'fileselected':function (btn, value) {
                var me = btn.ownerCt.ownerCt;
                var fileName = value.split(/[\/\\]/).pop();
                var rec = new me.store.recordType({
                    input:btn.detachInputFile(),
                    fileName:fileName,
                    shortName:Ext.util.Format.ellipsis(fileName, 40),
                    state:'queued'
                });

                rec.commit();
                me.store.add(rec);
            }
        }
    }),
        {
            text:'Envoi',
            handler:function (button, evt) {
                var me = button.ownerCt.ownerCt;
                var toStart = false;
                if(!currentFolderId || currentFolderId == null){
                    alert('Dépôt non autorisé à cet emplacement.');
                    return;
                }


                me.store.each(function (record) {
                    if (record.get('state') != 'done') {
                        var progressId = parseInt(Math.random() * 1e10, 10);
                        var form = Ext.getBody().createChild({
                            tag:'form', action:'/upload/upload/send/' + currentFolderId, method:'post', cls:'x-hidden', id:Ext.id(), cn:[
                                {
                                    tag:'input', type:'hidden', name:'fileId', value:2
                                },
                                {
                                    tag:'input', type:'hidden', name:'progressId', value:progressId
                                }
                            ]
                        }, null, true);

                        var fd = null;
                        record.set('form', form);
                        record.set('progressId', progressId);
                        record.set('state', 'uploading');
                        if (record.get('fileItem') == null) {
                            form.appendChild(record.get('input').dom);
                        } else {
                            fd = new FormData(form);
                            fd.append('file', record.get('fileItem'));
                        }

                        // set state
                        record.set('state', 'uploading');
                        record.set('pctComplete', 0);

                        if (fd == null) {
                            Ext.Ajax.request({
                                url:form.action,
                                method:'post',
                                form:form,
                                isUpload:true,
                                success:function (response, opts) {
                                    record.set('state', 'done');
                                    record.set('error', '');
                                    record.commit();
                                    me.fireEvent('upfinished', me, record);
                                }
                            });
                        } else {
                            //Post form data
                            var xhr = new XMLHttpRequest();
                            xhr.open("POST", form.action);
                            xhr.onreadystatechange = function () {
                                if (xhr.readyState == 4 && xhr.status == 200) {
                                    record.set('state', 'done');
                                    record.set('error', '');
                                    record.commit();
                                    me.fireEvent('upfinished', me, record);
                                }
                            };
                            xhr.send(fd);
                        }

                        toStart = true;
                    }
                    return true;
                });


                if (toStart)
                    me.startProgress();

            }
        },
        {
            text:'Tout effacer',
            handler:function (button, evt) {
                Ext.MessageBox.confirm('Tout effacer ?', 'Attention, les fichiers non envoyés seront supprimer. Continuer ?', function (btn) {
                    if (btn == 'yes') {
                        button.ownerCt.ownerCt.store.removeAll();
                        button.ownerCt.ownerCt.initDropZone(button.ownerCt.ownerCt, button.ownerCt.ownerCt.dropZoneBck);
                        button.ownerCt.ownerCt.doLayout();
                        filesList = [];
                    }


                });
            }
        }
    ],
    initDropZone:function (me, targetDropZone) {
        if (!targetDropZone) {
            me.dropZoneItem = new Ext.BoxComponent({
                id:'ekn-drop-zone',
                autoEl:{
                    tag:'div'
                },
                height:120,
                width:300,
                style:{
                    marginTop:'20px',
                    marginBottom:'20px',
                    border:'1px dashed grey',
                    webkitBorderRadius:'8px',
                    mozBorderRadius:'8px',
                    borderRadius:'8px'
                },
                html:'',
                listeners:{
                    afterRender:function () {
                        var dropZone = document.getElementById(this.getEl().id);
                        dropZone.addEventListener('dragover', handleDragOver, false);
                        var me = this;
                        dropZone.addEventListener('drop', function (evt) {
                            var elem = me;
                            handleFileSelect(evt, elem);
                        }, false);
                    }
                }
            });
            me.add(me.dropZoneItem);
        } else {
            me.dropZoneBck = targetDropZone;
            targetDropZone.getEl().addListener('dragover', handleDragOver);
            targetDropZone.getEl().addListener('drop', function (evt) {
                var elem = this;
                handleFileSelect(evt, elem);
            }, this);
        }
    },
    progressTaskItem:function (uploader, data, record) {
        var bytesTotal, bytesUploaded, pctComplete, state, idx, item, width, pgWidth;
        if (record) {


            state = record.get('state');
            bytesTotal = record.get('bytesTotal') || 1;
            bytesUploaded = record.get('bytesUploaded') || 0;
            if ('uploading' === state) {
                pctComplete = Math.round(1000 * bytesUploaded / bytesTotal) / 10;
            }
            else if ('done' === state) {
                pctComplete = 100;
            }
            else {
                pctComplete = 0;
            }
            record.set('pctComplete', pctComplete);
            idx = this.store.indexOf(record);
            item = Ext.get(this.view.getNode(idx));
            if (item) {

                if (!item.progressBar) {
                    item.progressBar = new Ext.ProgressBar({
                        renderTo:item,
                        value:0,
                        text:'0 %'
                    });
                }
                if (bytesUploaded > 0 && bytesTotal > 0) {
                    var frc = bytesUploaded / bytesTotal;
                    var pct = frc;
                } else {
                    pct = 0;
                }


                if (pct == 1)
                    item.progressBar.updateProgress(100, 'Finished', false);
                else
                    item.progressBar.updateProgress(pct, Math.round(pct * 100) + '%', false);


                /*item.update(pctComplete + ' %'); */
            }
        }
    },
    startProgress:function () {
        if (!this.progressTask) {
            this.progressTask = new Ext.util.DelayedTask(this.requestProgress, this);
        }
        this.progressTask.delay.defer(this.progressInterval / 2, this.progressTask, [this.progressInterval]);
    },
    stopProgress:function () {
        if (this.progressTask) {
            this.progressTask.cancel();
        }
    },
    requestProgress:function () {
        var records, p;
        var o = {
            url:'/upload/upload/progress', method:'post', params:{}, scope:this, callback:function (options, success, response) {
                var o;
                if (true !== success) {
                    return;
                }
                try {
                    o = Ext.decode(response.responseText);
                }
                catch (e) {
                    return;
                }
                if ('object' !== Ext.type(o)) {
                    return;
                }

                options.record.set('bytesTotal', o.totalSize);
                options.record.set('bytesUploaded', o.bytesRead);
                options.record.set('pctComplete', o.percentage);

                //this.fireEvent('progress', this, options.record.data, options.record);
                this.progressTaskItem(this, options.record.data, options.record);
                this.progressTask.delay(this.progressInterval);
            }
        };
        records = this.store.query('state', 'uploading');
        records.each(function (r) {
            o.params['progressId'] = r.get('progressId');
            o.record = r;
            (function () {
                Ext.Ajax.request(o);
            }).defer(250);
        }, this);
    }
});

