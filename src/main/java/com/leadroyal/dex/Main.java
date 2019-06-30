package com.leadroyal.dex;

import org.jf.dexlib2.dexbacked.ZipDexContainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        Config config = new CommandParser().parse(args);
        ScanResult result = new ScanResult(config.targetClasses);
        // scan config.files
        for (String file : config.files) {
            if (result.shouldFinish())
                break;
            processFile(file, result);
        }
        // scan config.directories
        for (String directory : config.directories) {
            if (config.recursive) {
                // when enable recursive
                for (String file : recursiveVisit(directory))
                    if (new File(file).isFile()) {
                        if (result.shouldFinish())
                            break;
                        processFile(file, result);
                    }

            } else {
                String[] files = new File(directory).list();
                for (String file : files)
                    if (new File(directory + File.separator + file).isFile()) {
                        if (result.shouldFinish())
                            break;
                        processFile(directory + File.separator + file, result);
                    }
            }
        }
        result.show();
    }

    private static List<String> recursiveVisit(String directory) {
        List<String> ret = new ArrayList<>();
        File[] fs = new File(directory).listFiles();
        for (File f : fs) {
            if (f.isDirectory())
                ret.addAll(recursiveVisit(directory + File.separator + f.getName()));
            if (f.isFile()) {
                if (!f.getName().endsWith(".png")
                        && !f.getName().endsWith(".xml")
                        && !f.getName().endsWith(".jpg"))
                    ret.add(directory + File.separator + f.getName());
            }
        }
        return ret;
    }

    private static void processFile(String file, ScanResult result) {
        ZipDexContainer container = new ZipDexContainer(new File(file), null);
        if (container.isZipFile()) {
            // case apk
            ClassFinder.handleApk(file, result);
        } else {
            // case dex
            ClassFinder.handleDex(file, result);
        }
    }
}
