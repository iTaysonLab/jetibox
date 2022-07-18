package com.microsoft.xalwrapper.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class LogEvent {
    public static LogEvent with(String areaName, String message, int traceLevel, long threadId, long timeStamp) {
        return new AutoValue_LogEvent(areaName, message, traceLevel, threadId, timeStamp);
    }

    public abstract String areaName();

    public abstract String message();

    public abstract int traceLevel();

    public abstract long threadId();

    public abstract long timeStamp();
}