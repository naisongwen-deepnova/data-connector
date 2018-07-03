package com.netease.spring.handler;

import com.netease.CmdOptions;
import com.netease.spring.handler.xml.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/****
 * Author:wennaisong 2018/05/20
 *
 */
public class TaskProcessor {

    private Map<String, DataSource> processWaiting = new HashMap<>();

    private Map<String, DataSource> processCompleted = new HashMap<>();

    private SparkConf conf = new SparkConf().
            set("spark.sql.parquet.binaryAsString", "true").
            set("spark.sql.crossJoin.enabled", "true").
            setAppName(TaskProcessor.class.getSimpleName());
    private JavaSparkContext sc = new JavaSparkContext(conf);
    private SQLContext sqlContext = new SQLContext(sc);

    public void process(com.netease.spring.handler.xml.Process process, Properties externalProperties) {
        Properties properties = process.getProperties();
        properties.putAll(externalProperties);
        String statDate = properties.getProperty("statDate");
        if (statDate == null) {
            DateTime datetime = new DateTime();
            statDate = datetime.toString("YYYY-MM-dd");
            properties.put("statDate", statDate);
        }
        String endDate = DateTime.parse(statDate).plusDays(1).toString("yyyy-MM-dd");
        properties.put("endDate", endDate);
        initProcessWaiting(process);
        for (int duration : process.getDuration()) {
            if (duration > 0)
                properties.put("startDate", DateTime.parse(endDate).minus(duration).toString("YYYY-MM-dd"));
            else
                properties.put("startDate", "2018-01-01");
            properties.put("duration", String.valueOf(duration));
            processCompleted.clear();
            for (DataSource dataSource : processWaiting.values()) {
                processDataSource(dataSource, properties);
            }
        }
        sc.stop();
    }

    static String renderString(String virginSrc, Properties properties) {
        if (StringUtils.isEmpty(virginSrc))
            return virginSrc;
        Matcher m = Pattern.compile("\\$\\{(\\w+)\\}").matcher(virginSrc);
        while (m.find()) {
            String value = properties.getProperty(m.group(1));
            if (value == null) {
                throw new IllegalArgumentException(String.format("No variable key %s provided or the value is not instance of String", m.group(1)));
            }
            virginSrc = virginSrc.replace(m.group(), properties.getProperty(m.group(1)));
        }
        return virginSrc;
    }

    private void postProcessDataSource(DataSource ds, Properties properties) {
        Boolean cache = ds.getCache();
        Boolean distinct = ds.getDistinct();
        Dataset<Row> dataset;
        String sqlTextWithoutSort = renderString(ds.getQuerySql(), properties);
        dataset = sqlContext.sql(sqlTextWithoutSort);
        dataset.createOrReplaceTempView(ds.getId());
        if (!StringUtils.isEmpty(ds.getCondition())) {
            String sqlText = renderString(ds.toString(), properties);
            dataset = sqlContext.sql(sqlText);
        }
        if (cache) {
            dataset = dataset.cache();
        }
        if (distinct) {
            dataset = dataset.distinct();
        }
        Integer partitionNum = ds.getPartitionNum();
        if (partitionNum > 0) {
            dataset = dataset.repartition(partitionNum);
        }
        String savePath = renderString(ds.getSavePath(), properties);
        String saveFormat = ds.getSaveFormat();
        String saveMode = ds.getSaveMode();
        if (!StringUtils.isEmpty(savePath)) {
            dataset.write().format(saveFormat).mode(saveMode).save(savePath);
        }
        String jdbcUrl = renderString(ds.getJdbcUrl(), properties);
        if (!StringUtils.isEmpty(jdbcUrl)) {
            dataset.write().mode(saveMode).jdbc(jdbcUrl, ds.getTable(), properties);
        }
        dataset.createOrReplaceTempView(ds.getId());
    }

    private void checkIdUnique(String id) {
        if (processWaiting.containsKey(id))
            throw new RuntimeException(id + " already exist,please make sure id unique in a process");
    }

    private void initProcessWaiting(com.netease.spring.handler.xml.Process process) {
        if (process.getDataSourceList() != null)
            for (DataSource dataSource : process.getDataSourceList()) {
                checkIdUnique(dataSource.getId());
                processWaiting.put(dataSource.getId(), dataSource);
            }
        if (process.getDataMapList() != null)
            for (DataSource dataSource : process.getDataMapList()) {
                checkIdUnique(dataSource.getId());
                processWaiting.put(dataSource.getId(), dataSource);
            }
        if (process.getDataCollectList() != null)
            for (DataSource dataSource : process.getDataCollectList()) {
                checkIdUnique(dataSource.getId());
                processWaiting.put(dataSource.getId(), dataSource);
            }
        if (process.getDataGroupList() != null)
            for (DataSource dataSource : process.getDataGroupList()) {
                checkIdUnique(dataSource.getId());
                processWaiting.put(dataSource.getId(), dataSource);
            }
        if (process.getDataJoinList() != null)
            for (DataSource dataSource : process.getDataJoinList()) {
                checkIdUnique(dataSource.getId());
                processWaiting.put(dataSource.getId(), dataSource);
            }
    }

