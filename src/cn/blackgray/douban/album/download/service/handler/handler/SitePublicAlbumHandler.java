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
 * Сվ������ᴦ����
 */
public class SitePublicAlbumHandler extends AlbumHandler {

//	http://site.douban.com/127530/widget/public_album/7152314/
//	http://site.douban.com/127530/widget/public_album/7152314/?start=30
		
	public static final int PAGE_SIZE_IMAGES_SITE = 30;		//Сվ���������Ƭ��ҳ��С��һҳ30��ͼ��
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://site.douban.com/\\d+/widget/public_album/\\d+/";

	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}

	@Override
	public String getPageRegex() {
//		/127530/widget/public_album/7152314/?start=30
		return "/widget/public_album/\\d+/\\?\\w+=\\d+";
	}

	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_SITE;
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
	public boolean hasRaw() {
		return false;
	}

	@Override
	public String getRawURL(String imageURL) {
		return null;
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {

//		<li>
//		    <div class="photo-item">
//		    <a href="http://site.douban.com/127530/widget/public_album/7152314/photo/1781015133/" title="��ׯ" alt="��ׯ" class="album_photo" id="p1781015133"><img src="http://img3.douban.com/view/photo/thumb/public/p1781015133.jpg" ></a>
//		    <div class="desc">
//		    
//		    <p>��ׯ</p>
//		    ���� <a href="http://www.douban.com/people/48218002/">Ȱ��Ī����</a>
//		        <a href="http://site.douban.com/127530/widget/public_album/7152314/photo/1781015133/#comments">1��Ӧ</a>
//		    </div>
//		    </div>
//	    </li>
		
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String siteAlbumId = pageURL.substring(pageURL.indexOf("public_album/") + 13, pageURL.lastIndexOf("/"));
		//��������
//		<a href="http://site.douban.com/127530/widget/public_album/7152314/photo/1781015133/" title="
		String startIndexStr = pageURL.substring(0,pageURL.indexOf(siteAlbumId)) + siteAlbumId + "/photo/" + imageId + "/\" title=\"";
		int descStartIndex = source.indexOf(startIndexStr);
		String desc;
		if (descStartIndex != -1) {
			int start = descStartIndex + startIndexStr.length();
			desc = source.substring(start, source.indexOf("\"",start));
		}else{
			desc = "";
		}
		//����Ƭ��������
		//<a href="http://site.douban.com/127530/widget/public_album/7152314/photo/1781015133/#comments">1��Ӧ</a>
		String commentTatolStartIndexStr = pageURL.substring(0,pageURL.indexOf(siteAlbumId)) + siteAlbumId + "/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//��3��Ӧ��
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("��Ӧ", ""));
		}
		//��������ID&��ҳ��
		String ownerURL = null;
		String ownerName = null;
		if (descStartIndex != -1) {
			String ownerStartStr = "���� <a href=\"";
			int ownerStartIndex = source.indexOf(ownerStartStr,descStartIndex);
			String ownerA = source.substring(ownerStartIndex + 12, source.indexOf("</a>", ownerStartIndex));
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
			ownerName = ownerA.substring(ownerA.indexOf(">") + 1);
		}
		//����Ƭ��
		//http://img3.douban.com/view/photo/thumb/public/p1748477871.jpg
		imageURL = imageURL.replace("thumb", "photo").trim();		//thumb����>photo������ͼ����>��ͼ
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL) || (map.containsKey(imageURL) && (map.get(imageURL).getOwnerURL()==null && ownerURL!=null))) {
			BGImage bgImage = new BGImage(desc, imageURL, commentTatol);
			bgImage.setOwnerURL(ownerURL);
			bgImage.setOwnerName(ownerName);
			map.put(imageURL, bgImage);
		}

	}


	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
//		http://site.douban.com/127530/widget/public_album/7152314/
//		http://site.douban.com/127530/widget/public_album/7152314/photo/1781015215/
		return album.getUrl() + "photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		String startTag = "data-title=\"";
		if (source.indexOf(startTag) != -1) {
			int startIndex = source.indexOf(startTag) + startTag.length();
			String desc = source.substring(startIndex,source.indexOf("\"", startIndex)).replace("\\t\\n","").trim();
			if (desc.indexOf("��") != -1) {
				desc = desc.substring(desc.lastIndexOf("��") + 1);
			}
			return desc;
		}else{
			return null;
		}
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

}

