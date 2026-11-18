
package org.springcrazy.modules.cms.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.CmsSubject;
import org.springcrazy.modules.cms.service.ICmsSubjectService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.Author;
import org.springcrazy.modules.system.entity.User;
import org.springcrazy.modules.system.service.IUserService;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class ArticleWrapper extends BaseEntityWrapper<Article, ArticleVO>  {

    public static ArticleWrapper build() {
        return new ArticleWrapper();
    }

	@Override
	public ArticleVO entityVO(Article article) {
		ArticleVO articleVO = BeanUtil.copy(article, ArticleVO.class);
		ICmsSubjectService cmsSubjectService = SpringUtil.getBean(ICmsSubjectService.class);
		IUserService userService = SpringUtil.getBean(IUserService.class);
		CmsSubject cmsSubject = cmsSubjectService.getSubject(articleVO.getSubjectId());
		if(Func.isNotEmpty(cmsSubject)){
			articleVO.setSubjectName(cmsSubject.getSubjectName());
		}
		if(Func.isNotBlank(article.getImages())){
			articleVO.setImageList(Arrays.asList(article.getImages().split(",")));
		}
		if(article.getCreateUser()!=null){
			User user = userService.getById(article.getCreateUser());
			Author auther = new Author();
			auther.setAvatar(user.getAvatar());
			auther.setName(user.getName());
			articleVO.setAuthorInfo(auther);

		}
		return articleVO;
	}

}
