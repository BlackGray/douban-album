package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;

/**
 * 相册URL获取器实现 - 根据活动首页获取相册
 */
public class OnlineIndexFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
		//首页 http://www.douban.com/online/11127307/
		//相册 http://www.douban.com/online/11127307/album/72416214/
		//相册 http://www.douban.com/online/11023488/album/64765014/?start=0
		//照片 http://www.douban.com/online/11127307/photo/1573338563/
		List<String> list = new ArrayList<String>();
		String source = URLUtils.readSource(url);
		String regex = url + "album/\\d+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		if (m.find()) {
			String str = m.group();
			if (str.endsWith("/")) {
				list.add(str);
			}else{
				list.add(str + "/");
			}
		}
		return list;

	}

	@Override
	public String getURLRegex() {
		return "(http|https)://www.douban.com/online/\\d+/";
	}

	@Override
	public String getFindFailMsg() {
		return null;
	}

}
