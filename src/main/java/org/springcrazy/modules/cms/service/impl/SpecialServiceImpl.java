
package org.springcrazy.modules.cms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Special;
import org.springcrazy.modules.cms.mapper.ArticleMapper;
import org.springcrazy.modules.cms.mapper.SpecialMapper;
import org.springcrazy.modules.cms.service.IArticleService;
import org.springcrazy.modules.cms.service.ISpecialService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.SpecialVO;
import org.springframework.stereotype.Service;

/**
 * 文章信息表 服务实现类
 *
 * @author TongZhou
 * @since 2020-02-28
 */
@Service
public class SpecialServiceImpl extends BaseServiceImpl<SpecialMapper, Special> implements ISpecialService {

	@Override
	public IPage<SpecialVO> selectSpecialPage(IPage<SpecialVO> page, SpecialVO specialVO) {
		return page.setRecords(baseMapper.selectSpecialPage(page, specialVO));
	}

}
