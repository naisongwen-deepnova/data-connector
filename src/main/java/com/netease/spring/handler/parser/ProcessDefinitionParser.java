package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.*;
import com.netease.spring.handler.xml.Process;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
/****
 * Author:wennaisong 2018/05/20
 *
 */
public class ProcessDefinitionParser extends BaseDefinitionParser {
    protected Class getBeanClass(Element element) {
        return Process.class;
    }

    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder bean) {
        super.doParse(element, bean);
        NodeList nodeList = element.getChildNodes();

        Map<String, List> dataSourceMap = new HashMap<String, List>();
        Map<String, List> dataMapMap = new HashMap<>();
        Map<String, List> dataCollectMap = new HashMap<String, List>();
        Map<String, List> dataGroupMap = new HashMap<String, List>();
        Map<String, List> dataJoinMap = new HashMap<String, List>();

        Properties properties=new Properties();
        NamedNodeMap namedNodeMap = element.getAttributes();
        for (int index = 0; index < namedNodeMap.getLength(); index++) {
            Node node = namedNodeMap.item(index);
            String name = node.getNodeName();
            String value = node.getNodeValue();
            properties.setProperty(name, value);
        }

        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            DataSourceDefinitionParser dataSourceDefinitionParser = new DataSourceDefinitionParser();
            DataMapDefinitionParser dataMapDefinitionParser = new DataMapDefinitionParser();
            DataGroupDefinitionParser dataGroupDefinitionParser = new DataGroupDefinitionParser();
            DataCollectDefinitionParser dataCollectDefinitionParser = new DataCollectDefinitionParser();
            DataJoinDefinitionParser dataJoinDefinitionParser = new DataJoinDefinitionParser();
            try {
                if (DataSource.tagName.equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = dataSourceDefinitionParser.parseBean((Element) node, parserContext);
                    DataSource dataSourceInstance = (DataSource) instantiateBean(beanDefinition);
                    if (!dataSourceMap.containsKey(DataSource.tagName)) {
                        dataSourceMap.put(DataSource.tagName, new ArrayList());
                    }
                    dataSourceMap.get(DataSource.tagName).add(dataSourceInstance);
                }
                if (DataMap.tagName.equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = dataMapDefinitionParser.parseBean((Element) node, parserContext);
                    DataMap dataMapInstance = (DataMap) instantiateBean(beanDefinition);
                    if (!dataMapMap.containsKey(DataMap.tagName)) {
                        dataMapMap.put(DataMap.tagName, new ArrayList());
                    }
                    dataMapMap.get(DataMap.tagName).add(dataMapInstance);
                }
                if (DataGroup.tagName.equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = dataGroupDefinitionParser.parseBean((Element) node, parserContext);
                    DataGroup dataGroupInstance = (DataGroup) instantiateBean(beanDefinition);
                    if (!dataGroupMap.containsKey(DataGroup.tagName)) {
                        dataGroupMap.put(DataGroup.tagName, new ArrayList());
                    }
                    dataGroupMap.get(DataGroup.tagName).add(dataGroupInstance);
                }
                if (DataCollect.tagName.equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = dataCollectDefinitionParser.parseBean((Element) node, parserContext);
                    DataCollect dataCollectInstance = (DataCollect) instantiateBean(beanDefinition);
                    if (!dataCollectMap.containsKey(DataCollect.tagName)) {
                        dataCollectMap.put(DataCollect.tagName, new ArrayList());
                    }
                    dataCollectMap.get(DataCollect.tagName).add(dataCollectInstance);
                }
                if (DataJoin.tagName.equals(node.getLocalName())) {
                    AbstractBeanDefinition beanDefinition = dataJoinDefinitionParser.parseBean((Element) node, parserContext);
                    DataJoin dataJoinInstance = (DataJoin) instantiateBean(beanDefinition);
                    if (!dataJoinMap.containsKey(DataJoin.tagName)) {
                        dataJoinMap.put(DataJoin.tagName, new ArrayList());
                    }
                    dataJoinMap.get(DataJoin.tagName).add(dataJoinInstance);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("instantiate column failed:" + t);
            }
        }
        bean.addPropertyValue("properties",properties);
        bean.addPropertyValue("dataSourceList", dataSourceMap.get(DataSource.tagName));
        bean.addPropertyValue("dataMapList", dataMapMap.get(DataMap.tagName));
        bean.addPropertyValue("dataCollectList", dataCollectMap.get(DataCollect.tagName));
        bean.addPropertyValue("dataGroupList", dataGroupMap.get(DataGroup.tagName));
        bean.addPropertyValue("dataJoinList", dataJoinMap.get(DataJoin.tagName));
    }
}
