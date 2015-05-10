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

package org.kimios.kernel.index.query.factory;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.LongType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.index.query.model.SearchRequest;
import org.kimios.kernel.index.query.model.SearchRequestACL;
import org.kimios.kernel.index.query.model.SearchRequestSecurity;
import org.kimios.kernel.security.*;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SearchRequestSecurityFactory extends HFactory {
    final Logger log = LoggerFactory.getLogger(SearchRequestSecurityFactory.class);


    public List<SearchRequest> authorizedRequests(List<SearchRequest> e, String userName, String userSource,
                                                  List<String> hashs, List<String> noAccessHash)
            throws ConfigException, DataSourceException {
        try {


            if (e.size() == 0)
                return new ArrayList<SearchRequest>();

            List<Long> requestsIds = new ArrayList<Long>();
            for (SearchRequest z : e) {
                requestsIds.add(z.getId());
            }

            String rightQuery =
                    "select distinct s.id from searches s left join search_request_acl acl on " +
                            "(s.id = acl.search_request_id) where " +
                            " (acl.rule_hash in (:hash) or (s.owner = :userName and s.owner_source = :userSource))"
                            + " and s.id in (:requestIds) " +
                            " and s.id not in (select no.search_request_id from search_request_acl no inner join searches sid " +
                            "on (sid.id = no.search_request_id) "
                            + "where no.rule_hash in (:noAccessHash) and no.search_request_id in (:requestIds))";

            List<Long> idsList = getSession().createSQLQuery(rightQuery)
                    .addScalar("id", LongType.INSTANCE)
                    .setParameterList("hash", hashs)
                    .setString("userName", userName)
                    .setString("userSource", userSource)
                    .setParameterList("noAccessHash", noAccessHash)
                    .setParameterList("requestIds", requestsIds)
                    .list();

            if (idsList == null || idsList.size() == 0) {
                return new ArrayList<SearchRequest>();
            }
            return getSession().createQuery("from SearchRequest where id in (:idList)")
                    .setParameterList("idList", idsList)
                    .list();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }


    public boolean ruleExists(SearchRequest e, String userName, String userSource, List<String> hashs,
                              List<String> noAccessHash)
            throws ConfigException, DataSourceException {
        try {

            String rightQuery = "select distinct s.id "
                    + "from searches s "
                    + "left join search_request_acl acl "
                    + "on (s.id = acl.search_request_id) "
                    + "where "
                    + " (acl.rule_hash in (:hash) "
                    + "or "
                    + "(s.owner = :userName and s.owner_source = :userSource))"
                    + " and s.id = :requestId "
                    + " and "
                    + "s.id not in "
                    + "("
                    + "select no.search_request_id "
                    + "from search_request_acl no "
                    + "inner join searches sid "
                    + "on (sid.id = no.search_request_id) "
                    + "where no.rule_hash in (:noAccessHash) and no.search_request_id = :requestId)";

            Integer t = getSession().createSQLQuery(rightQuery)
                    .setLong("requestId", e.getId())
                    .setString("userName", userName)
                    .setString("userSource", userSource)
                    .setParameterList("hash", hashs)
                    .setParameterList("noAccessHash", noAccessHash)
                    .list()
                    .size();

            return (t.intValue() > 0);
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }


    public void createSecurityEntityRules(String secEntityName, String secEntitySource, int secEntityType)
            throws ConfigException, DataSourceException {
        try {
            DMSecurityRule read =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.READRULE);
            DMSecurityRule write =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.WRITERULE);
            DMSecurityRule full =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.FULLRULE);
            DMSecurityRule access =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.NOACCESS);

            try {
                getSession().saveOrUpdate(read);
                getSession().saveOrUpdate(write);
                getSession().saveOrUpdate(full);
                getSession().saveOrUpdate(access);

                getSession().flush();
            } catch (NonUniqueObjectException e) {

            }
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void addAclToSearchRequest(SecurityEntity sec,
                                      SearchRequest a, short rule) {
        try {
            SearchRequestACL acl = new SearchRequestACL(a);
            acl.setRuleHash(DMSecurityRule.getInstance(
                    sec.getID(), sec.getAuthenticationSourceName(), sec.getType(), rule).getRuleHash());
            getSession().save(acl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanACL(SearchRequest searchRequest)
            throws ConfigException, DataSourceException {
        try {
            String q = "delete from SearchRequestACL where searchRequestId = :reqId";
            getSession().createQuery(q)
                    .setLong("reqId", searchRequest.getId()).executeUpdate();
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void deleteSearchRequestACL(SearchRequestSecurity des)
            throws ConfigException, DataSourceException {
        try {
            SearchRequestACL acl = new SearchRequestACL(des.getSearchRequest());
            getSession().delete(acl);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<SearchRequestSecurity> getSearchRequestSecurities(SearchRequest e)
            throws ConfigException, DataSourceException {
        try {
//      String query = "select rule from DMEntityACL acl, DMSecurityRule rule where acl.path like :path and rule.ruleHash = acl.ruleHash";
            String query =
                    "select rule from SearchRequestACL acl, DMSecurityRule rule where acl.searchRequestId = :request_id and rule.ruleHash = acl.ruleHash" +
                            " order by rule.securityEntityUid, rule.securityEntitySource, rule.securityEntityType";
            List<DMSecurityRule> list = getSession().createQuery(query)
                    .setLong("request_id", e.getId())
                    .list();

            List<SearchRequestSecurity> vDes = new ArrayList<SearchRequestSecurity>();
            for (DMSecurityRule rule : list) {
                SearchRequestSecurity tt = null;
                log.trace("Rule Info for request " + e.getId() + ": " + rule.getSecurityEntityUid() + " " +
                        rule.getSecurityEntitySource());
                for (SearchRequestSecurity secEnt : vDes) {
                    if (secEnt.getName().equalsIgnoreCase(rule.getSecurityEntityUid())
                            && secEnt.getSource().equalsIgnoreCase(rule.getSecurityEntitySource())
                            && secEnt.getType() == rule.getSecurityEntityType()) {
                        tt = secEnt;
                        break;
                    }
                }
                if (tt == null) {
                    tt = new SearchRequestSecurity();
                    tt.setName(rule.getSecurityEntityUid());
                    tt.setSource(rule.getSecurityEntitySource());
                    tt.setType(rule.getSecurityEntityType());
                    tt.setSearchRequest(e);
                    AuthenticationSource source = FactoryInstantiator.getInstance()
                            .getAuthenticationSourceFactory()
                            .getAuthenticationSource(rule.getSecurityEntitySource());
                    SecurityEntity securityEntity = null;
                    switch (rule.getSecurityEntityType()) {
                        case SecurityEntityType.USER:
                            securityEntity = source.getUserFactory().getUser(rule.getSecurityEntityUid());
                            break;
                        case SecurityEntityType.GROUP:
                            securityEntity = source.getGroupFactory().getGroup(rule.getSecurityEntityUid());
                            break;
                        default:
                            ;
                    }
                    if (securityEntity != null) {
                        tt.setFullName(securityEntity.getName());
                    }
                    vDes.add(tt);
                }
                if (rule.getRights() == DMSecurityRule.READRULE) {
                    tt.setRead(true);
                }
                if (rule.getRights() == DMSecurityRule.WRITERULE) {
                    tt.setWrite(true);
                }
                if (rule.getRights() == DMSecurityRule.FULLRULE) {
                    tt.setFullAccess(true);
                }
                if (rule.getRights() == DMSecurityRule.NOACCESS) {
                    tt.setFullAccess(false);
                    tt.setWrite(false);
                    tt.setRead(false);
                }
            }
            return vDes;
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public List<SearchRequestACL> getSearchRequestACL(SearchRequest e)
            throws ConfigException, DataSourceException {
        try {
            String query = "from SearchRequestACL acl where searchRequestId = :sid";
            return getSession().createQuery(query)
                    .setLong("sid", e.getId())
                    .list();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public SearchRequestSecurity getSearchRequestSecurity(SearchRequest e, String name,
                                                          String source, int type) throws ConfigException,
            DataSourceException {
        try {
            SearchRequestSecurity d = (SearchRequestSecurity) getSession().createCriteria(SearchRequestSecurity.class)
                    .add(Restrictions.eq("searchRequestId", e.getId()))
                    .add(Restrictions.eq("name", name))
                    .add(Restrictions.eq("source", source))
                    .add(Restrictions.eq("type", type))
                    .uniqueResult();
            return d;
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public List<SearchRequestACL> saveSearchRequestSecurity(SearchRequestSecurity des)
            throws ConfigException, DataSourceException {

        List<SearchRequestACL> ret = new ArrayList<SearchRequestACL>();
        SearchRequestACL readAcl = new SearchRequestACL(des.getSearchRequest());
        SearchRequestACL writeAcl = new SearchRequestACL(des.getSearchRequest());
        SearchRequestACL fullAcl = new SearchRequestACL(des.getSearchRequest());
        SearchRequestACL noAcl = new SearchRequestACL(des.getSearchRequest());

        try {
            createSecurityEntityRules(des.getName(), des.getSource(), des.getType());

            if (des.isRead()) {
                DMSecurityRule dr = DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.READRULE);
                readAcl.setRuleHash(dr.getRuleHash());
                try {
                    getSession().save(readAcl);
                    ret.add(readAcl);
                } catch (NonUniqueObjectException o) {
                    readAcl = (SearchRequestACL) getSession().merge(readAcl);
                }
            }
            if (des.isWrite()) {
                writeAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.WRITERULE)
                        .getRuleHash());
                try {
                    getSession().save(writeAcl);
                    ret.add(writeAcl);
                } catch (NonUniqueObjectException o) {
                    writeAcl = (SearchRequestACL) getSession().merge(writeAcl);
                }
            }
            if (des.isFullAccess()) {
                fullAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.FULLRULE)
                        .getRuleHash());
                try {
                    getSession().save(fullAcl);
                    ret.add(fullAcl);
                } catch (NonUniqueObjectException e) {
                    fullAcl = (SearchRequestACL) getSession().merge(fullAcl);
                }
            }
            if (!des.isFullAccess() && !des.isRead() && !des.isWrite()) {
                noAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.NOACCESS)
                        .getRuleHash());
                try {
                    getSession().save(noAcl);
                    ret.add(noAcl);
                } catch (NonUniqueObjectException e) {
                    noAcl = (SearchRequestACL) getSession().merge(noAcl);
                }
            }


            getSession().flush();
            return ret;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

}

