
package org.springcrazy.modules.creditMall.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.CmsSubject;
import org.springcrazy.modules.cms.service.ICmsSubjectService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.pojo.CreditMallVO;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class CreditMallWrapper extends BaseEntityWrapper<CreditMall, CreditMallVO>  {

    public static CreditMallWrapper build() {
        return new CreditMallWrapper();
    }

	@Override
	public CreditMallVO entityVO(CreditMall creditMall) {
		CreditMallVO articleVO = BeanUtil.copy(creditMall, CreditMallVO.class);

		return articleVO;
	}

}
