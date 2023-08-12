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

import java.util.Map;

/**
 * The type Hash map utils.
 */
public class HashMapUtils {


    /**
     * Concat keys string.
     *
     * @param <T> the type parameter
     * @param map the map
     * @return the string
     */
    public static <T> String concatKeys(final Map<String, T> map) {
        return concatKeys(map, ",");
    }

    /**
     * Concat keys string.
     *
     * @param <T>       the type parameter
     * @param map       the map
     * @param separator the separator
     * @return string
     */
    public static <T> String concatKeys(final Map<String, T> map, String separator) {
        return String.join(separator, map.keySet());
    }
}
