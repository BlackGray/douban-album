package cn.blackgray.douban.album.download.test;

import cn.blackgray.douban.album.download.service.image.ImageInfo;
import cn.blackgray.douban.album.download.service.image.ImageUtils;


public class TestGetImageSize {
	
	public static void main(String[] args) {
		ImageInfo rawInfo = ImageUtils.getImageSize("/Users/blackgray/Downloads/p2626439856.jpg");
		System.out.println(rawInfo);
		
	}

}
