package com.turbospaces.protodise;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ClassResolver {
    private final ClassLoader classLoader;
    private final ConcurrentMap<String, Class<?>> cache = new ConcurrentHashMap<String, Class<?>>();

    public static final ClassResolver DEFAULT = new ClassResolver( null );

    public ClassResolver(ClassLoader cl) {
        ClassLoader c = cl;
        if ( c == null ) {
            c = Thread.currentThread().getContextClassLoader();
            if ( c == null ) {
                c = this.getClass().getClassLoader();
            }
        }
        this.classLoader = c;
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
