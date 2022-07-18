package com.microsoft.xalwrapper.models;

import android.util.SparseArray;

public enum XalPrivilegeCheckDenyReason {
    NONE(0),
    PURCHASE_REQUIRED(1),
    RESTRICTED(2),
    BANNED(3),
    UNKNOWN(-1);
    
    private static final SparseArray<XalPrivilegeCheckDenyReason> map = new SparseArray<>();
    private final int value;

    static {
        for (XalPrivilegeCheckDenyReason xalPrivilegeCheckDenyReason : values()) {
            map.put(xalPrivilegeCheckDenyReason.value, xalPrivilegeCheckDenyReason);
        }
    }

    XalPrivilegeCheckDenyReason(int i2) {
        this.value = i2;
    }

    public int getValue() {
        return this.value;
    }

    public static XalPrivilegeCheckDenyReason valueOf(int i2) {
        if (i2 >= 0 && i2 <= 3) {
            return map.get(i2);
        }
        return UNKNOWN;
    }
}