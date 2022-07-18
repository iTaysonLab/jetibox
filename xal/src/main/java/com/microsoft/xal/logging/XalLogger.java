package com.microsoft.xal.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

public class XalLogger implements AutoCloseable {
    private static final SimpleDateFormat LogDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final String TAG = "XALJAVA";
    private final String m_subArea;
    private final ArrayList<LogEntry> m_logs = new ArrayList<>();
    private LogLevel m_leastVerboseLevel = LogLevel.Verbose;

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
        Log(LogLevel.Error, String.format("[%s] %s", this.m_subArea, str));
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
        Log(LogLevel.Important, String.format("[%c][%s] %s", LogLevel.Important.ToChar(), this.m_subArea, str));
    }

    public void Information(String str) {
        Log(LogLevel.Information, String.format("[%s] %s", this.m_subArea, str));
    }

    public synchronized void Log(LogLevel logLevel, String str) {
        this.m_logs.add(new LogEntry(logLevel, String.format("[%c][%s][%s] %s", logLevel.ToChar(), Timestamp(), this.m_subArea, str)));
        if (this.m_leastVerboseLevel.ToInt() > logLevel.ToInt()) {
            this.m_leastVerboseLevel = logLevel;
        }
    }

    public void Verbose(String str) {
        Log(LogLevel.Verbose, String.format("[%s] %s", this.m_subArea, str));
    }

    public void Warning(String str) {
        Log(LogLevel.Warning, String.format("[%s] %s", this.m_subArea, str));
    }

    @Override
    public void close() {
        Flush();
    }
}