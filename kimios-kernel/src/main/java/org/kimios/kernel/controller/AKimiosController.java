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
package org.kimios.kernel.controller;

import org.kimios.kernel.index.AbstractIndexManager;
import org.kimios.kernel.jobs.security.IACLUpdater;
import org.kimios.kernel.security.SecurityAgent;

public abstract class AKimiosController implements DmsController
{
    private SecurityAgent securityAgent;

    protected IACLUpdater aclUpdater;

    protected AbstractIndexManager indexManager;

    protected org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator;

    protected org.kimios.kernel.security.FactoryInstantiator securityFactoryInstantiator;

    protected org.kimios.kernel.user.FactoryInstantiator authFactoryInstantiator;

    protected org.kimios.kernel.log.FactoryInstantiator logFactoryInstantiator;

    protected org.kimios.kernel.filetransfer.FactoryInstantiator transferFactoryInstantiator;

    protected org.kimios.kernel.reporting.FactoryInstantiator reportFactoryInstantiator;

    public org.kimios.kernel.reporting.FactoryInstantiator getReportFactoryInstantiator()
    {
        return reportFactoryInstantiator;
    }

    public void setReportFactoryInstantiator(
            org.kimios.kernel.reporting.FactoryInstantiator reportFactoryInstantiator)
    {
        this.reportFactoryInstantiator = reportFactoryInstantiator;
    }

    public org.kimios.kernel.filetransfer.FactoryInstantiator getTransferFactoryInstantiator()
    {
        return transferFactoryInstantiator;
    }

    public void setTransferFactoryInstantiator(
            org.kimios.kernel.filetransfer.FactoryInstantiator transferFactoryInstantiator)
    {
        this.transferFactoryInstantiator = transferFactoryInstantiator;
    }

    public org.kimios.kernel.log.FactoryInstantiator getLogFactoryInstantiator()
    {
        return logFactoryInstantiator;
    }

    public void setLogFactoryInstantiator(
            org.kimios.kernel.log.FactoryInstantiator logFactoryInstantiator)
    {
        this.logFactoryInstantiator = logFactoryInstantiator;
    }

    public org.kimios.kernel.user.FactoryInstantiator getAuthFactoryInstantiator()
    {
        return authFactoryInstantiator;
    }

    public void setAuthFactoryInstantiator(
            org.kimios.kernel.user.FactoryInstantiator authFactoryInstantiator)
    {
        this.authFactoryInstantiator = authFactoryInstantiator;
    }

    public org.kimios.kernel.security.FactoryInstantiator getSecurityFactoryInstantiator()
    {
        return securityFactoryInstantiator;
    }

    public void setSecurityFactoryInstantiator(
            org.kimios.kernel.security.FactoryInstantiator securityFactoryInstantiator)
    {
        this.securityFactoryInstantiator = securityFactoryInstantiator;
    }

    public org.kimios.kernel.dms.FactoryInstantiator getDmsFactoryInstantiator()
    {
        return dmsFactoryInstantiator;
    }

    public void setDmsFactoryInstantiator(org.kimios.kernel.dms.FactoryInstantiator dmsFactoryInstantiator)
    {
        this.dmsFactoryInstantiator = dmsFactoryInstantiator;
    }

    public SecurityAgent getSecurityAgent()
    {
        return this.securityAgent;
    }

    public void setSecurityAgent(SecurityAgent securityAgent)
    {
        this.securityAgent = securityAgent;
    }

    public IACLUpdater getAclUpdater()
    {
        return aclUpdater;
    }

    public void setAclUpdater(IACLUpdater aclUpdater)
    {
        this.aclUpdater = aclUpdater;
    }

    public AbstractIndexManager getIndexManager()
    {
        return indexManager;
    }

    public void setIndexManager(AbstractIndexManager indexManager)
    {
        this.indexManager = indexManager;
    }
}

