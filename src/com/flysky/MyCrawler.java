package com.flysky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flysky.html.StringsUtils;

/**
 * 
 * @author James liu
 * @date 2022/08/08 爬取指定页面的内容和连接
 *
 */
public class MyCrawler {
	public static void main2(String[] args) throws Exception {
		String url = "https://www.oracle.com/";
		URL rootUrl = new URL(url);
		String pageContents = downloadPage(rootUrl);
		String tags[]=new String[] {"p", "h1", "h2", "h3", "h4", "h5", "h6", "a"};
		List<String> result= new ArrayList<String>();
		for(String tag: tags) {
			String startTag="<"+tag+">";
			String endTag="</"+tag+">";
			String[] setenses = StringsUtils.substringsBetween(pageContents, startTag, endTag);
			result.addAll(Arrays.asList(setenses));
		}
		for(String find:result) {
			System.out.println(find);
		}
	}
	public static void main(String[] args) {
		try (Scanner sc = new Scanner(System.in)) {
			String rootUrl = "https://www.oracle.com/";
			System.out.print("输入要查询的URL(" + rootUrl + ")");

			String inputUrl = sc.nextLine();
			if (!"".equals(inputUrl.trim())) {
				rootUrl = inputUrl;
			}
			String searchKeyWord = "Oracle Fusion";
			System.out.print("输入要查询的关键字(" + searchKeyWord + ") ");
			String inputKeyword = sc.nextLine();
			if (!"".equals(inputKeyword.trim())) {
				searchKeyWord = inputKeyword;
			}
			int maxNum = 3;
			System.out.print("输入要查询的最大链接数(" + maxNum + ") ");
			String inputMax = sc.nextLine();
			if (!"".equals(inputKeyword.trim())) {
				try {
					maxNum = Integer.parseInt(inputMax);
				} catch (NumberFormatException nfe) {
				}
			}

			Map<URL, List<String>> foundMap = new HashMap<URL, List<String>>();
			Map<String, List<String>> disallowListCache = new HashMap<String, List<String>>();

			crawRootUrl(foundMap, disallowListCache, rootUrl, searchKeyWord, maxNum);
			System.out.println("爬取根路径 [" + rootUrl + "] 完成。");
			if (foundMap.size() > 0) {
				System.out.println("爬取到下面链接:");
				for (URL url : foundMap.keySet()) {
					List<String> finds = foundMap.get(url);
					if (finds.size() == 0) {
						continue;
					}
					System.out.println("页面[" + url + "]找到关键字次数:[" + finds.size() + "]");
					for (String find : finds) {
						System.out.println(find);
					}
				}
			}
		}
	}

