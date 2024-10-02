package cn.blackgray.douban.album.download.common;

import cn.blackgray.douban.album.download.common.utils.URLUtils;



/**
 * 版本检查器
 * 用于检查相册下载器是否为最新版本
 * @author BlackGray
 *
 */
public class VersionChecker {
	
	/**
	 * 获取版本信息
	 * @return
	 */
	public static boolean haveNewVersion(){
		String note = URLUtils.readSource(Common.URL_HELP);
		int begin = note.indexOf("最新版本：");
		int end = note.indexOf("<br>", begin);
		String version = note.substring(begin + 5 ,end);
		if (Common.VERSION.equals(version)) {
			return false;
		}else{
			return true;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(haveNewVersion());
	}

}
