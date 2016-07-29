/*
 * @(#)ConfigUtil.java        1.0 2016-2-23
 *
 * Copyright (c) 2007-2016 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.config;

import org.apache.commons.lang3.StringUtils;

/**
 * Class description goes here.
 * 
 * @version 1.0 2016-2-23
 * @author "lmbao"
 * @history
 * 
 */
public class ConfigUtil {

	private static String appCode;

	public static String get(String key) {
		if (StringUtils.isBlank(key)) {
			throw new IllegalArgumentException("key is null");
		}
		if (!key.startsWith("/")) {
			key = "/" + key;
		}
		String path = "/" + appCode + key;
		String result = System.getProperty(path);
		return result == null ? "" : result;
	}

	static void init(String app) {
		appCode = app;
	}

}
