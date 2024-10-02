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
 * 相册URL获取器实现 - 根据豆列获取相册
 * @author BlackGray
 */
public class AlbumDouListFinder implements IAlbumURLFinder{

//	http://www.douban.com/doulist/3353890/;
//	http://www.douban.com/doulist/3353890/?start=25&filter=undo
	private static final int PAGE_SIZE_ALBUM = 25;			//相册分页大小(一页25个相册)

		
	@Override
	public List<String> findAlbumURL(String url) {
		if (!url.endsWith("/")) {
			url = url + "/";
		}
		Console.print("扫描相册豆列首页：" + url);
		//【得到所有相册分页（每页25个相册）】
		List<String> pageURLList = new ArrayList<String>();
		String source = URLUtils.readSource(url);
		String regex = url + "\\?start=\\d+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		//查找出页面中所有分页连接
		int maxStartNum = 0;
		while (m.find()) {
			String u = m.group();
			int num = Integer.parseInt(u.substring(u.lastIndexOf("=") + 1));
			maxStartNum = num > maxStartNum ? num : maxStartNum;
		}
		//根据最大起始数字，生成页面地址
		for (int i = 0; i <= maxStartNum; i += PAGE_SIZE_ALBUM) {
			String u = url + "?start=" + i;
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
				String u = matcher.group();
				if (!u.endsWith("/")) {
					u += "/";
				}
				albumURLSet.add(u);
			}
		}
		return new ArrayList<String>(albumURLSet);
	}

	@Override
	public String getURLRegex() {
		return "(http|https)://www.douban.com/doulist/\\d+/";
	}

	@Override
	public String getFindFailMsg() {
		return null;
	}
	
}
