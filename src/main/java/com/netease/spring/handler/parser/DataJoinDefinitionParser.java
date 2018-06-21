package com.netease.spring.handler.parser;

import com.netease.spring.handler.xml.DataJoin;
import org.w3c.dom.Element;
/****
 *Author:wennaisong 2018/05/23
 *Parser for DataJoin
 */
public class DataJoinDefinitionParser extends DataSourceDefinitionParser {
    protected Class getBeanClass(Element element) {
        return DataJoin.class;
    }
}
