package com.leo.chat.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:46
 */
@Data
@Entity
public class FriendsRequest {
    @Id
    private String id;

    private String sendUserId;

    private String acceptUserId;

    private Date requestDateTime;

}
