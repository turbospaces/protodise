package com.turbospaces.protodise;

import static com.turbospaces.protodise.gen.GenException.check;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;

public final class EnumDescriptor extends NamedDescriptor {
    private final SortedMap<Integer, String> members = new TreeMap<Integer, String>();

    public EnumDescriptor(String name) {
        this.name = name;
    }
    public void addMember(Integer tag, String member) {
        check( !members.containsKey( tag ), "enum member with tag=%s already defined", tag );
        members.put( tag, member );
    }
    @Override
    public String toString() {
        return String.format( "EnumDescriptor [name=%s, members=%s]", getName(), getMembers() );
    }
    @Override
    public String getName() {
        return name;
    }
    public SortedMap<Integer, String> getMembers() {
        return Collections.unmodifiableSortedMap( members );
    }
}
