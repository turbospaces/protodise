package com.turbospaces.protodise;

import static com.turbospaces.protodise.gen.GenException.check;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;
import com.turbospaces.protodise.gen.ProtoGenerationContext;
import com.turbospaces.protodise.types.MessageType;

public final class ServiceDescriptor extends NamedDescriptor {
    private String parent;
    private final Map<String, MethodDescriptor> methods = new HashMap<String, MethodDescriptor>();

    public ServiceDescriptor(String name) {
        this.name = name;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public void addMethod(String name, MethodDescriptor m) {
        check( !methods.containsKey( name ), "service method with name=%s already defined", name );
        methods.put( name, m );
    }
    public Map<String, MethodDescriptor> getMethods() {
        return Collections.unmodifiableMap( methods );
    }
    public String getParent() {
        return parent;
    }

    public static final class MethodDescriptor extends NamedDescriptor implements InitializingBean {
        private MessageType request, response;
        Set<String> exceptions = new HashSet<String>();

        public MethodDescriptor(String name) {
            this.name = name;
        }
        public void setRequestType(MessageType request) {
            this.request = request;
        }
        public void setResponseType(MessageType response) {
            this.response = response;
        }
        public MessageType getRequestType() {
            return request;
        }
        public MessageType getResponseType() {
            return response;
        }
        public Set<String> getExceptions() {
            return exceptions;
        }
        public void addException(String exception) {
            exceptions.add( exception );
        }
        @Override
        public void init(ProtoGenerationContext ctx) throws Exception {
            if ( request != null ) {
                request.init( ctx );
            }
            response.init( ctx );
            Set<String> qualified = new HashSet<String>();
            for ( String exc : exceptions ) {
                String q = ctx.qualifiedMessageReference( exc );
                qualified.add( q );
            }
            exceptions = qualified;
        }
        @Override
        public String toString() {
            return String.format( "MethodDescriptor [request=%s, response=%s, exceptions=%s]", request, response, exceptions );
        }
    }

    @Override
    public String toString() {
        return String.format( "ServiceDescriptor [name=%s, parent=%s, methods=%s]", name, parent, methods );
    }
}
