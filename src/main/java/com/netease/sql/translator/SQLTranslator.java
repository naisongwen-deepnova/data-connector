package com.netease.sql.translator;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.google.common.io.Files;
import com.netease.TranslateOption;
import com.netease.spring.handler.xml.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/*****
 Created by wennaisong on 2018/6/11 15:25
 *****/
public class SQLTranslator extends SQLASTVisitorAdapter {
    XMLWriter writer;
    Document document = DocumentHelper.createDocument();
    Element root = document.addElement("beans").addNamespace("", "http://www.springframework.org/schema/beans").
            addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance").
            addNamespace("yuedu", "http://www.netease.com/schema/wenman").
            addAttribute("xsi:schemaLocation", "http://www.springframework.org/schema/beans\n" +
                    "       http://www.springframework.org/schema/beans/spring-beans.xsd\n" +
                    "       http://www.netease.com/schema/wenman http://www.netease.com/schema/wenman/process.xsd");
    Element currElement = root.addElement("yuedu:process");

    public XMLWriter createXMLWriter(String xmlName) throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        writer = new XMLWriter(new FileWriter(xmlName), format);
        return writer;
    }

    public void writeDocument() throws IOException {
        this.writer.write(document);
        writer.close();
    }

    protected void translateQuery(SQLSelectQuery x) {
        Class<?> clazz = x.getClass();
        if (clazz == MySqlSelectQueryBlock.class) {
            visit((MySqlSelectQueryBlock) x);
        } else if (clazz == SQLSelectQueryBlock.class) {
            visit((SQLSelectQueryBlock) x);
        } else if (clazz == SQLUnionQuery.class) {
            visit((SQLUnionQuery) x);
        } else {
            x.accept(this);
        }
    }

    protected void translateTableSource(SQLTableSource x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLJoinTableSource.class) {
            visit((SQLJoinTableSource) x);
        } else if (clazz == SQLExprTableSource.class) {
            visit((SQLExprTableSource) x);
        } else if (clazz == SQLSubqueryTableSource.class) {
            visit((SQLSubqueryTableSource) x);
        } else {
            x.accept(this);
        }
    }

    protected final void translateExpr(SQLExpr x) {
        Class<?> clazz = x.getClass();
        if (clazz == SQLIdentifierExpr.class) {
            visit((SQLIdentifierExpr) x);
        } else if (clazz == SQLPropertyExpr.class) {
            visit((SQLPropertyExpr) x);
        } else if (clazz == SQLAllColumnExpr.class) {
            //print('*');
        } else if (clazz == SQLAggregateExpr.class) {
            visit((SQLAggregateExpr) x);
        } else if (clazz == SQLBinaryOpExpr.class) {
            visit((SQLBinaryOpExpr) x);
        } else if (clazz == SQLCharExpr.class) {
            visit((SQLCharExpr) x);
        } else if (clazz == SQLNullExpr.class) {
            visit((SQLNullExpr) x);
        } else if (clazz == SQLIntegerExpr.class) {
            visit((SQLIntegerExpr) x);
        } else if (clazz == SQLNumberExpr.class) {
            visit((SQLNumberExpr) x);
        } else if (clazz == SQLMethodInvokeExpr.class) {
            visit((SQLMethodInvokeExpr) x);
        } else if (clazz == SQLVariantRefExpr.class) {
            visit((SQLVariantRefExpr) x);
        } else if (clazz == SQLBinaryOpExprGroup.class) {
            visit((SQLBinaryOpExprGroup) x);
        } else if (clazz == SQLCaseExpr.class) {
            visit((SQLCaseExpr) x);
        } else if (clazz == SQLInListExpr.class) {
            visit((SQLInListExpr) x);
        } else if (clazz == SQLNotExpr.class) {
            visit((SQLNotExpr) x);
        } else {
            x.accept(this);
        }
    }

    protected void translateSelectList(List<SQLSelectItem> selectList) {
        for (int i = 0, lineItemCount = 0, size = selectList.size()
             ; i < size
                ; ++i, ++lineItemCount) {
            SQLSelectItem selectItem = selectList.get(i);
            SQLExpr selectItemExpr = selectItem.getExpr();

            if (selectItem.getClass() == SQLSelectItem.class) {
                this.visit(selectItem);
            } else {
                selectItem.accept(this);
            }
        }
    }

    public void translate(String sql, String dbType, String outputXml) throws IOException {
        createXMLWriter(outputXml);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement statement : stmtList) {
            if (statement instanceof SQLSelectStatement) {
                SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                visit(selectStatement);
            }
        }
        writeDocument();
    }


    public void translate(File sqlFile, String dbType, String outputXml) throws IOException {
        String sql = Files.toString(sqlFile, Charset.defaultCharset());
        createXMLWriter(outputXml);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        for (SQLStatement statement : stmtList) {
            if (statement instanceof SQLSelectStatement) {
                SQLSelectStatement selectStatement = (SQLSelectStatement) statement;
                visit(selectStatement);
            }
        }
        writeDocument();
    }

    @Override
    public boolean visit(SQLSelectStatement stmt) {
        SQLSelect select = stmt.getSelect();
        visit(select);
        return false;
    }

    public boolean visit(SQLSelect x) {
        SQLWithSubqueryClause withSubQuery = x.getWithSubQuery();
        if (withSubQuery != null) {
            withSubQuery.accept(this);
        }

        translateQuery(x.getQuery());

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(this);
        }
        String id = getIdFromAttr(x);
        addToParentAttrId(x, id);
        return false;
    }

    @Override
    public boolean visit(SQLSelectQueryBlock x) {
        Map<String, ArrayList<Column>> selectedItemListMap = new HashMap<>();
        for (SQLSelectItem selectItem : x.getSelectList()) {
            Column column = new Column();
            column.setAlias(selectItem.getAlias());
            SQLExpr selectItemExpr = selectItem.getExpr();
            if (selectItemExpr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identifierExpr = ((SQLIdentifierExpr) selectItemExpr);
                String owner = "UNKNOWN";
                ArrayList<Column> selectedItemList = selectedItemListMap.containsKey(owner) ? selectedItemListMap.get(owner) : new ArrayList<Column>();
                String item = identifierExpr.getSimpleName();
                column.setExpr(item);
                selectedItemList.add(column);
                selectedItemListMap.put(owner, selectedItemList);
            } else if (selectItemExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr propertyExpr = ((SQLPropertyExpr) selectItemExpr);
                String owner = propertyExpr.getOwnernName();
                if (StringUtils.isEmpty(owner)) {
                    owner = "UNKNOWN";
                }
                ArrayList<Column> selectedItemList = selectedItemListMap.containsKey(owner) ? selectedItemListMap.get(owner) : new ArrayList<Column>();
                String item = propertyExpr.getSimpleName();
                column.setExpr(item);
                selectedItemList.add(column);
                selectedItemListMap.put(owner, selectedItemList);
            }
        }
        ArrayList<Column> columnArrayList = new ArrayList<>();
        Iterator<ArrayList<Column>> iterable = selectedItemListMap.values().iterator();
        while (iterable.hasNext()) {
            columnArrayList.addAll(iterable.next());
        }
        x.putAttribute("parentSelectedItemList", selectedItemListMap);

        translateSelectList(x.getSelectList());

        SQLTableSource from = x.getFrom();
        if (from != null) {
            translateTableSource(from);
        }

        SQLExpr where = x.getWhere();
        if (where != null) {
            translateExpr(where);
        }

        SQLSelectGroupByClause groupBy = x.getGroupBy();
        if (groupBy != null) {
            visit(groupBy);
        }

        SQLOrderBy orderBy = x.getOrderBy();
        if (orderBy != null) {
            visit(orderBy);
        }

        SQLLimit limit = x.getLimit();
        if (limit != null) {
            visit(limit);
        }
        DataSource ds = (DataSource) x.getFrom().getAttribute("dataSource");
        ds.setColumnList(columnArrayList);
        toXMLElement(ds);
        if (groupBy != null) {
            DataGroup dataGroup = (DataGroup) x.getGroupBy().getAttribute("dataSource");
            dataGroup.setColumnList(columnArrayList);
            toXMLElement(dataGroup);
        }
        String id = getIdFromAttr(x);
        addToParentAttrId(x, id);
        return false;
    }

    protected String getIdFromAttr(SQLObject object) {
        if (object.getAttribute("id") != null)
            return (String) object.getAttribute("id");
        else if (object.getAttribute("childrenIdList") != null) {
            ArrayList<String> currIdlist = (ArrayList<String>) object.getAttribute("childrenIdList");
            if (currIdlist.size() > 0)
                return currIdlist.get(0);
        }
        return null;
    }

    protected void addToParentAttrId(SQLObject object, final String id) {
        ArrayList<String> currIdlist = new ArrayList() {{
            add(id);
        }};
        object.putAttribute("id", id);
        if (object.getParent().getAttribute("childrenIdList") != null) {
            currIdlist = (ArrayList<String>) object.getParent().getAttribute("childrenIdList");
            currIdlist.add(id);
        } else {
            object.getParent().putAttribute("childrenIdList", currIdlist);
        }
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        Map<String, ArrayList<Column>> parentSelectedItemList = (Map<String, ArrayList<Column>>) x.getParent().getAttribute("parentSelectedItemList");
        String id = x.getName().getSimpleName();
        addToParentAttrId(x, id);
//        Element e = currElement.addElement("yuedu:dataSource").addAttribute("id", id);
//        x.putAttribute("element", e);
        DataSource dataSource = new DataSource();
        dataSource.setId(id);
        dataSource.setColumnList(parentSelectedItemList.get(x.computeAlias()));
        x.putAttribute("dataSource", dataSource);
        return false;
    }

    protected Element toXMLElement(DataSource dataSource) {
        Element e = null;
        if (dataSource.getClass() == DataJoin.class) {
            DataJoin dataJoin = (DataJoin) dataSource;
            e = currElement.addElement("yuedu:" + DataJoin.tagName).addAttribute("id", dataJoin.getId());
            e.addAttribute("joinType", dataJoin.getJoinType());
            e.addAttribute("leftRef", dataJoin.getLeftRef());
            e.addAttribute("rightRef", dataJoin.getRightRef());
        } else if (dataSource.getClass() == DataCollect.class) {
            DataCollect dataCollect = (DataCollect) dataSource;
            e = currElement.addElement("yuedu:" + DataCollect.tagName).addAttribute("id", dataCollect.getId());
            e.addAttribute("collectType", dataCollect.getCollectType());
            e.addAttribute("leftRef", dataCollect.getLeftRef());
            e.addAttribute("rightRef", dataCollect.getRightRef());
        } else if (dataSource.getClass() == DataMap.class) {
            DataMap dataMap = (DataMap) dataSource;
            e = currElement.addElement("yuedu:" + DataMap.tagName).addAttribute("id", dataMap.getId());
            e.addAttribute("target", dataMap.getTarget());
        } else if (dataSource.getClass() == DataGroup.class) {
            DataGroup dataGroup = (DataGroup) dataSource;
            e = currElement.addElement("yuedu:" + DataGroup.tagName).addAttribute("id", dataGroup.getId());
            e.addAttribute("target", dataGroup.getTarget());
        } else if (dataSource.getClass() == DataSource.class) {
            e = currElement.addElement("yuedu:" + DataSource.tagName).addAttribute("id", dataSource.getId());
        }
        List<Column> columnList = dataSource.getColumnList();
        if (columnList != null)
            for (Column column : columnList) {
                e.addElement("yuedu:column").addAttribute("expr", column.getExpr()).addAttribute("alias", column.getAlias());
            }
        return e;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        Map<String, ArrayList<Column>> parentSelectedItemList = (Map<String, ArrayList<Column>>) x.getParent().getAttribute("parentSelectedItemList");
        x.putAttribute("parentSelectedItemList", parentSelectedItemList);
        SQLExpr condition = x.getCondition();
        SQLTableSource left = x.getLeft();
        translateTableSource(left);
        toXMLElement((DataSource) left.getAttribute("dataSource"));
        SQLTableSource right = x.getRight();
        translateTableSource(right);
        toXMLElement((DataSource) right.getAttribute("dataSource"));
        String leftId = getIdFromAttr(left);
        String rightId = getIdFromAttr(right);
        String id = leftId + x.getJoinType().name + rightId;
        id = id.replaceAll("\\s+", "_");
        addToParentAttrId(x, id);
//            Element e = currElement.addElement("yuedu:dataJoin").addAttribute("id", id).
//                    addAttribute("joinType", x.getJoinType().name).
//                    addAttribute("leftRef", leftId).
//                    addAttribute("rightRef", rightId);
//            x.putAttribute("element", e);
        DataJoin dataJoin = new DataJoin();
        dataJoin.setId(id);
        dataJoin.setLeftRef(leftId);
        dataJoin.setRightRef(rightId);
        dataJoin.setJoinType(x.getJoinType().name);
        x.putAttribute("dataSource", dataJoin);
        if (condition != null) {
            translateExpr(condition);
        }
        return false;
    }

    @Override
    public boolean visit(SQLSubqueryTableSource x) {
        this.visit(x.getSelect());
        String target = getIdFromAttr(x);
        String id = x.getAlias();
//            Element e = currElement.addElement("yuedu:dataMap").addAttribute("id", id).
//                    addAttribute("target", target);
//            x.putAttribute("element", e);
        DataMap dataMap = new DataMap();
        dataMap.setId(id);
        dataMap.setTarget(target);
        //toXMLElement(dataMap);
        x.putAttribute("dataSource", dataMap);
        addToParentAttrId(x, id);
        return false;
    }

    @Override
    public boolean visit(SQLUnionQuery x) {
        SQLUnionOperator operator = x.getOperator();
        SQLSelectQuery left = x.getLeft();
        left.accept(this);
        String leftId = getIdFromAttr(left);

        SQLSelectQuery right = x.getRight();

        right.accept(this);
        String rightId = getIdFromAttr(right);

        String id = leftId + operator.name + rightId;
        id = id.replaceAll("\\s+", "_");
        addToParentAttrId(x, id);

//            Element e = currElement.addElement("yuedu:dataCollect").addAttribute("id", id).
//                    addAttribute("collectType", x.getOperator().name).
//                    addAttribute("leftRef", leftId).
//                    addAttribute("rightRef", rightId);
//            x.putAttribute("element", e);
        DataCollect dataCollect = new DataCollect();
        dataCollect.setId(id);
        dataCollect.setLeftRef(leftId);
        dataCollect.setRightRef(rightId);
        dataCollect.setCollectType(x.getOperator().name);
        if (x.getOrderBy() != null) {
            x.getOrderBy().accept(this);
        }
        return false;
    }

    @Override
    public boolean visit(SQLUnionQueryTableSource x) {
        x.getUnion().accept(this);
        String id = x.getAlias();
        addToParentAttrId(x, id);
        String childId = getIdFromAttr(x.getUnion());
//            Element e = currElement.addElement("yuedu:dataMap").addAttribute("id", id).
//                    addAttribute("target", childId);
//            x.putAttribute("element", e);
        DataMap dataMap = new DataMap();
        dataMap.setId(id);
        dataMap.setTarget(childId);
        x.putAttribute("dataSource", dataMap);
        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        int itemSize = x.getItems().size();
        for (int i = 0; i < itemSize; ++i) {
            SQLExpr sqlExpr = x.getItems().get(i);
            sqlExpr.accept(this);
        }
        if (x.getHaving() != null) {
            x.getHaving().accept(this);
        }
        String parentId = getIdFromAttr(x.getParent());
        String id = "group_" + parentId;
        x.putAttribute("id", id);
//            Element e = currElement.addElement("yuedu:dataGroup").addAttribute("id", id).
//                    addAttribute("target", parentId);
//            x.putAttribute("element", e);
        DataGroup dataGroup = new DataGroup();
        dataGroup.setId(id);
        dataGroup.setTarget(parentId);
        dataGroup.setGroupBy(x.getItems().toString());
        if (x.getHaving() != null) {
            dataGroup.setHaving(x.getHaving().toString());
        }
        x.putAttribute("dataSource", dataGroup);
        return false;
    }

    public static void main(String args[]) throws Exception {
        SQLTranslator sqlTranslator = new SQLTranslator();
        TranslateOption translateOption=new TranslateOption();
        boolean f = translateOption.hasOption("f", args);
        String sql = translateOption.getOptionValue("sql", args);
        String dbType = translateOption.getOptionValue("dbType", args);
        String outputFile = translateOption.getOptionValue("outputFile", args);
        try {
            if (f) {
                System.out.println("Translating sql from file " + sql);
                sqlTranslator.translate(new File(sql), dbType, outputFile);
            } else {
                System.out.println("Translating sql  " + sql);
                sqlTranslator.translate(sql, dbType, outputFile);
            }
        }catch (Exception e){
            translateOption.help();
        }
    }
}
