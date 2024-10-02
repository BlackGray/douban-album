package cn.blackgray.douban.album.download.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.blackgray.douban.album.download.service.download.DownloadFailManager;

public class TestFailFileDocCreate {
	
	public static void main(String[] args) {
		
	
		List<String> albumPathList = new ArrayList<String>();
		albumPathList.add("/Users/blackgray/Documents/temp/test-douban");
		
		Map<String, String> failFileMap = new TreeMap<String, String>();
		failFileMap.put("https://img1.doubanio.com/view/photo/l/public/p1932868158.jpg", "/Users/blackgray/Documents/temp/test-douban");
		failFileMap.put("https://img1.doubanio.com/view/photo/raw/public/p1932870238.jpg", "/Users/blackgray/Documents/temp/test-douban/raw");
		
		DownloadFailManager.createAlbumFailFileDoc(albumPathList, failFileMap);
		
		
	}

}
