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

package org.kimios.kernel.index.query.model;

import org.kimios.kernel.ws.pojo.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private SearchRequest temporaryRequest;

    private boolean facetResponse;

    private List<Document> rows;

    private List<Long> documentIds;

    private String virtualPath = "";

    private HashMap facetsData;

    public Map getFacetsData()
    {
        return facetsData;
    }

    public void setFacetsData( HashMap facetsData )
    {
        this.facetsData = facetsData;
    }

    public boolean isFacetResponse()
    {
        return facetResponse;
    }

    public void setFacetResponse( boolean facetResponse )
    {
        this.facetResponse = facetResponse;
    }

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

    public String getVirtualPath()
    {
        return virtualPath;
    }

    public void setVirtualPath( String virtualPath )
    {
        this.virtualPath = virtualPath;
    }

    public SearchRequest getTemporaryRequest() {
        return temporaryRequest;
    }

    public void setTemporaryRequest(SearchRequest temporaryRequest) {
        this.temporaryRequest = temporaryRequest;
    }
}
