package com.netease;

import org.apache.commons.cli.*;

import java.util.Properties;

/*****
 Created by wennaisong on 2018/6/7 11:50
 *****/
public class CmdOptions {

    Options options =null;

    public void initOptions() {
        options = new Options();
        Option statDate = OptionBuilder.withArgName("date")
                .hasArg()
                .withDescription("statDate represents yestoday usually")
                .create("statDate");
        Option hdfs = new Option("hdfs", "if hdfs option is set,a hdfs path is coming hereafter");
        Option config = OptionBuilder.withArgName("file")
                .hasArg()
                .withDescription("use given file for xml configuration")
                .create("config");
        Option property = OptionBuilder.withArgName("property=value")
                .hasArgs(2)
                .withValueSeparator()
                .withDescription("use value for given property")
                .create("D");
        options.addOption(statDate);
        options.addOption(hdfs);
        options.addOption(config);
        options.addOption(property);
    }

    public CmdOptions(){
        initOptions();
    }

    public void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getClass().getSimpleName(), options);
    }

    public String getOptionValue(String option, String args[]) throws ParseException {
        CommandLineParser parser = new GnuParser();
        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
        return line.getOptionValue(option);
    }

    public Properties getOptionProperty(String option, String args[]) throws ParseException {
        CommandLineParser parser = new GnuParser();
        // parse the command line arguments
        CommandLine line = parser.parse(options, args);
        return line.getOptionProperties(option);
    }

    public boolean hasOption(String option, String args[]) {
        CommandLineParser parser = new GnuParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            return line.hasOption(option);
        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }
        return false;
    }

    public static void main(String args[]) throws ParseException {
        CmdOptions cmdOptionsv=new CmdOptions();
        cmdOptionsv.help();
        System.out.println(cmdOptionsv.getOptionValue("statDate", new String[]{"-statDate", "2018-06-07"}));
        System.out.println(cmdOptionsv.getOptionProperty("D", new String[]{"-statDate", "2018-06-07"}));
        System.out.println(cmdOptionsv.getOptionProperty("D", new String[]{"-statDate", "2018-06-07","-Dk1=v1","-Dk2=v2"}));
        System.out.println(cmdOptionsv.getOptionValue("config", new String[]{"-statDate", "2018-06-07", "-config", "/user/pris/tmp"}));
        System.out.println(cmdOptionsv.hasOption("hdfs", new String[]{"-statDate", "2018-06-07", "-hdfs", "-config", "/user/pris/tmp"}));
    }
}
