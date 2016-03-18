package cn.blackgray.douban.album.download.common.utils;

import java.io.File;
import java.io.FilenameFilter;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.model.Album;

/**
 * �洢Ŀ¼���ɡ���ȡ������
 * @author BlackGray
 */
public class DirUtils {

	private static File getDir(final Album album){
		//�ж�Ŀ¼���Ƿ�������Ŀ¼������Ѿ����ڣ�����Ŀ¼���ƣ���������ڣ�������Ŀ¼
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
	 * ���ɱ���Ŀ¼
	 */
	public static void createDir(final Album album){
		//����Ŀ¼
		File dir = getDir(album);
		if (dir != null) {
			String newName = dir.getParent() + File.separator + dir.getName().replaceAll("\\(\\d+\\)", "").trim();
			Console.print("����Ѵ��ڣ�����Ŀ¼��" + dir.getAbsolutePath() + " -> " + newName);
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
				Console.print("�½�Ŀ¼��" + path);
			}
		}
		album.setPath(dir.getAbsolutePath());
	}
	
}
