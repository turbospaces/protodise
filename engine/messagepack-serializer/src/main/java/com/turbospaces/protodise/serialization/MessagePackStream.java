package com.turbospaces.protodise.serialization;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.turbospaces.protodise.CachingClassResolver;
import com.turbospaces.protodise.MessageDescriptor.FieldDescriptor;
import com.turbospaces.protodise.gen.GeneratedEnum;
import com.turbospaces.protodise.gen.GeneratedMessage;
import com.turbospaces.protodise.types.CollectionMessageType;
import com.turbospaces.protodise.types.FieldType;
import com.turbospaces.protodise.types.MapMessageType;
import com.turbospaces.protodise.types.MessageType;
import com.turbospaces.protodise.types.ObjectMessageType;

public class MessagePackStream {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private final MessagePack msgpack;
    private final CachingClassResolver classResolver;

    public MessagePackStream(MessagePack msgpack, CachingClassResolver classResolver) {
        this.msgpack = checkNotNull( msgpack );
        this.classResolver = checkNotNull( classResolver );
    }
    public MessagePackStream(MessagePack msgpack) {
        this.msgpack = checkNotNull( msgpack );

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null ) {
            classLoader = this.getClass().getClassLoader();
        }
        this.classResolver = new CachingClassResolver( classLoader );
    }
    public MessagePackStream() {
        this( new MessagePack() );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void deserialize(final GeneratedMessage target, final byte[] arr) throws IOException {
        ByteArrayInputStream stream = new ByteArrayInputStream( arr );
        Unpacker unpacker = msgpack.createUnpacker( stream );

        try {
            while ( unpacker.getReadByteCount() < arr.length ) {
                int tag = unpacker.readInt();
                FieldDescriptor fd = target.getFieldDescriptor( tag );
                MessageType type = fd.getType();

                if ( type instanceof CollectionMessageType ) {
                    CollectionMessageType cmt = (CollectionMessageType) type;
                    int size = unpacker.readArrayBegin();
                    Collection c = cmt.isSet() ? Sets.newHashSetWithExpectedSize( size ) : Lists.newArrayListWithCapacity( size );
                    for ( int i = 0; i < size; i++ ) {
                        Object value = readValue( cmt.getElementType(), cmt.getElementTypeReference(), unpacker );
                        c.add( value );
                    }
                    target.setFieldValue( tag, c );
                    unpacker.readArrayEnd();
                }
                else if ( type instanceof MapMessageType ) {
                    MapMessageType mmt = (MapMessageType) type;
                    int size = unpacker.readMapBegin();
                    Map m = Maps.newHashMap();
                    for ( int i = 0; i < size; i++ ) {
                        Object key = readValue( mmt.getKeyType(), mmt.getKeyTypeReference(), unpacker );
                        Object value = readValue( mmt.getValueType(), mmt.getValueTypeReference(), unpacker );
                        m.put( key, value );
                    }
                    target.setFieldValue( tag, m );
                    unpacker.readMapEnd();
                }
                else {
                    ObjectMessageType omt = (ObjectMessageType) type;
                    FieldType ftype = omt.getType();
                    Object value = readValue( ftype, omt.getTypeReference(), unpacker );
                    target.setFieldValue( tag, value );
                }
            }
        }
        finally {
            unpacker.close();
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public byte[] serialize(final GeneratedMessage msg) throws IOException {
        Collection<FieldDescriptor> desc = msg.getAllDescriptors();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Packer packer = msgpack.createPacker( stream );

        try {
            for ( FieldDescriptor fd : desc ) {
                int tag = fd.getTag();
                MessageType type = fd.getType();
                Object value = msg.getFieldValue( tag );

                if ( value == null ) {
                    continue;
                }

                packer.write( tag );
                if ( type instanceof CollectionMessageType ) {
                    CollectionMessageType cmt = (CollectionMessageType) type;
                    Collection c = (Collection) value;

                    packer.writeArrayBegin( c.size() );
                    for ( Object next : c ) {
                        writeValue( cmt.getElementType(), next, packer );
                    }
                    packer.writeArrayEnd();
                }
                else if ( type instanceof MapMessageType ) {
                    MapMessageType mmt = (MapMessageType) type;
                    Map m = (Map) value;

                    packer.writeMapBegin( m.size() );
                    for ( Map.Entry next : (Set<Map.Entry>) m.entrySet() ) {
                        writeValue( mmt.getKeyType(), next.getKey(), packer );
                        writeValue( mmt.getValueType(), next.getValue(), packer );
                    }
                    packer.writeMapEnd();
                }
                else {
                    ObjectMessageType omt = (ObjectMessageType) type;
                    FieldType ftype = omt.getType();
                    writeValue( ftype, value, packer );
                }
            }

            return stream.toByteArray();
        }
        finally {
            packer.close();
        }
    }
    @SuppressWarnings("rawtypes")
    private Object readValue(final FieldType ftype, final String typeRef, final Unpacker unpacker) throws IOException {
        Object value = null;
        switch ( ftype ) {
            case BYTE:
                value = unpacker.readByte();
                break;
            case INT16:
                value = unpacker.readShort();
                break;
            case INT32:
                value = unpacker.readInt();
                break;
            case INT64:
                value = unpacker.readLong();
                break;
            case STRING:
                value = unpacker.readString();
                break;
            case BOOL:
                value = unpacker.readBoolean();
                break;
            case FLOAT:
                value = unpacker.readFloat();
                break;
            case DOUBLE:
                value = unpacker.readDouble();
                break;
            case BINARY:
                value = unpacker.readByteArray();
                break;
            case ENUM:
                try {
                    int orderNum = unpacker.readInt();
                    Class<?> enumClass = classResolver.resolve( typeRef );
                    GeneratedEnum e = (GeneratedEnum) enumClass.getEnumConstants()[0];
                    value = e.valueOf( orderNum );
                }
                catch ( ClassNotFoundException ex ) {
                    logger.error( ex.getMessage(), ex );
                    throw new IOException( ex );
                }
                break;
            case MESSAGE:
                try {
                    GeneratedMessage m;
                    m = (GeneratedMessage) classResolver.resolve( typeRef ).newInstance();
                    byte[] bytes = unpacker.readByteArray();
                    deserialize( m, bytes );
                    value = m;
                }
                catch ( InstantiationException ex ) {
                    logger.error( ex.getMessage(), ex );
                    throw new IOException( ex );
                }
                catch ( IllegalAccessException ex ) {
                    logger.error( ex.getMessage(), ex );
                    throw new IOException( ex );
                }
                catch ( ClassNotFoundException ex ) {
                    logger.error( ex.getMessage(), ex );
                    throw new IOException( ex );
                }
                break;
            default:
                throw new Error();
        }
        return value;
    }
    private void writeValue(final FieldType ftype, final Object value, final Packer packer) throws IOException {
        switch ( ftype ) {
            case BYTE:
                packer.write( (Byte) value );
                break;
            case INT16:
                packer.write( (Short) value );
                break;
            case INT32:
                packer.write( (Integer) value );
                break;
            case INT64:
                packer.write( (Long) value );
                break;
            case STRING:
                packer.write( (String) value );
                break;
            case BOOL:
                packer.write( (Boolean) value );
                break;
            case FLOAT:
                packer.write( (Float) value );
                break;
            case DOUBLE:
                packer.write( (Double) value );
                break;
            case BINARY:
                packer.write( (byte[]) value );
                break;
            case ENUM:
                GeneratedEnum<?> genum = (GeneratedEnum<?>) value;
                packer.write( genum.tag() );
                break;
            case MESSAGE:
                byte[] bytes = serialize( (GeneratedMessage) value );
                packer.write( bytes );
                break;
            default:
                throw new Error();
        }
    }
}
