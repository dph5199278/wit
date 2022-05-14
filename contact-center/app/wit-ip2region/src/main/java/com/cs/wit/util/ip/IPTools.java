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
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.DataBlock;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbMakerConfigException;
import org.lionsoul.ip2region.DbSearcher;
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
	private DbSearcher _searcher = null ;

	@NonNull
	private final ApplicationContext applicationContext;

	@PostConstruct
	public void setup() {
		try {
			final Resource resource = applicationContext.getResource(
					"classpath:/config/ip2region.db");
			log.info("init with file [{}]", resource.getURL());
			if(resource.exists()) {
				try(InputStream inputStream = resource.getInputStream();
						ByteArrayOutputStream outputStream = new ByteArrayOutputStream((int)resource.contentLength())) {
					StreamUtils.copy(inputStream, outputStream);
					_searcher = new DbSearcher(new DbConfig(), outputStream.toByteArray());
				}
			}
		} catch (DbMakerConfigException | IOException e) {
			e.printStackTrace();
		}
	}

	public IP findGeography(String remote) {
		IP ip = new IP();
		try {
			DataBlock block = _searcher.memorySearch(remote!=null ? remote : "127.0.0.1")  ;
			if(block!=null && block.getRegion() != null){
				String[] region = block.getRegion().split("[\\|]") ;
				if(region.length == 5){
					ip.setCountry(region[0]);
					if(StringUtils.hasText(region[1]) && !region[1].equalsIgnoreCase("null")){
						ip.setRegion(region[1]);
					}else{
						ip.setRegion("");
					}
					if(StringUtils.hasText(region[2]) && !region[2].equalsIgnoreCase("null")){
						ip.setProvince(region[2]);
					}else{
						ip.setProvince("");
					}
					if(StringUtils.hasText(region[3]) && !region[3].equalsIgnoreCase("null")){
						ip.setCity(region[3]);
					}else{
						ip.setCity("");
					}
					if(StringUtils.hasText(region[4]) && !region[4].equalsIgnoreCase("null")){
						ip.setIsp(region[4]);
					}else{
						ip.setIsp("");
					}
				}
			}
		}
		catch(Exception ignored){}
		return ip;
	}
}
