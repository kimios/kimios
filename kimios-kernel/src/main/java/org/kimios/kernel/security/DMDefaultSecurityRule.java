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

package org.kimios.kernel.security;

import org.kimios.kernel.utils.HashCalculator;

import javax.persistence.*;

/**
 */

@Entity
@IdClass(DMDefaultSecurityRulePK.class)
@Table(name = "dm_default_security_rules")
public class DMDefaultSecurityRule {

    @Id
    @Column(name = "rule_hash")
    private String ruleHash;

    @Id
    @Column(name = "object_type")
    private String objectType;

    @Column(name = "entity_path", nullable = true)
    private String entityPath;


    @Column(name = "rule_security_entity_id")
    private String securityEntityUid;

    @Column(name = "rule_security_entity_source")
    private String securityEntitySource;

    @Column(name = "rule_security_entity_type")
    private int securityEntityType;

    @Column(name = "security_rule")
    private short rights;

    public String getRuleHash() {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash) {
        this.ruleHash = ruleHash;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getSecurityEntityUid() {
        return securityEntityUid;
    }

    public void setSecurityEntityUid(String securityEntityUid) {
        this.securityEntityUid = securityEntityUid;
    }

    public String getSecurityEntitySource() {
        return securityEntitySource;
    }

    public void setSecurityEntitySource(String securityEntitySource) {
        this.securityEntitySource = securityEntitySource;
    }

    public int getSecurityEntityType() {
        return securityEntityType;
    }

    public void setSecurityEntityType(int securityEntityType) {
        this.securityEntityType = securityEntityType;
    }

    public short getRights() {
        return rights;
    }

    public void setRights(short rights) {
        this.rights = rights;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }

    public static DMDefaultSecurityRule getInstance(String securityEntityUid, String securityEntitySource,
                                                    int securityEntityType,
                                                    short securityRule,
                                                    String objectType,
                                                    String entitypath) {
        try {
            DMDefaultSecurityRule rule = new DMDefaultSecurityRule();
            rule.setRights(securityRule);
            rule.setSecurityEntitySource(securityEntitySource);
            rule.setSecurityEntityType(securityEntityType);
            rule.setSecurityEntityUid(securityEntityUid);

            String md5hash = new HashCalculator("MD5").hashToString(
                    (securityEntityUid + securityEntitySource + Integer.toString(securityEntityType) +
                            Short.toString(securityRule)).getBytes("UTF-8")).replaceAll(" ", "");

            String sha1Hash = new HashCalculator("SHA-1").hashToString(
                    (securityEntityUid + securityEntitySource + Integer.toString(securityEntityType) +
                            Short.toString(securityRule)).getBytes("UTF-8")).replaceAll(" ", "");
            rule.setRuleHash(md5hash + ":" + sha1Hash);
            rule.setObjectType(objectType);
            if(entitypath != null){
                rule.setEntityPath(entitypath);
            } else {
                rule.setEntityPath("");
            }

            return rule;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
