package org.springcrazy.modules.material.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.common.enums.SwitchEnum;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.pojo.MaterialParamDTO;
import org.springcrazy.modules.material.pojo.MaterialStorageDTO;
import org.springcrazy.modules.material.pojo.MaterialVO;

import java.util.List;

/**
 *
 */
public interface MaterialService extends BaseService<Material> {

    IPage<MaterialVO> getList(MaterialParamDTO param);

    List<MaterialVO> getAll(MaterialParamDTO param);

    Boolean saveOrUpdateByDTO(Material dto);

    MaterialVO getInfo(Long id);

    /**
    * 通过积分礼品获取物料信息
    * */
    Material getByCreditMall(CreditMall creditMall);

    /**
     * 启用 停用
     * */
    Boolean switchState(List<Long> ids, SwitchEnum switchEnum);

    /**
    * 出入库
    * */
    boolean handleStorage(MaterialStorageDTO dto);
}
