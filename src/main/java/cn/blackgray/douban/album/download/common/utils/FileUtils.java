package cn.blackgray.douban.album.download.common.utils;

import java.io.File;
import java.io.FilenameFilter;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.service.download.DownloadFailManager;

/**
 * 目录、文件工具类
 * @author BlackGray
 */
public class FileUtils {

	/**
	 * 获取相册目录
	 * @param album
	 * @return
	 */
	private static File getDir(Album album){
		//判断目录下是否存在相册目录，如果已经存在，更新目录名称，如果不存在，创建新目录
		File parentDir = new File(Common.PATH_DOWNLOAD);
		File[] files = parentDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.startsWith(album.getName())) {
					return true;
				}else{
					return false;
				}
			}
		});
		if (files.length != 0) {
			return files[0];
		}else{
			return null;
		}
	}

	/**
	 * 生成相册目录
	 */
	public static void createDir(Album album){
		File dir = getDir(album);
		if (dir != null) {
			//更新目录名称
			String newName = dir.getParent() + File.separator + dir.getName().replaceAll("\\(\\d+\\)", "").trim();
			Console.print("相册已存在，更新目录：" + dir.getAbsolutePath() + " -> " + newName);
			File newDir = new File(newName);
			boolean flag = dir.renameTo(newDir);
			if (flag) {
				dir = newDir;				
			}
			album.setUpdate(true);
			//删除早期下载失败记录文档
			DownloadFailManager.deleteAlbumFailFileDoc(newDir.getAbsolutePath());
		}else{
			String path = Common.PATH_DOWNLOAD + File.separator + album.getName().trim(); 
			dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
				Console.print("新建目录：" + path);
			}
		}
		album.setPath(dir.getAbsolutePath());
	}
	

	/**
	 * 删除图片文件 - 用于下载失败时，删除不完整图片文件
	 * @param url
	 * @param filePath
	 */
	public static void deleteImageFile(String url,String filePath) {
		String fileName = url.substring(url.lastIndexOf('/'));
		File file = new File(filePath + File.separatorChar + fileName);
		if(file.exists()) {
			file.delete();
		}
	}



}
