package cn.blackgray.douban.album.download.service.handler.finder.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;

/**
 * 相册URL获取器实现 - 根据相册列表获取相册
 */
public class AlbumListFinder implements IAlbumURLFinder{

	private static final int PAGE_SIZE_ALBUM = 16;			//相册分页大小(一页16个相册)
	

	@Override
	public List<String> findAlbumURL(String albumListURL) {
		if (albumListURL.endsWith("/")) {
			albumListURL = albumListURL.substring(0,albumListURL.length() - 1);
		}
		Console.print("扫描相册列表首页：" + albumListURL);
		//		http://www.douban.com/people/BlackGray/photos
		//		http://www.douban.com/photos/album/xxxxx
		//【得到所有相册分页（每页16个相册）】
		List<String> pageURLList = new ArrayList<String>();
		String source = URLUtils.readSource(albumListURL);
		String regex = albumListURL + "\\?\\w+=\\d+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		//查找出页面中所有分页连接
		int maxStartNum = 0;
		while (m.find()) {
			String url = m.group();
			int num = Integer.parseInt(url.substring(url.lastIndexOf("=") + 1));
			maxStartNum = num > maxStartNum ? num : maxStartNum;
		}
		//根据最大起始数字，生成页面地址
		for (int i = 0; i <= maxStartNum; i += PAGE_SIZE_ALBUM) {
			String u = albumListURL + "?start=" + i;
			pageURLList.add(u);
			Console.print("获取相册分页地址：" + u);
		}

		//【得到所有相册】
		Set<String> albumURLSet = new TreeSet<String>();
		for (int i = 0; i < pageURLList.size(); i++) {
			source = URLUtils.readSource(pageURLList.get(i));
			String albumRegex = "(http|https)://www.douban.com/photos/album/\\d+";
			Pattern pattern = Pattern.compile(albumRegex);
			Matcher matcher = pattern.matcher(source);
			while (matcher.find()) {
				String url = matcher.group();
				if (!url.endsWith("/")) {
					url += "/";
				}
				albumURLSet.add(url);
			}
		}
		return new ArrayList<String>(albumURLSet);
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://www.douban.com/people/\\w+/photos/";
	}

	@Override
	public String getFindFailMsg() {
		return "相册获取失败，因豆瓣改版“用户相册首页”只支持登录后访问，程序无权限。";
	}

}
