package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.protodise.AbstractStreamsTest;

public class JsonStreamsTest extends AbstractStreamsTest {
    JsonStreams stream = new JsonStreams();

    @Test
    @Override
    public void address() throws Exception {
        String str = stream.serialize( a1 );
        System.out.println( str );
        Address prototype = new Address();
        stream.deserialize( prototype, str );
        assertEquals( a1, prototype );
    }

    @Override
    public void user() throws Exception {}
}
