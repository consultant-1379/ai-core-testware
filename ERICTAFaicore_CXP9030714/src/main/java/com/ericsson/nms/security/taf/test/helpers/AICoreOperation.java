package com.ericsson.nms.security.taf.test.helpers;

public enum AICoreOperation {
    DELETE("delete"), WRITE("write"), READ("read");

    private String value;

    AICoreOperation(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
