package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.DataCollect;
import org.w3c.dom.Element;
/****
 *Author:wennaisong 2018/05/23
 *Parser for DataCollect
 */
public class DataCollectDefinitionParser extends DataSourceDefinitionParser {
    protected Class getBeanClass(Element element) {
        return DataCollect.class;
    }
}
