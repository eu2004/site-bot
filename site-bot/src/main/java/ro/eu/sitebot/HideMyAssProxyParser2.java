package ro.eu.sitebot;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by emilu on 12/1/2015.
 */
class HideMyAssProxyParser2 {
	private static final Logger logger = LogManager.getLogger(HideMyAssProxyParser2.class);
	private static final String version = "1.0";
	static {
		logger.info("Version " + version);
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("Use hidemyass.bat <path to input file>." +
					"e.g. hidemyass.bat F:\\work\\sitebot\\src\\main\\resources\\input_page.txt");
		}

		try {
			//get lines
			List<String> lines = IOUtils.readLines(new FileInputStream(args[0]), "utf-8");

			//parse ips
			List<String> ipsList = new ArrayList<String>(1);
			List<String> protocolList = new ArrayList<String>(1);
			for(String line : lines){
				String ip = getIp(line);
				if (ip != null) {
					ipsList.add(ip);
				}else {
					String protocol = getProtocol(line);
					if (protocol != null) {
						protocolList.add(protocol);
					}
				}
			}

			//build ips list
			List<String> results = new ArrayList<String>(1);
			int index = 0;
			for(String ip : ipsList) {
				results.add(ip + protocolList.get(index++));
			}

			//generate file containing ips list
			String fileName = "proxies_" + System.currentTimeMillis() + ".csv";
			logger.info("Generating file " + fileName + " ...");
			PrintWriter printWriter = new PrintWriter(fileName, "UTF-8");
			for(String result : results) {
				printWriter.println(result);
			}
			printWriter.flush();
			printWriter.close();
			logger.info("Done.");
		} catch (Exception e) {
			logger.error("Error parsing input file " + e.getMessage(), e);
		}
	}

	private static final String MATCHER_PATTERN_STR = "(socks)|(HTTPS)|(HTTP)";
	private static final Pattern MATCHER_PATTERN = Pattern.compile(MATCHER_PATTERN_STR);

	private static String getProtocol(String line) {
		Matcher matcher = MATCHER_PATTERN.matcher(line);
		if (matcher.find()) {
			return matcher.group().toUpperCase();
		}
		return null;
	}

	private static final String IPADDRESS_PATTERN_STR =
			"(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?) \t\\d*";
	private static final Pattern IPADDRESS_PATTERN = Pattern.compile(IPADDRESS_PATTERN_STR);


	private static String getIp(String line) {
		Matcher matcher = IPADDRESS_PATTERN.matcher(line);
		if (matcher.find()) {
			return matcher.group().replace(" \t", ",") + ",,,";
		}
		return null;
	}
}