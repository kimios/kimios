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
package org.kimios.kernel.user;

import org.kimios.kernel.user.impl.HAuthenticationSource;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "authentication_source")
public class AuthenticationSourceBean
{
    @Id @Column(name = "source_name", nullable = false)
    private String name;

    @Column(name = "java_class", nullable = false)
    private String javaClass = HAuthenticationSource.class.getName();

    @Column(name = "enable_sso", nullable = true)
    private Boolean enableSso = false;

    @Column(name = "enable_mail_check", nullable = true)
    private Boolean enableMailCheck = false;

    @ElementCollection
    @CollectionTable(name = "authentication_params",
            joinColumns = { @JoinColumn(name = "authentication_source_name", referencedColumnName = "source_name") })
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> parameters = new HashMap<String, String>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass(String javaClass)
    {
        this.javaClass = javaClass;
    }

    public Map<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }

    public Boolean getEnableSso() {
        return enableSso;
    }

    public void setEnableSso(Boolean enableSso) {
        this.enableSso = enableSso;
    }

    public Boolean getEnableMailCheck() {
        return enableMailCheck;
    }

    public void setEnableMailCheck(Boolean enableMailCheck) {
        this.enableMailCheck = enableMailCheck;
    }
}

