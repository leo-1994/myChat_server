package com.leo.chat.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:45
 */
@Entity
@Data
public class ChatMsg {

    @Id
    private String id;

    private String sendUserId;

    private String acceptUserId;

    private String msg;

    private Integer signFlag;

    private Date createTime;
}
