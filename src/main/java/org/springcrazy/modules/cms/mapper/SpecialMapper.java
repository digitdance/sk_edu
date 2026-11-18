
package org.springcrazy.modules.cms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Special;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.SpecialVO;

import java.util.List;

/**
 * 文章信息表 Mapper 接口
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public interface SpecialMapper extends BaseMapper<Special> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param
	 * @return
	 */
	List<SpecialVO> selectSpecialPage(IPage page, SpecialVO specialVO);

}
