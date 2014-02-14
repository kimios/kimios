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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.Document;
import org.kimios.kernel.dms.FactoryInstantiator;
import org.kimios.kernel.exception.IndexException;

import java.util.Vector;

public class SearchQuery
{
    private Vector<SearchClause> clauses = new Vector<SearchClause>();

    private DMEntity searchRoot;

    public SearchQuery(DMEntity searchRoot)
    {
        this.searchRoot = searchRoot;
    }

    public void addClause(SearchClause clause)
    {
        clauses.add(clause);
    }

    public Vector<Document> execute(IndexReader reader) throws IndexException
    {
        try {
            if (this.searchRoot != null) {
                this.clauses.add(new DocumentParentClause(this.searchRoot.getPath()));
            }
            Query[] q = new Query[this.clauses.size()];
            for (int i = 0; i < this.clauses.size(); i++) {
                q[i] = this.clauses.get(i).getLuceneQuery();
            }
            Query query = IndexHelper.mergeQueries(IndexHelper.getAnalyzer(), q);
            IndexSearcher searcher = new IndexSearcher(reader);
            final Vector<Document> results = new Vector<Document>();
            final IndexReader readerCpy = reader;
            TopScoreDocCollector dcColl = TopScoreDocCollector.create(Integer.MAX_VALUE, true);
            searcher.search(query, dcColl);
            for (ScoreDoc it : dcColl.topDocs().scoreDocs) {
                try {
                    long uid = Long.parseLong(readerCpy.document(it.doc).get("DocumentUid"));
                    Document r = FactoryInstantiator.getInstance().getDocumentFactory().getDocument(uid);
                    if (r != null) {
                        results.add(r);
                    }
                } catch (Exception ex) {
                }
            }
            return results;
        } catch (Exception ex) {
            throw new IndexException(ex, "Error during query parsing : " + ex.getMessage());
        }
    }
}

