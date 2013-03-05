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

var iconList = [];
kimios.util.IconHelper = {

  iconIndexer : function(extension) {
    if (extension == 'jpg' || extension == 'png'
        || extension == 'gif' || extension == 'psd'
        || extension == 'tif' || extension == 'tiff'
        || extension == 'bmp' || extension == 'ico')
      return 'img';
    
    if (extension == 'odt')
      return 'ooo';
    
    if (extension == 'zip' || extension == 'rar' || extension == 'ace'
        || extension == 'gz' || extension == 'war'
        || extension == 'tgz' || extension == 'bz2')
      return 'zip';
    
    if (extension == 'pdf'
      || extension == 'doc'
        || extension == 'docx'
        || extension == 'xls'
        || extension == 'xlsx'
        || extension == 'vsd'
        || extension == 'mpp'
        || extension == 'ppt'
        || extension == 'html'
        || extension == 'htm'
        || extension == 'txt')
      return extension;
    
  
    return 'unknown';
  },
  fileIconStyle: function(extension){
    var style = 'background-image: url(' + srcContextPath + '/images/fileicons/' + extension + '.png) !important;' +
    	'background-repeat: no-repeat;' +
    	'background-position: center right;';

      return style;
  },
  fileIconClass: function(extension){
      if(iconList.indexOf(extension) > -1)
        return extension;
      else
        return 'unknown';
  },
  getDocumentIcon : function(extension, size){
    if (size == null) size = 16;
      var path = srcContextPath + '/images/icons';
      return path + "/" + size + "x" + size + "/" + kimios.util.IconHelper.iconIndexer(extension) + ".png";
  },
  
  getIcon : function(type, extension){
    var sTheme = (defaultTheme && defaultTheme.length > 0 ? 'themes/' +  defaultTheme : '') + '/';

    switch (type) {
      case 1:
        return srcContextPath + '/images/' + sTheme + 'icons/16x16/database.png';
      case 2:
        return srcContextPath + '/images/' + sTheme + 'icons/16x16/folder.png';
      case 3:
        return kimios.util.IconHelper.getDocumentIcon(extension, 16);
    }
  },
  getIconClass : function(type, extension){
    switch (type) {
      case 1:
        return 'dm-entity-tab-properties-workspace';
      case 2:
        return 'dm-entity-tab-properties-folder';
      case 3:
        if (extension == undefined || extension.trim().length == 0) return 'unknown';
        return kimios.util.IconHelper.fileIconClass(extension.toLowerCase());
      default:
        return 'home';
    }
  },
  getIconStyle: function(extension){
      return kimios.util.IconHelper.fileIconStyle(extension.toLowerCase());
  },
  iconThemeSwitcher: function(themeName){
          var i,
                  a,
                  links = document.getElementsByTagName("link"),
                  len = links.length;




          var exist = false;
          for (i = 0; i < len; i++) {
              a = links[i];
              if (a.getAttribute("title") && a.getAttribute("title") == themeName){
                  a.disabled = false;
                  exist = true;
                  break;
              }
          }
          if(!exist){
              var fileref=document.createElement("link")
              fileref.setAttribute("rel", "stylesheet")
              fileref.setAttribute("type", "text/css")
              fileref.setAttribute("href", srcContextPath + '/images/themes/' + themeName + '/css/icons.css');
              if (typeof fileref!="undefined")
                  document.getElementsByTagName("head")[0].appendChild(fileref)


          }
      },
      iconClassLoader: function (){
          Ext.Ajax.request({
              url: srcContextPath + '/icons/list',
              success: function(response, opts) {
                  var obj = Ext.decode('(' + response.responseText + ')');
                  iconList = [];
                  for(var i in obj){
                      iconList.push(obj[i]);
                  }
              },
              failure: function(response, opts) {
              }
          });
      }
};
