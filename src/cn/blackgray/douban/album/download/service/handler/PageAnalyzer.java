package cn.blackgray.douban.album.download.service.handler;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.HTMLUtils;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;

/**
 * 页面分析器
 * @author BlackGray
 */
public class PageAnalyzer {
	
	private static final String DEFAULT_ALBUM_NAME = "NO-NAME";
	
	private static String source;
	
	
	/**
	 * 获取相册名称
	 * @return
	 */
	public static String findAlbumName() {
		String name;
		//判断标题是否存在
		if (source.indexOf("<title>") != -1) {
			name = source.substring(source.indexOf("<title>") + 7,source.indexOf("</title>")).replace("\\t\\n","").trim();	
		}else{
			return DEFAULT_ALBUM_NAME;
		}
		if (name.length() != 0) {
			//转换HTML中的特殊字符
			name = HTMLUtils.htmlToText(name);
			//去除结尾的点，防止windows文件夹命名错误
			while(name.endsWith(".")){
				name = name.substring(0, name.length() - 1);
			}
			//过滤windows下文件名不能使用的字符，并返回结果
			return name.replaceAll("[\\\\ / : \\* \\? < > \\|]", " ");				
		}else{
			return DEFAULT_ALBUM_NAME;
		}
	}
	
	/**
	 * 获取相册描述
	 * @return
	 */
	public static String findAlbumDesc(AlbumHandler albumHandler) {
		String desc = albumHandler.getAlbumDesc(source);
		if (desc != null) {
			return HTMLUtils.htmlToText(desc);
		}else{
			return null;
		}
	}
	
	
	/**
	 * 根据相册地址，获取所有分页地址
	 * @param albumURL
	 * @return
	 * @throws MalformedURLException
	 */
	public static List<String> findPageURL(AlbumHandler albumHandler) {
		//相册地址
		String albumURL = albumHandler.getAlbumURL();
		//页面集合
		List<String> pageURLList = new ArrayList<String>();
		source = URLUtils.readSource(albumURL);
		String regex = albumHandler.getPageRegex();
		if (regex != null) {
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(source);
			//查找出页面中所有分页连接
			int maxStartNum = 0;
			while (m.find()) {
				String url = m.group();
				int num = 0;
				Pattern tp = Pattern.compile(albumHandler.getPageTag() + "=\\d+");
				Matcher tm = tp.matcher(url);
				if (tm.find()) {
					String pageTagStr = tm.group();
					num = Integer.parseInt(pageTagStr.substring(pageTagStr.lastIndexOf("=") + 1));
				}
				//找到分页值
				maxStartNum = num > maxStartNum ? num : maxStartNum;
			}
			//根据最大起始数字，生成页面地址
			int size = albumHandler.getPageSize();
			for (int i = 0; i <= maxStartNum; i+=size) {
				String pageURL = albumURL;
				if (albumURL.indexOf("?") == -1) {
					pageURL += "?";
				}else{
					pageURL += "&";
				}
				pageURL += albumHandler.getPageTag() + "=" + i; 
				pageURLList.add(pageURL);
			}
		}else{
			pageURLList.add(albumURL);
		}
		
		for (int i = 0; i < pageURLList.size(); i++) {
			Console.print("获取图片地址-页面(" + (i+1) + "/" + pageURLList.size() + ")：" + pageURLList.get(i));
		}
		return pageURLList;
	};
	
	
	/**
	 * 根据分页地址，获取所有图片信息（地址、描述、所有者等）
	 * @param pageURL
	 * @return
	 */
	public static Map<String,BGImage> findImageURLAndDesc(Album album,String pageURL) {
		//		http://www.douban.com/photos/album/65283821/?start=0 - 豆瓣相册单页地址
		//		http://www.douban.com/photos/photo/1424544615/ - 豆瓣图片页地址
		//		http://img3.douban.com/view/photo/photo/public/p1424544615.jpg - 豆瓣图片地址

		Map<String,BGImage> result = new HashMap<String,BGImage>();
		//读取页面源码
		String source = URLUtils.readSource(pageURL);

		//获取单页所有图片地址
		String regex = "(http|https)://(\\w|\\s|\\.|-|_|/)+[\\.](" + Common.IMAGE_TYPE + ")";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		AlbumHandler albumHandler = album.getAlbumHandler();
		while (m.find()) {
			String imageURL = m.group().trim();
			//获取名字
			String imageName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
			//分析并创建图片对象
			String imageNameRegex = albumHandler.getImageNameRegex();
			if (imageNameRegex == null || (imageNameRegex != null && imageName.matches(imageNameRegex))) {
				albumHandler.createBGImage(source, pageURL, imageURL, result);
			}
		}
		
		//去除albumicon图片
		List<String> removeList = new ArrayList<String>();
		for (Entry<String, BGImage> element : result.entrySet()) {
			if(!albumHandler.checkBGImage(element.getValue())){
				removeList.add(element.getKey());
			}
		}
		for (String key : removeList) {
			result.remove(key);
		}
		return result;
	}
	
	
}
