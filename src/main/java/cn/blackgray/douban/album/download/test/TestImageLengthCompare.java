package cn.blackgray.douban.album.download.test;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 网络资源大小与本地文件大小对比测试
 * @author blackgray
 * @createTime 2022-10-24 00:34:46
 */
public class TestImageLengthCompare {


	/**
	 * 获取网络资源图片大小
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static long getNetworkResourceContentLength(String url) throws MalformedURLException, ProtocolException, IOException {

		//配置网络资源
		URL image = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) image.openConnection();

		//2016-03-16 如不加referer信息，下载影人相册时，大图监测返回403异常
		conn.setRequestProperty("referer", "https://www.douban.com/");

		conn.setConnectTimeout(10*1000);	//设置连接超时
		conn.setReadTimeout(10*1000);		//设置读取超时
		conn.setDoInput(true);				//默认为true
		conn.connect();
		//		InputStream in = conn.getInputStream();
		//			inputStream = new BufferedInputStream(in);
		//			outputStream = new BufferedOutputStream(new FileOutputStream(file));
		//			byte[] data = new byte[2048];
		//			int n = 0; 
		//			while ((n = inputStream.read(data)) != -1) {
		//				outputStream.write(data,0,n);
		//			}
		//			outputStream.flush();
		//			in.close();
		//			inputStream.close();
		//			outputStream.close();

		conn.disconnect();

		return conn.getContentLength();
	}


	public static long getLocalFileLength(String path)  {
		File file = new File(path);
		return file.length();
	}




	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
		long networkResourceContentLength =  getNetworkResourceContentLength("https://img9.doubanio.com/view/photo/l/public/p2626439856.jpg");
		long localFileLength =  getLocalFileLength("/Users/blackgray/Downloads/p2626439856.jpg");

		System.out.println("networkResourceContentLength = " + networkResourceContentLength + "| localFileLength = " + localFileLength);

	}

}
