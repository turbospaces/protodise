package com.turbospaces.protodise.serialization;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CountDownLatch;

import com.turbospaces.protodise.AbstractStreamsTest;
import com.turbospaces.protodise.Misc.ExposedByteArrayOutputStream;

public class JsonStreamBenchmark {
    public static void main(String... args) throws InterruptedException {
        final JsonStream stream = new JsonStream( AbstractStreamsTest.registry );
        final int threads = Runtime.getRuntime().availableProcessors();
        final int iterations = 1024 * 1024;
        final CountDownLatch c = new CountDownLatch( iterations * threads );
        final long now = System.currentTimeMillis();

        for ( int i = 0; i < threads; i++ ) {
            Thread t = new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        for ( int j = 0; j < iterations; j++ ) {
                            ExposedByteArrayOutputStream baos = new ExposedByteArrayOutputStream( 512 );
                            stream.serialize( AbstractStreamsTest.a1, baos );
                            stream.deserialize( new ByteArrayInputStream( baos.getBuffer(), 0, baos.size() ) );
                            c.countDown();
                        }
                    }
                    catch ( Exception ex ) {
                        throw new RuntimeException( ex );
                    }
                }
            } );
            t.setName( "XXX-THREAD-" + i );
            t.start();
        }
        c.await();

        long took = ( System.currentTimeMillis() - now );
        double txPerMs = ( ( (double) ( iterations * threads ) ) / ( (double) took ) );

        System.out.println( "took  " + took );
        System.out.println( "Operations = " + (int) ( txPerMs * 1000 ) );
    }
}
