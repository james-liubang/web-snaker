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
 * @date 2022/08/08 ��ȡָ��ҳ������ݺ�����
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
			System.out.print("����Ҫ��ѯ��URL(" + rootUrl + ")");

			String inputUrl = sc.nextLine();
			if (!"".equals(inputUrl.trim())) {
				rootUrl = inputUrl;
			}
			String searchKeyWord = "Oracle Fusion";
			System.out.print("����Ҫ��ѯ�Ĺؼ���(" + searchKeyWord + ") ");
			String inputKeyword = sc.nextLine();
			if (!"".equals(inputKeyword.trim())) {
				searchKeyWord = inputKeyword;
			}
			int maxNum = 3;
			System.out.print("����Ҫ��ѯ�����������(" + maxNum + ") ");
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
			System.out.println("��ȡ��·�� [" + rootUrl + "] ��ɡ�");
			if (foundMap.size() > 0) {
				System.out.println("��ȡ����������:");
				for (URL url : foundMap.keySet()) {
					List<String> finds = foundMap.get(url);
					if (finds.size() == 0) {
						continue;
					}
					System.out.println("ҳ��[" + url + "]�ҵ��ؼ��ִ���:[" + finds.size() + "]");
					for (String find : finds) {
						System.out.println(find);
					}
				}
			}
		}
	}

	/**
	 * ����ҳ��
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
	 * ��ѯ�ؼ��ֲ��������ӵ��б���
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
	 * ��ӵ������б� ��ϸ�淶��
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
			// ��ȡrobot.txt�ļ�
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.indexOf("Disallow:") == 0) {
					String disallowPath = line.substring("Disallow:".length());
					// �Ƴ�comment
					int commentIndex = disallowPath.indexOf("#");
					if (commentIndex != -1) {
						disallowPath = disallowPath.substring(0, commentIndex);
					}
					disallowPath = disallowPath.trim();
					// ���·���������б�
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
	 * ����Ƿ�������robot����
	 * 
	 * @param disallowListCache
	 * @param pageUrl
	 * @return
	 */
	static boolean validateIfAllowRobot(Map<String, List<String>> disallowListCache, URL pageUrl) {
		String host = pageUrl.getHost().toLowerCase();
		// ����Ƿ��Ѿ������б�
		List<String> disallowList = disallowListCache.get(host);
		// ���������, ����ҳ��robot.txt���б���ӵ�disallowListCache
		if (disallowList == null) {
			disallowList = new ArrayList<String>();
			addDisableList(disallowList, host);
			disallowListCache.put(host, disallowList);
		}
		/*
		 * ѭ������Ƿ��������б���
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
	 * ����ָ��ҳ���е���������
	 * 
	 * @param foundMap          �ҵ����ӵ�Map
	 * @param disallowListCache �����б�
	 * @param pageUrl           ҳ��URL
	 * @param pageContents      ҳ������
	 * @param limitHost         �Ƿ���ʾ��ǰhost�²���
	 * @param maxNum            ����ѯ������
	 */
	static void fetchLinkUrls(Map<URL, List<String>> foundMap, Map<String, List<String>> disallowListCache, URL pageUrl,
			String pageContents, boolean limitHost, int maxNum) {
		// ҳ���е�ƥ������link
		Pattern pattern = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(pageContents);
		while (m.find()) {
			String link = m.group(1).trim();
			if (link.length() < 1) {
				continue;
			}
			// ���˵�ǰҳ���ê��
			if (link.charAt(0) == '#') {
				continue;
			}
			// �����ʼ�link
			if (link.indexOf("mailto:") != -1) {
				continue;
			}
			// ����javascript��link
			if (link.toLowerCase().indexOf("javascript") != -1) {
				continue;
			}
			// ���URL��Ժ;���·��
			if (link.indexOf("://") == -1) {
				// ����·��
				if (link.charAt(0) == '/') {
					link = "http://" + pageUrl.getHost() + link;
				}
				// ���·��
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

			// ��Ҫ�Ƴ�ê���е�#
			int index = link.indexOf('#');
			if (index != -1) {
				link = link.substring(0, index);
			}
			// Ϊƥ�������еĵ�ַ�Ƴ�www
			link = remove3W(link);
			// �����Ƿ���ЧURL
			URL linkURL = verifyUrl(link);
			if (linkURL == null) {
				continue;
			}
			// �Ƿ�ǰURL����վ������ʵ�
			if (!validateIfAllowRobot(disallowListCache, linkURL)) {
				continue;
			}
			/*
			 * ��ǰhost�޶�
			 */
			if (limitHost && !pageUrl.getHost().toLowerCase().equals(linkURL.getHost().toLowerCase())) {
				continue;
			}
			// ��ӵ�Map�б���
			if (foundMap.get(linkURL) == null) {
				foundMap.put(linkURL, new ArrayList<String>());
			}
		}
	}

	/**
	 * ��ȡҳ��������е�link
	 * 
	 * @param foundMap          �ҵ�Map
	 * @param disallowListCache �����б�
	 * @param url               ָ����ȡURL
	 * @param searchKeyWord     �ؼ���
	 * @param maxNum            Ѱ�����������
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
	 * ��URL�Ƴ�://www.
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
	 * ����URL�Ƿ�����Ч��
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
