package org.springcrazy.modules.file.vo;

import lombok.Data;

@Data
public class SysOssUploadVo {

    /**
     * URL地址
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;
    private String fileType;
    /**
     * 对象存储主键
     */
    private String ossId;

}