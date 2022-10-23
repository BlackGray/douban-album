package cn.blackgray.douban.album.download.service.download;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.FileUtils;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 下载失败后处理类
 * @author BlackGray
 * @createTime 2022-10-23 23:32:56
 */
public class DownloadFailManager {
	
	private static Map<String,String> failFileMap = new TreeMap<String, String>();		//下载失败的文件集合Map<url,path>
	
	
	/**
	 * 添加下载失败图片
	 * @return
	 */
	public static void add(String url, String path) {
		synchronized (failFileMap) {
			if (!failFileMap.containsKey(url)) {
				//删除下载不完整图片文件
				FileUtils.deleteImageFile(url, path);
				failFileMap.put(url,path);
			}
		}
	}
	

	/**
	 * 批量添加下载失败图片
	 * @param failMap
	 */
	public static void add(Map<String, String> failMap) {
		synchronized (failFileMap) {
			failFileMap.putAll(failMap);
		}
	}
	
	
	/**
	 * 获取下载失败文件集合
	 * @return
	 */
	public static Map<String,String> getFailFileMap() {
		return failFileMap;
	}
	
	/**
	 * 获取下载失败的图片数量
	 * @return
	 */
	public static int getFailSize() {
		return failFileMap.size();
	}
	
	/**
	 * 清除集合中所有记录
	 * @return
	 */
	public static void clearAll() {
		failFileMap.clear();
	}
	
	/**
	 * 下载未成功下载的图片
	 * @return true = 下载失败文件成功，false = 下载失败文件未成功（未完全下载）。
	 */
	public static boolean downloadFailFile(){
		int num = 1;
		int size = DownloadFailManager.getFailSize();
		//DownloadThread中下载异常时，将继续向公共Map中添加记录，为防止冲突，新建Map用于异常文件重新下载
		Map<String, String> failFileMapForRetry = new TreeMap<String, String>();
		failFileMapForRetry.putAll(DownloadFailManager.getFailFileMap());
		
		JProgressBar progressBar = MainFrame.getInstance().progressBar;
		progressBar.setMaximum(size);
		progressBar.setValue(0);
		Console.print("=====================================");
		Console.print("下载上次下载失败图片：" + size + "(张)");
		Map<String, String> newFailFileMap = new TreeMap<String, String>();
		for (Entry<String, String> element : failFileMapForRetry.entrySet()) {
			String url = element.getKey();
			String path = element.getValue();
			try {
				//下载
				Console.print("下载图片(" + num + "/" + size + ")：" + url);
				DownloadThread downloadThread = new DownloadThread();
				int state = downloadThread.downloadImage(url, path);
				if (state == Common.IMAGE_DOWNLOAD_STATUS_DOWNLOAD_FAIL) {
					Console.print("失败重下 - 图片下载异常，已下载文件小于网络资源大小，等待再次重试：" + url);
					//加入下载异常集合，待重试
					FileUtils.deleteImageFile(url, path);
					newFailFileMap.put(url, path);
				}
			} catch (IOException e) {
				Console.print("图片下载失败：" + url);
				FileUtils.deleteImageFile(url, path);
				newFailFileMap.put(url, path);
			}
			progressBar.setValue(num);
			num++;
		}
		DownloadFailManager.clearAll();
		if (newFailFileMap.size() > 0) {
			Console.print("【FINISH】成功：" + (size - newFailFileMap.size()) + "，失败" + newFailFileMap.size());
			//批量添加下载失败图片
			DownloadFailManager.add(newFailFileMap);
			return false;
		} else {
			Console.print("【FINISH】成功：" + size + "，失败" + 0);
			return true;
		}
	}
	
	
	/**
	 * 生成下载失败的图片记录文档 - 多个相册
	 * 
	 * 图片下载失败、且重试后仍有失败，点击取消重试后将调用此方法生成记录文档
	 * 用户可使用其他下载软件下载图片，或用于知晓下载失败内容
	 * 
	 * 相册更新时将自动重新下载缺失图片，因此不再单独实现记录文档识别与重新下载功能
	 * 
	 * @param albumPathList 相册目录
	 */
	public static void createAlbumFailFileDoc(List<String> albumPathList, Map<String, String> failFileMap) {

		Console.print("【正在生成下载失败图片记录文件（下载失败图片记录.txt）,请稍等...】");
		
		//按相册对下载异常文件做分组Map<albumPath, Map<url,path>>
		Map<String, Map<String, String>> failFileMapGroupByAlbumPath = new TreeMap<String, Map<String, String>>();
		for (String albumPath : albumPathList) {
			for (Entry<String, String> entry : failFileMap.entrySet()) {
				String imageUrl = entry.getKey();
				String imagePath = entry.getValue();
				if(imagePath.startsWith(albumPath)) {
					Map<String, String> tempMap = null;
					if(failFileMapGroupByAlbumPath.containsKey(albumPath)) {
						tempMap = failFileMapGroupByAlbumPath.get(albumPath);
					}else {
						tempMap = new TreeMap<String, String>();
					}
					tempMap.put(imageUrl, imagePath);
					failFileMapGroupByAlbumPath.put(albumPath, tempMap);
				}
			}
			
		}
		
		//生成单个相册文档
		for (String albumPath : albumPathList) {
			createAlbumFailFileDoc(albumPath, failFileMapGroupByAlbumPath.get(albumPath));
		}
		
		Console.print("【下载失败图片记录文件生成成功】");
		
	}
	
	
	/**
	 * 生成下载失败的图片记录文档 - 单个相册
	 */
	private static void createAlbumFailFileDoc(String albumPath, Map<String, String> failFileMap) {
		if(failFileMap != null && failFileMap.size() > 0) {
			try {
				String path = albumPath + File.separator + Common.DEFAULT_FAIL_FILE_DOC_NAME;
				Console.print("开始生成文档：" + path);
				BufferedWriter bw = new BufferedWriter(new FileWriter(path));
				//输出记录
				bw.write("------------------------------------------------------------------");
				bw.newLine();
				bw.write("下载失败图片URL地址及保存目录");
				bw.newLine();
				bw.write("------------------------------------------------------------------"); 
				bw.newLine();
				for (Entry<String, String> entry : failFileMap.entrySet()) {
					String imageUrl = entry.getKey();
					String imagePath = entry.getValue();
					bw.write(imageUrl + " → " + imagePath.replace(albumPath, Common.DEFAULT_ALBUM_ROOT_PATH_STR));
					bw.newLine();
				}
				//输出纯地址
				bw.newLine();
				bw.newLine();
				bw.newLine();
				bw.write("------------------------------------------------------------------");
				bw.newLine();
				bw.write("下载失败图片URL地址，若希望使用其他工具下载缺失图片，可复制以下地址");
				bw.newLine();
				bw.write("------------------------------------------------------------------"); 
				bw.newLine();
				for (Entry<String, String> entry : failFileMap.entrySet()) {
					String imageUrl = entry.getKey();
					bw.write(imageUrl);
					bw.newLine();
				}
				
				bw.flush();
				bw.close();
				Console.print("文档生成成功");
			} catch (IOException e) {
				Console.print("文档生成失败");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 根据相册路径删除“下载失败图片记录.txt”文档，更新相册时调用
	 */
	public static void deleteAlbumFailFileDoc(String albumPath) {
		File doc = new File(albumPath + File.separator + Common.DEFAULT_FAIL_FILE_DOC_NAME);
		if (doc.exists()) {
			Console.print("更新相册，删除早期生成的“下载失败图片记录.txt”文档");
			doc.delete();
		}
	}
	
	

}


