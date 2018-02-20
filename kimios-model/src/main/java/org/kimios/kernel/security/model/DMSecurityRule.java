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
package org.kimios.kernel.security.model;

import org.kimios.kernel.share.model.Share;
import org.kimios.utils.hash.HashCalculator;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@IdClass(DMSecurityRulePK.class)
@Table(name = "dm_security_rules")
public class DMSecurityRule implements Serializable
{
    public static short READRULE = 1;

    public static short WRITERULE = 2;

    public static short FULLRULE = 4;

    public static short NOACCESS = 8;

    public static DMSecurityRule getInstance(String securityEntityUid, String securityEntitySource,
                                             int securityEntityType,
                                             short securityRule)
    {
        try {
            DMSecurityRule rule = new DMSecurityRule(
                    securityEntityUid, securityEntitySource, securityEntityType, securityRule);
            rule.setRuleHash(rule.generateRuleHash(false));
            rule.setRuleHashShare(rule.getRuleHash());
            return rule;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DMSecurityRule getInstance(String securityEntityUid, String securityEntitySource,
            int securityEntityType,
            short securityRule,
            Share share)
    {
        try {
            DMSecurityRule rule = new DMSecurityRule(
                    securityEntityUid, securityEntitySource, securityEntityType, securityRule, share);
            rule.setRuleHash(rule.generateRuleHash(false));
            rule.setRuleHashShare(
                    rule.getShare() != null ?
                            rule.generateRuleHash(true) :
                            rule.getRuleHash()
            );
            return rule;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DMSecurityRule()
    {
    }

    public DMSecurityRule(String securityEntityUid, String securityEntitySource, int securityEntityType, short rights) {
        this.securityEntityUid = securityEntityUid;
        this.securityEntitySource = securityEntitySource;
        this.securityEntityType = securityEntityType;
        this.rights = rights;
    }

    public DMSecurityRule(String securityEntityUid, String securityEntitySource, int securityEntityType, short rights, Share share) {
        this.securityEntityUid = securityEntityUid;
        this.securityEntitySource = securityEntitySource;
        this.securityEntityType = securityEntityType;
        this.rights = rights;
        this.share = share;
    }

    public String generateRuleHash (boolean withShare) {
        String propsAggregatedStr = securityEntityUid
                + securityEntitySource
                + Integer.toString(securityEntityType)
                + Short.toString(rights);
        propsAggregatedStr += (withShare && this.getShare() != null) ?
                share.getId() :
                "";
        String ruleHash = null;
        try {
            byte[] propsAggregated = (propsAggregatedStr).getBytes("UTF-8");

            String md5hash = new HashCalculator("MD5").hashToString(propsAggregated).replaceAll(" ", "");

            String sha1Hash = new HashCalculator("SHA-1").hashToString(propsAggregated).replaceAll(" ", "");
            ruleHash = md5hash + ":" + sha1Hash;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ruleHash;
    }

    @Id @Column(name = "rule_hash")
    private String ruleHash;

    @Column(name = "rule_hash_share")
    private String ruleHashShare;

    @Column(name = "security_entity_id")
    private String securityEntityUid;

    @Column(name = "security_entity_source")
    private String securityEntitySource;

    @Column(name = "security_entity_type")
    private int securityEntityType;

    @Column(name = "rights", nullable = false)
    private short rights;

    @JoinColumn(name = "dm_entity_share_id")
    @ManyToOne
    private Share share;

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
    }

    public String getRuleHashShare() {
        return ruleHashShare;
    }

    public void setRuleHashShare(String ruleHashShare) {
        this.ruleHashShare = ruleHashShare;
    }

    public String getSecurityEntityUid()
    {
        return securityEntityUid;
    }

    public void setSecurityEntityUid(String securityEntityUid)
    {
        this.securityEntityUid = securityEntityUid;
    }

    public String getSecurityEntitySource()
    {
        return securityEntitySource;
    }

    public void setSecurityEntitySource(String securityEntitySource)
    {
        this.securityEntitySource = securityEntitySource;
    }

    public int getSecurityEntityType()
    {
        return securityEntityType;
    }

    public void setSecurityEntityType(int securityEntityType)
    {
        this.securityEntityType = securityEntityType;
    }

    public short getRights()
    {
        return rights;
    }

    public void setRights(short rights)
    {
        this.rights = rights;
    }

    public Share getShare() {
        return share;
    }

    public void setShare(Share share) {
        this.share = share;
    }

    public static boolean securityRuleEquals(DMSecurityRule rule1, DMSecurityRule rule2){

        if (rule1 == rule2) return true;
        if (rule2 == null || rule1 == null || rule1.getClass() != rule2.getClass()) return false;
        if (rule1.securityEntityType != rule2.securityEntityType) return false;
        if (rule1.rights != rule2.rights) return false;
        if (!rule1.securityEntityUid.equals(rule2.securityEntityUid)) return false;

        if (rule1.getShare() == null && rule2.getShare() != null) return false;
        if (rule1.getShare() != null && rule2.getShare() == null) return false;
        if (rule1.getShare() != null && rule2.getShare() != null
                && rule1.getShare().getId() != rule2.getShare().getId()) return false;

        return rule1.securityEntitySource.equals(rule2.securityEntitySource);
    }



}

