package cn.blackgray.douban.album.download.service.download;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
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
	
	public static int downloadImage(List<String> imageURLList,String path) {
		
		mainProgressBar.setMaximum(imageURLList.size());	//进度条设置 - 最大值
		mainProgressBar.setValue(0);						//进度条设置 - 初始值
		List<DownloadThread> threadList = new ArrayList<DownloadThread>();
		int imageSize = imageURLList.size();
		for (int i = 0; i < Common.DOWNLOAD_THREAD; i++) {
			String threadName = "线程0";
			if (i < 10) {
				threadName += i;
			}else{
				threadName = "线程" + String.valueOf(i);
			}
			DownloadThread thread = new DownloadThread(threadName,imageURLList, imageSize, path, mainProgressBar);
			thread.start();
			threadList.add(thread);
		}
		Map<DownloadThread,Integer> waitThreadMap = new HashMap<DownloadThread,Integer>();
		while (true) {
			try {
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
								Console.print("下载超时,中断线程,请稍等.. - " + thread.getName() + " - " + thread.getUrl());
								thread.closeStream();
								//添加至错误集合
								if (!Common.failFileMap.containsKey(thread.getUrl())) {
									Common.failFileMap.put(thread.getUrl(), thread.getPath());
								};
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
	
	public static int downloadFailFile(){
		int num = 1;
		int size = Common.failFileMap.size();
		JProgressBar progressBar = MainFrame.getInstance().progressBar;
		progressBar.setMaximum(size);
		progressBar.setValue(0);
		Console.print("=====================================");
		Console.print("下载图片上次失败图片：" + size + "(张)");
		Map<String, String> failMap = new TreeMap<String, String>();
		for (Entry<String, String> element : Common.failFileMap.entrySet()) {
			try {
				//下载
				Console.print("下载图片(" + num + "/" + size + ")：" + element.getKey());
				DownloadThread downloadThread = new DownloadThread();
				downloadThread.downloadImage(element.getKey(), element.getValue(), true);
			} catch (IOException e) {
				Console.print("图片下载失败：" + element.getKey());
				failMap.put(element.getKey(), element.getValue());
			}
			progressBar.setValue(num);
			num++;
		}
		Common.failFileMap.clear();
		if (failMap.size() > 0) {
			Console.print("【FINISH】成功：" + (size - failMap.size()) + "，失败" + failMap.size());
			Common.failFileMap.putAll(failMap);
			return 0;
		} else {
			Console.print("【FINISH】成功：" + size + "，失败" + 0);
			return 1;
		}
	}
	
	
	
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException {
		System.out.println("START");
		new DownloadThread().downloadImage("http://img3.douban.com/view/photo/photo/public/p1105635956.jpg","D:\\", true);
		System.out.println("FINISH");
	}

}


