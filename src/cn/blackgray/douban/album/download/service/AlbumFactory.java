package cn.blackgray.douban.album.download.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.handler.AlbumHandlerFactory;

/**
 * 相册工厂
 * @author BlackGray
 */
public class AlbumFactory {
	
	/**
	 * 根据地址获取相册
	 * @param url
	 * @return
	 */
	public static List<Album> getFromURL(String url){
		List<Album> albums = new ArrayList<Album>();
		//根据地址获取处理器
		List<AlbumHandler> albumHandlers = AlbumHandlerFactory.getHandler(url);
		for (AlbumHandler albumHandler : albumHandlers) {
			Album album = new Album();
			album.setAlbumHandler(albumHandler);
			album.setUrl(albumHandler.getAlbumURL());
			albums.add(album);
		}
		return albums;
	}
	
	/**
	 * 根据本地文件获取相册
	 * @return
	 * @throws IOException 
	 */
	public static Album getFromLocalFile(String path) throws IOException{
		Album album = new Album();
		File descFile = new File(path + File.separator + Common.DEFAULT_DOC_NAME);
		String url = "";
		String charset = "GBK";
		String albumDesc = null;
		Date downloadTime = null;
		List<BGImage> photosList = new ArrayList<BGImage>();
		final List<String> imageNameList = new ArrayList<String>();
		//【1】如果文件存在，获取信息（相册地址，照片地址，描述）
		if (descFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(descFile));
			String str;
			//获取相册信息
			if ((str = reader.readLine()) != null) {
				String[] strArray = str.trim().split(" ",4);
				if (strArray.length == 1) {
					url = strArray[0];
				}
				if (strArray.length == 2) {
					charset = strArray[0];
					url = strArray[1];
				}
				if (strArray.length == 3) {
					charset = strArray[0];
					url = strArray[1];
					downloadTime = new Date(Long.valueOf(strArray[2]));
				}
				if (strArray.length == 4) {
					charset = strArray[0];
					url = strArray[1];
					downloadTime = new Date(Long.valueOf(strArray[2]));
					albumDesc = strArray[3];
				}
				List<AlbumHandler> handlerList = AlbumHandlerFactory.getHandler(url,false);
				if (handlerList != null && handlerList.size() != 0) {
					album.setAlbumHandler(handlerList.get(0));						
				}
			}
			//获取图片信息
			List<BGImage> bgImages = album.getAlbumHandler().getBGImageFromDescDoc(descFile);
			photosList.addAll(bgImages);
			for (BGImage bgImage : bgImages) {
				imageNameList.add(bgImage.getName());
			}
			reader.close();
		}
		
		//【2】添加文档中没有，但目录下有的图片
		File dir = new File(path);
		File[] images = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				if (!imageNameList.contains(file.getName())) {
					if (file.isFile()) {
						String name = file.getName();
						return name.substring(name.lastIndexOf(".") + 1).matches("(" + Common.IMAGE_TYPE + ")");
					}else{
						return false;
					}
				}else{
					return false;
				}
			}
		});
		String imagePath;
		List<File> list = new ArrayList<File>(Arrays.asList(images));
		Collections.sort(list,new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return (int) (o1.lastModified() - o2.lastModified());
			}
		});
		for (int i = 0; i < list.size(); i++) {
			File imageFile = list.get(i);
			imagePath = imageFile.getAbsolutePath().replaceAll("\\\\", "/");
			BGImage bgImage = new BGImage("O-" + (i + 1), imagePath, imageFile.getName());
			photosList.add(bgImage);
		}
		path = path.replaceAll("\\\\", "/");
		album.setName(path.substring(path.lastIndexOf("/") + 1));
		album.setUrl(url);
		album.setCharset(charset);
		album.setPath(path);
		album.setPhotosList(photosList);
		album.setDesc(albumDesc);
		album.setDate(downloadTime);
		return album;
	}
	
}
