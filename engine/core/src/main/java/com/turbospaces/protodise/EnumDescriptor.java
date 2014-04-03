package com.turbospaces.protodise;

import static com.turbospaces.protodise.gen.GenException.check;

import java.util.Collections;
import java.util.SortedMap;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.turbospaces.protodise.ProtoContainer.NamedDescriptor;

public final class EnumDescriptor extends NamedDescriptor {
    private SortedMap<Integer, String> members = Maps.newTreeMap();

    public EnumDescriptor(String name) {
        this.name = name;
    }
    public void addMember(Integer tag, String member) {
        check( !members.containsKey( tag ), "enum member with tag=%s already defined", tag );
        members.put( tag, member );
    }
    @Override
    public String toString() {
        return Objects.toStringHelper( this ).add( "name", name ).add( "values", members ).toString();
    }
    public String getName() {
        return name;
    }
    public SortedMap<Integer, String> getMembers() {
        return Collections.unmodifiableSortedMap( members );
    }
}
