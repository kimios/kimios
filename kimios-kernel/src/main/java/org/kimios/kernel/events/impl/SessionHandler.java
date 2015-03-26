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

import org.kimios.kernel.events.GenericEventHandler;
import org.kimios.kernel.events.annotations.DmsEvent;
import org.kimios.kernel.events.annotations.DmsEventName;
import org.kimios.kernel.events.annotations.DmsEventOccur;

public class SessionHandler extends GenericEventHandler
{
    @DmsEvent(eventName = DmsEventName.SESSION_START, when = DmsEventOccur.AFTER)
    public void testEventAfter(Object[] args)
    {
        for (Object it : args) {
            if (it != null) {
                System.out.println(it.toString());
            }
        }
        System.out.println("After");
    }

    @DmsEvent(eventName = DmsEventName.SESSION_START, when = DmsEventOccur.BEFORE)
    public void testEventBefore(Object[] args)
    {
        for (Object it : args) {
            if (it != null) {
                System.out.println(it.toString());
            }
        }
        System.out.println("Before");
    }
}

