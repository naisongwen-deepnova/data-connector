package com.netease.spring.handler;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.net.URL;

public class XMLValidateTest {
    public static void main(String args[]) throws SAXException {
        String xmlFileName = "bricks-demo.xml";
        String xsdFileName = "process.xsd" ;
        // 建立schema工厂
        SchemaFactory schemaFactory = SchemaFactory
                . newInstance("http://www.w3.org/2001/XMLSchema");
        // 建立验证文档文件对象，利用此文件对象所封装的文件进行schema验证
        URL schemaFile = XMLValidateTest. class.getClassLoader()
                .getResource(xsdFileName);
        // 利用schema工厂，接收验证文档文件对象生成Schema对象
        Schema schema = schemaFactory.newSchema(schemaFile);
        // 通过Schema产生针对于此Schema的验证器，利用schenaFile进行验证
        Validator validator = schema.newValidator();
        // 创建默认的XML错误处理器
        XMLErrorHandler errorHandler = new XMLErrorHandler();
        validator.setErrorHandler(errorHandler);
        // 得到验证的数据源
        InputStream xmlStream = XMLValidateTest. class.getClassLoader()
                .getResourceAsStream(xmlFileName);
        Source source = new StreamSource(xmlStream);
        // 开始验证，成功输出success!!!，失败输出fail
        try {
            validator.validate(source);
            XMLWriter writer = new XMLWriter(OutputFormat.createPrettyPrint());
            // 如果错误信息不为空，说明校验失败，打印错误信息
            if (errorHandler.getErrors().hasContent()) {
                System. out.println( "XML文件通过XSD文件校验失败！" );
                writer.write(errorHandler.getErrors());
            } else {
                System. out.println( "Good! XML文件通过XSD文件校验成功！" );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
