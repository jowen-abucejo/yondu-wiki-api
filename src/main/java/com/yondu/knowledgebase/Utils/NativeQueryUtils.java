package com.yondu.knowledgebase.Utils;

import java.util.Arrays;

public class NativeQueryUtils {
    public static <T> String arrayToSqlStringList(T[] array) {
        String[] stringArray = Arrays.stream(array)
                .map(String::valueOf)
                .toArray(String[]::new);

        return String.join(",", stringArray);
    }
}
