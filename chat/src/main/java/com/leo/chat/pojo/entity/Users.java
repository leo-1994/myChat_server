package com.leo.chat.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:48
 */
@Entity
@Data
public class Users {

    @Id
    private String id;
    private String username;
    private String password;
    private String faceImage;
    private String faceImageBig;
    private String nickname;
    private String qrcode;
    private String cid;
}
