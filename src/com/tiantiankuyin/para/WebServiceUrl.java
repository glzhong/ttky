package com.tiantiankuyin.para;

//TODO 代码合并完成后，需在调用以下URL的地方，作地址为空的判断，为空则不发请求
public class WebServiceUrl {

	/** 服务器接口 */
	public static final String EASOU_SERVER = "http://service.mp3.easou.com:82/";
//	http://113.105.248.198:8081/  http://service.mp3.easou.com:82/
//	/** 搜索接口 */
//	public static final String SEARCH = "http://mp3.easou.com/inter.e";

	/** 升级接口，分渠道更新 */
	public static final String UPDATE = "http://mapp.easou.com/version.xml";

	/** 意见返回接口 */
	public static final String EASOU_MUSIC_FEEDBACK = "http://fk.easou.com/feedback/api/post";

//	/** 获取资源新链接 */
//	public static final String DOWNLOAD = "http://service.mp3.easou.com:82/aai.e";

	public static String BANNER = "http://service.mp3.easou.com:82/aai.e?type=cb&ver=4&esid=&im=&cid=&vs=2&encrypt=ENCRYPT_1_PARAMS_";
	
	public static String RECOMMOND = "http://service.mp3.easou.com:82/aai.e?type=top&id=1203&size=20&page=1&ver=4&esid=&im=&cid=&ma=&vs=2&encrypt=ENCRYPT_1_PARAMS_";
	
	public static final String RECOMMEND_URL = "SUNSHINE_RECOMMEND_URL";
	
	public static final String CHART_URL = "SUNSHINE_CHART_URL";
	
	public static final String OMNIBUS_URL = "SUNSHINE_OMNIBUS_URL";
	
	public static final String BAOYUE_URL_1 = "SUNSHINE_BAOYUE_URL1";
	public static final String BAOYUE_URL_2 = "SUNSHINE_BAOYUE_URL2";
	
	public static final String SEARCH_MUSIC_KEY_URL = "SUNSHINE_SEARCH_MUSIC_KEY_URL";
	
	public static final String CHART_MAPPING_CACHE_KEY = "SUNSHINE_CHART_MAPPING_CACHE_KEY";	
	public static final String CURRENT_CHART_MAPPING_CACHE_KEY = "SUNSHINE_CURRENT_CHART_MAPPING_CACHE_KEY";	
	public static final String HASH_NEXT_CHART_MAPPING_CACHE_KEY = "SUNSHINE_HASH_NEXT_CHART_MAPPING_CACHE_KEY";	
}
