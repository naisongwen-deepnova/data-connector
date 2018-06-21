package com.netease.spring.handler.xml;

import java.util.List;
import java.util.Properties;

/***
 * Author:wennaisong 2018/05/18
 * Process represent a job in spark
 */

public class Process {
    private String id;
    private String cron;
    private int[] duration;
    private Integer testInt;
    Properties properties;

    List<DataSource> dataSourceList;
    List<DataMap> dataMapList;
    List<DataCollect> dataCollectList;
    List<DataGroup> dataGroupList;
    List<DataJoin> dataJoinList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public int[] getDuration() {
        return duration;
    }

    public void setDuration(int[] duration) {
        this.duration = duration;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public List<DataSource> getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(List<DataSource> dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    public List<DataMap> getDataMapList() {
        return dataMapList;
    }

    public void setDataMapList(List<DataMap> dataMapList) {
        this.dataMapList = dataMapList;
    }

    public List<DataGroup> getDataGroupList() {
        return dataGroupList;
    }


    public List<DataCollect> getDataCollectList() {
        return dataCollectList;
    }

    public void setDataCollectList(List<DataCollect> dataCollectList) {
        this.dataCollectList = dataCollectList;
    }

    public void setDataGroupList(List<DataGroup> dataGroupList) {
        this.dataGroupList = dataGroupList;
    }

    public List<DataJoin> getDataJoinList() {
        return dataJoinList;
    }

    public void setDataJoinList(List<DataJoin> dataJoinList) {
        this.dataJoinList = dataJoinList;
    }

    public Integer getTestInt() {
        return testInt;
    }

    public void setTestInt(Integer testInt) {
        this.testInt = testInt;
    }
}
