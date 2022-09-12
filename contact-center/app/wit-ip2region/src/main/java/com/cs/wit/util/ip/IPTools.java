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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class IPTools {
	private Searcher _searcher = null;

	private final ConcurrentMap<String, IP> caches = new ConcurrentHashMap<>();

	@NonNull
	private final ApplicationContext applicationContext;

	private final static IP EMPTY = new IP();

	@PostConstruct
	public void setup() {
		try {
			final Resource resource = applicationContext.getResource(
					"classpath:/config/ip2region.xdb");
			log.info("init with file [{}]", resource.getURL());
			if(resource.exists()) {
				try(InputStream inputStream = resource.getInputStream();
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int)resource.contentLength())) {
					StreamUtils.copy(inputStream, outputStream);
					_searcher = Searcher.newWithBuffer(outputStream.toByteArray());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public IP findGeography(String remote) {
		// 先找缓存
		if (caches.containsKey(remote)) {
			return caches.get(remote);
		}

		// 缓存找不到，再来查询
		IP ip = EMPTY;
		try {
			String region = _searcher.search(StringUtils.hasText(remote) ? remote : "127.0.0.1");
			if(StringUtils.hasText(region)){
				String[] regions = region.split("[\\|]");
				if(regions.length == 5){
					ip = new IP();
					ip.setCountry(regions[0]);
					if(StringUtils.hasText(regions[1]) && !regions[1].equalsIgnoreCase("null")){
						ip.setRegion(regions[1]);
					}else{
						ip.setRegion("");
					}
					if(StringUtils.hasText(regions[2]) && !regions[2].equalsIgnoreCase("null")){
						ip.setProvince(regions[2]);
					}else{
						ip.setProvince("");
					}
					if(StringUtils.hasText(regions[3]) && !regions[3].equalsIgnoreCase("null")){
						ip.setCity(regions[3]);
					}else{
						ip.setCity("");
					}
					if(StringUtils.hasText(regions[4]) && !regions[4].equalsIgnoreCase("null")){
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
}
