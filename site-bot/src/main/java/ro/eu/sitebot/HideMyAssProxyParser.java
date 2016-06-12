package ro.eu.sitebot;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Created by emilu on 12/1/2015.
 */
class HideMyAssProxyParser {

	public String loadPage(String url) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(httpget);
			String responseString = IOUtils.toString(response.getEntity().getContent(), "utf-8");
			System.out.println("responseString " + responseString);
			return responseString;
		} catch (IOException e) {
			e.printStackTrace();
		}  finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static void main(String[] args) {
		testRowParser();
		//http://proxylist.hidemyass.com/search-1292985/1#listable
		//http://proxylist.hidemyass.com/search-1292985/2#listable
		//http://proxylist.hidemyass.com/search-1292985/3#listable
		//String urlToParse = args[0];
		try {
			List<String> lines = IOUtils.readLines(new FileInputStream(".\\src\\main\\resources\\hidemyass_response.html"), "utf-8");
			String responseString = null;
			for(String line : lines) {
				responseString += line;
			}

			String rowPatternStr = "<tr\\sclass=\"\\D*\"\\s\\srel=\"\\d+\">(.*?)</tr>";
			Matcher rowMatcher = Pattern.compile(rowPatternStr).matcher(responseString);
			while (rowMatcher.find()) {
				String row = rowMatcher.group(1).trim();
				parseRow(row);
				//String ipPatternStr = "<td><span><style>.*?</style><span.*?>(\\d+)</span>.*?|<span.*?>(\\.)</span>.*?|(\\d+).*?</td>";
				//String ipPatternStr = "<td>.*?<span\\sclass=\"\\D+\">(\\d+)</span>.*?|<span\\sstyle=\"display:\\sinline\">(\\d+)</span>|<span\\sclass=\"\\d+\">(\\d+)</span>.*?</td>";
				//String ipPatternStr = "<td><span><style>.*?</style>.*?<span\\sclass=\"\\D+\">(\\d+)</span></span></td>";
				//String ipPatternStr = "<td><span><style>.*?</style>.*?<span\\sclass=\"\\D+\">(\\d+)</span>.*?</span></td>";
				//good String ipPatternStr = "<span\\sclass=\"\\D+\">(\\d+)</span>|<span\\sclass=\"\\d+\">(\\d+)</span>|<span\\sstyle=\"display:\\sinline\">(\\d+)</span>";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		//		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
//		Matcher matcher = pattern.matcher(ipString);
//		int index = 1;
//		while (matcher.find()) {
//			if (index++ % 2 == 0) {
//				//display protocol
//				System.out.println(matcher.group().toUpperCase());
//			} else {
//				//display ip + port
//				System.out.print(matcher.group().replace(" \t", ",") + ",,,");
//			}
//		}
	}

	private static void testRowParser() {
		String row = "<td><span><style>\n" +
				"    .pDqT{display:none}\n" +
				"    .Oe-7{display:inline}\n" +
				"    .gVOg{display:none}\n" +
				"    .IjiX{display:inline}\n" +
				"    .VYSi{display:none}\n" +
				"    .rXd0{display:inline}\n" +
				"    .lT3N{display:none}\n" +
				"    .IbL6{display:inline}\n" +
				"</style><span class=\"VYSi\">60</span><span class=\"IbL6\">189</span><span style=\"display: inline\">.</span><span style=\"display:none\">83</span><div style=\"display:none\">83</div><span style=\"display:none\">93</span><span class=\"VYSi\">93</span><div style=\"display:none\">93</div><span class=\"lT3N\">213</span><div style=\"display:none\">213</div><span class=\"rXd0\">219</span>.<div style=\"display:none\">3</div><span class=\"VYSi\">65</span><div style=\"display:none\">65</div><span style=\"display:none\">77</span><span class=\"IbL6\">102</span><span style=\"display:none\">139</span><span class=\"lT3N\">139</span><span style=\"display:none\">153</span><span class=\"pDqT\">153</span><span></span><span style=\"display:none\">235</span><span class=\"pDqT\">235</span><span class=\"Oe-7\">.</span><div style=\"display:none\">48</div><span class=\"87\">124</span><span style=\"display:none\">203</span><span class=\"pDqT\">203</span><span class=\"VYSi\">231</span><div style=\"display:none\">231</div></span></td>";
		parseRow(row);

		System.exit(1);
	}

	private static void parseRow(String rowToParse) {
		System.out.println(rowToParse);
		List<String> notDisplayStyles = getNotDisplayStyles(rowToParse);
		String ip = getIp(notDisplayStyles, rowToParse);
		System.out.println(ip);
	}

	private static String getIp(List<String> notDisplayedStyles, String textToParse) {
		String spanIpPatternStr = "(<span\\sclass=\"\\D+\">\\d+</span>)|(<span\\sclass=\"\\d+\">\\d+</span>)|" +
				"(<span\\sstyle=\"display:\\sinline\">\\d+</span>)|(</div>\\d+<span)";
		Matcher matcher = Pattern.compile(spanIpPatternStr).matcher(textToParse);
		String matchedSpanIpPatternStr = "";
		while (matcher.find()){
			if (isSpanGroupOK(matcher.group(1), notDisplayedStyles)) {
				matchedSpanIpPatternStr += matcher.group(1);
			}else if (isSpanGroupOK(matcher.group(2), notDisplayedStyles)) {
				matchedSpanIpPatternStr += matcher.group(2);
			}else if (isSpanGroupOK(matcher.group(3), notDisplayedStyles)) {
				matchedSpanIpPatternStr += matcher.group(3);
			}else if (isSpanGroupOK(matcher.group(4), notDisplayedStyles)) {
				matchedSpanIpPatternStr += matcher.group(4);
			}
		}

		String ipPatternStr = "<span\\sclass=\"\\D+\">(\\d+)</span>|<span\\sclass=\"\\d+\">(\\d+)</span>|" +
				"<span\\sstyle=\"display:\\sinline\">(\\d+)</span>|</div>(\\d+)<span";
		matcher = Pattern.compile(ipPatternStr).matcher(matchedSpanIpPatternStr);
		String matchedIp = "";
		while (matcher.find()){
			if (matcher.group(1) != null) {
				matchedIp += matcher.group(1).trim() + ".";
			}else if (matcher.group(2) != null) {
				matchedIp += matcher.group(2).trim() + ".";
			}else if (matcher.group(3) != null) {
				matchedIp += matcher.group(3).trim() + ".";
			}else if (matcher.group(4) != null) {
				matchedIp += matcher.group(4).trim() + ".";
			}
		}
		matchedIp = matchedIp.substring(0, matchedIp.length() - 1);
		return matchedIp;
	}

	private static boolean isSpanGroupOK(String group, List<String> styles) {
		if (group == null) {
			return false;
		}

		for(String style : styles) {
			if (group.contains(style)) {
				return false;
			}
		}

		return true;
	}

	private static List<String> getNotDisplayStyles(String textToParse){
		List<String> styles = new ArrayList<String>(1);
		String stylePattern = "<td><span><style>(.*?)</style>";
		Matcher matcher = Pattern.compile(stylePattern).matcher(textToParse);
		if (!matcher.find()) {
			return styles;
		}
		String stylesStr = matcher.group(1);
		String displayStylePattern = "(\\.\\w+?\\{display:none\\})";
		matcher = Pattern.compile(displayStylePattern).matcher(stylesStr);
		while(matcher.find()){
			String styleNameStr = matcher.group(1).trim();
			Matcher styleNameMatcher = Pattern.compile("(\\.\\w+)").matcher(styleNameStr);
			if(styleNameMatcher.find()){
				String styleName = styleNameMatcher.group(1);
				styles.add(styleName.trim().substring(1, styleName.trim().length()));
			}
		}
		return styles;
	}

	private static final String IPADDRESS_PATTERN =
			"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?) \t\\d*|(socks)|(HTTP)";

	private static final String ipString = "58secs \t119.167.230.189 \t81 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"58secs \t37.59.49.168 \t60088 \tflag France \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"58secs \t78.46.93.189 \t57515 \tflag Germany \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"58secs \t202.117.15.80 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"58secs \t121.69.24.22 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"58secs \t185.22.185.207 \t60088 \tflag Europe \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"1min \t124.160.184.122 \t81 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"1min \t120.198.231.22 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"1min \t198.169.246.30 \t80 \tflag Canada \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"1min \t119.167.230.190 \t81 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"1min \t124.160.184.121 \t81 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"1min \t125.212.37.72 \t3128 \tflag Philippines \t\n" +
			"\t\n" +
			"\tHTTP \tMedium\n" +
			"1min \t178.32.216.214 \t60088 \tflag France \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"2mins \t208.109.176.5 \t60088 \tflag USA \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"4mins \t124.160.184.22 \t81 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"5mins \t124.206.133.227 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"5mins \t121.41.161.110 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"5mins \t31.193.239.212 \t60088 \tflag Denmark \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"6mins \t212.99.54.106 \t80 \tflag France \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"7mins \t119.18.54.19 \t57515 \tflag India \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"7mins \t101.6.54.28 \t1080 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"7mins \t222.39.64.74 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"7mins \t117.135.241.78 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"7mins \t24.173.40.24 \t8080 \tflag USA \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"7mins \t218.244.130.11 \t1080 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"8mins \t47.88.139.33 \t1080 \tflag Canada \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"13mins \t119.18.54.20 \t57515 \tflag India \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"13mins \t196.41.9.169 \t8585 \tflag South Africa \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"14mins \t216.17.105.183 \t10080 \tflag USA \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"14mins \t87.98.139.209 \t60088 \tflag France \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"18mins \t217.170.201.78 \t60088 \tflag Norway \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"20mins \t120.198.231.86 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"23mins \t196.36.53.202 \t9999 \tflag South Africa \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"33mins \t61.157.198.67 \t1080 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"46mins \t92.103.82.198 \t80 \tflag France \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"51mins \t222.45.85.210 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"1h 5mins \t211.66.88.164 \t808 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"1h 7mins \t121.69.29.122 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"1h 57mins \t190.104.245.39 \t8080 \tflag Argentina \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"3h 9mins \t61.162.184.14 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"3h 18mins \t124.88.67.53 \t83 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"4h 13mins \t182.162.141.7 \t80 \tflag Korea \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"4h 33mins \t222.39.87.21 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"4h 52mins \t222.114.148.248 \t8080 \tflag Latvia \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"5h 29mins \t123.125.104.240 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"5h 48mins \t111.11.184.51 \t9999 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"5h 53mins \t101.99.18.237 \t8080 \tflag Viet Nam \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"6h 29mins \t222.174.177.130 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"6h 50mins \t122.70.178.242 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"7h 1min \t122.136.46.151 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"7h 20mins \t177.39.186.62 \t8008 \tflag Brazil \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"7h 20mins \t50.167.252.103 \t3128 \tflag USA \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"7h 30mins \t183.111.169.201 \t3128 \tflag Korea \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"8h \t124.244.76.175 \t80 \tflag Hong Kong \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"8h 24mins \t186.46.115.22 \t3128 \tflag Ecuador \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"8h 58mins \t222.124.142.108 \t8080 \tflag Indonesia \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"9h 3mins \t201.20.182.10 \t8080 \tflag Brazil \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"10h 3mins \t117.135.241.78 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"10h 28mins \t121.69.31.90 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"11h 5mins \t124.88.67.24 \t83 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"11h 5mins \t122.96.59.99 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"11h 10mins \t125.209.91.190 \t8080 \tflag Pakistan \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"11h 27mins \t196.26.121.107 \t9999 \tflag South Africa \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"11h 31mins \t222.45.85.53 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"12h 7mins \t221.7.206.140 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"12h 7mins \t180.97.185.35 \t10001 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"12h 30mins \t122.96.59.102 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"12h 43mins \t27.131.173.2 \t8080 \tflag Thailand \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"12h 47mins \t124.88.67.39 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"12h 57mins \t177.55.254.113 \t8080 \tflag Brazil \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"13h 10mins \t201.251.62.245 \t3128 \tflag Argentina \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"14h 7mins \t122.96.59.105 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"14h 49mins \t186.92.26.157 \t8080 \tflag Venezuela \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"14h 50mins \t27.115.75.114 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"14h 54mins \t220.185.103.99 \t3128 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"16h 43mins \t124.88.67.33 \t843 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"17h 32mins \t27.106.33.161 \t8080 \tflag India \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"17h 48mins \t200.93.121.233 \t8080 \tflag Venezuela \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"18h 3mins \t110.45.135.229 \t8080 \tflag Korea \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"18h 16mins \t115.29.139.240 \t54321 \tflag China \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"18h 34mins \t78.46.107.42 \t1058 \tflag India \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA\n" +
			"19h 19mins \t182.254.153.54 \t8080 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"19h 39mins \t202.29.97.2 \t3128 \tflag Thailand \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"19h 48mins \t180.73.0.10 \t81 \tflag Malaysia \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"20h 3mins \t186.225.53.22 \t8080 \tflag Brazil \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"22h 8mins \t121.69.15.86 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"22h 19mins \t111.11.255.11 \t80 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"22h 24mins \t122.136.46.151 \t3128 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"22h 46mins \t24.157.57.56 \t8080 \tflag USA \t\n" +
			"\t\n" +
			"\tHTTP \tHigh\n" +
			"23h 7mins \t222.39.64.13 \t8118 \tflag China \t\n" +
			"\t\n" +
			"\tHTTP \tHigh +KA\n" +
			"23h 31mins \t213.181.73.145 \t8080 \tflag Spain \t\n" +
			"\t\n" +
			"\tHTTP \tLow\n" +
			"23h 40mins \t50.63.60.188 \t60088 \tflag USA \t\n" +
			"\t\n" +
			"\tsocks4/5 \tHigh +KA";
}
