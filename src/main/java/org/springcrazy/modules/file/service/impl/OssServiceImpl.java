package org.springcrazy.modules.file.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.aliyun.oss.ServiceException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jetbrains.annotations.NotNull;

import org.springcrazy.common.oss.OssClient;
import org.springcrazy.common.oss.OssFactory;
import org.springcrazy.common.oss.UploadResult;
import org.springcrazy.common.tool.MapstructUtils;
import org.springcrazy.common.tool.StringUtils;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.modules.file.entity.SysOss;
import org.springcrazy.modules.file.mapper.SysOssMapper;
import org.springcrazy.modules.file.service.OssService;
import org.springcrazy.modules.file.vo.SysOssVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
public class OssServiceImpl extends ServiceImpl<SysOssMapper, SysOss> implements OssService {


    /**
     * 上传 MultipartFile 到对象存储服务，并保存文件信息到数据库
     *
     * @param file 要上传的 MultipartFile 对象
     * @return 上传成功后的 SysOssVo 对象，包含文件信息
     * @throws ServiceException 如果上传过程中发生异常，则抛出 ServiceException 异常
     */
    @Override
    public SysOss upload(CrazyFile file) {
        String originalfileName = file.getOriginalFileName();
        String suffix = StringUtils.substring(originalfileName, originalfileName.lastIndexOf("."), originalfileName.length());
        OssClient storage = OssFactory.instance();
        UploadResult uploadResult;
        try {

            //uploadResult = storage.uploadSuffix(file.getBytes(), suffix, file.getContentType());
            String key = new Snowflake(20,20).nextIdStr();
            uploadResult = storage.uploadLocalFile(file.getUploadPath(),key+suffix);
           // uploadLocalFile
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        // 保存文件信息
        return buildResultEntity(originalfileName, suffix, storage.getConfigKey(), uploadResult);
    }

    @NotNull
    private SysOss buildResultEntity(String originalfileName, String suffix, String configKey, UploadResult uploadResult) {
        SysOss oss = new SysOss();
        oss.setUrl(uploadResult.getUrl());
        oss.setFileSuffix(suffix);
        oss.setFileName(uploadResult.getFilename());
        oss.setOriginalName(originalfileName);
        oss.setExt1(uploadResult.getETag());
        oss.setService(configKey);
        baseMapper.insert(oss);

        return oss;
    }

}
