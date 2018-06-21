package com.netease.spring.handler;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SparkTest {

    static String renderString(String virginSrc, Properties properties) {
        String s ="hello world";
        s = s.replaceAll("\\s+", "_");
        Matcher m = Pattern.compile("\\$\\{(\\w+)\\}").matcher(virginSrc);
        while (m.find()) {
            String value=properties.getProperty(m.group(1));
            if(value==null){
                throw new IllegalArgumentException("No variable key provided or the value is not instance of String");
            }
            virginSrc = virginSrc.replace(m.group(), properties.getProperty(m.group(1)));
        }
        return virginSrc;
    }

    public static void main(String args[]) {

        String virginSrc = "${what} ${who}";
        Properties properties = new Properties();
        properties.setProperty("what", "hello");
        properties.setProperty("who", "world");
        String distString = renderString(virginSrc, properties);
        System.out.println(distString);

        SparkConf conf = new SparkConf().setMaster("local").
                set("spark.sql.parquet.binaryAsString", "true").
                set("spark.sql.crossJoin.enabled", "true").
                setAppName(SparkTest.class.getSimpleName());
        JavaSparkContext sc = new JavaSparkContext(conf);
        SQLContext sqlContext = new SQLContext(sc);
        Dataset<Row> dataset = sqlContext.read().format("json").load("/D:/workspace/sqlBricks/testData/readdata.json");
        //Dataset<Row> dataset = sqlContext.read().format("csv").load("/D:/workspace/sqlBricks/testData/readdata.csv");
        dataset.show();
        dataset = dataset.filter("_c1=1");
        dataset.write().format("csv").mode("overwrite").save("/D:/workspace/sqlBricks/testData/readdata1.csv");
        dataset.write().format("json").mode("overwrite").save("/D:/workspace/sqlBricks/testData/readdata1.json");

        Dataset<Row> parquetDataSet = sqlContext.read().format("parquet").load("/D:/workspace/sqlBricks/testData/read_book_stat/2018-06-05/1");
        parquetDataSet.show();
    }
}
