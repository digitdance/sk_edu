package org.springcrazy.common.tool;


import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.seepine.oss.service.OssTemplate;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


@Component
public class CossUtil {
    private  String ossWriteUrl;

    @Value("${oss.bucket}")
    private  String bucket;

    @Value("${oss.secret-id}")
    private  String accessId;

    @Value("${oss.secret-key}")
    private  String accessKey;

    @Value("${oss.region}")
    private  String region;

    @Autowired
    OssTemplate ossTemplate;

    /**
     * 判断文件是否存在
     * @param
     * @return
     * 2018年11月23日
     */

    public String upload(CrazyFile crazyFile,String rootPath) throws Exception {
        // 创建OSSClient实例
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessId, accessKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        // clientConfig中包含了设置region, https(默认http), 超时, 代理等set方法, 使用可参见源码或者接口文档FAQ中说明
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 3 生成cos客户端
        COSClient ossClient = new COSClient(cred, clientConfig);


        File f = new File(crazyFile.getUploadPath());

        if(!f.exists()){
            throw new Exception("文件不存在");
        }

        Boolean result=ossClient.doesObjectExist( bucket,rootPath+"/"+crazyFile.getUploadVirtualPath());
        if(!result){
            try {
                System.out.println("开始上传-"+rootPath+"/"+crazyFile.getUploadVirtualPath());
                String fileUrl = ossTemplate.upload(bucket,rootPath+"/"+crazyFile.getUploadVirtualPath(),new FileInputStream(f));
                System.out.println("上传完成，上传路径="+fileUrl);
                return fileUrl;
            } catch (IOException e) {
                System.out.println("开始失败-"+rootPath+"/"+crazyFile.getUploadVirtualPath());
                e.printStackTrace();
            }finally {
                ossClient.shutdown();
            }
        }else {
            throw new Exception("文件已存在");
        }
        return "";
    }

}
