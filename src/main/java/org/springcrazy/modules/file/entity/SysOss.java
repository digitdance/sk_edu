package org.springcrazy.modules.file.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.common.pojo.AutoFillEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
public class SysOss extends AutoFillEntity {

    /**
     * 对象存储主键
     */
    @TableId(value = "oss_id")
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 服务商
     */
    private String service;
    private String fileType;

    private Long fileId;
    private int isDir;
    private Long fileSize;
    private Date deleteTime;//移到回收站时间
    private String identifier;//md5码
    private Long memberId;
    private String filePath;
    private String uploadTime;//移到回收站时间
    private Integer pointCount;
    private String timeStampName;
    private String fileUrl;
    private Integer deleteFlag;
    private int isOss;
    private Long orgId;
    private String outSrc;
}
