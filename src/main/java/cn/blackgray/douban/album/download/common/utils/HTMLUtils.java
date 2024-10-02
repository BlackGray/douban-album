package cn.blackgray.douban.album.download.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTML字符转义工具类
 * @author BlackGray
 */
public class HTMLUtils {
	
	private static Map<String,String> htmlSignMap = new HashMap<String,String>();			//HTML特殊字符集合(HTML-TEXT)
	private static Map<String,String> htmlSignMapReversed = new HashMap<String,String>();	//HTML特殊字符集合(TEXT-HTML)
	
	static{
		//初始化符号集合
		htmlSignMap.put("&Alpha;","Α");
		htmlSignMap.put("&Beta;", "Β");
		htmlSignMap.put("&Gamma;", "Γ");
		htmlSignMap.put("&Delta;", "Δ");
		htmlSignMap.put("&Epsilon;", "Ε");
		htmlSignMap.put("&Zeta;", "Ζ");
		htmlSignMap.put("&Eta;", "Η");
		htmlSignMap.put("&Theta;", "Θ");
		htmlSignMap.put("&Iota;", "Ι");
		htmlSignMap.put("&Kappa;", "Κ");
		htmlSignMap.put("&Lambda;", "Λ");
		htmlSignMap.put("&Mu;", "Μ");
		htmlSignMap.put("&Nu;", "Ν");
		htmlSignMap.put("&Xi;", "Ξ");
		htmlSignMap.put("&Omicron;", "Ο");
		htmlSignMap.put("&Rho;", "Ρ");
		htmlSignMap.put("&Sigma;", "Σ");
		htmlSignMap.put("&Tau;", "Τ");
		htmlSignMap.put("&Upsilon;", "Υ");
		htmlSignMap.put("&Phi;", "Φ");
		htmlSignMap.put("&Chi;", "Χ");
		htmlSignMap.put("&Psi;", "Ψ");
		htmlSignMap.put("&Omega;", "Ω");
		htmlSignMap.put("&alpha;", "α");
		htmlSignMap.put("&beta;", "β");
		htmlSignMap.put("&gamma;", "γ");
		htmlSignMap.put("&delta;", "δ");
		htmlSignMap.put("&epsilon;", "ε");
		htmlSignMap.put("&zeta;", "ζ");
		htmlSignMap.put("&eta;", "η");
		htmlSignMap.put("&theta;", "θ");
		htmlSignMap.put("&iota;", "ι");
		htmlSignMap.put("&kappa;", "κ");
		htmlSignMap.put("&lambda;", "λ");
		htmlSignMap.put("&mu;", "μ");
		htmlSignMap.put("&nu;", "ν");
		htmlSignMap.put("&xi;", "ξ");
		htmlSignMap.put("&omicron;", "ο");
		htmlSignMap.put("&rho;", "ρ");
		htmlSignMap.put("&sigmaf;", "ς");
		htmlSignMap.put("&sigma;", "σ");
		htmlSignMap.put("&tau;", "τ");
		htmlSignMap.put("&upsilon;", "υ");
		htmlSignMap.put("&chi;", "φ");
		htmlSignMap.put("&psi;", "ψ");
		htmlSignMap.put("&omega;", "ω");
		htmlSignMap.put("&thetasym;", "ϑ");
		htmlSignMap.put("&upsih;", "ϒ");
		htmlSignMap.put("&piv;", "ϖ");
		htmlSignMap.put("&bull;", "•");
		htmlSignMap.put("&hellip;", "…");
		htmlSignMap.put("&prime;", "′");
		htmlSignMap.put("&Prime;", "″");
		htmlSignMap.put("&oline;", "‾");
		htmlSignMap.put("&frasl;", "⁄");
		htmlSignMap.put("&weierp;", "℘");
		htmlSignMap.put("&image;", "ℑ");
		htmlSignMap.put("&real;", "ℜ");
		htmlSignMap.put("&trade;", "™");
		htmlSignMap.put("&alefsym;", "ℵ");
		htmlSignMap.put("&larr;", "←");
		htmlSignMap.put("&uarr;", "↑");
		htmlSignMap.put("&rarr;", "→");
		htmlSignMap.put("&darr;", "↓");
		htmlSignMap.put("&harr;", "↔");
		htmlSignMap.put("&crarr;", "↵");
		htmlSignMap.put("&lArr;", "⇐");
		htmlSignMap.put("&uArr;", "⇑");
		htmlSignMap.put("&rArr;", "⇒");
		htmlSignMap.put("&dArr;", "⇓");
		htmlSignMap.put("&hArr;", "⇔");
		htmlSignMap.put("&forall;", "∀");
		htmlSignMap.put("&part;", "∂");
		htmlSignMap.put("&exist;", "∃");
		htmlSignMap.put("&empty;", "∅");
		htmlSignMap.put("&nabla;", "∇");
		htmlSignMap.put("&isin;", "∈");
		htmlSignMap.put("&notin;", "∉");
		htmlSignMap.put("&ni;", "∋");
		htmlSignMap.put("&prod;", "∏");
		htmlSignMap.put("&sum;", "∑");
		htmlSignMap.put("&minus;", "−");
		htmlSignMap.put("&lowast;", "∗");
		htmlSignMap.put("&radic;", "√");
		htmlSignMap.put("&prop;", "∝");
		htmlSignMap.put("&infin;", "∞");
		htmlSignMap.put("&ang;", "∠");
		htmlSignMap.put("&and;", "∧");
		htmlSignMap.put("&or;", "∨");
		htmlSignMap.put("&cap;", "∩");
		htmlSignMap.put("&cup;", "∪");
		htmlSignMap.put("&int;", "∫");
		htmlSignMap.put("&there4;", "∴");
		htmlSignMap.put("&sim;", "∼");
		htmlSignMap.put("&cong;", "≅");
		htmlSignMap.put("&asymp;", "≈");
		htmlSignMap.put("&ne;", "≠");
		htmlSignMap.put("&equiv;", "≡");
		htmlSignMap.put("&le;", "≤");
		htmlSignMap.put("&ge;", "≥");
		htmlSignMap.put("&sub;", "⊂");
		htmlSignMap.put("&sup;", "⊃");
		htmlSignMap.put("&nsub;", "⊄");
		htmlSignMap.put("&sube;", "⊆");
		htmlSignMap.put("&supe;", "⊇");
		htmlSignMap.put("&oplus;", "⊕");
		htmlSignMap.put("&otimes;", "⊗");
		htmlSignMap.put("&perp;", "⊥");
		htmlSignMap.put("&sdot;", "⋅");
		htmlSignMap.put("&lceil;", "⌈");
		htmlSignMap.put("&rceil;", "⌉");
		htmlSignMap.put("&lfloor;", "⌊");
		htmlSignMap.put("&rfloor;", "⌋");
		htmlSignMap.put("&loz;", "◊");
		htmlSignMap.put("&spades;", "♠");
		htmlSignMap.put("&clubs;", "♣");
		htmlSignMap.put("&hearts;", "♥");
		htmlSignMap.put("&diams;", "♦");
		htmlSignMap.put("&nbsp;", " ");
		htmlSignMap.put("&iexcl;", "¡");
		htmlSignMap.put("&cent;", "¢");
		htmlSignMap.put("&pound;", "£");
		htmlSignMap.put("&curren;", "¤");
		htmlSignMap.put("&yen;", "¥");
		htmlSignMap.put("&brvbar;", "¦");
		htmlSignMap.put("&sect;", "§");
		htmlSignMap.put("&uml;", "¨");
		htmlSignMap.put("&copy;", "©");
		htmlSignMap.put("&ordf;", "ª");
		
		htmlSignMap.put("&laquo;", "«");
		htmlSignMap.put("&not;", "¬");
		htmlSignMap.put("&reg;", "®");
		htmlSignMap.put("&micro;", "µ");
		htmlSignMap.put("&macr;", "¯");
		htmlSignMap.put("&deg;", "°");
		htmlSignMap.put("&plusmn;", "±");
		htmlSignMap.put("&sup2;", "²");
		htmlSignMap.put("&sup3;", "³");
		htmlSignMap.put("&acute;", "´");
		
		htmlSignMap.put("&amp;", "&");
		htmlSignMap.put("&#39;", "'");
		htmlSignMap.put("&#34;", "\"");
		
		for (Entry<String, String> entry : htmlSignMap.entrySet()) {
			htmlSignMapReversed.put(entry.getValue(), entry.getKey());
		}
		htmlSignMapReversed.remove("&");
	}
	
	/**
	 * 特殊字符转换为HTML使用字符
	 * @param str
	 * @return
	 */
	public static String textToHTML(String str){
		if (str.contains("&")) {
			str.replace("&", "&amp;");
		};
		for (Entry<String, String> entry : htmlSignMapReversed.entrySet()) {
			if (str.contains(entry.getKey())) {
				str = str.replaceAll(entry.getKey(), entry.getValue());
			}
		}
		return str;
	}
	
	/**
	 * 特殊字符转换为Json使用字符
	 * @param str
	 * @return
	 */
	public static String textToJson(String str){
		str = htmlToText(str);
		str = str.replace("'", "\"");
		if (str.endsWith("\\")) {
			str += " ";
		}
		return str;
	}
	
	/**
	 * HTML特殊字符转换为正常字符
	 * @param str
	 * @return
	 */
	public static String htmlToText(String str){
		for (Entry<String, String> element : htmlSignMap.entrySet()) {
			if (str.contains(element.getKey())) {
				str = str.replace(element.getKey(), element.getValue());
			};
		}
		return str;
	}
	
	
}
