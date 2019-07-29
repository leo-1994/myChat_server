package com.leo.chat.enums;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:49
 */
public enum MsgSignFlagEnum {
    /**
     *
     */
    UNSIGN(0, "未签收"),
    SIGNED(1, "已签收");
    public final Integer type;
    public final String content;

    MsgSignFlagEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
