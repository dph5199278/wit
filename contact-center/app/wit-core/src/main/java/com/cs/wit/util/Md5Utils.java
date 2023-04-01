/*
 * Copyright (C) 2023 Dely<https://github.com/dph5199278>, All rights reserved.
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

import cn.hutool.crypto.SecureUtil;

/**
 * Md5Utils
 *
 * @author Dely
 * @version 1.0
 * @date 2022 -12 add
 */
public class Md5Utils {

	/**
	 * Md5 and Md5 string.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String md5(String str) {
		return SecureUtil.md5(SecureUtil.md5(str));
	}

	/**
	 * Md5 string.
	 *
	 * @param bytes the bytes
	 * @return the string
	 */
	public static String md5(byte[] bytes) {
		return SecureUtil.md5().digestHex(bytes);
	}

}
