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
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.utils.extension.IExtensionRegistryManager;


/**
 */
@Service
@Command(
        scope = "kimios",
        name = "metafeed-registry",
        description = "View available metafeeds class")
public class MetaFeedRegistryViewCommand extends KimiosCommand {

    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {
            for(String c: extensionRegistryManager.itemsAsString(MetaFeedImpl.class)){
                System.out.println("(from registry mng) found Meta feed class: " + c);
            }
        }
    }
}
