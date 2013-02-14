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

package org.kimios.kernel.index.query.model;

import org.kimios.kernel.ws.pojo.Document;

import java.util.List;

/**
 * @author Fabien Alin <fabien.alin@gmail.com>
 */
public class SearchResponse
{

    public SearchResponse()
    {
    }

    public SearchResponse( int results, List<Document> rows )
    {
        this.results = results;
        this.rows = rows;
    }

    public SearchResponse( List<Long> documentIds )
    {
        this.documentIds = documentIds;
    }

    private int results;

    private List<Document> rows;

    private List<Long> documentIds;

    public List<Long> getDocumentIds()
    {
        return documentIds;
    }

    public void setDocumentIds( List<Long> documentIds )
    {
        this.documentIds = documentIds;
    }

    public int getResults()
    {
        return results;
    }

    public void setResults( int results )
    {
        this.results = results;
    }

    public List<Document> getRows()
    {
        return rows;
    }

    public void setRows( List<Document> rows )
    {
        this.rows = rows;
    }
}
