package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.protodise.AbstractStreamsTest;

public class JsonStreamsTest extends AbstractStreamsTest {
    @Test
    @Override
    public void address() throws Exception {
        String str = JsonStreams.serialize( a1 );
        System.out.println( str );
        Address prototype = new Address();
        JsonStreams.deserialize( prototype, str );
        assertEquals( a1, prototype );
    }

    @Override
    public void user() throws Exception {}
}
