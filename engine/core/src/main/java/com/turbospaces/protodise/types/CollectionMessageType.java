package com.turbospaces.protodise.types;

import com.turbospaces.protodise.gen.ProtoGenerationContext;

public class CollectionMessageType implements MessageType {
    private ObjectMessageType objectMessageType;
    private boolean isSet;

    public CollectionMessageType(String ref, boolean setOtherwiseList) {
        this.objectMessageType = new ObjectMessageType( ref );
        this.isSet = setOtherwiseList;
    }
    public CollectionMessageType(FieldType type, String ref, boolean setOtherwiseList) {
        this.objectMessageType = new ObjectMessageType( type, ref );
        this.isSet = setOtherwiseList;
    }
    @Override
    public void init(ProtoGenerationContext ctx) throws Exception {
        objectMessageType.init( ctx );
    }
    @Override
    public String javaTypeAsString() {
        String coll = isSet ? "Set" : "List";
        return coll + "<" + objectMessageType.javaTypeAsString() + ">";
    }
    @Override
    public String csharpTypeAsString() {
        String coll = isSet ? "ISet" : "IList";
        return coll + "<" + objectMessageType.csharpTypeAsString() + ">";
    }
    public FieldType getElementType() {
        return objectMessageType.getType();
    }
    public String getElementTypeReference() {
        return objectMessageType.getTypeReference();
    }
    public boolean isSet() {
        return isSet;
    }
    @Override
    public boolean isCollection() {
        return true;
    }
    @Override
    public boolean isMap() {
        return false;
    }
}
