package cn.blackgray.douban.album.download.common.utils;

/**
 * 公共工具类
 * @author BlackGray
 * @createTime 2022-10-19 01:18:31
 *
 */
public class CommonUtils {
	
	/**
	 * 判断当前系统是否为MacOS
	 */
	public static boolean isMacOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("mac") >= 0) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断当前系统是否为Windows系统
	 */
	public static boolean isWindows() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.indexOf("windows") >= 0) {
			return true;
		}else {
			return false;
		}
	}

}
