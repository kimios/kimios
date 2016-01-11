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

import org.kimios.kernel.dms.model.MetaFeedImpl;
import org.kimios.kernel.utils.BundleUrlType;
import org.kimios.kernel.utils.ClassFinder;
import org.kimios.utils.extension.ExtensionRegistry;
import org.kimios.utils.extension.ExtensionRegistryManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MetaFeedManager extends ExtensionRegistry<MetaFeedImpl>
{
    private static Logger log = LoggerFactory.getLogger(MetaFeedManager.class);

    private List<String> metaFeedClass = new ArrayList<String>();


    private MetaFeedManager(){ super(); }

    public synchronized void init()
    {
        log.info("[kimios MetaFeed Manager] - Starting ...");
        metaFeedClass.clear();
        Collection<Class<? extends MetaFeedImpl>> classes = null;
        /*try {
            Bundle kimiosModelBundle = FrameworkUtil.getBundle(MetaFeedImpl.class);
            log.info("will load class from bundle {}", kimiosModelBundle.getBundleId() + " ==> " + kimiosModelBundle.getSymbolicName());
            Collection colClass = ClassFinder.findImplement(new BundleUrlType(kimiosModelBundle),
                    "org.kimios", MetaFeedImpl.class, kimiosModelBundle.adapt(BundleWiring.class).getClassLoader());
            log.info("found class for {} : {} item(s)", MetaFeedImpl.class, colClass.size());
            if(classes == null){
                classes = new ArrayList<Class<? extends MetaFeedImpl>>();
            }
            classes.addAll(colClass);
        }
        catch (NoClassDefFoundError ex){
            log.warn("not in osgi environment. will load manually");

        }
        catch (Throwable ex){
            log.error("error while loading class from osgi bundle", ex);
        }*/
        Collection colClass = ClassFinder.findImplement("org.kimios", MetaFeedImpl.class);
        classes = colClass;
        if (classes != null) {
            for (Class<? extends MetaFeedImpl> c : classes) {
                try {
                    log.info("mManager adding {}", c);
                    metaFeedClass.add(c.getName());
                    this.addClass(c);
                } catch (Exception ex){
                    log.error("error while adding {} into {}. Msg: {}", c, metaFeedClass, ex.getMessage());
                }
            }
            log.info("[kimios MetaFeed Manager] - Started : " + classes.size() + " loaded and available.");
        } else {
            log.error("[kimios MetaFeed Manager] - Start error : package not found, or no classes found");
        }
    }

    public List<String> getMetasFeedClasses()
    {
        log.debug("current metafeed manager ref: {}", this);
        List<String> classz = new ArrayList<String>(ExtensionRegistryManager.itemsAsString(MetaFeedImpl.class));
        return classz;
    }

    private static MetaFeedManager metaFeedManager;

    public static synchronized MetaFeedManager getMetaFeedManager(){

        if(metaFeedManager == null){
            metaFeedManager = new MetaFeedManager();
        }
        return metaFeedManager;

    }

    @Override
    protected void handleAdd(Class<? extends MetaFeedImpl> classz) {
        log.info("adding metafeed {}", classz);
        if(metaFeedClass == null)
            metaFeedClass = new ArrayList<String>();
        metaFeedClass.add(classz.getName());
    }

    @Override
    protected void handleRemove(Class<? extends MetaFeedImpl> classz) {
        log.info("removing metafeed {}", classz);
        metaFeedClass.remove(classz.getName());
    }
}

