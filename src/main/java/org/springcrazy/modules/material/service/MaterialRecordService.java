package org.springcrazy.modules.material.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.material.entity.MaterialRecord;
import org.springcrazy.modules.material.pojo.MaterialRecordParamDTO;
import org.springcrazy.modules.material.pojo.MaterialRecordVO;

/**
 * 物料出入库服务类
 */
public interface MaterialRecordService extends BaseService<MaterialRecord> {

    /**
    * 分页
    * */
    IPage<MaterialRecordVO> getList(MaterialRecordParamDTO param);

}
