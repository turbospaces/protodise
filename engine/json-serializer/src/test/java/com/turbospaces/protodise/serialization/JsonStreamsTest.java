package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.demo.User;
import com.turbospaces.protodise.AbstractStreamsTest;

public class JsonStreamsTest extends AbstractStreamsTest {
    JsonStream stream = new JsonStream();

    @Test
    @Override
    public void address() throws Exception {
        String str = stream.serialize( a1 );
        Address prototype = new Address();
        stream.deserialize( prototype, str );
        assertEquals( a1, prototype );
    }

    @Test
    @Override
    public void user() throws Exception {
        String json = stream.serialize( u );
        User prototype = new User();
        stream.deserialize( prototype, json );
        assertEquals( u, prototype );
    }

    @Test
    public void sizeAndToString() throws IOException {
        String s = stream.serialize( a1 );
        logger.debug( "size={}", s.getBytes( Charset.forName( "UTF-8" ) ).length );
    }
}
