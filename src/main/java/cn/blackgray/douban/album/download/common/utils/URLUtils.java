package cn.blackgray.douban.album.download.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Random;

import org.openqa.selenium.WebDriver;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;


/**
 * 网络资源相关工具类
 * @author BlackGray
 */
public class URLUtils {

	
	public static String charset = Common.CHARTSET_UTF8;
	
	/**
	 * 获取页面源码
	 * @param url
	 * @return
	 */
	public static String readSource(String url){
		
		StringBuffer sb = new StringBuffer();
		
		//判断是否已登录
		if (LoginUtils.IS_LOGIN) {
			//若已登录使用CHROME_DRIVER获取源码
			WebDriver driver = LoginUtils.CHROME_DRIVER;			
			driver.get(url);
			Console.print("睡眠2s，等待页面加载完整。");
			try {
				Thread.sleep(2*1000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

//			System.out.println("获取页面源码。");
			String pageSource = driver.getPageSource();

//			System.out.println("输出页面源码。");
//			System.out.println(pageSource);
			
			sb.append(pageSource.toString());
			
		}else {
			//若未登录，使用HttpURLConnection获取源码
			//获取页面源码
			try {
				//代理
//				SocketAddress add = new InetSocketAddress("203.66.187.246", 81);
//				Proxy p = new Proxy(Proxy.Type.HTTP , add);
//				HttpURLConnection connection = (HttpURLConnection) u.openConnection(p);
//				String headerKey = "Proxy-Authorization";  
//				String headerValue = "Basic " + Base64.encode(user+":"+password);  
//				conn.setRequestProperty(headerKey, headerValue);  
				
				URL u = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) u.openConnection();
				connection.setRequestProperty("User-Agent", randomUserAgentStr());
				
				//connection.setRequestProperty("referer", "https://www.douban.com/");
				
				//默认UTF-8读取
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
		}
		

		String result = sb.toString();
		if (result.trim().length() == 0) {
			Console.print("源码获取失败：" + url);
			return result;
		}else{
			String charsetCheck = charset;
			//判断字符集
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
				Console.print("字符集：" + charset + " -> " + charsetCheck);
				charset = charsetCheck;
				return readSource(url);
			}else{
				return result;				
			}
		}

	}
	
	
	/**
	 * 随机生成user-agent字符串
	 */
	public static String randomUserAgentStr() {

		Random r = new Random();
		Integer chromeVesionFirstNum = r.nextInt(14) + 100;
		Integer chromeVesionThirdNum = r.nextInt(3800);
		Integer chromeVesionFourthNum = r.nextInt(140);
		
		String[] osTypeArray = new String[] {
			"(Windows NT 6.1; WOW64) ",
			"(Windows NT 10.0; WOW64) ",
			"(X11; Linux x86_64) ",
			"(Macintosh; Intel Mac OS X 10_15_7) "
		};
		
		String result = "";
		result += "Mozilla/5.0 " ;
		result += osTypeArray[r.nextInt(3)] ;
		result += "AppleWebKit/537.36 (KHTML, like Gecko) ";
		result += "Chrome/" + chromeVesionFirstNum + ".0." + chromeVesionThirdNum + "." + chromeVesionFourthNum + " ";
		result += "Safari/537.36";
		    		
		return result;
	}
	
	/**
	 * 判断URL资源是否存在
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static boolean exists(String url) throws MalformedURLException, ProtocolException, IOException{
		return exists(url);
	}
	
	/**
	 * 判断URL资源是否存在
	 * @param url
	 * @param cookie字符串
	 * @return
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public static boolean exists(String url, String cookieStr) throws MalformedURLException, ProtocolException, IOException{
		URL u = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		//允许重定向，否则部分图片无法下载
		HttpURLConnection.setFollowRedirects(true);
		conn.setInstanceFollowRedirects(true);
		/* 设置 URL 请求的方法， GET POST HEAD OPTIONS PUT DELETE TRACE 以上方法之一是合法的，具体取决于协议的限制。*/
		conn.setRequestMethod("GET");
		conn.setRequestProperty("User-Agent", URLUtils.randomUserAgentStr());
		
		//2016-03-16 如不加referer信息，下载影人相册时，大图监测返回403异常
		conn.setRequestProperty("referer", "https://www.douban.com/");
		
		//2024-10-02 如果有Cookie，设置参数。用于登录后页面访问。
		if (cookieStr != null && cookieStr != "") {
			conn.setRequestProperty("Cookie", cookieStr);
		}
		
		//=======信息=======
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
		//=======/信息=======
		System.out.println("ResponseCode:" + conn.getResponseCode());
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return true;
		}else{
			return false;
		}
	}
	

	public static void main(String[] args) throws MalformedURLException, ProtocolException, IOException {
//		//资源验证
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/photo/public/p814563030.jpg"));
//		System.out.println("==========");
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/large/public/p814563030.jpg"));
//		System.out.println("==========");
//		System.out.println(URLUtils.exists("http://img5.douban.com/view/photo/large/public/p814563030.jpgx"));
		
		//获取页面源码
//		System.out.println(readSource("http://www.douban.com/photos/album/67952443/"));
//		System.out.println(readSource("https://www.douban.com/photos/album/120012756/"));
//		System.out.println(readSource("https://movie.douban.com/celebrity/1138320/photos/?start=0"));
		//2023-07-05：因豆瓣改版“用户相册首页”只支持登录后访问，程序无权限，无法获取相册清单。
		System.out.println(readSource("https://www.douban.com/people/BlackGray/photos"));
		
//		try {
//			System.out.println(URLUtils.exists("https://img1.doubanio.com/view/photo/raw/public/p2321685527.jpg"));
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ProtocolException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//此前默认user-agent
//		System.out.println("Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36");
		//随机生成user-agent
//		System.out.println(randomUserAgentStr());
		
	}

}
