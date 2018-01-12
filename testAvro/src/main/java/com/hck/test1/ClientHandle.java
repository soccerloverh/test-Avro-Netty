package com.hck.test1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;

import java.io.ByteArrayInputStream;

/**
 * Created by Administrator on 2018/1/11.
 */
public class ClientHandle extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println("接受到 "+bytes.length+"字节数据");
        Schema schema = new Schema.Parser().parse(new ByteArrayInputStream(bytes));
        System.out.println("解析后Schema:"+schema);

        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes,null);
        DatumReader<GenericRecord> reader = new SpecificDatumReader<GenericRecord>(schema);
        GenericRecord record = new GenericData.Record(schema);

        reader.read(record,decoder);
        System.out.println(record);
    }
}
