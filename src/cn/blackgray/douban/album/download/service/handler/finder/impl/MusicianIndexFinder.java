package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;

import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;


/**
 * 相册URL获取器实现 - 音乐人人首页获取相册
 */
public class MusicianIndexFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
//		https://music.douban.com/musician/100144/
//		https://music.douban.com/musician/100144/photos/
		List<String> list = new ArrayList<String>();
		list.add(url + "photos/");
		return list;
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://music.douban.com/musician/\\d+/";
	}

}

