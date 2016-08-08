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
import org.kimios.kernel.dms.utils.PathElement;

import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
@Command(description = "Path Template Utility", name = "path-element", scope = "kimios")
public class PathElementCommand extends KimiosCommand
{
    @Option(name = "-c",
            aliases = "--content",
            description = "show json content",
            required = false, multiValued = false)
    String login = null;



    @Override protected void doExecuteKimiosCommand() throws Exception
    {
        System.out.println(PathElement.convertStructureToString(pathElements));
    }



    private static List<PathElement> pathElements;
    static {
        pathElements = new ArrayList<PathElement>();


        PathElement p = new PathElement();
        p.setElementType(PathElement.FIXED_STRING);
        p.setElementValue("SPORTS");

        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("MM");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("dd");
        pathElements.add(p);

        p = new PathElement();
        p.setElementType(PathElement.INDEX_FIELD);
        p.setElementValue("Sports");
        pathElements.add(p);


        p = new PathElement();
        p.setElementType(PathElement.CREATION_DATE);
        p.setElementFormat("yyyy-MM-dd-hh-mm-ss");
        p.setDocumentName(true);
        pathElements.add(p);

    }
}
