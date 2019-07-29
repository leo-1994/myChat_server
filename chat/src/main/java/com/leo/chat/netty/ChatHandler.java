package com.leo.chat.netty;

import com.alibaba.fastjson.JSON;
import com.leo.chat.enums.MsgActionEnum;
import com.leo.chat.service.UserService;
import com.leo.chat.service.impl.UserServiceImpl;
import com.leo.chat.utils.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理消息的handler
 * TextWebSocketFrame:在netty中，事专用于为websocket处理文本的对象，frame是消息的载体
 *
 * @author chao.li@quvideo.com
 * @date 2019-07-17 19:00
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 用于记录和管理所有客户端的channel
     */
    private static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static UserService userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        if (userService == null) {
            userService = (UserServiceImpl) SpringUtil.getBean("userServiceImpl");
        }
        // 1.获取客户端发来的消息
        String content = msg.text();
        Channel currentChannel = ctx.channel();
        DataContent dataContent = JSON.parseObject(content, DataContent.class);
        Integer action = dataContent.getAction();
        ChatMsg chatMsg = dataContent.getChatMsg();
        // 2.判断消息类型，根据不同的类型处理不同的业务
        if (MsgActionEnum.CONNECT.type.equals(action)) {
            //  2.1 当websocket 第一次open的时候，初始化channel，把用户的channel和userId关联起来
            String senderId = chatMsg.getSenderId();
            UserChannelRel.put(senderId, currentChannel);
        } else if (MsgActionEnum.CHAT.type.equals(action)) {
            //  2.2 聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            String msgText = chatMsg.getMsg();
            String receiverId = chatMsg.getReceiverId();
            String senderId = chatMsg.getSenderId();
            // 保存到数据库，并且标记为 为签收
            String msgId = userService.saveChatMsg(senderId, receiverId, msgText);
            // 发生消息给接收人
            chatMsg.setMsgId(msgId);
            // 获取接收发channel
            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                // TODO channel为空，代表用户离线，推送消息
            } else {
                // 当receiverChannel不为空，从ChannelGroup去查找对应的channel是否存在
                Channel findChannel = users.find(receiverChannel.id());
                if (findChannel != null) {
                    findChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContent)));
                } else {
                    // TODO channel为空，代表用户离线，推送消息
                }
            }
        } else if (MsgActionEnum.SIGNED.type.equals(action)) {
            //  2.3 签收消息，针对具体的消息类型进行签收，修改数据库中对应消息的签收状态[已签收]
            // 扩展字段在signed类型消息中，代表需要去签收的消息id，逗号间隔
            String msgIdStr = dataContent.getExtend();
            if (StringUtils.isBlank(msgIdStr)) {
                return;
            }
            String[] msgIds = msgIdStr.split(",");
            List<String> msgIdList = new ArrayList<>();
            for (String msgId : msgIds) {
                if (StringUtils.isNotBlank(msgId)) {
                    msgIdList.add(msgId);
                }
            }
            if (!CollectionUtils.isEmpty(msgIdList)) {
                userService.signedChatMsg(msgIdList);
            }
        } else if (MsgActionEnum.KEEPALIVE.type.equals(action)) {
            //  2.4 心跳类型的消息

        }
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去管理
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
        System.out.println("客户端断开，channel长id为:" + ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel短id为:" + ctx.channel().id().asShortText());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        // 发生异常后关闭连接，随后从channelGroup中移除
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
