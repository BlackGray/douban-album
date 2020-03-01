package cn.blackgray.douban.album.download.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class TestReflect {

	public static List<String[]> getJarMethod(String jarFile) throws Exception {
		String NORMAL_METHOD = "waitequalsnotifynotifyAlltoStringhashCodegetClass";
		List<String[]> a = new ArrayList<String[]>();
		try {
			// 通过jarFile 和JarEntry得到所有的类
			JarFile jar = new JarFile(jarFile);// "D:/sip-test.jar"
			Enumeration<?> e = jar.entries();

			while (e.hasMoreElements()) {
				JarEntry entry = (JarEntry) e.nextElement();
				// entry.getMethod()
				if (entry.getName().indexOf("META-INF") < 0) {
					String sName = entry.getName();
					String substr[] = sName.split("/");
					String pName = "";
					for (int i = 0; i < substr.length - 1; i++) {
						if (i > 0)
							pName = pName + "/" + substr[i];
						else
							pName = substr[i];
					}
					if (sName.indexOf(".class") < 0) {
						sName = sName.substring(0, sName.length() - 1);
					} else {
						// 通过URLClassLoader.loadClass方法得到具体某个类
						URL url1 = new URL("file:D:\\sip-test.jar");
						URLClassLoader myClassLoader = new URLClassLoader(new URL[] { url1 }, Thread.currentThread().getContextClassLoader());
						String ppName = sName.replace("/", ".").replace(".class", "");
						System.out.println("ppName:" + ppName);
						Class<?> myClass = myClassLoader.loadClass(ppName);
						// 通过getMethods得到类中包含的方法
						Method m[] = myClass.getMethods();
						for (int i = 0; i < m.length; i++) {
							String sm = m[i].getName();
							if (NORMAL_METHOD.indexOf(sm) < 0) {
								String[] c = { sm, sName };
								a.add(c);
							}
						}
						myClassLoader.close();
					}
					String[] b = { sName, pName };
					a.add(b);
				}
			}
			jar.close();
			return a;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return a;
	}

	public static void main(String[] args) throws Exception {
		List<String[]> list = TestReflect.getJarMethod("C:/Users/Think/Desktop/x.jar");
		for (String[] arr : list) {
			System.out.println(Arrays.toString(arr));
		}
	}
}
