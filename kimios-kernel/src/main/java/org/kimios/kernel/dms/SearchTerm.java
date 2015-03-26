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
package org.kimios.kernel.dms;

import java.util.Date;

public class SearchTerm
{
    private Meta meta;

    private long documentUid;

    private long documentTypeUid;

    private String documentName;

    private String stringValue;

    private Double numberFrom;

    private Double numberTo;

    private Date dateFrom;

    private Date dateTo;

    private Boolean booleanValue;

    public SearchTerm(Meta meta)
    {
        this.meta = meta;
    }

    public SearchTerm(Meta meta, String stringValue, Double numberFrom, Double numberTo,
            Date dateFrom, Date dateTo, Boolean booleanValue)
    {
        this.meta = meta;
        this.stringValue = stringValue;
        this.numberFrom = numberFrom;
        this.numberTo = numberTo;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.booleanValue = booleanValue;
    }

    public long getDocumentUid()
    {
        return documentUid;
    }

    public void setDocumentUid(long documentUid)
    {
        this.documentUid = documentUid;
    }

    public long getDocumentTypeUid()
    {
        return documentTypeUid;
    }

    public void setDocumentTypeUid(long documentTypeUid)
    {
        this.documentTypeUid = documentTypeUid;
    }

    public String getDocumentName()
    {
        return documentName;
    }

    public void setDocumentName(String documentName)
    {
        this.documentName = documentName;
    }

    public Meta getMeta()
    {
        return meta;
    }

    public void setMeta(Meta meta)
    {
        this.meta = meta;
    }

    public Double getNumberFrom()
    {
        return numberFrom;
    }

    public void setNumberFrom(Double numberFrom)
    {
        this.numberFrom = numberFrom;
    }

    public Double getNumberTo()
    {
        return numberTo;
    }

    public void setNumberTo(Double numberTo)
    {
        this.numberTo = numberTo;
    }

    public Date getDateFrom()
    {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom)
    {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo()
    {
        return dateTo;
    }

    public void setDateTo(Date dateTo)
    {
        this.dateTo = dateTo;
    }

    public Boolean getBooleanValue()
    {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue)
    {
        this.booleanValue = booleanValue;
    }

    public String getStringValue()
    {
        return stringValue;
    }

    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }
}

