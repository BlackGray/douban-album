package cn.blackgray.douban.album.download.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TestCheckExists {
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 1000; i++) {
			try {
				URL url = new URL("http://img3.douban.com/view/photo/photo/public/p1601592092.jpg");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				HttpURLConnection.setFollowRedirects(false);
				
				 /* ���� URL ����ķ����� GET POST HEAD OPTIONS PUT DELETE TRACE ���Ϸ���֮һ�ǺϷ��ģ�����ȡ����Э������ơ�*/
				conn.setRequestMethod("HEAD");;
				System.out.println(i + " = " + conn.getResponseCode());
				//System.out.println(HttpURLConnection.HTTP_OK);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
