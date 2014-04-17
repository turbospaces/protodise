package com.turbospaces.protodise.serialization;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.google.common.base.Throwables;
import com.turbospaces.demo.Address;
import com.turbospaces.protodise.AbstractStreamsTest;

public class MessagePackStreamBenchmark {
    public static void main(String... args) throws InterruptedException {
        final MessagePackStream stream = new MessagePackStream();
        final int threads = 4;
        final int iterations = 1024 * 1024;
        final CountDownLatch c = new CountDownLatch( iterations * threads );
        final long now = System.currentTimeMillis();

        for ( int i = 0; i < threads; i++ ) {
            Thread t = new Thread( new Runnable() {
                public void run() {
                    try {
                        for ( int j = 0; j < iterations; j++ ) {
                            byte[] bytes = stream.serialize( AbstractStreamsTest.a1 );
                            Address prototype = new Address();
                            stream.deserialize( prototype, bytes );
                            c.countDown();
                        }
                    }
                    catch ( IOException ex ) {
                        Throwables.propagate( ex );
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
