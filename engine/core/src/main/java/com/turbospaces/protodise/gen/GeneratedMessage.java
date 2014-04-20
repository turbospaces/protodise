package com.turbospaces.protodise.gen;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

import com.turbospaces.protodise.MessageDescriptor.FieldDescriptor;

public interface GeneratedMessage extends Cloneable {
    Object getFieldValue(int tag);
    void setFieldValue(int tag, Object value);
    FieldDescriptor getFieldDescriptor(int tag);
    Collection<FieldDescriptor> getAllDescriptors();
    int getClassId();
    //
    // hash()-equals()-toString()
    //
    @Override
    String toString();
    @Override
    boolean equals(Object other);
    @Override
    int hashCode();

    public static abstract class Util {
        public static GeneratedMessage clone(GeneratedMessage m) {
            GeneratedMessage clone = null;
            try {
                clone = m.getClass().newInstance();
            }
            catch ( Exception e ) {
                throw new RuntimeException( e );
            }
            Collection<FieldDescriptor> descriptors = m.getAllDescriptors();
            for ( FieldDescriptor f : descriptors ) {
                Object obj = m.getFieldValue( f.getTag() );
                if ( obj != null ) {
                    clone.setFieldValue( f.getTag(), obj );
                }
            }
            return clone;
        }
        public static int hashCode(GeneratedMessage m) {
            int result = 1;
            Collection<FieldDescriptor> descriptors = m.getAllDescriptors();
            for ( FieldDescriptor f : descriptors ) {
                Object value = m.getFieldValue( f.getTag() );
                result = 31 * result + ( value == null ? 0 : value.hashCode() );
            }
            return result;
        }
        public static boolean equals(GeneratedMessage thiz, Object obj) {
            if ( obj == null )
                return false;
            if ( obj == thiz )
                return true;
            if ( !thiz.getClass().equals( obj.getClass() ) )
                return false;

            GeneratedMessage other = (GeneratedMessage) obj;
            boolean equals = true;
            Collection<FieldDescriptor> descriptors = thiz.getAllDescriptors();
            for ( FieldDescriptor f : descriptors ) {
                Object value = thiz.getFieldValue( f.getTag() );
                Object otherValue = other.getFieldValue( f.getTag() );

                equals = equals && Objects.deepEquals( value, otherValue );
                if ( !equals ) {
                    break;
                }
            }
            return equals;
        }
        public static String toString(GeneratedMessage m) {
            StringBuilder b = new StringBuilder();
            b.append( "{" ).append( m.getClass().getSimpleName() );

            Collection<FieldDescriptor> descriptors = m.getAllDescriptors();
            for ( Iterator<FieldDescriptor> iterator = descriptors.iterator(); iterator.hasNext(); ) {
                FieldDescriptor f = iterator.next();
                Object value = m.getFieldValue( f.getTag() );
                if ( value != null ) {
                    b.append( f.getName() ).append( "=" );
                    if ( value instanceof String ) {
                        b.append( "\"" + value + "\"" );
                    }
                    else {
                        b.append( value );
                    }
                    if ( iterator.hasNext() ) {
                        b.append( "," );
                    }
                }
            }
            b.append( "}" );
            return b.toString();
        }
        private Util() {}
    }
}
