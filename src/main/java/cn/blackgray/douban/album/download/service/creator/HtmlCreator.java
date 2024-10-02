package cn.blackgray.douban.album.download.service.creator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.HTMLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.AlbumFactory;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.image.ImageInfo;
import cn.blackgray.douban.album.download.service.image.ImageUtils;

/**
 * HTML页面生成工具类
 * @author BlackGray
 */
public class HtmlCreator {

	public static final String DESC_FRONT_COVER = "【相册封面】";
	public static final String DESC_DEFAULT = "-";

	/**
	 * 生成相册HTML页面 - 根据相册获取时生成
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static boolean createAlbumHTML(String albumPath) throws IOException {

		File albumDir = new File(albumPath);
		if (!albumDir.exists()) {
			return false;
		}
		//验证目录下是否有文件
		File[] imageFiles = albumDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().substring(f.getName().lastIndexOf(".") + 1).matches("(" + Common.IMAGE_TYPE + ")");
			}
		});
		if (imageFiles.length == 0) {
			return false;
		}

		Album album = null;
		try {
			album = AlbumFactory.getFromLocalFile(albumPath);
		} catch (IOException e) {
			Console.print(e.getMessage());
			Console.print("HTML文档生成失败");
		}
		
		String name = album.getName();
		AlbumHandler albumHandler = album.getAlbumHandler();
		List<BGImage> photosList = album.getPhotosList();
		
		//【拼装页面】
		String page = Common.HTML_TEMPLATE_PAGE;
//		page = page.replace(Common.HTML_TAG_CHARSET, album.getCharset());		//字符集
		page = page.replace(Common.HTML_TAG_TITLE, HTMLUtils.textToHTML(name) + "(" + photosList.size() + ")");	//标题
		page = page.replace(Common.HTML_TAG_NAME, HTMLUtils.textToHTML(name));	//相册名
		page = page.replace(Common.HTML_TAG_URL, album.getUrl());				//相册原始地址
		//相册描述
		if (album.getDesc() == null || album.getDesc().equals("-")) {
			page = page.replace(Common.HTML_TAG_ALBUM_DESC, "");
		}else{
			page = page.replace(Common.HTML_TAG_ALBUM_DESC, HTMLUtils.textToHTML("■相册描述：" + album.getDesc()));	
		}
		//下载时间
		page = page.replace(Common.HTML_TAG_DOWNLOAD_TIME, "■下载时间：" + Common.SIMPLE_DATE_FORMAT.format(album.getDate()));
		//照片数量
		page = page.replace(Common.HTML_TAG_IMAGES_TOTAL, "■照片数量：" + photosList.size());	
		


		//【拼装照片列表】
		Collections.sort(photosList,new Comparator<BGImage>() {
			@Override
			public int compare(BGImage i1, BGImage i2) {
				String number1 = i1.getNumber();
				String number2 = i2.getNumber();
				int id1;
				int id2;
				if (number1.startsWith("O-")) {
					if (number2.startsWith("O-")) {
						//本地文件
						id1 = Integer.parseInt(number1.substring(number1.indexOf("O-") + 2));
						id2 = Integer.parseInt(number2.substring(number2.indexOf("O-") + 2));						
					}else{
						return 1;
					}
				}else{
					if (number2.startsWith("O-")) {
						return -1;					
					}else{
						//网络文件
						id1 = Integer.parseInt(number1);
						id2 = Integer.parseInt(number2);
					}
				};
				return id1 - id2;
			}
		});
		
		//【如果是小站&个人相册，获取raw目录下文件列表】
		Set<String> rawSet = new HashSet<String>();
		if (albumHandler.hasRaw(album)) {
			File dir = new File(album.getPath() + File.separatorChar + "raw");
			if (dir.exists()) {
				for (File file : dir.listFiles()) {
					rawSet.add(file.getName());
				};
			}
		}

		//【拼装JSON】
		StringBuffer images = new StringBuffer("[");
		for (BGImage image : photosList) {
			//获取值
			String desc = image.getDesc();
			if (desc.trim().length() == 0) {
				desc = DESC_DEFAULT;
			}else{
				if (desc.startsWith("※")) {
					desc = DESC_FRONT_COVER + desc.replaceAll("※+", "");
				};
			}
			
			images.append("{");
			//编号
			images.append("'number':'").append(image.getNumber()).append("',");	
			//图片
			images.append("'name':'").append(HTMLUtils.textToJson(image.getName())).append("',");
			//描述
			images.append("'desc':'").append(HTMLUtils.textToJson(desc)).append("',");	
			//照片所有者			
			if (image.getOwnerName() != null) {
				images.append("'ownerName':'").append(HTMLUtils.textToJson(image.getOwnerName())).append("',");
				images.append("'ownerURL':'").append(image.getOwnerURL()).append("',");
			}
			//评论
			String commentURL = albumHandler.getCommentURL(album, image);
			if (commentURL != null) {
				images.append("'commentURL':'").append(commentURL).append("',");	
			}
			//回应数
			Integer commentTotal = image.getCommentTotal();
			if (commentTotal != null) {
				images.append("'commentTotal':'").append(commentTotal).append("',");	
			}
			
			
			
			//【检查raw目录】
			if (albumHandler.hasRaw(album)) {
				if (rawSet.contains(image.getName())) {
					try {
						//判断图片大小，如果大小相同
						ImageInfo imageInfo = ImageUtils.getImageSize(album.getPath() + File.separatorChar + image.getName());
						ImageInfo rawInfo = ImageUtils.getImageSize(album.getPath() + File.separatorChar + Common.DEFAULT_RAW_DIR + File.separatorChar + image.getName());
						if ((imageInfo.getWidth() == rawInfo.getWidth()) && imageInfo.getHeight() == rawInfo.getHeight()) {
							images.append("'raw':'").append(Common.RAW_TYPE_UNCOMPRESSED).append("'");
						}else{
							images.append("'raw':'").append(Common.RAW_TYPE_LARGE).append("'");
						}						
					} catch (Exception e) {
						//如果对比出现异常，默认RAW尺寸更大
						images.append("'raw':'").append(Common.RAW_TYPE_LARGE).append("'");
					}
				};
			}
			
			//去掉多余的逗号
			if (images.substring(images.length() - 1, images.length()).equals(",")) {
				images.delete(images.length() - 1, images.length());				
			}
			
			images.append("},");
		}
		images.delete(images.length() - 1, images.length());
		images.append("]");
		page = page.replace(Common.HTML_TAG_IMAGES, images.toString());
		
		File file = new File(album.getPath() + File.separator + Common.DEFAULT_HTML_NAME);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(page.toString());
		bw.flush();
		bw.close();


		//输出资源文件
		List<String> resourceList = new ArrayList<String>(); 
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/bg.jpg");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/numberBg.png");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/half-l.png");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/half-d.png");
		File dir = new File(album.getPath() + File.separator + Common.DEFAULT_HTML_RESOURCE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();	
		}
		for (String resource : resourceList) {
			InputStream inputStream = Common.class.getResourceAsStream(resource);
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			String path = dir.getAbsolutePath() + File.separator + resource.substring(resource.lastIndexOf("/") + 1);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
			int i;
			while ((i = bis.read()) != -1) {
				bos.write(i);
			}
			bos.flush();
			bos.close();
			bis.close();
		}
		return true;
	}

	//批量生成网页
	public static void createAlbumHTML(List<String> paths) {
		for (String path : paths) {
			try {
				HtmlCreator.createAlbumHTML(path);
			} catch (IOException e) {
				Console.print(e.getMessage());
				Console.print("HTML文档生成失败");
				e.printStackTrace();
			}
		}		
	}

}
