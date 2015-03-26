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

import org.kimios.kernel.converter.ConverterCacheHandler;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.DocumentVersion;
import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