    private void processDataSource(DataSource dataSource, Properties properties) {
        if (!processCompleted.containsKey(dataSource.getId())) {
            if (dataSource.getClass() == DataSource.class) {
                String path = renderString(dataSource.getPath(), properties);
                String format = dataSource.getFormat();
                Dataset dataset = sqlContext.read().format(format).load(path);
                String filter = renderString(dataSource.getFilter(), properties);
                if (!StringUtils.isEmpty(filter)) {
                    dataset = dataset.filter(filter);
                }
                //TODO: parse non-structural data with regex
                dataset.createOrReplaceTempView(dataSource.getId());
            } else if (dataSource.getClass() == DataMap.class) {
                String targetDatasourceRef = ((DataMap) dataSource).getTarget();
                if (!processCompleted.containsKey(targetDatasourceRef)) {
                    DataSource ds = processWaiting.get(targetDatasourceRef);
                    processDataSource(ds, properties);
                }
            } else if (dataSource.getClass() == DataCollect.class) {
                String leftDatasourceRef = ((DataCollect) dataSource).getLeftRef();
                if (!processCompleted.containsKey(leftDatasourceRef)) {
                    DataSource ds = processWaiting.get(leftDatasourceRef);
                    processDataSource(ds, properties);
                }
                String rightDatasourceRef = ((DataCollect) dataSource).getRightRef();
                if (!processCompleted.containsKey(rightDatasourceRef)) {
                    DataSource ds = processWaiting.get(rightDatasourceRef);
                    processDataSource(ds, properties);
                }
            } else if (dataSource.getClass() == DataJoin.class) {
                String leftDatasourceRef = ((DataJoin) dataSource).getLeftRef();
                if (!processCompleted.containsKey(leftDatasourceRef)) {
                    DataSource ds = processWaiting.get(leftDatasourceRef);
                    if (ds == null)
                        throw new RuntimeException(String.format("%s not existed", leftDatasourceRef));
                    processDataSource(ds, properties);
                }
                String rightDatasourceRef = ((DataJoin) dataSource).getRightRef();
                if (!processCompleted.containsKey(rightDatasourceRef)) {
                    DataSource ds = processWaiting.get(rightDatasourceRef);
                    processDataSource(ds, properties);
                }
            } else if (dataSource.getClass() == DataGroup.class) {
                String targetDatasourceRef = ((DataGroup) dataSource).getTarget();
                if (!processCompleted.containsKey(targetDatasourceRef)) {
                    DataSource ds = processWaiting.get(targetDatasourceRef);
                    processDataSource(ds, properties);
                }
            } else if (dataSource.getClass() == DataMap.class) {
                String targetDatasourceRef = ((DataMap) dataSource).getTarget();
                if (!processCompleted.containsKey(targetDatasourceRef)) {
                    DataSource ds = processWaiting.get(targetDatasourceRef);
                    processDataSource(ds, properties);
                }
            }
            System.out.println(renderString(dataSource.getQuerySql(), properties));
            //System.out.println(dataSource.toString());
            postProcessDataSource(dataSource, properties);
            processCompleted.put(dataSource.getId(), dataSource);
        }
    }

    static String readfile(String uriString) throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fSystem = FileSystem.get(configuration);

        InputStream inputStream = null;
        try {
            Path path = new Path(uriString);
            String fileName = path.getName();
            inputStream = fSystem.open(path);
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            //复制到标准输出流
            IOUtils.copyBytes(inputStream, fos, 4096, false);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeStream(inputStream);
        }
    }

    public static void main(String args[]) throws Exception {
        ApplicationContext context;
        CmdOptions cmdOptionsv=new CmdOptions();
        boolean hdfs = cmdOptionsv.hasOption("hdfs", args);
        String config = cmdOptionsv.getOptionValue("config", args);
        String statDate = cmdOptionsv.getOptionValue("statDate", args);
        if (hdfs) {
            System.out.println("Loading " + config + " from hdfs");
            context = new FileSystemXmlApplicationContext(readfile(config));
        } else {
            System.out.println("Loading " + config + " from local fs");
            context = new ClassPathXmlApplicationContext(new String[]{config});
        }
        com.netease.spring.handler.xml.Process process = (com.netease.spring.handler.xml.Process) context.getBean(context.getBeanDefinitionNames()[0]);
        TaskProcessor taskProcessor = new TaskProcessor();
        Properties properties = cmdOptionsv.getOptionProperty("D", args);
        if (!StringUtils.isEmpty(statDate)) properties.put("statDate", statDate);
        taskProcessor.process(process, properties);
    }
}
