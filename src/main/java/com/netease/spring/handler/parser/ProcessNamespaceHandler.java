package com.netease.spring.handler.parser;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;
/****
 * Author:wennaisong 2018/05/20
 *
 */
public class ProcessNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("process", new ProcessDefinitionParser());
    }
}
