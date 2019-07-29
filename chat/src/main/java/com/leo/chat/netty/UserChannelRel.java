package com.leo.chat.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户id和channel的管理关系处理
 *
 * @author chao.li@quvideo.com
 * @date 2019-07-29 15:33
 */
public class UserChannelRel {
    private static Map<String, Channel> manager = new HashMap<>();

    public static void put(String userId, Channel channel) {
        manager.put(userId, channel);
    }

    public static Channel get(String userId) {
        return manager.get(userId);
    }
}
