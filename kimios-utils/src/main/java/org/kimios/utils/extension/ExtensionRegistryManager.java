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

package org.kimios.utils.extension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by farf on 6/22/14.
 */
public class ExtensionRegistryManager implements IExtensionRegistryManager {

    private static Logger logger = LoggerFactory.getLogger(ExtensionRegistryManager.class);
    
    private Map<String, ExtensionRegistry> _registries = new ConcurrentHashMap<String, ExtensionRegistry>();

    private Map<String, ClassLoader> _tempItems = new ConcurrentHashMap<String, ClassLoader>();

    public ExtensionRegistryManager(){ }


    @Override
    synchronized public void addClass(Class clazz){
        logger.debug("looking for registry which can handle class {}. (available registries {})",
                this,
                clazz,
                _registries.size(),
                _registries);
        ExtensionRegistry toAddRegistry = findMatchingRegistry(clazz);

        if(toAddRegistry == null){
            if(!this._tempItems.keySet().contains(clazz)){
                this._tempItems.put(clazz.getName(), clazz.getClassLoader());
                logger.debug("temporarily added extension for type {} ==> Final Size is {}",
                        clazz,
                        this._tempItems.size());
            }
        } else {
            logger.debug("adding class to registry {} {}",
                    toAddRegistry.registryClass,
                    clazz);
            toAddRegistry.addClass(clazz);
        }
    }


    synchronized public void registerRegistry(ExtensionRegistry registry) {
        logger.debug("registering extension registry {} handling type: {}. ", this,
                registry.getClass().getName(),
                registry.registryClass);
        if (this._registries.get(registry.registryClass.getName()) != null) {
            logger.warn("registry for type {} was already in. previous setup wil be crushed",
                    registry.registryClass.getName());
        }

        //check if class already available
        List<String> _toRemove = new ArrayList<String>();
        for(String sC: this._tempItems.keySet()) {
            try {
                logger.debug("processing temp plugin class {}, cl: {}", sC);
                ClassLoader classLoader =  this._tempItems.get(sC);
                Class _c = classLoader.loadClass(sC);
                logger.debug("processing temp plugin class found {}", _c);
                boolean canHandle = _c != null && registry.registryClass.isAssignableFrom(_c);
                logger.debug("can Handle class: {}", canHandle);
                if(canHandle){
                    logger.debug("class {} added async in registry {} for gen class {}", _c, registry, registry.registryClass);
                    registry.addClass(_c);
                    _toRemove.add(sC);
                }
            }catch (Exception ex){
                logger.error(sC + "  not found", ex);
            }
        }
        this._tempItems.keySet().removeAll(_toRemove);
        _registries.put(registry.registryClass.getName(), registry);
        logger.debug("Setting registry for class: {} in registryManager (manager: {}. available registries {})", registry.registryClass, this, _registries);
    }

    @Override
    public Collection<String> itemsAsString(Class classz) {
        ExtensionRegistry extensionRegistry = findMatchingRegistry(classz);
        if (extensionRegistry != null) {
            return extensionRegistry.listAsString();
        } else {
            logger.warn("registry not found for {}", classz);
            return new ArrayList<String>();
        }

    }

    @Override
    public <T> Collection<Class<? extends T>> itemsAsClass(Class<T> classz) {
        ExtensionRegistry extensionRegistry = findMatchingRegistry(classz);
        if (extensionRegistry != null) {
            return extensionRegistry.list();
        } else {
            logger.warn("registry not found for {}", classz);
            return new ArrayList<Class<? extends T>>();
        }
    }

    private <T>  ExtensionRegistry<T> findMatchingRegistry(Class<T> item){
        for(ExtensionRegistry r: _registries.values()){
            boolean canHandle = r.registryClass.isAssignableFrom(item);
            logger.debug("looking for registry for ext class {} assignable from {}: {}", item,
                    r.registryClass, canHandle);
            if(canHandle){
                logger.info("found registry for {}: {}", item, r);
                return r;
            }
        }
        return null;
    }

}
