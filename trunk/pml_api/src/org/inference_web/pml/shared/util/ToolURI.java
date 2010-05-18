package org.inference_web.pml.shared.util;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class ToolURI {
	private static Logger getLogger() {
		return Logger.getLogger(ToolURI.class);
	}

	
	public static boolean equalURI(String szURI1, String szURI2) {
		boolean result = false;
		try {
			URI uri1 = new URI(szURI1);
			URI uri2 = new URI (szURI2);
			result = uri1.equals(uri2);
			if (!result){
				URL url1 = uri1.toURL();
				URL url2 = uri2.toURL();
				result = url1.equals(url2);
			}
		} catch (URISyntaxException use) {
			use.printStackTrace();
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Returns a list of URIs selected from group 1. Each URI in returned list is NOT a<br>
	 * member of group 2.
	 * @param group1 list of URIs
	 * @param group2 list of URIs
	 * @return a list of URIs
	 */
	public static List<String> URIListExclude(List<String> group1, List<String> group2) {
		List<String> result = null;
		if (group1 != null && group2 != null) {
			result = new ArrayList<String>();
			int size1 = group1.size();
			int size2 = group2.size();
			String uri1 = null;
			String uri2 = null;
			for (int i=0; i<size1; i++) {
				uri1 = group1.get(i);
				boolean found = false;
				for (int j=0; j<size2; j++) {
					uri2 = group2.get(j);
					if (ToolURI.equalURI(uri1,uri2)) {
						found = true;
						j = size2;
					}
				}
				if (!found) {
					result.add(uri1);
//System.out.println("found exclude "+i+":"+uri1);
				} else {
//System.out.println("fond NOT exclude"+i+":"+uri1);
				}
			}
		}
		
		return result;
	}

	/**
	 * Returns a list of URIs selected from group 1. Each URI in returned list is also a<br>
	 * member of group 2.
	 * @param group1 list of URIs
	 * @param group2 list of URIs
	 * @return a list of URIs
	 */
	public static List<String> URIListInclude(List<String> group1, List<String> group2) {
		List<String> result = null;
		if (group1 != null && group2 != null) {
			result = new ArrayList<String>();
			int size1 = group1.size();
			int size2 = group2.size();
			String uri1 = null;
			String uri2 = null;
			for (int i=0; i<size1; i++) {
				uri1 = group1.get(i);
				boolean found = false;
				for (int j=0; j<size2; j++) {
					uri2 = group2.get(j);
					if (ToolURI.equalURI(uri1,uri2)) {
						found = true;
						j = size2;
					}
				}
				if (found) {
					result.add(uri1);
				}
			}
		}
		
		return result;
	}
	/**
	 * Returns true if any element in group 2 is found in group 1.
	 * @param group1 list of URIs
	 * @param group2 list of URIs
	 */
	public static boolean doesURIListInclude(List<String> group1, List<String> group2) {
		boolean result = false;
		if (group1 != null && group2 != null) {
			int size1 = group1.size();
			int size2 = group2.size();
			String uri1 = null;
			String uri2 = null;
			for (int i=0; i<size1; i++) {
				uri1 = group1.get(i);
				boolean found = false;
				for (int j=0; j<size2; j++) {
					uri2 = group2.get(j);
					if (ToolURI.equalURI(uri1,uri2)) {
						found = true;
						j = size2;
					}
				}
				if (found) {
					return true;
				}
			}
		}		
		return result;
	}
	
	/**
	 * Returns true if any element in group equals uri.
	 * @param group list of URIs
	 * @param uri URI to be checked
	 */
	public static boolean containURI(List<String> group, String uri) {
		boolean result = false;
		if (group != null && uri != null) {
			Iterator gIt = group.listIterator();
			String uri1 = null;
			boolean found = false;
			while (gIt.hasNext() && !found) {
				uri1 = (String)gIt.next();
				if (ToolURI.equalURI(uri1,uri)) {
					found = true;
//System.out.println("contain "+uri);
					result = true;
				}
			}
		}
		return result;
	}

}
