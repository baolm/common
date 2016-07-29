/*
 * @(#)ConfigException.java        1.0 2016-2-22
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

package com.handpay.obm.common.exception;

/**
 * Class description goes here.
 * 
 * @version 1.0 2016-2-22
 * @author "lmbao"
 * @history
 * 
 */
public class ConfigException extends ObmBaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3312130194039607924L;

	public ConfigException(String errorCode, String errorMsg) {
		super(errorCode, errorMsg);
	}

	public ConfigException(String errorCode, Throwable caused) {
		super(errorCode, caused);
	}

	public ConfigException(String errorCode, String errorMsg, Throwable caused) {
		super(errorCode, errorMsg, caused);
	}
}
