/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2015  DevLib'
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

package org.kimios.kernel.index;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.common.SolrInputDocument;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.model.*;
import org.kimios.kernel.events.impl.AddonDataHandler;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.index.query.model.DocumentIndexStatus;
import org.kimios.kernel.security.model.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by farf on 28/10/15.
 */
public class SolrDocGenerator {


    private static Logger log = LoggerFactory.getLogger(SolrDocGenerator.class);


    private Document document;
    private DocumentVersion version;
    private DocumentWorkflowStatusRequest req;
    private DocumentWorkflowStatus st;
    private List<MetaValue> values;
    private List<DMEntityACL> acls;
    private Lock documentLock;

    private ObjectMapper mp;
    private WorkflowStatus stOrg;


    public SolrDocGenerator(Document document, ObjectMapper mp){
        this.document = document;
        this.mp = mp;
        loadDocumentData(document);
    }

    private void loadDocumentData(Document document){

        TransactionHelper txHelper = new TransactionHelper();
        Object txStatus = null;
        boolean shouldRollback = false;

        if(!txHelper.isRunningInTransaction()){
            try {
                txStatus = txHelper.startNew(null);
                shouldRollback = true;
            }catch (Exception ex){
                log.error("error while beginning tx", ex);
            }
        }

        version = FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion(document);
        req = FactoryInstantiator.getInstance().getDocumentWorkflowStatusRequestFactory().getLastPendingRequest(
                document);
        st = FactoryInstantiator.getInstance().getDocumentWorkflowStatusFactory().getLastDocumentWorkflowStatus(
                document.getUid());
        if(st != null){
            stOrg = FactoryInstantiator.getInstance().getWorkflowStatusFactory().getWorkflowStatus(
                    st.getWorkflowStatusUid());
        }
        values = FactoryInstantiator.getInstance().getMetaValueFactory().getMetaValues(version);
        acls = org.kimios.kernel.security.FactoryInstantiator.getInstance().getDMEntitySecurityFactory().getDMEntityACL(
                document);

        documentLock = FactoryInstantiator.getInstance()
                .getLockFactory().getDocumentLock(document);


        if(shouldRollback){
            try {
                txHelper.rollback(txStatus);
            }catch (Exception ex){
                log.error("error while rollbacking tx", ex);
            }
        }
    }


