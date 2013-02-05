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
    this.createProgressView();
    this.progressTask = new Ext.util.DelayedTask(this.requestProgress,
      this);
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
  uploadFile : function(parent, form, action, securityInherit, sec, parentUid, metas) {
    this.endMsg = kimios.lang('AddDocumentOK');
    this.parentCmp = parent;
    this.createUploadDOM(form, action, securityInherit, sec, parentUid, metas);
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
    this.startProgress.defer(200, this);
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


