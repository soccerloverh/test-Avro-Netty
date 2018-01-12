package com.hck.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/1/10.
 */
public class ClientHandle extends ChannelInboundHandlerAdapter{
    Schema schema = null;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接成功,准备请求Schema");
        ByteBuf buffer = Unpooled.buffer(125);
        buffer.writeBytes("schema".getBytes());
        ctx.writeAndFlush(buffer);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        if (0 == bytes[0]){//如果发送的是Schema
            deSerializeSchema(bytes);
            System.out.println("准备请求对象...");
            byte[] req = "object".getBytes();
            ByteBuf reqBuf = Unpooled.buffer(req.length);
            reqBuf.writeBytes(req);
            System.out.println("发送请求:"+new String(req));
            ctx.writeAndFlush(reqBuf);
        }else if(1 == bytes[0]){
            deSerializeObject(bytes);
        }
    }

    private void deSerializeSchema(byte[] bytes) {
        System.out.println("反序列化Schema开始...");
        System.out.println("接受到的Buf中byte数组长度为:"+bytes.length);
        ByteArrayInputStream in = new ByteArrayInputStream(bytes,1,bytes.length);
        try {
            schema = new Schema.Parser().parse(in);
            System.out.println("反序列化的Schema:"+schema);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Schema反序列化失败!!!");
        }
    }


    private void deSerializeObject(byte[] bytes){
        System.out.println("反序列化对象开始,字节数组长度："+bytes.length);

       DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);

        BinaryDecoder decoder = DecoderFactory.get().createBinaryDecoder(bytes,1,bytes.length-1,null);

        GenericRecord object = new GenericData.Record(schema);

        try {
            object = reader.read(object,decoder);
        } catch (IOException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }

        System.out.println(object);
    }
}
