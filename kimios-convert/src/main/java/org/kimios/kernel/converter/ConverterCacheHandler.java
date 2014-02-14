/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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

package org.kimios.kernel.converter;

import org.kimios.kernel.converter.source.InputSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterCacheHandler {


    private Map<Long, InputSource> _cache = new ConcurrentHashMap<Long, InputSource>();


    private static ConverterCacheHandler converterCacheHandler;

    private ConverterCacheHandler(){

    }

    public static ConverterCacheHandler getInstance(){
        if(converterCacheHandler == null){
            converterCacheHandler = new ConverterCacheHandler();
        }
        return converterCacheHandler;
    }

    public static void cachePreviewData(Long versionId, InputSource inputSource){
        getInstance()._cache.put(versionId, inputSource);
    }

    public static boolean cacheExist(Long versionId){
        return getInstance()._cache.get(versionId) != null;
    }

    public static InputSource load(Long versionId){
        return getInstance()._cache.get(versionId);
    }

    public static void cancelCache(Long versionId){
        getInstance()._cache.remove(versionId);
    }

}
