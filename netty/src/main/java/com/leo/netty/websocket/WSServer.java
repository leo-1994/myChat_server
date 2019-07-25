package com.leo.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-17 17:44
 */
public class WSServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup masterGroup = new NioEventLoopGroup();
        EventLoopGroup salverGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(masterGroup, salverGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WSServerInitializer() );

            ChannelFuture future = serverBootstrap.bind(8088).sync();

            future.channel().closeFuture().sync();
        } finally {
            masterGroup.shutdownGracefully();
            salverGroup.shutdownGracefully();
        }

    }
}
