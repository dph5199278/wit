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
package com.cs.wit.util;

import java.util.Objects;

/**
 * The type Base 62.
 */
public class Base62 {

	private static final int NUMBER_61 = 0x0000003d;

	/**
	 * The Digits.
	 */
	static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
			'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z' };


	/**
	 * Encode string.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String encode(long value){
		return Objects.requireNonNull(encode(String.valueOf(value))).toLowerCase() ;
	}

	/**
	 * Encode string.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String encode(String str){
		// get md5 hex string
		String md5Hex = Md5Utils.md5(str);
		// get md5 first 4 byte string
		String md5HexFirst4Byte = md5Hex.substring(0, 8);
		// parse to long
		long md5HexFirst4ByteLong = Long.parseLong(md5HexFirst4Byte, 16);
		StringBuilder encodedBuilder = new StringBuilder();
		// 6 digit binary can indicate 62 letter & number from 0-9a-zA-Z
		for (int j = 5; j >= 0; j--) {
			int charIndex = (int)((md5HexFirst4ByteLong >> (j * 6)) & NUMBER_61);
			encodedBuilder.append(DIGITS[charIndex]);
		}
		if(0 < encodedBuilder.length()) {
			return encodedBuilder.toString();
		}
		// if all 4 possibilities are already exists
		return null;
	}
}