package com.leo.chat.enums;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-27 14:25
 */
public enum OperateFriendRequestTypeEnum {

    /**
     *
     */
    IGNORE(0, "忽略"),
    PASS(1, "通过");

    public final Integer code;
    public final String msg;

    OperateFriendRequestTypeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
