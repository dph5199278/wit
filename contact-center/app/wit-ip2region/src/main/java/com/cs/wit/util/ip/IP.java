/*
 * Copyright (C) 2026 Dely<https://github.com/dph5199278>, All rights reserved.
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

import java.io.Serial;
import java.io.Serializable;

/**
 * 表示 IP 地址的地理位置信息。
 * <p>
 * 该 record 封装了国家、省份、城市、ISP 运营商以及自定义区域等信息，
 * 并提供了 Builder 模式以便于构造不可变实例。实现了 {@link Serializable} 接口，
 * 可用于分布式环境或持久化存储。
 * </p>
 *
 * <p>字段说明：</p>
 * <ul>
 *   <li>{@code country} - 国家名称（例如 "中国"）</li>
 *   <li>{@code province} - 省份名称（例如 "广东省"），可能为 "0" 表示未知或无此级别信息</li>
 *   <li>{@code city} - 城市名称（例如 "深圳市"），可能为 "0" 表示未知或无此级别信息</li>
 *   <li>{@code isp} - 互联网服务提供商（例如 "中国电信"）</li>
 *   <li>{@code region} - 自定义区域标识，可用于业务分区等场景</li>
 * </ul>
 *
 * <p>通过 {@link Builder} 可以链式设置字段，最终调用 {@link Builder#build()} 生成实例。
 * 同时提供了 {@link #builder()} 静态方法快速获取 Builder 实例。</p>
 *
 * <p>注意：该类重写了 {@link #toString()} 方法，返回一个友好的地理位置描述字符串，
 * 具体规则见方法注释。</p>
 *
 * @author Dely
 * @version 1.0
 * @see Builder
 */
public record IP(String country, String province, String city, String isp, String region) implements Serializable {

	/**
	 * 序列化版本号，用于确保序列化兼容性。
	 */
	@Serial
	private static final long serialVersionUID = -421278423658892060L;

	/**
	 * {@code IP} 对象的构建器，采用 Builder 模式。
	 * <p>
	 * 所有字段均为可选，未设置的字段将保持 {@code null}（String 类型）。
	 * 调用 {@link #build()} 方法生成不可变的 {@link IP} 实例。
	 * </p>
	 */
	public static class Builder {
		private String country;
		private String province;
		private String city;
		private String isp;
		private String region;

		/**
		 * 设置国家名称。
		 *
		 * @param country 国家名称
		 * @return 当前 Builder 实例
		 */
		public Builder country(String country) {
			this.country = country;
			return this;
		}

		/**
		 * 设置省份名称。
		 *
		 * @param province 省份名称，可为 "0" 表示未知
		 * @return 当前 Builder 实例
		 */
		public Builder province(String province) {
			this.province = province;
			return this;
		}

		/**
		 * 设置城市名称。
		 *
		 * @param city 城市名称，可为 "0" 表示未知
		 * @return 当前 Builder 实例
		 */
		public Builder city(String city) {
			this.city = city;
			return this;
		}

		/**
		 * 设置 ISP 运营商名称。
		 *
		 * @param isp 运营商名称
		 * @return 当前 Builder 实例
		 */
		public Builder isp(String isp) {
			this.isp = isp;
			return this;
		}

		/**
		 * 设置自定义区域标识。
		 *
		 * @param region 区域标识
		 * @return 当前 Builder 实例
		 */
		public Builder region(String region) {
			this.region = region;
			return this;
		}

		/**
		 * 构建 {@link IP} 实例。
		 *
		 * @return 新创建的不可变 IP 对象
		 */
		public IP build() {
			return new IP(country, province, city, isp, region);
		}
	}

	/**
	 * 获取一个新的 {@link Builder} 实例，用于构造 {@code IP} 对象。
	 *
	 * @return Builder 实例
	 */
	public static Builder builder() {
		return new Builder();
	}

	// --- 传统 JavaBeans 风格的 getter 方法，用于兼容需要此类命名规范的框架（如 Spring、MyBatis） ---

	/**
	 * 返回国家名称。
	 *
	 * @return 国家名称
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * 返回省份名称。
	 *
	 * @return 省份名称，可能为 "0"
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * 返回城市名称。
	 *
	 * @return 城市名称，可能为 "0"
	 */
	public String getCity() {
		return city;
	}

	/**
	 * 返回 ISP 运营商名称。
	 *
	 * @return 运营商名称
	 */
	public String getIsp() {
		return isp;
	}

	/**
	 * 返回自定义区域标识。
	 *
	 * @return 区域标识
	 */
	public String getRegion() {
		return region;
	}

	@Override
	public String toString() {
		// 如果省份或城市为 "0"，则认为只有国家信息有效
		if ("0".equals(province) || "0".equals(city)) {
			return country;
		}
		// 如果省份或城市非空，尝试拼接省份和城市
		if (province != null || city != null) {
			// 如果省份不为空，优先使用省份；否则使用城市
			if (province != null) {
				return province;
			} else {
				// city 不为空（因为外层条件保证了至少有一个非空）
				return city;
			}
		}
		// 否则使用区域或未知
		if (region != null) {
			return region;
		}
		return "未知";
	}
}
