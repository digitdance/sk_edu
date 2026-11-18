
package org.springcrazy.common.tool;

import com.alibaba.excel.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springcrazy.common.exception.BizException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通用工具类
 *
 * @author TongZhou
 */
public class CommonUtil {

    /**
     * 生成订单号 当前用户id+毫秒数
     */
    public static String getOrderNum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int a = (int)(Math.random() * 9000.0D) + 1000;
        System.out.println(a);
        Date date = new Date();
        String str = sdf.format(date);
        String[] split = str.split("-");
        String s = split[0] + split[1] + split[2];
        String[] split1 = s.split(" ");
        String s1 = split1[0] + split1[1];
        String[] split2 = s1.split(":");
        return split2[0] + split2[1] + split2[2] + a;
    }
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    public CommonUtil() {
    }

    public static void handleValidMessage(BindingResult result) {
        List<FieldError> fieldErrors = result.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            throw new BizException(((FieldError)fieldErrors.get(0)).getDefaultMessage());
        }
    }

    public static String getSysYear() {
        Calendar date = Calendar.getInstance();
        return String.valueOf(date.get(1));
    }

    public static long dateToTimestamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        return date.getTime();
    }

    public static String timestampToDate(Long s) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        return simpleDateFormat.format(date);
    }

    public static Date strToDate(String str) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.parse(str);
    }

    public static int timestampToSec(Date date) {
        if (null == date) {
            return 0;
        } else {
            String timestamp = String.valueOf(date.getTime() / 1000L);
            return Integer.parseInt(timestamp);
        }
    }

    public static void jsonResponse(HttpServletResponse response, String jsonStr) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(jsonStr);
    }

    public static boolean isPost(HttpServletRequest request) {
        return request.getMethod().equals("POST");
    }

    public static String lineToHump(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            str = str.toLowerCase();
            Matcher matcher = linePattern.matcher(str);
            StringBuffer sb = new StringBuffer();

            while(matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }

            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    public static String humpToLine(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            Matcher matcher = humpPattern.matcher(str);
            StringBuffer sb = new StringBuffer();

            while(matcher.find()) {
                matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            }

            matcher.appendTail(sb);
            return sb.toString();
        }
    }

    public static Boolean checkFileExt(String fileName, String ext) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        } else {
            String[] extList = ext.trim().split(",");
            String[] var3 = extList;
            int var4 = extList.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String ex = var3[var5];
                if (fileName.endsWith(ex)) {
                    return true;
                }
            }

            return false;
        }
    }

//    public static String getCookie(String cookieName) {
//        HttpServletRequest request = RequestUtil.getRequest();
//        String token = null;
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            Cookie[] var4 = cookies;
//            int var5 = cookies.length;
//
//            for(int var6 = 0; var6 < var5; ++var6) {
//                Cookie cookie = var4[var6];
//                if (cookie.getName().equals(cookieName)) {
//                    token = cookie.getValue();
//                    break;
//                }
//            }
//        }
//
//        return token;
//    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static Integer versionCompare(String version1, String version2) {
        if (version1 != null && version2 != null) {
            String[] versionArray1 = version1.split("\\.");
            String[] versionArray2 = version2.split("\\.");
            int idx = 0;
            int minLength = Math.min(versionArray1.length, versionArray2.length);

            int diff;
            for(diff = 0; idx < minLength && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0 && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0; ++idx) {
            }

            diff = diff != 0 ? diff : versionArray1.length - versionArray2.length;
            return diff;
        } else {
            return null;
        }
    }

    public static String markMobile(String mobile) {
        if (StringUtils.isNotBlank(mobile) && mobile.length() == 11) {
            mobile = mobile.replaceAll(mobile, mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4));
        }

        return mobile;
    }
}
