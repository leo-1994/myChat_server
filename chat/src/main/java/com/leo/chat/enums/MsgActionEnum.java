package com.leo.chat.enums;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:21
 */
public enum MsgActionEnum {
    /**
     *
     */
    CONNECT(1, "初始化连接"),
    CHAT(2, "聊天消息"),
    SIGNED(3, "消息签收"),
    KEEPALIVE(4, "客户端保持心跳");
    public final Integer type;
    public final String content;

    MsgActionEnum(Integer type, String content) {
        this.type = type;
        this.content = content;
    }
}
