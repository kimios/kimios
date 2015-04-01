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
package org.kimios.kernel.controller;

import org.kimios.kernel.jobs.security.IACLUpdater;
import org.kimios.kernel.security.ISecurityAgent;
import org.kimios.kernel.security.SecurityAgent;

public abstract class AKimiosController implements DmsController
{
    private ISecurityAgent securityAgent;

    protected IACLUpdater aclUpdater;

    protected org.kimios.kernel.dms.IDmsFactoryInstantiator dmsFactoryInstantiator;

    protected org.kimios.kernel.security.ISecurityFactoryInstantiator securityFactoryInstantiator;

    protected org.kimios.kernel.user.IAuthenticationFactoryInstantiator authFactoryInstantiator;

    protected org.kimios.kernel.log.ILogFactoryInstantiator logFactoryInstantiator;

    protected org.kimios.kernel.filetransfer.IFileTransferFactoryInstantiator transferFactoryInstantiator;

    protected org.kimios.kernel.reporting.IReportFactoryInstantiator reportFactoryInstantiator;

    public org.kimios.kernel.reporting.IReportFactoryInstantiator getReportFactoryInstantiator()
    {
        return reportFactoryInstantiator;
    }

    public void setReportFactoryInstantiator(
            org.kimios.kernel.reporting.IReportFactoryInstantiator reportFactoryInstantiator)
    {
        this.reportFactoryInstantiator = reportFactoryInstantiator;
    }

    public org.kimios.kernel.filetransfer.IFileTransferFactoryInstantiator getTransferFactoryInstantiator()
    {
        return transferFactoryInstantiator;
    }

    public void setTransferFactoryInstantiator(
            org.kimios.kernel.filetransfer.IFileTransferFactoryInstantiator transferFactoryInstantiator)
    {
        this.transferFactoryInstantiator = transferFactoryInstantiator;
    }

    public org.kimios.kernel.log.ILogFactoryInstantiator getLogFactoryInstantiator()
    {
        return logFactoryInstantiator;
    }

    public void setLogFactoryInstantiator(
            org.kimios.kernel.log.ILogFactoryInstantiator logFactoryInstantiator)
    {
        this.logFactoryInstantiator = logFactoryInstantiator;
    }

    public org.kimios.kernel.user.IAuthenticationFactoryInstantiator getAuthFactoryInstantiator()
    {
        return authFactoryInstantiator;
    }

    public void setAuthFactoryInstantiator(
            org.kimios.kernel.user.IAuthenticationFactoryInstantiator authFactoryInstantiator)
    {
        this.authFactoryInstantiator = authFactoryInstantiator;
    }

    public org.kimios.kernel.security.ISecurityFactoryInstantiator getSecurityFactoryInstantiator()
    {
        return securityFactoryInstantiator;
    }

    public void setSecurityFactoryInstantiator(
            org.kimios.kernel.security.ISecurityFactoryInstantiator securityFactoryInstantiator)
    {
        this.securityFactoryInstantiator = securityFactoryInstantiator;
    }

    public org.kimios.kernel.dms.IDmsFactoryInstantiator getDmsFactoryInstantiator()
    {
        return dmsFactoryInstantiator;
    }

    public void setDmsFactoryInstantiator(org.kimios.kernel.dms.IDmsFactoryInstantiator dmsFactoryInstantiator)
    {
        this.dmsFactoryInstantiator = dmsFactoryInstantiator;
    }

    public ISecurityAgent getSecurityAgent()
    {
        return this.securityAgent;
    }

    public void setSecurityAgent(ISecurityAgent securityAgent)
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

}

