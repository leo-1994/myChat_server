package com.leo.chat.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:17
 */
@Data
class ChatMsg implements Serializable {
    private static final long serialVersionUID = -4144191721841313978L;
    private String senderId;
    private String receiverId;
    private String msg;
    /**
     * 消息id
     */
    private String msgId;
}
