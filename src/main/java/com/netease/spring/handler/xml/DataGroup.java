package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;
/****
 * Author:wennaisong 2018/05/18
 * DataGroup can do grouping operation on any dataSource(all instances of DataSource)
 */
public class DataGroup extends DataSource {
    public static String tagName="dataGroup";
    private String target;
    private String groupBy;
    private String having;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getHaving() {
        return having;
    }

    public void setHaving(String having) {
        this.having = having;
    }


    private String buildGroupClause() {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(groupBy)) {
            sb.append(" group by ");
            sb.append(groupBy);
            if (!StringUtils.isEmpty(having)) {
                sb.append(" having ");
                sb.append(having);
            }
        }
        return sb.toString();
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
        String group=buildGroupClause();
        sb.append(group);
        String sortEtc=buildSortEtcClause();
        sb.append(sortEtc);
        return sb.toString();
    }
}
