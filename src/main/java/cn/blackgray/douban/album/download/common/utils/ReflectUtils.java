package cn.blackgray.douban.album.download.common.utils;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具类
 * @author BlackGray
 */
public class ReflectUtils {

	public static Integer PARA_TYPE_INCLUDE = 0;
	public static Integer PARA_TYPE_EXCLUDE = 1;

	/**
	 * 通过反射获取对象的所有属性
	 * 返回MAP集合
	 * 键值为对象属性名称
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static Map<String,Object> getFieldValues(Object obj) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		return getFieldValues(obj,null);
	}

	public static Map<String,Object> getFieldValues(Object obj,Integer paraType, Object... paras) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		//如果有参数，判断参数类型并处理
		Set<String> feildNameSet = new HashSet<String>();
		if (paraType != null) {
			if (paraType == PARA_TYPE_INCLUDE || paraType == PARA_TYPE_EXCLUDE) {
				for (String name : paras[0].toString().split(",")) {
					feildNameSet.add(name.trim());						
				}
			}
		}
		Map<String,Object> map = new HashMap<String, Object>();
		Method[] methods = obj.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			String methodName = methods[i].getName();
			if (methodName.startsWith("get") || methodName.startsWith("is")) {
				//判断返回值类型，获取变量名
				Class<?> returnTypeClass = methods[i].getReturnType();
				String feildName;
				if(returnTypeClass.equals(boolean.class) || returnTypeClass.equals(Boolean.class)){
					feildName = methodName.substring(2,3).toLowerCase() + methodName.substring(3);
				}else{
					feildName = methodName.substring(3,4).toLowerCase() + methodName.substring(4);
				}
				boolean flag = true;
				if (paraType == PARA_TYPE_INCLUDE) {
					flag = feildNameSet.contains(feildName);
				}
				if (paraType == PARA_TYPE_EXCLUDE) {
					flag = !feildNameSet.contains(feildName);
				}
				if (flag) {
					//获取变量类型
					//Type type = methods[i].getGenericReturnType();
					map.put(feildName, methods[i].invoke(obj));	
				}
			}
		}
		return map;
	}

	/**
	 * 根据包获取所有类
	 * @param pkg
	 * @return
	 */
	public static List<Class<?>> getClassWithPackage(String pkg){
		//判断环境&//获取JAR包
		try {
			String jarPath = URLDecoder.decode(ReflectUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"utf-8");
			File file = new File(jarPath);
			if (file.isFile() && file.getName().endsWith("jar")) {
				JarFile jar = new JarFile(jarPath);
				return getClassWithPackageFromJar(jar,pkg);
			}else{
				return getClassWithPackageFromDir(pkg);
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从Jar中加载类文件 - 运行环境中使用
	 * @param pkg
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	private static List<Class<?>> getClassWithPackageFromJar(JarFile jar,String pkg) throws IOException, ClassNotFoundException{
		//获取所有指定包内的类
		String pakageName = pkg.replaceAll("\\.", "/"); 
		List<Class<?>> list = new ArrayList<Class<?>>();
		ClassLoader loader = ReflectUtils.class.getClassLoader();
		Enumeration<JarEntry> e = jar.entries();
		while (e.hasMoreElements()) { 
			JarEntry jarEntry = (JarEntry) e.nextElement();
			String entryName = jarEntry.getName();
			if (entryName.indexOf(pakageName) != -1 && entryName.endsWith(".class")) {
				list.add(loader.loadClass(entryName.replace("/", ".").replace(".class", "")));
			}
		}
		return list;
	}

	/**
	 * 从目录中加载类文件 - 开发环境中使用
	 * @param pkg
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws ClassNotFoundException 
	 */
	private static List<Class<?>> getClassWithPackageFromDir(String pkg) throws UnsupportedEncodingException, ClassNotFoundException{
		List<Class<?>> list = new ArrayList<Class<?>>();
		String jarPath = URLDecoder.decode(ReflectUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(),"utf-8");
		File dir = new File(jarPath + "/" + pkg.replaceAll("\\.", "/"));
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".class");
			}
		});
		for (File f : files) {
			String className = f.getName().substring(0, f.getName().indexOf("."));
			Class<?> clazz = Class.forName(pkg + "." + className);
			list.add(clazz);
		}
		return list;
	}


	public static void main(String[] args) {
		List<Class<?>> list = getClassWithPackage("cn.blackgray.douban.album.download.service.handler.finder.impl");
		for (Class<?> cls : list) {
			System.out.println(cls);
		}
	}

}
