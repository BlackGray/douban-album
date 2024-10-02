package cn.blackgray.douban.album.download.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import cn.blackgray.douban.album.download.common.utils.URLUtils;

public class TestImageExists {
	
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException {
		boolean i = URLUtils.exists("http://img4.douban.com/view/photo/photo/public/p2112306858.jpg");
		System.out.println(i);
		System.out.println("==========");
		i = URLUtils.exists("http://img3.douban.com/view/photo/photo/public/p1215810444.jpg");
		System.out.println(i);
		
		
	}

}
