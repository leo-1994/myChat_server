package com.leo.chat.service;

import com.leo.chat.enums.SearchFriendsStatusEnum;
import com.leo.chat.pojo.entity.ChatMsg;
import com.leo.chat.pojo.entity.Users;
import com.leo.chat.pojo.vo.FriendRequestVO;
import com.leo.chat.pojo.vo.MyFriendsVO;

import java.util.List;

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

    /**
     * 搜索朋友的前置
     *
     * @param myUserId
     * @param friendUsername
     * @return
     */
    SearchFriendsStatusEnum preconditionSearchFriends(String myUserId, String friendUsername);

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */
    Users selectUserByUsername(String username);

    /**
     * 保存添加好友请求
     *
     * @param myUserId
     * @param friendUsername
     * @return
     */
    void sendFriendsRequest(String myUserId, String friendUsername);

    /**
     * 获取好友请求列表
     *
     * @param userId
     * @return
     */
    List<FriendRequestVO> getFriendRequestList(String userId);

    /**
     * 通过好友请求
     *
     * @param acceptUserId
     * @param sendUserId
     */
    void passFriendRequest(String acceptUserId, String sendUserId);

    /**
     * 忽略好友请求
     *
     * @param acceptUserId
     * @param sendUserId
     */
    void ignoreFriendRequest(String acceptUserId, String sendUserId);

    /**
     * 获取我的好友
     *
     * @param userId
     * @return
     */
    List<MyFriendsVO> getMyFriends(String userId);

    /**
     * 保存聊天消息
     *
     * @param senderId
     * @param receiverId
     * @param msg
     * @return
     */
    String saveChatMsg(String senderId, String receiverId, String msg);

    /**
     * 签收聊天消息
     *
     * @param msgIdList
     */
    void signedChatMsg(List<String> msgIdList);

    List<ChatMsg> getUnReadMsgList(String userId);
}
