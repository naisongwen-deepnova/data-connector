package com.netease.spring.handler.xml;

import org.springframework.util.StringUtils;

import java.io.Serializable;

/****
 *Author:wennaisong 2018/05/18
 */
public class Column implements Serializable {
    private String expr;
    private String alias;

    public String getExpr() {
        return expr;
    }

    public void setExpr(String expr) {
        this.expr = expr;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return StringUtils.isEmpty(alias) ? expr : (expr + " as " + alias);
    }
}
