/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

public class DocumentNameAnalysedClause
{
    private String documentNameQuery;

    public DocumentNameAnalysedClause(String documentNameQuery)
    {
        this.documentNameQuery = documentNameQuery;
    }

    public Query getLuceneQuery() throws IndexException
    {
        try {
            return IndexHelper
                    .getStandardQuery("DocumentNameAnalysed", this.documentNameQuery, IndexHelper.getAnalyzer());
        } catch (Exception ex) {
            throw new IndexException(ex, "Error during query parsing : " + ex.getMessage());
        }
    }
}

