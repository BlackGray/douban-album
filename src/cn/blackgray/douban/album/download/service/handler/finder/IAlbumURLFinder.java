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

}
