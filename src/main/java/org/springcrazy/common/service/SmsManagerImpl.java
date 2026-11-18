//package org.springcrazy.common.service;
//
//import cn.hutool.json.JSONUtil;
//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.profile.DefaultProfile;
////import com.hzb.erp.common.service.SettingService;
//import com.hzb.erp.common.service.SettingService;
//import com.hzb.erp.exception.BizException;
//import com.hzb.erp.sysservice.SmsManager;
//import com.hzb.erp.sysservice.cache.SmsCodeCache;
//import com.hzb.erp.sysservice.cache.SmsSendLimitCache;
//import com.hzb.erp.sysservice.dto.SmsSendDTO;
//import com.hzb.erp.sysservice.enums.SettingNameEnum;
//import com.hzb.erp.sysservice.enums.SmsSceneType;
//import com.tencentcloudapi.ic.v20190307.models.SendSmsRequest;
//import com.tencentcloudapi.ic.v20190307.models.SendSmsResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
///**
// * 手机短信实现
// * todo 请根据自己的短信服务商自行实现 send 方法
// */
//@Service
//@Slf4j
//public class SmsManagerImpl implements SmsManager {
//
//    @Autowired
//    private ISettingService settingService;
//
//    @Override
//    public boolean sendCode(SmsSendDTO dto) {
//        String codeTemplateId = settingService.strValue(SettingNameEnum.SMS_CODE_TEMPLATE.getCode());
//        dto.setTempId(codeTemplateId);
//        limitValid(dto.getMobile());
//        return send(dto);
//    }
//
//    /**
//    * 发送短信实现，其他短信自行修改该方法即可
//    * */
//    @Override
//    public boolean send(SmsSendDTO dto) {
//
//        if (StringUtils.isBlank(dto.getMobile())) {
//            log.error("缺少手机号");
//            return false;
//        }
//
//        // 阿里云短信服务
//        return aliSmsServe(dto);
//
//        // 腾讯云短信服务
////        return tencentSmsServe(dto);
//    }
//
//    private boolean aliSmsServe(SmsSendDTO dto) {
//        String aliAccessKey = settingService.strValue(SettingNameEnum.SMS_ACCESS_KEY.getCode());
//        String aliAccessSecret = settingService.strValue(SettingNameEnum.SMS_ACCESS_SECRET.getCode());
//        String aliSignName = settingService.strValue(SettingNameEnum.SMS_SIGN_NAME.getCode());
//
//        if (StringUtils.isBlank(aliAccessKey) || StringUtils.isBlank(aliAccessSecret) || StringUtils.isBlank(aliSignName)) {
//            log.error("短信配置出错，请检查!");
//            return false;
//        }
//
//        if (StringUtils.isBlank(dto.getTempId()) && StringUtils.isBlank(dto.getContent())) {
//            log.error("短信模板或内容未设置");
//            return false;
//        }
//
//        return false;
////        String regionId = "cn-hangzhou";
////        DefaultProfile profile = DefaultProfile.getProfile(regionId, aliAccessKey, aliAccessSecret);
////        IAcsClient client = new DefaultAcsClient(profile);
////        SendSmsRequest request = new SendSmsRequest();
////        request.setSysRegionId(regionId);
////        request.setPhoneNumbers(dto.getMobile());
////        request.setSignName(aliSignName);
////        request.setTemplateCode(dto.getTempId());
////        request.setTemplateParam(JSONUtil.toJsonStr(dto.getDataMap()));
////        try {
////            SendSmsResponse response = client.getAcsResponse(request);
////            if ("OK".equals(response.getCode()) && "OK".equals(response.getMessage())) {
////                return true;
////            } else {
////                log.error("短信发送失败!");
////                return false;
////            }
////        } catch (ClientException e) {
////            log.error("短信发送异常:" + e.toString());
////            throw new BizException("短信发送异常");
////        }
//    }
//
//    /**
//    * 腾讯短信服务，pom加上依赖：
//     *      <!-- go to https://search.maven.org/search?q=tencentcloud-sdk-java and get the latest version. -->
//     *      <!-- 请到https://search.maven.org/search?q=tencentcloud-sdk-java查询所有版本，最新版本如下 -->
//         <dependency>
//             <groupId>com.tencentcloudapi</groupId>
//             <artifactId>tencentcloud-sdk-java</artifactId>
//             <version>3.1.691</version>
//         </dependency>
//    * */
//
////    private boolean tencentSmsServe(SmsSendDTO dto) {
////
////        // secretId
////        String secretId = SettingNameEnum.SMS_TXY_SECRET_ID.getValueType();
////        // secretKey
////        String secretKey = SettingNameEnum.SMS_TXY_SECRET_KET.getValueType();
////        // 短信应用ID
////        String sdkAppId = SettingNameEnum.SMS_TXY_SDK_APP_ID.getValueType();
////        // 短信签名内容
////        String signName = SettingNameEnum.SMS_TXY_SIGN_NAME.getValueType();
////
////        if (StringUtils.isBlank(secretId) || StringUtils.isBlank(secretKey) || StringUtils.isBlank(sdkAppId) || StringUtils.isBlank(signName)) {
////            log.error("短信配置出错，请检查!");
////            return false;
////        }
////
////        if (StringUtils.isBlank(dto.getTempId())) {
////            log.error("短信模板或内容未设置");
////            return false;
////        }
////
////        Credential cred = new Credential(secretId, secretKey);
////
////        SendSmsRequest req = new SendSmsRequest();
////        req.setSmsSdkAppId(sdkAppId);
////        req.setSignName(signName);
////        req.setTemplateId(dto.getTempId());
////
////        HttpProfile httpProfile = new HttpProfile();
////        httpProfile.setReqMethod("POST");
////        httpProfile.setConnTimeout(60);
////        httpProfile.setEndpoint("sms.tencentcloudapi.com");
////
////        ClientProfile clientProfile = new ClientProfile();
////        clientProfile.setSignMethod("HmacSHA256");
////        clientProfile.setHttpProfile(httpProfile);
////
////        // 获取到验证码
////        String code = String.valueOf(dto.getDataMap().get("code"));
////
////        String[] templateParamSet = {code};
////        req.setTemplateParamSet(templateParamSet);
////        String[] phoneNumberSet = {"+86" + dto.getMobile()};
////        req.setPhoneNumberSet(phoneNumberSet);
////
////        SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
////
////        SendSmsResponse res = null;
////        try {
////            res = client.SendSms(req);
////            // 输出json格式的字符串回包
////            log.info(SendSmsResponse.toJsonString(res));
////            return true;
////        } catch (TencentCloudSDKException e) {
////            log.error("短信发送异常:" + e.toString());
////            return false;
////        }
////    }
//
//    @Override
//    public boolean valid(String scene, String mobile, String code) {
//        SmsSceneType sceneEnum = getSceneEnum(scene);
//        return SmsCodeCache.valid(mobile, code, sceneEnum);
//    }
//
//    /**
//    * 验证短信发送频率，并缓存验证码
//    * */
//    @Override
//    public void limitValid(String mobile) {
//        long remainingSec = SmsSendLimitCache.remainingSec(mobile);
//        if (remainingSec > 0) {
//            throw new BizException("短信发送过于频繁，请" + remainingSec + "秒后重发");
//        }
//    }
//
//    /**
//     * 生成5位验证码
//     */
//    @Override
//    public String makeSmsCode() {
//        return "" + (int) ((Math.random() * 9 + 1) * 1000);
//    }
//
//    private SmsSceneType getSceneEnum(String scene) {
//        for (SmsSceneType each : SmsSceneType.class.getEnumConstants()) {
//            if (each.getCode().equals(scene)) {
//                return each;
//            }
//        }
//        throw new BizException("短信场景参数有误");
//    }
//}
