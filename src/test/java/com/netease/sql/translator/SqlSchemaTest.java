package com.netease.sql.translator;

import com.alibaba.druid.sql.repository.SchemaRepository;

/*****
 Created by wennaisong on 2018/6/20 13:58
 *****/
public class SqlSchemaTest {
    public static void main(String args[]) {
        SchemaRepository repository = new SchemaRepository();
        String result=repository.resolve(SqlClauseConstants.sql);
        System.out.println(result);
    }
}
