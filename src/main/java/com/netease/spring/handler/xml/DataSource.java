package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/****
 * Author:wennaisong 2018/05/18
 * DataSource represent different source of stored path,such as hdfs,jdbc,etc
 * it should be loaded when used
 * this data struct can be inherited by DataGroup,DataMap,DataJoin,DataCollect
 */
public class DataSource {
    public static String tagName = "dataSource";
    private String id;
    private String format="parquet";
    private String path;
    private String regex;
    private String condition;
    private String filter;
    private String jdbcUrl;
    private String table;
    private String savePath;
    private String saveFormat="parquet";
    private String orderBy;
    private String sortBy;
    private String clusterBy;
    private Boolean cache=false;
    private Boolean distinct=false;
    private String distributeBy;
    private Integer partitionNum=0;
    private String saveMode="error";

    List<Column> columnList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getSaveFormat() {
        return saveFormat;
    }

    public void setSaveFormat(String saveFormat) {
        this.saveFormat = saveFormat;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getClusterBy() {
        return clusterBy;
    }

    public void setClusterBy(String clusterBy) {
        this.clusterBy = clusterBy;
    }

    public Boolean getCache() {
        return cache;
    }

    public void setCache(Boolean cache) {
        this.cache = cache;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    public String getDistributeBy() {
        return distributeBy;
    }

    public void setDistributeBy(String distributeBy) {
        this.distributeBy = distributeBy;
    }

    public Integer getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(Integer partitionNum) {
        this.partitionNum = partitionNum;
    }

    public String getSaveMode() {
        return saveMode;
    }

    public void setSaveMode(String saveMode) {
        this.saveMode = saveMode;
    }


    public List<Column> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<Column> columnList) {
        this.columnList = columnList;
    }

    public boolean needProcess() {
        return !StringUtils.isEmpty(jdbcUrl) || !StringUtils.isEmpty(savePath);
    }

    public String buildSimpleSelectClause() {
        StringBuilder sb = new StringBuilder();
        for(Column c:columnList) {
            String alias=c.getAlias();
            sb.append(StringUtils.isEmpty(alias)?c.getExpr():alias);
            sb.append(",");
        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length() - 1);
        }else{
            sb.append("*");
        }
        sb.insert(0,"select ");
        sb.append(" from ");
        return sb.toString();
    }

    public String buildSelectClause() {
        StringBuilder sb = new StringBuilder();
        for(Column c:columnList) {
            sb.append(c.toString());
            sb.append(",");
        }
        if(sb.length()>0) {
            sb.deleteCharAt(sb.length() - 1);
        }else{
            sb.append("*");
        }
        sb.insert(0,"select ");
        sb.append(" from ");
        return sb.toString();
    }

    public String buildFilterClause() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(filter)) {
            sb.append(filter);
        }
        return sb.toString();
    }

    public String buildWhereClause() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(condition)) {
            sb.append(condition);
        }
        return sb.toString();
    }

    public String buildSortEtcClause() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(orderBy)) {
            sb.append(" order by  ");
            sb.append(orderBy);
        }
        else if (!StringUtils.isEmpty(sortBy)) {
            sb.append(" sort by  ");
            sb.append(sortBy);
        }
        else if (!StringUtils.isEmpty(clusterBy)) {
            sb.append(" cluster by  ");
            sb.append(clusterBy);
        }
        else if (!StringUtils.isEmpty(distributeBy)) {
            sb.append(" distribute by  ");
            sb.append(distributeBy);
        }
        return sb.toString();
    }

    public String getQuerySql() {
        StringBuilder sb = new StringBuilder(buildSelectClause());
        sb.append(id);
        sb.append(" ");
        String sortEtc=buildSortEtcClause();
        sb.append(sortEtc);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(buildSimpleSelectClause());
        sb.append(id);
        sb.append(" ");
        String where=buildWhereClause();
        if(!StringUtils.isEmpty(where)) {
            sb.append(" where ");
            sb.append(where);
        }
        return sb.toString();
    }
}
