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

    private Map<String, ExtensionRegistry> _registry;


    private List<Class> _tempItems = new ArrayList<Class>();


    public synchronized static ExtensionRegistryManager init(){
        if(_registryManager == null){
            _registryManager = new ExtensionRegistryManager();
            _registryManager._registry = new HashMap<String, ExtensionRegistry>();
        }
        return _registryManager;
    }

    private ExtensionRegistryManager(){ }


    public static void addClass(Class clazz){
        ExtensionRegistry toAddRegistry = null;
        Class spClass = clazz;
        while(!spClass.equals(Object.class)){
            logger.info("processing registry for class");
            if (_registryManager._registry.get(spClass) != null){
                toAddRegistry = _registryManager._registry.get(spClass);
                logger.info("found registry for {}", spClass);
                break;
            }
            spClass = spClass.getSuperclass();
        }
        if(toAddRegistry == null){
            if(!_registryManager._tempItems.contains(clazz)){
                _registryManager._tempItems.add(clazz);
                logger.info("temporarily added extension {}", clazz);
            }
        } else {
            logger.info("adding class to registry {}", clazz);
            toAddRegistry.addClass(clazz);
        }
    }


    protected static void registerRegistry(ExtensionRegistry registry) {
        init();
        logger.info("registering extension registry {}", registry.getClass().getName());
        if (_registryManager._registry.get(registry.registryClass.getName()) != null) {
            logger.warn("registry for type {} was already in. previous setup wil be crushed",
                    registry.registryClass.getName());
        }
        //check if class already available

        List<Class> _toRemove = new ArrayList<Class>();
        for(Class _c: _registryManager._tempItems) {
            if(registry.registryClass.isAssignableFrom(_c) && _c != null){
                logger.info("class {} added async in registry {} for gen class {}", _c, registry, registry.registryClass);
                registry.addClass(_c);
            }
        }
        _registryManager._tempItems.removeAll(_toRemove);

        _registryManager._registry.put(registry.registryClass.getName(), registry);

    }


    public static Collection<String> itemsAsString(Class classz) {
        if (_registryManager._registry.containsKey(classz.getName())) {
            return _registryManager._registry.get(classz.getName()).listAsString();
        } else {
            return new ArrayList<String>();
        }

    }

    public static Collection<Class> itemsAsClass(Class classz) {
        if (_registryManager._registry.containsKey(classz.getName())) {
            return _registryManager._registry.get(classz.getName()).list();
        } else {
            return new ArrayList<Class>();
        }
    }

}
