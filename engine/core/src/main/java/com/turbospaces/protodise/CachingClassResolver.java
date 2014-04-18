package com.turbospaces.protodise;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class CachingClassResolver {
    private final ClassLoader classLoader;
    private final ConcurrentMap<String, Class<?>> cache = new ConcurrentHashMap<String, Class<?>>();

    public CachingClassResolver(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> resolve(String className) throws ClassNotFoundException {
        Class<?> clazz = cache.get( className );

        if ( clazz == null ) {
            try {
                clazz = classLoader.loadClass( className );
            }
            catch ( ClassNotFoundException e ) {
                clazz = Class.forName( className, false, classLoader );
            }
            cache.put( className, clazz );
        }

        return clazz;
    }
}
