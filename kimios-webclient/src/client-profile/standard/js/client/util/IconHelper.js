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
      /*
        TODO: check css class existence
       */
      return extension;
  },
  getDocumentIcon : function(extension, size){
    if (size == null) size = 16;
      var path = srcContextPath + '/images/icons';
      return path + "/" + size + "x" + size + "/" + kimios.util.IconHelper.iconIndexer(extension) + ".png";
  },
  
  getIcon : function(type, extension){
    switch (type) {
      case 1:
        return srcContextPath + '/images/icons/16x16/database.png';
      case 2:
        return srcContextPath + '/images/icons/16x16/folder.png';
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
        if (extension == undefined) return 'unknown';
        return kimios.util.IconHelper.fileIconClass(extension.toLowerCase());
      default:
        return 'home';
    }
  },
  getIconStyle: function(extension){
      return kimios.util.IconHelper.fileIconStyle(extension.toLowerCase());
  }

};
