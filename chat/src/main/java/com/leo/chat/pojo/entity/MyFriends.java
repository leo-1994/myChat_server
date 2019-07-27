package com.leo.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 09:47
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class MyFriends {

    @Id
    private String id;

    private String myUserId;

    private String myFriendUserId;
}
