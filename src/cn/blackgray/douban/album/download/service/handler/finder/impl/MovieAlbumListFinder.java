package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;

import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;

/**
 * ���URL��ȡ��ʵ�� - ���ݵ�Ӱ����б��ȡ���
 */
public class MovieAlbumListFinder implements IAlbumURLFinder{

	@Override
	public List<String> findAlbumURL(String url) {
		//��ҳ http://movie.douban.com/subject/3652297/all_photos
		//��� http://movie.douban.com/subject/3652297/photos?type=S
		//��� http://movie.douban.com/subject/3652297/photos?type=R
		//��� http://movie.douban.com/subject/3652297/photos?type=W
		url = url.substring(0,url.lastIndexOf("all_photos"));
		List<String> list = new ArrayList<String>();
		list.add(url + "photos?type=S");
		list.add(url + "photos?type=R");
		list.add(url + "photos?type=W");
		return list;

	}

	@Override
	public String getURLRegex() {
		return "(http|https)://movie.douban.com/subject/\\d+/all_photos(/)*";
	}
	
	public static void main(String[] args) {
		MovieAlbumListFinder finder = new MovieAlbumListFinder();
		String url = "(http|https)://movie.douban.com/subject/3652297/all_photos/";
		System.out.println(url.matches(finder.getURLRegex()));
		System.out.println(finder.findAlbumURL(url));
	}

}
