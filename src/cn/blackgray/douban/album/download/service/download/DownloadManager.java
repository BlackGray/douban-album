package cn.blackgray.douban.album.download.service.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 图片工具类，负责下载
 * @author BlackGray
 */
public class DownloadManager {
	
	//主进度条 - 显示照片下载等进度
	private final static JProgressBar mainProgressBar = MainFrame.getInstance().progressBar;
	public static Integer updateCount = 0;
	private static final Integer TIMEOUT = 10;	//单图片下载超时时间
	
	/**
	 * 批量下载图片
	 * @param imageURLList
	 * @param path
	 * @return
	 */
	public static int downloadImage(Album album, List<String> imageURLList,String path) {
		
		mainProgressBar.setMaximum(imageURLList.size());	//进度条设置 - 最大值
		mainProgressBar.setValue(0);						//进度条设置 - 初始值
		List<DownloadThread> threadList = new ArrayList<DownloadThread>();
		int imageSize = imageURLList.size();
		//创建多个线程，开始批量下载图片
		for (int i = 0; i < Common.DOWNLOAD_THREAD; i++) {
			String threadName = "线程0";
			if (i < 10) {
				threadName += i;
			}else{
				threadName = "线程" + String.valueOf(i);
			}
			DownloadThread thread = new DownloadThread(album, threadName,imageURLList, imageSize, path, mainProgressBar);
			thread.start();
			threadList.add(thread);
		}
		Map<DownloadThread,Integer> waitThreadMap = new HashMap<DownloadThread,Integer>();
		//循环中每隔1s判断下载完成情况
		while (true) {
			try {
				//睡眠1s
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//判断线程是否都已经结束
			if (imageURLList.size() == 0) {
				for (DownloadThread thread : threadList) {
					if (thread.isAlive()) {
						if (waitThreadMap.containsKey(thread)) {
							waitThreadMap.put(thread, waitThreadMap.get(thread)+1);
						}else{
							waitThreadMap.put(thread, 0);
						}
						//判断超时
						if (waitThreadMap.get(thread) > TIMEOUT) {
							//中断线程
							try {
								String url = thread.getUrl();
								Console.print("下载超时,中断线程,请稍等.. - " + thread.getName() + " - " + url);
								thread.closeStream();
								//添加至错误集合
								DownloadFailManager.add(url, path);
							} catch (IOException e) {
								Console.print("线程中断操作异常：" + e.getMessage());
								e.printStackTrace();
							}
							waitThreadMap.remove(thread);
						}
					}else{
						waitThreadMap.remove(thread);
					}
				}
				//如果结束，跳出循环，否则提示				
				if (waitThreadMap.size() == 0) {
					mainProgressBar.setValue(mainProgressBar.getMaximum());
					break;
				}else{
					//提示
					Console.print("就快好了~ ～(￣▽￣～)(～￣▽￣)～ ");
					Console.print("---------------------------------------------------");
					for (Entry<DownloadThread, Integer> entry : waitThreadMap.entrySet()) {
						DownloadThread t = entry.getKey();
						Integer time = entry.getValue();
						StringBuffer sb = new StringBuffer();
						sb.append("等待线程").append(" - ").append(t.getName()).append(" - [").append(time).append("s]");
						sb.append(" = ").append(t.getUrl());
						Console.print(sb.toString());	
					}
					
				}
			}
		}
		return updateCount;
	}
	
	
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException, URISyntaxException, InterruptedException {
		System.out.println("START");
		//https://www.douban.com/personage/27503633/photo/1946222503/
		//https://img3.doubanio.com/view/photo/l/public/p1946222503.jpg
		new DownloadThread().downloadImage("https://img3.doubanio.com/view/photo/raw/public/p1946222503.jpg", "/Users/blackgray/Documents/temp", false, false);
		System.out.println("FINISH");
	}

}


