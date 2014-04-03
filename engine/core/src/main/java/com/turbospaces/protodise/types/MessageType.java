package com.turbospaces.protodise.types;

import com.turbospaces.protodise.InitializingBean;

public interface MessageType extends InitializingBean {
    boolean isMap();
    boolean isCollection();

    String javaTypeAsString(); // for java code generation
    String csharpTypeAsString(); // for c# code generation
}
