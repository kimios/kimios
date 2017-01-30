/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2016  DevLib'
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

package org.kimios.kernel.dms.metafeeds.builder;

import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.utils.extension.IExtensionRegistryManager;

import java.util.Collection;
import java.util.Map;

/**
 * Created by farf on 01/02/16.
 */
public class MetaFeedBuilder {

    private IExtensionRegistryManager extensionRegistryManager;

    public MetaFeedImpl buildMetaFeed(String javaClass, Map<String, String > preferences){
        try{

            Collection<Class<? extends MetaFeedImpl>> items =
                    extensionRegistryManager.itemsAsClass(MetaFeedImpl.class);

            Class<? extends MetaFeedImpl>  metafeedClass = null;
            for(Class<? extends MetaFeedImpl> c: items){
                if(c.getName().equals(javaClass)){
                    metafeedClass = c;
                    break;
                }
            }
            if(metafeedClass != null){
                MetaFeedImpl m = metafeedClass.newInstance();
                m.setPreferences(preferences);
                return m;
            } else {
                return null;
            }

        }   catch (InstantiationException ex){
            return null;
        }   catch (IllegalAccessException ex){
            return null;
        }
    }
}
