package com.hck.test1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by Administrator on 2018/1/11.
 */
public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT= 9999;
    public static void start() throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new ClientHandle());
                    }
                });
        ChannelFuture f = bootstrap.connect(HOST,PORT).sync();
        System.out.println("发起连接请求...");

    }

    public static void main(String[] args) throws InterruptedException {
        start();
    }
}
