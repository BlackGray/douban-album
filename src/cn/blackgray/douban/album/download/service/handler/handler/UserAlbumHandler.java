package cn.blackgray.douban.album.download.service.handler.handler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.blackgray.douban.album.download.common.Common;
import cn.blackgray.douban.album.download.common.Console;
import cn.blackgray.douban.album.download.common.utils.URLUtils;
import cn.blackgray.douban.album.download.model.Album;
import cn.blackgray.douban.album.download.model.BGImage;
import cn.blackgray.douban.album.download.service.handler.AlbumHandler;

/**
 * 个人相册处理器
 */
public class UserAlbumHandler extends AlbumHandler {

	//照片分页大小（一页18张图）
	public static final int PAGE_SIZE_IMAGES = 18;			
	public static final String PAGE_TAG = "m_start";
	
	//若为私密相册，照片前缀为x，公共相册照片前缀为p
	public static final String IMAGE_NAME_REGEX = "(x|p)\\d+.(" + Common.IMAGE_TYPE + ")";
	public static final String ALBUM_URL_REGEX = "(http|https)://www.douban.com/photos/album/\\d+/";
	
	@Override
	public String getURLRegex() {
		return ALBUM_URL_REGEX;
	}

	@Override
	public String getPageRegex() {
		return super.getAlbumURL() + "\\?\\w+=\\d+";
	}
	
	@Override
	public boolean removeURLParameter() {
		return true;
	}

	@Override
	public Integer getPageSize() {
		return PAGE_SIZE_IMAGES;
	}

	@Override
	public String getPageTag() {
		return PAGE_TAG;
	}

	@Override
	public String getImageNameRegex() {
		return IMAGE_NAME_REGEX;
	}

	@Override
	public boolean hasRaw(Album album) {
		if (album.getIsPrivateAlbum()) {
			//私密相册没有原始大图，不做判断下载
			return false;
		}else {
			return true;
		}
	}

	@Override
	public String getRawURL(String imageURL) {
		//最初 - 没有原始图 - 已失效
		//http://img3.douban.com/view/photo/photo/public/p1932887553.jpg - 小图
		//http://img3.douban.com/view/photo/large/public/p1932887553.jpg - 大图
		
		//2017-11-06之前
		//小图
		//https://img1.doubanio.com/view/photo/m/public/p2504126549.webp
		//大图
		//https://img3.doubanio.com/view/photo/l/public/p2504126600.webp
		//原始图
		//https://img3.doubanio.com/view/photo/large/public/p2504126600.jpg
		
		//2017-11-06
		//原始图地址有变，地址中photo/large变为photo/raw
		
		//2024-10-02
		//检查豆瓣，发现页面中已经无法访问原始图，点击“查看大图”，显示的图与此前"l"类型图片大小一致。
		//但通过程序仍能下载原始大图，地址中photo/l，改为photo/raw。
		//若在界面中访问原始大图，需手动调整地址，地址中photo/l，改为photo/large。
		//很迷惑的操作，不理解。豆瓣内部有问题？
		
		return imageURL.replace("photo/l", "photo/raw").trim();
	}

