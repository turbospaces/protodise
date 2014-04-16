package com.turbospaces.protodise.serialization;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.base.Enums;
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

public class JsonStreams {
    private final Logger logger = LoggerFactory.getLogger( getClass() );
    private final ConcurrentMap<Class<?>, Map<String, FieldDescriptor>> FIELD_NAMES = Maps.newConcurrentMap();
    private final JsonFactory factory = new JsonFactory();
    private final CachingClassResolver classResolver;

    {
        factory.disable( JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT );
        factory.disable( JsonGenerator.Feature.AUTO_CLOSE_TARGET );
        factory.enable( JsonParser.Feature.ALLOW_COMMENTS );
    }

    public JsonStreams(CachingClassResolver classResolver) {
        this.classResolver = classResolver;

    }
    public JsonStreams() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if ( classLoader == null ) {
            classLoader = this.getClass().getClassLoader();
        }
        this.classResolver = new CachingClassResolver( classLoader );
    }

    public void deserialize(final GeneratedMessage target, final String json) throws IOException {
        JsonParser parser = factory.createParser( json );

        try {
            parser.nextToken();
            deserialize( target, parser );
        }
        finally {
            parser.close();
        }
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void deserialize(final GeneratedMessage target, final JsonParser parser) throws IOException {
        Collection<FieldDescriptor> desc = target.getAllDescriptors();
        Map<String, FieldDescriptor> cache = FIELD_NAMES.get( target.getClass() );

        if ( cache == null ) {
            cache = Maps.newHashMapWithExpectedSize( desc.size() );
            for ( FieldDescriptor fd : desc ) {
                cache.put( fd.getName(), fd );
            }
            Map<String, FieldDescriptor> prev = FIELD_NAMES.putIfAbsent( target.getClass(), cache );
            if ( prev != null ) {
                cache = prev;
            }
        }
        assert ( parser.getCurrentToken() == JsonToken.START_OBJECT );

        while ( parser.nextToken() != JsonToken.END_OBJECT ) {
            String fieldName = parser.getCurrentName();
            FieldDescriptor fd = cache.get( fieldName );
            MessageType type = fd.getType();

            if ( type instanceof CollectionMessageType ) {
                CollectionMessageType cmt = (CollectionMessageType) type;
                Collection c = cmt.isSet() ? Sets.newHashSet() : Lists.newLinkedList();
                JsonToken startArrayToken = parser.nextToken();
                assert ( startArrayToken == JsonToken.START_ARRAY );

                while ( parser.nextToken() != JsonToken.END_ARRAY ) {
                    Object value = readValue( cmt.getElementType(), cmt.getElementTypeReference(), parser );
                    c.add( value );
                }
                target.setFieldValue( fd.getTag(), c );
            }
            else if ( type instanceof MapMessageType ) {
                MapMessageType mmt = (MapMessageType) type;
                Map m = Maps.newHashMap();
                JsonToken startArray = parser.nextToken();
                assert ( startArray == JsonToken.START_ARRAY );
                while ( parser.nextToken() != JsonToken.END_ARRAY ) {
                    MapMessageWrapper mmw = new MapMessageWrapper( mmt );
                    deserialize( mmw, parser );
                    m.put( mmw.key, mmw.value );
                }
                target.setFieldValue( fd.getTag(), m );
            }
            else {
                ObjectMessageType omt = (ObjectMessageType) type;
                FieldType ftype = omt.getType();
                parser.nextToken();
                Object value = readValue( ftype, omt.getTypeReference(), parser );
                target.setFieldValue( fd.getTag(), value );
            }
        }
    }
    public String serialize(final GeneratedMessage msg) throws IOException {
        StringWriter stream = new StringWriter();
        JsonGenerator gen = factory.createGenerator( stream );
        try {
            serialize( msg, gen );
        }
        finally {
            gen.close();
        }
        return stream.toString();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void serialize(final GeneratedMessage msg, final JsonGenerator gen) throws IOException {
        Collection<FieldDescriptor> desc = msg.getAllDescriptors();

        gen.writeStartObject();
        for ( FieldDescriptor fd : desc ) {
            MessageType type = fd.getType();
            String fieldName = fd.getName();
            Object value = msg.getFieldValue( fd.getTag() );

            if ( value == null ) {
                continue;
            }

            gen.writeFieldName( fieldName );

            if ( type instanceof CollectionMessageType ) {
                CollectionMessageType cmt = (CollectionMessageType) type;
                Collection c = (Collection) value;

                gen.writeStartArray();
                for ( Object next : c ) {
                    writeValue( cmt.getElementType(), next, gen );
                }
                gen.writeEndArray();
            }
            else if ( type instanceof MapMessageType ) {
                MapMessageType mmt = (MapMessageType) type;
                Map m = (Map) value;

                gen.writeStartArray();
                for ( Map.Entry next : (Set<Map.Entry>) m.entrySet() ) {
                    MapMessageWrapper mmw = new MapMessageWrapper( mmt );
                    mmw.key = next.getKey();
                    mmw.value = next.getValue();
                    writeValue( FieldType.MESSAGE, mmw, gen );
                }
                gen.writeEndArray();
            }
            else {
                ObjectMessageType omt = (ObjectMessageType) type;
                FieldType ftype = omt.getType();
                writeValue( ftype, value, gen );
            }
        }
        gen.writeEndObject();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Object readValue(final FieldType ftype, final String typeRef, final JsonParser unpacker) throws IOException {
        Object value = null;
        switch ( ftype ) {
            case BYTE:
                break;
            case INT16:
                value = unpacker.getShortValue();
                break;
            case INT32:
                value = unpacker.getIntValue();
                break;
            case INT64:
                value = unpacker.getLongValue();
                break;
            case STRING:
                value = unpacker.getText();
                break;
            case BOOL:
                value = unpacker.getBooleanValue();
                break;
            case FLOAT:
                value = unpacker.getFloatValue();
                break;
            case DOUBLE:
                value = unpacker.getDoubleValue();
                break;
            case BINARY:
                value = unpacker.getBinaryValue();
                break;
            case ENUM:
                try {
                    Class<? extends Enum> enumClass = (Class<? extends Enum>) classResolver.resolve( typeRef );
                    value = Enums.valueOfFunction( enumClass ).apply( unpacker.getText() );
                }
                catch ( ClassNotFoundException ex ) {
                    logger.error( ex.getMessage(), ex );
                    throw new IOException( ex );
                }

                break;
            case MESSAGE:
                try {
                    Class<?> valueClass = classResolver.resolve( typeRef );
                    GeneratedMessage m = (GeneratedMessage) valueClass.newInstance();
                    deserialize( m, unpacker.getCurrentToken().toString() );
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
    private void writeValue(final FieldType ftype, final Object value, final JsonGenerator packer) throws IOException {
        switch ( ftype ) {
            case BYTE:
                break;
            case INT16:
                packer.writeNumber( (Short) value );
                break;
            case INT32:
                packer.writeNumber( (Integer) value );
                break;
            case INT64:
                packer.writeNumber( (Long) value );
                break;
            case STRING:
                packer.writeString( (String) value );
                break;
            case BOOL:
                packer.writeBoolean( (Boolean) value );
                break;
            case FLOAT:
                packer.writeNumber( (Float) value );
                break;
            case DOUBLE:
                packer.writeNumber( (Double) value );
                break;
            case BINARY:
                packer.writeBinary( (byte[]) value );
                break;
            case ENUM:
                GeneratedEnum<?> genum = (GeneratedEnum<?>) value;
                packer.writeString( genum.toString() );
                break;
            case MESSAGE:
                GeneratedMessage msg = (GeneratedMessage) value;
                serialize( msg, packer );
                break;
            default:
                throw new Error();
        }
    }
}
