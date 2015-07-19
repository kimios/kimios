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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.search.SyntaxError;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.utils.configuration.ConfigurationManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

public class IndexHelper
{


    private static final Version LuceneVersion = Version.LUCENE_46;

    public static String EMPTY_STRING = "";

    public static Query getLongRangeQuery(String fieldName, long min, long max) throws SyntaxError
    {
        String q = fieldName + ":[" + NumberUtils.pad(min) + " TO " + NumberUtils.pad(max) + "]";
        return new QueryParser(LuceneVersion, fieldName, null).parse(q);
    }

    public static Query getDateRangeQuery(String fieldName, Date min, Date max) throws SyntaxError
    {
        String q = fieldName + ":[" + NumberUtils.pad(min.getTime()) + " TO " + NumberUtils.pad(max.getTime()) + "]";
        return new QueryParser(LuceneVersion, fieldName, null).parse(q);
    }


    @Deprecated
    public static Query getStandardQuery(String fieldName, String clause, Analyzer a) throws SyntaxError
    {
        String q = fieldName + ":" + clause;
        return new QueryParser(LuceneVersion, fieldName, null).parse(q);
    }

    public static Query getWildCardQuery(String fieldName, String clause) throws SyntaxError
    {
        return new WildcardQuery(new Term(fieldName, clause));
    }

    public static Query mergeQueries(Analyzer a, Query[] q) throws SyntaxError
    {
        String r = "";
        for (int i = 0; i < q.length; i++) {
            if (i == 0) {
                r += "(" + q[i].toString() + ")";
            } else {
                r += "AND (" + q[i].toString() + ")";
            }
        }
        return new QueryParser(LuceneVersion, "body", null).parse(r);
    }
    public static Analyzer getAnalyzer()
    {
        try {
            Class<?> analyserClass = Class.forName(ConfigurationManager.getValue(Config.DEFAULT_INDEX_ANALYSER));
            Constructor<?> cAnalyser = analyserClass.getConstructor(Version.class);
            return (Analyzer) cAnalyser.newInstance(LuceneVersion);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ConfigException e) {
            e.printStackTrace();
            return null;
        }
    }

}

