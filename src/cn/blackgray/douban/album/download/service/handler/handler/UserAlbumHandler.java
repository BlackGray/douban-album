package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 个人相册处理器
 */
public class UserAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES = 18;			//照片分页大小（一页18张图）
	public static final String PAGE_TAG = "m_start";
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
		//已失效
		//http://img3.douban.com/view/photo/photo/public/p1932887553.jpg - 小图
		//http://img3.douban.com/view/photo/large/public/p1932887553.jpg - 大图
		
		//2017-11-06
		//小图
		//https://img1.doubanio.com/view/photo/m/public/p2504126549.webp
		//大图
		//https://img3.doubanio.com/view/photo/l/public/p2504126600.webp
		//原始图
		//https://img3.doubanio.com/view/photo/large/public/p2504126600.jpg
		
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {

		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		
		//照片描述
		String descStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/\" class=\"photolst_photo\" title=\"";
		int descStartIndex = source.indexOf(descStartIndexStr);
		String desc;
		if (descStartIndex != -1) {
			desc = source.substring(descStartIndex + descStartIndexStr.length(), source.indexOf("\">",descStartIndex));
		}else{
			desc = "";
		}
		//照片评论
		String commentTatolStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//“3回应”
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("回应", ""));
		}
		
		//添加到集合
		//m——>l：缩略图——>大图
		imageURL = imageURL.replace("photo/m", "photo/l").trim();	
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL)) {
			map.put(imageURL, new BGImage(desc, imageURL, commentTatol));
		}else{
			BGImage bgImage = map.get(imageURL);
			if (bgImage.getCommentTotal()!=null && commentTatol==null) {
				commentTatol = bgImage.getCommentTotal();
			}
			map.put(imageURL, new BGImage("※" + bgImage.getDesc(), imageURL, commentTatol));
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
			if (desc.indexOf("【") != -1) {
				desc = desc.substring(desc.lastIndexOf("】") + 1);
			}
			return desc;
		}else{
			return null;
		}
	}
	
}
