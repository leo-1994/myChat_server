package com.leo.chat.pojo.vo;

import com.leo.chat.pojo.entity.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-27 16:51
 */
@NoArgsConstructor
@Data
public class FriendRequestVO {

    private String sendUserId;
    private String sendUsername;
    private String sendFaceImage;
    private String sendNickname;

    public FriendRequestVO(Users sendUser) {
        sendUserId = sendUser.getId();
        sendUsername = sendUser.getUsername();
        sendFaceImage = sendUser.getFaceImage();
        sendNickname = sendUser.getNickname();
    }
}
