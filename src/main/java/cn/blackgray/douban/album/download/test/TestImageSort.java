package cn.blackgray.douban.album.download.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.blackgray.douban.album.download.service.image.ImageListComparator;

public class TestImageSort {
	
	public static void main(String[] args) {
		
		List<String> imageList = new ArrayList<String>();
		imageList.add("https://img9.doubanio.com/view/photo/l/public/p1154450175.jpg");
		imageList.add("https://img2.doubanio.com/view/photo/l/public/p656889432.jpg");
		imageList.add("https://img1.doubanio.com/view/photo/l/public/p1079652422.jpg");
		
		//排序
		Collections.sort(imageList,new ImageListComparator());
		
		for (int i = 0; i < imageList.size(); i++) {
			System.out.println(imageList.get(i));
		}
		
	}

}
