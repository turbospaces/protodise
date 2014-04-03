package com.turbospaces.protodise;

import static com.turbospaces.protodise.gen.GenException.check;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Objects;
import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;
import com.turbospaces.protodise.gen.ProtoGenerationContext;
import com.turbospaces.protodise.types.MessageType;

public final class MessageDescriptor extends NamedDescriptor {
    private final String parent;
    private final String pkg;
    private boolean isException;
    private final Map<Integer, FieldDescriptor> fields = new HashMap<Integer, FieldDescriptor>();

    public MessageDescriptor(String name, String parent, String pkg) {
        this.name = name;
        this.parent = parent;
        this.pkg = pkg;
    }
    public void addField(int tag, FieldDescriptor desc) {
        check( !fields.containsKey( tag ), "message field with tag=%s already defined", tag );
        fields.put( tag, desc );
    }
    public Map<Integer, FieldDescriptor> getFieldDescriptors() {
        return Collections.unmodifiableMap( fields );
    }
    public FieldDescriptor getFieldDescriptor(int tag) {
        return fields.get( tag );
    }
    public String getParent() {
        return parent;
    }
    public String getPkg() {
        return pkg;
    }
    public boolean isException() {
        return isException;
    }
    public void setException(boolean isException) {
        this.isException = isException;
    }

    public static final class FieldDescriptor extends NamedDescriptor implements InitializingBean {
        private final int tag;
        private final MessageType type;

        public FieldDescriptor(int tag, String name, MessageType type) {
            this.tag = tag;
            this.name = name;
            this.type = type;
        }
        public int getTag() {
            return tag;
        }
        public String getName() {
            return name;
        }
        public MessageType getType() {
            return type;
        }
        @Override
        public void init(ProtoGenerationContext ctx) throws Exception {
            type.init( ctx );
        }
        @Override
        public String toString() {
            return Objects.toStringHelper( this ).add( "tag", tag ).add( "name", name ).add( "type", type ).toString();
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper( this ).add( "name", name ).add( "parent", parent ).add( "fields", fields ).toString();
    }
}
