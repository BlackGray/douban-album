package cn.blackgray.douban.album.download.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.blackgray.douban.album.download.service.download.DownloadProcessing;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.handler.PageAnalyzer;

/**
 * ���MODEL
 * @author BlackGray
 */
public class Album {
	
	//1.��ͼƬ - �ļ���(���·����ȡ������Ҫ·��)
	//2.����ע - ��ע.txt
	//3.��������� - �ļ�������
	//4.������ַ - ��ע.txt��һ��
	//5.�ĵ�����ʱ�� - ����ִ��ʱ��
	
//	public static final int TYPE_NOMAL = 1;
//	public static final int TYPE_DOUBAN_ALBUM = 2;
//	public static final int TYPE_DOUBAN_ONLINE = 3;
//	public static final int TYPE_DOUBAN_SITE = 4;
//	public static final int TYPE_DOUBAN_CELEBRITY = 5;
	
	private String name;			//�������
	private String url;				//����ַ
	private Date date;				//����
	private String path;			//����·��
	private String charset;			//�ַ���
	private String desc;			//�������
//	private int type = TYPE_NOMAL;	//�������
	
	private boolean update = false;	//�Ƿ�Ϊ����
	
	private AlbumHandler albumHandler;	//��ᴦ����
	
	//��Ƭ����
	private List<BGImage> photosList = new ArrayList<BGImage>();
	//ҳ�漯�� - ÿ��ҳ���¶�����Ƭ
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
//	 * ����������
//	 * @param url
//	 * @return
//	 */
//	public static int checkType(String url) {
//		//�����������
//		if (url.matches(DownloadService.REGEX_DOUBAN_ALBUM)) {
//			return TYPE_DOUBAN_ALBUM;
//		}
//		//��������
//		if (url.matches(DownloadService.REGEX_DOUBAN_ONLINE_ALBUM)) {
//			return TYPE_DOUBAN_ONLINE;
//		}
//		//����Сվ
//		if (url.matches(DownloadService.REGEX_DOUBAN_SITE) || url.matches(DownloadService.REGEX_DOUBAN_SITE_NEW)) {
//			return TYPE_DOUBAN_SITE;
//		}
//		//��ͨ����
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
	
	/**
	 * ����
	 */
	public void download(){
		//�������ģ��
		//����ģ���������
		DownloadProcessing.downloadAlbum(this);
		
	};
	
	/**
	 * ���������ĵ�
	 * @param imageAndDescMap
	 */
	public void createDescDoc(){
		this.albumHandler.createDescDoc(this);
	}
	
	/**
	 * ��ʼ�����
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
		
		//����ʼ�������Ϣ��
		//URL
		this.setUrl(this.albumHandler.getAlbumURL());
		//����ҳ��
		List<String> pageURLLsit = PageAnalyzer.findPageURL(albumHandler);
		this.setPageURLLsit(pageURLLsit);
		//�������
		String name = PageAnalyzer.findAlbumName().trim();
		this.setName(albumHandler.albumNameProcess(name));
		//�������
		String desc = PageAnalyzer.findAlbumDesc(albumHandler);
		if (desc != null) {
			this.setDesc(desc.trim());
		}
		//��������
		this.setDate(new Date());
		
	}

	@Override
	public String toString() {
		return "Album [name=" + name + ", url=" + url + ", date=" + date
				+ ", path=" + path + ", charset=" + charset 
				+ ", update=" + update + ", photosList=" + photosList
				+ ", pageURLLsit=" + pageURLLsit + "]";
	}
	
}
