package cn.blackgray.douban.album.download.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.swing.JTextArea;

/**
 * 控制台日志输出
 * @author BlackGray
 */
public class Console {

	public static JTextArea consoleArea;
	public static Long count = 0L;
	private static Integer MAX_LINE = 1000;
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private static boolean isDebug = true;

	public static void print(String text){
		if (isDebug) {
			System.out.println(text);
		}
		if (consoleArea != null) {
			synchronized (consoleArea) {
				int lineCount = consoleArea.getLineCount();
				if (lineCount >= MAX_LINE) {
					int end = 0;
					try {
						end = consoleArea.getLineEndOffset(MAX_LINE);
					} catch (Exception e) {
					}
					consoleArea.replaceRange("", 0, end);
				}
				consoleArea.append(sdf.format(new Date()) + " | " + text + "\r\n");
				consoleArea.setCaretPosition(consoleArea.getText().length());			//滚动条显示到最下方
				count++;
			}
		}
	}

	public static void setArea(JTextArea consoleArea) {
		Console.consoleArea = consoleArea;
	}

	public static void print(Logger log, String msg) {
		log.info(msg);
		print(msg);
	}

}
