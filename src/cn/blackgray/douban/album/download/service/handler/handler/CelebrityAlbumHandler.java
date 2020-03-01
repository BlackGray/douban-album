package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 影人相册处理器
 */
public class CelebrityAlbumHandler extends AlbumHandler {

	public static final int PAGE_SIZE_IMAGES_CELEBRITY = 30;//影人照片分页大小（一页40张图）
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://movie.douban.com/celebrity/\\d+/photos/";

	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}
	
	@Override
	public String albumNameProcess(String name) {
		return name = "影人-" + super.albumNameProcess(name);
	}

	@Override
	public String getPageRegex() {
		//影人相册分页含有多个参数
		//http://movie.douban.com/celebrity/1040543/photos/?type=C&start=0&sortby=all&size=a&subtype=a
		//http://movie.douban.com/celebrity/1040543/photos/?type=C&start=40&sortby=all&size=a&subtype=a
		//http://movie.douban.com/celebrity/1040543/photos/?type=C&amp;start=0&amp;sortby=all&amp;size=a&amp;subtype=a - html显示效果
		return getAlbumURL() + "\\?(\\w+=\\w+&*(amp;)*)+";
	}
	
	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_CELEBRITY;
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
		//http://img3.douban.com/view/photo/thumb/public/p730185909.jpg
		//http://img5.douban.com/view/photo/photo/public/p730185909.jpg
		//http://img5.douban.com/view/photo/raw/public/p730185909.jpg
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {
//		===============图片描述===============
//        <li>
//        <div class="cover">
//            <a href="http://movie.douban.com/celebrity/1040543/photo/1261122420/" class="">
//                <img src="http://img3.douban.com/view/photo/thumb/public/p1261122420.jpg" class="" />
//            </a>
//        </div>
//
//        <div class="prop">
//            465x640
//        </div>
//            <div class="name">
//        娇嫩欲滴~
//                    <a href="http://movie.douban.com/celebrity/1040543/photo/1261122420/#comments">29回应</a>
//            </div>
//    </li>
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String celebrityId = pageURL.substring(pageURL.indexOf("celebrity/") + 10, pageURL.indexOf("/photos"));
		//【描述】
		String startIndexStr = "<a href=\"https://movie.douban.com/celebrity/" + celebrityId + "/photo/" + imageId + "/\" class=\"";
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
		//<a href="http://movie.douban.com/celebrity/1040543/photo/1261122420/#comments">29回应</a>
		String commentTatolStartIndexStr = "<a href=\"https://movie.douban.com/celebrity/" + celebrityId + "/photo/" + imageId + "/#comments\">";
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
		//http://movie.douban.com/celebrity/1041142/photos/
		//http://movie.douban.com/celebrity/1041142/photo/1596128090/
		return album.getUrl().replace("photos", "photo") + "/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
}
