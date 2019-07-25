package com.leo.chat.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-17 17:44
 */
@Component
public class WsServer {

    private static class SingletonWsServer {
        static final WsServer instance = new WsServer();
    }

    public static WsServer getInstance() {
        return SingletonWsServer.instance;
    }

    private ServerBootstrap serverBootstrap;

    public WsServer() {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup salverGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(masterGroup, salverGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new WsServerInitializer());
    }

    public void start() {
//        ChannelFuture future =
                serverBootstrap.bind(8088);
        System.out.println("netty websocket server 启动完毕...");
    }

}
