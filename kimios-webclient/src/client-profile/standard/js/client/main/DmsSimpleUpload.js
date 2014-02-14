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
DmsSimpleUpload = function(config) {
  Ext.apply(this, config);
  // call parent
  DmsSimpleUpload.superclass.constructor.apply(this, arguments);
  // add events
  // {{{
  this.addEvents('start', 'finished', 'progress');

  this.on( {
    start : {
      scope : this,
      fn : this.onStart
    },
    finished : {
      scope : this,
      fn : this.onFinish
    }
  });
};

Ext.ns('Ext.ux');

Ext.ux.XHRUpload = function(config){
    Ext.apply(this, config, {
        method: 'POST'
        //,fileNameHeader: 'X-File-Name'
        //,filePostName:'fileName'
        //,contentTypeHeader: 'text/plain; charset=x-user-defined-binary'
        ,extraPostData:{}
        //,xhrExtraPostDataPrefix:'extraPostData_'
        ,sendMultiPartFormData:false
    });
    this.addEvents( //extend the xhr's progress events to here
        'loadstart',
        'progress',
        'abort',
        'error',
        'load',
        'loadend'
    );
    Ext.ux.XHRUpload.superclass.constructor.call(this);
};

Ext.extend(Ext.ux.XHRUpload, Ext.util.Observable,{
    send:function(config){
        Ext.apply(this, config);

        this.xhr = new XMLHttpRequest();
        this.xhr.addEventListener('loadstart', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('progress', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('progressabort', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('error', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('load', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('loadend', this.relayXHREvent.createDelegate(this), false);

        this.xhr.upload.addEventListener('loadstart', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('progress', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('progressabort', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('error', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('load', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('loadend', this.relayUploadEvent.createDelegate(this), false);

        this.xhr.open(this.method, this.url, true);
        this.xhr.send(this.formData);


        if(typeof(FileReader) !== 'undefined' && this.sendMultiPartFormData ){
            //currently this is firefox only, chrome 6 will support this in the future
            this.reader = new FileReader();
            this.reader.addEventListener('load', this.sendFileUpload.createDelegate(this), false);
            this.reader.readAsBinaryString(this.file);
            return true;
        }
        //This will work in both Firefox 1.6 and Chrome 5
        //this.xhr.overrideMimeType(this.contentTypeHeader);
        //this.xhr.setRequestHeader(this.fileNameHeader, this.file.name);
        //for(attr in this.extraPostData){
        //    this.xhr.setRequestHeader(this.xhrExtraPostDataPrefix + attr, this.extraPostData[attr]);
        //}
        //xhr.setRequestHeader('X-File-Size', files.size); //this may be useful

        return true;

    }
    ,sendFileUpload:function(){

        var boundary = (1000000000000+Math.floor(Math.random()*8999999999998)).toString(),
            data = '';

        for(attr in this.extraPostData){
            data += '--'+boundary + '\r\nContent-Disposition: form-data; name="' + attr + '"\r\ncontent-type: text/plain;\r\n\r\n'+this.extraPostData[attr]+'\r\n';
        }

        //window.btoa(binaryData)
        //Creates a base-64 encoded ASCII string from a string of binary data.
        //https://developer.mozilla.org/en/DOM/window.btoa
        //Firefox and Chrome only!!

        data += '--'+boundary + '\r\nContent-Disposition: form-data; name="' + this.filePostName + '"; filename="' + this.file.name + '"\r\nContent-Type: '+this.file.type+'\r\nContent-Transfer-Encoding: base64\r\n\r\n' + window.btoa(this.reader.result) + '\r\n'+'--'+boundary+'--\r\n\r\n';

        this.xhr.setRequestHeader('Content-Type', 'multipart/form-data; boundary='+boundary);
        this.xhr.send(data);
    }
    ,relayUploadEvent:function(event){
        this.fireEvent('upload'+event.type, event);
    }
    ,relayXHREvent:function(event){
        this.fireEvent(event.type, event);
    }
});

Ext.extend(DmsSimpleUpload, Ext.util.Observable, {
  maxFileSize : 524288,
  progressIdName : 'UPLOAD_ID',
  progressInterval : 1000,
  progressCmd : 'progress',
  progressUrl : getBackEndUrl('Uploader'),
  progressMap : {
    bytes_total : 'bytesTotal',
    bytes_uploaded : 'bytesUploaded',
    est_sec : 'estSec',
    files_uploaded : 'filesUploaded',
    speed_average : 'speedAverage',
    speed_last : 'speedLast',
    time_last : 'timeLast',
    time_start : 'timeStart'
  },
  createSimpleUploadDOM : function(form, action, docUid) {
    var progressId = parseInt(Math.random() * 1e10, 10);
    form.createChild( {
      tag : 'input',
      type : 'hidden',
      name : 'actionUpload',
      value : action
    }, form.first());
    form.createChild( {
      tag : 'input',
      type : 'hidden',
      name : 'APC_UPLOAD_PROGRESS',
      value : progressId
    }, form.first());

    form.createChild( {
      tag : 'input',
      type : 'hidden',
      name : this.progressIdName,
      value : progressId
    }, form.first());
    form.createChild( {
      tag : 'input',
      type : 'hidden',
      name : 'documentUid',
      value : docUid
    }, form.first());

    this.progressId = progressId;
  },
  createUploadDOM : function(form, action, securityInherit, sec, parentUid, docType) {

    var secJson = '[]';
    var docTypeUid = -1;
    var metas = '';
    var progressId = parseInt(Math.random() * 1e10, 10);
    
    if (!securityInherit && sec != null){
      form.getForm().findField('securityField').setValue(sec);
    }
    if (docType) {
      form.getForm().findField('dtUidField').setValue(docType.documentTypeUid);
      form.getForm().findField('metaValuesField').setValue(docType.metaValues);
    }
    
    form.getForm().findField('progressIdField').setValue(progressId);
    this.progressId = progressId;
  },
  fireFinishEvents : function(form) {
    this.fireEvent('finished', form);
  },
  processSuccess : function(options, response, o) {
    alert('process success');
  },
  processFailure : function(options, response, error) {
    alert('progress failure');
  },
  requestProgress : function() {
    if (!this.finished) {
      Ext.Ajax.request( {
        url : this.progressUrl,
        method : 'post',
        params : {
          action : this.progressCmd,
          progressId : this.progressId
        },
        scope : this,
        callback : function(options, success, response) {
          try {
            var info = {bytesRead: 0, totalLength: 0};
            if(response.responseText != 'null'){
                info = Ext.decode(response.responseText);
            }
            if (this.msgBox) {
                  if(info.bytesRead > 0 && info.totalLength > 0){
                    var frc = info.bytesRead / info.totalLength;
                    var pct = Math.round(frc * 100);
                  } else {
                      pct = 0;
                  }

                if (pct == 100)
                    this.msgBox.updateProgress(frc, 'Finished');
                else
                    this.msgBox.updateProgress(frc, pct + ' %');
            }
            if (pct < 100)
              this.progressTask.delay.defer(200, this);
            else
              this.progressTask = null;
          } catch (e) {
          }
        }
      });
    } else {
      this.progressTask = null;
      this.msgBox.updateProgress(1, 'Finished');
    }
  },
  startProgress : function() {
      this.progressTask.delay.defer(this.progressInterval / 2,
          this.progressTask, [ this.progressInterval ]);
  },
  stopProgress : function() {
    if (this.progressTask) {
      this.progressTask.cancel();
    }
  },
  uploadCallback : function(options, success, response) {
    alert('upload callback');
  },
  uploadFile : function(parent, form, action, securityInherit, sec, parentUid, metas, documentFileItem) {
    this.endMsg = kimios.lang('AddDocumentOK');
    this.parentCmp = parent;
    this.createUploadDOM(form, action, securityInherit, sec, parentUid, metas);


    this.documentFileItem = documentFileItem;
    if(console)
        console.log(this.documentFileItem);
    this.fireEvent('start', form);


  },
  uploadFileImport : function(parent, action, form, docUid) {
    this.endMsg = kimios.lang('VersionImportOK');
    this.parentCmp = parent;
    this.createSimpleUploadDOM(form.getEl(), action, docUid);
    this.fireEvent('start', form);
  },
  onStart : function(form) {

    var postUrl = getBackEndUrl('Uploader');

    //init progress view
    this.createProgressView();
      this.progressTask = new Ext.util.DelayedTask(this.requestProgress,
          this);

    if(this.documentFileItem){
        var fd = new FormData(form.getForm().getEl().dom);
        fd.append('docUpload', this.documentFileItem);
        /*
            Ajax push
         */
        this.xhr = new XMLHttpRequest();
        /*this.xhr.addEventListener('loadstart', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('progress', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('progressabort', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('error', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('load', this.relayXHREvent.createDelegate(this), false);
        this.xhr.addEventListener('loadend', this.relayXHREvent.createDelegate(this), false);

        this.xhr.upload.addEventListener('loadstart', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('progress', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('progressabort', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('error', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('load', this.relayUploadEvent.createDelegate(this), false);
        this.xhr.upload.addEventListener('loadend', this.relayUploadEvent.createDelegate(this), false);*/


        var xhrMe = this.xhr;
        var me = this;
        this.xhr.onreadystatechange = function () {
            if(xhrMe.readyState != 4) return;
            if (xhrMe.status == 200) {
                var json = Ext.util.JSON.decode(xhrMe.responseText);

                if(json.success){
                    me.fireFinishEvents(form);
                } else {

                    //handle error
                    if(me.progressTask){
                        me.stopProgress();
                    }
                    try{
                        if(me.msgBox){
                            me.msgBox.getDialog().close();
                        }
                    }catch(e){
                    }

                    kimios.MessageBox.exception({
                        exception: json.exception,
                        stackTrace: json.trace
                    });

                    me.finished = true;//loop fix
                    if (me.progressTask)
                        me.progressTask.cancel();
                    me.progressTask = null;
                }



            } else {
                //handle error
                if(me.progressTask){
                    me.stopProgress();
                }
                try{
                    if(me.msgBox){
                        me.msgBox.getDialog().close();
                    }
                }catch(e){
                }
                me.finished = true;//loop fix
                if (me.progressTask)
                    me.progressTask.cancel();
                me.progressTask = null;
            }
        };
        this.xhr.open('post', postUrl, true);
        this.xhr.send(fd);

    }else
        form.getForm().submit({
      clientValidation : false,
      url : postUrl,
      method : 'post',
      fileUpload: true,
      failure: function(form, action){
        //close progress view
        
        if(this.progressTask){
          this.stopProgress();
        }
        try{
          if(this.msgBox){
            this.msgBox.getDialog().close();
          }
        }catch(e){
        }
        var json = Ext.util.JSON.decode(action.response.responseText);
        kimios.MessageBox.exception({
                    exception: json.exception,
                    stackTrace: json.trace
                });
        
        this.finished = true;//loop fix
        if (this.progressTask)
            this.progressTask.cancel();
        this.progressTask = null;
      },
      success : function(form, action) {
        this.fireFinishEvents(form);
      },
      scope : this
    });
    //start progress process
    this.startProgress.defer(100, this);
  },
  onFinish : function(form) {
    if (this.progressTask)
      this.progressTask.cancel();

    this.finished = true;
    this.progressTask = null;
    if (this.msgBox) {
      this.msgBox.updateProgress(1, 'Finished');
      this.msgBox.getDialog().close();
    }
  },
  createProgressView : function() {
    var msgBox = Ext.Msg.show( {
      msg : kimios.lang('ImportingDocument'),
      progress : true,
      modal : true,
      progressText : '0 %',
      closable : false
    });
    this.msgBox = msgBox;
  }
}); // eo extend

// register xtype
Ext.reg('qsimpleuploader', DmsSimpleUpload);

// eof


