/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.index;

import java.util.Date;

import org.apache.lucene.search.Query;
import org.kimios.kernel.exception.IndexException;

public class MetaDateValueClause implements SearchClause
{
    private long metaUid;

    private Date min;

    private Date max;

    public static Date MIN = new Date(0);

    public static Date MAX = new Date(32472140400000L);

    public MetaDateValueClause(long metaUid, Date min, Date max)
    {
        this.metaUid = metaUid;
        this.min = min;
        this.max = max;
    }

    public Query getLuceneQuery() throws IndexException
    {
        try {
            return IndexHelper.getDateRangeQuery("MetaData" + this.metaUid, this.min, this.max);
        } catch (Exception ex) {
            throw new IndexException(ex, "Error during query parsing : " + ex.getMessage());
        }
    }
}