	@Override
	public void createBGImage(Album album, String source, String pageURL, String imageURL, Map<String, BGImage> map) {
		
		//【1】若未做过判断，判断是否为私密相册
		if (!album.getIsPrivateAlbum()) {
			//通过图片前缀判断，x为私密，p为公开
			String imageNameRegexToPrivate = "x\\d+.(" + Common.IMAGE_TYPE + ")";
			String imageName = imageURL.substring(imageURL.lastIndexOf("/") + 1);
			if (imageName.matches(imageNameRegexToPrivate)) {
				//私密相册
				album.setIsPrivateAlbum(true);
			}
		}
		
		
		
		//【2】获取照片描述、评论信息
		//获取照片ID
		String imageId;
		if (album.getIsPrivateAlbum()) {
			//私密相册处理实现
			imageId = imageURL.substring(imageURL.lastIndexOf("/x") + 2,imageURL.lastIndexOf("."));
		}else {
			//公共相册处理实现
			imageId = imageURL.substring(imageURL.lastIndexOf("/p") + 2,imageURL.lastIndexOf("."));
		}
		//照片描述
		String descStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/\" class=\"photolst_photo\" title=\"";
		int descStartIndex = source.indexOf(descStartIndexStr);
		String desc;
		if (descStartIndex != -1) {
			desc = source.substring(descStartIndex + descStartIndexStr.length(), source.indexOf("\">",descStartIndex));
		}else{
			desc = "";
		}
		//照片评论
		String commentTatolStartIndexStr = "<a href=\"https://www.douban.com/photos/photo/" + imageId + "/#comments\">";
		int commentTatolStartIndex = source.indexOf(commentTatolStartIndexStr);
		Integer commentTatol = null;
		if (commentTatolStartIndex != -1) {
			//“3回应”
			String s = source.substring(commentTatolStartIndex + commentTatolStartIndexStr.length(), source.indexOf("</a>",commentTatolStartIndex));
			commentTatol = Integer.valueOf(s.replace("回应", ""));
		}
		
		
		//【3】获取照片地址
		if (album.getIsPrivateAlbum()) {
			//私密相册处理实现
			//如果是私密相册，涉及每张照片地址单独获取，时间较长，输出日志明细
			Console.print("---------------------------------------");
			album.setPrivatePhotoURLAnalyzeTotal(album.getPrivatePhotoURLAnalyzeTotal() + 1);
			Console.print("获取私密相册照片地址开始 - " + album.getPrivatePhotoURLAnalyzeTotal() + " - " + pageURL);
			
			//根据照片ID生成照片单独页面URL
			String photoPageURL = "https://www.douban.com/photos/photo/" + imageId;
			Console.print("获取私密相册照片页面源码 - " + photoPageURL);
			String privatePhotoPageSource = URLUtils.readSource(photoPageURL);
			
			//获取照片URL
			imageURL = getPrivatePhotoURL(privatePhotoPageSource);
			if (imageURL != null) {
				Console.print("获取私密照片URL成功 - " + imageURL);
			}else{
				Console.print("获取私密照片URL失败 - 无有效照片URL");
			}
			
		}else {
			//公共相册处理实现
			/**
			 * 2024-10-02 增加备注
			 * 个人相册分公开相册、私有相册
			 * 公开相册缩略图地址示例：https://img3.doubanio.com/view/photo/m/public/p881668782.jpg
			 * 私有相册缩略图地址示例：https://simg.douban.com/view/photo/m/-urE8H6nKPAUXOxX_PVPWA/2745512/x2196276230.jpg
			 * 
			 * 图片名称公开的前缀为p，私有的为x。
			 * 
			 * 私有照片大图地址示例：https://simg.douban.com/view/photo/l/6NrUwD0fG4RjxvP7FjkzLA/2745512/x2196276230.jpg
			 * 地址中包含随机生成字符串"6fph28J_c7MrPu9_LQT0Pw",每张图片值不同。
			 * 且字符串只在明细界面可见，在清单界面中无信息。
			 * 导致无法通过简单修改固定字符串方式推测大图地址。
			 * 
			 * 私有照片原始大图示例：https://simg.douban.com/view/photo/l/6NrUwD0fG4RjxvP7FjkzLA/2745512/x2196276230.jpg
			 * 和大图地址相同？与老图片没有RAW原图有关？待检查其他图片示例
			 * 检查豆瓣，发现页面中已经无法访问原始图，点击“查看大图”，显示的图与此前"l"类型图片大小一致。
			 * 但通过程序仍能下载原始大图。
			 */
			//推测大图地址，加入集合。若为私密相册，无法通过此方式推测地址。
			//m——>l：缩略图——>大图
			imageURL = imageURL.replace("photo/m", "photo/l").trim();	
		}
		
		//【4】照片加入集合
		if (imageURL != null) {
			if (!map.containsKey(imageURL)) {
				desc = desc.replace("\\t\\n","").trim();
				map.put(imageURL, new BGImage(desc, imageURL, commentTatol));
			}else{
				//标注相册首页
				BGImage bgImage = map.get(imageURL);
				if (bgImage.getCommentTotal()!=null && commentTatol==null) {
					commentTatol = bgImage.getCommentTotal();
				}
				map.put(imageURL, new BGImage("※" + bgImage.getDesc(), imageURL, commentTatol));
			}
		}
		
	}
	
	
	@Override
	public boolean checkBGImage(BGImage bgImage) {
		return bgImage.getUrl().indexOf("albumicon") < 0;
	}

	@Override
	public String getCommentURL(Album album, BGImage image) {
		//http://www.douban.com/photos/album/67952443/
		//http://www.douban.com/photos/photo/1560777504/
		return "http://www.douban.com/photos/photo/" + image.getId();
	}

	@Override
	public String getAlbumDesc(String source) {
		String startTag = "data-desc=\"";
		if (source.indexOf(startTag) != -1) {
			int startIndex = source.indexOf(startTag) + startTag.length();
			String desc = source.substring(startIndex,source.indexOf("\"", startIndex)).replace("\\t\\n","").trim();
			if (desc.indexOf("【") != -1) {
				desc = desc.substring(desc.lastIndexOf("】") + 1);
			}
			return desc;
		}else{
			return null;
		}
	}
	
	
	/**
	 * 获取私密相册照片地址
	 */
	private String getPrivatePhotoURL(String source) {
		
		//示例源码
		//view-source:https://www.douban.com/photos/photo/2196276230/
		//需获取的照片地址
		//https://simg.douban.com/view/photo/l/6NrUwD0fG4RjxvP7FjkzLA/2745512/x2196276230.jpg
		
		//截取出包含照片地址的代码段
		int beginIndex = source.indexOf("image-show-inner");
		int endIndex = source.indexOf("</a>", beginIndex);
		source = source.substring(beginIndex, endIndex);
		
		//获取照片地址
		String regex = "(http|https)://(\\w|\\s|\\.|-|_|/)+[\\.](" + Common.IMAGE_TYPE + ")";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		
		return null;
	}
	
	
}
