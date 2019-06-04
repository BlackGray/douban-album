package cn.blackgray.douban.album.download.service.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.DirUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.handler.PageAnalyzer;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 下载处理工具类
 * @author BlackGray
 */
public class DownloadProcessing {

	private static MainFrame mainFrame = MainFrame.getInstance();

	private static JProgressBar processUnitProgressBar = mainFrame.processUnitProgressBar;	//处理单元进度条
	private static JLabel processUnitCountLabel = mainFrame.processUnitCountLabel;			//处理单元进度条


	
	/**
	 * 根据相册模型下载相册
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void downloadAlbum(Album album){

		//单相册下载时间
		long albumDownloadTime = System.currentTimeMillis();
		//更新统计
		int updateCount = 0;	

		Map<String,BGImage> imageMap = new HashMap<String,BGImage>();	//【单相册全照片集合】

		//【创建目录】
		DirUtils.createDir(album);

		//【启动处理单元】
		//处理单元总数
		int processUnitMax = new Double(Math.ceil((double)album.getPageURLLsit().size()/Common.PROCESS_UNIT_SIZE)).intValue();
		int processUnitNumber = 0;	//处理单元计数
		processUnitProgressBar.setMaximum(processUnitMax);
		processUnitProgressBar.setValue(0);
		processUnitCountLabel.setText("0/" + processUnitMax + " ");
		for (int j = 0; j < processUnitMax; j++) {
			//处理单元耗时
			long processUnitTime = System.currentTimeMillis();
			//取出规定条数记录，执行处理单元
			List<String> pageURLList = new ArrayList<String>();
			int start = processUnitNumber * Common.PROCESS_UNIT_SIZE;
			int end = start + Common.PROCESS_UNIT_SIZE;
			if (end > album.getPageURLLsit().size()) {
				end = album.getPageURLLsit().size();
			}
			for (int k = start; k < end; k++) {
				pageURLList.add(album.getPageURLLsit().get(k));
			}
			//处理 - 返回更新信息
			updateCount += processUnit(album,imageMap,pageURLList);

			//处理单元计数+1
			processUnitNumber++;
			processUnitProgressBar.setValue(j + 1);
			processUnitCountLabel.setText((j + 1) + "/" + processUnitMax + " ");
			//【判断启动休眠】
			//处理单元数大于1，并且不是最后一次处理才执行休眠判断
			if (processUnitMax > 1 && (j + 1) != processUnitMax) {
				long t = System.currentTimeMillis() - processUnitTime;
				Console.print("处理单元耗时：" + t);
				if (t < Common.TIME_PROCESS_MIN) {
					Console.print("短时间访问页面次数过多，启动休眠~");
					Console.print("(￣ε(#￣)☆╰╮o(￣皿￣///)");
					long c = Common.TIME_PROCESS_SLEEP;
					while (true) {
						if (c <= 0) {
							Console.print("[]~(￣▽￣)~* ");
							break;
						}
						try {
							Thread.sleep(1000);
							Console.print("休眠倒计时：" + c + "\t (＃°Д°)\"");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						c--;
					}
				}
			}
		}
		//单相册处理完成，map赋值，提供后续操作使用
		album.setPhotosList(new ArrayList<BGImage>(imageMap.values()));

		//后续操作
		if (album.getPhotosList().size() != 0) {
			//【生成描述文件】
			album.createDescDoc();
			//【输出统计信息】
			Console.print("相册下载完成 - " + album.getName());
			Console.print(" 数量：" + album.getPhotosList().size());
			if (album.isUpdate()) {
				Console.print(" 新增:" + updateCount + "(张)");
			}
			Console.print(" 单相册耗时:" + (System.currentTimeMillis() - albumDownloadTime)/1000 + "s");
		}else{
			Console.print("提示：失败或页面无图像。");
		}

	}

	
	/**
	 * 处理单元
	 * @param album
	 * @param imageMap
	 * @param pageURLList
	 * @param processUnitMax
	 * @param processUnitNumber
	 * @return 图片更新数
	 */
	private static int processUnit(Album album, Map<String,BGImage> imageMap,List<String> pageURLList){
		int update = 0;
		//【信息获取】
		Console.print("处理单元：启动信息获取");
		Set<String> imageURLSet = infoProcess(album, imageMap, pageURLList);
		//【下载图片】
		Console.print("处理单元：开始下载：" + album.getName() + "(" + imageURLSet.size() + "张)");
		update = DownloadManager.downloadImage(new ArrayList<String>(imageURLSet),album.getPath());

		//【下载大图】
		AlbumHandler albumHandler = album.getAlbumHandler();
		if (Common.IS_DOWNLOAD_RAW && albumHandler.hasRaw()) {
			Console.print("处理单元：检测并下载大图");
			//创建目录
			String path = album.getPath() + File.separatorChar + "raw";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			//【获取地址】
			//小站大图
			List<String> list = new ArrayList<String>();
			for (String url : imageURLSet) {
				list.add(albumHandler.getRawURL(url));
			}
			//执行下载
			update += DownloadManager.downloadImage(list,path);			
		}
		return update;
	}

	
	/**
	 * 照片信息处理
	 * @param imageMap
	 * @param pageURLList
	 * @param processUnitMax
	 * @param processUnitNumber
	 * @return
	 */
	private static Set<String> infoProcess(Album album, Map<String,BGImage> imageMap,List<String> pageURLList){

		Set<String> imageURLSet = new HashSet<String>();	//需要下载的图片 - 单处理单元

		for (int i = 0; i < pageURLList.size(); i++) {
			Console.print("分析页面(" + (i + 1) + "/" + pageURLList.size() + ")：" + pageURLList.get(i));
			//查询单页所有照片地址和描述,可能会出现错误，如果出错，尝试重新分析一次
			Map<String, BGImage> map = new HashMap<String, BGImage>();
			try {
				map = PageAnalyzer.findImageURLAndDesc(album, pageURLList.get(i));
			} catch (Exception e) {
				try {
					map = PageAnalyzer.findImageURLAndDesc(album, pageURLList.get(i));	
				} catch (Exception e2) {
					Console.print("页面分析错误，下载失败：" + pageURLList.get(i));
				}
				e.printStackTrace();
			}

			//保存照片地址和描述信息
			for (Entry<String, BGImage> entry : map.entrySet()) {
				if (!imageMap.containsKey(entry.getKey())) {
					imageMap.put(entry.getKey(), entry.getValue());
					imageURLSet.add(entry.getKey());
				}else{
					//※每个页面都有相册封面照片的链接，扫描照片地址时，有可能每次都对这张照片进行处理
					//封面照片可能在任意一页中，获取描述信息时，除了所在页可以得到描述外，其他页的描述都为空
					//这里判断每页图片是否存在交集，存在，说明该图是封面照片
					//根据图片是否有描述，可以确定图片是否在该页
					if (imageMap.get(entry.getKey()).getDesc().equals("")) {
						//如果之前添加的这张图没有描述，并且当前图有描述，设置描述信息，并添加首页图片标识，否则不执行任何操作
						if (entry.getValue().getDesc().equals("")) {
							//如果不加photo.getValue().equals("")判断，可能出现的情况：
							//1 false 	""
							//2 false 	""		-> ※
							//3 true	"DESC"  -> ※
							BGImage image = imageMap.get(entry.getKey());
							image.setDesc("※" + entry.getValue().getDesc());
							imageMap.put(entry.getKey(), image);
						}else{
							imageMap.put(entry.getKey(), entry.getValue());
						}
					}else {
						//如果之前添加的图有描述，添加首页图片标识
						BGImage bgImage = imageMap.get(entry.getKey());
						String desc = ("※" + bgImage.getDesc()).replaceAll("※+", "※");
						if (desc.equals("※")) {
							bgImage = entry.getValue();
							bgImage.setDesc("※" + bgImage.getDesc());
						}else{
							bgImage.setDesc(desc);							
						}
						imageMap.put(entry.getKey(), bgImage);	
					};
				};
			}
		}
		return imageURLSet;
	}




}
