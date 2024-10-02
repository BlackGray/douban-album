package cn.blackgray.douban.album.download.test;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestJarFile {
	
	public static void main(String[] args) throws IOException {
		
		JarFile jar = new JarFile("C:/Users/Think/Desktop/x.jar");
		String pakageName = "cn.blackgray.douban.album.download.service.handler.finder.impl"; 
		pakageName = pakageName.replaceAll("\\.", "/");
		
		Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) {
			JarEntry jarEntry = (JarEntry) e.nextElement();
			String entryName = jarEntry.getName();
			if (entryName.indexOf(pakageName) != -1 && entryName.endsWith(".class")) {
				System.out.println("name:" + entryName);
			}
		}
		jar.close();
		
	}

}
