package cn.blackgray.douban.album.download.service.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * 图片工具类
 * @author BlackGray
 */
public class ImageUtils {

	/**
	 * 获取图片尺寸信息
	 * 通过ImageReader获取图片尺寸
	 * 效率高于BufferedImage
	 * @param path
	 */
	public static ImageInfo getImageSize(String path){
		try {
			File file = new File(path);
			String readImageFormat = path.substring(path.lastIndexOf(".") + 1, path.length());
			Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(readImageFormat);
			ImageReader reader = (ImageReader) readers.next();
			ImageInputStream iis = ImageIO.createImageInputStream(file);
			reader.setInput(iis, true);
			return new ImageInfo(reader.getWidth(0), reader.getHeight(0));
		} catch (IOException e) {
			//如果出现异常，使用备用方法获取尺寸
			//javax.imageio.IIOException: Not a JPEG file: starts with 0x89 0x50
			//上述异常表是图片为PNG格式，后缀为JPG
			return getImageSizeByBufferedImage(path);
		}
	}
	
	private static ImageInfo getImageSizeByBufferedImage(String path){
		File picture = new File(path);
		BufferedImage sourceImg;
		try {
			sourceImg = ImageIO.read(new FileInputStream(picture));
			return new ImageInfo(sourceImg.getWidth(), sourceImg.getHeight());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
			//javax.imageio.IIOException: Error reading PNG image data
			throw new RuntimeException("图片文件尺寸获取异常：" + e.getMessage());
		}
		return null;
	}
	
	
	
	public static void main(String[] args) {
		
		//【TEST - 图片大小信息获取对比测试】
		String path = "D:\\raw";
		File dir = new File(path);
		//1.通过ImageReader获取图片尺寸
		long begin = System.currentTimeMillis();
		for (File picture : dir.listFiles()) {
			getImageSize(picture.getPath());
		};
		System.out.println("【getImageSize】" + (System.currentTimeMillis() - begin));
		
		//2.通过BufferedImage获取图片尺寸
		begin = System.currentTimeMillis();
		for (File picture : dir.listFiles()) {
			getImageSizeByBufferedImage(picture.getPath());
		};
		System.out.println("【getImageSizeByBufferedImage】" + (System.currentTimeMillis() - begin));
		
		
		
		
	}
}

