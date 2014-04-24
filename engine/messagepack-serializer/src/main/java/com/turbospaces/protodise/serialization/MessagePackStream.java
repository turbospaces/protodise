package com.turbospaces.protodise.serialization;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.unpacker.Unpacker;

import com.turbospaces.protodise.MessageDescriptor.FieldDescriptor;
import com.turbospaces.protodise.MessageRegistry;
import com.turbospaces.protodise.Misc.ExposedByteArrayOutputStream;
import com.turbospaces.protodise.Stream;
import com.turbospaces.protodise.gen.GeneratedEnum;
import com.turbospaces.protodise.gen.GeneratedMessage;
import com.turbospaces.protodise.types.CollectionMessageType;
import com.turbospaces.protodise.types.FieldType;
import com.turbospaces.protodise.types.MapMessageType;
import com.turbospaces.protodise.types.MessageType;
import com.turbospaces.protodise.types.ObjectMessageType;

public final class MessagePackStream implements Stream {
    private final MessagePack msgpack;
    private final MessageRegistry registry;

    public MessagePackStream(MessagePack msgpack, MessageRegistry registry) {
        this.registry = registry;
        this.msgpack = msgpack;
    }

    public MessagePackStream(MessageRegistry registry) {
        this( new MessagePack(), registry );
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public GeneratedMessage
            deserialize(final InputStream stream) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        int lenght = stream.available();
        Unpacker unpacker = msgpack.createUnpacker( stream );

        try {
            int classId = unpacker.readInt();
            GeneratedMessage target = registry.newInstance( classId );
            while ( unpacker.getReadByteCount() < lenght ) {
                int tag = unpacker.readInt();
                FieldDescriptor fd = target.getFieldDescriptor( tag );
                MessageType type = fd.getType();

                if ( type instanceof CollectionMessageType ) {
                    CollectionMessageType cmt = (CollectionMessageType) type;
                    int size = unpacker.readArrayBegin();
                    Collection c = cmt.isSet() ? new HashSet( size ) : new ArrayList( size );
                    for ( int i = 0; i < size; i++ ) {
                        Object value = readValue( cmt.getElementType(), unpacker );
                        c.add( value );
                    }
                    target.setFieldValue( tag, c );
                    unpacker.readArrayEnd();
                }
                else if ( type instanceof MapMessageType ) {
                    MapMessageType mmt = (MapMessageType) type;
                    int size = unpacker.readMapBegin();
                    Map m = new HashMap();
                    for ( int i = 0; i < size; i++ ) {
                        Object key = readValue( mmt.getKeyType(), unpacker );
                        Object value = readValue( mmt.getValueType(), unpacker );
                        m.put( key, value );
                    }
                    target.setFieldValue( tag, m );
                    unpacker.readMapEnd();
                }
                else {
                    ObjectMessageType omt = (ObjectMessageType) type;
                    FieldType ftype = omt.getType();
                    Object value = readValue( ftype, unpacker );
                    target.setFieldValue( tag, value );
                }
            }
            return target;
        }
        finally {
            unpacker.close();
        }
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void serialize(final GeneratedMessage msg, final OutputStream stream) throws IOException {
        Collection<FieldDescriptor> desc = msg.getAllDescriptors();
        Packer packer = msgpack.createPacker( stream );

        try {
            packer.write( msg.getClassId() );
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
            stream.flush();
        }
        finally {
            packer.close();
        }
    }
    private Object
            readValue(final FieldType ftype, final Unpacker unpacker) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
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
                int classId = unpacker.readInt();
                int tag = unpacker.readInt();
                value = registry.enumInstance( classId, tag );
                break;
            case MESSAGE:
                byte[] bytes = unpacker.readByteArray();
                ByteArrayInputStream in = new ByteArrayInputStream( bytes );
                value = deserialize( in );
                in.close();
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
                GeneratedEnum genum = (GeneratedEnum) value;
                packer.write( genum.getClassId() );
                packer.write( genum.tag() );
                break;
            case MESSAGE:
                ExposedByteArrayOutputStream stream = new ExposedByteArrayOutputStream();
                serialize( (GeneratedMessage) value, stream );
                packer.write( stream.getBuffer(), 0, stream.size() );
                break;
            default:
                throw new Error();
        }
    }
}
