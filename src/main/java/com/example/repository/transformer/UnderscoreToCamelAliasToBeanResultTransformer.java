package com.example.repository.transformer;

import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class UnderscoreToCamelAliasToBeanResultTransformer extends AliasedTupleSubsetResultTransformer {
    private final Class resultClass;
    private boolean isInitialized;
    private String[] aliases;

    private UnderscoreToCamelAliasToBeanResultTransformer(Class resultClass) {
        this.resultClass = resultClass;
    }

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

    private void initialize(String[] aliases) {
        this.aliases = new String[aliases.length];
        for (int i = 0; i < aliases.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                alias = alias.toLowerCase();
                if (alias.contains("_")) {
                    alias = convertUnderscoreNameToCamelName(alias);
                }
                this.aliases[i] = alias;
                aliases[i] = this.aliases[i];
            }
        }
        isInitialized = true;
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException(
                    "aliases are different from what is cached; aliases=" + Arrays.asList(aliases) +
                            " cached=" + Arrays.asList(this.aliases));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        Object result = null;
        try {
            if (!isInitialized) {
                initialize(aliases);
            } else {
                check(aliases);
            }
            HashMap<String, Object> hashMap = new HashMap<>(aliases.length);
            for (int i = 0; i < tuple.length; i++) {
                hashMap.put(aliases[i], tuple[i]);
            }
            if (Map.class.equals(resultClass) || HashMap.class.equals(resultClass)) {
                return hashMap;
            } else if (Map.class.isAssignableFrom(resultClass)) {
                Map result_ = (Map) resultClass.newInstance();
                result_.putAll(hashMap);
            } else {
                result = resultClass.newInstance();
                PropertyAccessorFactory.forBeanPropertyAccess(result).setPropertyValues(new MutablePropertyValues(hashMap), true, false);
            }
        } catch (Exception e) {
            throw new RuntimeException("以默认构造函数创建实例失败，请检查是否有公开的默认构造方法", e);
        }
        return result;
    }

    public static UnderscoreToCamelAliasToBeanResultTransformer aliasToBean(Class resultClass) {
        return new UnderscoreToCamelAliasToBeanResultTransformer(resultClass);
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }
}
