package com.turbospaces.protodise;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turbospaces.protodise.gen.GeneratedEnum;
import com.turbospaces.protodise.gen.GeneratedMessage;

public final class MessageRegistry {
    private final Logger logger = LoggerFactory.getLogger( getClass() );

    private final ConcurrentMap<Integer, Class<?>> messageIds = new ConcurrentHashMap<Integer, Class<?>>();
    private final ConcurrentMap<String, Class<?>> messageNames = new ConcurrentHashMap<String, Class<?>>();

    private final ClassResolver classResolver;

    public MessageRegistry(ClassResolver classResolver) {
        this.classResolver = classResolver;
    }
    public MessageRegistry() {
        this( ClassResolver.DEFAULT );
    }

    public void registerAll(Collection<MessageDescriptor> mds, Collection<EnumDescriptor> eds) {
        for ( MessageDescriptor md : mds ) {
            String className = md.getPkg() + "." + md.getName();
            try {
                Class<?> clazz = classResolver.resolve( className );
                GeneratedMessage msg = (GeneratedMessage) clazz.newInstance();
                int classId = msg.getClassId();
                Class<?> prev = messageIds.put( classId, clazz );
                if ( prev != null ) {
                    logger.warn( "Message with ID={} already registered, overriding {} to {}", classId, prev.getSimpleName(), className );
                }
                prev = messageNames.put( className, clazz );
                if ( prev != null ) {
                    logger.warn( "Message with Qualifier={} already registered, overriding {}", prev.getSimpleName(), className );
                }
            }
            catch ( Exception e ) {
                logger.error( e.getMessage(), e );
                throw new RuntimeException( e );
            }
        }
        for ( EnumDescriptor ed : eds ) {
            String enumName = ed.getPkg() + "." + ed.getName();
            try {
                Class<?> clazz = classResolver.resolve( enumName );
                Object[] enumConstants = clazz.getEnumConstants();
                for ( int i = 0; i < enumConstants.length; ) {
                    int classId = ( (GeneratedEnum) enumConstants[i] ).getClassId();
                    Class<?> prev = messageIds.put( classId, clazz );
                    if ( prev != null ) {
                        logger.warn( "Message with ID={} already registered, overriding {} to {}", classId, prev.getSimpleName(), ed.getName() );
                    }
                    prev = messageNames.put( enumName, clazz );
                    if ( prev != null ) {
                        logger.warn( "Enum with Qualifier={} already registered, overriding {}", prev.getSimpleName(), enumName );
                    }
                    break;
                }
            }
            catch ( Exception e ) {
                logger.error( e.getMessage(), e );
                throw new RuntimeException( e );
            }
        }
    }
    public GeneratedMessage newInstance(int classId) throws InstantiationException, IllegalAccessException {
        Class<?> c = messageIds.get( classId );
        if ( c == null ) {
            throw new IllegalStateException( String.format( "Message with ID=%s is not registered", classId ) );
        }
        return (GeneratedMessage) c.newInstance();
    }
    public GeneratedMessage newInstance(String classname) throws InstantiationException, IllegalAccessException {
        Class<?> c = messageNames.get( classname );
        if ( c == null ) {
            throw new IllegalStateException( String.format( "Message with name=%s is not registered", classname ) );
        }
        return (GeneratedMessage) c.newInstance();
    }
    public GeneratedEnum enumInstance(int classId, int tag) {
        Class<?> c = messageIds.get( classId );
        if ( c == null ) {
            throw new IllegalStateException( String.format( "Enum with ID=%s is not registered", classId ) );
        }
        GeneratedEnum[] gev = (GeneratedEnum[]) c.getEnumConstants();
        for ( GeneratedEnum ge : gev ) {
            if ( ge.tag() == tag )
                return ge;
        }
        return null;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public GeneratedEnum enumInstance(String classname, String member) {
        Class<? extends Enum> c = (Class<? extends Enum>) messageNames.get( classname );
        if ( c == null ) {
            throw new IllegalStateException( String.format( "Enum with name=%s is not registered", classname ) );
        }
        return (GeneratedEnum) Enum.valueOf( c, member );
    }
    public void unregisterAll() {
        messageIds.clear();
    }
    public ClassResolver getClassResolver() {
        return classResolver;
    }
}
