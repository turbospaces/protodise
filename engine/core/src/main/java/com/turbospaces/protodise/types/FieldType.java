package com.turbospaces.protodise.types;

public enum FieldType {
    BYTE,
    INT16,
    INT32,
    INT64,
    STRING,
    BOOL,
    FLOAT,
    DOUBLE,
    BINARY,
    ENUM,
    MESSAGE;

    public boolean isComlex() {
        return this == FieldType.MESSAGE || this == FieldType.ENUM;
    }

    public String javaTypeAsString() {
        switch ( this ) {
            case BINARY:
                return byte[].class.getSimpleName();
            case BOOL:
                return Boolean.class.getSimpleName();
            case BYTE:
                return Byte.class.getSimpleName();
            case DOUBLE:
                return Double.class.getSimpleName();
            case FLOAT:
                return Float.class.getSimpleName();
            case INT16:
                return Short.class.getSimpleName();
            case INT32:
                return Integer.class.getSimpleName();
            case INT64:
                return Long.class.getSimpleName();
            case STRING:
                return String.class.getSimpleName();
            case ENUM:
            case MESSAGE:
            default:
                throw new Error();
        }
    }
    public String csharpTypeAsString() {
        switch ( this ) {
            case BINARY:
                return byte[].class.getSimpleName();
            case BOOL:
                return "bool";
            case BYTE:
                return "byte";
            case DOUBLE:
                return "double";
            case FLOAT:
                return "float";
            case INT16:
                return "short";
            case INT32:
                return "int";
            case INT64:
                return "long";
            case STRING:
                return "string";
            case ENUM:
            case MESSAGE:
            default:
                throw new Error();
        }
    }
}
