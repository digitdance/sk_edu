
package org.springcrazy.modules.desk.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.desk.entity.Notice;

import java.util.List;

/**
 * 服务类
 *
 * @author TongZhou
 */
public interface INoticeService extends BaseService<Notice> {

	/**
	 * 自定义分页
	 * @param page
	 * @param notice
	 * @return
	 */
	IPage<Notice> selectNoticePage(IPage<Notice> page, Notice notice);

    List<Notice> selectNoticeList(Notice notice);
}
