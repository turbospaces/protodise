package com.turbospaces.protodise;

import static com.turbospaces.protodise.gen.GenException.check;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;

public final class EnumDescriptor extends NamedDescriptor {
    private final SortedMap<Integer, String> members = new TreeMap<Integer, String>();
    private final String pkg;

    public EnumDescriptor(String name, String pkg) {
        this.name = name;
        this.pkg = pkg;
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
    public String getPkg() {
        return pkg;
    }
    public SortedMap<Integer, String> getMembers() {
        return Collections.unmodifiableSortedMap( members );
    }
}
