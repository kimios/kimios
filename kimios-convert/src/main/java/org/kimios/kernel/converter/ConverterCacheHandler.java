package org.kimios.kernel.converter;

import org.kimios.kernel.converter.source.InputSource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: farf
 * Date: 1/22/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
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
