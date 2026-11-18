package org.springcrazy.modules.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.core.boot.file.CrazyFile;
import org.springcrazy.modules.edu.entity.CourseFavorites;
import org.springcrazy.modules.file.entity.SysOss;
import org.springcrazy.modules.file.vo.SysOssVo;
import org.springframework.web.multipart.MultipartFile;

public interface OssService  extends IService<SysOss> {
    SysOss upload(CrazyFile file);
}
