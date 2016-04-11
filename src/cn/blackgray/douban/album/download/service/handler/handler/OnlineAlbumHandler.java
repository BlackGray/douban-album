package cn.blackgray.douban.album.download.service.handler.handler;

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
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;
import cn.blackgray.douban.album.download.service.image.ImageListComparator;

/**
 * ���ᴦ����
 */
public class OnlineAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES_ONLINE = 90;	//���Ƭ��ҳ��С��һҳ30��ͼ��
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://www.douban.com/online/\\d+/album/\\d+/";
	
	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}

	@Override
	public String getPageRegex() {
		return super.getAlbumURL() + "\\?\\w+=\\d+";
	}
	
	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_ONLINE;
	}

	@Override
	public String getPageTag() {
		return PAGE_TAG;
	}

	@Override
	public String getImageNameRegex() {
		return IMAGE_NAME_REGEX;
	}

	@Override
	public String getRawURL(String imageURL) {
		return null;
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {
		
//	    <div class="photo_wrap">
//	    <a href="http://www.douban.com/online/11127307/photo/1573338563/" title="�ҵĽ���" alt="�ҵĽ���">
//	    <img src="http://img1.douban.com/view/photo/thumb/public/p1573338563.jpg" />
//	    </a>
//	    <div class="pl" style="padding-bottom:8px;">
//	        �ҵĽ���
//	        <br />
//	    ���� <a href="http://www.douban.com/people/similer/">similer����</a>
//	    
//	    </div>
//	    </div>
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String onlineId = pageURL.substring(pageURL.indexOf("online/") + 7, pageURL.indexOf("/album"));

		//��������
		String startIndexStr = "<a href=\"https://www.douban.com/online/" + onlineId + "/photo/" + imageId + "/\" title=\"";
		int descStartIndex = source.indexOf(startIndexStr);
		String desc;
		if (descStartIndex != -1) {
			int start = descStartIndex + startIndexStr.length();
			desc = source.substring(start, source.indexOf("\"",start));
		}else{
			desc = "";
		}
		//��������ID&��ҳ��
		String ownerStartStr = "���� <a href=\"";
		int ownerStartIndex = source.indexOf(ownerStartStr,descStartIndex);
		String ownerA = source.substring(ownerStartIndex + 12, source.indexOf("</a>", ownerStartIndex));
		String ownerURL = "@@@@";
		try {
			ownerURL = ownerA.substring(0,ownerA.indexOf("/\">"));	
		} catch (Exception e) {
			System.out.println("=====================");
			System.out.println(ownerA);
			System.out.println(ownerA.indexOf("<a href=\"") + 9);
			System.out.println(ownerA.indexOf("/\">"));
			System.out.println("=====================");
			e.printStackTrace();
		}
		String ownerName = ownerA.substring(ownerA.indexOf(">") + 1);
		
		//����Ƭ��������
		String commentTatolStartIndexStr = "<a href=\"https://www.douban.com/online/" + onlineId + "/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//��3��Ӧ��
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("��Ӧ", ""));
		}
		
		
		//����Ƭ��
		imageURL = imageURL.replace("thumb", "photo").trim();	//thumb����>photo������ͼ����>��ͼ
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL)) {
			BGImage bgImage = new BGImage(desc, imageURL, commentTatol);
			bgImage.setOwnerName(ownerName);
			bgImage.setOwnerURL(ownerURL);
			map.put(imageURL, bgImage);
		}
	}
	
	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}
	
	@Override
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
			BufferedWriter bw = new BufferedWriter(new FileWriter(album.getPath() +  "/" + Common.DEFAULT_DOC_NAME));
			//�������ַ
			bw.write(URLUtils.charset + " " + album.getUrl() + " " +album.getDate().getTime() + " -");
			bw.newLine();
			//�����Ƭ��ַ������
			for (int i = 0; i < keyList.size(); i++) {
				BGImage bgImage = map.get(keyList.get(i));
				Integer commentTotal = bgImage.getCommentTotal();
				String commentTotalStr = commentTotal==null?"-":String.valueOf(commentTotal);
				bw.write((i + 1) + " " + keyList.get(i) + " " + commentTotalStr + " " + bgImage.getDesc());
				bw.newLine();
				//���ᣬ����û���&��ҳ��ַ
				bw.write(bgImage.getOwnerURL() + " " + bgImage.getOwnerName());
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
	
	
	@Override
	public List<BGImage> getBGImageFromDescDoc(File descFile) throws IOException {
		List<BGImage> list = new ArrayList<BGImage>();
		BufferedReader reader = new BufferedReader(new FileReader(descFile));
		BGImage tempBGImage = null;
		int line = 0;
		String str;
		while ((str = reader.readLine()) != null) {
			if (line == 0) {
				line++;
			}else{
				//0��Ϊ���&ҳ����Ϣ����������Ƭ��Ϣ��ż�����û���Ϣ
				if (line%2 == 1) {
					String[] info = str.split(" ",4);
					//info[0],info[1],info[2],info[3]�ֱ�Ϊ��Ƭ��š�ԭʼURL��ַ������������Ƭ����
					tempBGImage = new BGImage(info[0],info[1],info[3]);
					//������
					if (!info[2].equals("-")) {
						tempBGImage.setCommentTotal(Integer.valueOf(info[2]));
					}
					line++;
				}else{
					String[] info = str.split(" ",2);
					//info[0],info[1]�ֱ�Ϊ�û���ҳURL���û���
					BGImage bgImage = tempBGImage;
					bgImage.setOwnerURL(info[0]);
					bgImage.setOwnerName(info[1]);
					list.add(bgImage);
					line++;
				}
			}
		}
		reader.close();
		return list;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		//���۵�ַ
//		http://www.douban.com/online/11127307/album/72416214/
//		http://www.douban.com/online/11127307/photo/1573338563/
		return album.getUrl().substring(0, album.getUrl().indexOf("/album/")) + "/photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
	
}
