package com.example.watermonitor.model.consts;

import lombok.Getter;

@Getter
public enum AlertType {
    WARNING(1, "警戒"),
    DANGER(2, "危险");

    private final int code;
    private final String desc;

    AlertType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static AlertType of(int code) {
        for (AlertType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
