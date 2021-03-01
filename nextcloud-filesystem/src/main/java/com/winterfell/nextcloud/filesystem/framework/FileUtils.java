package com.winterfell.nextcloud.filesystem.framework;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhuzhenjie
 */
public class FileUtils {

    private FileUtils() {

    }

    static Set<String> pictureSuffix = new HashSet<String>() {
        {
            add(".jpg");
            add(".png");
            add(".jpeg");
        }
    };

    static boolean isPicture(String fileName) {
        return pictureSuffix.contains(getFileSuffix(fileName));
    }

    static String getFileSuffix(String fileName) {
        int p = fileName.lastIndexOf(".");
        return p < 0 ? "" :
                fileName.substring(p);
    }

    /**
     * @param fileName 文件名称
     * @return 检测文件名是否合法
     */
    static boolean verifyFileName(String fileName) {
        return true;
    }

}
