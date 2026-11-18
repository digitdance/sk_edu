
package org.springcrazy.common.cache;

import jdk.nashorn.internal.runtime.GlobalConstants;

/**
 * 缓存名
 *
 * @author TongZhou
 */
public interface CacheNames {

	String NOTICE_ONE = "notice:one";

	String DICT_VALUE = "dict:value";
	String DICT_LIST = "dict:list";

	String CAPTCHA_KEY = "blade:auth::captcha:";
	String SMSCODE_KEY = "smscode:";
	String SMSCODECHECK_KEY = "smscodecheck:";
	String ACTIVECODE_KEY = "activecode:";
	String VIEWLOG = "viewlog:";
	String LOGINLOG = "loginlog:";

	/**
	 * OSS配置
	 */
	String SYS_OSS_CONFIG =  "sys_oss_config";
}
