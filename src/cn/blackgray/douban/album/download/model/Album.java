package cn.blackgray.douban.album.download.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.blackgray.douban.album.download.service.download.DownloadProcessing;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.handler.PageAnalyzer;

/**
 * 相册MODEL
 * @author BlackGray
 */
public class Album {
	
	//1.※图片 - 文件名(相对路径获取，不需要路径)
	//2.※备注 - 备注.txt
	//3.※相册名称 - 文件夹名称
	//4.※相册地址 - 备注.txt第一行
	//5.文档生成时间 - 程序执行时间
	
//	public static final int TYPE_NOMAL = 1;
//	public static final int TYPE_DOUBAN_ALBUM = 2;
//	public static final int TYPE_DOUBAN_ONLINE = 3;
//	public static final int TYPE_DOUBAN_SITE = 4;
//	public static final int TYPE_DOUBAN_CELEBRITY = 5;
	
	private String name;			//相册名称
	private String url;				//相册地址
	private Date date;				//日期
	private String path;			//储存路径
	private String charset;			//字符集
	private String desc;			//相册描述
//	private int type = TYPE_NOMAL;	//相册类型
	
	private boolean update = false;	//是否为更新
	
	private AlbumHandler albumHandler;	//相册处理器
	
	private boolean isVisibleToSelf;	//是否仅对自己可见
	private boolean isVisibleToFriend;	//是否仅对朋友可见
	
	//照片集合
	private List<BGImage> photosList = new ArrayList<BGImage>();
	//页面集合 - 每个页面下多张照片
	private List<String> pageURLLsit = new ArrayList<String>();
	
	public Album() {
		super();
	}
	
//	public Album(String url, List<BGImage> photosList, String path, String charset) {
//		super();
//		this.name = path.substring(path.lastIndexOf("/") + 1);
//		this.date = new Date();
//		this.path = path;
//		this.charset = charset;
//		this.photosList = photosList;
//		if (url.endsWith("/")) {
//			url = url.substring(0,url.lastIndexOf("/"));
//		}
//		this.url = url;
//	}
	
//	/**
//	 * 检查相册类型
//	 * @param url
//	 * @return
//	 */
//	public static int checkType(String url) {
//		//豆瓣相册下载
//		if (url.matches(DownloadService.REGEX_DOUBAN_ALBUM)) {
//			return TYPE_DOUBAN_ALBUM;
//		}
//		//豆瓣活动下载
//		if (url.matches(DownloadService.REGEX_DOUBAN_ONLINE_ALBUM)) {
//			return TYPE_DOUBAN_ONLINE;
//		}
//		//豆瓣小站
//		if (url.matches(DownloadService.REGEX_DOUBAN_SITE) || url.matches(DownloadService.REGEX_DOUBAN_SITE_NEW)) {
//			return TYPE_DOUBAN_SITE;
//		}
//		//普通下载
//		return TYPE_NOMAL;
//	}

	public AlbumHandler getAlbumHandler() {
		return albumHandler;
	}



	public void setAlbumHandler(AlbumHandler albumHandler) {
		this.albumHandler = albumHandler;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}
	

//	public int getType() {
//		return type;
//	}
//	
//	public void setType(int type) {
//		this.type = type;
//	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCharset() {
		return charset;
	}


	public void setCharset(String charset) {
		this.charset = charset;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public Date getDate() {
		return date;
	}

	public List<BGImage> getPhotosList() {
		return photosList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setPhotosList(List<BGImage> photosList) {
		this.photosList = photosList;
	}
	
	public List<String> getPageURLLsit() {
		return pageURLLsit;
	}

	public void setPageURLLsit(List<String> pageURLLsit) {
		this.pageURLLsit = pageURLLsit;
	}
	
	
	public boolean getIsVisibleToSelf() {
		return isVisibleToSelf;
	}

	public void setIsVisibleToSelf(boolean isVisibleToSelf) {
		this.isVisibleToSelf = isVisibleToSelf;
	}

	
	
	public boolean getIsVisibleToFriend() {
		return isVisibleToFriend;
	}

	public void setIsVisibleToFriend(boolean isVisibleToFriend) {
		this.isVisibleToFriend = isVisibleToFriend;
	}

	/**
	 * 下载
	 */
	public void download(){
		//创建相册模型
		//根据模型下载相册
		DownloadProcessing.downloadAlbum(this);
		
	};
	
	/**
	 * 生成描述文档
	 * @param imageAndDescMap
	 */
	public void createDescDoc(){
		this.albumHandler.createDescDoc(this);
	}
	
	/**
	 * 初始化相册
	 */
	public void init(){
		
//		this.name = path.substring(path.lastIndexOf("/") + 1);
//		this.date = new Date();
//		this.path = path;
//		this.charset = charset;
//		this.photosList = photosList;
//		if (url.endsWith("/")) {
//			url = url.substring(0,url.lastIndexOf("/"));
//		}
//		this.url = url;
		
		//【初始化相册信息】
		//URL
		this.setUrl(this.albumHandler.getAlbumURL());
		//所有页面
		List<String> pageURLLsit = PageAnalyzer.findPageURL(albumHandler);
		this.setPageURLLsit(pageURLLsit);
		//相册名称
		String name = PageAnalyzer.findAlbumName().trim();
		this.setName(albumHandler.albumNameProcess(name));
		//相册描述
		String desc = PageAnalyzer.findAlbumDesc(albumHandler);
		if (desc != null) {
			this.setDesc(desc.trim());
		}
		//下载日期
		this.setDate(new Date());
		
	}

	@Override
	public String toString() {
		return "Album [name=" + name + ", url=" + url + ", date=" + date
				+ ", path=" + path + ", charset=" + charset 
				+ ", update=" + update + ", photosList=" + photosList
				+ ", pageURLLsit=" + pageURLLsit + ", isVisibleToSelf=" + isVisibleToSelf
				 + ", isVisibleToFriend=" + isVisibleToFriend + "]";
	}
	
}
