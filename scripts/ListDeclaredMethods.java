import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prints declared constructors and methods for class names read from stdin.
 * Output is a JSON object: { "com.example.Foo": [ { "name": "...", "parameterTypes": [...] } ], ... }
 * Missing classes are mapped to null.
 */
public final class ListDeclaredMethods {

    public static void main(String[] args) throws Exception {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Map<String, List<MemberJson>> byType = new LinkedHashMap<>();
        Map<String, String> errors = new LinkedHashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String typeName = line.trim();
                if (typeName.isEmpty()) {
                    continue;
                }
                try {
                    Class<?> clazz = loadClass(typeName, loader);
                    byType.put(typeName, listMembers(clazz));
                } catch (ClassNotFoundException | LinkageError | IllegalArgumentException ex) {
                    byType.put(typeName, null);
                    errors.put(typeName, ex.getClass().getSimpleName() + ": " + ex.getMessage());
                }
            }
        }

        System.out.print(toJson(byType));
        if (!errors.isEmpty()) {
            for (Map.Entry<String, String> error : errors.entrySet()) {
                System.err.println("SKIP " + error.getKey() + " (" + error.getValue() + ")");
            }
        }
    }

    private static Class<?> loadClass(String typeName, ClassLoader loader) throws ClassNotFoundException {
        if (typeName.endsWith("[]")) {
            return loadClass(typeName.substring(0, typeName.length() - 2), loader).arrayType();
        }
        return switch (typeName) {
            case "boolean" -> boolean.class;
            case "byte" -> byte.class;
            case "char" -> char.class;
            case "double" -> double.class;
            case "float" -> float.class;
            case "int" -> int.class;
            case "long" -> long.class;
            case "short" -> short.class;
            case "void" -> void.class;
            default -> Class.forName(typeName, false, loader);
        };
    }

    private static List<MemberJson> listMembers(Class<?> clazz) {
        List<MemberJson> members = new ArrayList<>();

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Arrays.sort(constructors, Comparator.comparing(ListDeclaredMethods::signature));
        for (Constructor<?> constructor : constructors) {
            members.add(new MemberJson("<init>", parameterTypeNames(constructor.getParameterTypes())));
        }

        Method[] methods = clazz.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(ListDeclaredMethods::signature));
        for (Method method : methods) {
            members.add(new MemberJson(method.getName(), parameterTypeNames(method.getParameterTypes())));
        }

        return members;
    }

    private static String[] parameterTypeNames(Class<?>[] parameterTypes) {
        String[] names = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            names[i] = parameterTypes[i].getTypeName();
        }
        return names;
    }

    private static String signature(Constructor<?> constructor) {
        return "<init>(" + String.join(",", parameterTypeNames(constructor.getParameterTypes())) + ")";
    }

    private static String signature(Method method) {
        return method.getName() + "(" + String.join(",", parameterTypeNames(method.getParameterTypes())) + ")";
    }

    private static String toJson(Map<String, List<MemberJson>> byType) {
        StringBuilder json = new StringBuilder();
        json.append('{');
        boolean firstType = true;
        for (Map.Entry<String, List<MemberJson>> entry : byType.entrySet()) {
            if (!firstType) {
                json.append(',');
            }
            firstType = false;
            json.append(quote(entry.getKey())).append(':');
            List<MemberJson> members = entry.getValue();
            if (members == null) {
                json.append("null");
                continue;
            }
            json.append('[');
            for (int i = 0; i < members.size(); i++) {
                if (i > 0) {
                    json.append(',');
                }
                MemberJson member = members.get(i);
                json.append("{\"name\":").append(quote(member.name)).append(",\"parameterTypes\":[");
                for (int j = 0; j < member.parameterTypes.length; j++) {
                    if (j > 0) {
                        json.append(',');
                    }
                    json.append(quote(member.parameterTypes[j]));
                }
                json.append("]}");
            }
            json.append(']');
        }
        json.append('}');
        return json.toString();
    }

    private static String quote(String value) {
        StringBuilder quoted = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"', '\\' -> quoted.append('\\').append(ch);
                case '\n' -> quoted.append("\\n");
                case '\r' -> quoted.append("\\r");
                case '\t' -> quoted.append("\\t");
                default -> quoted.append(ch);
            }
        }
        return quoted.append('"').toString();
    }

    private record MemberJson(String name, String[] parameterTypes) {}
}
