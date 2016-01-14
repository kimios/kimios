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

package org.kimios.editors.impl.etherpad;

import org.kimios.api.events.annotations.DmsEvent;
import org.kimios.api.events.annotations.DmsEventName;
import org.kimios.api.events.annotations.DmsEventOccur;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.dms.model.Document;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.model.EventContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by farf on 16/01/16.
 */
public class EtherpadEventHandler extends GenericEventHandler {

    private static Logger log = LoggerFactory.getLogger(EtherpadEventHandler.class);

    private EtherpadEditor etherpadEditor;

    protected void setEtherpadEditor(EtherpadEditor etherpadEditor){
        this.etherpadEditor = etherpadEditor;
    }

    @DmsEvent(eventName = { DmsEventName.DOCUMENT_CHECKIN }, when = DmsEventOccur.AFTER)
    public void documentUpdate(Object[] obj, Object retour, EventContext ctx) throws Exception
    {

        Document doc = FactoryInstantiator.getInstance()
                .getDocumentFactory().getDocument((Long) obj[1]);
        log.debug("checking out document {}. will kill existing pad !", ctx.getEntity());
        try {
             if(etherpadEditor != null){
                 etherpadEditor.endDocumentEditFromCheckout(ctx.getSession(), doc.getUid());
             }
        } catch (Exception e) {
            log.error(" error while ending etherpad PAD for doc " + doc.getUid(), e);
        }
    }


}
