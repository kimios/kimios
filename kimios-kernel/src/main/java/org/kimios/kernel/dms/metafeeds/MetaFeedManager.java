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
package org.kimios.kernel.dms.metafeeds;

import org.kimios.kernel.dms.MetaFeed;
import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.utils.ClassFinder;
import org.kimios.utils.extension.ExtensionRegistry;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetaFeedManager extends ExtensionRegistry<MetaFeedImpl>
{
    private static Logger log = LoggerFactory.getLogger(MetaFeedManager.class);


    private static List<String> metaFeedClass = new ArrayList<String>();

    public void init()
    {
        log.info("[kimios MetaFeed Manager] - Starting ...");
        metaFeedClass.clear();
        Collection<Class<? extends MetaFeedImpl>> classes = ClassFinder.findImplement("org.kimios", MetaFeedImpl.class);
        if (classes != null) {
            for (Class<? extends MetaFeedImpl> c : classes) {
                log.info("mManager adding {}", c);
                metaFeedClass.add(c.getName());
                this.addClass(c);
            }
            log.info("[kimios MetaFeed Manager] - Started : " + classes.size() + " loaded and available.");
        } else {
            log.error("[kimios MetaFeed Manager] - Start error : package not found, or no classes found");
        }
    }

    public static List<String> getMetasFeedClasses()
    {
        List<String> classz = new ArrayList<String>(ExtensionRegistryManager.itemsAsString(MetaFeedImpl.class));
        return classz;
    }
}

