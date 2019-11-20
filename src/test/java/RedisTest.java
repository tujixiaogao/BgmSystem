import cn.bgm.index.util.RedisUtil;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RedisTest {

    @Autowired
    RedisUtil redisUtil;

    public static void main(String[] args) {


    }

    @Test
    public void test1(){
        redisUtil.set("name","gaozhulin");
        String name = (String) redisUtil.get("name");
        System.out.println("获取的参数是:"+name);
    }
}
