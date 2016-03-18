package cn.blackgray.douban.album.download.service.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.ReflectUtils;
import cn.blackgray.douban.album.download.service.handler.finder.IAlbumURLFinder;
import cn.blackgray.douban.album.download.service.handler.handler.DefaultAlbumHandler;


/**
 * ��ᴦ���������࣬���ݲ�ͬ����Ϣ�����ɲ�ͬ����Ϣ������ʵ��
 */
public class AlbumHandlerFactory {

	public static final String PACKAGE_FINDER = "cn.blackgray.douban.album.download.service.handler.finder.impl";
	public static final String PACKAGE_HANDER = "cn.blackgray.douban.album.download.service.handler.handler";

	public static Map<String, IAlbumURLFinder> albumURLFinderMap = new HashMap<String, IAlbumURLFinder>();
	public static Map<String, Class<?>> albumHandlerClassMap = new HashMap<String, Class<?>>();


	static{
		//�����ȡ��������ַ��ѯ������
		List<Class<?>> finderClassList = ReflectUtils.getClassWithPackage(PACKAGE_FINDER);
		for (Class<?> finderClass : finderClassList) {
			try {
				IAlbumURLFinder obj = (IAlbumURLFinder) finderClass.newInstance();
				albumURLFinderMap.put(obj.getURLRegex(), obj);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		//�����ȡ���д�������
		List<Class<?>> handlerClassList = ReflectUtils.getClassWithPackage(PACKAGE_HANDER);
		for (Class<?> handerClass : handlerClassList) {
			try {
				AlbumHandler obj = (AlbumHandler) handerClass.newInstance();
				albumHandlerClassMap.put(obj.getURLRegex(), handerClass);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<AlbumHandler> getHandler(String url) {
		return getHandler(url,true);
	}
	
	public static List<AlbumHandler> getHandler(String url, boolean isPrintLog) {
		List<String> albumURLList = new ArrayList<String>(); 
		//1.�鿴�Ƿ����URL��ѯ��������У�ִ�в�ѯ����ȡ����ַ
		boolean hasFinder = false;
		for (Entry<String, IAlbumURLFinder> element : albumURLFinderMap.entrySet()) {
			if (url.matches(element.getKey())) {
				IAlbumURLFinder albumURLFinder = element.getValue();
				List<String> albumURLs = albumURLFinder.findAlbumURL(url);
				for (String u : albumURLs) {
					if(isPrintLog){
						Console.print("��ȡ����ַ��" + u);
					}
				}
				albumURLList.addAll(albumURLs);
				hasFinder = true;
				break;
			}
		}
		if (hasFinder == false) {
			albumURLList.add(url);
			if(isPrintLog){
				Console.print("��ȡͼƬ��ַ��" + url);
			}
		}
		//2.��������ַ����ȡ������
		List<AlbumHandler> handlerList = new ArrayList<AlbumHandler>();
		boolean hasHander = false;
		for (String albumURL : albumURLList) {
			for (Entry<String, Class<?>> element : albumHandlerClassMap.entrySet()) {
				if (element.getKey() != null && albumURL.matches(element.getKey())) {
					Class<?> clazz = element.getValue();
					AlbumHandler handler;
					try {
						handler = (AlbumHandler) clazz.newInstance();
						handler.setAlbumURL(albumURL);
						handlerList.add(handler);
						if(isPrintLog){
							Console.print("������ᴦ������" + clazz.getSimpleName() + " - " + albumURL);
						}
						hasHander = true;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			if (hasHander == false) {
				DefaultAlbumHandler defaultAlbumHandler = new DefaultAlbumHandler();
				defaultAlbumHandler.setAlbumURL(albumURL);
				handlerList.add(defaultAlbumHandler);
				if(isPrintLog){
					Console.print("����Ĭ����ᴦ������" + defaultAlbumHandler.getClass().getSimpleName() + " - " + albumURL);
				}
			}
		}
		return handlerList;

	}

	public static void main(String[] args) {

		System.out.println("-------------����б�--------------");
		getHandler("http://www.douban.com/people/blackgray/photos/");
		System.out.println("-------------���--------------");
		getHandler("http://www.douban.com/photos/album/67952443/");

		System.out.println("-------------Сվ--------------");
		getHandler("http://site.douban.com/108128/widget/photos/7528342/");
		getHandler("http://site.douban.com/zheng/widget/photos/17304118/");

		System.out.println("-------------Ӱ����ҳ--------------");
		getHandler("http://movie.douban.com/celebrity/1048027/");
		System.out.println("-------------Ӱ��--------------");
		getHandler("http://movie.douban.com/celebrity/1048027/photos/");

		System.out.println("-------------���ҳ--------------");
		getHandler("http://www.douban.com/online/11127307/");
		System.out.println("-------------�--------------");
		getHandler("http://www.douban.com/online/11127307/album/72416214/");

		System.out.println("-------------����--------------");
		getHandler("http://www.baidu.com/");

	}

}
