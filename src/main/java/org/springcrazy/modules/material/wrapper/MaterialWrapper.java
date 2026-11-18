
package org.springcrazy.modules.material.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.material.entity.Material;
import org.springcrazy.modules.material.pojo.MaterialVO;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class MaterialWrapper extends BaseEntityWrapper<Material, MaterialVO>  {

    public static MaterialWrapper build() {
        return new MaterialWrapper();
    }

	@Override
	public MaterialVO entityVO(Material material) {
		MaterialVO articleVO = BeanUtil.copy(material, MaterialVO.class);
//		ICmsSubjectService cmsSubjectService = SpringUtil.getBean(ICmsSubjectService.class);
//		CmsSubject cmsSubject = cmsSubjectService.getSubject(articleVO.getSubjectId());
//		if(Func.isNotEmpty(cmsSubject)){
//			articleVO.setSubjectName(cmsSubject.getSubjectName());
//		}
		return articleVO;
	}

}
