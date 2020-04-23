package com.netease.sql.translator;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

/*****
 Created by wennaisong on 2018/6/20 13:58
 *****/
public class SqlSchemaTest {
    public static void main(String args[]) {
        String dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(SqlClauseConstants.sqls[0], dbType);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        for (SQLStatement stmt : stmtList) {
            SQLSelectQueryBlock queryBlock = ((SQLSelectStatement) stmt).getSelect().getQueryBlock();
            System.out.println(queryBlock.findTableSource("a"));
            stmt.accept(statVisitor);
        }
        System.out.println(statVisitor.getColumns());
        System.out.println(statVisitor.getTables());
        System.out.println(statVisitor.getConditions());

        SchemaRepository repository = new SchemaRepository(dbType);
        String result = repository.resolve(SqlClauseConstants.sqls[7]);

        System.out.println(result);

        repository = new SchemaRepository(dbType);
        repository.console(SqlClauseConstants.sqls[7]);
        String console =repository.console("show columns from a");
        System.out.println(console);
    }
}
