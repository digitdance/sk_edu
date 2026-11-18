package org.springcrazy.common.service;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springcrazy.common.enums.BaseEnum;
import org.springcrazy.common.exception.BizException;
import org.springcrazy.common.tool.CommonUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class ImportExportService {
    private static final Logger log = LoggerFactory.getLogger(ImportExportService.class);

    public ImportExportService() {
    }

    public <T> void exportExcel(Map<String, String> headerMap, List<T> dataList, String titleName) {
        if (dataList != null && dataList.size() != 0) {
            titleName = titleName + LocalDate.now().toString();
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = servletRequestAttributes.getResponse();
            if (response == null) {
                throw new BizException("响应错误");
            } else {
                ExcelWriter writer = ExcelUtil.getWriter();
                Iterator var7 = headerMap.keySet().iterator();

                while(var7.hasNext()) {
                    String key = (String)var7.next();
                    writer.addHeaderAlias(key, (String)headerMap.get(key));
                }

                List<Map<String, Object>> data = new ArrayList();

                try {
                    Iterator var16 = dataList.iterator();

                    while(var16.hasNext()) {
                        T item = (T) var16.next();
                        Map<String, Object> newMap = new LinkedHashMap();
                        Map<String, Object> itemMap = objectToMap(item);
                        Iterator var12 = headerMap.keySet().iterator();

                        while(var12.hasNext()) {
                            String key = (String)var12.next();
                            newMap.put(key, itemMap.get(key));
                        }

                        data.add(newMap);
                    }
                } catch (IllegalAccessException var14) {
                    writer.close();
                    throw new BizException("数据映射错误:" + var14.getMessage());
                }

                writer.merge(headerMap.size() - 1, titleName);
                writer.write(data, true);
                this.handleExport(response, writer, titleName);
            }
        } else {
            throw new BizException("导出数据为空");
        }
    }

    private static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap();
        Class<?> clazz = obj.getClass();
        Field[] var3 = clazz.getDeclaredFields();
        int var4 = var3.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = field.get(obj);
            if (value instanceof BaseEnum) {
                map.put(fieldName, ((BaseEnum)value).getName());
            } else {
                map.put(fieldName, value == null ? "" : String.valueOf(value));
            }
        }

        return map;
    }

    public void exportExcel(HttpServletResponse response, Map<String, String> headerMap, List<Map<String, Object>> exampleList, String titleName) {
        titleName = titleName + LocalDate.now().toString();
        ExcelWriter writer = ExcelUtil.getWriter();
        Map<String, Object> emptyRow = new LinkedHashMap();
        Iterator var7 = headerMap.keySet().iterator();

        while(var7.hasNext()) {
            String key = (String)var7.next();
            writer.addHeaderAlias(key, (String)headerMap.get(key));
            emptyRow.put(key, "");
        }

        writer.merge(headerMap.size() - 1, titleName);
        if (exampleList == null) {
            exampleList = new ArrayList();
            ((List)exampleList).add(emptyRow);
        }

        writer.write((Iterable)exampleList, true);
        this.handleExport(response, writer, titleName);
    }

    private void handleExport(HttpServletResponse response, ExcelWriter writer, String titleName) {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");

        try {
            String name = URLEncoder.encode(titleName, StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + name + ".xls");
        } catch (UnsupportedEncodingException var6) {
            log.error("URL转码异常：" + var6.getMessage());
        }

        try {
            writer.flush(response.getOutputStream(), true);
            writer.close();
        } catch (Exception var5) {
            log.error("Excel导出IO异常：" + var5.getMessage());
        }

    }

    public List<Map<String, Object>> importExcel(MultipartFile file, Map<String, String> headerMap) {
        String fileName = file.getOriginalFilename();
        if (!CommonUtil.checkFileExt(fileName, ".xls,.xlsx")) {
            log.error("请上传扩展名为.xls或.xlsx的文件");
            throw new BizException("请上传扩展名为.xls或.xlsx的文件");
        } else {
            ExcelReader reader;
            try {
                InputStream inputStream = file.getResource().getInputStream();
                reader = ExcelUtil.getReader(inputStream);
            } catch (IOException var14) {
                log.error("导入出错：" + var14.getMessage());
                throw new BizException("导入出错：" + var14.getMessage());
            }

            List<Map<String, Object>> readAll = reader.read(1, 1, 100000);
            List<Map<String, Object>> res = new ArrayList();

            HashMap resMap;
            label49:
            for(Iterator var7 = readAll.iterator(); var7.hasNext(); res.add(resMap)) {
                Map<String, Object> oneMap = (Map)var7.next();
                resMap = new HashMap();
                Iterator var10 = oneMap.keySet().iterator();

                while(true) {
                    while(true) {
                        if (!var10.hasNext()) {
                            continue label49;
                        }

                        String key1 = (String)var10.next();
                        if (key1.trim().startsWith("*") && StringUtils.isEmpty(oneMap.get(key1).toString())) {
                            throw new BizException(key1 + "列有空值，无法导入!");
                        }

                        Iterator var12 = headerMap.keySet().iterator();

                        while(var12.hasNext()) {
                            String key2 = (String)var12.next();
                            if (key1.equals(headerMap.get(key2))) {
                                resMap.put(key2, oneMap.get(key1));
                                break;
                            }
                        }
                    }
                }
            }

            return res;
        }
    }

    public static Boolean isRowNotUnique(List<Map<String, Object>> dataList, String fieldName) {
        Set<Object> set1 = new HashSet();
        Iterator var3 = dataList.iterator();

        while(var3.hasNext()) {
            Map<String, Object> m = (Map)var3.next();
            set1.add(m.get(fieldName));
        }

        return dataList.size() != set1.size() ? true : false;
    }
}
