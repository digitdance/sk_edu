package org.springcrazy.common.oss;


import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
//import org.dromara.common.core.constant.Constants;
//import org.dromara.common.core.utils.DateUtils;
//import org.dromara.common.core.utils.StringUtils;
//import org.dromara.common.core.utils.file.FileUtils;
//import org.dromara.common.oss.constant.OssConstant;
//import org.dromara.common.oss.entity.UploadResult;
//import org.dromara.common.oss.enums.AccessPolicyType;
//import org.dromara.common.oss.exception.OssException;
//import org.dromara.common.oss.properties.OssProperties;


import io.minio.*;
import io.minio.errors.MinioException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springcrazy.common.constant.Constants;

import org.springcrazy.common.tool.StringUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Date;
import java.util.function.Consumer;

/**
 * S3 存储协议 所有兼容S3协议的云厂商均支持
 * 阿里云 腾讯云 七牛云 minio
 *
 * @author AprilWind
 */
public class OssClient {

    /**
     * 服务商
     */
    private final String configKey;

    /**
     * 配置属性
     */
    private final OssProperties properties;



    /**
     * 构造方法
     *
     * @param configKey     配置键
     * @param ossProperties Oss配置属性
     */
    public OssClient(String configKey, OssProperties ossProperties) {
        this.configKey = configKey;
        this.properties = ossProperties;
        try {


        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }



    /**
     * 上传 InputStream 到 Amazon S3
     *
     * @return UploadResult 包含上传后的文件信息
     * @throws OssException 如果上传失败，抛出自定义异常
     */
    public UploadResult upload(String filePath, String key) {

        try {
            MinioClient minioClient = MinioClient.builder()
                            .endpoint(properties.getEndpoint())
                            .credentials(properties.getAccessKey(), properties.getSecretKey())
                            .build();
            minioClient.setTimeout(1000000000,1000000000,1000000000);
            // Make 'asiatrip' bucket if not exist.
            boolean found =
                    minioClient.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
            if (!found) {
                // Make a new bucket called 'asiatrip'.
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
            } else {
                System.out.println("Bucket 'asiatrip' already exists.");
            }

            // Upload '/home/user/Photos/asiaphotos.zip' as object name 'asiaphotos-2015.zip' to bucket
            // 'asiatrip'.
            ObjectWriteResponse response =  minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(properties.getBucketName())
                            .object(key)
                            .filename(filePath)
                            .build());
            System.out.println(response.toString());

            return UploadResult.builder().url("http://116.63.57.4:9090/"+properties.getBucketName()+"/"+key ).filename(key).eTag(response.etag()).build();
        } catch (Exception e) {
            System.out.println("Error occurred: " + e);
            e.printStackTrace();
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }



    public UploadResult uploadLocalFile(String filePath, String key) {
        return upload(filePath, key);
    }



    /**
     * 获取 S3 客户端的终端点 URL
     *
     * @return 终端点 URL
     */
    public String getEndpoint() {
        // 根据配置文件中的是否使用 HTTPS，设置协议头部
        String header = getIsHttps();
        // 拼接协议头部和终端点，得到完整的终端点 URL
        return header + properties.getEndpoint();
    }

    /**
     * 获取 S3 客户端的终端点 URL（自定义域名）
     *
     * @return 终端点 URL
     */
    public String getDomain() {
        // 从配置中获取域名、终端点、是否使用 HTTPS 等信息
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();

        // 如果是云服务商，直接返回域名或终端点
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return StringUtils.isNotEmpty(domain) ? header + domain : header + endpoint;
        }

        // 如果是 MinIO，处理域名并返回
        if (StringUtils.isNotEmpty(domain)) {
            return domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP) ? domain : header + domain;
        }

        // 返回终端点
        return header + endpoint;
    }



    /**
     * 获取云存储服务的URL
     *
     * @return 文件路径
     */
    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = getIsHttps();
        // 云服务商直接返回
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            return header + (StringUtils.isNotEmpty(domain) ? domain : properties.getBucketName() + "." + endpoint);
        }
        // MinIO 单独处理
        if (StringUtils.isNotEmpty(domain)) {
            // 如果 domain 以 "https://" 或 "http://" 开头
            return (domain.startsWith(Constants.HTTPS) || domain.startsWith(Constants.HTTP)) ?
                    domain + StringUtils.SLASH + properties.getBucketName() : header + domain + StringUtils.SLASH + properties.getBucketName();
        }
        return header + endpoint + StringUtils.SLASH + properties.getBucketName();
    }

    /**
     * 生成一个符合特定规则的、唯一的文件路径。通过使用日期、UUID、前缀和后缀等元素的组合，确保了文件路径的独一无二性
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 文件路径
     */
    public String getPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 生成日期路径
        Date now = new Date();

        String datePath = DateFormatUtils.format(now, "yyyy/MM/dd");
        // 拼接路径
        String path = StringUtils.isNotEmpty(prefix) ?
                prefix + StringUtils.SLASH + datePath + StringUtils.SLASH + uuid : datePath + StringUtils.SLASH + uuid;
        return path + suffix;
    }

    /**
     * 移除路径中的基础URL部分，得到相对路径
     *
     * @param path 完整的路径，包括基础URL和相对路径
     * @return 去除基础URL后的相对路径
     */
    public String removeBaseUrl(String path) {
        return path.replace(getUrl() + StringUtils.SLASH, "");
    }

    /**
     * 服务商
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * 获取是否使用 HTTPS 的配置，并返回相应的协议头部。
     *
     * @return 协议头部，根据是否使用 HTTPS 返回 "https://" 或 "http://"
     */
    public String getIsHttps() {
        return OssConstant.IS_HTTPS.equals(properties.getIsHttps()) ? Constants.HTTPS : Constants.HTTP;
    }

    /**
     * 检查配置是否相同
     */
    public boolean checkPropertiesSame(OssProperties properties) {
        return this.properties.equals(properties);
    }


}
