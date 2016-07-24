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
        link += "&outputFormat=" + (kimios.util.PreviewHelper.extensionMapping()[entityRecord.extension.toLowerCase()][0].target);
        link += "&inline=true";
        return link;
    },
    extensionMapping : function(){
        var baseConverterPath = 'org.kimios.converter.impl.';
        var baseVendorConverterPath = baseConverterPath + 'vendors.aspose.';
        var vendorMappingModel = {
            doc: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'DocxToHTML'
            }],
            docx: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'DocxToHTML'
            }],
            odt: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'DocxToHTML'
            }],
            eml: [{
                target: 'html',
                conv: baseVendorConverterPath + 'MailToHTML'
            }],
            msg: [{
                target: 'html',
                conv: baseVendorConverterPath + 'MailToHTML'
            }],
            xls: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'XlsToHTML'
            }],
            xlsx: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'XlsToHTML'
            }],
            ods: [{
                target: 'pdf',
                conv: baseVendorConverterPath + 'XlsToHTML'
            }],
            ppt: [{
                target: 'pdf',
                conv:baseVendorConverterPath + 'PptToHTML'
            }],
            pptx: [{
                target: 'pdf',
                conv:baseVendorConverterPath + 'PptToHTML'
            }],
            odp: [{
                target: 'pdf',
                conv:baseVendorConverterPath + 'PptToHTML'
            }]
        }
        var osMappingModel = {
            doc: [{
                target: 'html',
                conv: baseConverterPath + 'DocToHTML'
            }],
            docx: [{
                target: 'html',
                conv: baseConverterPath + 'Docx4jDocxToHTML'
            }],
            eml: [{
                target: 'html',
                conv: baseConverterPath + 'MailToHTML'
            }],
            msg: [{
                target: 'html',
                conv: baseConverterPath + 'MailToHTML'
            }],
            xls: [{
                target: 'html',
                conv: baseConverterPath + 'XlsToHTML'
            }],
            xlsx: [{
                target: 'html',
                conv: baseConverterPath + 'XlsToHTML'
            }],
            ods: [{
                target: 'html',
                conv: baseConverterPath + 'XlsToHTML'
            }],
            asciidoc: [{
                target: 'html',
                conv:'org.kimios.converter.impl.AsciiDocToHTML'
            }],
            adoc: [{
                target: 'html',
                conv:'org.kimios.converter.impl.AsciiDocToHTML'
            }],
            ps: [{
                target: 'pdf',
                conv: 'org.kimios.converter.impl.PostscriptToPDF'
            }]
        };
        var extMapping = osMappingModel;
        return extMapping;
    }
};
