package com.leadroyal.dex;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedClassDef;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.raw.ClassDefItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class ClassFinder {
    private static final Logger logger = LoggerFactory.getLogger(ClassFinder.class);

    private static Set<String> checkBackend(DexBackedDexFile dexFile, Set<String> target) {
        Set<String> found = new HashSet<>();
        for (DexBackedClassDef def : dexFile.getClasses()) {
            String type = def.getType();
            if (target.contains(type)) {
                target.remove(type);
                found.add(type);
            }
        }
        return found;
    }

    public static String javaToDexName(String javaName) {
        if (javaName.charAt(0) == '[') {
            return javaName.replace('.', '/');
        }
        return 'L' + javaName.replace('.', '/') + ';';
    }

    public static String dexToJavaName(String dexName) {
        if (dexName.charAt(0) == '[') {
            return dexName.replace('/', '.');
        }
        return dexName.replace('/', '.').substring(1, dexName.length() - 2);
    }

    public static void handleDex(String file, ScanResult result) {
        try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            DexBackedDexFile dexFile = DexBackedDexFile.fromInputStream(Opcodes.getDefault(), inputStream);
            for (String targetClass : checkBackend(dexFile, result.targetClasses))
                result.results.add(new ScanResult.Position(targetClass, file, "./"));
            logger.info("Check {} finished", file);
        } catch (DexBackedDexFile.NotADexFile e) {
            logger.debug("{} isn't a dex file", file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void handleApk(String file, ScanResult result) {
        for (int i = 1; true; i++) {
            String innerPath = String.format("classes%d.dex", i);
            if (i == 1)
                innerPath = "classes.dex";
            try {
                DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(new File(file), innerPath, true, Opcodes.getDefault());
                for (String targetClass : checkBackend(dexFile, result.targetClasses))
                    result.results.add(new ScanResult.Position(targetClass, file, innerPath));
                logger.info("Check {}->{} finished", file, innerPath);
            } catch (DexFileFactory.DexFileNotFoundException e) {
                logger.debug("{} finished because it doesn't have {}", file, innerPath);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
