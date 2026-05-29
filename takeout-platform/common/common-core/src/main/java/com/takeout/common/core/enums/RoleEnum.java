package com.takeout.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleEnum {

    USER("USER", "普通用户"),
    MERCHANT("MERCHANT", "商家"),
    RIDER("RIDER", "骑手"),
    ADMIN("ADMIN", "管理员");

    private final String code;
    private final String desc;

    public static RoleEnum of(String code) {
        for (RoleEnum role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }

    public boolean isAtLeast(RoleEnum other) {
        return this.ordinal() >= other.ordinal();
    }
}
