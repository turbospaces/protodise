package com.turbospaces.protodise.serialization;

import java.util.Collection;

import com.google.common.collect.ImmutableSet;
import com.turbospaces.protodise.MessageDescriptor.FieldDescriptor;
import com.turbospaces.protodise.gen.GeneratedMessage;
import com.turbospaces.protodise.types.MapMessageType;
import com.turbospaces.protodise.types.ObjectMessageType;

public final class MapMessageWrapper implements GeneratedMessage {
    private final Collection<FieldDescriptor> fieldDescriptors;
    private final FieldDescriptor keyDesc, valueDesc;

    Object key, value;

    public MapMessageWrapper(MapMessageType mmt) {
        this.keyDesc = new FieldDescriptor( 1, "key", new ObjectMessageType( mmt.getKeyType(), mmt.getKeyTypeReference() ) );
        this.valueDesc = new FieldDescriptor( 2, "value", new ObjectMessageType( mmt.getValueType(), mmt.getValueTypeReference() ) );
        this.fieldDescriptors = ImmutableSet.of( this.keyDesc, this.valueDesc );
    }

    @Override
    public Object getFieldValue(int tag) {
        if ( keyDesc.getTag() == tag )
            return key;
        if ( valueDesc.getTag() == tag )
            return value;
        throw new Error();
    }
    @Override
    public void setFieldValue(int tag, Object obj) {
        if ( keyDesc.getTag() == tag )
            key = obj;
        else if ( valueDesc.getTag() == tag )
            value = obj;
        else {
            throw new Error();
        }
    }
    @Override
    public FieldDescriptor getFieldDescriptor(int tag) {
        if ( keyDesc.getTag() == tag )
            return keyDesc;
        if ( valueDesc.getTag() == tag )
            return valueDesc;
        throw new Error();
    }
    @Override
    public Collection<FieldDescriptor> getAllDescriptors() {
        return fieldDescriptors;
    }
}
