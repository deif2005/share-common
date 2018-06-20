package com.usi.util;

import java.util.HashSet;
import java.util.Set;

public class CacheConstants {
	
	public static Set<String> extensionList=new HashSet<String>();
	
	static {
		extensionList.add("jpg");
		extensionList.add("jpeg");
		extensionList.add("png");
		extensionList.add("bmp");
	}
	
	public static Set<String> videoExtensionList=new HashSet<String>();
	
	static {
	    videoExtensionList.add("mp4");
	    videoExtensionList.add("silk");
		videoExtensionList.add("mp3");
	}
}
