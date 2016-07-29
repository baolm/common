/*
 * @(#)HandpayPropertyLoader.java        1.0 2013-11-30
 *
 * Copyright (c) 2007-2013 Shanghai Handpay IT, Co., Ltd.
 * 16/F, 889 YanAn Road. W., Shanghai, China
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * Shanghai Handpay IT Co., Ltd. ("Confidential Information").  
 * You shall not disclose such Confidential Information and shall use 
 * it only in accordance with the terms of the license agreement you 
 * entered into with Handpay.
 */

package com.handpay.obm.common.spring;

import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 加载properties文件中[hp.voucher.]开头的property
 * 
 * @version 1.0 2013-11-30
 * @author "lmbao"
 * 
 */
public class HandpayPropertyLoader extends PropertyPlaceholderConfigurer {

	@Override
	protected void processProperties(
			ConfigurableListableBeanFactory beanFactoryToProcess,
			Properties props) throws BeansException {

		String prefix = null;
		for (Entry<Object, Object> entry : props.entrySet()) {
			if (entry.getKey().equals("app.prefix")) {
				prefix = (String) entry.getValue();
				break;
			}
		}
		if (prefix != null) {
			for (Entry<Object, Object> entry : props.entrySet()) {
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				if (key.startsWith(prefix)) {
					System.setProperty(key, value);
					logger.info("load property to java.lang.System: [key: "
							+ key + "];[value:" + value + "]");
				}
			}
		}
		super.processProperties(beanFactoryToProcess, props);
	}
}
