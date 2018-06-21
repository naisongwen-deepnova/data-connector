package com.netease.spring.handler;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ProcessTest {
    public static void main(String args[]){
        String xml = "classpath:bricks-demo.xml";
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { xml });
        com.netease.spring.handler.xml.Process process= (com.netease.spring.handler.xml.Process) context.getBean("readStat");
        System.out.println(process.toString());
    }
}
