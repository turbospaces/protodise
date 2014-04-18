package com.turbospaces.protodise;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.turbospaces.protodise.gen.GeneratedMessage;

public interface Stream {
    void deserialize(GeneratedMessage target, InputStream ins) throws IOException;
    void serialize(GeneratedMessage msg, OutputStream outs) throws IOException;

    public static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return buf;
        }
    }
}
