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
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.Version;
import org.apache.solr.parser.QueryParser;
import org.apache.solr.search.SyntaxError;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.configuration.Config;
import org.kimios.kernel.exception.IndexException;
import org.kimios.kernel.security.DMSecurityRule;
import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.security.Session;
import org.kimios.kernel.user.Group;
import org.kimios.utils.configuration.ConfigurationManager;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class IndexHelper
{
    public static String EMPTY_STRING = "";

    public static Query getLongRangeQuery(String fieldName, long min, long max) throws SyntaxError
    {
        String q = fieldName + ":[" + NumberUtils.pad(min) + " TO " + NumberUtils.pad(max) + "]";
        return new QueryParser(Version.LUCENE_46, fieldName, null).parse(q);
    }

    public static Query getDateRangeQuery(String fieldName, Date min, Date max) throws SyntaxError
    {
        String q = fieldName + ":[" + NumberUtils.pad(min.getTime()) + " TO " + NumberUtils.pad(max.getTime()) + "]";
        return new QueryParser(Version.LUCENE_46, fieldName, null).parse(q);
    }


    @Deprecated
    public static Query getStandardQuery(String fieldName, String clause, Analyzer a) throws SyntaxError
    {
        String q = fieldName + ":" + clause;
        return new QueryParser(Version.LUCENE_46, fieldName, null).parse(q);
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
        return new QueryParser(Version.LUCENE_46, "body", null).parse(r);
    }

    public static Field getAnalyzedField(String fieldName, String value)
    {
        return new Field(fieldName, value, Store.YES, Index.ANALYZED);
    }

    public static Field getAnalyzedNotStoredField(String fieldName, String value)
    {
        return new Field(fieldName, value, Store.NO, Index.ANALYZED);
    }

    public static Field getAnalyzedNotStoredFromReaderField(String fieldName, Reader value)
    {
        Field f = new Field(fieldName, EMPTY_STRING, Store.NO, Index.ANALYZED);
        f.setReaderValue(value);
        return f;
    }

    public static Field getUnanalyzedField(String fieldName, Object value)
    {
        if (value.getClass().equals(Date.class)) {
            return new Field(fieldName, NumberUtils.pad(((Date) value).getTime()), Store.YES, Index.NOT_ANALYZED);
        }
        if (value.getClass().equals(Calendar.class)) {
            return new Field(fieldName, NumberUtils.pad(((Calendar) value).getTime().getTime()), Store.YES,
                    Index.NOT_ANALYZED);
        }
        if (value.getClass().equals(Timestamp.class)) {
            return new Field(fieldName, NumberUtils.pad(((Timestamp) value).getTime()), Store.YES, Index.NOT_ANALYZED);
        }
        if (value.getClass().equals(Long.class)) {
            return new Field(fieldName, NumberUtils.pad((Long) value), Store.YES, Index.NOT_ANALYZED);
        }
        if (value.getClass().equals(Integer.class)) {
            return new Field(fieldName, NumberUtils.pad((Long) value), Store.YES, Index.NOT_ANALYZED);
        }
        if (value.getClass().equals(Double.class)) {
            return new Field(fieldName, NumberUtils.pad(Math.round((Double) value)), Store.YES, Index.NOT_ANALYZED);
        }
        return new Field(fieldName, value.toString(), Store.YES, Index.NOT_ANALYZED);
    }

    public static Analyzer getAnalyzer()
    {
        try {
            Class<?> analyserClass = Class.forName(ConfigurationManager.getValue(Config.DEFAULT_INDEX_ANALYSER));
            Constructor<?> cAnalyser = analyserClass.getConstructor(Version.class);
            return (Analyzer) cAnalyser.newInstance(Version.LUCENE_36);
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

    public static Query getACLQuery(Session session) throws IndexException
    {
        BooleanQuery.setMaxClauseCount(10240);
        BooleanQuery q1 = new BooleanQuery();
        q1.add(new WildcardQuery(new Term("DocumentOwner", session.getUserName() + "@" + session.getUserSource())),
                Occur.SHOULD);
        q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        DMSecurityRule.READRULE).getRuleHash())), Occur.SHOULD);
        q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        DMSecurityRule.WRITERULE).getRuleHash())), Occur.SHOULD);
        q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        DMSecurityRule.FULLRULE).getRuleHash())), Occur.SHOULD);
        for (Group g : session.getGroups()) {
            q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                    .getInstance(g.getGid(), session.getUserSource(), SecurityEntityType.GROUP, DMSecurityRule.READRULE)
                    .getRuleHash())), Occur.SHOULD);
            q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                    .getInstance(g.getGid(), session.getUserSource(), SecurityEntityType.GROUP,
                            DMSecurityRule.WRITERULE).getRuleHash())), Occur.SHOULD);
            q1.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                    .getInstance(g.getGid(), session.getUserSource(), SecurityEntityType.GROUP, DMSecurityRule.FULLRULE)
                    .getRuleHash())), Occur.SHOULD);
        }
        BooleanQuery q2 = new BooleanQuery();
        q2.add(new WildcardQuery(new Term("DocumentOwner", session.getUserName() + "@" + session.getUserSource())),
                Occur.MUST_NOT);
        q2.add(new WildcardQuery(new Term("DocumentACL", DMSecurityRule
                .getInstance(session.getUserName(), session.getUserSource(), SecurityEntityType.USER,
                        DMSecurityRule.NOACCESS).getRuleHash())), Occur.MUST);
        BooleanQuery q3 = new BooleanQuery();
        q3.add(q1, Occur.MUST);
        q3.add(q2, Occur.MUST_NOT);
        return q3;
    }
}

