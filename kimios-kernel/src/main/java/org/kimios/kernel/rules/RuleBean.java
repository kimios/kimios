/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.rules;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.MapKeyColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "rules")
@SequenceGenerator(name = "seq", allocationSize = 1, sequenceName = "rule_id_seq")
public class RuleBean
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq")
    @Column(name = "id")
    private long id;

    @Column(name = "java_class")
    private String javaClass;

    @Column(name = "rule_name")
    private String name;

    @Column(name = "dm_path")
    private String path;

    @Column(name = "rule_owner")
    private String ruleOwner;

    @Column(name = "rule_owner_source")
    private String ruleOwnerSource;

    @Column(name = "rule_creation_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ruleCreationDate;

    @Column(name = "rule_update_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date ruleUpdateDate;

    @Column(name = "rule_recursive")
    private Boolean recursive = false;

    @ElementCollection
    @CollectionTable(
            name = "rule_event",
            joinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<EventBean> events = new HashSet<EventBean>();

    @ElementCollection
    @MapKeyColumn(name = "param_name")
    @JoinTable(name = "rule_param",
            joinColumns = @JoinColumn(name = "rule_id"))
    @Column(name = "param_value")
    private Map<String, Serializable> parameters;

    @ElementCollection
    @CollectionTable(
            name = "rule_exclude_paths",
            joinColumns = @JoinColumn(name = "rule_id")
    )
    private Set<String> excludePaths = new HashSet<String>();

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass(String javaClass)
    {
        this.javaClass = javaClass;
    }

    public Map<String, Serializable> getParameters()
    {
        return parameters;
    }

    public void setParameters(Map<String, Serializable> parameters)
    {
        this.parameters = parameters;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Set<EventBean> getEvents()
    {
        return events;
    }

    public void setEvents(Set<EventBean> events)
    {
        this.events = events;
    }

    public String getRuleOwner()
    {
        return ruleOwner;
    }

    public void setRuleOwner(String ruleOwner)
    {
        this.ruleOwner = ruleOwner;
    }

    public String getRuleOwnerSource()
    {
        return ruleOwnerSource;
    }

    public void setRuleOwnerSource(String ruleOwnerSource)
    {
        this.ruleOwnerSource = ruleOwnerSource;
    }

    public Date getRuleCreationDate()
    {
        return ruleCreationDate;
    }

    public void setRuleCreationDate(Date ruleCreationDate)
    {
        this.ruleCreationDate = ruleCreationDate;
    }

    public Date getRuleUpdateDate()
    {
        return ruleUpdateDate;
    }

    public void setRuleUpdateDate(Date ruleUpdateDate)
    {
        this.ruleUpdateDate = ruleUpdateDate;
    }

    public Boolean isRecursive()
    {
        return recursive;
    }

    public void setRecursive(Boolean recursive)
    {
        this.recursive = recursive;
    }

    public Set<String> getExcludePaths()
    {
        return excludePaths;
    }

    public void setExcludePaths(Set<String> excludePaths)
    {
        this.excludePaths = excludePaths;
    }

    public org.kimios.kernel.ws.pojo.RuleBean toPojo(RuleBean rubleBean)
    {

        org.kimios.kernel.ws.pojo.RuleBean pojo = new org.kimios.kernel.ws.pojo.RuleBean();

        pojo.setName(name);
        pojo.setJavaClass(javaClass);
        pojo.setRecursive(recursive);
        pojo.setId(id);
        pojo.setRuleOwner(ruleOwner);
        pojo.setRuleOwnerSource(ruleOwnerSource);
        pojo.setRuleCreationDate(ruleCreationDate);
        pojo.setRuleUpdateDate(ruleUpdateDate);
        pojo.setPath(path);

        return pojo;
    }
}

