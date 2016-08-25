package com.example.repository.transformer;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 下划线转驼峰，使用兼容模式，返回的结果例子：
 * {first_name:"Michael",firstName:"Michael"}
 * Created by zskx-dev on 2016/8/24.
 */
public class UnderscoreToCamelTransformer extends AliasedTupleSubsetResultTransformer {
    public static final UnderscoreToCamelTransformer INSTANCE = new UnderscoreToCamelTransformer();

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    private static String firstCharToLowerCase(String str) {
        return str.substring(0, 1).toLowerCase().concat(str.substring(1));
    }

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    private static String firstCharToUpperCase(String str) {
        return str.substring(0, 1).toUpperCase().concat(str.substring(1));
    }

    /**
     * 下划线转驼峰
     *
     * @param str
     * @return
     */
    private static String convertUnderscoreNameToCamelName(String str) {
        return Stream.of(str.toLowerCase().split("_"))
                .map(String::trim)
                .filter(s1 -> !s1.isEmpty())
                .reduce("", (s1, s2) -> s1.concat(s1.isEmpty() ? firstCharToLowerCase(s2) : firstCharToUpperCase(s2)));
    }

    private UnderscoreToCamelTransformer() {
    }

    //@Override
    //public Object transformTuple(Object[] tuple, String[] aliases) {
    //    Map<String, Object> result = new HashMap<>(tuple.length * 2);
    //    Map<String, Object> camelResult = new HashMap<>(tuple.length);//装载驼峰别名的值
    //    for (int i = 0; i < tuple.length; i++) {
    //        String alias = aliases[i];
    //        if (alias != null) {
    //            if (alias.contains("_")) {
    //                String camelName = convertUnderscoreNameToCamelName(alias);
    //                camelResult.put(camelName, tuple[i]);//装载驼峰别名的值
    //            }
    //            result.put(alias, tuple[i]);//1、先把最初的别名的值放进result
    //        }
    //    }
    //    for (String camelName : camelResult.keySet()) {
    //        result.putIfAbsent(camelName, camelResult.get(camelName));//2、驼峰别名的值作为补充，没有则放进result
    //    }
    //    return result;//返回的值：优先选择sql原本的别名
    //}

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Map<String, Object> result = new HashMap<>(tuple.length * 2);
        for (int i = 0; i < tuple.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                if (alias.contains("_")) {
                    String propertyName = convertUnderscoreNameToCamelName(alias);
                    result.put(propertyName, tuple[i]);
                }
                result.put(alias, tuple[i]);
            }
        }
        return result;
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    /**
     * Serialization hook for ensuring singleton uniqueing.
     *
     * @return The singleton instance : {@link #INSTANCE}
     */
    private Object readResolve() {
        return INSTANCE;
    }
}
