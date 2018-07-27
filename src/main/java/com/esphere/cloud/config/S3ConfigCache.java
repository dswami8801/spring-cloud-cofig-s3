package com.esphere.cloud.config;

import java.util.HashMap;
import java.util.Map;

public class S3ConfigCache {

	private static Map<String, S3Resource> map = new HashMap<>();

	public static S3Resource get(String name) {
		return map.get(name);
	}

	public static void set(String name, S3Resource inputStreamResource) {
		map.put(name, inputStreamResource);
	}
}
