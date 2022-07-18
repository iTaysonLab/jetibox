package com.microsoft.xalwrapper.models;

public class XalWebAccountParameter {
    private final String m_name;
    private final String m_value;

    public XalWebAccountParameter(String str, String str2) {
        this.m_name = str;
        this.m_value = str2;
    }

    public String getName() {
        return this.m_name;
    }

    public String getValue() {
        return this.m_value;
    }
}