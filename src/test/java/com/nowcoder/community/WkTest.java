package com.nowcoder.community;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class WkTest {

    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75 https://www.nowcoder.com /Users/tt/Demo/data/community/wk-img/1.png";
        try {
            Runtime.getRuntime().exec(cmd);
            System.out.println("OK");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
