package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.DataGroup;
import org.w3c.dom.Element;
/****
 *Author:wennaisong 2018/05/23
 *Parser for DataGroup
 */
public class DataGroupDefinitionParser extends DataSourceDefinitionParser {
    protected Class getBeanClass(Element element) {
        return DataGroup.class;
    }
}
