package com.hck.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.*;

/**
 * Created by Administrator on 2018/1/10.
 */
public class ServerHandle extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        String request = new String(bytes);
        System.out.println("客户端发来请求:"+request);
        if("schema".equals(request)){
            sendSchema(ctx);

        }else if("object".equals(request)){
            System.out.println("发送object：");
            sendUser(ctx);
            ctx.close();
        }
    }

    private void sendSchema(ChannelHandlerContext ctx){
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("user.avsc");
            System.out.println("InputStream 的可读长度availble:"+in.available());

            byte[] bytes = new byte[in.available()+1];//多一位来用作标识schema和对象

            System.out.println("bytes缓冲 的可写长度bytes.length:"+bytes.length);

            bytes[0] = (byte)0; //0表示是Schema

            in.read(bytes,1,bytes.length-1);//从1开始写入Schema

            ByteBuf buf = Unpooled.buffer(bytes.length);

            buf.writeBytes(bytes);

            ctx.writeAndFlush(buf);
        } catch (FileNotFoundException e) {
            System.err.println("找不到User.avsc文件");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUser(ChannelHandlerContext ctx) {
        User user = new User();
        user.setName("hck");
        user.setAge(18);
        user.setFavorite("Football");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out,null);
        DatumWriter<User> writer = new SpecificDatumWriter<User>(User.getClassSchema());
        try {
            writer.write(user,encoder);
            encoder.flush();
            byte[] bytes1 = out.toByteArray();
            byte[] bytes = new byte[bytes1.length+1];
            bytes[0] = 1; //1表示是对象
            System.arraycopy(bytes1,0,bytes,1,bytes1.length);
            ByteBuf buf = Unpooled.buffer(bytes.length);
            buf.writeBytes(bytes);
            ctx.writeAndFlush(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
