package org.springcrazy.common.tool;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestUtil {
    public RequestUtil() {
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    public static void shareRequestOnSubThread() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        RequestContextHolder.setRequestAttributes(requestAttributes, true);
    }

    public static String getDomain() {
        HttpServletRequest request = getRequest();
        StringBuffer url = request.getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }

    public static String getOrigin() {
        HttpServletRequest request = getRequest();
        return request.getHeader("Origin");
    }

    public static String getCookieValue(String cookieName) {
        if (StringUtils.isBlank(cookieName)) {
            return null;
        } else {
            HttpServletRequest request = getRequest();
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Cookie[] var3 = cookies;
                int var4 = cookies.length;

                for(int var5 = 0; var5 < var4; ++var5) {
                    Cookie cookie = var3[var5];
                    if (cookie.getName().equals(cookieName)) {
                        return cookie.getValue();
                    }
                }
            }

            return null;
        }
    }
}
