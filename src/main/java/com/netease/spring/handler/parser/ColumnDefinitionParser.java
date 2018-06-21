package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.Column;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.w3c.dom.Element;
/****
 *Author:wennaisong 2018/05/18
 */
public class ColumnDefinitionParser extends BaseDefinitionParser {
    protected Class getBeanClass(Element element) {
        return Column.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        super.doParse(element, bean);
    }
}
