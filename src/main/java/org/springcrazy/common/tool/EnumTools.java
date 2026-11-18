package org.springcrazy.common.tool;


import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springcrazy.common.enums.BaseEnum;

public class EnumTools {
    public EnumTools() {
    }

    public static <T extends BaseEnum> Map<Object, String> fetchForMap(Class<?> c) throws Exception {
        Map<Object, String> res = new LinkedHashMap();
        if (!c.isEnum()) {
            return res;
        } else {
            Method method = c.getDeclaredMethod("values");
            BaseEnum[] elements = (BaseEnum[])((BaseEnum[])method.invoke((Object)null));
            BaseEnum[] var4 = elements;
            int var5 = elements.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                T element = (T) var4[var6];
                res.put(element.getCode(), element.getName());
            }

            return res;
        }
    }

    public static <T extends BaseEnum> String queryById(Class<T> c, int id) throws Exception {
        Method method = c.getDeclaredMethod("codeOf", Integer.TYPE);
        BaseEnum rejectReasonType = (BaseEnum)method.invoke((Object)null, id);
        if (rejectReasonType == null) {
            return "";
        } else {
            StringBuilder reason = new StringBuilder();
            reason.append(rejectReasonType.getName());
            return reason.toString();
        }
    }

    public static <T extends BaseEnum> T getByDist(String dist, Class<T> enumClass) {
        if (StringUtils.isNotBlank(dist)) {
            BaseEnum[] var2 = (BaseEnum[])enumClass.getEnumConstants();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                T each = (T) var2[var4];
                if (dist.equals(each.getName())) {
                    return each;
                }
            }
        }

        return null;
    }

    public static <T extends BaseEnum> Integer getCodeByDist(String dist, Class<T> enumClass) {
        if (StringUtils.isBlank(dist)) {
            return null;
        } else {
            BaseEnum target = getByDist(dist, enumClass);
            return target == null ? null : target.getCode();
        }
    }

    public static <T extends BaseEnum> T getByCode(int code, Class<T> enumClass) {
        BaseEnum[] var2 = (BaseEnum[])enumClass.getEnumConstants();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            T each = (T) var2[var4];
            if (code == each.getCode()) {
                return each;
            }
        }

        return null;
    }
}
