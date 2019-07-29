package com.leo.chat.pojo.vo;

import lombok.Data;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 17:25
 */
@Data
public class ChatMsgVO {

    private String acceptUserId;
    private String sendUserId;
    private String msg;
}
