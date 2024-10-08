package cn.blackgray.douban.album.download.common.utils;

import java.io.File;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.ui.LoginQrFrame;
import cn.blackgray.douban.album.download.ui.MainFrame;

/**
 * 登录工具类
 * 
 * @author BlackGray
 * @createTime 2024-10-02 03:12:27
 */
public class LoginUtils {

	public static WebDriver CHROME_DRIVER = null;
	//是否已登录，默认为false
	public static boolean IS_LOGIN = false;			
	//豆瓣登录首页
	private static String DOUBAN_LOGIN_URL = "https://accounts.douban.com/passport/login";
	//是否正在执行登陆中
	public static boolean IS_LOGGING_IN = false;
	//chromedirver下载地址
	private static String CHROME_DRIVER_DOWNLOAD_URL = "https://googlechromelabs.github.io/chrome-for-testing/";
	

	
	/**
	 * 初始化ChromeDriver
	 */
	public static WebDriver initChromeDriver() {
		// 获取 chromedirver 的存放位置
//		String chromedriverPath = "/Users/blackgray/Downloads/chromedriver-mac-arm64/chromedriver";
		String chromedriverPath = Common.PATH_APP + File.separator + "chromedriver";
		if (CommonUtils.isWindows()) {
			chromedriverPath = chromedriverPath + ".exe";
		}
		
		//检查chromedriver文件是否存在
		File chromedriverFile = new File(chromedriverPath);
		if (chromedriverFile.exists()) {
			//若存在，执行初始化
			Console.print("检测chromedriver文件是否存在 - 存在");
			
			Console.print("加载chromedriver - " + chromedriverPath);
			System.getProperties().setProperty("webdriver.chrome.driver", chromedriverPath);
			
			ChromeOptions chromeOptions = new ChromeOptions();

			chromeOptions.addArguments("--no-sandbox"); // 不使用沙箱
			chromeOptions.addArguments("--disable-dev-shm-usage");
			// chromeOptions.addArguments("blink-settings=imagesEnabled=false"); //不加载图片
			chromeOptions.addArguments("--disable-gpu"); // 禁用GPU
			chromeOptions.addArguments("--remote-allow-origins=*");

			// 使用后台打开chrome的方式
			chromeOptions.addArguments("--headless");
			return new ChromeDriver(chromeOptions);
		}else {
			//若不存在，提醒下载、打开下载地址
			Console.print("检测chromedriver文件是否存在 - 不存在。");
			Common.openURLWithBrowse(CHROME_DRIVER_DOWNLOAD_URL, MainFrame.getInstance());
			Console.print("已使用默认浏览器打开chromedriver下载界面。");
			Console.print("请下载与自己Chrome大版本一致的chromedriver。");
			Console.print("下载后拷贝文件至下载器相同目录下，再重新执行登陆操作。");
			return null;
		}
	}

