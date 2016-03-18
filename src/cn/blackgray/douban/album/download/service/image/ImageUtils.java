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
 * ͼƬ������
 * @author BlackGray
 */
public class ImageUtils {

	/**
	 * ��ȡͼƬ�ߴ���Ϣ
	 * ͨ��ImageReader��ȡͼƬ�ߴ�
	 * Ч�ʸ���BufferedImage
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
			//��������쳣��ʹ�ñ��÷�����ȡ�ߴ�
			//javax.imageio.IIOException: Not a JPEG file: starts with 0x89 0x50
			//�����쳣����ͼƬΪPNG��ʽ����׺ΪJPG
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
			throw new RuntimeException("ͼƬ�ļ��ߴ��ȡ�쳣��" + e.getMessage());
		}
		return null;
	}
	
	
	
	public static void main(String[] args) {
		
		//��TEST - ͼƬ��С��Ϣ��ȡ�ԱȲ��ԡ�
		String path = "D:\\raw";
		File dir = new File(path);
		//1.ͨ��ImageReader��ȡͼƬ�ߴ�
		long begin = System.currentTimeMillis();
		for (File picture : dir.listFiles()) {
			getImageSize(picture.getPath());
		};
		System.out.println("��getImageSize��" + (System.currentTimeMillis() - begin));
		
		//2.ͨ��BufferedImage��ȡͼƬ�ߴ�
		begin = System.currentTimeMillis();
		for (File picture : dir.listFiles()) {
			getImageSizeByBufferedImage(picture.getPath());
		};
		System.out.println("��getImageSizeByBufferedImage��" + (System.currentTimeMillis() - begin));
		
		
		
		
	}
}

