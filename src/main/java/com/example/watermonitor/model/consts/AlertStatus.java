package com.example.watermonitor.model.consts;

import lombok.Getter;

@Getter
public enum AlertStatus {
    UNHANDLED(0, "未处理"),
    HANDLED(1, "已处理");

    private final int code;
    private final String desc;

    AlertStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AlertStatus of(int code) {
        for (AlertStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
