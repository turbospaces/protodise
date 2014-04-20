package com.turbospaces.protodise;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ProtoContainer {
    public String pkg, name;
    public Set<String> imports = new LinkedHashSet<String>();
    public Map<String, ServiceDescriptor> services = new HashMap<String, ServiceDescriptor>();
    //
    public Map<String, MessageDescriptor> messages = new HashMap<String, MessageDescriptor>();
    public Map<String, String> aliases = new HashMap<String, String>();
    public Map<String, EnumDescriptor> enums = new HashMap<String, EnumDescriptor>();
    public Map<String, ConstantDescriptor> constants = new HashMap<String, ConstantDescriptor>();

    public String getPkg() {
        return pkg;
    }
    public String getName() {
        return name;
    }
    public Collection<MessageDescriptor> getMessages() {
        return messages.values();
    }
    public Collection<ConstantDescriptor> getConstants() {
        return constants.values();
    }
    public Collection<EnumDescriptor> getEnums() {
        return enums.values();
    }
    @Override
    public String toString() {
        return String.format(
                "ProtoContainer [pkg=%s, name=%s, imports=%s, services=%s, messages=%s, aliases=%s, enums=%s, constants=%s]",
                pkg,
                name,
                imports,
                services,
                messages,
                aliases,
                enums,
                constants );
    }

    public static abstract class NamedDescriptor {
        protected String name;

        public String getName() {
            return name;
        }
    }
}
