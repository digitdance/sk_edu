
package org.springcrazy.modules.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Special;
import org.springcrazy.modules.cms.mapper.SpecialMapper;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.SpecialVO;

/**
 * 文章信息表 服务类
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public interface ISpecialService extends BaseService<Special> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param article
	 * @return
	 */
	IPage<SpecialVO> selectSpecialPage(IPage<SpecialVO> page, SpecialVO article);

}
