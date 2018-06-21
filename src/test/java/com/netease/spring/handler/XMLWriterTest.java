package com.netease.spring.handler;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;

/*****
 Created by wennaisong on 2018/6/12 9:59
 Refer :http://www.51gjie.com/java/741.html
 *****/
public class XMLWriterTest {
    public static void main(String[] args) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        XMLWriter writer = new XMLWriter(new FileWriter("output.xml"), format);

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("beans").addNamespace("","http://www.springframework.org/schema/beans").
                addNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance").
                addNamespace("yuedu","http://www.netease.com/schema/wenman").
                addAttribute("xsi:schemaLocation","http://www.springframework.org/schema/beans\n" +
                        "       http://www.springframework.org/schema/beans/spring-beans.xsd\n" +
                        "       http://www.netease.com/schema/wenman http://www.netease.com/schema/wenman/process.xsd");

        root.addElement("yuedu:process").addElement("yuedu:dataSource");
        writer.write(document);
        writer.close();
    }
}
