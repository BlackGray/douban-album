package cn.blackgray.douban.album.download.test;

import cn.blackgray.douban.album.download.common.utils.URLUtils;

public class TestGetSource {
	
	public static void main(String[] args) {
		String source = URLUtils.readSource("http://www.douban.com/photos/album/51681522/");
		System.out.println(source);
	}

}
