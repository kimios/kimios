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
            DMSecurityRule rule = new DMSecurityRule();
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
            return rule;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DMSecurityRule()
    {
    }

    @Id @Column(name = "rule_hash")
    private String ruleHash;

    @Column(name = "security_entity_id")
    private String securityEntityUid;

    @Column(name = "security_entity_source")
    private String securityEntitySource;

    @Column(name = "security_entity_type")
    private int securityEntityType;

    @Column(name = "rights", nullable = false)
    private short rights;

    public String getRuleHash()
    {
        return ruleHash;
    }

    public void setRuleHash(String ruleHash)
    {
        this.ruleHash = ruleHash;
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
}

