package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;

import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;


/**
 * 相册URL获取器实现 - 根据影人首页获取相册
 */
public class CelebrityIndexFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
//		http://movie.douban.com/celebrity/1048027/
//		http://movie.douban.com/celebrity/1048027/photos/
		List<String> list = new ArrayList<String>();
		list.add(url + "photos/");
		return list;
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://movie.douban.com/celebrity/\\d+/";
	}

	@Override
	public String getFindFailMsg() {
		return null;
	}

}

