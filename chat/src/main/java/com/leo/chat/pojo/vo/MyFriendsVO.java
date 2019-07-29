package com.leo.chat.pojo.vo;

import com.leo.chat.pojo.entity.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 10:16
 */
@NoArgsConstructor
@Data
public class MyFriendsVO {
    private String friendUserId;
    private String friendUsername;
    private String friendFaceImage;
    private String friendNickname;

    public MyFriendsVO(Users user) {
        this.friendUserId = user.getId();
        this.friendUsername = user.getUsername();
        this.friendFaceImage = user.getFaceImage();
        this.friendNickname = user.getNickname();
    }
}
