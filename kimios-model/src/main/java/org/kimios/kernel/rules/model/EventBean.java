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
package org.kimios.kernel.rules.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventBean
{
    @Column(name = "event_name", nullable = false)
    private int dmsEventName;

    @Column(name = "event_status", nullable = false)
    private int dmsEventStatus;

    public EventBean()
    {
    }

    public EventBean(int eventName, int eventStatus)
    {
        this.dmsEventName = eventName;
        this.dmsEventStatus = eventStatus;
    }

    public int getDmsEventName()
    {
        return dmsEventName;
    }

    public void setDmsEventName(int dmsEventName)
    {
        this.dmsEventName = dmsEventName;
    }

    public int getDmsEventStatus()
    {
        return dmsEventStatus;
    }

    public void setDmsEventStatus(int dmsEventStatus)
    {
        this.dmsEventStatus = dmsEventStatus;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof EventBean) {
            EventBean t = (EventBean) obj;
            return (this.dmsEventName == t.getDmsEventName() && this.dmsEventStatus == t.getDmsEventStatus());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return this.dmsEventName ^ 29 + this.dmsEventStatus ^ 29;
    }
}

