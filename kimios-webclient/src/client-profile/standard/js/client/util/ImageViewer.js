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
kimios.util.ImageViewer = Ext.extend(Ext.Panel, {
  constructor : function(config){
    this.closable = true;
    this.title = config.pojo.name;
    this.iconCls = kimios.util.IconHelper.getIconClass(config.pojo.type, config.pojo.extension);
    this.getDocumentsButton = new Ext.Button({
      text : kimios.lang('GetDocument'),
      iconCls : 'reindex-run',
      handler : function(){
        window.location.href = kimios.util.getDocumentLink(config.pojo.uid);
      }  
    });
    this.addToBookmarksButton = new Ext.Button({
      text : kimios.lang('AddToBookmark'),
      iconCls : 'qaction-bookmarks',
      handler : function(){
        kimios.request.addToBookmarks(config.pojo.uid, config.pojo.type);
      }
    });
    this.deleteButton = new Ext.Button({
      text : kimios.lang('Delete'),
      iconCls : 'trash',
      scope : this,
      handler : function(){
        var thisPanel = this;
        kimios.request.deleteDMEntity(config.pojo.uid, config.pojo.type, config.pojo.name, function(){
          thisPanel.destroy();
        });
      }
    });
    this.closeButton = new Ext.Button({
      iconCls : 'del-icon',
      tooltip : kimios.lang('Close'),
      scope : this,
      handler : function(){
        this.destroy();
      }
    });
    this.tbar = new Ext.Toolbar({
      items : [
               this.getDocumentsButton,
               this.addToBookmarksButton,
               this.deleteButton, '->',
               this.closeButton
               ]
    });


    //


    
    var html = '<div id="kimios-image-viewer">';
    html += '<span class="kimios-pdf-title">'+config.pojo.name+'</span>';
    html += '<table>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('DocNum')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+config.pojo.uid+'</td></tr>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('Position')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+(config.pojo.path.substr(0, config.pojo.path.lastIndexOf('/')))+'</td></tr>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('DocumentType')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+(config.pojo.documentTypeName != '' ? config.pojo.documentTypeName : kimios.lang('Document'))+' ('+config.pojo.extension.toUpperCase()+')</td></tr>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('Size')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+(config.pojo.length/1024).toFixed(2)+' '+kimios.lang('Kb')+'</td></tr>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('Author')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+config.pojo.owner+'@'+config.pojo.ownerSource+'</td></tr>';
    html += '<tr><td class="kimios-pdf-label">'+kimios.lang('CreationDate')+kimios.lang('LabelSeparator')+'&nbsp;</td><td class="kimios-pdf-value">'+kimios.date(config.pojo.creationDate)+'</td></tr>';
    html += '</table>';
    if (config.pojo.extension == 'pdf' || config.pojo.extension == 'PDF'){
      html += '<br />';
      /*for (var i=0; i<config.links.length; i++){
        html += '<span class="kimios-pdf-page">'+kimios.lang('PDFPage')+' '+config.links[i].num+'</span><br/>';
        html += '<img class="kimios-pdf-image" alt="'+kimios.lang('Wait')+' ('+config.links[i].num+'/'+config.links.length+')" src="'+getBackEndUrl('DocumentVersion') + '&action=getTemporaryFile&uid='+config.pojo.uid+'&path='+config.links[i].path+'"><br />';
      }*/
        for (var i=0; i<config.links.length; i++){


            var href=  getBackEndUrl('DocumentVersion') + '&action=getTemporaryFile&uid='+config.pojo.uid+'&path='+config.links[i].path;

            var width = '100%';
            var height = '100%';
            var type = 'application/pdf';



            html += '<object width="' + width + '" height="' + height + '" data="' + href + '" type="' + type + '">';
            //html += '<param name="Src" value="' + href + '" />';
            //html += '<embed width="' + width + '" height="' + height + '" type="' + type + '" href="' + href + '" src="'+ href + '" />';
            html += '</object>';

        }
    }else{
      html += '<img class="kimios-pdf-image" alt="'+kimios.lang('Wait')+'" src="'+config.links[0].path+'"><br />';
    }
    html += '</div>';
    this.bodyCfg = html;
    kimios.util.ImageViewer.superclass.constructor.call(this, config);
  },
  initComponent: function(){
    kimios.util.ImageViewer.superclass.initComponent.apply(this, arguments);
  },
  refreshLanguage : function(){
    this.getDocumentsButton.setText(kimios.lang('GetDocument'));
    this.addToBookmarksButton.setText(kimios.lang('AddToBookmark'));
    this.deleteButton.setText(kimios.lang('Delete'));
    this.closeButton.setTooltip(kimios.lang('Close'));
  }
});
