package com.turbospaces.protodise.types;

import com.turbospaces.protodise.InitializingBean;
import com.turbospaces.protodise.gen.ProtoGenerationContext;

public class MapMessageType implements MessageType, InitializingBean {
    String kref, vref;
    FieldType ktype, vtype;

    public MapMessageType(String keyRef, String valueRef) {
        this.kref = keyRef;
        this.vref = valueRef;
    }
    public MapMessageType(FieldType ktype, String kref, FieldType vtype, String vref) {
        this.kref = kref;
        this.vref = vref;
        this.ktype = ktype;
        this.vtype = vtype;
    }
    public String getKeyTypeReference() {
        return kref;
    }
    public String getValueTypeReference() {
        return vref;
    }
    public FieldType getKeyType() {
        return ktype;
    }
    public FieldType getValueType() {
        return vtype;
    }
    @Override
    public void init(ProtoGenerationContext ctx) throws Exception {
        for ( FieldType t : FieldType.values() ) {
            if ( t.name().equals( kref.toUpperCase() ) ) {
                this.ktype = t;
            }
            else if ( t.name().equals( vref.toUpperCase() ) ) {
                this.vtype = t;
            }
        }

        {
            if ( this.ktype == null ) {
                String kq = ctx.qualifiedMessageReference( getKeyTypeReference() );
                if ( kq != null ) {
                    this.ktype = FieldType.MESSAGE;
                    this.kref = kq;
                }
                kq = ctx.qualifiedEnumReference( getKeyTypeReference() );
                if ( kq != null ) {
                    this.ktype = FieldType.ENUM;
                    this.kref = kq;
                }
            }
            assert ( this.ktype != null );
        }
        {
            if ( this.vtype == null ) {
                String vq = ctx.qualifiedMessageReference( getValueTypeReference() );
                if ( vq != null ) {
                    this.vtype = FieldType.MESSAGE;
                    this.vref = vq;
                }
                vq = ctx.qualifiedEnumReference( getValueTypeReference() );
                if ( vq != null ) {
                    this.vtype = FieldType.ENUM;
                    this.vref = vq;
                }
            }
            assert ( this.vtype != null );
        }
    }
    @Override
    public String javaTypeAsString() {
        String k = ktype.isComlex() ? kref : ktype.javaTypeAsString();
        String v = vtype.isComlex() ? vref : vtype.javaTypeAsString();
        return "Map<" + k + "," + v + ">";
    }
    @Override
    public String csharpTypeAsString() {
        String k = ktype.isComlex() ? kref : ktype.csharpTypeAsString();
        String v = vtype.isComlex() ? vref : vtype.csharpTypeAsString();
        return "IDictionary<" + k + "," + v + ">";
    }
    @Override
    public boolean isMap() {
        return true;
    }
    @Override
    public boolean isCollection() {
        return false;
    }
}
