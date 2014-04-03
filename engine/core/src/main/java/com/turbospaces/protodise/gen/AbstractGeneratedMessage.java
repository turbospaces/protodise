package com.turbospaces.protodise.gen;

public abstract class AbstractGeneratedMessage implements GeneratedMessage {
    @Override
    public AbstractGeneratedMessage clone() {
        return (AbstractGeneratedMessage) GeneratedMessage.Util.clone( this );
    }
    @Override
    public int hashCode() {
        return GeneratedMessage.Util.hashCode( this );
    }
    @Override
    public boolean equals(Object obj) {
        return GeneratedMessage.Util.equals( this, obj );
    }
    @Override
    public String toString() {
        return GeneratedMessage.Util.toString( this );
    }
}
