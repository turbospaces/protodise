package com.turbospaces.protodise.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.turbospaces.protodise.MessageDescriptor.FieldDescriptor;
import com.turbospaces.protodise.MessageRegistry;
import com.turbospaces.protodise.Stream;
import com.turbospaces.protodise.gen.GeneratedEnum;
import com.turbospaces.protodise.gen.GeneratedMessage;
import com.turbospaces.protodise.types.CollectionMessageType;
import com.turbospaces.protodise.types.FieldType;
import com.turbospaces.protodise.types.MapMessageType;
import com.turbospaces.protodise.types.MessageType;
import com.turbospaces.protodise.types.ObjectMessageType;

public class JsonStream implements Stream {
    public static final String QUALIFIER = "qualifier";

    private final JsonFactory factory = new JsonFactory();
    private final ObjectMapper mapper;
    private final MessageRegistry registry;

    {
        factory.enable( JsonParser.Feature.ALLOW_COMMENTS );
        mapper = new ObjectMapper( factory );
    }

    public JsonStream(MessageRegistry registry) {
        this.registry = registry;
    }

    @Override
    public GeneratedMessage deserialize(final InputStream in) throws IOException, InstantiationException, IllegalAccessException {
        JsonNode tree = mapper.readTree( in );
        String qualifier = tree.get( QUALIFIER ).asText();
        GeneratedMessage target = registry.newInstance( qualifier );
        deserialize( target, tree );
        return target;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void deserialize(final GeneratedMessage target, final JsonNode tree) throws IOException, InstantiationException, IllegalAccessException {
        Collection<FieldDescriptor> desc = target.getAllDescriptors();
        Map<String, FieldDescriptor> cache = new HashMap<String, FieldDescriptor>( desc.size() );
        for ( FieldDescriptor fd : desc ) {
            cache.put( fd.getName(), fd );
        }

        Iterator<Entry<String, JsonNode>> fields = tree.fields();
        while ( fields.hasNext() ) {
            Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode node = entry.getValue();
            if ( QUALIFIER.equals( fieldName ) )
                continue;

            FieldDescriptor fd = cache.get( fieldName );
            MessageType type = fd.getType();

            if ( type instanceof CollectionMessageType ) {
                CollectionMessageType cmt = (CollectionMessageType) type;
                Collection c = cmt.isSet() ? new HashSet() : new LinkedList();
                ArrayNode arrayNode = (ArrayNode) node;
                Iterator<JsonNode> elements = arrayNode.elements();
                while ( elements.hasNext() ) {
                    JsonNode arrElementNode = elements.next();
                    Object value = readValue( cmt.getElementType(), cmt.getElementTypeReference(), arrElementNode );
                    c.add( value );
                }
                target.setFieldValue( fd.getTag(), c );
            }
            else if ( type instanceof MapMessageType ) {
                MapMessageType mmt = (MapMessageType) type;
                Map m = new HashMap();
                ArrayNode arrayNode = (ArrayNode) node;
                Iterator<JsonNode> elements = arrayNode.elements();

                while ( elements.hasNext() ) {
                    JsonNode arrElementNode = elements.next();

                    MapMessageWrapper mmw = new MapMessageWrapper( mmt );
                    deserialize( mmw, arrElementNode );
                    m.put( mmw.key, mmw.value );
                }
                target.setFieldValue( fd.getTag(), m );
            }
            else {
                ObjectMessageType omt = (ObjectMessageType) type;
                FieldType ftype = omt.getType();
                Object value = readValue( ftype, omt.getTypeReference(), node );
                target.setFieldValue( fd.getTag(), value );
            }
        }
    }
    @Override
    public void serialize(final GeneratedMessage msg, final OutputStream out) throws IOException {
        JsonGenerator gen = factory.createGenerator( out );
        try {
            serialize( msg, gen );
        }
        finally {
            gen.close();
            out.flush();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void serialize(final GeneratedMessage msg, final JsonGenerator gen) throws IOException {
        Collection<FieldDescriptor> desc = msg.getAllDescriptors();

        gen.writeStartObject();
        if ( !( msg instanceof MapMessageWrapper ) ) {
            gen.writeStringField( QUALIFIER, msg.getClass().getName() );
        }
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

    private Object
            readValue(final FieldType ftype, final String typeRef, final JsonNode node) throws IOException, InstantiationException, IllegalAccessException {
        Object value = null;
        switch ( ftype ) {
            case BYTE:
                value = (byte) node.shortValue();
                break;
            case INT16:
                value = node.shortValue();
                break;
            case INT32:
                value = node.intValue();
                break;
            case INT64:
                value = node.longValue();
                break;
            case STRING:
                value = node.asText();
                break;
            case BOOL:
                value = node.booleanValue();
                break;
            case FLOAT:
                value = node.floatValue();
                break;
            case DOUBLE:
                value = node.doubleValue();
                break;
            case BINARY:
                value = node.binaryValue();
                break;
            case ENUM:
                value = registry.enumInstance( typeRef, node.asText() );
                break;
            case MESSAGE:
                GeneratedMessage m = registry.newInstance( typeRef );
                deserialize( m, node );
                value = m;
                break;
            default:
                throw new Error();
        }
        return value;
    }
    private void writeValue(final FieldType ftype, final Object value, final JsonGenerator packer) throws IOException {
        switch ( ftype ) {
            case BYTE:
                packer.writeNumber( (Byte) value );
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
                GeneratedEnum genum = (GeneratedEnum) value;
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
