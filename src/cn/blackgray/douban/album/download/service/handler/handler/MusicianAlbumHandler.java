package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 音乐人相册处理器
 */
public class MusicianAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES_MUSICIAN = 30;	//音乐人照片分页大小（一页40张图）
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://music.douban.com/musician/\\d+/photos/";
	
	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}
	
	@Override
	public String albumNameProcess(String name) {
		return name = "音乐人-" + super.albumNameProcess(name);
	}

	@Override
	public String getPageRegex() {
		//音乐人相册分页含有多个参数
		return getAlbumURL() + "\\?(\\w+=\\w+&*(amp;)*)+";
	}
	
	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_MUSICIAN;
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
		//https://img3.doubanio.com/view/photo/photo/public/p825770544.jpg
		//https://img3.doubanio.com/view/photo/raw/public/p825770544.jpg
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {
//		===============图片描述===============
//	  <li>
//		<div class="cover"><a href="https://music.douban.com/musician/100144/photo/1318874397/">
//		<img src="https://img1.doubanio.com/view/photo/thumb/public/p1318874397.webp" /></a></div>
//
//        <div class="prop">
//            414x613
//        </div>
//        <div class="name">
//            此妞只应天上有
//                <a href="https://music.douban.com/musician/100144/photo/1318874397/#comments">1回应</a>
//        </div>
//    </li>
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String musicianId = pageURL.substring(pageURL.indexOf("musician/") + 9, pageURL.indexOf("/photos"));
		//【描述】
		String startIndexStr = "<a href=\"https://music.douban.com/musician/" + musicianId + "/photo/" + imageId + "/\"";
		int descStartIndex = source.indexOf(startIndexStr);
		String desc;
		if (descStartIndex != -1) {
			String str = "<div class=\"name\">";
			int start = source.indexOf(str, (descStartIndex + startIndexStr.length()));
			desc = source.substring(start + str.length(), source.indexOf("<",start+str.length()));
		}else{
			desc = "";
		}
		//【照片评论数】
		//<a href="https://music.douban.com/musician/100144/photo/1318874397/#comments">1回应</a>
		String commentTatolStartIndexStr = "<a href=\"https://music.douban.com/musician/" + musicianId + "/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//“3回应”
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("回应", ""));
		}
		//【照片】
		imageURL = imageURL.replace("photo/m", "photo/l").trim();	//thumb——>photo：缩略图——>大图
		desc = desc.replace("\\t\\n","").trim();
		if (!map.containsKey(imageURL)) {
			BGImage bgImage = new BGImage(desc, imageURL, commentTatol);
			map.put(imageURL, bgImage);
		}
	}
	
	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		//https://music.douban.com/musician/100144/photos/
		//https://music.douban.com/musician/100144/photo/825770544/
		return album.getUrl().replace("photos", "photo") + "/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
	 
}
