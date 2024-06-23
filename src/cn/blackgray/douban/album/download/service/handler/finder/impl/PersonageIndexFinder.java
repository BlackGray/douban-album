package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;

import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;


/**
 * 相册URL获取器实现 - 根据人士首页获取相册
 */
public class PersonageIndexFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
//		https://www.douban.com/personage/27246297/
//		https://www.douban.com/personage/27246297/photos/
		List<String> list = new ArrayList<String>();
		list.add(url + "photos/");
		return list;
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://www.douban.com/personage/\\d+/";
	}

	@Override
	public String getFindFailMsg() {
		return null;
	}

}

