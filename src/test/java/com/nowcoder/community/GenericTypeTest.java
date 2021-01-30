package com.nowcoder.community;

import org.elasticsearch.common.inject.Singleton;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class GenericTypeTest {

    @Test
    public void genericTest() {
        Banana banana = new Banana("banana");
        List<Fluit> list = new ArrayList<>();
        list.add(banana);
        // genericType.read(list);
        ProxyTest proxyTest = new ProxyTest();
        Fluit fluit = (Fluit) proxyTest.newProxyInstance(banana);
        System.out.println((String) fluit.get());
        List<Integer> list1 = new ArrayList<>(5);
        for (int i = 0; i < 10; i++) {
            list1.add(i);
        }
        System.out.println(list1.size());
    }

    public class ProxyTest implements InvocationHandler{

        private Object targetClass;

        public Object newProxyInstance(Object targetClass){
            this.targetClass = targetClass;
            return Proxy.newProxyInstance(targetClass.getClass().getClassLoader(), targetClass.getClass().getInterfaces(), this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("代理一下！");
            Object ret = null;
            ret = method.invoke(targetClass, args);
            return ret;
        }
    }

    public class EnumSingleton {

        public Fluit getInstance() {
            return new Banana("");
        }
    }


    public class GenericType <T extends Banana>{

        T curFluit = null;

        public <E> void read(List<E> t){
            System.out.println(t.get(0));
        }

        public void write(T t){
            curFluit = t;
        }
    }

    public interface Fluit{

        public String get();

        public void set(String Fluit);

        public String toString();

    }

    class Apple implements Fluit{
        String apple = "";
        public Apple(String apple){
            this.apple = apple;
        }
        @Override
        public String get(){
            return this.apple;
        }

        @Override
        public void set(String apple){
            this.apple = apple;
        }
    }

     class Banana implements Fluit{
        String banana = "";
        public Banana(String banana){
            this.banana = banana;
        }
        @Override
        public String get(){
            return this.banana;
        }
        @Override
        public void set(String banana){
            this.banana = banana;
        }

         @Override
         public String toString() {
             return "Banana{" +
                     "banana='" + banana + '\'' +
                     '}';
         }
     }
}

