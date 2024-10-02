package cn.blackgray.douban.album.download.service.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.image.ImageListComparator;

/**
 * 相册处理器父类 - 子类用于处理并获取相册下照片信息（地址、描述、评论数等）
 * @author BlackGray
 */
public abstract class AlbumHandler{

	protected String albumURL;
	
	public AlbumHandler() {
		super();
	}

	public AlbumHandler(String albumURL) {
		super();
		this.albumURL = albumURL;
	}
	
	public String getAlbumURL() {
		return albumURL;
	}
	
	/**
	 * 获取相册描述
	 */
	public abstract String getAlbumDesc(String source);
	
	
	public void setAlbumURL(String albumURL) {
		boolean isRemoveURLPara = removeURLParameter();
		if (isRemoveURLPara && (albumURL.indexOf("?") > 0)) {
			this.albumURL = albumURL.substring(0, albumURL.indexOf("?"));				
		}else{
			this.albumURL = albumURL;			
		}
	}

	/**
	 * 获取页面照片数大小
	 */
	public abstract Integer getPageSize();
	
	
	/**
	 * 获取分页标签
	 * 豆瓣为start
	 */
	public abstract String getPageTag();
	
	/**
	 * 相册名称处理
	 * @param name
	 * @return
	 */
	public String albumNameProcess(String name){
		return name;
	}
	
	/**
	 * 获取相册URL正则
	 * @return
	 */
	public abstract String getURLRegex();
	
	/**
	 * 获取相册分页正则
	 * @return
	 */
	public abstract String getPageRegex();
	
	/**
	 * 图片名称正则
	 * 可提高准确率
	 * @return
	 */
	public abstract String getImageNameRegex();
	
	/**
	 * 是否有大图
	 * @return
	 */
	public boolean hasRaw(Album album){
		return false;
	}
	
	/**
	 * 获取大图地址
	 * @return
	 */
	public abstract String getRawURL(String imageURL);
	
	/**
	 * 获取评论地址
	 * @param album
	 * @param image
	 * @return
	 */
	public abstract String getCommentURL(Album album, BGImage image);
	
	
	/**
	 * 删除URL中的参数
	 * @return
	 */
	public boolean removeURLParameter(){
		return false;
	}
	
	
	/**
	 * 根据源码和图片路径分析并创建图片对象
	 * @param source
	 * @param imageURL
	 * @param map 
	 * @param imageURL2 
	 * @return
	 */
	public abstract void createBGImage(Album album, String source,String pageURL, String imageURL, Map<String, BGImage> map);
	
	/**
	 * 大图地址处理
	 * @return
	 */
	public boolean checkBGImage(BGImage bgImage){
		return true;
	}

	/**
	 * 创建描述文档
	 * @param album
	 */
	public void createDescDoc(Album album) {
		List<BGImage> imageList = album.getPhotosList();
		Map<String,BGImage> map = new HashMap<String,BGImage>();
		for (BGImage bgImage : imageList) {
			map.put(bgImage.getUrl(), bgImage);
		}
		List<String> keyList = new ArrayList<String>(map.keySet());
		//排序
		Collections.sort(keyList,new ImageListComparator());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(album.getPath() + "/" + Common.DEFAULT_DOC_NAME));
			//输出相册地址
			if (album.getDesc() == null) {
				bw.write(URLUtils.charset + " " + album.getUrl() + " " + album.getDate().getTime() + " -");
			}else{
				bw.write(URLUtils.charset + " " + album.getUrl() + " " + album.getDate().getTime() + " " + album.getDesc());
			}
			bw.newLine();
			//输出照片地址和描述，格式：序号 + 评论数 + 照片地址 + 描述
			for (int i = 0; i < keyList.size(); i++) {
				BGImage bgImage = map.get(keyList.get(i));
				Integer commentTotal = bgImage.getCommentTotal();
				String commentTotalStr = commentTotal==null?"-":String.valueOf(commentTotal);
				bw.write((i + 1) + " " + keyList.get(i) + " " + commentTotalStr + " " + bgImage.getDesc());
				bw.newLine();
			}
			bw.flush();
			bw.close();
			Console.print("生成描述文档：成功");
		} catch (IOException e) {
			Console.print("生成描述文档：失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 从描述文档获取图片集合
	 * @param descFile
	 * @return
	 * @throws IOException 
	 */
	public List<BGImage> getBGImageFromDescDoc(File descFile) throws IOException{
		List<BGImage> list = new ArrayList<BGImage>();
		if (descFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(descFile));
			String str;
			int line = 0;
			while ((str = reader.readLine()) != null) {
				if (line == 0) {
					line++;
				}else{
					//获取照片地址&描述信息
					String[] info = str.split(" ",4);
					//info[0],info[1],info[2],info[3]分别为照片编号、原始URL地址、评论数、照片描述
					BGImage bgImage = new BGImage(info[0],info[1],info[3]);
					//评论数
					if (!info[2].equals("-")) {
						bgImage.setCommentTotal(Integer.valueOf(info[2]));
					}
					list.add(bgImage);
				}
			}
			reader.close();
		}
		return list;
	}
	
}
