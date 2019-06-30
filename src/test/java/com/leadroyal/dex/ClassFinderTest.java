package com.leadroyal.dex;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.dexbacked.raw.ClassDefItem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class ClassFinderTest {
    private static final Logger logger = LoggerFactory.getLogger(ClassFinder.class);

    @Test
    public void simple() throws IOException {

        String file = "/tmp/weico-no-ads/weico.apk";
        String t1 = ClassFinder.javaToDexName("de.greenrobot.event.EventBus");
        String t2 = "Lcom/tencent/android/tpush/stat/a/b;";
        for (int i = 1; true; i++) {
            String innerPath = String.format("classes%d.dex", i);
            if (i == 1)
                innerPath = "classes.dex";
            logger.error("handle {}" , innerPath);
            try {
                DexBackedDexFile dexFile = DexFileFactory.loadDexEntry(new File(file), innerPath, true, Opcodes.getDefault());
                for (int j = 0; j < dexFile.getClassCount(); j++) {
                    int classDefOffset = dexFile.getClassDefItemOffset(j);
                    int classDataOffset = dexFile.readSmallUint(classDefOffset + ClassDefItem.CLASS_DATA_OFFSET);
                    String type = dexFile.getType(dexFile.readSmallUint(classDefOffset + ClassDefItem.CLASS_OFFSET));
                    if (type.equals(t1) || type.equals(t2))
                        logger.error("{} {} {} ", type, classDefOffset, classDataOffset);
                }
            } catch (DexFileFactory.DexFileNotFoundException e) {
                logger.info("{} finished because it doesn't have {}", file, innerPath);
                break;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