    protected SolrInputDocument toSolrInputDocument(boolean flush,
                                                    boolean updateMetasWrapper,
                                                    DocumentIndexStatus documentIndexStatus )
            throws DataSourceException, ConfigException {

        documentIndexStatus.setEntityId(document.getUid());
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat utcDateParser = new SimpleDateFormat("dd-MM-yyyy");
        utcDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat utcDateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        log.debug("processing document {} for path {}", document.getUid(), document.getPath());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", document.getUid());
        doc.addField("DocumentName", document.getName().toLowerCase());
        doc.addField("DocumentNameDisplayed", document.getName());
        doc.addField("DocumentNameAnalysed", document.getName());
        if (document.getExtension() != null) {
            doc.addField("DocumentExtension", document.getExtension().toLowerCase());
        }
        doc.addField("DocumentOwner", document.getOwner() + "@" + document.getOwnerSource());
        doc.addField("DocumentOwnerId", document.getOwner());
        doc.addField("DocumentOwnerSource", document.getOwnerSource());
        doc.addField("DocumentPath", document.getPath().replaceAll("__TRASHED_ENTITY__", ""));
        doc.addField("DocumentParent", document.getFolder().getPath().replaceAll("__TRASHED_ENTITY__", "") + "/");
        doc.addField("DocumentParentId", document.getFolder().getUid());


        if (version == null) {
            log.error("Document {} has no version", document.getUid());
            return null;
        }
        //standard datas
        doc.addField("DocumentCreationDate", document.getCreationDate());
        doc.addField("DocumentUpdateDate", document.getUpdateDate());
        doc.addField("DocumentVersionId", version.getUid());
        doc.addField("DocumentVersionCreationDate", version.getCreationDate());
        doc.addField("DocumentVersionUpdateDate", version.getModificationDate());
        doc.addField("DocumentVersionOwner", version.getAuthor() + "@" + version.getAuthorSource());
        doc.addField("DocumentVersionOwnerId", version.getAuthor());
        doc.addField("DocumentVersionOwnerSource", version.getAuthorSource());
        doc.addField("DocumentVersionLength", version.getLength());
        doc.addField("DocumentVersionHash", version.getHashMD5() + ":" + version.getHashSHA1());
        doc.addField("DocumentVersionCustomVersion", version.getCustomVersion());
        doc.addField("DocumentVersionCustomVersionPending", version.getCustomVersionPending());

        doc.addField("DocumentCheckout", documentLock != null);

        if (documentLock != null) {
            doc.addField("DocumentCheckoutOwnerId", documentLock.getUser());
            doc.addField("DocumentCheckoutOwnerSource", documentLock.getUserSource());
            doc.addField("DocumentCheckoutDate", documentLock.getDate());
        }

        boolean outOfWorkflow = true;
        if (req != null) {
            outOfWorkflow = false;
        }
        if (st != null) {
            doc.addField("DocumentWorkflowStatusName", stOrg.getName());
            doc.addField("DocumentWorkflowStatusUid", st.getWorkflowStatusUid());
            if (stOrg.getSuccessorUid() == null) {
                outOfWorkflow = true;
            }
        }
        doc.addField("DocumentOutWorkflow", outOfWorkflow);
        if (version.getDocumentType() != null && values != null) {
            doc.addField("DocumentTypeUid", version.getDocumentType().getUid());
            doc.addField("DocumentTypeName", version.getDocumentType().getName());
            for (MetaValue value : values) {
                switch (value.getMeta().getMetaType()) {
                    case MetaType.STRING:
                        doc.addField("MetaDataString_" + value.getMetaUid(),
                                value.getValue() != null ? ((String) value.getValue().toString()) : null);
                        break;
                    case MetaType.BOOLEAN:
                        doc.addField("MetaDataBoolean_" + value.getMetaUid(), value.getValue());
                        break;
                    case MetaType.NUMBER:
                        doc.addField("MetaDataNumber_" + value.getMetaUid(), value.getValue());
                        break;
                    case MetaType.DATE:

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        if (value != null && value.getValue() != null) {
                            //reparse date for local
                            String dateString = dateParser.format(value.getValue());
                            try {
                                cal.setTime(utcDateParser.parse(dateString));
                                doc.addField("MetaDataDate_" + value.getMetaUid(), cal.getTime());
                            } catch (Exception ex) {
                                log.error("error while reparsing meta data date {} {} {}", value.getMeta().getName(), value, dateString);
                            }
                        }
                        break;
                    case MetaType.LIST:
                        if (value != null && value.getValue() != null) {
                            List<String> items = ((MetaListValue) value).getValue();
                            for (String u : items)
                                doc.addField("MetaDataList_" + value.getMetaUid(), u);
                        }
                        break;
                    default:
                        doc.addField("MetaData_" + value.getMetaUid(), value.getValue());
                        break;
                }
            }
        }
        for (String attribute : document.getAttributes().keySet()) {
            if (attribute.equals("SearchTag")) {
                //Custom parsing
                String[] tags = document.getAttributes().get(attribute) != null &&
                        document.getAttributes().get(attribute).getValue() != null ?
                        document.getAttributes().get(attribute).getValue().split("\\|\\|\\|") : new String[]{};
                for (String tag : tags) {
                    doc.addField("Attribute_SEARCHTAG", tag);
                }
            } else {
                doc.addField("Attribute_" + attribute.toUpperCase(),
                        document.getAttributes().get(attribute).getValue());
            }

        }
        if ((values != null && values.size() > 0) || (document.getAttributes() != null || document.getAttributes().size() > 0)) {
            AddonDataHandler.AddonDatasWrapper wrapper = new AddonDataHandler.AddonDatasWrapper();
            wrapper.setEntityAttributes(document.getAttributes());
            wrapper.setEntityMetaValues(values);
            try {
                document.setAddOnDatas(mp.writeValueAsString(wrapper));
            } catch (Exception ex) {
                log.error("error while generation addon meta field", ex);
            }
        }
        if (document.getAddOnDatas() != null && document.getAddOnDatas().length() > 0) {
            doc.addField("DocumentRawAddonDatas", document.getAddOnDatas());
        }
        //acls
        for (int i = 0; i < acls.size(); i++) {
            doc.addField("DocumentACL", acls.get(i).getRuleHash());
        }
        return doc;
    }

    protected SolrInputDocument toSolrContentInputDocument(boolean flush,
                                                    boolean updateMetasWrapper,
                                                    Map<String, Object> addonFields,
                                                    DocumentIndexStatus documentIndexStatus )
            throws DataSourceException, ConfigException {

        documentIndexStatus.setEntityId(document.getUid());
        SimpleDateFormat dateParser = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat utcDateParser = new SimpleDateFormat("dd-MM-yyyy");
        utcDateParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat utcDateTimeParser = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        utcDateTimeParser.setTimeZone(TimeZone.getTimeZone("UTC"));
        log.debug("processing document {} for path {}", document.getUid(), document.getPath());
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("DocumentUid", document.getUid());
        if (version == null) {
            log.error("Document {} has no version", document.getUid());
            return null;
        }
        doc.addField("DocumentVersionLength", version.getLength());
        doc.addField("DocumentVersionHash", version.getHashMD5() + ":" + version.getHashSHA1());
        doc.addField("VersionFileName", version.getStoragePath());

        if(addonFields != null && addonFields.size() > 0){
            for(String solrInputField: addonFields.keySet())
                doc.addField(solrInputField, addonFields.get(solrInputField));
        }
        return doc;
    }
}
