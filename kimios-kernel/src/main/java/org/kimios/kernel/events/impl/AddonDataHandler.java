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

package org.kimios.kernel.events.impl;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.extension.impl.DMEntityAttribute;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddonDataHandler extends GenericEventHandler {


    private static Logger log = LoggerFactory.getLogger(AddonDataHandler.class);


    private ObjectMapper objectMapper;

    public AddonDataHandler(ObjectMapper mapper) {
        this.objectMapper = mapper;

        mapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        mapper.getSerializationConfig().addMixInAnnotations(Meta.class, MetaMixIn.class);
    }


    @DmsEvent(eventName = {DmsEventName.DOCUMENT_UPDATE}, when = DmsEventOccur.AFTER)
    public void documentUpdate(Object[] obj, Object retour, EventContext ctx) throws Exception {
        log.debug("Adding meta datas on document update: " + (Long) obj[1]);
        Document doc = FactoryInstantiator.getInstance()
                .getDocumentFactory().getDocument((Long) obj[1]);
        try {
            doc.setAddOnDatas(generateMetaDatas(doc));
            FactoryInstantiator.getInstance().getDocumentFactory().updateDocument(doc);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = {DmsEventName.DOCUMENT_VERSION_UPDATE}, when = DmsEventOccur.AFTER)
    public void documentVersionUpdate(Object[] obj, Object retour, EventContext ctx) throws Exception {
        log.debug("Indexing version update: " + (Long) obj[1]);
        Document doc = FactoryInstantiator.getInstance()
                .getDocumentFactory().getDocument((Long) obj[1]);
        try {
            doc.setAddOnDatas(generateMetaDatas(doc));
            FactoryInstantiator.getInstance().getDocumentFactory().updateDocument(doc);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }

    @DmsEvent(eventName = {DmsEventName.EXTENSION_ENTITY_ATTRIBUTE_SET}, when = DmsEventOccur.AFTER)
    public void entityAttributeSet(Object[] obj, Object retour, EventContext ctx) throws Exception {
        log.debug("Indexing version update: " + (Long) obj[1]);
        DMEntityImpl entity = (DMEntityImpl)FactoryInstantiator.getInstance()
                .getDmEntityFactory().getEntity((Long) obj[1]);
        try {
            entity.setAddOnDatas(generateMetaDatas(entity));
            FactoryInstantiator.getInstance().getDmEntityFactory().updateEntity(entity);
        } catch (Exception e) {
            log.error(" index action Exception on Document " + entity.getUid(), e);
        }
    }


    @DmsEvent(eventName = {DmsEventName.FOLDER_CREATE}, when = DmsEventOccur.AFTER)
    public void folderCreate(Object[] obj, Object retour, EventContext ctx) throws Exception {
        Folder f = null;
        try {
            f = (Folder)EventContext.getParameters().get("virtualFolder");
            List<VirtualFolderMetaData> metaValues = (List)EventContext.getParameters().get("virtualFolderMetas");
            if(f != null && metaValues != null){
                log.debug("Adding meta datas on virtual folder");
                f.setAddOnDatas(generateMetaDatasForFolder(f, metaValues));
                FactoryInstantiator.getInstance().getFolderFactory().updateFolder(f);
                log.debug("folder updated with {}", f.getAddOnDatas());
            }

        } catch (Exception e) {
            log.error(" index action Exception on folder " + f.getUid(), e);
        }
    }


    @DmsEvent(eventName = {DmsEventName.DOCUMENT_COPY}, when = DmsEventOccur.AFTER)
    public void documentCopy(Object[] obj, Object retour, EventContext ctx) throws Exception {
        Document doc = (Document)EventContext.getParameters().get("document");
        try {
            if(doc != null){
                doc.setAddOnDatas(generateMetaDatas(doc));
                FactoryInstantiator.getInstance().getDocumentFactory().updateDocument(doc);
            }
        } catch (Exception e) {
            log.error(" index action Exception on Document " + doc.getUid(), e);
        }
    }



    private String generateMetaDatas(DMEntityImpl entity) {
        /*
            Load Document attribute
         */
        try {
            AddonDatasWrapper wrapper = new AddonDatasWrapper();
            wrapper.setEntityAttributes(entity.getAttributes());
            if(entity instanceof Document){
                List<MetaValue> metaValues = FactoryInstantiator.getInstance().getMetaValueFactory()
                        .getMetaValues(FactoryInstantiator.getInstance().getDocumentVersionFactory().getLastDocumentVersion((Document)entity));
                wrapper.setEntityMetaValues(metaValues);
            }
            return objectMapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            log.error("Error while generating Document addon data", e);
        }
        return null;
    }

    private String generateMetaDatasForFolder(Folder entity, List<VirtualFolderMetaData> metaValues) {
        /*
            Load Document attribute
         */
        try {
            AddonDatasWrapper wrapper = new AddonDatasWrapper();
            wrapper.setEntityAttributes(entity.getAttributes());
            List<MetaValue> metaValuesFinal = new ArrayList<MetaValue>();
            log.debug("processing virtual folder meta datas {}", metaValues);
            for(VirtualFolderMetaData m: metaValues){
                log.debug("generating folder metavalue for meta "
                        + m.getMeta() + " (" + (m.getMeta() != null ? m.getMeta().getMetaType() : " no meta )"));
                if(m.getMeta() == null){
                    //load manually
                    m.setMeta(FactoryInstantiator.getInstance().getMetaFactory().getMeta(m.getMetaId()));
                }
                if(m.getMeta().getMetaType() == MetaType.STRING){
                    MetaStringValue mv = new MetaStringValue();
                    mv.setValue(m.getStringValue());
                    mv.setMeta(m.getMeta());
                    metaValuesFinal.add(mv);
                } else {
                    MetaDateValue mv = new MetaDateValue();
                    mv.setValue(m.getDateValue());
                    mv.setMeta(m.getMeta());
                    metaValuesFinal.add(mv);
                }
            }
            wrapper.setEntityMetaValues(metaValuesFinal);

            String wr = objectMapper.writeValueAsString(wrapper);
            log.debug("final virtual folder meta datas {}. wrapper data: {}", metaValuesFinal, wr);
            return  wr;
        } catch (Exception e) {
            log.error("Error while generating Document addon data", e);
        }
        return null;
    }



    public static abstract class MetaMixIn {

        @JsonIgnore
        abstract MetaFeedImpl getMetaFeedBean();

        @JsonIgnore
        abstract MetaFeedImpl getMetaFeed();

        @JsonIgnore
        abstract DocumentType getDocumentType();



    }

    public static class AddonDatasWrapper {

        private Map<String, DMEntityAttribute> entityAttributes;

        private List<MetaValue> entityMetaValues;

        public Map<String, DMEntityAttribute> getEntityAttributes() {
            return entityAttributes;
        }

        public void setEntityAttributes(Map<String, DMEntityAttribute> entityAttributes) {
            this.entityAttributes = entityAttributes;
        }

        public List<MetaValue> getEntityMetaValues() {
            return entityMetaValues;
        }

        public void setEntityMetaValues(List<MetaValue> entityMetaValues) {
            this.entityMetaValues = entityMetaValues;
        }
    }


}
