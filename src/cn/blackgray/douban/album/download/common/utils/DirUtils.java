package cn.blackgray.douban.album.download.common.utils;

import java.io.File;
import java.io.FilenameFilter;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.model.Album;

/**
 * 存储目录生成、获取工具类
 * @author BlackGray
 */
public class DirUtils {

	private static File getDir(final Album album){
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
	 * 生成保存目录
	 */
	public static void createDir(final Album album){
		//下载目录
		File dir = getDir(album);
		if (dir != null) {
			String newName = dir.getParent() + File.separator + dir.getName().replaceAll("\\(\\d+\\)", "").trim();
			Console.print("相册已存在，更新目录：" + dir.getAbsolutePath() + " -> " + newName);
			File newDir = new File(newName);
			boolean flag = dir.renameTo(newDir);
			if (flag) {
				dir = newDir;				
			}
			album.setUpdate(true);
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
	
}
