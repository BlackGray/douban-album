package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;

import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;

/**
 * 相册URL获取器实现 - 根据电影首页获取相册
 */
public class MovieIndexFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
		//首页 http://movie.douban.com/subject/3652297/
		//相册 http://movie.douban.com/subject/3652297/photos?type=S
		//相册 http://movie.douban.com/subject/3652297/photos?type=R
		//相册 http://movie.douban.com/subject/3652297/photos?type=W
		List<String> list = new ArrayList<String>();
		list.add(url + "photos?type=S");
		list.add(url + "photos?type=R");
		list.add(url + "photos?type=W");
		return list;

	}

	@Override
	public String getURLRegex() {
		return "(http|https)://movie.douban.com/subject/\\d+/";
	}
	
	public static void main(String[] args) {
		System.out.println(new MovieAlbumListFinder().findAlbumURL("(http|https)://movie.douban.com/subject/3652297/"));;
	}

	@Override
	public String getFindFailMsg() {
		return null;
	}

}
