package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.DataMap;
import org.w3c.dom.Element;

/****
 *Author:wennaisong 2018/05/30
 *Parser for DataMap
 */
public class DataMapDefinitionParser extends DataSourceDefinitionParser {
    protected Class getBeanClass(Element element) {
        return DataMap.class;
    }
}
