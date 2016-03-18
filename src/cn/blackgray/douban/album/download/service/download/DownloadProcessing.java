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
 * ���ش�������
 * @author BlackGray
 */
public class DownloadProcessing {

	private static MainFrame mainFrame = MainFrame.getInstance();

	private static JProgressBar processUnitProgressBar = mainFrame.processUnitProgressBar;	//����Ԫ������
	private static JLabel processUnitCountLabel = mainFrame.processUnitCountLabel;			//����Ԫ������


	
	/**
	 * �������ģ���������
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void downloadAlbum(Album album){

		//���������ʱ��
		long albumDownloadTime = System.currentTimeMillis();
		//����ͳ��
		int updateCount = 0;	

		Map<String,BGImage> imageMap = new HashMap<String,BGImage>();	//�������ȫ��Ƭ���ϡ�

		//������Ŀ¼��
		DirUtils.createDir(album);

		//����������Ԫ��
		//����Ԫ����
		int processUnitMax = new Double(Math.ceil((double)album.getPageURLLsit().size()/Common.PROCESS_UNIT_SIZE)).intValue();
		int processUnitNumber = 0;	//����Ԫ����
		processUnitProgressBar.setMaximum(processUnitMax);
		processUnitProgressBar.setValue(0);
		processUnitCountLabel.setText("0/" + processUnitMax + " ");
		for (int j = 0; j < processUnitMax; j++) {
			//����Ԫ��ʱ
			long processUnitTime = System.currentTimeMillis();
			//ȡ���涨������¼��ִ�д���Ԫ
			List<String> pageURLList = new ArrayList<String>();
			int start = processUnitNumber * Common.PROCESS_UNIT_SIZE;
			int end = start + Common.PROCESS_UNIT_SIZE;
			if (end > album.getPageURLLsit().size()) {
				end = album.getPageURLLsit().size();
			}
			for (int k = start; k < end; k++) {
				pageURLList.add(album.getPageURLLsit().get(k));
			}
			//���� - ���ظ�����Ϣ
			updateCount += processUnit(album,imageMap,pageURLList);

			//����Ԫ����+1
			processUnitNumber++;
			processUnitProgressBar.setValue(j + 1);
			processUnitCountLabel.setText((j + 1) + "/" + processUnitMax + " ");
			//���ж��������ߡ�
			//����Ԫ������1�����Ҳ������һ�δ����ִ�������ж�
			if (processUnitMax > 1 && (j + 1) != processUnitMax) {
				long t = System.currentTimeMillis() - processUnitTime;
				Console.print("����Ԫ��ʱ��" + t);
				if (t < Common.TIME_PROCESS_MIN) {
					Console.print("��ʱ�����ҳ��������࣬��������~");
					Console.print("(����(#��)��t�ro(�����///)");
					long c = Common.TIME_PROCESS_SLEEP;
					while (true) {
						if (c <= 0) {
							Console.print("[]~(������)~* ");
							break;
						}
						try {
							Thread.sleep(1000);
							Console.print("���ߵ���ʱ��" + c + "\t (���㧥��)\"");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						c--;
					}
				}
			}
		}
		//����ᴦ����ɣ�map��ֵ���ṩ��������ʹ��
		album.setPhotosList(new ArrayList<BGImage>(imageMap.values()));

		//��������
		if (album.getPhotosList().size() != 0) {
			//�����������ļ���
			album.createDescDoc();
			//�����ͳ����Ϣ��
			Console.print("���������� - " + album.getName());
			Console.print(" ������" + album.getPhotosList().size());
			if (album.isUpdate()) {
				Console.print(" ����:" + updateCount + "(��)");
			}
			Console.print(" ������ʱ:" + (System.currentTimeMillis() - albumDownloadTime)/1000 + "s");
		}else{
			Console.print("��ʾ��ʧ�ܻ�ҳ����ͼ��");
		}

	}

	
	/**
	 * ����Ԫ
	 * @param album
	 * @param imageMap
	 * @param pageURLList
	 * @param processUnitMax
	 * @param processUnitNumber
	 * @return ͼƬ������
	 */
	private static int processUnit(Album album, Map<String,BGImage> imageMap,List<String> pageURLList){
		int update = 0;
		//����Ϣ��ȡ��
		Console.print("����Ԫ��������Ϣ��ȡ");
		Set<String> imageURLSet = infoProcess(album, imageMap, pageURLList);
		//������ͼƬ��
		Console.print("����Ԫ����ʼ���أ�" + album.getName() + "(" + imageURLSet.size() + "��)");
		update = DownloadManager.downloadImage(new ArrayList<String>(imageURLSet),album.getPath());

		//�������Сվ���&������ᣬ���ش�ͼ��
		AlbumHandler albumHandler = album.getAlbumHandler();
		if (albumHandler.hasRaw()) {
			Console.print("����Ԫ����Ⲣ���ش�ͼ");
			//����Ŀ¼
			String path = album.getPath() + File.separatorChar + "raw";
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
			//����ȡ��ַ��
			//Сվ��ͼ
			List<String> list = new ArrayList<String>();
			for (String url : imageURLSet) {
				list.add(albumHandler.getRawURL(url));
			}
			//ִ������
			update += DownloadManager.downloadImage(list,path);			
		}
		return update;
	}

	
	/**
	 * ��Ƭ��Ϣ����
	 * @param imageMap
	 * @param pageURLList
	 * @param processUnitMax
	 * @param processUnitNumber
	 * @return
	 */
	private static Set<String> infoProcess(Album album, Map<String,BGImage> imageMap,List<String> pageURLList){

		Set<String> imageURLSet = new HashSet<String>();	//��Ҫ���ص�ͼƬ - ������Ԫ

		for (int i = 0; i < pageURLList.size(); i++) {
			Console.print("����ҳ��(" + (i + 1) + "/" + pageURLList.size() + ")��" + pageURLList.get(i));
			//��ѯ��ҳ������Ƭ��ַ������,���ܻ���ִ�����������������·���һ��
			Map<String, BGImage> map = new HashMap<String, BGImage>();
			try {
				map = PageAnalyzer.findImageURLAndDesc(album, pageURLList.get(i));
			} catch (Exception e) {
				try {
					map = PageAnalyzer.findImageURLAndDesc(album, pageURLList.get(i));	
				} catch (Exception e2) {
					Console.print("ҳ�������������ʧ�ܣ�" + pageURLList.get(i));
				}
				e.printStackTrace();
			}

			//������Ƭ��ַ��������Ϣ
			for (Entry<String, BGImage> entry : map.entrySet()) {
				if (!imageMap.containsKey(entry.getKey())) {
					imageMap.put(entry.getKey(), entry.getValue());
					imageURLSet.add(entry.getKey());
				}else{
					//��ÿ��ҳ�涼����������Ƭ�����ӣ�ɨ����Ƭ��ַʱ���п���ÿ�ζ���������Ƭ���д���
					//������Ƭ����������һҳ�У���ȡ������Ϣʱ����������ҳ���Եõ������⣬����ҳ��������Ϊ��
					//�����ж�ÿҳͼƬ�Ƿ���ڽ��������ڣ�˵����ͼ�Ƿ�����Ƭ
					//����ͼƬ�Ƿ�������������ȷ��ͼƬ�Ƿ��ڸ�ҳ
					if (imageMap.get(entry.getKey()).getDesc().equals("")) {
						//���֮ǰ��ӵ�����ͼû�����������ҵ�ǰͼ������������������Ϣ���������ҳͼƬ��ʶ������ִ���κβ���
						if (entry.getValue().getDesc().equals("")) {
							//�������photo.getValue().equals("")�жϣ����ܳ��ֵ������
							//1 false 	""
							//2 false 	""		-> ��
							//3 true	"DESC"  -> ��
							BGImage image = imageMap.get(entry.getKey());
							image.setDesc("��" + entry.getValue().getDesc());
							imageMap.put(entry.getKey(), image);
						}else{
							imageMap.put(entry.getKey(), entry.getValue());
						}
					}else {
						//���֮ǰ��ӵ�ͼ�������������ҳͼƬ��ʶ
						BGImage bgImage = imageMap.get(entry.getKey());
						String desc = ("��" + bgImage.getDesc()).replaceAll("��+", "��");
						if (desc.equals("��")) {
							bgImage = entry.getValue();
							bgImage.setDesc("��" + bgImage.getDesc());
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
