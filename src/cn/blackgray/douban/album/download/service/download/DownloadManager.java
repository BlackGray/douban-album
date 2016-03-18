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
 * ͼƬ�����࣬��������
 * @author BlackGray
 */
public class DownloadManager {
	
	//�������� - ��ʾ��Ƭ���صȽ���
	private final static JProgressBar mainProgressBar = MainFrame.getInstance().progressBar;
	public static Integer updateCount = 0;
	private static final Integer TIMEOUT = 10;	//��ͼƬ���س�ʱʱ��
	
	public static int downloadImage(List<String> imageURLList,String path) {
		
		mainProgressBar.setMaximum(imageURLList.size());	//���������� - ���ֵ
		mainProgressBar.setValue(0);						//���������� - ��ʼֵ
		List<DownloadThread> threadList = new ArrayList<DownloadThread>();
		int imageSize = imageURLList.size();
		for (int i = 0; i < Common.DOWNLOAD_THREAD; i++) {
			String threadName = "�߳�0";
			if (i < 10) {
				threadName += i;
			}else{
				threadName = "�߳�" + String.valueOf(i);
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
			//�ж��߳��Ƿ��Ѿ�����
			if (imageURLList.size() == 0) {
				for (DownloadThread thread : threadList) {
					if (thread.isAlive()) {
						if (waitThreadMap.containsKey(thread)) {
							waitThreadMap.put(thread, waitThreadMap.get(thread)+1);
						}else{
							waitThreadMap.put(thread, 0);
						}
						//�жϳ�ʱ
						if (waitThreadMap.get(thread) > TIMEOUT) {
							//�ж��߳�
							try {
								Console.print("���س�ʱ,�ж��߳�,���Ե�.. - " + thread.getName() + " - " + thread.getUrl());
								thread.closeStream();
								//��������󼯺�
								if (!Common.failFileMap.containsKey(thread.getUrl())) {
									Common.failFileMap.put(thread.getUrl(), thread.getPath());
								};
							} catch (IOException e) {
								Console.print("�߳��жϲ����쳣��" + e.getMessage());
								e.printStackTrace();
							}
							waitThreadMap.remove(thread);
						}
					}else{
						waitThreadMap.remove(thread);
					}
				}
				//�������������ѭ����������ʾ				
				if (waitThreadMap.size() == 0) {
					mainProgressBar.setValue(mainProgressBar.getMaximum());
					break;
				}else{
					//��ʾ
					Console.print("�Ϳ����~ ��(��������)(��������)�� ");
					Console.print("---------------------------------------------------");
					for (Entry<DownloadThread, Integer> entry : waitThreadMap.entrySet()) {
						DownloadThread t = entry.getKey();
						Integer time = entry.getValue();
						StringBuffer sb = new StringBuffer();
						sb.append("�ȴ��߳�").append(" - ").append(t.getName()).append(" - [").append(time).append("s]");
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
		Console.print("����ͼƬ�ϴ�ʧ��ͼƬ��" + size + "(��)");
		Map<String, String> failMap = new TreeMap<String, String>();
		for (Entry<String, String> element : Common.failFileMap.entrySet()) {
			try {
				//����
				Console.print("����ͼƬ(" + num + "/" + size + ")��" + element.getKey());
				DownloadThread downloadThread = new DownloadThread();
				downloadThread.downloadImage(element.getKey(), element.getValue(), true);
			} catch (IOException e) {
				Console.print("ͼƬ����ʧ�ܣ�" + element.getKey());
				failMap.put(element.getKey(), element.getValue());
			}
			progressBar.setValue(num);
			num++;
		}
		Common.failFileMap.clear();
		if (failMap.size() > 0) {
			Console.print("��FINISH���ɹ���" + (size - failMap.size()) + "��ʧ��" + failMap.size());
			Common.failFileMap.putAll(failMap);
			return 0;
		} else {
			Console.print("��FINISH���ɹ���" + size + "��ʧ��" + 0);
			return 1;
		}
	}
	
	
	
	public static void main(String[] args) throws MalformedURLException, FileNotFoundException, IOException {
		System.out.println("START");
		new DownloadThread().downloadImage("http://img3.douban.com/view/photo/photo/public/p1105635956.jpg","D:\\", true);
		System.out.println("FINISH");
	}

}


