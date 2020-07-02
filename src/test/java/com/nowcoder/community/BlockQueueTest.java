package com.nowcoder.community;

import org.apache.tomcat.jni.Time;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueueTest {

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Watcher(queue)).start();
    }
}

class Producer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println("生产：" + i);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {
    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; ++i) {
                Thread.sleep(new Random().nextInt(1000));
                System.out.println(Thread.currentThread() + "-消费：" + queue.take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Watcher implements Runnable {
    private BlockingQueue<Integer> queue;

    public Watcher(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            while (queue.size() != 0) {
                Thread.sleep(100);
                System.out.println("库存：" + queue.size());
            }
        } catch (InterruptedException e) {
            e.getStackTrace();
        }
    }
}

