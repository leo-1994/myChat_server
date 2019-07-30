package com.leo.chat.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author chao.li@quvideo.com
 * @date 2019-07-17 17:49
 */
public class WsServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // websocket基于http协议，所以要有http编解码器
        pipeline.addLast(new HttpServerCodec());
        // 对写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        // 对httpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        // 几乎在netty中的变成，都会使用此handler
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        // =========以上是用户支持http协议============
        // =========增加心跳支持=====================
        // 针对客户端，如果在1分钟时没有想服务端发送读写心跳，则主动断开
        // 如果是读空闲或者写空闲，不处理
        pipeline.addLast(new IdleStateHandler(8, 10, 12));
        // 自定义空闲状态检测
        pipeline.addLast(new HeartBeatHandler());

        // websocket服务器处理的协议，用于指定给客户端连接访问的路由
        // 此handler会帮你处理一些繁重复杂的事
        // 会帮你处理握手动作：handshaking（close，ping，pong） ping+pong=心跳
        // 对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        // 自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
