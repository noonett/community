package com.nowcoder.community.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.BitSet;

@Component
public class BloomFilter {

    @Value("${bloomfilter.bitsize}")
    private int bitsize;

    // bit总位数
    private int capacity;

    // 随机数数量
    @Value("${bloomfilter.seednum}")
    private int seednum;

    private int[] seeds;

    // 哈希函数
    private HashFunction[] functions;

    // BitSet
    private BitSet bitSet;

    @PostConstruct
    public void init() {
        this.capacity = 2 << bitsize;
        this.seeds = primeGenerator(seednum);
        bitSet = new BitSet(capacity);
        functions = new HashFunction[seednum];
        for (int i = 0; i < seednum; i++) {
            functions[i] = new HashFunction(capacity, seeds[i]);
        }
    }

    public void addKey(String key) {
        if (key == null) {
            return;
        }
        for (int i = 0; i < seednum; i++) {
            bitSet.set(functions[i].hash(key), true);
        }
    }

    public boolean existsKey(String key) {
        if (key == null) {
            return false;
        }
        for (int i = 0; i < seednum; i++) {
            if (!bitSet.get(functions[i].hash(key))) {
                return false;
            }
        }
        return true;
    }

    // 生成素数，根据随机数数量，生成对应数量的素数数组
    private int[] primeGenerator(int seednum) {
        if (seednum == 1) {
            return new int[]{2};
        }
        if (seednum == 2) {
            return new int[]{2, 3};
        }
        int[] prime = new int[seednum];
        int count = 0;
        prime[0] = 2;
        prime[1] = 3;
        for (int i = 5; i < Integer.MAX_VALUE; i++) {
            if (count == seednum) {
                break;
            }
            boolean isPrime = true;
            for (int j = 0; j < count; j++) {
                if (!isPrime || prime[j] > Math.sqrt(i)) {
                    break;
                }
                if (i % prime[j] == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                prime[count] = i;
                ++count;
            }
        }
        return prime;
    }

    public class HashFunction {

        private int capacity;

        private int seed;

        public HashFunction(int capacity, int seed) {
            this.capacity = capacity;
            this.seed = seed;
        }

        // 哈希函数
        public int hash(String key) {
            long result = 0;
            int len = key.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + key.charAt(i);
            }
            return (int) result % (capacity - 1);
        }

    }
}
