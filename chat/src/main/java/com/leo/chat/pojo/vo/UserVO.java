package com.leo.chat.pojo.vo;

import lombok.Data;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-22 14:24
 */
@Data
public class UserVO {

    private String id;
    private String username;
    private String faceImage;
    private String faceImageBig;
    private String nickname;
    private String qrcode;
}
