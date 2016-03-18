package cn.blackgray.douban.album.download.service.handler.finder;

import java.util.List;

/**
 * ���URL��ȡ���ӿ� - ���ڻ�ȡ����ַ��������û���ҳ���ҳ��ҳ�Ȼ�ȡ��Ӧ��������ַ��
 * @author BlackGray
 */
public interface IAlbumURLFinder {
	

	/**
	 * ��ȡ���URL����
	 * @return
	 */
	public String getURLRegex();
	
	/**
	 * ���������ȡ���URL
	 * @param url
	 * @return
	 */
	public List<String> findAlbumURL(String url);

}
