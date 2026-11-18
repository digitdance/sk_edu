package org.springcrazy.modules.file.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.core.launch.props.CrazyProperties;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.constant.SystemConstant;
import org.springcrazy.core.tool.utils.FileUtil;
import org.springcrazy.modules.file.entity.SysOss;
import org.springcrazy.modules.file.service.OssService;
import org.springcrazy.modules.file.vo.SysOssUploadVo;
import org.springcrazy.modules.file.vo.SysOssVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/uploadOss")
@Api(value = "前端用户相关接口", tags = "前端接口")
public class UploadOssController extends CrazyController {



    private final CrazyProperties crazyProperties;

    @Autowired
    private OssService ossService;
    /**
     * 通用上传请求
     */
    @PostMapping("")
    public R uploadOss(MultipartFile file, HttpServletRequest httpServletRequest){

        SystemConstant me = SystemConstant.me();
        //获取文件名判断文件是否支持上传
        if(me.getFileExt().size() > 0){
            if(!me.getFileExt().contains(FileUtil.getFileExtension(file.getOriginalFilename()))){
                return R.fail("不支持该文件类型上传");
            }
        }
//        String url ="";
//        String frontPath = crazyProperties.get("front-icon-path");
//        String adminPath = crazyProperties.get("admin-icon-path");
        CrazyFile crazyFile = getFile(file,"ico");//
        crazyFile.transfer();
       // String tenant  = httpServletRequest.getHeader("tenant");
        try {

            SysOss oss =  ossService.upload(crazyFile);

            SysOssUploadVo uploadVo = new SysOssUploadVo();
            uploadVo.setUrl(oss.getUrl());
            uploadVo.setFileName(oss.getOriginalName());
            uploadVo.setOssId(oss.getOssId().toString());

            return R.data(uploadVo);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("上传失败");
        }

    }



}
