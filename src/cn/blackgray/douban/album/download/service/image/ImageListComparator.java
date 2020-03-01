package cn.blackgray.douban.album.download.service.image;

import java.util.Comparator;

import cn.blackgray.douban.album.download.common.Common;

/**
 * 图片对比排序
 * @author BlackGray
 */
public class ImageListComparator implements Comparator<String>{

	@Override
	public int compare(String p1, String p2) {
		String regex = "p\\d+.(" + Common.IMAGE_TYPE + ")";
		p1 = p1.substring(p1.lastIndexOf("/") + 1);		//	p1473298817.jpg
		p2 = p2.substring(p2.lastIndexOf("/") + 1);
		if (p1.matches(regex) && p2.matches(regex)) {
			long i1 = Long.parseLong(p1.substring(1,p1.lastIndexOf(".")));	//1473298817
			long i2 = Long.parseLong(p2.substring(1,p2.lastIndexOf(".")));
			return (int) (i2 - i1);
		}else{
			return p1.compareTo(p2);
		}
	}

}
