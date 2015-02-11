package net.nitrogen.ates.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static boolean isNullOrWhiteSpace(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static File combinePath(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2;
    }

    public static URL combineUrls(URL baseUrl, String url) throws MalformedURLException {
        return new URL(baseUrl, url);
    }

    public static String joinStrings(List<String> strings, String delimiter) {
        if (strings.size() <= 0) {
            return null;
        }

        StringBuilder strb = new StringBuilder(strings.get(0));

        for (int i = 1; i < strings.size(); i++) {
            strb.append(delimiter);
            strb.append(strings.get(i));
        }

        return strb.toString();
    }

    public static String joinStrings(String[] strings, String delimiter) {
        List<String> strs = new ArrayList<String>();

        for (String s : strings) {
            strs.add(s);
        }

        return joinStrings(strs, delimiter);
    }

    public static String shortenString(String origin, int maxSize) {
        if (maxSize <= 0) {
            return "";
        }

        if (origin.length() <= maxSize) {
            return origin;
        } else {
            if (maxSize < 3) {
                return origin.substring(0, maxSize);
            } else {
                return String.format("%s...", origin.substring(0, maxSize - 3));
            }
        }
    }
}
