package cn.blackgray.douban.album.download.service;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.service.creator.HtmlCreator;
import cn.blackgray.douban.album.download.service.download.DownloadFailManager;
import cn.blackgray.douban.album.download.ui.FailFileFrame;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 下载业务类
 * @author BlackGray
 */
public class DownloadService {

	private static MainFrame mainFrame = MainFrame.getInstance();
	private static JProgressBar albumListProgressBar = mainFrame.albumListProgressBar;		//相册进度条
	private static JLabel albumListCountLabel = mainFrame.albumListCountLabel;				//相册进度条
	
	/**
	 * 下载
	 * @param urlList
	 */
	public static void download(List<String> urlList) {
		
		
		//【获取相册列表】
		List<Album> albums = new ArrayList<Album>();
		for (String url : urlList) {
			albums.addAll(AlbumFactory.getFromURL(url));
		}
		
		//【统计】
		long time = System.currentTimeMillis();	//总耗时
		long imagesCount = 0;					//照片总数

		//----------设置进度条&标签-----------
		albumListProgressBar.setMaximum(albums.size());
		albumListProgressBar.setValue(0);
		albumListCountLabel.setText("[0/" + albums.size() + "] ");
		//-----------------------------------
		
		//【执行处理】
		for (int i = 0; i < albums.size(); i++) {
			Album album = albums.get(i);
			//初始化相册对象
			album.init();
			//执行下载
			album.download();
			//修改进度条
			albumListProgressBar.setValue(i+1);
			albumListCountLabel.setText(i+1 + "/" + albums.size() + " ");
			imagesCount += album.getPhotosList().size();
		}
		
		//【显示统计信息】
		if (imagesCount != 0) {
			String sp = "                  ";
			StringBuffer sb = new StringBuffer();
			sb.append("==============================\r\n");
			sb.append(sp).append(" 相册总数：").append(albums.size()).append("(个)\r\n");
			sb.append(sp).append(" 照片总数:").append(imagesCount).append("(张)\r\n");
			sb.append(sp).append(" 成功:").append(imagesCount - DownloadFailManager.getFailSize()).append("(张)\r\n");
			sb.append(sp).append(" 失败:").append(DownloadFailManager.getFailSize()).append("(张)\r\n");
			sb.append(sp).append(" 总耗时:").append((System.currentTimeMillis() - time)/1000 + "s").append(sp).append("~\\(≧▽≦)/~");
			Console.print(sb.toString());
		}
		
		//【重下异常文件】
		//自动重新下载
		if (DownloadFailManager.getFailSize() != 0) {
			Console.print("【存在下载失败文件,尝试重新下载】");
			int num = 1;
			boolean success = false;
			while ((num < Common.AUTO_DOWNLOAD_FAIL_FILE) && !success) {
				success = DownloadFailManager.downloadFailFile();
				if (success) {
					Console.print("【失败文件下载完成】");
				}else{
					Console.print("【部分文件依然下载失败，显示失败文件列表】 - " + num);	
				}
				num++;
			}
		}
		//重下完成，监测是否依然存在错误文件，如果存在，显示错误文件窗口，否则生成HTML文档
		List<String> finishedAlbumPathList = new ArrayList<String>();
		for (Album a : albums) {
			finishedAlbumPathList.add(a.getPath());
		}
		if (DownloadFailManager.getFailSize() > 0) {
			//下载失败，显示错误文件窗口
			FailFileFrame frame = FailFileFrame.getInstance(finishedAlbumPathList);
			frame.setVisible(true);
		}else{
			//【生成HTML文档】
			if(imagesCount != 0) {
				Console.print("【正在生成HTML文档,请稍等】");
				HtmlCreator.createAlbumHTML(finishedAlbumPathList);
			}
			Console.print("【FINISH】");
			//设置界面下载按钮可用
			MainFrame.getInstance().downloadBtn.setEnabled(true);
		}
		
		
		
	}
	
	

}
