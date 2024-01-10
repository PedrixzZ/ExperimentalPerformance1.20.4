package ca.fxco.experimentalperformance.utils;

import org.objectweb.asm.Type;
import java.util.Map;
import java.util.HashMap;

public class GeneralUtils {

    public static String getLastPathPart(String path) {
        int lastIndex = path.lastIndexOf("/");
        return lastIndex >= 0 ? path.substring(lastIndex + 1) : path;
    }

    public static String formatPathSlash(String path) {
        StringBuilder sb = new StringBuilder(path.length());
        for (char c : path.toCharArray()) {
            sb.append(c == '.' ? '/' : c);
        }
        return sb.toString();
    }

    public static String formatPathDot(String path) {
        StringBuilder sb = new StringBuilder(path.length());
        for (char c : path.toCharArray()) {
            sb.append(c == '/' ? '.' : c);
        }
        return sb.toString();
    }

    public static Type asType(String className) {
        return Type.getType('L' + className + ';');
    }

    // Additional optimization:

    private static final Map<String, String> pathCache = new HashMap<>();

    public static String getLastPathPartCached(String path) {
        String cached = pathCache.get(path);
        if (cached == null) {
            cached = getLastPathPart(path);
            pathCache.put(path, cached);
        }
        return cached;
    }
}
