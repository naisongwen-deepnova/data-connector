package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.Column;
import com.netease.spring.handler.xml.DataSource;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
/****
 *Author:wennaisong 2018/05/23
 *Parser for DataSource
 */
public class DataSourceDefinitionParser extends BaseDefinitionParser {
    protected Class getBeanClass(Element element) {
        return DataSource.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
        super.doParse(element, bean);
        NodeList nodeList = element.getChildNodes();
        ColumnDefinitionParser columnDefinitionParser = new ColumnDefinitionParser();
        List<Column> columnList = new ArrayList<Column>();
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            try {
                if ("column".equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = columnDefinitionParser.parseBean((Element)node, parserContext);
                    Column columnInstance= (Column) instantiateBean(beanDefinition);
                    columnList.add(columnInstance);
                }
            } catch (Throwable t) {
                throw new RuntimeException("instantiate column failed:" + t.getMessage());
            }
        }
        bean.addPropertyValue("columnList", columnList);
    }
}
