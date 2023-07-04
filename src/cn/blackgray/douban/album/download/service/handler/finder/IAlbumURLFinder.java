package cn.blackgray.douban.album.download.service.handler.finder;

import java.util.List;

/**
 * 相册URL获取器接口 - 用于获取相册地址（如根据用户首页、活动页首页等获取对应下属相册地址）
 * @author BlackGray
 */
public interface IAlbumURLFinder {
	

	/**
	 * 获取相册URL正则
	 * @return
	 */
	public String getURLRegex();
	
	/**
	 * 根据正则获取相册URL
	 * @param url
	 * @return
	 */
	public List<String> findAlbumURL(String url);
	
	/**
	 * 获取相册地址失败提醒消息
	 * 部分相册随豆瓣改版影响，可能无法访问，暂时保留相关查询类
	 * 但此方法若返回值不为null，认为无法解析获取，并在界面显示提醒文字
	 * @return
	 */
	public String getFindFailMsg();

}
