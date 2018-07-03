package com.netease.sql.translator;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;

/*****
 Created by wennaisong on 2018/6/8 10:10
 *****/
public class SqlParserTest {
    public static void main(String args[]) {
        // 新建 MySQL Parser
        SQLStatementParser parser = new MySqlStatementParser(SqlClauseConstants.sqls[7]);

        List<SQLSelectItem> items = null;
        StringBuffer select = new StringBuffer();
        StringBuffer from = new StringBuffer();
        StringBuffer where = new StringBuffer();
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLUtils.toSQLString(stmtList, JdbcConstants.MYSQL);

        for (SQLStatement statement : stmtList) {
            if (statement instanceof SQLSelectStatement) {
                SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                SQLSelectQuery sqlSelectQuery = selectStatement.getSelect().getQuery();
                SQLASTOutputVisitor selectVisitor = SQLUtils.createFormatOutputVisitor(select,
                        stmtList, JdbcUtils.MYSQL);
                SQLASTOutputVisitor outVisitor = SQLUtils.createFormatOutputVisitor(from,
                        stmtList, JdbcUtils.MYSQL);
                SQLASTOutputVisitor whereVisitor = SQLUtils.createFormatOutputVisitor(
                        where, stmtList, JdbcUtils.MYSQL);
                ((SQLSelectStatement) statement).getSelect().accept(selectVisitor);

                if (sqlSelectQuery.getClass() == SQLUnionQuery.class) {
                    SQLUnionQuery sqlUnionQuery = (SQLUnionQuery) selectStatement.getSelect().getQuery();
                    System.out.println(sqlUnionQuery.getOperator());
                } else if (sqlSelectQuery.getClass() == MySqlSelectQueryBlock.class) {
                    MySqlSelectQueryBlock sqlSelectQueryBlock = (MySqlSelectQueryBlock) selectStatement.getSelect().getQueryBlock();
                    SQLTableSource tableSource = sqlSelectQueryBlock.getFrom();
                    sqlSelectQueryBlock.getFrom().accept(outVisitor);
                    sqlSelectQueryBlock.getWhere().accept(whereVisitor);
                    items = sqlSelectQueryBlock.getSelectList();
                }
            }
            for (SQLSelectItem s : items) {
                System.out.println(s.getAlias());
            }
            System.out.println("--------------------------------");

            System.out.println("from==" + from.toString());
            System.out.println("select==" + select);
            System.out.println("where==" + where);
        }
        System.exit(0);
    }
}
