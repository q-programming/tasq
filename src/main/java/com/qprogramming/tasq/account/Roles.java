package com.qprogramming.tasq.account;

import com.qprogramming.tasq.support.Utils;

public enum Roles {
    ROLE_POWERUSER("role.poweruser"), ROLE_ADMIN("role.admin"), ROLE_USER("role.user"), ROLE_VIEWER("role.viewer");

    private String code;

    Roles(String code) {
        this.code = code;
    }

    /**
     * Checks if currently logged user have ROLE_REPORTER authority
     *
     * @return
     */
    public static boolean isUser() {
        return Utils.getCurrentAccount().getIsUser();
    }

    /**
     * Checks if currently logged user have ROLE_USER authority
     *
     * @return
     */
    public static boolean isPowerUser() {
        return Utils.getCurrentAccount().getIsPowerUser();
    }

    /**
     * Checks if currently logged user have ROLE_ADMIN authority
     *
     * @return
     */
    public static boolean isAdmin() {
        return Utils.getCurrentAccount().getIsAdmin();
    }

    public String getCode() {
        return code;
    }

}
