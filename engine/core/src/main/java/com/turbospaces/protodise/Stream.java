package com.turbospaces.protodise;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.turbospaces.protodise.gen.GeneratedMessage;

public interface Stream {
    /**
     * serialize message to user's provided output stream (both text and binary formats).
     * 
     * @param msg - any automatically generated message.
     * @param out - user provided output byte stream. 
     * @throws IOException - if for some reason bytes cannot be written to output stream.
     */
    void serialize(GeneratedMessage msg, OutputStream out) throws IOException;
    /**
     * decode byte array stream and de-serialize to object.
     * 
     * @param in - user provided input byte stream.
     * @return decoded message.
     * 
     * @throws IOException - if byte buffer 'whipped'.
     * @throws InstantiationException - if object can't be instantiated (doesn't have default constructor or for any other reason).
     * @throws IllegalAccessException - if object can't be instantiated (any security reason or for any other reason).
     * @throws ClassNotFoundException - if platform doesn't recognize message by class id.
     */
    GeneratedMessage deserialize(InputStream in) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException;
}
