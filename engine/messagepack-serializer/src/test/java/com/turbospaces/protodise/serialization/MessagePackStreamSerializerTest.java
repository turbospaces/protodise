package com.turbospaces.protodise.serialization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.turbospaces.demo.Address;
import com.turbospaces.demo.User;
import com.turbospaces.protodise.AbstractStreamsTest;

public class MessagePackStreamSerializerTest extends AbstractStreamsTest {
    MessagePackStream stream = new MessagePackStream();

    @Test
    @Override
    public void address() throws Exception {
        byte[] bytes = stream.serialize( a1 );
        Address prototype = new Address();
        stream.deserialize( prototype, bytes );
        assertEquals( a1, prototype );
    }

    @Test
    @Override
    public void user() throws Exception {
        byte[] bytes = stream.serialize( u );
        User prototype = new User();
        stream.deserialize( prototype, bytes );
        assertEquals( u, prototype );
    }
}
