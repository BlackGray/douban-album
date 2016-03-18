package cn.blackgray.douban.album.download.common;

import cn.blackgray.douban.album.download.common.utils.URLUtils;



/**
 * �汾�����
 * ���ڼ������������Ƿ�Ϊ���°汾
 * @author BlackGray
 *
 */
public class VersionChecker {
	
	/**
	 * ��ȡ�汾��Ϣ
	 * @return
	 */
	public static boolean haveNewVersion(){
		String note = URLUtils.readSource(Common.URL_HELP);
		int begin = note.indexOf("���°汾��");
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
