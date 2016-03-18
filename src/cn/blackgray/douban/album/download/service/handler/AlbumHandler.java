package cn.blackgray.douban.album.download.service.handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.image.ImageListComparator;

/**
 * ��ᴦ�������� - �������ڴ�����ȡ�������Ƭ��Ϣ����ַ���������������ȣ�
 * @author BlackGray
 */
public abstract class AlbumHandler{

	protected String albumURL;
	
	public AlbumHandler() {
		super();
	}

	public AlbumHandler(String albumURL) {
		super();
		this.albumURL = albumURL;
	}
	
	public String getAlbumURL() {
		return albumURL;
	}
	
	/**
	 * ��ȡ�������
	 */
	public abstract String getAlbumDesc(String source);
	
	
	public void setAlbumURL(String albumURL) {
		boolean isRemoveURLPara = removeURLParameter();
		if (isRemoveURLPara && (albumURL.indexOf("?") > 0)) {
			this.albumURL = albumURL.substring(0, albumURL.indexOf("?"));				
		}else{
			this.albumURL = albumURL;			
		}
	}

	/**
	 * ��ȡҳ����Ƭ����С
	 */
	public abstract Integer getPageSize();
	
	
	/**
	 * ��ȡ��ҳ��ǩ
	 * ����Ϊstart
	 */
	public abstract String getPageTag();
	
	/**
	 * ������ƴ���
	 * @param name
	 * @return
	 */
	public String albumNameProcess(String name){
		return name;
	}
	
	/**
	 * ��ȡ���URL����
	 * @return
	 */
	public abstract String getURLRegex();
	
	/**
	 * ��ȡ����ҳ����
	 * @return
	 */
	public abstract String getPageRegex();
	
	/**
	 * ͼƬ��������
	 * �����׼ȷ��
	 * @return
	 */
	public abstract String getImageNameRegex();
	
	/**
	 * �Ƿ��д�ͼ
	 * @return
	 */
	public boolean hasRaw(){
		return false;
	}
	
	/**
	 * ��ȡ��ͼ��ַ
	 * @return
	 */
	public abstract String getRawURL(String imageURL);
	
	/**
	 * ��ȡ���۵�ַ
	 * @param album
	 * @param image
	 * @return
	 */
	public abstract String getCommentURL(Album album, BGImage image);
	
	
	/**
	 * ɾ��URL�еĲ���
	 * @return
	 */
	public boolean removeURLParameter(){
		return false;
	}
	
	
	/**
	 * ����Դ���ͼƬ·������������ͼƬ����
	 * @param source
	 * @param imageURL
	 * @param map 
	 * @param imageURL2 
	 * @return
	 */
	public abstract void createBGImage(String source,String pageURL, String imageURL, Map<String, BGImage> map);
	
	/**
	 * ��ͼ��ַ����
	 * @return
	 */
	public boolean checkBGImage(BGImage bgImage){
		return true;
	}

	/**
	 * ���������ĵ�
	 * @param album
	 */
	public void createDescDoc(Album album) {
		List<BGImage> imageList = album.getPhotosList();
		Map<String,BGImage> map = new HashMap<String,BGImage>();
		for (BGImage bgImage : imageList) {
			map.put(bgImage.getUrl(), bgImage);
		}
		List<String> keyList = new ArrayList<String>(map.keySet());
		//����
		Collections.sort(keyList,new ImageListComparator());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(album.getPath() + "/" + Common.DEFAULT_DOC_NAME));
			//�������ַ
			if (album.getDesc() == null) {
				bw.write(URLUtils.charset + " " + album.getUrl() + " " + album.getDate().getTime() + " -");
			}else{
				bw.write(URLUtils.charset + " " + album.getUrl() + " " + album.getDate().getTime() + " " + album.getDesc());
			}
			bw.newLine();
			//�����Ƭ��ַ����������ʽ����� + ������ + ��Ƭ��ַ + ����
			for (int i = 0; i < keyList.size(); i++) {
				BGImage bgImage = map.get(keyList.get(i));
				Integer commentTotal = bgImage.getCommentTotal();
				String commentTotalStr = commentTotal==null?"-":String.valueOf(commentTotal);
				bw.write((i + 1) + " " + keyList.get(i) + " " + commentTotalStr + " " + bgImage.getDesc());
				bw.newLine();
			}
			bw.flush();
			bw.close();
			Console.print("���������ĵ����ɹ�");
		} catch (IOException e) {
			Console.print("���������ĵ���ʧ��");
			e.printStackTrace();
		}
	}
	
	/**
	 * �������ĵ���ȡͼƬ����
	 * @param descFile
	 * @return
	 * @throws IOException 
	 */
	public List<BGImage> getBGImageFromDescDoc(File descFile) throws IOException{
		List<BGImage> list = new ArrayList<BGImage>();
		if (descFile.exists()) {
			BufferedReader reader = new BufferedReader(new FileReader(descFile));
			String str;
			int line = 0;
			while ((str = reader.readLine()) != null) {
				if (line == 0) {
					line++;
				}else{
					//��ȡ��Ƭ��ַ&������Ϣ
					String[] info = str.split(" ",4);
					//info[0],info[1],info[2],info[3]�ֱ�Ϊ��Ƭ��š�ԭʼURL��ַ������������Ƭ����
					BGImage bgImage = new BGImage(info[0],info[1],info[3]);
					//������
					if (!info[2].equals("-")) {
						bgImage.setCommentTotal(Integer.valueOf(info[2]));
					}
					list.add(bgImage);
				}
			}
			reader.close();
		}
		return list;
	}
	
}
