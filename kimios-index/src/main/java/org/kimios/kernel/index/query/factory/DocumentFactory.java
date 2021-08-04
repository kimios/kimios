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

package org.kimios.kernel.index.query.factory;

import org.apache.solr.common.SolrDocument;
import org.kimios.kernel.dms.model.MetaType;
import org.kimios.kernel.ws.pojo.DMEntity;
import org.kimios.kernel.ws.pojo.Document;
import org.kimios.kernel.ws.pojo.Folder;
import org.kimios.kernel.ws.pojo.MetaValue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class DocumentFactory {

    public List<DMEntity> getPojosFromSolrInputDocument(List<SolrDocument> docs) {

        ArrayList<DMEntity> documentArrayList = new ArrayList<DMEntity>();

        for (SolrDocument doc : docs) {
            DMEntity basePojo = null;
            if (doc.getFieldValue("DocumentVersionHash") != null && doc.getFieldValue("DocumentVersionHash").equals("folder")) {
                basePojo = new Folder();
                Folder pojo = (Folder) basePojo;
                //pojo.setIndexScore((Float)doc.getFirstValue("score"));
                pojo.setUid((Long) doc.get("DocumentUid"));
                pojo.setName(doc.get("DocumentNameDisplayed").toString());
                pojo.setPath(doc.get("DocumentPath").toString());
                Calendar creationDate = Calendar.getInstance();
                creationDate.setTime((Date) doc.get("DocumentCreationDate"));
                pojo.setCreationDate(creationDate);
                Calendar updateDate = Calendar.getInstance();
                updateDate.setTime((Date) doc.get("DocumentUpdateDate"));
                pojo.setUpdateDate(updateDate);

                String ownerId = doc.get("DocumentOwnerId") != null ? doc.get("DocumentOwnerId").toString() : "";
                pojo.setOwner(ownerId);
                String ownerSource = doc.get("DocumentOwnerSource") != null ? doc.get("DocumentOwnerSource").toString() : "";
                pojo.setOwnerSource(ownerSource);
                pojo.setParentUid((Long) doc.get("DocumentParentId"));


                for (String fieldName : doc.getFieldNames()) {

                    if (fieldName.startsWith("MetaData")) {
                        long metaId = Long.parseLong(fieldName.split("_")[1]);
                        org.kimios.kernel.ws.pojo.MetaValue mv = null;
                        mv = new MetaValue();
                        mv.setMeta(new org.kimios.kernel.ws.pojo.Meta());

                        if (fieldName.startsWith("MetaDataString")) {

                            mv.getMeta().setMetaType(MetaType.STRING);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataNumber")) {
                            mv.getMeta().setMetaType(MetaType.NUMBER);
                            mv.setValue(doc.get(fieldName));
                        } else if (fieldName.startsWith("MetaDataDate")) {
                            mv.getMeta().setMetaType(MetaType.DATE);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataBoolean")) {
                            mv.getMeta().setMetaType(MetaType.BOOLEAN);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataList")) {
                            mv.getMeta().setMetaType(MetaType.LIST);
                            mv.setValue(new ArrayList((Collection) doc.getFieldValue(fieldName)));

                        }
                        mv.setMetaId(metaId);
                        mv.getMeta().setUid(metaId);
                        mv.getMeta().setDocumentTypeUid(doc.getFieldValue("DocumentTypeUid") instanceof List && ((List) doc.getFieldValue("DocumentTypeUid")).size() > 0 ? (Long) ((List) doc.getFieldValue("DocumentTypeUid")).get(0) : -1);
                        pojo.getMetaDatas().put(fieldName, mv);
                    }

                }

            } else {
                basePojo = new Document();
                Document pojo = (Document) basePojo;
                pojo.setIndexScore((Float) doc.getFirstValue("score"));
                pojo.setUid((Long) doc.get("DocumentUid"));
                pojo.setName(doc.get("DocumentNameDisplayed").toString());
                pojo.setPath(doc.get("DocumentPath").toString());
                Calendar creationDate = Calendar.getInstance();
                creationDate.setTime((Date) doc.get("DocumentCreationDate"));
                pojo.setCreationDate(creationDate);
                Calendar updateDate = Calendar.getInstance();
                updateDate.setTime((Date) doc.get("DocumentUpdateDate"));
                pojo.setUpdateDate(updateDate);

                pojo.setLastVersionId((Long) doc.get("DocumentVersionId"));
                pojo.setCustomVersion(doc.get("DocumentVersionCustomVersion") != null ?
                        doc.get("DocumentVersionCustomVersion").toString() : "");
                pojo.setCustomVersionPending(doc.get("DocumentVersionCustomVersionPending") != null ?
                        doc.get("DocumentVersionCustomVersionPending").toString() : "");

                Calendar versionCreationDate = Calendar.getInstance();
                versionCreationDate.setTime((Date) doc.get("DocumentVersionCreationDate"));
                pojo.setVersionCreationDate(versionCreationDate);
                Calendar versionUpdateDate = Calendar.getInstance();
                versionUpdateDate.setTime((Date) doc.get("DocumentVersionUpdateDate"));
                pojo.setVersionUpdateDate(versionUpdateDate);


                pojo.setLastUpdateAuthor(doc.get("DocumentVersionLastUpdateAuthor") != null ?
                        doc.get("DocumentVersionLastUpdateAuthor").toString() : "");

                pojo.setLastUpdateAuthorSource(doc.get("DocumentVersionLastUpdateAuthorSource") != null ?
                        doc.get("DocumentVersionLastUpdateAuthorSource").toString() : "");

                pojo.setLastUpdateAuthor(doc.get("DocumentWorkflowValidatorUser") != null ?
                        doc.get("DocumentWorkflowValidatorUser").toString() : "");

                pojo.setLastUpdateAuthorSource(doc.get("DocumentWorkflowValidatorUserSource") != null ?
                        doc.get("DocumentWorkflowValidatorUserSource").toString() : "");

                String documentTypeName = doc.get("DocumentTypeName") != null ? doc.get("DocumentTypeName").toString() : "";
                pojo.setDocumentTypeName(documentTypeName);


                ArrayList<Long> docTypes = (ArrayList<Long>) doc.get("DocumentTypeUid");

                if (docTypes != null && docTypes.size() > 0)
                    pojo.setDocumentTypeUid(docTypes.get(0));


                String ownerId = doc.get("DocumentOwnerId") != null ? doc.get("DocumentOwnerId").toString() : "";
                pojo.setOwner(ownerId);
                String ownerSource = doc.get("DocumentOwnerSource") != null ? doc.get("DocumentOwnerSource").toString() : "";
                pojo.setOwnerSource(ownerSource);

                String extension = doc.get("DocumentExtension") != null ? doc.get("DocumentExtension").toString() : "";
                pojo.setExtension(extension);

                pojo.setFolderUid((Long) doc.get("DocumentParentId"));

                pojo.setLength((Long) doc.get("DocumentVersionLength"));

                pojo.setCheckedOut((Boolean) (doc.get("DocumentCheckout") != null ? doc.get("DocumentCheckout") : false));
                if (pojo.getCheckedOut()) {
                    Calendar checkoutDate = Calendar.getInstance();
                    checkoutDate.setTime((Date) doc.get("DocumentCheckoutDate"));
                    pojo.setCheckoutDate(checkoutDate);
                    String checkoutOwnerId = doc.get("DocumentCheckoutOwnerId") != null ? doc.get("DocumentCheckoutOwnerId").toString() : "";
                    pojo.setCheckoutUser(checkoutOwnerId);
                    String checkoutOwnerSource = doc.get("DocumentCheckoutOwnerSource") != null ?
                            doc.get("DocumentCheckoutOwnerSource").toString() : "";
                    pojo.setCheckoutUserSource(checkoutOwnerSource);
                }
                pojo.setWorkflowStatusName(doc.get("DocumentWorkflowStatusName") != null ? doc.get("DocumentWorkflowStatusName").toString() : "");
                pojo.setWorkflowStatusUid((Long) (doc.get("DocumentWorkflowStatusUid") != null ? doc.get("DocumentWorkflowStatusUid") : null));
                pojo.setOutOfWorkflow((Boolean) doc.get("DocumentOutWorkflow"));
                pojo.setValidatorUserName(doc.get("DocumentWorkflowValidatorUser") != null ? doc.get("DocumentWorkflowValidatorUser").toString() : "");
                pojo.setValidatorUserSource(doc.get("DocumentWorkflowValidatorUserSource") != null ? doc.get("DocumentWorkflowValidatorUserSource").toString() : "");
                pojo.setWorkflowName(doc.get("DocumentWorkflowName") != null ? doc.get("DocumentWorkflowName").toString() : "");
                pojo.setLastUpdateAuthor(doc.get("DocumentVersionLastUpdateAuthor") != null ?
                        doc.get("DocumentVersionLastUpdateAuthor").toString() : "");
                pojo.setLastUpdateAuthorSource(doc.get("DocumentVersionLastUpdateAuthorSource") != null ?
                        doc.get("DocumentVersionLastUpdateAuthorSource").toString() : "");
                pojo.setAddonDatas((String) doc.get("DocumentRawAddonDatas"));
                pojo.setTags(doc.get("DocumentTags") != null ?
                        (List<String>) doc.get("DocumentTags") :
                        new ArrayList<>());

                for (String fieldName : doc.getFieldNames()) {

                    if (fieldName.startsWith("MetaData")) {
                        long metaId = Long.parseLong(fieldName.split("_")[1]);
                        org.kimios.kernel.ws.pojo.MetaValue mv = null;
                        mv = new MetaValue();
                        mv.setMeta(new org.kimios.kernel.ws.pojo.Meta());

                        if (fieldName.startsWith("MetaDataString")) {

                            mv.getMeta().setMetaType(MetaType.STRING);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataNumber")) {
                            mv.getMeta().setMetaType(MetaType.NUMBER);
                            mv.setValue(doc.get(fieldName));
                        } else if (fieldName.startsWith("MetaDataDate")) {
                            mv.getMeta().setMetaType(MetaType.DATE);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataBoolean")) {
                            mv.getMeta().setMetaType(MetaType.BOOLEAN);
                            mv.setValue(doc.getFieldValue(fieldName));
                        } else if (fieldName.startsWith("MetaDataList")) {
                            mv.getMeta().setMetaType(MetaType.LIST);
                            mv.setValue(new ArrayList((Collection) doc.getFieldValue(fieldName)));

                        }
                        mv.setMetaId(metaId);
                        mv.getMeta().setUid(metaId);
                        mv.getMeta().setDocumentTypeUid(doc.getFieldValue("DocumentTypeUid") instanceof List && ((List) doc.getFieldValue("DocumentTypeUid")).size() > 0 ? (Long) ((List) doc.getFieldValue("DocumentTypeUid")).get(0) : -1);
                        pojo.getMetaDatas().put(fieldName, mv);
                    }

                }


            }


            documentArrayList.add(basePojo);


        }
        return documentArrayList;

    }
}
