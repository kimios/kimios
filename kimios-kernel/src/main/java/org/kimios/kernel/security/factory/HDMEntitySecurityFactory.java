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
import org.kimios.kernel.dms.DMEntity;
import org.kimios.kernel.dms.DMEntityImpl;
import org.kimios.kernel.exception.DataSourceException;
import org.kimios.kernel.hibernate.HFactory;
import org.kimios.kernel.security.*;
import org.kimios.kernel.user.AuthenticationSource;
import org.kimios.kernel.user.FactoryInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HDMEntitySecurityFactory extends HFactory implements DMEntitySecurityFactory
{
    final Logger log = LoggerFactory.getLogger(HDMEntitySecurityFactory.class);

    public <T extends DMEntityImpl> List<T> authorizedEntities(List<T> e, String userName, String userSource,
            Vector<String> hashs, Vector<String> noAccessHash)
            throws ConfigException, DataSourceException
    {
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
            throws ConfigException, DataSourceException
    {
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
            throws ConfigException, DataSourceException
    {
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
            String noAccessHash) throws ConfigException, DataSourceException
    {
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

    public void createSecurityEntityRules(String secEntityName, String secEntitySource, int secEntityType)
            throws ConfigException, DataSourceException
    {
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

    public void addACLToDmEntity(SecurityEntity sec,
            DMEntityImpl a, short rule)
    {
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
            throws ConfigException, DataSourceException
    {
        try {
            String q = "delete from DMEntityACL where dmEntityUid = :dm_entity_id";
            getSession().createQuery(q).setLong("dm_entity_id", d.getUid()).executeUpdate();
            getSession().flush();
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public void cleanACLRecursive(DMEntity d)
            throws ConfigException, DataSourceException
    {
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
            throws ConfigException, DataSourceException
    {
        try {
            DMEntityACL acl = new DMEntityACL(des.getDmEntity());
            getSession().delete(acl);
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }

    public Vector<DMEntitySecurity> getDMEntitySecurities(DMEntity e)
            throws ConfigException, DataSourceException
    {
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
                            && secEnt.getType() == rule.getSecurityEntityType())
                    {
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

    public List<DMEntityACL> getDMEntityACL(DMEntity e)
            throws ConfigException, DataSourceException
    {
        try {
            String query = "from DMEntityACL acl where dmEntityUid = :dmEntityUid";
            return getSession().createQuery(query)
                    .setLong("dmEntityUid", e.getUid())
                    .list();
        } catch (HibernateException ex) {
            throw new DataSourceException(ex);
        }
    }



    public List<DMEntityACL> saveDMEntitySecurity(DMEntitySecurity des)
            throws ConfigException, DataSourceException
    {

        List<DMEntityACL> ret = new ArrayList<DMEntityACL>();
        DMEntityACL readAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL writeAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL fullAcl = new DMEntityACL(des.getDmEntity());
        DMEntityACL noAcl = new DMEntityACL(des.getDmEntity());

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
                    readAcl = (DMEntityACL) getSession().merge(readAcl);
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
                    writeAcl = (DMEntityACL) getSession().merge(writeAcl);
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
                    fullAcl = (DMEntityACL) getSession().merge(fullAcl);
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
                    noAcl = (DMEntityACL) getSession().merge(noAcl);
                }
            }


            getSession().flush();
            return ret;
        } catch (HibernateException e) {
            throw new DataSourceException(e);
        }
    }


    public List<DMEntitySecurity> getDefaultDMEntitySecurity(String objectType, String entityPath)
            throws ConfigException, DataSourceException
    {
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
                            && secEnt.getType() == rule.getSecurityEntityType())
                    {
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
            throws ConfigException, DataSourceException
    {

        try {
            createSecurityEntityRules(des.getName(), des.getSource(), des.getType());

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
            Vector<String> noAccessHashs) throws ConfigException, DataSourceException
    {
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
            throws ConfigException, DataSourceException
    {
        try {
            getSession().update(des);
        } catch (HibernateException e) {
            boolean integrity = e instanceof ConstraintViolationException;
            throw new DataSourceException(e, e.getMessage());
        }
    }
}

