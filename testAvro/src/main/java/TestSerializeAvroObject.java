import com.hck.test.User;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/1/10.
 */
public class TestSerializeAvroObject {
    public static void main(String[] args) throws IOException {
        deSerializeByteArray();
    }

    public static void deSerialize() throws IOException {
        DatumReader<User> reader = new SpecificDatumReader<User>(User.class);
        DataFileReader<User> fileReader = new DataFileReader<User>(new File("user.avro"),reader);
        User user = null;
        while(fileReader.hasNext()){
            user = fileReader.next(user);
            System.out.println(user);
        }
    }
    @Test
    public static void deSerializeByteArray(){
        User user = new User();
        user.setName("hck");
        user.setAge(18);
        user.setFavorite("Football");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(out,null);
        DatumWriter<User> writer = new SpecificDatumWriter<User>(User.getClassSchema());
        byte[] bytes = null;
        try {
            writer.write(user,encoder);
            encoder.flush();
            bytes = out.toByteArray();


        } catch (IOException e) {
            e.printStackTrace();
        }

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(User.getClassSchema());

        BinaryDecoder decoder = DecoderFactory.get().createBinaryDecoder(bytes,null);

        GenericRecord object = new GenericData.Record(User.getClassSchema());

        try {
            object = reader.read(object,decoder);
        } catch (IOException e) {
            System.out.println("读取失败");
            e.printStackTrace();
        }

        System.out.println(object);
    }

    public static void serialize(){
        //方式1
        User user1 = new User();
        user1.setName("hck");
        user1.setFavorite("Soccer");
        user1.setAge(18);
        System.out.println( user1 );

        //方式二
        User user2 = new User("lkx",19,"Sleep");
        System.out.println(user2);

        //方式三
        User user3 = User.newBuilder().setAge(1).setName("hey").setFavorite("Football").build();
        System.out.println(user3);

        DatumWriter<User> userDatumWriter = new SpecificDatumWriter<User>(User.class);
        DataFileWriter<User> dataFileWriter = new DataFileWriter<User>(userDatumWriter);

        try {
            dataFileWriter.create(User.getClassSchema(),new File("user.avro"));
            dataFileWriter.append(user1);
            dataFileWriter.append(user2);
            dataFileWriter.append(user3);
            dataFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
