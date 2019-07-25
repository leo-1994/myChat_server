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
     */
    Users selectUser(String username, String pwd);

    Users saveUser(Users user);
}
