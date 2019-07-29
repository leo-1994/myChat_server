package com.leo.chat.repository;

import com.leo.chat.pojo.entity.MyFriends;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-27 14:38
 */
public interface MyFriendsRepository extends JpaRepository<MyFriends, String> {
    /**
     * 判断好友关系是否存在
     *
     * @param myUserId
     * @param myFriendUserId
     * @return
     */
    boolean existsByMyUserIdAndMyFriendUserId(String myUserId, String myFriendUserId);

    /**
     * 根据userId获取全部好友
     *
     * @param myUserId
     * @return
     */
    List<MyFriends> findAllByMyUserId(String myUserId);
}
