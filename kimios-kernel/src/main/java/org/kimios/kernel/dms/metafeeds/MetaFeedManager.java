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
package org.kimios.kernel.dms.metafeeds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kimios.kernel.dms.MetaFeedImpl;
import org.kimios.kernel.utils.ClassFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaFeedManager
{
    private static Logger log = LoggerFactory.getLogger(MetaFeedManager.class);

    private static List<String> metasFeedsClass = new ArrayList<String>();

    public void init()
    {
        log.info("[kimios MetaFeed Manager] - Starting ...");
        Collection<Class<? extends MetaFeedImpl>> classes = ClassFinder.findImplement("org.kimios", MetaFeedImpl.class);
        metasFeedsClass.clear();
        if (classes != null) {
            for (Class<?> c : classes) {
                metasFeedsClass.add(c.getName());
            }
            log.info("[kimios MetaFeed Manager] - Started : " + classes.size() + " loaded and available.");
        } else {
            log.error("[kimios MetaFeed Manager] - Start error : package not found, or no classes found");
        }
    }

    public static List<String> getMetasFeedClasses()
    {
        return metasFeedsClass;
    }
}

