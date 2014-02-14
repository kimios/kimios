package org.kimios.kernel.events.impl;

import org.kimios.kernel.converter.ConverterCacheHandler;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.filetransfer.DataTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 1/26/14
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class PreviewCacheHandler extends GenericEventHandler {

    private static Logger logger = LoggerFactory.getLogger(PreviewCacheHandler.class);

    @DmsEvent(eventName = {DmsEventName.FILE_UPLOAD}, when = DmsEventOccur.AFTER)
    public void documentVersionUpdateUpload(Object[] obj, Object retour, EventContext ctx) throws Exception {

        if (logger.isDebugEnabled())
            logger.debug(" cache reset handling");

        DocumentVersion documentVersion = (DocumentVersion) EventContext.getParameters().get("version");
        if (documentVersion != null) {
            try {
                logger.debug(" cancelling preview cache for " + documentVersion.getUid());
                ConverterCacheHandler.cancelCache(documentVersion.getUid());
                return;
            } catch (Exception e) {
                logger.error(" exception while resetting cache for " + documentVersion.getUid(), e);
            }

        } else {
            logger.debug("version not found after ending upload transaction. Won't cancel cache.");
        }
        Document doc = (Document) ctx.getEntity();
        if (logger.isDebugEnabled())
            logger.debug(" should do it with doc... doc " + doc);
    }
}
