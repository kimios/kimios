/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.controller;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.kimios.client.controller.DocumentController;
import org.kimios.client.controller.helpers.XMLGenerators;
import org.kimios.core.DateTranformerExt;
import org.kimios.core.wrappers.DMEntity;
import org.kimios.core.wrappers.DocumentVersionJSON;
import org.kimios.core.wrappers.Meta;
import org.kimios.i18n.InternationalizationManager;
import org.kimios.kernel.ws.pojo.Bookmark;
import org.kimios.kernel.ws.pojo.DocumentVersion;
import org.kimios.kernel.ws.pojo.MetaValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Farf
 */
public class DocumentActionHandler
    extends Controller
{
    public DocumentActionHandler( Map<String, String> parameters )
    {
        super( parameters );
    }

    public String execute()
        throws Exception
    {
        String jsonResp = "";
        if ( action != null )
        {
            if ( action.equalsIgnoreCase( "lastVersion" ) )
            {
                jsonResp = lastVersion();
            }
            if ( action.equals("version") )
            {
                jsonResp = version();
            }
            if ( action.equalsIgnoreCase("metaValues") )
            {
                jsonResp = loadVersionMetaValues();
            }
            if ( action.equalsIgnoreCase( "lastMetaValues" ) )
            {
                jsonResp = loadLastVersionMetaValues();
            }
            if ( action.equalsIgnoreCase( "versions" ) )
            {
                jsonResp = getVersions();
            }
            if ( action.equalsIgnoreCase( "UpdateDocument" ) )
            {
                updateDocument();
            }
            if ( action.equalsIgnoreCase( "relatedDocuments" ) )
            {
                jsonResp = relatedDocuments();
            }
            if ( action.equalsIgnoreCase( "bookmarks" ) )
            {
                jsonResp = bookmarks();
            }
            if ( action.equalsIgnoreCase( "recents" ) )
            {
                jsonResp = recentItems();
            }
            if ( action.equalsIgnoreCase( "viewTrash" ) )
            {
                jsonResp = viewTrash();
            }
            if ( action.equalsIgnoreCase( "restoreFromTrash" ) )
            {
                restoreFromTrash();
            }
            if ( action.equalsIgnoreCase( "addToTrash" ) )
            {
                addToTrash();
            }
            if ( action.equals( "AddBookmarkItem" ) )
            {
                addBookmarkItem();
            }
            if ( action.equals( "AddBookmarksItem" ) )
            {
                addBookmarksItem();
            }
            if ( action.equals( "RemoveBookmarkItem" ) )
            {
                removeBookmarkItem();
            }
            if ( action.endsWith( "AddRelatedDocument" ) )
            {
                addRelatedDocument();
            }
            if ( action.endsWith( "RemoveRelatedDocument" ) )
            {
                removeRelatedDocument();
            }
            if ( action.equals( "checkoutDocument" ) )
            {
                checkoutDocument();
            }
            if ( action.equals( "checkinDocument" ) )
            {
                checkinDocument();
            }

            return jsonResp;
        }
        return "NO ACTION";
    }

    private String lastVersion()
        throws Exception
    {
        org.kimios.kernel.ws.pojo.Document doc =
            documentController.getDocument( sessionUid, Long.parseLong( parameters.get( "documentUid" ) ) );
        DocumentVersion dv = documentVersionController.getLastDocumenVersion(sessionUid, doc.getUid());
        DocumentVersionJSON dvj = new DocumentVersionJSON( dv );
        String jsonResp = "[" + new JSONSerializer().serialize( dvj ) + "]";
        return jsonResp;
    }

    private String getVersions()
        throws Exception
    {
        DocumentVersion[] versions = documentVersionController.getDocumentVersions(sessionUid, Long.parseLong(
                parameters.get("documentUid")));
        List<DocumentVersionJSON> vJson = new ArrayList<DocumentVersionJSON>();
        for ( DocumentVersion v : versions )
        {
            vJson.add( new DocumentVersionJSON( v ) );
        }
        String jsonResp = new JSONSerializer().serialize( vJson );
        return jsonResp;
    }

    private String version()
        throws Exception
    {
        DocumentVersion dv = documentVersionController.getDocumentVersion(sessionUid, Long.parseLong(
                parameters.get("versionUid")));
        DocumentVersionJSON dvj = new DocumentVersionJSON( dv );
        String jsonResp = "[" + new JSONSerializer().serialize(dvj) + "]";
        return jsonResp;
    }

    private String loadVersionMetaValues()
        throws Exception
    {
        DocumentVersion dv = documentVersionController.getDocumentVersion(sessionUid, Long.parseLong(
                parameters.get("versionUid")));
        List<Meta> metaValues = loadVersionMetaValues(dv);
        String jsonResp = "{'documentTypeUid':" + dv.getDocumentTypeUid() + ",'metaValues':";
        jsonResp += new JSONSerializer().transform( new DateTranformerExt(
            InternationalizationManager.getSingleValue( parameters.get( "selected_lang" ), "SimpleDateForm" ) ),
                                                    Date.class ).serialize( metaValues ) + "}";
        return jsonResp;
    }

    private String loadLastVersionMetaValues()
        throws Exception
    {
        org.kimios.kernel.ws.pojo.Document doc =
            documentController.getDocument(sessionUid, Long.parseLong(parameters.get("uid")));

        DocumentVersion docVersion = documentVersionController.getLastDocumenVersion(sessionUid, doc.getUid());



        List<Meta> items = loadVersionMetaValues(docVersion);

        return "{'lastMetaValues':" + new JSONSerializer().transform( new DateTranformerExt(
            InternationalizationManager.getSingleValue( parameters.get( "selected_lang" ), "SimpleDateForm" ) ),
                                                                      Date.class ).serialize( items ) + "}";

    }

    private List<Meta> loadVersionMetaValues(DocumentVersion docVersion)
        throws Exception
    {
        List<Meta> metaValues = new ArrayList<Meta>();
        org.kimios.kernel.ws.pojo.Meta[] mServ =
            documentVersionController.getMetas(sessionUid, docVersion.getDocumentTypeUid());
        List<MetaValue> values = documentVersionController.getMetaValues(sessionUid, docVersion.getUid());
        List<org.kimios.kernel.ws.pojo.Meta> toRemove = new ArrayList<org.kimios.kernel.ws.pojo.Meta>();
        for ( MetaValue mValue : values )
        {
            log.debug( mValue.getMeta().getName() + " --> " + mValue.getValue() );
            Object value = null;
            if(mValue.getMeta().getMetaType() == 3 && mValue.getValue() != null){
                value = new Date( (Long)mValue.getValue() );
            } else {
                value = mValue.getValue();
            }
            metaValues.add( new Meta( mValue.getMetaId(), mValue.getMeta().getName(), value,
                                      mValue.getMeta().getMetaType(),
                                        mValue.getMeta().getMetaFeedUid(),
                                        mValue.getMeta().isMandatory(), mValue.getMeta().getPosition() ) );
            for ( org.kimios.kernel.ws.pojo.Meta meta : mServ )
            {
                if ( mValue.getMeta().getUid() == meta.getUid() )
                {
                    toRemove.add( meta );
                    break;
                }
            }
        }
        for ( org.kimios.kernel.ws.pojo.Meta m : mServ )
        {
            if ( !toRemove.contains( m ) )
            {
                metaValues.add( new Meta( m.getUid(), m.getName(), null, m.getMetaType(), m.getMetaFeedUid(), m.isMandatory(), m.getPosition() ) );
            }
        }


        return metaValues;
    }

    private String relatedDocuments()
        throws Exception
    {
        org.kimios.kernel.ws.pojo.Document[] rels =
            documentController.getRelatedDocuments(sessionUid, Long.parseLong(parameters.get("documentUid")));
        ArrayList<DMEntity> relDocs = new ArrayList<DMEntity>();
        for ( org.kimios.kernel.ws.pojo.Document d : rels )
        {
            relDocs.add( new DMEntity( d ) );
        }
        String jsonResp =
            new JSONSerializer().exclude(
                "class" ).serialize( relDocs );
        return jsonResp;
    }

    private String viewTrash()
            throws Exception
    {
        List<org.kimios.kernel.ws.pojo.DMEntity> trashedItems =
                extensionController.viewTrash(sessionUid, 0, Integer.MAX_VALUE);
        ArrayList<DMEntity> relDocs = new ArrayList<DMEntity>();
        for ( org.kimios.kernel.ws.pojo.DMEntity d : trashedItems )
        {
            relDocs.add( new DMEntity( d ) );
        }
        String jsonResp =
                new JSONSerializer().exclude(
                        "class" ).serialize( relDocs );
        return jsonResp;
    }

    private void restoreFromTrash()
            throws Exception
    {
        Long documentId = Long.parseLong(parameters.get("documentId"));
        extensionController.restoreDocumentFromTrash(sessionUid, documentId);
    }

    private void addToTrash()
            throws Exception
    {
        Long documentId = Long.parseLong(parameters.get("documentId"));
        extensionController.addDocumentToTrash(sessionUid, documentId);
    }


    private String bookmarks()
        throws Exception
    {
        Bookmark[] rels = documentController.getBookmarks( sessionUid );
        ArrayList<DMEntity> tmp = new ArrayList<DMEntity>();
        for ( Bookmark b : rels )
        {
            if(b.getEntity() != null){
                tmp.add( new DMEntity(b.getEntity()) );
            } else {
                DMEntity t = getEntity( b.getDmEntityType(), b.getDmEntityUid() );
                if ( t != null )
                {
                    tmp.add( t );
                }
            }
        }
        String jsonResp = new JSONSerializer().exclude( "class" ).serialize( tmp );

        return jsonResp;
    }

    private String recentItems()
        throws Exception
    {
        Bookmark[] rels = documentController.getRecentItems( sessionUid );
        ArrayList<DMEntity> tmp = new ArrayList<DMEntity>();
        for ( Bookmark b : rels )
        {
            if(b.getEntity() != null){
                tmp.add( new DMEntity(b.getEntity()));
            } else {
                DMEntity t = getEntity( b.getDmEntityType(), b.getDmEntityUid() );
                if ( t != null )
                {
                    tmp.add( t );
                }
            }
        }
        String jsonResp = new JSONSerializer().exclude( "class" ).serialize( tmp );
        return jsonResp;
    }

    private void updateDocument()
        throws Exception
    {
        DocumentController fsm = documentController;
        long docUid = Long.parseLong( parameters.get( "uid" ) );
        boolean newVersion =
            ( parameters.get( "newVersion" ) != null && ( parameters.get( "newVersion" ).equals( "true" )
                ? true
                : false ) );
        org.kimios.kernel.ws.pojo.Document d = fsm.getDocument( sessionUid, docUid );
        if ( !d.getName().equals( parameters.get( "name" ) ) )
        {
            d.setName( parameters.get( "name" ) );
            fsm.updateDocument( sessionUid, d );
        }
        long docType = Long.parseLong( parameters.get( "documentTypeUid" ) );

        String sec = parameters.get( "sec" );
        String metaValues = parameters.get( "metaValues" );
        boolean changeSecurity = true;
        if ( parameters.get( "changeSecurity" ) != null )
        {
            changeSecurity = Boolean.parseBoolean( parameters.get( "changeSecurity" ) );
        }

        if ( changeSecurity == true )
        {
            securityController.updateDMEntitySecurities( sessionUid, docUid, 3, false, false,
                                                         DMEntitySecuritiesParser.parseFromJson( sec, docUid, 3 ) );
        }
        if ( newVersion )
        {
            documentVersionController.createDocumentVersionFromLatest( sessionUid, docUid );
        }
        Map<org.kimios.kernel.ws.pojo.Meta, String> mMetasValues =
            DMEntitySecuritiesParser.parseMetasValuesFromJson( sessionUid, metaValues, documentVersionController );
        String xmlMeta = XMLGenerators.getMetaDatasDocumentXMLDescriptor( mMetasValues, "yyyy-MM-dd" );
        documentVersionController.updateDocumentVersion( sessionUid, d.getUid(), docType, xmlMeta );
    }

    private void addBookmarkItem()
        throws Exception
    {
        int dmEntityType = Integer.parseInt( parameters.get( "type" ) );
        long dmEntityUid = Long.parseLong( parameters.get( "uid" ) );
        documentController.addBookmark( sessionUid, dmEntityUid, dmEntityType );
    }

    private void addBookmarksItem()
        throws Exception
    {
        List<Map<String, Object>> dmEntities = (ArrayList<Map<String, Object>>) new JSONDeserializer().deserialize(
            parameters.get( "dmEntityPojosJson" ) );
        for ( Map<String, Object> dmEntity : dmEntities )
        {
            long dmEntityUid = Long.parseLong( String.valueOf( dmEntity.get( "uid" ) ) );
            int dmEntityType = Integer.parseInt( String.valueOf( dmEntity.get( "type" ) ) );
            documentController.addBookmark( sessionUid, dmEntityUid, dmEntityType );
        }
    }

    private void removeBookmarkItem()
        throws Exception
    {
        int dmEntityType = Integer.parseInt( parameters.get( "type" ) );
        long dmEntityUid = Long.parseLong( parameters.get( "uid" ) );
        documentController.removeBookmark( sessionUid, dmEntityUid, dmEntityType );
    }

    private void addRelatedDocument()
        throws Exception
    {
        long dmEntityUid = Long.parseLong( parameters.get( "uid" ) );
        long relatedDocUid = Long.parseLong( parameters.get( "relatedUid" ) );
        if ( dmEntityUid != relatedDocUid )
        {
            documentController.addRelatedDocument( sessionUid, dmEntityUid, relatedDocUid );
        }
    }

    private void removeRelatedDocument()
        throws Exception
    {
        long dmEntityUid = Long.parseLong( parameters.get( "uid" ) );
        long relatedDocUid = Long.parseLong( parameters.get( "relatedUid" ) );
        documentController.removeRelatedDocument( sessionUid, dmEntityUid, relatedDocUid );
    }

    private void checkoutDocument()
        throws Exception
    {
        documentController.checkoutDocument( sessionUid,
                                             Long.parseLong( String.valueOf( parameters.get( "documentUid" ) ) ) );
    }

    private void checkinDocument()
        throws Exception
    {
        documentController.checkinDocument( sessionUid,
                                            Long.parseLong( String.valueOf( parameters.get( "documentUid" ) ) ) );
    }

    private DMEntity getEntity( int type, long dmEntityUid )
        throws Exception
    {
        switch ( type )
        {
            case 1:
                return new DMEntity( workspaceController.getWorkspace( sessionUid, dmEntityUid ) );
            case 2:
                return new DMEntity( folderController.getFolder( sessionUid, dmEntityUid ) );
            case 3:
                return new DMEntity( documentController.getDocument( sessionUid, dmEntityUid ) );

            default:
                return null;
        }
    }
}

