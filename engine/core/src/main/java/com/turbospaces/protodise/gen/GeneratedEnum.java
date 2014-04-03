package com.turbospaces.protodise.gen;

public interface GeneratedEnum<T extends Enum<T>> {
    T valueOf(int tag);
    int tag();
}
