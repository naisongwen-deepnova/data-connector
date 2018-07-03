package com.netease;

import org.apache.commons.cli.*;

/*****
 Created by wennaisong on 2018/6/28 19:17
 *****/
public class TranslateOption extends CmdOptions{
    @Override
    public  void initOptions() {
        options = new Options();
        Option sql = OptionBuilder.withArgName("sql")
                .hasArg()
                .withDescription("sql statements MUST NOT NULL")
                .create("sql");
        Option dbType = OptionBuilder.withArgName("dbType")
                .hasArg()
                .withDescription("dbType")
                .create("dbType");
        Option outputFile = OptionBuilder.withArgName("outputFile")
                .hasArg()
                .withDescription("outputFile")
                .create("outputFile");
        Option f = new Option("f", "if f option is set,a file is coming hereafter");

        options.addOption(sql);
        options.addOption(f);
        options.addOption(dbType);
        options.addOption(outputFile);
    }

    public TranslateOption() {
        initOptions();
    }


    public static void main(String args[]) throws ParseException {
        TranslateOption translateOption=new TranslateOption();
        translateOption.help();
        System.out.println(translateOption.getOptionValue("sql", new String[]{"-sql","select a.id,a.name from a where a.id=1 order by a.age"}));
    }
}
