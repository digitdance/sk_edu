package org.springcrazy.modules.clazz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.clazz.entity.Grade;
import org.springcrazy.modules.clazz.mapper.GradeMapper;
import org.springcrazy.modules.clazz.pojo.GradeParamDTO;
import org.springcrazy.modules.clazz.pojo.GradeSaveDTO;
import org.springcrazy.modules.clazz.pojo.GradeVO;
import org.springcrazy.modules.clazz.service.GradeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 成绩单 服务实现类
 * </p>
 *
 * @author Ryan
 */
@Service
public class GradeServiceImpl extends ServiceImpl<GradeMapper, Grade> implements GradeService {

    @Override
    public IPage<GradeVO> getList(GradeParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<GradeVO> getAll(GradeParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(GradeSaveDTO dto) {
        Grade item = new Grade();
        BeanUtils.copyProperties(dto, item);
        return this.saveOrUpdate(item);
    }
}
