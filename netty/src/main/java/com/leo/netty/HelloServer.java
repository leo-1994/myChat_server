package com.leo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-17 16:33
 */
public class HelloServer {

    public static void main(String[] args) throws Exception {
        // 定义一对线程
        // 主线程组，用于接受客户端的连接，但是不做任何处理
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 从线程组，主线程组把任务丢给他，让手下线程组做任务
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // netty服务器的创建，ServerBootstrap是一个启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    // 设置主从线程组
                    .group(bossGroup, workerGroup)
                    // 设置nio的双向通道
                    .channel(NioServerSocketChannel.class)
                    // 子处理器，用于处理workerGroup
                    .childHandler(new HelloServerInitializer());

            // 启动server，并且设置8088为启动端口好，启动方式为同步
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            // 监听关闭的channel，设置为同步方式
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
