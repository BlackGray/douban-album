package cn.blackgray.douban.album.download.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;


/**
 * ������Դ��ع�����
 * @author BlackGray
 */
public class URLUtils {

	
	public static String charset = Common.CHARTSET_UTF8;
	
	
	/**
	 * ��ȡҳ��Դ��
	 * @param url
	 * @return
	 */
	public static String readSource(String url){

		//��ȡҳ��Դ��
		StringBuffer sb = new StringBuffer();
		try {
			URL u = new URL(url);
			//����
//			SocketAddress add = new InetSocketAddress("203.66.187.246", 81);
//			Proxy p = new Proxy(Proxy.Type.HTTP , add);
//			HttpURLConnection connection = (HttpURLConnection) u.openConnection(p);
//			String headerKey = "Proxy-Authorization";  
//			String headerValue = "Basic " + Base64.encode(user+":"+password);  
//			conn.setRequestProperty(headerKey, headerValue);  
			
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
			//Ĭ��UTF-8��ȡ
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),charset));
			String str;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			reader.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String result = sb.toString();
		if (result.trim().length() == 0) {
			Console.print("Դ���ȡʧ�ܣ�" + url);
			return result;
		}else{
			String charsetCheck = charset;
			//�ж��ַ���
			if (result.indexOf(Common.CHARTSET_GBK) != -1) {
				charsetCheck = Common.CHARTSET_GBK;
			}else if(result.indexOf(Common.CHARTSET_GB2312) != -1){
				//GB2312
				charsetCheck = Common.CHARTSET_GB2312;
			}else{
				//utf-8
				charsetCheck = Common.CHARTSET_UTF8;
			}
			if (!charsetCheck.equals(charset)) {
				Console.print("�ַ�����" + charset + " -> " + charsetCheck);
				charset = charsetCheck;
				return readSource(url);
			}else{
				return result;				
			}
		}

	}
	
	
	/**
	 * �ж�URL��Դ�Ƿ����
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static boolean exists(String url) throws MalformedURLException, ProtocolException, IOException{
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		//�����ض��򣬷��򲿷�ͼƬ�޷�����
		HttpURLConnection.setFollowRedirects(true);
		conn.setInstanceFollowRedirects(true);
		/* ���� URL ����ķ����� GET POST HEAD OPTIONS PUT DELETE TRACE ���Ϸ���֮һ�ǺϷ��ģ�����ȡ����Э������ơ�*/
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
		
		//2016-03-16 �粻��referer��Ϣ������Ӱ�����ʱ����ͼ��ⷵ��403�쳣
		conn.setRequestProperty("referer", "https://www.douban.com/");
		
		//=======��Ϣ=======
//		Map<String, List<String>> map = conn.getHeaderFields();
//		for (Entry<String, List<String>> element : map.entrySet()) {
//			System.out.println(element.getKey() + " = " + element.getValue());	
//		}
//		System.out.println("getContentType = " + conn.getContentType());
//		System.out.println("getContentLength = " + conn.getContentLength());
//		System.out.println("getInstanceFollowRedirects = " + conn.getInstanceFollowRedirects());
//		System.out.println("getDefaultUseCaches = " + conn.getDefaultUseCaches());
//		System.out.println("getContentEncoding = " + conn.getContentEncoding());
//		System.out.println("getExpiration = " + conn.getExpiration());
//		System.out.println("getResponseCode = " + conn.getResponseCode());
//		System.out.println("getResponseMessage = " + conn.getResponseMessage());
		//=======/��Ϣ=======
		System.out.println("ResponseCode:" + conn.getResponseCode());
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return true;
		}else{
			return false;
		}
	}
	

	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
//		//��Դ��֤
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/photo/public/p814563030.jpg"));
//		System.out.println("==========");
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/large/public/p814563030.jpg"));
//		System.out.println("==========");
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/large/public/p814563030.jpgx"));
		
		//��ȡҳ��Դ��
//		System.out.println(readSource("http://www.douban.com/photos/album/67952443/"));
//		System.out.println(readSource("https://www.douban.com/photos/album/120012756/"));
		
		try {
			System.out.println(URLUtils.exists("https://img1.doubanio.com/view/photo/raw/public/p2321685527.jpg"));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