	/**
	 * 下载页面
	 * 
	 * @param pageUrl
	 * @return
	 */
	private static String downloadPage(URL pageUrl) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
			String line;
			StringBuffer pageBuffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				pageBuffer.append(line);
			}
			return pageBuffer.toString();
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 查询关键字并将结果添加到列表中
	 * 
	 * @param found
	 * @param content
	 * @param keyword
	 */
	private static List<String> findWordByKeyword(String content, String keyword) {
		List<String> found = new ArrayList<String>();;
		String tags[]=new String[] {"p", "h1", "h2", "h3", "h4", "h5", "h6", "a"};
		List<String> result= new ArrayList<String>();
		for(String tag: tags) {
			String startTag="<"+tag+">";
			String endTag="</"+tag+">";
			String[] setenses = StringsUtils.substringsBetween(content, startTag, endTag);
			result.addAll(Arrays.asList(setenses));
		}
		for(String setense:result) {
			if(setense.toLowerCase().contains(keyword.toLowerCase())) {
				found.add(setense);
			}
		}

		
		return found;
	}

	/**
	 * 添加到禁用列表 详细规范：
	 * https://developers.google.com/search/docs/advanced/robots/robots_txt?hl=zh-cn
	 * 
	 * @param disallowList
	 * @param host
	 */
	static void addDisableList(List<String> disallowList, String host) {
		BufferedReader reader = null;
		try {
			URL robotsFileUrl = new URL("http://" + host + "/robots.txt");
			reader = new BufferedReader(new InputStreamReader(robotsFileUrl.openStream()));
			// 读取robot.txt文件
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("Disallow:") == 0) {
					String disallowPath = line.substring("Disallow:".length());
					// 移除comment
					int commentIndex = disallowPath.indexOf("#");
					if (commentIndex != -1) {
						disallowPath = disallowPath.substring(0, commentIndex);
					}
					disallowPath = disallowPath.trim();
					// 添加路径到禁用列表
					disallowList.add(disallowPath);
				}
			}

		} catch (Exception e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 检查是否是允许robot访问
	 * 
	 * @param disallowListCache
	 * @param pageUrl
	 * @return
	 */
	static boolean validateIfAllowRobot(Map<String, List<String>> disallowListCache, URL pageUrl) {
		String host = pageUrl.getHost().toLowerCase();
		// 检查是否已经存在列表
		List<String> disallowList = disallowListCache.get(host);
		// 如果不存在, 访问页面robot.txt中列表并添加到disallowListCache
		if (disallowList == null) {
			disallowList = new ArrayList<String>();
			addDisableList(disallowList, host);
			disallowListCache.put(host, disallowList);
		}
		/*
		 * 循环检查是否在允许列表中
		 */
		String file = pageUrl.getFile();
		for (int i = 0; i < disallowList.size(); i++) {
			String disallow = (String) disallowList.get(i);
			if (file.startsWith(disallow)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 搜索指定页面中的所有连接
	 * 
	 * @param foundMap          找到链接的Map
	 * @param disallowListCache 禁用列表
	 * @param pageUrl           页面URL
	 * @param pageContents      页面内容
	 * @param limitHost         是否显示当前host下查找
	 * @param maxNum            最大查询链接数
	 */
	static void fetchLinkUrls(Map<URL, List<String>> foundMap, Map<String, List<String>> disallowListCache, URL pageUrl,
			String pageContents, boolean limitHost, int maxNum) {
		// 页面中的匹配所有link
		Pattern pattern = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(pageContents);
		while (m.find()) {
			String link = m.group(1).trim();
			if (link.length() < 1) {
				continue;
			}
			// 过滤当前页面的锚点
			if (link.charAt(0) == '#') {
				continue;
			}
			// 过滤邮件link
			if (link.indexOf("mailto:") != -1) {
				continue;
			}
			// 过滤javascript的link
			if (link.toLowerCase().indexOf("javascript") != -1) {
				continue;
			}
			// 检查URL相对和绝对路径
			if (link.indexOf("://") == -1) {
				// 绝对路径
				if (link.charAt(0) == '/') {
					link = "http://" + pageUrl.getHost() + link;
				}
				// 相对路径
				else {
					String file = pageUrl.getFile();
					if (file.indexOf('/') == -1) {
						link = "http://" + pageUrl.getHost() + "/" + link;
					} else {
						String path = file.substring(0, file.lastIndexOf('/') + 1);
						link = "http://" + pageUrl.getHost() + path + link;
					}
				}
			}

			// 需要移除锚点中的#
			int index = link.indexOf('#');
			if (index != -1) {
				link = link.substring(0, index);
			}
			// 为匹配连接中的地址移除www
			link = remove3W(link);
			// 检验是否有效URL
			URL linkURL = verifyUrl(link);
			if (linkURL == null) {
				continue;
			}
			// 是否当前URL是网站允许访问的
			if (!validateIfAllowRobot(disallowListCache, linkURL)) {
				continue;
			}
			/*
			 * 当前host限定
			 */
			if (limitHost && !pageUrl.getHost().toLowerCase().equals(linkURL.getHost().toLowerCase())) {
				continue;
			}
			// 添加到Map中保存
			if (foundMap.get(linkURL) == null) {
				foundMap.put(linkURL, new ArrayList<String>());
			}
		}
	}

	/**
	 * 爬取页面里的所有的link
	 * 
	 * @param foundMap          找到Map
	 * @param disallowListCache 禁用列表
	 * @param url               指定爬取URL
	 * @param searchKeyWord     关键字
	 * @param maxNum            寻找最大链接数
	 */
	static void crawRootUrl(Map<URL, List<String>> foundMap, Map<String, List<String>> disallowListCache, String url,
			String searchKeyWord, int maxNum) {
		int count = 0;
		try {
			URL rootUrl = new URL(url);
			String pageContents = downloadPage(rootUrl);
			// find keyword in root page
			List<String> found = findWordByKeyword(pageContents, searchKeyWord);
			if (found != null) {
				foundMap.put(rootUrl, found);
				count++;
			}
			fetchLinkUrls(foundMap, disallowListCache, rootUrl, pageContents, false, maxNum);
			for (URL subUrl : foundMap.keySet()) {
				List<String> finds = foundMap.get(subUrl);
				String subPageContents = downloadPage(subUrl);
				// find keyword in sub page
				List<String> subFound = findWordByKeyword(subPageContents, searchKeyWord);
				if (subFound != null) {
					finds.addAll(subFound);
					foundMap.put(rootUrl, finds);
					count++;
				}
				if (count >= maxNum) {
					break;
				}
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

	/**
	 * 从URL移除://www.
	 * 
	 * @param url
	 * @return
	 */
	private static String remove3W(String url) {
		int index = url.indexOf("://www.");
		if (index != -1) {
			return url.substring(0, index + 3) + url.substring(index + 7);
		}
		return (url);
	}

	/**
	 * 检验URL是否是有效的
	 * 
	 * @param url
	 * @return
	 */
	private static URL verifyUrl(String url) {
		URL verifiedUrl = null;
		try {
			verifiedUrl = new URL(url);
		} catch (Exception e) {
			return null;
		}
		return verifiedUrl;
	}

	
}
