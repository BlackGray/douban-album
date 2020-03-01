package cn.blackgray.douban.album.download.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class TetsInputStream {

	public static void main(String[] args) throws IOException {

		for (int i = 0; i < 10; i++) {
			String url = "http://img3.douban.com/icon/ul37817084-33.jpg";
			URL image = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) image.openConnection();
			conn.setConnectTimeout(10*1000);	//设置连接超时
			conn.setReadTimeout(10*1000);		//设置读取超时
			conn.setDoInput(true);				//默认为true
			conn.connect();
			InputStream in = conn.getInputStream();

			//执行下载
			File file = new File("D:/TEST/test" + (i+1) + ".jpg");
			BufferedInputStream inputStream = new BufferedInputStream(in);
			System.out.println("xxxxxxxxx" + in.getClass());
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
			byte[] data = new byte[1024];
			int n = 0;
			int total = 0;
			while ((n = inputStream.read(data)) != -1) {
				outputStream.write(data,0,n);
				total += n;
				System.out.println("i:" + (i+1) + "\tn:" + n + "\tdata:" + Arrays.toString(data));
			}
			System.out.println("i:" + (i+1) + "\ttotal:" + total);
			System.out.println("i:" + (i+1) + "\tFINISH");
			outputStream.flush();
			in.close();
			inputStream.close();
			outputStream.close();
			conn.disconnect();
		}

	}

}
