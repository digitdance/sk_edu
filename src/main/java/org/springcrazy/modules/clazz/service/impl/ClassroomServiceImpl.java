package org.springcrazy.modules.clazz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.clazz.entity.Classroom;
import org.springcrazy.modules.clazz.mapper.ClassroomMapper;
import org.springcrazy.modules.clazz.pojo.ClassroomParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassroomSaveDTO;
import org.springcrazy.modules.clazz.pojo.ClassroomVO;
import org.springcrazy.modules.clazz.service.ClassroomService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 教室 服务实现类
 * </p>
 *
 * @author Ryan
 */
@Service
public class ClassroomServiceImpl extends ServiceImpl<ClassroomMapper, Classroom> implements ClassroomService {

    @Override
    public IPage<ClassroomVO> getList(ClassroomParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(ClassroomSaveDTO dto) {
        Classroom item = new Classroom();
        BeanUtils.copyProperties(dto, item);
        if(item.getId() == null) {
            // 创建时所属学校是创建者的所属学校
            item.setSchoolId(dto.getSchoolId());
        }
        return this.saveOrUpdate(item);
    }

}
