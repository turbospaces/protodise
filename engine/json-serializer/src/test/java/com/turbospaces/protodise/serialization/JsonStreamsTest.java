package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.demo.User;
import com.turbospaces.protodise.AbstractStreamsTest;

public class JsonStreamsTest extends AbstractStreamsTest {
    JsonStream stream = new JsonStream( registry );

    @Test
    @Override
    public void address() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.serialize( a1, out );
        System.out.println( new String( out.toByteArray() ) );
        Address prototype = (Address) stream.deserialize( new ByteArrayInputStream( out.toByteArray() ) );
        assertEquals( a1, prototype );
    }

    @Test
    @Override
    public void user() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.serialize( u, out );
        System.out.println( new String( out.toByteArray() ) );
        User prototype = (User) stream.deserialize( new ByteArrayInputStream( out.toByteArray() ) );
        assertEquals( u, prototype );
    }

    @Test
    public void sizeAndToString() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        stream.serialize( a1, out );
        logger.debug( "size={}", out.size() );
    }
}
