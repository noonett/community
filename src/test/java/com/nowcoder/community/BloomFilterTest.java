package com.nowcoder.community;

import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.BloomFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class BloomFilterTest {

    @Autowired
    private BloomFilter bloomFilter;

    @Test
    public void BloomFilterTest(){
        for (int i = 10000000; i < 20000000; i++) {
            bloomFilter.addKey("" + i);
        }
        for (int i = 10000000; i < 300000000; i++) {
            if(i < 20000000 && (!bloomFilter.existsKey(""+i))){
                System.out.println(i);
            }
            if(i > 20000000 && bloomFilter.existsKey(""+i)){
                System.out.println(i);
            }
        }
    }
}
