package com.xiong.Spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.servlet.ServletContext;

public class SpringStart implements Runnable {

    public void testSpring() {
        System.out.println("testSpring");

    }


    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationfile.xml");
        System.out.println("context启动成功");
        SpringStart springStart = context.getBean(SpringStart.class);
        springStart.testSpring();
    }

    @Override
    public void run() {

    }
}
