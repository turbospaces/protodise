package com.turbospaces.protodise;

import com.turbospaces.protodise.gen.ProtoGenerationContext;

public interface InitializingBean {
    void init(ProtoGenerationContext ctx) throws Exception;
}
