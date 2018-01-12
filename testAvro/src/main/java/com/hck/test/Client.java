package com.hck.test;



import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import static com.hck.test.Server.start;

/**
 * Created by Administrator on 2018/1/10.
 */
public class Client implements Runnable{


    public static void main(String[] args) {
        new Client().run();
    }

    @Override
    public void run() {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup worker = new NioEventLoopGroup();
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new ClientHandle());
                channel.pipeline().addLast();
            }
        });

        try {
            ChannelFuture f = bootstrap.connect("127.0.0.1",9999).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
