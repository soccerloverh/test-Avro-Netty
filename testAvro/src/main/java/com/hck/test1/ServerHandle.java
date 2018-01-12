package com.hck.test1;

import com.hck.test.User;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/1/11.
 */
public class ServerHandle extends ChannelInboundHandlerAdapter{
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("新客户端连接....");
        byte[] userBytes = getUserBinaryArray();
//        byte[] schemaBytes = getSchemaBinaryArray();
        send(userBytes,ctx);
    }

    private byte[] getSchemaBinaryArray(){
        InputStream in = null;
        byte[] schemaBytes = null;
        try {
            in = this.getClass().getClassLoader().getResourceAsStream("user.avsc");
            schemaBytes = new byte[in.available()];
            in.read(schemaBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return schemaBytes;
    }

    private byte[] getUserBinaryArray() {
        User user = User.newBuilder().setName("hck").setAge(18).setFavorite("Soccer").build();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out,null);

        DatumWriter<User> writer = new SpecificDatumWriter<User>(User.getClassSchema());

        try {
            writer.write(user,encoder);
            encoder.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] userBytes = out.toByteArray();
        return userBytes;
    }

    private void send(byte[] bytes,ChannelHandlerContext ctx) {
        ByteBuf buf = Unpooled.buffer(bytes.length);
        buf.writeBytes(bytes);
        System.out.println("发送"+bytes.length+"字节数据");
        ctx.writeAndFlush(buf);
        ctx.flush();
        ctx.close();
    }

}
