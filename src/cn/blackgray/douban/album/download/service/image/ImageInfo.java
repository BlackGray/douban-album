package cn.blackgray.douban.album.download.service.image;

/**
 * 图片信息MODEL - 用于对比图片宽高尺寸
 * @author BlackGray
 */
public class ImageInfo {

	private int height;
	private int width;

	public ImageInfo(int width, int height) {
		super();
		this.height = height;
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		return "height: " + height + ", width: " + width;
	}
	
}
