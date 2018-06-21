package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;
/****
 *  Author:wennaisong 2018/05/18
 *  DataCollect can do different data collection operation between two dataSources(all instances of DataSource)
 */
public class DataCollect extends DataSource{
    public static String tagName="dataCollect";
    private String leftRef;
    private String rightRef;
    private String collectType;

    //NOTE: condition ignored in DataCollect,please set it in its refered DataSources

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

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }


    public String getQuerySql() {
        StringBuilder sb = new StringBuilder(buildSelectClause());
        sb.append(leftRef);
        sb.append(" ");
        String collectType=getCollectType();
        sb.append(" ");
        sb.append(collectType);
        sb.append(" ");
        sb.append(buildSelectClause());
        sb.append(rightRef);
        String sortEtc=buildSortEtcClause();
        sb.append(sortEtc);
        return sb.toString();
    }
}
