package com.turbospaces.protodise;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class Misc {
    public static int hash32(String text) {
        byte[] data = text.getBytes();
        int length = data.length;
        int seed = 0xe17a1465;

        final int m = 0x5bd1e995;
        final int r = 24;
        // Initialize the hash to a random value
        int h = seed ^ length;
        int length4 = length / 4;

        for ( int i = 0; i < length4; i++ ) {
            final int i4 = i * 4;
            int k = ( data[i4 + 0] & 0xff ) + ( ( data[i4 + 1] & 0xff ) << 8 ) + ( ( data[i4 + 2] & 0xff ) << 16 ) + ( ( data[i4 + 3] & 0xff ) << 24 );
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        // Handle the last few bytes of the input array
        switch ( length % 4 ) {
            case 3:
                h ^= ( data[( length & ~3 ) + 2] & 0xff ) << 16;
            case 2:
                h ^= ( data[( length & ~3 ) + 1] & 0xff ) << 8;
            case 1:
                h ^= ( data[length & ~3] & 0xff );
                h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
    public static boolean deepEquals(Object a, Object b) {
        if ( a == b )
            return true;
        else if ( a == null || b == null )
            return false;
        else if ( a instanceof Object[] && b instanceof Object[] )
            return Arrays.deepEquals( (Object[]) a, (Object[]) b );
        else
            return a.equals( b );
    }

    public static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public ExposedByteArrayOutputStream() {
            super();
        }
        public ExposedByteArrayOutputStream(int size) {
            super( size );
        }
        public byte[] getBuffer() {
            return buf;
        }
    }
}
