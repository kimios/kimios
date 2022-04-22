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
package org.kimios.kernel.security.factory;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.LongType;
import org.kimios.exceptions.ConfigException;
import org.kimios.kernel.dms.model.DMEntity;
import org.kimios.kernel.dms.model.DMEntityImpl;
import org.kimios.exceptions.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.DMEntitySecurityFactory;
import org.kimios.kernel.security.model.*;
import org.kimios.kernel.share.model.Share;
import org.kimios.kernel.user.FactoryInstantiator;
import org.kimios.kernel.user.model.AuthenticationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class HDMEntitySecurityFactory extends HFactory implements DMEntitySecurityFactory {
    final Logger log = LoggerFactory.getLogger(HDMEntitySecurityFactory.class);

    public <T extends DMEntity> List<T> authorizedEntities(List<T> e, String userName, String userSource,
                                                               Vector<String> hashs, Vector<String> noAccessHash)
            throws ConfigException, DataSourceException {
        try {

            Vector<String> paths = new Vector<String>();
            for (T it : e) {
                paths.add(it.getPath());
            }

            String rightQuery =
                    "select distinct dm.dm_entity_id, dm.dm_entity_path from dm_entity dm left join dm_entity_acl acl on " +
                            "(dm.dm_entity_id = acl.dm_entity_id) where "
                            + " dm.dm_entity_path in (:paths) and "
                            +
                            " (acl.rule_hash in (:hash) or (dm.dm_entity_owner = :userName and dm.dm_entity_owner_source = :userSource))"
                            +
                            " and dm.dm_entity_id not in (select no.dm_entity_id from dm_entity_acl no inner join dm_entity dmid " +
                            "on (dmid.dm_entity_id = no.dm_entity_id) "
                            + "where dmid.dm_entity_path in (:paths) and no.rule_hash in (:noAccessHash))";

            List<Long> idsList = getSession().createSQLQuery(rightQuery)
                    .addScalar("dm_entity_id", LongType.INSTANCE)
                    .setParameterList("paths", paths)
                    .setParameterList("hash", hashs)
                    .setString("userName", userName)
                    .setString("userSource", userSource)
                    .setParameterList("noAccessHash", noAccessHash)
                    .list();

            if (idsList == null || idsList.size() == 0) {
                return new ArrayList<T>();
            }
            return getSession().createQuery("from DMEntityImpl where id in (:idList)")
                    .setParameterList("idList", idsList)
                    .list();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public boolean ruleExists(DMEntity e, String userName, String userSource, Vector<String> hashs,
                              Vector<String> noAccessHash)
            throws ConfigException, DataSourceException {
        try {

            String rightQuery = "select distinct dm.dm_entity_id, dm.dm_entity_path "
                    + "from dm_entity dm "
                    + "left join dm_entity_acl acl "
                    + "on (dm.dm_entity_id = acl.dm_entity_id) "
                    + "where "
                    + " dm.dm_entity_path = :path "
                    + "and "
                    + " ((acl.rule_hash in (:hash)) "
                    + "or "
                    + "((dm.dm_entity_owner = :userName and dm.dm_entity_owner_source = :userSource)))"
                    + " and "
                    + "dm.dm_entity_id not in "
                    + "("
                    + "select no.dm_entity_id "
                    + "from dm_entity_acl no "
                    + "inner join dm_entity dmid "
                    + "on (dmid.dm_entity_id = no.dm_entity_id) "
                    + "where dmid.dm_entity_path = :path and no.rule_hash in (:noAccessHash)"
                    + ")";

            Integer t = getSession().createSQLQuery(rightQuery)
                    .setString("path", e.getPath())
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

    public boolean hasAnyChildCheckedOut(DMEntity e, String userName, String userSource)
            throws ConfigException, DataSourceException {
        try {
            if (e.getType() == 2 || e.getType() == 1) {
                String hqlQuery = "select count(dm) from Document dm, Lock lck where lck.uid = dm.uid " +
                        "and dm.path like :path and (lck.user <> :userName or lck.userSource <> :userSource)";
                Integer t = ((Number) getSession().createQuery(hqlQuery)
                        .setString("path", e.getPath() + "/%")
                        .setString("userName", userName)
                        .setString("userSource", userSource)
                        .uniqueResult()).intValue();
                return (t.intValue() > 0);
            } else {
                //return true if entity is document
                return false;
            }
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public boolean hasAnyChildNotWritable(DMEntity e, String userName, String userSource, Vector<String> writeHash,
                                          String noAccessHash) throws ConfigException, DataSourceException {
        try {
            if (e.getType() == 2 || e.getType() == 1) {
                String sqlQuery = ""
                        + "select count(*) from dm_entity dm "
                        + "where dm.dm_entity_path like :path and "
                        + "("
                        + "("
                        + "(dm.dm_entity_owner <> :userName or dm.dm_entity_owner_source <> :userSource) "
                        + "and  "
                        + "dm.dm_entity_id not in "
                        + "("
                        + "select dm1.dm_entity_id from dm_entity dm1 "
                        + "inner join dm_entity_acl acl1 "
                        + "on dm1.dm_entity_id = acl1.dm_entity_id "
                        + "and dm1.dm_entity_path like :path "
                        + "and "
                        + "acl1.rule_hash in (:writeHash)"
                        + ")"
                        + ") "
                        + "or "
                        + "("
                        + "dm.dm_entity_id in "
                        + "("
                        + "select dm2.dm_entity_id from dm_entity dm2 "
                        + "inner join dm_entity_acl acl2 "
                        + "on dm2.dm_entity_id = acl2.dm_entity_id "
                        + "where dm2.dm_entity_path like :path "
                        + "and "
                        + "acl2.rule_hash = :noAccessHash"
                        + ")"
                        + ")"
                        + ")";
                Integer t = ((Number) getSession().createSQLQuery(sqlQuery)
                        .setString("path", e.getPath() + "/%")
                        .setString("userName", userName)
                        .setString("userSource", userSource)
                        .setString("noAccessHash", noAccessHash)
                        .setParameterList("writeHash", writeHash)
                        .uniqueResult()).intValue();
                return (t.intValue() > 0);
            } else {
                //return true if entity is document
                return true;
            }
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }


    public static HashMap<String, DMSecurityRule> rules;

    static {
        rules = new HashMap<String, DMSecurityRule>();
    }

    public void createSecurityEntityRules(String secEntityName, String secEntitySource, int secEntityType, Share share)
            throws ConfigException, DataSourceException {
        try {

            List<DMSecurityRule> rulesItems = new ArrayList<DMSecurityRule>();
            DMSecurityRule read =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.READRULE, share);
            DMSecurityRule write =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.WRITERULE, share);
            DMSecurityRule full =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.FULLRULE, share);
            DMSecurityRule access =
                    DMSecurityRule.getInstance(secEntityName, secEntitySource, secEntityType, DMSecurityRule.NOACCESS, share);


            rulesItems.add(read);
            rulesItems.add(write);
            rulesItems.add(full);
            rulesItems.add(access);


            //put in cache

            try {
                getSession().saveOrUpdate(read);
                getSession().saveOrUpdate(write);
                getSession().saveOrUpdate(full);
                getSession().saveOrUpdate(access);
                getSession().flush();
            } catch (NonUniqueObjectException e) {

            } catch (ConstraintViolationException ex){

                //if pkey constraint broken, it means rules already exists, so let it go
                if(!ex.getConstraintName().equals("dm_security_rules_pkey"))
                    throw ex;
            }
            //put in cache
            for (DMSecurityRule rule : rulesItems) {
                String ruleMapKey = rule.getSecurityEntityUid() + "_" + rule.getSecurityEntitySource() + "_" + rule.getSecurityEntityType() + "_" + rule.getRights();
                rules.put(ruleMapKey, rule);
            }

        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void deleteSecurityEntityRules(String secEntityName, String secEntitySource, int secEntityType)
            throws ConfigException, DataSourceException {

        List<DMSecurityRule> secRules =  getSession().createCriteria(DMSecurityRule.class)
                .add( Restrictions.eq( "securityEntityUid", secEntityName))
                .add( Restrictions.eq( "securityEntitySource", secEntitySource))
                .add( Restrictions.eq( "securityEntityType", secEntityType))
                .list();
        try {
            for (DMSecurityRule secRule : secRules) {
                this.deleteAclsForSecurityRule(secRule.getRuleHash());
                getSession().delete(secRule);
                getSession().flush();
            }
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void deleteAcl(DMEntityACL dmEntityACL) {
        try {
            dmEntityACL = (DMEntityACL) this.getSession().merge(dmEntityACL);
            getSession().delete(dmEntityACL);
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void deleteAclsForSecurityRule (String ruleHash)
            throws ConfigException, DataSourceException {
        try {
            String deleteAclsQuery = "delete from DMEntityACL acl where acl.ruleHash = :ruleHash";

            getSession().createQuery(deleteAclsQuery).setString("ruleHash", ruleHash).executeUpdate();
            getSession().flush();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void deleteAclsForShare (long shareId)
            throws ConfigException, DataSourceException {
        try {
            String deleteAclsQuery = "delete from DMEntityACL acl where acl.ruleHash in "
            + "(select rule.ruleHash from DMSecurityRule rule where rule.share.id = :shareId)";

            getSession().createQuery(deleteAclsQuery).setLong("shareId", shareId).executeUpdate();
            getSession().flush();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public void addACLToDmEntity(SecurityEntity sec,
                                 DMEntityImpl a, short rule) {
        try {
            DMEntityACL acl = new DMEntityACL(a);
            acl.setRuleHash(DMSecurityRule.getInstance(
                    sec.getID(), sec.getAuthenticationSourceName(), sec.getType(), rule).getRuleHash());
            getSession().save(acl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanACL(DMEntity d)
            throws ConfigException, DataSourceException {
        try {
            String q = "delete from DMEntityACL where dmEntityUid = :dm_entity_id";
            getSession().createQuery(q).setLong("dm_entity_id", d.getUid()).executeUpdate();
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void cleanACLRecursive(DMEntity d)
            throws ConfigException, DataSourceException {
        try {
            String qRecursive =
                    "delete from DMEntityACL acl where acl.dmEntityUid in (select uid from DMEntityImpl where path = :path or path like :pathRecursive)";
            getSession().createQuery(qRecursive)
                    .setString("path", d.getPath())
                    .setString("pathRecursive", d.getPath() + "/%")
                    .executeUpdate();
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void deleteDMEntitySecurity(DMEntitySecurity des)
            throws ConfigException, DataSourceException {
        try {
            DMEntityACL acl = new DMEntityACL(des.getDmEntity());
            getSession().delete(acl);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public List<DMEntitySecurity> generateDMEntitySecuritiesFromAcls(List<DMEntityACL> acls, DMEntity entity)
            throws ConfigException, DataSourceException {
        try {

            if (acls.size() == 0) {
                return new ArrayList<DMEntitySecurity>();

            }
            List<String> hashAcls = new ArrayList<String>();
            for (DMEntityACL acl : acls) {
                if (!hashAcls.contains(acl.getRuleHash()))
                    hashAcls.add(acl.getRuleHash());
            }


            log.debug("unique hash acls processed: {}", hashAcls.size());

            String query =
                    "select rule from DMSecurityRule rule " +
                            "where rule.ruleHash in (:hashList) " +
                            " order by rule.securityEntityUid, rule.securityEntitySource, rule.securityEntityType";
            List<DMSecurityRule> list = getSession().createQuery(query)
                    .setParameterList("hashList", hashAcls)
                    .list();

            log.debug("security rules loaded from hash {}", list.size());

            List<DMEntitySecurity> vDes = new ArrayList<DMEntitySecurity>();
            for (DMSecurityRule rule : list) {
                DMEntitySecurity tt = null;


                log.debug("processing rule {}", rule.getSecurityEntityUid() + "@" + rule.getSecurityEntitySource());

                for (DMEntitySecurity secEnt : vDes) {
                    if (secEnt.getName().equalsIgnoreCase(rule.getSecurityEntityUid())
                            && secEnt.getSource().equalsIgnoreCase(rule.getSecurityEntitySource())
                            && secEnt.getType() == rule.getSecurityEntityType()) {
                        tt = secEnt;
                        break;
                    }
                }
                if (tt == null) {
                    tt = new DMEntitySecurity();
                    tt.setName(rule.getSecurityEntityUid());
                    tt.setSource(rule.getSecurityEntitySource());
                    tt.setType(rule.getSecurityEntityType());
                    tt.setDmEntity(entity);
                    AuthenticationSource source = FactoryInstantiator.getInstance()
                            .getAuthenticationSourceFactory()
                            .getAuthenticationSource(rule.getSecurityEntitySource());
                    SecurityEntity securityEntity = null;
                    switch (rule.getSecurityEntityType()) {
                        case SecurityEntityType.USER:
                            try {
                                securityEntity = source.getUserFactory().getUser(rule.getSecurityEntityUid());
                            } catch (Exception ex) {
                                log.error("while generating securities from acls: for " +
                                        rule.getSecurityEntityUid() + "@"
                                        + rule.getSecurityEntitySource()
                                        + " an error happen", ex);
                            }
                            break;
                        case SecurityEntityType.GROUP:
                            try {
                                securityEntity = source.getGroupFactory().getGroup(rule.getSecurityEntityUid());
                            } catch (Exception ex) {
                                log.error("while generating securities from acls: for " +
                                        rule.getSecurityEntityUid() + "@"
                                        + rule.getSecurityEntitySource()
                                        + " an error happen", ex);
                            }
                            break;
                        default:
                            ;
                    }
                    if (securityEntity != null) {
                        tt.setFullName(securityEntity.getName());
                    } else {
                        log.warn("warn {}@{} isn't readable from security source",
                                rule.getSecurityEntityUid(), rule.getSecurityEntitySource());
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
            log.debug("returning sec list {}", vDes.size());
            return vDes;
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public Vector<DMEntitySecurity> getDMEntitySecurities(DMEntity e)
            throws ConfigException, DataSourceException {
        try {
//      String query = "select rule from DMEntityACL acl, DMSecurityRule rule where acl.path like :path and rule.ruleHash = acl.ruleHash";
            String query =
                    "select rule from DMEntityACL acl, DMSecurityRule rule where acl.dmEntityUid = :dm_entity_id and rule.ruleHash = acl.ruleHash" +
                            " order by rule.securityEntityUid, rule.securityEntitySource, rule.securityEntityType";
            List<DMSecurityRule> list = getSession().createQuery(query)
                    .setLong("dm_entity_id", e.getUid())
                    .list();

            Vector<DMEntitySecurity> vDes = new Vector<DMEntitySecurity>();
            for (DMSecurityRule rule : list) {
                DMEntitySecurity tt = null;
                log.trace("Rule Info for entity " + e.getPath() + ": " + rule.getSecurityEntityUid() + " " +
                        rule.getSecurityEntitySource());
                for (DMEntitySecurity secEnt : vDes) {
                    if (secEnt.getName().equalsIgnoreCase(rule.getSecurityEntityUid())
                            && secEnt.getSource().equalsIgnoreCase(rule.getSecurityEntitySource())
                            && secEnt.getType() == rule.getSecurityEntityType()) {
                        tt = secEnt;
                        break;
                    }
                }
                if (tt == null) {
                    tt = new DMEntitySecurity();
                    tt.setName(rule.getSecurityEntityUid());
                    tt.setSource(rule.getSecurityEntitySource());
                    tt.setType(rule.getSecurityEntityType());
                    tt.setDmEntity(e);
                    AuthenticationSource source = FactoryInstantiator.getInstance()
                            .getAuthenticationSourceFactory()
                            .getAuthenticationSource(rule.getSecurityEntitySource());
                    SecurityEntity securityEntity = null;
                    switch (rule.getSecurityEntityType()) {
                        case SecurityEntityType.USER:
                            try {
                                securityEntity = source.getUserFactory().getUser(rule.getSecurityEntityUid());
                            } catch (Exception ex) {
                                log.error("while generating securities from acls: for " +
                                        rule.getSecurityEntityUid() + "@"
                                        + rule.getSecurityEntitySource()
                                        + " an error happen", ex);
                            }
                            break;
                        case SecurityEntityType.GROUP:
                            try {
                                securityEntity = source.getGroupFactory().getGroup(rule.getSecurityEntityUid());
                            } catch (Exception ex) {
                                log.error("while generating securities from acls: for " +
                                        rule.getSecurityEntityUid() + "@"
                                        + rule.getSecurityEntitySource()
                                        + " an error happen", ex);
                            }
                            break;
                        default:
                            ;
                    }
                    if (securityEntity != null) {
                        tt.setFullName(securityEntity.getName());
                    } else {
                        log.warn("warn {}@{} isn't readable from security source",
                                rule.getSecurityEntityUid(), rule.getSecurityEntitySource());
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

    public List<DMEntityACL> getDMEntityACL(DMEntity e)
            throws ConfigException, DataSourceException {
        try {
            String query = "from DMEntityACL acl where dmEntityUid = :dmEntityUid";
            return getSession().createQuery(query)
                    .setLong("dmEntityUid", e.getUid())
                    .list();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }

    public List<DMEntityACL> saveDMEntitySecurity(DMEntitySecurity des, Share share)
            throws ConfigException, DataSourceException {

        List<DMEntityACL> ret = new ArrayList<DMEntityACL>();
        DMEntityACL readAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL writeAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL fullAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL noAcl = new DMEntityACL(des.getDmEntity());

        try {
            createSecurityEntityRules(des.getName(), des.getSource(), des.getType(), share);

            if (des.isRead()) {

                DMSecurityRule dr = DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.READRULE, share);
                readAcl.setRuleHash(dr.getRuleHash());
                try {
                    getSession().saveOrUpdate(readAcl);
                    ret.add(readAcl);
                } catch (NonUniqueObjectException o) {
                    log.error("already existing acl! entity " + des.getDmEntity().getPath() + " {} - {}@{}", "read", des.getName(), des.getSource());
                    readAcl = (DMEntityACL) getSession().merge(readAcl);
                    try {
                        getSession().saveOrUpdate(readAcl);
                    } catch (HibernateException e) {
                        throw new DataSourceException(e, e.getMessage());
                    }
                    ret.add(readAcl);
                }
            }
            if (des.isWrite()) {
                writeAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.WRITERULE, share)
                        .getRuleHash());
                try {
                    getSession().saveOrUpdate(writeAcl);
                    ret.add(writeAcl);
                } catch (NonUniqueObjectException o) {
                    log.error("already existing acl! entity " + des.getDmEntity().getPath() + " {} - {}@{}", "write", des.getName(), des.getSource());
                    writeAcl = (DMEntityACL) getSession().merge(writeAcl);
                    try {
                        getSession().saveOrUpdate(writeAcl);
                    } catch (HibernateException e) {
                        throw new DataSourceException(e, e.getMessage());
                    }
                    ret.add(writeAcl);
                }
            }
            if (des.isFullAccess()) {
                fullAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.FULLRULE, share)
                        .getRuleHash());
                try {
                    getSession().saveOrUpdate(fullAcl);
                    ret.add(fullAcl);
                } catch (NonUniqueObjectException e) {
                    log.error("already existing acl! entity " + des.getDmEntity().getPath() + " {} - {}@{}", "fullaccess", des.getName(), des.getSource());
                    fullAcl = (DMEntityACL) getSession().merge(fullAcl);
                    try {
                        getSession().saveOrUpdate(fullAcl);
                    } catch (HibernateException ex) {
                        throw new DataSourceException(ex, ex.getMessage());
                    }
                    ret.add(fullAcl);
                }
            }
            if (!des.isFullAccess() && !des.isRead() && !des.isWrite()) {
                noAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.NOACCESS)
                        .getRuleHash());
                try {
                    getSession().saveOrUpdate(noAcl);
                    ret.add(noAcl);
                } catch (NonUniqueObjectException e) {
                    log.error("already existing acl! entity " + des.getDmEntity().getPath() + " {} - {}@{}", "noaccess", des.getName(), des.getSource());
                    noAcl = (DMEntityACL) getSession().merge(noAcl);
                    try {
                        getSession().saveOrUpdate(noAcl);
                    } catch (HibernateException ex) {
                        throw new DataSourceException(ex, ex.getMessage());
                    }
                    ret.add(noAcl);
                }
            }


            getSession().flush();
            return ret;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void saveDMEntityACL(DMEntityACL dmEntityACL) {
        try {
            this.getSession().saveOrUpdate(dmEntityACL);
        } catch (NonUniqueObjectException e) {
            dmEntityACL = (DMEntityACL) this.getSession().merge(dmEntityACL);
            try {
                getSession().saveOrUpdate(dmEntityACL);
            } catch (HibernateException ex) {
                throw new DataSourceException(ex, ex.getMessage());
            }
        }

    }

    public List<DMEntityACL> generateDMEntityAclsFromSecuritiesObject(List<DMEntitySecurity> securities, DMEntity entity) {

        if (entity != null) {
            //reset entity properly
            for (DMEntitySecurity sec : securities)
                sec.setDmEntity(entity);
        }

        List<DMEntityACL> ret = new ArrayList<DMEntityACL>();
        for (DMEntitySecurity des : securities) {
            //create rules if it doesn't exists
            createSecurityEntityRules(des.getName(), des.getSource(), des.getType(), null);
            DMEntityACL readAcl = new DMEntityACL(des.getDmEntity());
            DMEntityACL writeAcl = new DMEntityACL(des.getDmEntity());
            DMEntityACL fullAcl = new DMEntityACL(des.getDmEntity());
            DMEntityACL noAcl = new DMEntityACL(des.getDmEntity());


            if (des.isRead()) {
                DMSecurityRule dr = DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.READRULE);
                readAcl.setRuleHash(dr.getRuleHash());
                ret.add(readAcl);
            }
            if (des.isWrite()) {
                writeAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.WRITERULE)
                        .getRuleHash());
                ret.add(writeAcl);
            }
            if (des.isFullAccess()) {
                fullAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.FULLRULE)
                        .getRuleHash());
                ret.add(fullAcl);
            }
            if (!des.isFullAccess() && !des.isRead() && !des.isWrite()) {
                noAcl.setRuleHash(DMSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.NOACCESS)
                        .getRuleHash());
                ret.add(noAcl);
            }
        }

        return ret;
    }


    public List<DMEntitySecurity> getDefaultDMEntitySecurity(String objectType, String entityPath)
            throws ConfigException, DataSourceException {
        try {
            String query =
                    "select rule from DMDefaultSecurityRule rule where rule.objectType = :objectType" +
                            " order by rule.securityEntityUid, rule.securityEntitySource, rule.securityEntityType";
            List<DMDefaultSecurityRule> list = getSession().createQuery(query)
                    .setString("objectType", objectType)
                    .list();

            List<DMEntitySecurity> vDes = new ArrayList<DMEntitySecurity>();
            for (DMDefaultSecurityRule rule : list) {
                DMEntitySecurity tt = null;
                for (DMEntitySecurity secEnt : vDes) {
                    if (secEnt.getName().equalsIgnoreCase(rule.getSecurityEntityUid())
                            && secEnt.getSource().equalsIgnoreCase(rule.getSecurityEntitySource())
                            && secEnt.getType() == rule.getSecurityEntityType()) {
                        tt = secEnt;
                        break;
                    }
                }
                if (tt == null) {
                    tt = new DMEntitySecurity();
                    tt.setName(rule.getSecurityEntityUid());
                    tt.setSource(rule.getSecurityEntitySource());
                    tt.setType(rule.getSecurityEntityType());
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


    public void saveDefaultDMEntitySecurity(DMEntitySecurity des, String objectType, String entityPath)
            throws ConfigException, DataSourceException {

        try {
            createSecurityEntityRules(des.getName(), des.getSource(), des.getType(), null);

            if (des.isRead()) {
                DMDefaultSecurityRule dr = DMDefaultSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.READRULE, objectType, entityPath);

                getSession().save(dr);
            }
            if (des.isWrite()) {
                DMDefaultSecurityRule dr = DMDefaultSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.WRITERULE, objectType, entityPath);

                getSession().save(dr);
            }
            if (des.isFullAccess()) {
                DMDefaultSecurityRule dr = DMDefaultSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.FULLRULE, objectType, entityPath);

                getSession().save(dr);
            }

            if (!des.isFullAccess() && !des.isRead() && !des.isWrite()) {
                DMDefaultSecurityRule dr = DMDefaultSecurityRule
                        .getInstance(des.getName(), des.getSource(), des.getType(), DMSecurityRule.NOACCESS, objectType, entityPath);
                getSession().save(dr);
            }


            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }


    public <T extends DMEntityImpl> DMEntity entityFromRule(long dmEntityUid, Vector<String> hashs,
                                                            Vector<String> noAccessHashs) throws ConfigException, DataSourceException {
        try {

//      String sqlQ = "from DMEntityImpl en, DMEntityACL acl where en.path = acl.path and en.uid = :uid and acl.ruleHash in (:hash) and  " + 
//      "en.path not in (select no.path from DMEntityACL no where no.dmEntityUid = :uid and no.ruleHash in (:noAccessHash))";

            String sqlQ =
                    "from DMEntityImpl en, DMEntityACL acl where en.uid = acl.dmEntityUid and en.uid = :uid and acl.ruleHash in (:hash) and  " +
                            "en.uid not in (select no.dmEntityUid from DMEntityACL no where no.dmEntityUid = :uid and no.ruleHash in (:noAccessHash))";

            T entity = (T) getSession().createQuery(sqlQ)
                    .setLong("uid", dmEntityUid)
                    .setParameterList("hash", hashs)
                    .setParameterList("noAccessHash", noAccessHashs)
                    .setMaxResults(1)
                    .uniqueResult();

            return entity;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void updateDMEntitySecurity(DMEntitySecurity des)
            throws ConfigException, DataSourceException {
        try {
            getSession().update(des);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

