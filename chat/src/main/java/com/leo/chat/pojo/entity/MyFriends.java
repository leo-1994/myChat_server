package com.leo.chat.pojo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:47
 */
@Entity
@Data
public class MyFriends {

    @Id
    private String id;

    private String myUserId;

    private String myFriendUserId;
}
