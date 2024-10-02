package cn.blackgray.douban.album.download.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestCelebrityRegex {
	
	public static void main(String[] args) {
		String url = "http://movie.douban.com/celebrity/1040543/photos/";
		String regex = url + "\\?(\\w+=\\w+&*(amp;)*)+";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher("http://movie.douban.com/celebrity/1040543/photos/?type=C&start=0&sortby=all&size=a&subtype=a http://movie.douban.com/celebrity/1040543/photos/?type=C&amp;start=0&amp;sortby=all&amp;size=a&amp;subtype=a");
		while (m.find()) {
			System.out.println(m.group());
		}
	}

}
