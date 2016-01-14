/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

kimios.editors = {}

kimios.editors.startDocumentEdit = function (doc) {

    if(console){
        console.log(doc);
    }

    var editableTypes = ['txt', 'adoc', 'asciidoc', 'js', 'c', 'htm', 'html'];

    if (doc.extension && editableTypes.indexOf(doc.extension.toLowerCase()) != -1) {

        //request on start edti

        kimios.ajaxRequestWithAnswer('Editor', {
            action: 'StartEdit',
            documentId: doc.uid
        }, function (resp) {



            var editData = Ext.util.JSON.decode(resp.responseText);

            if(console){
                console.log(editData);
                //check cookie

                console.log(Ext.util.Cookies.get('!Proxy!etherpadProxysessionID'));
                console.log(Ext.util.Cookies.get('!Proxy!etherpadProxyauthorID'));
            }
            var iframe = new Ext.Window({
                width: 640,
                height: 480,
                layout: 'fit',
                border: false,
                title: kimios.lang('LiveEditorTitle') +  ' Document: ' + doc.name + '.' + doc.extension,
                maximizable: true,
                modal: true,
                autoScroll: true,
                items: [
                    {
                        html: '<iframe id="editFrame" border="0" width="100%" height="100%" ' +
                        'frameborder="0" marginheight="12" marginwidth="16" scrolling="auto" ' +
                        'style="padding: 16px" ' +
                        'src="' + editData.etherPadUrl + '"></iframe>'
                    }
                ],
                fbar: [
                    {
                        text: kimios.lang('StopEdit'),
                        scope: this,
                        handler: function () {
                            kimios.ajaxRequestWithAnswer('Editor', {
                                action: 'StopEdit',
                                editorData: JSON.stringify(editData)
                            }, function (resp) {
                                iframe.close();
                            });
                        }
                    },
                    {
                        text: kimios.lang('VersionDocument'),
                        scope: this,
                        handler: function () {
                            kimios.ajaxRequestWithAnswer('Editor', {
                                action: 'VersionDocument',
                                editorData: JSON.stringify(editData)
                            }, function (resp) {
                                alert('One version has been added !')
                            });
                        }
                    },
                    {
                        text: kimios.lang('InviteUsers'),
                        scope: this,
                        handler: function () {

                        }
                    }
                ]
            });
            iframe.show();
            iframe.maximize();



        })
    } else {
        //Display Error Message Regarding type. Should also check if editors features are available on the server side
    }
}