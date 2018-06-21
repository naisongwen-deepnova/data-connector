package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;

/****
 * Author:wennaisong 2018/05/18
 * DataJoin can do different join operation between two dataSources(all instances of DataSource)
 */
public class DataJoin extends DataSource{
    public static String tagName="dataJoin";
    private String leftRef;
    private String rightRef;
    private String joinType;

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getLeftRef() {
        return leftRef;
    }

    public void setLeftRef(String leftRef) {
        this.leftRef = leftRef;
    }

    public String getRightRef() {
        return rightRef;
    }

    public void setRightRef(String rightRef) {
        this.rightRef = rightRef;
    }

    public String getQuerySql() {
        StringBuilder sb = new StringBuilder(buildSelectClause());
        sb.append(leftRef);
        sb.append(" ");
        String joinType=getJoinType();
        sb.append(joinType);
        sb.append(" ");
        sb.append(rightRef);
        String filter=buildFilterClause();
        if(!StringUtils.isEmpty(filter)) {
            sb.append(" on ");
            sb.append(filter);
        }
        String sortEtc=buildSortEtcClause();
        sb.append(sortEtc);
        return sb.toString();
    }
}
