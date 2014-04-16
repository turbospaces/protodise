package com.turbospaces.protodise;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

public class CachingClassResolver {
    private final ClassLoader classLoader;
    private final ConcurrentMap<String, Class<?>> CACHE = Maps.newConcurrentMap();

    public CachingClassResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> resolve(String className) throws ClassNotFoundException {
        Class<?> clazz = CACHE.get( className );

        if ( clazz == null ) {
            try {
                clazz = classLoader.loadClass( className );
            }
            catch ( ClassNotFoundException e ) {
                clazz = Class.forName( className, false, classLoader );
            }
            CACHE.put( className, clazz );
        }

        return clazz;
    }
}
