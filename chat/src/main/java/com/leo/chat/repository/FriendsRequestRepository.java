package com.leo.chat.repository;

import com.leo.chat.pojo.entity.FriendsRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-27 16:33
 */
public interface FriendsRequestRepository extends JpaRepository<FriendsRequest, String> {

    /**
     * 查询添加好友请求是否存在
     *
     * @param sendUserId
     * @param acceptUserId
     * @return
     */
    boolean existsBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);

    /**
     * 根据接受人获取好友请求列表
     *
     * @param acceptUserId
     * @return
     */
    List<FriendsRequest> findAllByAcceptUserId(String acceptUserId);

    /**
     * 根据发送者和接收者获取好友请求
     *
     * @param sendUserId
     * @param acceptUserId
     * @return
     */
    FriendsRequest findBySendUserIdAndAcceptUserId(String sendUserId, String acceptUserId);
}
