package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;

import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 默认相册处理器
 */
public class DefaultAlbumHandler extends AlbumHandler {

	@Override
	public String getURLRegex() {
		return null;
	}

	@Override
	public String getAlbumURL() {
		String url = super.getAlbumURL();
		if (url.endsWith("/")) {
			return url.substring(0, url.length() - 1);
		}else{
			return url;
		}
	}

	@Override
	public String getPageRegex() {
		return null;
	}

	@Override
	public Integer getPageSize() {
		return 0;
	}

	@Override
	public String getPageTag() {
		return null;
	}

	@Override
	public String getImageNameRegex() {
		return null;
	}

	@Override
	public String getRawURL(String imageURL) {
		return null;
	}


	@Override
	public void createBGImage(String source, String pageURL, String imageURL,Map<String, BGImage> map) {
		map.put(imageURL, new BGImage("", imageURL));
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		return null;
	}

	@Override
	public String getAlbumDesc(String source) {
		return null;
	}
	
	
	
}
