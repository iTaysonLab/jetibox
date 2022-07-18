package com.microsoft.xal.androidjava;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.microsoft.xal.logging.XalLogger;

public class PresenceManager implements DefaultLifecycleObserver {
    private static boolean isAttached = false;
    private final XalLogger m_logger = new XalLogger("PresenceManager");
    private boolean m_paused = false;

    static void attach() {
        if (!isAttached) {
            isAttached = true;
            new Handler(Looper.getMainLooper()).post(() -> ProcessLifecycleOwner.get().getLifecycle().addObserver(new PresenceManager()));
        }
    }

    private static native void pausePresence();

    private static native void resumePresence();

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        if (this.m_paused) {
            this.m_logger.Important("Ignoring pause, already paused");
            this.m_logger.Flush();
        } else {
            try {
                this.m_logger.Important("Pausing presence on app pause");
                this.m_logger.Flush();
                pausePresence();
                this.m_paused = true;
            } catch (UnsatisfiedLinkError e2) {
                this.m_logger.Error("Failed to pause presence: " + e2);
                this.m_logger.Flush();
            }
        }
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (this.m_paused) {
            try {
                this.m_logger.Important("Resuming presence on paused app resume");
                this.m_logger.Flush();
                resumePresence();
                this.m_paused = false;
            } catch (UnsatisfiedLinkError e2) {
                this.m_logger.Error("Failed to resume presence: " + e2);
                this.m_logger.Flush();
            }
        } else {
            this.m_logger.Important("Ignoring resume, not currently paused");
            this.m_logger.Flush();
        }
    }
}