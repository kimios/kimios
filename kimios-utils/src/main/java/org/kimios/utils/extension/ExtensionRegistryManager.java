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

/**
 * Created by farf on 6/22/14.
 */
public class ExtensionRegistryManager {


    private static Logger logger = LoggerFactory.getLogger(ExtensionRegistryManager.class);

    private static ExtensionRegistryManager _registryManager;

    private Map<String, ExtensionRegistry> _registries;


    private Map<String, ClassLoader> _tempItems = new HashMap<String, ClassLoader>();


    public synchronized static ExtensionRegistryManager init(){
        if(_registryManager == null){
            _registryManager = new ExtensionRegistryManager();
            _registryManager._registries = new HashMap<String, ExtensionRegistry>();
        }
        return _registryManager;
    }

    private ExtensionRegistryManager(){ }


    public static void addClass(Class clazz){
        ExtensionRegistry toAddRegistry = null;
        Class spClass = clazz;
        while(spClass != null && !spClass.equals(Object.class)){
            if(logger.isDebugEnabled())
                logger.debug("looking for registry for ext class {} ---> {}", spClass, _registryManager._registries.get(spClass.getName()));
            if (_registryManager._registries.get(spClass.getName()) != null){
                toAddRegistry = _registryManager._registries.get(spClass.getName());
                logger.info("found registry for {}: {}", spClass, toAddRegistry);
                break;
            }
            spClass = spClass.getSuperclass();
        }
        if(toAddRegistry == null){
            if(!_registryManager._tempItems.keySet().contains(clazz)){
                _registryManager._tempItems.put(clazz.getName(), clazz.getClassLoader());
                logger.info("temporarily added extension for type {}: {}", spClass, clazz);
            }
        } else {
            logger.info("adding class to registry {}: {}",spClass, clazz);
            toAddRegistry.addClass(clazz);
        }
    }


    protected static void registerRegistry(ExtensionRegistry registry) {
        init();
        logger.info("registering extension registry {}", registry.getClass().getName());
        if (_registryManager._registries.get(registry.registryClass.getName()) != null) {
            logger.warn("registry for type {} was already in. previous setup wil be crushed",
                    registry.registryClass.getName());
        }

        //check if class already available
        List<String> _toRemove = new ArrayList<String>();
        for(String sC: _registryManager._tempItems.keySet()) {
            try {
                ClassLoader classLoader =  _registryManager._tempItems.get(sC);
                Class _c = classLoader.loadClass(sC);
                if(registry.registryClass.isAssignableFrom(_c) && _c != null){
                    logger.info("class {} added async in registry {} for gen class {}", _c, registry, registry.registryClass);
                    registry.addClass(_c);
                    _toRemove.add(sC);
                }
            }catch (Exception ex){
                logger.error(sC + "  not found", ex);
            }


        }
        _registryManager._tempItems.keySet().removeAll(_toRemove);

        logger.info("Setting registry for class: {} in registryManager (manager: {}", registry.registryClass, registry.getClass());


        //check if in osgi mode !!!


        _registryManager._registries.put(registry.registryClass.getName(), registry);

    }

    public static Collection<String> itemsAsString(Class classz) {
        if (_registryManager._registries.containsKey(classz.getName())) {
            return _registryManager._registries.get(classz.getName()).listAsString();
        } else {
            return new ArrayList<String>();
        }

    }

    public static <T> Collection<Class<? extends T>> itemsAsClass(Class<T> classz) {
        if (_registryManager._registries.containsKey(classz.getName())) {
            return _registryManager._registries.get(classz.getName()).list();
        } else {
            return new ArrayList<Class<? extends T>>();
        }
    }

}
