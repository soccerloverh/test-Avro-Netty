import com.hck.test.User;

/**
 * Created by Administrator on 2018/1/10.
 */
public class TestImplementAvroObject {
    public static void main(String[] args) {

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
    }
}
