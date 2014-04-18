package com.turbospaces.protodise.serialization;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.turbospaces.demo.Address;
import com.turbospaces.protodise.AbstractStreamsTest;

public class JsonStreamBenchmark {
    public static void main(String... args) throws InterruptedException {
        final JsonStream stream = new JsonStream();
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
                            String json = stream.serialize( AbstractStreamsTest.a1 );
                            Address prototype = new Address();
                            stream.deserialize( prototype, json );
                            c.countDown();
                        }
                    }
                    catch ( IOException ex ) {
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
