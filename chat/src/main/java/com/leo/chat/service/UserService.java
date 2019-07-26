package com.leo.chat.service;

import com.leo.chat.pojo.entity.Users;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-19 10:56
 */
public interface UserService {

    /**
     * 判断用户名是否存在
     *
     * @param username
     * @return
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 查询用户
     *
     * @param username
     * @param pwd
     * @return
     */
    Users selectUser(String username, String pwd);

    /**
     * 保存用户
     *
     * @param user
     * @return
     */
    Users saveUser(Users user);

    /**
     * 保存头像
     *
     * @param userId
     * @param url
     * @return
     */
    Users saveUserFace(String userId, String url);

    /**
     * 设置昵称
     *
     * @param userId
     * @param nickname
     * @return
     */
    Users saveUserNickname(String userId, String nickname);
}
