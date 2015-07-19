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

import org.apache.karaf.shell.commands.Command;
import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.dms.metafeeds.MetaFeedManager;
import org.kimios.kernel.index.ReindexerProcess;
import org.kimios.utils.extension.ExtensionRegistryManager;

import java.util.List;


/**
 */
@Command(
        scope = "kimios",
        name = "metafeed-registry",
        description = "View available metafeeds class")
public class MetaFeedRegistryViewCommand extends KimiosCommand {


    @Override
    protected void doExecuteKimiosCommand() throws Exception {
        if (this.isConnected()) {

            for(String c: MetaFeedManager.getMetaFeedManager().getMetasFeedClasses()){
                System.out.println("found Meta feed class: " + c);
            }

            //try to match with generric registry



            for(String c: ExtensionRegistryManager.itemsAsString(MetaFeedImpl.class)){
                System.out.println("(from registry mng) found Meta feed class: " + c);
            }


        }


    }
}
