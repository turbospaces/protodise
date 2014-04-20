package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.demo.User;
import com.turbospaces.protodise.AbstractStreamsTest;

public class MessagePackStreamSerializerTest extends AbstractStreamsTest {
    MessagePackStream stream = new MessagePackStream( registry );

    @Test
    @Override
    public void address() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream.serialize( a1, baos );
        Address prototype = (Address) stream.deserialize( new ByteArrayInputStream( baos.toByteArray() ) );
        assertEquals( a1, prototype );
    }

    @Test
    @Override
    public void user() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream.serialize( u, baos );
        User prototype = (User) stream.deserialize( new ByteArrayInputStream( baos.toByteArray() ) );
        assertEquals( u, prototype );
    }

    @Test
    public void size() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        stream.serialize( a1, baos );
        logger.debug( "size={}", baos.size() );
    }
}
