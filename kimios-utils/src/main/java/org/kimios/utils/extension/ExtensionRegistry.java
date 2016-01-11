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

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by farf on 6/22/14.
 */
public abstract class ExtensionRegistry<T> implements IExtensionRegistry<T> {


    private static Logger logger = LoggerFactory.getLogger(ExtensionRegistry.class);


    protected Class<T> registryClass;

    public ExtensionRegistry(){
        this.registryClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        ExtensionRegistryManager.registerRegistry(this);
    }

    private Map<String, Class<? extends T>> _registry = new ConcurrentHashMap<String, Class<? extends T>>();

    @Override
    public void addClass(Class<? extends T> extension){
            _registry.put(extension.getName(), extension);
            handleAdd(extension);
            logger.info("added class {}", extension.getName());
    }

    @Override
    public Class<? extends T> readClass(String clazz){
        return _registry.get(clazz);
    }

    @Override
    public void removeClass(String clazz){
        Class<? extends T> t = _registry.remove(clazz);
        handleRemove(t);
        logger.info("removed class {}",clazz);
    }

    @Override
    public Collection<String> listAsString(){
        return _registry.keySet();
    }

    @Override
    public Collection<Class<? extends T>> list(){
        return _registry.values();
    }

    protected void handleAdd(Class<? extends T> classz){}

    protected void handleRemove(Class<? extends T> classz){}

}
