package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * ��Ӱ��ᴦ����
 */
public class MovieAlbumHandler extends AlbumHandler {

	//��Ӱ��ҳ
	//http://movie.douban.com/subject/3652297/?from=hot_movie
	//��Ӱ�����ҳ
	//http://movie.douban.com/subject/3652297/all_photos
	//����
	//http://movie.douban.com/subject/3652297/photos?type=S
	//����
	//http://movie.douban.com/subject/3652297/photos?type=R
	//��ֽ
	//http://movie.douban.com/subject/3652297/photos?type=W
			
	public static final int PAGE_SIZE_IMAGES_MOVIE = 30;//��Ӱ��Ƭ��ҳ��С��һҳ40��ͼ��
	public static final String PAGE_TAG = "start";
	public static final String IMAGE_NAME_REGEX = "p\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://movie.douban.com/subject/\\d+/photos\\?(\\w+=\\w+&*)+";
	
	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}
	
	@Override
	public String getPageRegex() {
		String url = getAlbumURL();
		if (url.indexOf("?") != -1) {
			url = url.substring(0,url.indexOf("?"));
		}
		System.out.println("==============");
		System.out.println(getAlbumURL() + "\\?(\\w+=\\w+&*(amp;)*)+");
		System.out.println(url  + "\\?(\\w+=\\w+&*(amp;)*)+");
		System.out.println("==============");
		return url + "\\?(\\w+=\\w+&*(amp;)*)+";
	}
	
	@Override
	public void setAlbumURL(String albumURL) {
		super.setAlbumURL(albumURL);
		Pattern p = Pattern.compile("type=\\w+");
		Matcher m = p.matcher(albumURL);
		if(m.find()) {
			this.albumURL += ("?" + m.group());
		}
	}
	
	@Override
	public String getAlbumURL() {
		if (albumURL.endsWith("/")) {
			return albumURL.substring(0, albumURL.length() - 1);
		}
		return albumURL;
	}
	
	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES_MOVIE;
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
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(String source, String pageURL, String imageURL, Map<String, BGImage> map) {
//        <li data-id="2158663965">
//        <div class="cover">
//                <a href="http://movie.douban.com/photos/photo/2158663965/">
//                    <img src="http://img3.douban.com/view/photo/thumb/public/p2158663965.jpg" />
//                </a>
//        </div>
//
//            <div class="prop">
//                1024x576
//            </div>
//                <div class="name">
//                    ���ð�������˵���˵�...
//                        <a href="http://movie.douban.com/photos/photo/2158663965/#comments">1��Ӧ</a>
//                </div>
//        </li>
		String imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		//��������
		String startIndexStr = "<a href=\"https://movie.douban.com/photos/photo/" + imageId + "/\">";
		int descStartIndex = source.indexOf(startIndexStr);
		String desc;
		if (descStartIndex != -1) {
			String str = "<div class=\"name\">";
			int start = source.indexOf(str, (descStartIndex + startIndexStr.length()));
			desc = source.substring(start + str.length(), source.indexOf("<",start+str.length()));
		}else{
			desc = "";
		}
		//����Ƭ��������
		//<a href="http://movie.douban.com/celebrity/1040543/photo/1261122420/#comments">29��Ӧ</a>
		String commentTatolStartIndexStr = "<a href=\"https://movie.douban.com/photos/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//��3��Ӧ��
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("��Ӧ", ""));
		}
		//����Ƭ��
//		http://img3.douban.com/view/photo/thumb/public/p2125663360.jpg
//		http://img3.douban.com/view/photo/photo/public/p2109950882.jpg
		imageURL = imageURL.replace("photo/m", "photo/l").trim();	//thumb����>photo������ͼ����>��ͼ
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
		//http://img3.douban.com/view/photo/thumb/public/p2109950882.jpg
		//http://movie.douban.com/photos/photo/2109950882/
		return "http://movie.douban.com/photos/photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
	
}
