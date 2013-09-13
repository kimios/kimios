package org.kimios.kernel.events.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.kimios.kernel.dms.*;
import org.kimios.kernel.dms.extension.impl.DMEntityAttribute;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.kimios.kernel.security.DMEntityACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 9/13/13
 * Time: 2:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class AddonDataHandler extends GenericEventHandler {


    private static Logger log = LoggerFactory.getLogger(AddonDataHandler.class);


    private ObjectMapper objectMapper;

    public AddonDataHandler(ObjectMapper mapper) {
        this.objectMapper = mapper;
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

    public class AddonDatasWrapper {

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
