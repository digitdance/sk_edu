package org.springcrazy.common.tool;


import java.util.Arrays;
import java.util.Collection;

public class CustomQueryUtils {

    /**
     * 构建 FIND_IN_SET 条件的 SQL 片段
     *
     * @param weeks      包含周编号的集合
     * 循环集合Collection<String>里面的数据进行动态添加sql
     * @return SQL 片段字符串
     */
    public static String buildFindInSetConditions(String[] teachers) {
        StringBuilder sb = new StringBuilder("");
        if (teachers!=null && teachers.length>0) {
            if (!Arrays.toString(teachers).equals("[]")){
                sb.append(" AND (");
                boolean firstCondition = true; // 用来跟踪是否已添加第一个条件
                if (teachers.length>0){
                    for (String week : teachers) {
                        if (!firstCondition) { // 跳过第一个的 AND (
                            sb.append(" AND ");
                        }
                        sb.append("FIND_IN_SET('").append(week).append("', teacher_ids) > 0");
                        firstCondition = false; // 标记第一个条件已添加
                    }
                }
                sb.append(")");
            }

        }

        return sb.toString();
    }

}