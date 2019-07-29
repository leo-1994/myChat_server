package com.leo.chat.repository;

import com.leo.chat.pojo.entity.ChatMsg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:37
 */
public interface ChatMsgRepository extends JpaRepository<ChatMsg, String> {
    /**
     * 根据接收人和签收状态取得消息
     *
     * @param acceptUserId
     * @param signFlag
     * @return
     */
    List<ChatMsg> findAllByAcceptUserIdAndSignFlag(String acceptUserId, int signFlag);
}
