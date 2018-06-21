package com.netease.sql.translator;

import com.alibaba.druid.util.JdbcConstants;

import java.io.File;
import java.io.IOException;

/*****
 Created by wennaisong on 2018/6/11 15:32
 *****/
public class SQLTranslatorTest {
    public static void main(String args[]) throws IOException {
        SQLTranslator translator=new SQLTranslator();
//        translator.translate(SqlClauseForTest.sql, JdbcConstants.MYSQL,"test.xml");
        translator.translate(new File(SqlClauseConstants.sqlFilePath), JdbcConstants.MYSQL,"test.xml");
        System.exit(0);
    }
}
