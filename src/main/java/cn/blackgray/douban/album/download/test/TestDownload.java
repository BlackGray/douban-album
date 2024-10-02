package cn.blackgray.douban.album.download.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestDownload {

	public static void main(String[] args) {
		try {
			URL url = new URL("https://img1.doubanio.com/view/photo/l/public/p2257588668.jpg");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			HttpURLConnection.setFollowRedirects(false);
			
			conn.setConnectTimeout(10*1000);	//设置连接超时
			conn.setReadTimeout(10*1000);		//设置读取超时
			conn.setDoInput(true);				//默认为true
			conn.connect();
			System.out.println("【getContentLength】" + conn.getContentLength());
			System.out.println("【getContentType】" + conn.getContentType());
			InputStream in = conn.getInputStream();
			
			
			
			BufferedInputStream inputStream = new BufferedInputStream(in);
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File("/Users/blackgray/Desktop/douban/test.jpg")));
			byte[] data = new byte[1024];
			int n = 0; 
			while ((n = inputStream.read(data)) != -1) {
				outputStream.write(data,0,n);
			}
			outputStream.flush();
			in.close();
			inputStream.close();
			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
