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
				int state = downloadImage(url, path);
				if (state == Common.IMAGE_DOWNLOAD_STATUS_EXISTS) {
					Console.print(this.getName() + " - 图片已存在(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);
				}
				if(state == Common.IMAGE_DOWNLOAD_STATUS_FINISH){
					Console.print(this.getName() + " - 图片下载完成(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);					
				}
				if(state == Common.IMAGE_DOWNLOAD_STATUS_URL_NOT_EXISTS){
					Console.print(this.getName() + " - 图片不存在(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);					
				}
				if (state == Common.IMAGE_DOWNLOAD_STATUS_DOWNLOAD_FAIL) {
					Console.print(this.getName() + " - 图片下载异常，已下载文件小于网络资源大小(" + (imageCount - listSize) + "/" + imageCount + ")：" + url);
					//加入下载异常集合，待重试
					DownloadFailManager.add(url, path);
				}
				synchronized (DownloadManager.updateCount) {
					DownloadManager.updateCount += 1;
				}
			} catch (Exception e) {
				if (!e.getClass().equals(FileNotFoundException.class)) {
					Console.print("图片下载失败：" + url + " - " + e.getMessage());
					//删除下载失败图片，并将图片信息加入失败文件集合
					DownloadFailManager.add(url, path);
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
	public int downloadImage(String url,String filePath) throws MalformedURLException, FileNotFoundException, IOException{
		
		String fileName = url.substring(url.lastIndexOf('/'));
		File file = new File(filePath + File.separatorChar + fileName);
		if(file.exists()) {
			//如果本地文件已存在，不执行下载，返回已存在标识
			return Common.IMAGE_DOWNLOAD_STATUS_EXISTS;
		}else {
			//如果本地图不存在，执行下载
			//判断网络资源是否存在
			if (URLUtils.exists(url)) {
				//执行下载
				//配置网络资源
				URL image = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) image.openConnection();

				//2016-03-16 如不加referer信息，下载影人相册时，大图监测返回403异常
				conn.setRequestProperty("referer", "https://www.douban.com/");
				
				conn.setConnectTimeout(10*1000);	//设置连接超时
				conn.setReadTimeout(10*1000);		//设置读取超时
				conn.setDoInput(true);				//默认为true
				conn.connect();
				//获取网络资源文件大小
				long contentLength = conn.getContentLengthLong();
				
				InputStream in = conn.getInputStream();
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
				
				//验证文件大小
				if(file.length() < contentLength) {
					// 图片下载异常，已下载文件小于网络资源大小
					return Common.IMAGE_DOWNLOAD_STATUS_DOWNLOAD_FAIL;
				}else {
					// 下载完成
					return Common.IMAGE_DOWNLOAD_STATUS_FINISH;
				}
			}else{
				return Common.IMAGE_DOWNLOAD_STATUS_URL_NOT_EXISTS;
			}
			
		}
	}

}
