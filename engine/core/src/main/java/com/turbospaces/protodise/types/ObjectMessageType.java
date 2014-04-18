package com.turbospaces.protodise.types;

import com.turbospaces.protodise.gen.ProtoGenerationContext;

public class ObjectMessageType implements MessageType {
    FieldType type;
    String ref;

    public ObjectMessageType(String ref) {
        this.ref = ref;
    }
    public ObjectMessageType(FieldType type, String ref) {
        this.type = type;
        this.ref = ref;
    }
    @Override
    public void init(ProtoGenerationContext ctx) {
        for ( FieldType t : FieldType.values() ) {
            if ( t.name().equals( ref.toUpperCase() ) ) {
                this.type = t;
                break;
            }
        }
        if ( this.type == null ) {
            String q = ctx.qualifiedMessageReference( getTypeReference() );
            if ( q != null ) {
                this.type = FieldType.MESSAGE;
                ref = q;
            }
            q = ctx.qualifiedEnumReference( getTypeReference() );
            if ( q != null ) {
                this.type = FieldType.ENUM;
                ref = q;
            }
        }
        assert ( this.type != null );
    }
    @Override
    public String javaTypeAsString() {
        return getType().isComlex() ? ref : getType().javaTypeAsString();
    }
    @Override
    public String csharpTypeAsString() {
        return getType().isComlex() ? ref : getType().csharpTypeAsString();
    }
    public FieldType getType() {
        return type;
    }
    public String getTypeReference() {
        return ref;
    }
    @Override
    public String toString() {
        return String.format( "ObjectMessageType [type=%s, ref=%s]", type, ref );
    }
    @Override
    public boolean isMap() {
        return false;
    }
    @Override
    public boolean isCollection() {
        return false;
    }
}
