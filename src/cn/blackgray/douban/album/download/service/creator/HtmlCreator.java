package cn.blackgray.douban.album.download.service.creator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.HTMLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.AlbumFactory;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.image.ImageInfo;
import cn.blackgray.douban.album.download.service.image.ImageUtils;

/**
 * HTMLҳ�����ɹ�����
 * @author BlackGray
 */
public class HtmlCreator {

	public static final String DESC_FRONT_COVER = "�������桿";
	public static final String DESC_DEFAULT = "-";

	/**
	 * �������HTMLҳ�� - ��������ȡʱ����
	 * @param url
	 * @return
	 * @throws IOException 
	 */
	public static boolean createAlbumHTML(String albumPath) throws IOException {

		File albumDir = new File(albumPath);
		if (!albumDir.exists()) {
			return false;
		}
		//��֤Ŀ¼���Ƿ����ļ�
		File[] imageFiles = albumDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().substring(f.getName().lastIndexOf(".") + 1).matches("(" + Common.IMAGE_TYPE + ")");
			}
		});
		if (imageFiles.length == 0) {
			return false;
		}

		Album album = null;
		try {
			album = AlbumFactory.getFromLocalFile(albumPath);
		} catch (IOException e) {
			Console.print(e.getMessage());
			Console.print("HTML�ĵ�����ʧ��");
		}
		
		String name = album.getName();
		AlbumHandler albumHandler = album.getAlbumHandler();
		List<BGImage> photosList = album.getPhotosList();
		
		//��ƴװҳ�桿
		String page = Common.HTML_TEMPLATE_PAGE;
