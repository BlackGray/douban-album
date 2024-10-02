package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 人士相册处理器
 * 
 * 豆瓣2024年改版，将原有影人（celebrity）、音乐人（musician）合并为人士（personage）。
 */
public class PersonageAlbumHandler extends AlbumHandler {

//  人士相册示例地址
//	https://www.douban.com/personage/27246297/
//	https://www.douban.com/personage/27246297/photos/
	
	public static final int PAGE_SIZE_IMAGES_CELEBRITY = 30;//影人照片分页大小（一页30张图）
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://www.douban.com/personage/\\d+/photos/";
		

	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}
	
	@Override
	public String albumNameProcess(String name) {
		return name = "人士-" + super.albumNameProcess(name);
	}

	@Override
	public String getPageRegex() {
		//影人相册分页含有多个参数
		//?start=30&sortby=like&size=a&subtype=a
		//?start=30&sortby=comment&size=a&subtype=a
		//?start=30&sortby=time&size=a&subtype=a
		return "\\?\\w+=\\d+";
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
	public boolean hasRaw(Album album) {
		return true;
	}

	@Override
	public String getRawURL(String imageURL) {
		//https://img2.doubanio.com/view/photo/photo/public/p2905078171.webp
		//https://img2.doubanio.com/view/photo/l/public/p2905078171.webp
		//https://img2.doubanio.com/view/photo/raw/public/p2905078171.jpg
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(Album album, String source, String pageURL, String imageURL, Map<String, BGImage> map) {
//		===============图片描述===============
//		<li>
//        <a href="/personage/27503633/photo/2374131186" target="_blank">
//			<img src="https://img9.doubanio.com/view/photo/photo/public/p2374131186.webp">
//		  </a>
//            <div class="size">1334x719</div>
//        <div class="name">鼻血。。。
//            <a href="https://www.douban.com/personage/27503633/photo/2374131186/#comments">51回应</a>
//        </div>
//    	</li>
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		String personageId = pageURL.substring(pageURL.indexOf("personage/") + 10, pageURL.indexOf("/photos"));
		//【描述】
		String startIndexStr = "<a href=\"/personage/" + personageId + "/photo/" + imageId + "\" target=\"_blank\"";
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
		//<a href="https://www.douban.com/personage/27503633/photo/2374131186/#comments">51回应</a>
		String commentTatolStartIndexStr = "<a href=\"https://www.douban.com/personage/" + personageId + "/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//“51回应”
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("回应", ""));
		}
		//【照片】
		imageURL = imageURL.replace("photo/photo", "photo/l").trim();	//thumb——>photo：缩略图——>大图
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
		//https://www.douban.com/personage/27503633/photos/
		//https://www.douban.com/personage/27503633/photo/2145121738/
		return album.getUrl().replace("photos", "photo") + "/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
}
