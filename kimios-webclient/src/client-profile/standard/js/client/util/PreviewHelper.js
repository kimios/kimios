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

kimios.util.PreviewHelper = {
    generatePreviewUrl : function(entityRecord, converter){
        var link = srcContextPath + '/Converter?sessionId=' + sessionUid;
        link += '&documentId=' + entityRecord.uid;
        link += '&converterImpl=' + (converter ? converter : kimios.util.PreviewHelper.extensionMapping()[entityRecord.extension.toLowerCase()][0].conv) ;
        link += "&inline=true";
        return link;
    },

    extensionMapping : function(){
        var extMapping = {
           doc: [{
               target: 'html',
               conv:'org.kimios.kernel.converter.impl.DocToHTML'
           }],
           docx: [{
                target: 'html',
                conv:'org.kimios.kernel.converter.impl.Dox4jDocxToHTML'
           }],
           eml: [{
               target: 'html',
               conv: 'org.kimios.kernel.converter.impl.MailToHtml'
           }],
           xls: [{
               target: 'html',
               conv: 'org.kimios.kernel.converter.impl.XlsToHTML'
           }],
           xlsx: [{
                target: 'html',
                conv: 'org.kimios.kernel.converter.impl.XlsToHTML'
            }]
           //pdf: [{conv: 'org.kimios.kernel.converter.impl.PDFMerger', target: 'pdf'}]
        }
        return extMapping;
    }
};
