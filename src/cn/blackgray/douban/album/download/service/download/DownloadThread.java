package cn.blackgray.douban.album.download.service.download;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;

/**
 * 下载线程
 * @author BlackGray
 */
public class DownloadThread extends Thread{

	private List<String> imageURLList;
	private String path;	//保存路径
	private String url;		//正在处理中的图片url
	private int imageCount;
	private JProgressBar mainProgressBar;
	
	private BufferedInputStream inputStream;
	private BufferedOutputStream outputStream;
	
	public DownloadThread() {
		super();
	}

	public DownloadThread(String name, List<String> imageURLList,int imageCount , String path,JProgressBar mainProgressBar) {
		this.imageURLList = imageURLList;
		this.path = path.trim();
		this.imageCount = imageCount;
		this.mainProgressBar = mainProgressBar;
		this.setName(name);
	}
	
	public void closeStream() throws IOException {
		if (inputStream != null) {
			inputStream.close();
			inputStream = null;
		}
		if (outputStream != null) {
			outputStream.close();
			outputStream = null;
		}
	}

	public String getPath() {
		return path;
	}

	public String getUrl() {
		return url;
	}
	
	public BufferedInputStream getInputStream() {
		return inputStream;
	}
	
	public BufferedOutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void run() {
		while (true) {
			int listSize;
			synchronized (imageURLList) {
				//判断是否有图片需要下载
				if (imageURLList.size() != 0) {
					url = imageURLList.get(0);
					listSize = imageURLList.size() - 1;
					imageURLList.remove(url);
				}else{
					break;
				}
			}
			try {
				int c = downloadImage(url, path, false);
				if (c == Common.IMAGE_DOWNLOAD_STATUS_EXISTS) {
					Console.print(this.getName() + " - 图片已存在(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);
				}
				if(c == Common.IMAGE_DOWNLOAD_STATUS_FINISH){
					Console.print(this.getName() + " - 图片下载完成(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);					
				}
				if(c == Common.IMAGE_DOWNLOAD_STATUS_URL_NOT_EXISTS){
					Console.print(this.getName() + " - 图片不存在(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);					
				}
				synchronized (DownloadManager.updateCount) {
					DownloadManager.updateCount += c;
				}
			} catch (Exception e) {
				if (!e.getClass().equals(FileNotFoundException.class)) {
					Console.print("图片下载失败：" + url + " - " + e.getMessage());
					synchronized (Common.failFileMap) {
						if (!Common.failFileMap.containsKey(url)) {
							Common.failFileMap.put(url,path);
						};
					}
					e.printStackTrace();
				}else{
					Console.print("图片不存在：" + url + " - " + e.getMessage());
					e.printStackTrace();
				}
			}finally{
				synchronized (mainProgressBar) {
					mainProgressBar.setValue(mainProgressBar.getValue() + 1);
				}
			}
		}
	}
	
	/**
	 * 下载图片
	 * @param url
	 * @param filePath
	 * @throws MalformedURLException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public int downloadImage(String url,String filePath, boolean isDeleteOldFile) throws MalformedURLException, FileNotFoundException, IOException{
		if (URLUtils.exists(url)) {
			String fileName = url.substring(url.lastIndexOf('/'));
			File file = new File(filePath + File.separatorChar + fileName);
			//如果文件存在，删除文件
			if (isDeleteOldFile) {
				if (file.exists()) {
					file.delete();
				}
			}
			
			//配置网络资源
			URL image = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) image.openConnection();
			
			//2016-03-16 如不加referer信息，下载影人相册时，大图监测返回403异常
			conn.setRequestProperty("referer", "https://www.douban.com/");
			
			conn.setConnectTimeout(10*1000);	//设置连接超时
			conn.setReadTimeout(10*1000);		//设置读取超时
			conn.setDoInput(true);				//默认为true
			conn.connect();
			InputStream in = conn.getInputStream();
			
			//执行下载
			if (!file.exists()) {
				inputStream = new BufferedInputStream(in);
				outputStream = new BufferedOutputStream(new FileOutputStream(file));
				byte[] data = new byte[2048];
				int n = 0; 
				while ((n = inputStream.read(data)) != -1) {
					outputStream.write(data,0,n);
				}
				outputStream.flush();
				in.close();
				inputStream.close();
				outputStream.close();
				conn.disconnect();
				return Common.IMAGE_DOWNLOAD_STATUS_FINISH;
			}else{
				return Common.IMAGE_DOWNLOAD_STATUS_EXISTS;
			}
		}else{
			return Common.IMAGE_DOWNLOAD_STATUS_URL_NOT_EXISTS;
		}
	}
	
}
