package cn.blackgray.douban.album.download.common;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import cn.blackgray.douban.album.download.common.utils.CommonUtils;

/**
 * 公共变量
 * @author BlackGray
 */
public class Common {
	
	public static String VERSION = "v0.5.13";
	
	public static final String DEFAULT_DOC_NAME = "描述.txt";
	public static final String DEFAULT_HTML_NAME = "index.html";
	public static final String DEFAULT_HTML_RESOURCE_DIR = "resource";
	public static final String DEFAULT_RAW_DIR = "raw";
	
	//时间格式化
	public static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	//图片下载结果&状态
	public static final int IMAGE_DOWNLOAD_STATUS_EXISTS = 0;
	public static final int IMAGE_DOWNLOAD_STATUS_FINISH = 1;
	public static final int IMAGE_DOWNLOAD_STATUS_URL_NOT_EXISTS = 2;
	
//	public static int albumType;	//相册类型 - 普通、豆瓣相册、豆瓣活动
	 
	public static final String URL_HELP = "http://www.douban.com/note/206320326/";
	public static final String URL_DOUPIC = "http://www.douban.com/group/doupic/";
	
	public static final long TIME_PROCESS_MIN = 1*60*1000;	//边界时间 - 单位毫秒
	public static final long TIME_PROCESS_SLEEP = 60;		//休眠时间 - 单位秒
	
	public static final int PROCESS_UNIT_SIZE = 50;	//处理单元大小
	
	public static final String CHARTSET_UTF8 = "utf-8";
	public static final String CHARTSET_GBK = "gbk";
	public static final String CHARTSET_GB2312 = "gb2312";
	
	public static final int DOWNLOAD_THREAD = 15;	//下载线程数
	
	public static String PATH_DOWNLOAD = "";
	public static String PATH_APP = "";
	public static boolean IS_UPDATE = false;
	public static boolean IS_DOWNLOAD_RAW = false;
	
	public static final String IMAGE_TYPE = "gif|jpg|png";
	
	public static Map<String,String> failFileMap = new TreeMap<String, String>();;		//下载失败的文件集合
	public static final Integer AUTO_DOWNLOAD_FAIL_FILE = 5;							//自动下载错误文件次数
	
	public static final String HTML_TEMPLATE_PAGE;
	public static final String HTML_TEMPLATE_IMAGE;
	
	public static final String HTML_TAG_IMAGES = "${images}";
	public static final String HTML_TAG_IMAGES_TOTAL = "${imagesTotal}";
	public static final String HTML_TAG_NAME = "${name}";
	public static final String HTML_TAG_URL = "${url}";
	public static final String HTML_TAG_TITLE = "${title}";
	public static final String HTML_TAG_ALBUM_DESC = "${albumDesc}";
	public static final String HTML_TAG_DOWNLOAD_TIME = "${downloadTime}";
	public static final String HTML_TAG_CHARSET = "GBK";
	
	
	public static final String HTML_TAG_OWNER = "${owner}";
	public static final String HTML_TAG_DESC = "${desc}";
	public static final String HTML_TAG_COMMENT_URL = "${commentURL}";
	public static final String HTML_TAG_NUMBER = "${num}";
	public static final String HTML_TAG_IMAGE = "${image}";
	public static final String HTML_TAG_RAW = "${raw}";
	
	
	/*大图类型 - 1大图，2大小相同，高质量未压缩图*/
	public static final Integer RAW_TYPE_LARGE = 1;
	public static final Integer RAW_TYPE_UNCOMPRESSED = 2;
	
	static{
		//JAR包路径
		String jarPath;
		try {
			jarPath = URLDecoder.decode(Common.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"utf-8");
			//设置默认下载路径为程序所在目录
			PATH_DOWNLOAD = jarPath;
			if (CommonUtils.isWindows()) {
				//如果是Windows系统，若路径首字符为/，去除
				if(PATH_DOWNLOAD.startsWith("/")) {
					PATH_DOWNLOAD = jarPath.substring(1, jarPath.lastIndexOf("/"));
				}
			}
			//程序所在目录
			PATH_APP = PATH_DOWNLOAD;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		//加载HTML页面模版
		StringBuffer sb = new StringBuffer();
		InputStream inputStream = Common.class.getResourceAsStream("/cn/blackgray/douban/album/download/resources/html/Template.html");
		BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream));
		String str;
		try {
			while ((str = bw.readLine()) != null) {
				sb.append(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		HTML_TEMPLATE_PAGE = sb.toString();
		
		//初始化HTML图片单元模版
		HTML_TEMPLATE_IMAGE = "<div class=\"photos\"><div class=\"desc\">${owner}${desc}</div><div class=\"number\">${num}</div><a href=\"${commentURL}\" target=\"_blank\"><img src=\"${image}\"/></a></div>";
		
	}
	
	/**
	 * 通过系统默认浏览器打开页面
	 * @param url
	 * @param frame
	 */
	public static void openURLWithBrowse(String url, JFrame frame){
		//判断当前系统是否支持Java AWT Desktop扩展
		if(Desktop.isDesktopSupported()){
			try {
				//创建一个URI实例
				URI uri = URI.create(url); 
				//获取当前系统桌面扩展
				Desktop desktop = Desktop.getDesktop();
				//判断系统桌面是否支持要执行的功能
				if(desktop.isSupported(Desktop.Action.BROWSE)){
					//获取系统默认浏览器打开链接
					desktop.browse(uri);    
				}
			} catch(java.lang.NullPointerException e){
				e.printStackTrace();
			} catch (java.io.IOException e) {
				e.printStackTrace();
				String msg = "无法获取系统默认浏览器，地址：" + url;
				JOptionPane.showMessageDialog(frame, msg, "555~",JOptionPane.ERROR_MESSAGE);
			}  
		}else{
			String msg = "当前JDK版本过低，无法执行打开操作，地址：" + url;
			JOptionPane.showMessageDialog(frame, msg, "555~",JOptionPane.ERROR_MESSAGE);
		}
	}

}
