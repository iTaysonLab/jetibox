package com.microsoft.xalwrapper.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class XalUser {
    public static XalUser withContent(long xuid, int ageGroup, String gamertag, String uniqueModernGamertag, String webAccountId) {
        return new AutoValue_XalUser(xuid, ageGroup, gamertag, uniqueModernGamertag, webAccountId);
    }

    public abstract long xuid();

    public abstract int ageGroup();

    public abstract String gamertag();

    public abstract String uniqueModernGamertag();

    public abstract String webAccountId();
}