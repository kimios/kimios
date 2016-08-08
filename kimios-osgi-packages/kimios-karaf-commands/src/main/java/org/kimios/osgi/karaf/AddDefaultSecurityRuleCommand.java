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

package org.kimios.osgi.karaf;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.security.model.DMEntitySecurity;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
@Command(description = "Add Default Security Rule Command", name = "default-security", scope = "kimios")
public class AddDefaultSecurityRuleCommand extends KimiosCommand
{
    @Option(name = "-l",
            aliases = "--login",
            description = "User/Group Id",
            required = true, multiValued = false)
    String login = null;

    @Option(name = "-s",
            aliases = "--source",
            description = "User/Group Domain",
            required = true, multiValued = false)
    String source = null;

    @Option(name = "-t",
            aliases = "--type",
            description = "User or Group Type (1 or 2)",
            required = true, multiValued = false)
    int type = 1;

    @Option(name = "-r",
            aliases = "--read",
            description = "Read Access, default to true",
            required = false, multiValued = false)
    boolean read = true;

    @Option(name = "-w",
            aliases = "--write",
            description = "Write Access, default to false",
            required = false, multiValued = false)
    boolean write = false;

    @Option(name = "-f",
            aliases = "--content",
            description = "Full Access, default to false",
            required = false, multiValued = false)
    boolean fullAccess = false;

    @Option(name = "-o",
            aliases = "--objectType",
            description = "Object Type (folder, workspace, document, search request, bookmark)",
            required = true, multiValued = false)
    String objectType = "";





    @Override protected void doExecuteKimiosCommand() throws Exception
    {


        List<DMEntitySecurity> securities = new ArrayList<DMEntitySecurity>();

        DMEntitySecurity des = new DMEntitySecurity();
        des.setName(login);
        des.setSource(source);
        des.setRead(read);
        des.setWrite(write);
        des.setFullAccess(fullAccess);
        des.setType(type);
        securities.add(des);
        securityController.saveDefaultDMSecurityEntities(this.getCurrentSession(),securities, objectType, "");
    }




}
