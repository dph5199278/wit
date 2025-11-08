/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2019 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cs.wit.util.ip;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * The type Ip tools.
 * @author Dely
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class IPTools {

	/**
	 * IPV4 Only
	 */
	private Searcher searcherV4 = null;
	/**
	 * IPV6 Only
	 */
	private Searcher searcherV6 = null;

	private final ConcurrentMap<String, IP> caches = new ConcurrentHashMap<>();

	/**
	 * IPV4 Regex
	 */
	private final String IPV4_REGEX = "^(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";

	@NonNull
	private final ApplicationContext applicationContext;

	private final static IP EMPTY = new IP();

	/**
	 * Sets .
	 */
	@PostConstruct
	public void setup() {
		initSearchV4();
		initSearchV6();
	}

	/**
	 * Init IPV4 Search
	 */
	private void initSearchV4() {
		try {
			searcherV4 = initSearch("classpath:/config/ip2region_v4.xdb", "file:./config/ip2region_v4.xdb", Version.IPv4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Init IPV6 Search
	 */
	private void initSearchV6() {
		try {
			searcherV6 = initSearch("classpath:/config/ip2region_v6.xdb", "file:./config/ip2region_v6.xdb", Version.IPv6);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Searcher initSearch(String innerDbFile, String outerDbFile, Version ipVersion)
			throws IOException {
		Searcher searcher = null;
		// 优先找外部资源
		Resource resource = applicationContext.getResource(
				outerDbFile);
		if(!resource.exists()) {
			// 外部找不到找内置资源
			resource = applicationContext.getResource(
					innerDbFile);
		}
		if(resource.exists()) {
			log.info(ipVersion.name + " init with file [{}]", resource.getURL());
			long length = resource.contentLength();
			try(InputStream inputStream = resource.getInputStream()) {
				LongByteArray byteArray = loadContent(inputStream, length);
				searcher = Searcher.newWithBuffer(ipVersion, byteArray);
			}
		}
		else {
			log.warn(ipVersion.name + " not found with file");
		}
		return searcher;
	}

	/**
	 * Find geography ip.
	 *
	 * @param remote the remote
	 * @return the ip
	 */
	public IP findGeography(String remote) {
		// 先找缓存
		if (caches.containsKey(remote)) {
			return caches.get(remote);
		}

		// 缓存找不到，再来查询
		IP ip = EMPTY;
		try {
			final String remoteIp = StringUtils.hasText(remote) ? remote : "127.0.0.1";
			String region = null;
			if(remoteIp.matches(IPV4_REGEX)) {
				if(null != searcherV4) {
					region = searcherV4.search(remoteIp);
				}
			}
			else {
				if (null != searcherV6) {
					region = searcherV6.search(remoteIp);
				}
			}
			if(StringUtils.hasText(region)){
				String[] regions = regions(region);
				if(regions.length == 5) {
					ip = new IP();
					ip.setCountry(regions[0]);
					if(StringUtils.hasText(regions[1]) && !"null".equalsIgnoreCase(regions[1])){
						ip.setRegion(regions[1]);
					}else{
						ip.setRegion("");
					}
					if(StringUtils.hasText(regions[2]) && !"null".equalsIgnoreCase(regions[2])){
						ip.setProvince(regions[2]);
					}else{
						ip.setProvince("");
					}
					if(StringUtils.hasText(regions[3]) && !"null".equalsIgnoreCase(regions[3])){
						ip.setCity(regions[3]);
					}else{
						ip.setCity("");
					}
					if(StringUtils.hasText(regions[4]) && !"null".equalsIgnoreCase(regions[4])){
						ip.setIsp(regions[4]);
					}else{
						ip.setIsp("");
					}
				}
			}
		}
		catch(Exception ignored){}

		// 缓存并返回
		caches.put(remote, ip);
		return ip;
	}

	private LongByteArray loadContent(InputStream inputStream, long contentLength) throws IOException {
		long toRead = contentLength;

		LongByteArray byteArray;
		int rLen;
		for(byteArray = new LongByteArray(); toRead > 0L; toRead -= rLen) {
			byte[] buff = new byte[(int)Math.min(toRead, 2147479552L)];
			rLen = inputStream.read(buff);
			if (rLen != buff.length) {
				throw new IOException("incomplete read: read bytes should be " + buff.length + ", got `" + rLen + "`");
			}

			byteArray.append(buff);
		}

		return byteArray;
	}

	private String[] regions(String region) {
		String[] regions = region.split("[\\|]");
		if(regions.length != 4) {
			return regions;
		}

		String[] rtn = new String[5];
		rtn[0] = regions[0];
		rtn[1] = "0";
		rtn[2] = regions[1];
		rtn[3] = regions[2];
		rtn[4] = regions[3];
		return rtn;
	}
}
