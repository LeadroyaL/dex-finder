package com.leadroyal.dex;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLogger;

import java.io.File;
import java.util.Arrays;

public class CommandParser {
    private static final Logger logger = LoggerFactory.getLogger(CommandParser.class);

    public Config parse(String[] args) {
        logger.debug("parse command start");
        Config config = new Config();
        String cmdLineSyntax = "java -jar [-d] [-r] [-us] [-f file/directory] [-c classname]";
        Options options = new Options();
        options.addOption("c", "class", true, "Class you want to find.");
        options.addOption("f", "file", true, "File or directory to be scanned.");
        options.addOption("us", "use-sig", false, "Use class signature. If enable, use Ljava/lang/String; .");
        options.addOption("r", "recursive", false, "Recursive scan files.");
        options.addOption("d", "debug", false, "Enable debug log.");
        options.addOption("h", "help", false, "Show help.");
        DefaultParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(100);
        try {
            CommandLine commands = parser.parse(options, args);
            if (commands.hasOption("h") || !commands.hasOption("c") || !commands.hasOption("f")) {
                String footer = "java -jar dex-finder.jar -f demo.apk -c com.example.Activity\n" +
                        "java -jar dex-finder.jar -f classed.dex -c com.example.Activity\n" +
                        "java -jar dex-finder.jar -f /path/unzip_result/ -c com.example.Activity\n";
                formatter.printHelp(cmdLineSyntax, null, options, footer);
                System.exit(0);
            }
            if (commands.hasOption("us")) {
                config.targetClasses.addAll(Arrays.asList(commands.getOptionValues("c")));
            } else {
                for (String s : commands.getOptionValues("c")) {
                    config.targetClasses.add(ClassFinder.javaToDexName(s));
                }
            }
            for (String path : commands.getOptionValues("f")) {
                File f = new File(path);
                if (f.exists()) {
                    if (f.isDirectory()) {
                        config.directories.add(path);
                    } else {
                        config.files.add(path);
                    }
                } else {
                    logger.warn("No such file or directory {}", path);
                }
            }
            config.recursive = commands.hasOption("r");
            if (commands.hasOption("d"))
                System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
            if (config.targetClasses.isEmpty()) {
                logger.error("No class specified, abort scan.");
                System.exit(0);
            }
            if (config.files.isEmpty() && config.directories.isEmpty()) {
                logger.error("No files or directories specified, abort scan.");
                System.exit(0);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            logger.error("parse command success");
            return null;
        }
        logger.info("parse command success");
        return config;
    }
}
