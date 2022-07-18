package com.microsoft.xal.logging;

import com.microsoft.xal.logging.XalLogger;

/* loaded from: classes2.dex */
public class LogEntry {
    private final XalLogger.LogLevel m_level;
    private final String m_message;

    public LogEntry(XalLogger.LogLevel logLevel, String str) {
        this.m_level = logLevel;
        this.m_message = str;
    }

    public int Level() {
        return this.m_level.ToInt();
    }

    public String Message() {
        return this.m_message;
    }
}