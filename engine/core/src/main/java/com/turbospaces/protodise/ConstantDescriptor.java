package com.turbospaces.protodise;

import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;
import com.turbospaces.protodise.types.FieldType;

public final class ConstantDescriptor extends NamedDescriptor {
    FieldType type;
    Object value;

    public Object getValue() {
        return value;
    }
    public FieldType getType() {
        return type;
    }

    public ConstantDescriptor(String name, String typeRef, String text) {
        this.name = name;
        type = FieldType.valueOf( typeRef.toUpperCase() );

        switch ( type ) {
            case BOOL:
                value = Boolean.parseBoolean( text );
                break;
            case BYTE:
                value = Byte.parseByte( text );
                break;
            case DOUBLE:
                value = Double.parseDouble( text );
                break;
            case FLOAT:
                value = Float.parseFloat( text );
                break;
            case INT16:
                value = Short.parseShort( text );
                break;
            case INT32:
                value = Integer.parseInt( text );
                break;
            case INT64:
                value = Long.parseLong( text );
                break;
            case STRING:
                value = text;
                break;
            case BINARY:
                throw new Error();
            case ENUM:
                throw new Error();
            case MESSAGE:
                throw new Error();
        }
    }
    @Override
    public String toString() {
        return String.format( "ConstantDescriptor [name=%s, type=%s, value=%s]", getName(), getType(), getValue() );
    }
}