	/**
	 * 登陆执行
	 */
	public static void login() {
		
		Console.print("正在准备登陆，请稍等...");

		try {
			CHROME_DRIVER = initChromeDriver();
			if (CHROME_DRIVER != null) {
				// 1.模拟打开登陆页面
				String url = DOUBAN_LOGIN_URL;
				Console.print("打开登陆页面，URL：" + url);
				CHROME_DRIVER.get(url);

				// 2.等5秒钟响应后再操作，不然内容可能还没有返回
				Console.print("睡眠3s，等待页面加载完整。");
				Thread.sleep(3 * 1000l);
				Console.print("打页面标题：" + CHROME_DRIVER.getTitle());
				// System.out.println("========================");
				// System.out.println(driver.getPageSource());
				// System.out.println("========================");
				// webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

				// 3.获取二维码登录切换按钮
				WebElement loginTypeTab = CHROME_DRIVER.findElement(By.xpath("/html/body/div[1]/div[2]/div[2]/div/div[1]/div/div[1]/a[1]"));
				// 点击TAB，切换至密码登录表单
				loginTypeTab.click();
				Console.print("睡眠1s。");
				Thread.sleep(1 * 1000l);
				// Console.print("输出页面源码，检查二维码是否已生成。");
				// System.out.println(driver.getPageSource());

//				//4.获取二维码 - 保存到本地
//		        //存储截图文件的文件夹位置
//		        String dir = "/Users/blackgray/Downloads/selenium/";
//		        //文件名
//		        String imageFileName = "qr.png";
//		        //文件完整路径
//		        String imageFileFullName = dir + imageFileName;
//		        //本地文件转为文件类型
//		        File imageFile = new File(imageFileFullName);
//		        
//				WebElement qrParent = CHROME_DRIVER.findElement(By.className("account-qr-scan"));
//				WebElement qr = qrParent.findElements(By.xpath("./*")).get(0);
//				
//				//将元素对应的部分截图并转为文件类型
//				File eleScreenShotFile = qr.getScreenshotAs(OutputType.FILE);
//		        //将截图文件复制到本地文件
//		        FileUtils.copyFile(eleScreenShotFile, imageFile);

				// 4.获取二维码
				// 本地文件转为文件类型
				WebElement qrParent = CHROME_DRIVER.findElement(By.className("account-qr-scan"));
				WebElement qr = qrParent.findElements(By.xpath("./*")).get(0);
				// 将元素对应的部分截图并转为文件类型 - selenium截图默认使用了Java临时文件，程序关闭，文件将自动删除
				File eleScreenShotFile = qr.getScreenshotAs(OutputType.FILE);
				Console.print("获取登陆二维码 - " + eleScreenShotFile.getAbsolutePath());

				// 5.显示二维码
				LoginQrFrame frame = new LoginQrFrame();
				ImageIcon qrImage = new ImageIcon(eleScreenShotFile.getAbsolutePath());
				JLabel lable = new JLabel(qrImage);
				lable = new JLabel(qrImage);
				frame.setLayout(null);
				frame.add(lable);
				lable.setBounds(0, 0, frame.getWidth(), frame.getHeight() - 40);

				// 显示界面
				MainFrame mainFrame = MainFrame.getInstance();
				frame.setLocationRelativeTo(mainFrame);
				frame.setLocation(mainFrame.getLocation().x + 400, mainFrame.getLocation().y + 80);
				frame.setVisible(true);
				frame.revalidate();
				frame.setResizable(false);
				
				Console.print("等待使用豆瓣APP扫描二维码登录。");
				//检查是否登陆成功
				//设置登录中标识
				LoginUtils.IS_LOGGING_IN = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						//定时检查页面，确认是否登陆成功后跳转首页
						while (true) {
							if (LoginUtils.IS_LOGGING_IN) {
								try {
									Console.print("检查是否已登录 - 未登录。");
									if (!LoginUtils.CHROME_DRIVER.getCurrentUrl().equals(DOUBAN_LOGIN_URL)) {
										// 是否已登录标识更新
										LoginUtils.IS_LOGIN = true;
										Console.print("登陆成功，可开始执行下载。");
										//取消登录中标识
										LoginUtils.IS_LOGGING_IN = false;
										frame.setVisible(false);
										//若有录入相册地址，自动开始下载
										MainFrame mainFrame = MainFrame.getInstance();
										if (mainFrame.albumTextArea.getText().length() > 0) {
											mainFrame.downloadBtn.doClick();
										}
										break;
									}
									Thread.sleep(1 * 1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}else {
								Console.print("取消登录。");
								break;
							}
						}
					}
				}).run();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取Cookie字符串
	 * @param cookies
	 * @return
	 */
	public static String getCookiesStr(WebDriver driver) {
		Set<Cookie> cookies = driver.manage().getCookies();
		String cookieStr = convertCookiesToString(cookies);
//		System.out.println("【CookiesStr】" + cookieStr);
		return cookieStr;
    }
	
	/**
	 * Cookie集合转为字符串
	 * @param cookies
	 * @return
	 */
	private static String convertCookiesToString(Set<Cookie> cookies) {
        StringBuilder cookieBuilder = new StringBuilder();
        for (Cookie cookie : cookies) {
            if (cookieBuilder.length() > 0) {
                cookieBuilder.append("; ");
            }
            cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue());
        }
        return cookieBuilder.toString();
    }

}