//		page = page.replace(Common.HTML_TAG_CHARSET, album.getCharset());		//�ַ���
		page = page.replace(Common.HTML_TAG_TITLE, HTMLUtils.textToHTML(name) + "(" + photosList.size() + ")");	//����
		page = page.replace(Common.HTML_TAG_NAME, HTMLUtils.textToHTML(name));	//�����
		page = page.replace(Common.HTML_TAG_URL, album.getUrl());				//���ԭʼ��ַ
		//�������
		if (album.getDesc() == null || album.getDesc().equals("-")) {
			page = page.replace(Common.HTML_TAG_ALBUM_DESC, "");
		}else{
			page = page.replace(Common.HTML_TAG_ALBUM_DESC, HTMLUtils.textToHTML("�����������" + album.getDesc()));	
		}
		//����ʱ��
		page = page.replace(Common.HTML_TAG_DOWNLOAD_TIME, "������ʱ�䣺" + Common.SIMPLE_DATE_FORMAT.format(album.getDate()));
		//��Ƭ����
		page = page.replace(Common.HTML_TAG_IMAGES_TOTAL, "����Ƭ������" + photosList.size());	
		


		//��ƴװ��Ƭ�б�
		Collections.sort(photosList,new Comparator<BGImage>() {
			@Override
			public int compare(BGImage i1, BGImage i2) {
				String number1 = i1.getNumber();
				String number2 = i2.getNumber();
				int id1;
				int id2;
				if (number1.startsWith("O-")) {
					if (number2.startsWith("O-")) {
						//�����ļ�
						id1 = Integer.parseInt(number1.substring(number1.indexOf("O-") + 2));
						id2 = Integer.parseInt(number2.substring(number2.indexOf("O-") + 2));						
					}else{
						return 1;
					}
				}else{
					if (number2.startsWith("O-")) {
						return -1;					
					}else{
						//�����ļ�
						id1 = Integer.parseInt(number1);
						id2 = Integer.parseInt(number2);
					}
				};
				return id1 - id2;
			}
		});
		
		//�������Сվ&������ᣬ��ȡrawĿ¼���ļ��б�
		Set<String> rawSet = new HashSet<String>();
		if (albumHandler.hasRaw()) {
			File dir = new File(album.getPath() + File.separatorChar + "raw");
			if (dir.exists()) {
				for (File file : dir.listFiles()) {
					rawSet.add(file.getName());
				};
			}
		}

		//��ƴװJSON��
		StringBuffer images = new StringBuffer("[");
		for (BGImage image : photosList) {
			//��ȡֵ
			String desc = image.getDesc();
			if (desc.trim().length() == 0) {
				desc = DESC_DEFAULT;
			}else{
				if (desc.startsWith("��")) {
					desc = DESC_FRONT_COVER + desc.replaceAll("��+", "");
				};
			}
			
			images.append("{");
			//���
			images.append("'number':'").append(image.getNumber()).append("',");	
			//ͼƬ
			images.append("'name':'").append(HTMLUtils.textToJson(image.getName())).append("',");
			//����
			images.append("'desc':'").append(HTMLUtils.textToJson(desc)).append("',");	
			//��Ƭ������			
			if (image.getOwnerName() != null) {
				images.append("'ownerName':'").append(HTMLUtils.textToJson(image.getOwnerName())).append("',");
				images.append("'ownerURL':'").append(image.getOwnerURL()).append("',");
			}
			//����
			String commentURL = albumHandler.getCommentURL(album, image);
			if (commentURL != null) {
				images.append("'commentURL':'").append(commentURL).append("',");	
			}
			//��Ӧ��
			Integer commentTotal = image.getCommentTotal();
			if (commentTotal != null) {
				images.append("'commentTotal':'").append(commentTotal).append("',");	
			}
			
			
			
			//�����rawĿ¼��
			if (albumHandler.hasRaw()) {
				if (rawSet.contains(image.getName())) {
					try {
						//�ж�ͼƬ��С�������С��ͬ
						ImageInfo imageInfo = ImageUtils.getImageSize(album.getPath() + File.separatorChar + image.getName());
						ImageInfo rawInfo = ImageUtils.getImageSize(album.getPath() + File.separatorChar + Common.DEFAULT_RAW_DIR + File.separatorChar + image.getName());
						if ((imageInfo.getWidth() == rawInfo.getWidth()) && imageInfo.getHeight() == rawInfo.getHeight()) {
							images.append("'raw':'").append(Common.RAW_TYPE_UNCOMPRESSED).append("'");
						}else{
							images.append("'raw':'").append(Common.RAW_TYPE_LARGE).append("'");
						}						
					} catch (Exception e) {
						//����Աȳ����쳣��Ĭ��RAW�ߴ����
						images.append("'raw':'").append(Common.RAW_TYPE_LARGE).append("'");
					}
				};
			}
			
			//ȥ������Ķ���
			if (images.substring(images.length() - 1, images.length()).equals(",")) {
				images.delete(images.length() - 1, images.length());				
			}
			
			images.append("},");
		}
		images.delete(images.length() - 1, images.length());
		images.append("]");
		page = page.replace(Common.HTML_TAG_IMAGES, images.toString());
		
		File file = new File(album.getPath() + File.separator + Common.DEFAULT_HTML_NAME);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(page.toString());
		bw.flush();
		bw.close();


		//�����Դ�ļ�
		List<String> resourceList = new ArrayList<String>(); 
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/bg.jpg");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/numberBg.png");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/half-l.png");
		resourceList.add("/cn/blackgray/douban/album/download/resources/html/half-d.png");
		File dir = new File(album.getPath() + File.separator + Common.DEFAULT_HTML_RESOURCE_DIR);
		if (!dir.exists()) {
			dir.mkdirs();	
		}
		for (String resource : resourceList) {
			InputStream inputStream = Common.class.getResourceAsStream(resource);
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			String path = dir.getAbsolutePath() + File.separator + resource.substring(resource.lastIndexOf("/") + 1);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path));
			int i;
			while ((i = bis.read()) != -1) {
				bos.write(i);
			}
			bos.flush();
			bos.close();
			bis.close();
		}
		return true;
	}

	//����������ҳ
	public static void createAlbumHTML(List<String> paths) {
		for (String path : paths) {
			try {
				HtmlCreator.createAlbumHTML(path);
			} catch (IOException e) {
				Console.print(e.getMessage());
				Console.print("HTML�ĵ�����ʧ��");
				e.printStackTrace();
			}
		}		
	}

}
