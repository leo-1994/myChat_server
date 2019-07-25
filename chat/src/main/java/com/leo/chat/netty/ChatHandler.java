package com.leo.chat.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传来的消息
        String content = msg.text();
        System.out.println("接收到的数据：" + content);

//        for (Channel channel : clients) {
//            channel.writeAndFlush(new TextWebSocketFrame("[服务器接收到消息]:" + LocalDate.now()+" "+ LocalTime.now() + "，消息为:" + content));
//        }
        // 下面这个方法和上面的循环效果一样
        clients.writeAndFlush(new TextWebSocketFrame("[服务器接收到消息]:" + LocalDate.now() + " " + LocalTime.now() + "，消息为:" + content));
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
        clients.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//        cilents.remove(ctx.channel());
        System.out.println("客户端断开，channel长id为:" + ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel短id为:" + ctx.channel().id().asShortText());

    }
}
