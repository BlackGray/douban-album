package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * ������ᴦ����
 */
public class UserAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES = 18;			//��Ƭ��ҳ��С��һҳ18��ͼ��
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://www.douban.com/photos/album/\\d+/";
	
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
		return PAGE_SIZE_IMAGES;
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
		return true;
	}

	@Override
	public String getRawURL(String imageURL) {
		//http://img3.douban.com/view/photo/photo/public/p1932887553.jpg - Сͼ
		//http://img3.douban.com/view/photo/large/public/p1932887553.jpg - ��ͼ
		return imageURL.replace("photo/lphoto", "photo/large").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {

		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		
		//��Ƭ����
		String descStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/\" class=\"photolst_photo\" title=\"";
		int descStartIndex = source.indexOf(descStartIndexStr);
		String desc;
		if (descStartIndex != -1) {
			desc = source.substring(descStartIndex + descStartIndexStr.length(), source.indexOf("\">",descStartIndex));
		}else{
			desc = "";
		}
		//��Ƭ����
		String commentTatolStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//��3��Ӧ��
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("��Ӧ", ""));
		}
		
		//��ӵ�����
		imageURL = imageURL.replace("thumb", "photo").trim();	//thumb����>photo������ͼ����>��ͼ
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL)) {
			map.put(imageURL, new BGImage(desc, imageURL, commentTatol));
		}else{
			BGImage bgImage = map.get(imageURL);
			if (bgImage.getCommentTotal()!=null && commentTatol==null) {
				commentTatol = bgImage.getCommentTotal();
			}
			map.put(imageURL, new BGImage("��" + bgImage.getDesc(), imageURL, commentTatol));
		}
	}
	
	
	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		//http://www.douban.com/photos/album/67952443/
		//http://www.douban.com/photos/photo/1560777504/
		return "http://www.douban.com/photos/photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		String startTag = "data-desc=\"";
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
	
}
