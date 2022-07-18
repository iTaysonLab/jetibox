package com.microsoft.xal.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/* loaded from: classes2.dex */
public class XalLogger implements AutoCloseable {
    private static final SimpleDateFormat LogDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final String TAG = "XALJAVA";
    private final String m_subArea;
    private final ArrayList<LogEntry> m_logs = new ArrayList<>();
    private LogLevel m_leastVerboseLevel = LogLevel.Verbose;

    /* loaded from: classes2.dex */
    public enum LogLevel {
        Error(1, 'E'),
        Warning(2, 'W'),
        Important(3, 'P'),
        Information(4, 'I'),
        Verbose(5, 'V');
        
        private final char m_levelChar;
        private final int m_val;

        LogLevel(int i2, char c2) {
            this.m_val = i2;
            this.m_levelChar = c2;
        }

        public char ToChar() {
            return this.m_levelChar;
        }

        public int ToInt() {
            return this.m_val;
        }
    }

    public XalLogger(String str) {
        this.m_subArea = str;
        Verbose("XalLogger created.");
    }

    private String Timestamp() {
        return LogDateFormat.format(GregorianCalendar.getInstance().getTime());
    }

    private static native void nativeLogBatch(int i2, LogEntry[] logEntryArr);

    public void Error(String str) {
        String.format("[%s] %s", this.m_subArea, str);
        Log(LogLevel.Error, str);
    }

    public synchronized void Flush() {
        if (this.m_logs.isEmpty()) {
            return;
        }
        try {
            int ToInt = this.m_leastVerboseLevel.ToInt();
            ArrayList<LogEntry> arrayList = this.m_logs;
            nativeLogBatch(ToInt, (LogEntry[]) arrayList.toArray(new LogEntry[arrayList.size()]));
            this.m_logs.clear();
            this.m_leastVerboseLevel = LogLevel.Verbose;
        } catch (Exception e2) {
            String str = "Failed to flush logs: " + e2.toString();
        } catch (UnsatisfiedLinkError e3) {
            String str2 = "Failed to flush logs: " + e3.toString();
        }
    }

    public void Important(String str) {
        LogLevel logLevel = LogLevel.Important;
        String.format("[%c][%s] %s", Character.valueOf(logLevel.ToChar()), this.m_subArea, str);
        Log(logLevel, str);
    }

    public void Information(String str) {
        String.format("[%s] %s", this.m_subArea, str);
        Log(LogLevel.Information, str);
    }

    public synchronized void Log(LogLevel logLevel, String str) {
        this.m_logs.add(new LogEntry(logLevel, String.format("[%c][%s][%s] %s", Character.valueOf(logLevel.ToChar()), Timestamp(), this.m_subArea, str)));
        if (this.m_leastVerboseLevel.ToInt() > logLevel.ToInt()) {
            this.m_leastVerboseLevel = logLevel;
        }
    }

    public void Verbose(String str) {
        String.format("[%s] %s", this.m_subArea, str);
        Log(LogLevel.Verbose, str);
    }

    public void Warning(String str) {
        String.format("[%s] %s", this.m_subArea, str);
        Log(LogLevel.Warning, str);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        Flush();
    }
}