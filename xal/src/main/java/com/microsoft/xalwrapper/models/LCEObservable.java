package com.microsoft.xalwrapper.models;

import com.google.auto.value.AutoValue;

import java.util.ArrayList;
import java.util.List;

public class LCEObservable<T> {
    private T content;
    private ErrorState errorState;
    private LCEState currentState = LCEState.NOT_REQUESTED;
    private final List<Runnable> stateChangeListeners = new ArrayList<>();

    @AutoValue
    public static abstract class ErrorState {
        public static ErrorState with(int errorCode, String errorMessage) {
            return new AutoValue_LCEObservable_ErrorState(errorCode, errorMessage);
        }

        public abstract int errorCode();

        public abstract String errorMessage();
    }

    public enum LCEState {
        NOT_REQUESTED,
        LOADING,
        CONTENT,
        ERROR
    }

    private void callbackRegisteredListeners() {
        for (Runnable runnable : this.stateChangeListeners) {
            runnable.run();
        }
    }

    private void changeState(LCEState lCEState) {
        this.currentState = lCEState;
        callbackRegisteredListeners();
    }

    public T getContent() {
        return this.content;
    }

    public ErrorState getErrorState() {
        return this.errorState;
    }

    public boolean isContent() {
        return this.currentState == LCEState.CONTENT;
    }

    public boolean isError() {
        return this.currentState == LCEState.ERROR;
    }

    public boolean isLoading() {
        return this.currentState == LCEState.LOADING;
    }

    public boolean isNotRequested() {
        return this.currentState == LCEState.NOT_REQUESTED;
    }

    public void registerListener(Runnable runnable) {
        this.stateChangeListeners.add(runnable);
        if (this.currentState != LCEState.NOT_REQUESTED) {
            runnable.run();
        }
    }

    public void setContentState(T t) {
        this.content = t;
        changeState(LCEState.CONTENT);
    }

    public void setErrorState(int i2, String str) {
        this.errorState = ErrorState.with(i2, str);
        changeState(LCEState.ERROR);
    }

    public void setLoadingState() {
        changeState(LCEState.LOADING);
    }

    public void setNotRequestedState() {
        changeState(LCEState.NOT_REQUESTED);
        this.content = null;
        this.errorState = null;
    }
}