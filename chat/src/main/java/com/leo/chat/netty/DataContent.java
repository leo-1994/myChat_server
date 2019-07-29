package com.leo.chat.netty;

import lombok.Data;

import java.io.Serializable;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:14
 */
@Data
public class DataContent implements Serializable {
    private static final long serialVersionUID = 6010298191654948532L;
    /**
     * 动作类型
     */
    private Integer action;
    /**
     * 聊天内容
     */
    private ChatMsg chatMsg;
    /**
     * 扩展自动
     */
    private String extend;
}
