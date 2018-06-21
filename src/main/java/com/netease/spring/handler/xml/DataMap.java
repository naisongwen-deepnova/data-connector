package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;
/****
 * Author:wennaisong 2018/05/18
 * DataMap can do basic transformation on another
 */
public class DataMap extends DataSource {
    public static String tagName="dataMap";
    private String target;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getQuerySql() {
        StringBuilder sb = new StringBuilder(buildSelectClause());
        sb.append(target);
        sb.append(" ");
        String filter=buildFilterClause();
        if(!StringUtils.isEmpty(filter)) {
            sb.append(" where ");
            sb.append(filter);
        }
        String sortEtc=buildSortEtcClause();
        sb.append(sortEtc);
        return sb.toString();
    }
}
