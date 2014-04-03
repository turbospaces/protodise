package com.turbospaces.protodise.gen;

import com.google.common.base.Function;

public enum GenLanguage {
    JAVA("java", new Function<String, String>() {
        @Override
        public String apply(String input) {
            return input;
        }
    }),
    CSHARP("cs", new Function<String, String>() {
        @Override
        public String apply(String input) {
            return "";
        }
    });

    private GenLanguage(String classPrefix, Function<String, String> pkgName) {
        this.classPrefix = classPrefix;
        this.pkgName = pkgName;
    }

    public String classPrefix;
    public Function<String, String> pkgName; // java package to language package transformation
}
