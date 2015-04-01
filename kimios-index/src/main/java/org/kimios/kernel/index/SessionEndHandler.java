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

import org.kimios.kernel.events.EventContext;
import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;
import org.kimios.kernel.index.query.factory.SearchRequestFactory;
import org.kimios.kernel.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by farf on 1/19/15.
 */
public class SessionEndHandler extends GenericEventHandler
{

    private static Logger logger = LoggerFactory.getLogger(SessionEndHandler.class);


    private SearchRequestFactory requestFactory;

    public SessionEndHandler(SearchRequestFactory factory){
        this.requestFactory = factory;
    }

    @DmsEvent(eventName = DmsEventName.SESSION_STOP, when = DmsEventOccur.AFTER)
    public void testEventAfter(Object[] args, Object retour, EventContext ctx)
    {

        List<Session> items = (List)ctx.getParameters().get("sessions");
        logger.debug("should remove sessions. count: {}", items.size());
        if(items != null && items.size() > 0){
            for(Session s: items){
                logger.debug("removing quick searches for {}", s.getUid());
                requestFactory.deleteSearchRequestBySession(s.getUid());
            }
        }
    }

}
