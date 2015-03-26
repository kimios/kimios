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

import org.apache.lucene.search.Query;
import org.kimios.kernel.exception.IndexException;

public class MetaStringValueClause implements SearchClause
{
    private long metaUid;

    private String query;

    public MetaStringValueClause(long metaUid, String query)
    {
        this.metaUid = metaUid;
        this.query = query;
    }

    public Query getLuceneQuery() throws IndexException
    {
        try {
            return IndexHelper.getWildCardQuery("MetaData" + this.metaUid, "*" + this.query + "*");
        } catch (Exception ex) {
            throw new IndexException(ex, "Error during query parsing : " + ex.getMessage());
        }
    }
}

