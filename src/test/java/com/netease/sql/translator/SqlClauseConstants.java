package com.netease.sql.translator;

import com.alibaba.druid.util.JdbcConstants;

/*****
 Created by wennaisong on 2018/6/11 15:39
 *****/
public class SqlClauseConstants {
    public static final String dbType = JdbcConstants.MYSQL; // JdbcConstants.MYSQL或者JdbcConstants.POSTGRESQL

    public static String[] sqls = {
            "SELECT a, b, c FROM table1 FOO inner join TABLE2 ON a = b",
            "select a.id,a.name from a where a.id=1 order by a.age",
            "select a.id as ID1,a.name as NAME1 from mytable a where a.id = 3 group by name order by id asc limit 10",
            "select a.id aId,b.name bName from A a,B b where a.id=b.id order by a.age",
            "select a.id as ID from (select id,name from mytable where id=2)tmp",
            "select a.id as ID1,a.name as NAME1 from mytable a union all select b.id as ID2,b.name as NAME2 from yourtable b",
            "select * from(select a.id as ID1,a.name as NAME1 from mytable a union all select b.id as ID2,b.name as NAME2 fromom yourtable b)total",
            "select a.id as ID,a.name as NAME from (select id from (select id from othertable where id=2)mytable where id=2)tmp where id=2",
            "select a.id,count(distinct c.id) from a join b on a.id=b.id and a.name=b.name join c on a.id=c.id group by a.id"
};


    public static String sqlFilePath = "sqlfile.txt";
}
